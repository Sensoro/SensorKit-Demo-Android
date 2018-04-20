package com.sensoro.sensor.kit.update.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sensoro.sensor.kit.SensoroDevice;
import com.sensoro.sensor.kit.update.ble.scanner.SensoroUUID;
import com.sensoro.sensor.kit.update.proto.ProtoStationMsgV2;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by fangping on 2016/7/25.
 */

public class SensoroStationConnection {
    private static final String TAG = SensoroStationConnection.class.getSimpleName();
    private static final long CONNECT_TIME_OUT = 60000; // 30s connect timeout
    private Context context;
    private Handler handler;
    private SensoroDevice bleDevice;
    private SensoroConnectionCallback sensoroConnectionCallback;
    private Map<Integer, SensoroWriteCallback> writeCallbackHashMap;
    private String password;
    private BluetoothLEHelper4 bluetoothLEHelper4;
    private ByteBuffer byteBuffer = null;
    private ByteBuffer tempBuffer = null;
    private int buffer_total_length = 0;
    private int buffer_data_length = 0;

    public SensoroStationConnection(Context context, SensoroDevice bleDevice) {
        this.context = context;
        handler = new Handler();
        this.bleDevice = bleDevice;
        bluetoothLEHelper4 = new BluetoothLEHelper4(context);
        writeCallbackHashMap = new HashMap<>();
    }

    private void initData() {
        buffer_data_length = 0;
        buffer_total_length = 0;
    }


    /**
     * Connect to beacon.
     *
     * @param password                  If beacon has no password, set value null.
     * @param sensoroConnectionCallback The callback of beacon connection.
     * @throws Exception
     */
    public void connect(String password, SensoroConnectionCallback sensoroConnectionCallback) throws Exception {
        if (context == null) {
            throw new Exception("Context is null");
        }
        if (bleDevice == null) {
            throw new Exception("sensoroDevice is null");
        }
        if (sensoroConnectionCallback == null) {
            throw new Exception("SensoroConnectionCallback is null");
        }

        if (password != null) {
            this.password = password;
        }

        initData();
        // 开始连接，启动连接超时
        handler.postDelayed(connectTimeoutRunnable, CONNECT_TIME_OUT);

        this.sensoroConnectionCallback = sensoroConnectionCallback;

        if (!bluetoothLEHelper4.initialize()) {
            sensoroConnectionCallback.onConnectedFailure(ResultCode.BLUETOOTH_ERROR);
            disconnect();
        }

        if (!bluetoothLEHelper4.connect(bleDevice.getMacAddress(), bluetoothGattCallback)) {
            sensoroConnectionCallback.onConnectedFailure(ResultCode.INVALID_PARAM);
            disconnect();
        }
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {//连接成功
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt.discoverServices();
                } else {
                    sensoroConnectionCallback.onConnectedFailure(ResultCode.BLUETOOTH_ERROR);
                    disconnect();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {//发现服务
                List<BluetoothGattService> gattServiceList = gatt.getServices();
                if (bluetoothLEHelper4.checkGattServices(gattServiceList, BluetoothLEHelper4.GattInfo.SENSORO_STATION_SERVICE_UUID)) {
                    bluetoothLEHelper4.listenDescriptor(BluetoothLEHelper4.GattInfo.SENSORO_STATION_READ_CHAR_UUID);
                } else {
                    sensoroConnectionCallback.onConnectedFailure(ResultCode.SYSTEM_ERROR);
                    disconnect();
                }
            } else {
                sensoroConnectionCallback.onConnectedFailure(ResultCode.SYSTEM_ERROR);
                disconnect();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (descriptor.getUuid().equals(BluetoothLEHelper4.GattInfo.CLIENT_CHARACTERISTIC_CONFIG)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    UUID auth_uuid = BluetoothLEHelper4.GattInfo.SENSORO_STATION_AUTHORIZATION_CHAR_UUID;
                    int resultCode = bluetoothLEHelper4.requireWritePermission(password, auth_uuid);
                    if (resultCode != ResultCode.SUCCESS) {
                        sensoroConnectionCallback.onConnectedFailure(resultCode);
                        disconnect();
                    }
                }
            }
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            parseCharacteristicWrite(characteristic, status);

        }

        private void parseCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
            // check pwd
            if (characteristic.getUuid().equals(BluetoothLEHelper4.GattInfo.SENSORO_STATION_AUTHORIZATION_CHAR_UUID)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bluetoothLEHelper4.listenOnCharactertisticRead(BluetoothLEHelper4.GattInfo.SENSORO_STATION_READ_CHAR_UUID);
                } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                    sensoroConnectionCallback.onConnectedFailure(ResultCode.PASSWORD_ERR);
                    disconnect();
                } else {
                    sensoroConnectionCallback.onConnectedFailure(ResultCode.INVALID_PARAM);
                    disconnect();
                }
            }

            if (characteristic.getUuid().equals(BluetoothLEHelper4.GattInfo.SENSORO_STATION_WRITE_CHAR_UUID)) {
                Log.v(TAG, "onCharacteristicWrite success");
                int cmdType = bluetoothLEHelper4.getSendCmdType();
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bluetoothLEHelper4.sendPacket(characteristic);
                } else {
                    Log.v(TAG, "onCharacteristicWrite failure" + status);
                    // failure
                    switch (cmdType) {
                        case CmdType.CMD_R_CFG:
                            sensoroConnectionCallback.onConnectedFailure(ResultCode.SYSTEM_ERROR);
                            disconnect();
                            break;
                        case CmdType.CMD_W_CFG:
                            writeCallbackHashMap.get(cmdType).onWriteFailure(ResultCode.SYSTEM_ERROR, CmdType.CMD_NULL);
                            break;
                        default:
                            break;
                    }
                    bluetoothLEHelper4.resetSendPacket();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            parseCharacteristicRead(characteristic, status);
        }

        private void parseCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
            if (characteristic.getUuid().equals(BluetoothLEHelper4.GattInfo.SENSORO_STATION_READ_CHAR_UUID)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte value[] = characteristic.getValue();
                    //如果value 长度小于20,说明是个完整短包
                    byte[] total_data = new byte[2];
                    System.arraycopy(value, 0, total_data, 0, total_data.length);
                    buffer_total_length = SensoroUUID.bytesToInt(total_data, 0) - 1;//数据包长度
                    byte[] version_data = new byte[1];
                    System.arraycopy(value, 2, version_data, 0, version_data.length);
                    int data_version = version_data[0];
                    byteBuffer = ByteBuffer.allocate(buffer_total_length); //减去version一个字节长度
                    byte[] data = new byte[value.length - 3];//第一包数据
                    System.arraycopy(value, 3, data, 0, data.length);
                    byteBuffer.put(data);

                    if (buffer_total_length <= (value.length - 3)) { //一次性数据包
                        byte[] final_data = byteBuffer.array();
                        try {
                            parseStationData(final_data);
                            sensoroConnectionCallback.onConnectedSuccess(bleDevice, CmdType.CMD_NULL);
                        } catch (Exception e) {
                            e.printStackTrace();
                            sensoroConnectionCallback.onConnectedFailure(ResultCode.PARSE_ERROR);
                        } finally {
                            handler.removeCallbacks(connectTimeoutRunnable);
                        }
                    } else { //多包数据
                        if (tempBuffer != null) {//先出现change再出现read情况
                            byteBuffer.put(tempBuffer.array());
                            tempBuffer.clear();
                            tempBuffer = null;
                        }
                        buffer_data_length += (value.length - 3);
                    }
                }
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            try {
                parseChangedData(characteristic);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    private void parseChangedData(BluetoothGattCharacteristic characteristic) throws InvalidProtocolBufferException {
        byte[] data = characteristic.getValue();
        UUID uuid = characteristic.getUuid();
        if (uuid.equals(BluetoothLEHelper4.GattInfo.SENSORO_STATION_READ_CHAR_UUID)) { //出现先change后read情况
            if (byteBuffer == null) {
                buffer_data_length += data.length;
                tempBuffer = ByteBuffer.allocate(data.length);
                tempBuffer.put(data);
            }
        }
        int cmdType = bluetoothLEHelper4.getSendCmdType();
        switch (cmdType) {
            case CmdType.CMD_W_CFG:
                if (data.length >= 4) {
                    try {
                        byte[] lengthData = new byte[2];
                        System.arraycopy(data, 0, lengthData, 0, 2);
                        int length = SensoroUUID.bytesToInt(lengthData, 0);
                        byte[] retData = new byte[length - 1];
                        System.arraycopy(data, 3, retData, 0, length - 1);
                        ProtoStationMsgV2.StationMsg stationMsg = ProtoStationMsgV2.StationMsg.parseFrom(retData);
                        ProtoStationMsgV2.CmdRet cmdRet = stationMsg.getRet();
                        if (cmdRet.getNumber() == ResultCode.CODE_STATION_RET_SUCCCESS) {
                            writeCallbackHashMap.get(cmdType).onWriteSuccess(null, CmdType.CMD_NULL);
                        } else {
                            writeCallbackHashMap.get(cmdType).onWriteFailure(cmdRet.getNumber(), CmdType.CMD_NULL);
                        }
                    } catch (Exception e) {
                        writeCallbackHashMap.get(cmdType).onWriteFailure(ResultCode.PARSE_ERROR, CmdType.CMD_NULL);
                    }

                }
                break;
            case CmdType.CMD_R_CFG:
                //格式: length + version + retCode, 当数据为多包的情况下,onCharacteristicRead接收的第一个包数据不完整,因此,onCharacteristicChanged会不断被接收到数据,直到每次接收到的数据累加等于length
                //多包的情况下,可将第一次包的数据放到BufferByte里
                //数据是否写入成功
                if (byteBuffer != null) {
                    byteBuffer.put(data);
                    buffer_data_length += data.length;
                    if (buffer_data_length == buffer_total_length) {
                        try {
                            byte array[] = byteBuffer.array();
                            parseStationData(array);
                            handler.removeCallbacks(connectTimeoutRunnable);
                        } catch (Exception e) {
                            e.printStackTrace();
                            disconnect();
                            sensoroConnectionCallback.onConnectedFailure(ResultCode.PARSE_ERROR);
                            return;
                        }
                        sensoroConnectionCallback.onConnectedSuccess(bleDevice, CmdType.CMD_NULL);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void parseStationData(byte[] data) throws Exception {
        ProtoStationMsgV2.StationMsg msgCfg = ProtoStationMsgV2.StationMsg.parseFrom(data);
//        SensoroStation sensoroStation = (SensoroStation) bleDevice;

//        sensoroStation.setAccessMode(msgCfg.getNwkAccessMode().getNumber());
//        sensoroStation.setAllocationMode(msgCfg.getIpAlloc().getNumber());
//        sensoroStation.setIp(msgCfg.getIp());
//        sensoroStation.setMask(msgCfg.getMask());
//        sensoroStation.setPdns(msgCfg.getPdns());
//        sensoroStation.setAdns(msgCfg.getAdns());
//        sensoroStation.setGateway(msgCfg.getGateway());
//        sensoroStation.setEncrpt(msgCfg.getEncrypt());
//
//        sensoroStation.setPwd(msgCfg.getPwd());
//        sensoroStation.setSid(msgCfg.getSsid());
//        sensoroStation.setNetid(msgCfg.getNetid());
//        sensoroStation.setCloudaddress(msgCfg.getMcdomain());
//        sensoroStation.setCloudport(msgCfg.getMcport());
//        sensoroStation.setKey(msgCfg.getKey());

    }

    public void writeStationAdvanceConfiguration(SensoroStationConfiguration sensoroStationConfiguration, SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        ProtoStationMsgV2.StationMsg.Builder msgBuilder = ProtoStationMsgV2.StationMsg.newBuilder();
        msgBuilder.setNetid(sensoroStationConfiguration.netid);
        msgBuilder.setMcdomain(sensoroStationConfiguration.cloudaddress);
        msgBuilder.setMcport(sensoroStationConfiguration.cloudport);
        msgBuilder.setKey(sensoroStationConfiguration.key);
        ProtoStationMsgV2.StationMsg stationMsg = msgBuilder.build();
        byte[] data = stationMsg.toByteArray();
        int data_length = data.length;

        int total_length = data_length + 3;

        byte[] total_data = new byte[total_length];

        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

        byte[] version_data = SensoroUUID.intToByteArray(2, 1);

        System.arraycopy(length_data, 0, total_data, 0, 2);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);

        int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG, BluetoothLEHelper4.GattInfo.SENSORO_STATION_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }

    public void writeStationConfiguration(SensoroStationConfiguration sensoroStationConfiguration, SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        ProtoStationMsgV2.StationMsg.Builder msgBuilder = ProtoStationMsgV2.StationMsg.newBuilder();
        msgBuilder.setPwd(sensoroStationConfiguration.pwd);
        msgBuilder.setIp(sensoroStationConfiguration.ip);
        msgBuilder.setMask(sensoroStationConfiguration.mask);
        msgBuilder.setAdns(sensoroStationConfiguration.adns);
        msgBuilder.setPdns(sensoroStationConfiguration.pdns);
        msgBuilder.setGateway(sensoroStationConfiguration.gateway);
        msgBuilder.setNwkAccessMode(ProtoStationMsgV2.NwkAccessMode.valueOf(sensoroStationConfiguration.accessMode));
        msgBuilder.setIpAlloc(ProtoStationMsgV2.IPAllocationMode.valueOf(sensoroStationConfiguration.allocationMode));
        msgBuilder.setSsid(sensoroStationConfiguration.sid);
        msgBuilder.setEncrypt(sensoroStationConfiguration.encrpt);
        ProtoStationMsgV2.StationMsg stationMsg = msgBuilder.build();
        byte[] data = stationMsg.toByteArray();
        int data_length = data.length;

        int total_length = data_length + 3;

        byte[] total_data = new byte[total_length];

        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

        byte[] version_data = SensoroUUID.intToByteArray(2, 1);

        System.arraycopy(length_data, 0, total_data, 0, 2);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);

        int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG, BluetoothLEHelper4.GattInfo.SENSORO_STATION_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }



    private void writePassword(String newPassword, SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_SET_PASSWORD, writeCallback);

        int resultCode = bluetoothLEHelper4.setPassword(newPassword, BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }

    /**
     * Disconnect from beacon.
     */
    public void disconnect() {
        handler.removeCallbacks(connectTimeoutRunnable);

        if (bluetoothLEHelper4 != null) {
            bluetoothLEHelper4.close();
        }
        if (sensoroConnectionCallback != null) {
            sensoroConnectionCallback.onDisconnected();
        }

    }

    private Runnable connectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            sensoroConnectionCallback.onConnectedFailure(ResultCode.TASK_TIME_OUT);
            disconnect();
        }
    };

}
