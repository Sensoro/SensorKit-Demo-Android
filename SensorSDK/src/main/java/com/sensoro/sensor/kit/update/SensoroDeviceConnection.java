package com.sensoro.sensor.kit.update;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sensoro.sensor.kit.update.ble.BLEDevice;
import com.sensoro.sensor.kit.update.ble.BluetoothLEHelper4;
import com.sensoro.sensor.kit.update.ble.CmdType;
import com.sensoro.sensor.kit.update.ble.ResultCode;
import com.sensoro.sensor.kit.update.ble.SensoroConnectionCallback;
import com.sensoro.sensor.kit.update.ble.SensoroDevice;
import com.sensoro.sensor.kit.update.ble.SensoroDeviceConfiguration;
import com.sensoro.sensor.kit.update.ble.SensoroSensor;
import com.sensoro.sensor.kit.update.ble.SensoroSensorConfiguration;
import com.sensoro.sensor.kit.update.ble.SensoroSlot;
import com.sensoro.sensor.kit.update.ble.SensoroUtils;
import com.sensoro.sensor.kit.update.ble.SensoroWriteCallback;
import com.sensoro.sensor.kit.update.ble.scanner.SensoroUUID;
import com.sensoro.sensor.kit.update.proto.MsgNode1V1M5;
import com.sensoro.sensor.kit.update.proto.ProtoMsgCfgV1U1;
import com.sensoro.sensor.kit.update.proto.ProtoMsgTest1U1;
import com.sensoro.sensor.kit.update.proto.ProtoStd1U1;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sensoro.sensor.kit.update.ble.CmdType.CMD_ON_DFU_MODE;

/**
 * Created by fangping on 2016/7/25.
 */

public class SensoroDeviceConnection {
    public static final byte DATA_VERSION_03 = 0x03;
    public static final byte DATA_VERSION_04 = 0x04;
    public static final byte DATA_VERSION_05 = 0x05;
    private static final String TAG = SensoroDeviceConnection.class.getSimpleName();
    private static final long CONNECT_TIME_OUT = 60000; // 30s connect timeout
    private Context context;
    private Handler handler;
    private SensoroConnectionCallback sensoroConnectionCallback;
    private Map<Integer, SensoroWriteCallback> writeCallbackHashMap;
    private boolean isBodyData = false;
    private String password;
    private BluetoothLEHelper4 bluetoothLEHelper4;
    private ListenType listenType = ListenType.UNKNOWN;
    private ByteBuffer byteBuffer = null;
    private ByteBuffer signalByteBuffer = null;
    private ByteBuffer tempBuffer = null;
    private int buffer_total_length = 0;
    private int buffer_data_length = 0;
    private int signalBuffer_total_length = 0;
    private int signalBuffer_data_length = 0;
    private byte dataVersion = DATA_VERSION_03;
    private boolean isContainSignal;
    private String macAddress;
    private Runnable connectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            sensoroConnectionCallback.onConnectedFailure(ResultCode.TASK_TIME_OUT);
            disconnect();
        }
    };
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            bluetoothLEHelper4.bluetoothGatt = gatt;
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
                if (bluetoothLEHelper4.checkGattServices(gattServiceList, BluetoothLEHelper4.GattInfo
                        .SENSORO_DEVICE_SERVICE_UUID_ON_DFU_MODE)) {
                    sensoroConnectionCallback.onConnectedSuccess(null, CMD_ON_DFU_MODE);
                    return;
                }
                if (bluetoothLEHelper4.checkGattServices(gattServiceList, BluetoothLEHelper4.GattInfo
                        .SENSORO_DEVICE_SERVICE_UUID)) {
                    if (!isContainSignal) {
                        listenType = ListenType.READ_CHAR;
                        bluetoothLEHelper4.bluetoothGatt = gatt;
                        bluetoothLEHelper4.listenDescriptor(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_READ_CHAR_UUID);
                    } else {
                        listenType = ListenType.SIGNAL_CHAR;
                        bluetoothLEHelper4.listenSignalChar(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_SIGNAL_UUID);
                    }
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
            bluetoothLEHelper4.bluetoothGatt = gatt;
            if (descriptor.getUuid().equals(BluetoothLEHelper4.GattInfo.CLIENT_CHARACTERISTIC_CONFIG)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    switch (listenType) {
                        case SIGNAL_CHAR:
                            // 监听读特征成功
                            listenType = ListenType.READ_CHAR;
                            bluetoothLEHelper4.listenDescriptor(BluetoothLEHelper4.GattInfo
                                    .SENSORO_DEVICE_READ_CHAR_UUID);
                            break;
                        case READ_CHAR:
                            UUID auth_uuid = BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_AUTHORIZATION_CHAR_UUID;
                            int resultCode = bluetoothLEHelper4.requireWritePermission(password, auth_uuid);
                            if (resultCode != ResultCode.SUCCESS) {
                                sensoroConnectionCallback.onConnectedFailure(resultCode);
                                disconnect();
                            }
                            break;
                        default:
                            sensoroConnectionCallback.onConnectedFailure(ResultCode.SYSTEM_ERROR);
                            disconnect();
                            break;
                    }
                }
            }
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            bluetoothLEHelper4.bluetoothGatt = gatt;
            parseCharacteristicWrite(characteristic, status);

        }

        private void parseCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
            // check pwd
            if (characteristic.getUuid().equals(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_AUTHORIZATION_CHAR_UUID)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bluetoothLEHelper4.listenOnCharactertisticRead(BluetoothLEHelper4.GattInfo
                            .SENSORO_DEVICE_READ_CHAR_UUID);
                } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                    sensoroConnectionCallback.onConnectedFailure(ResultCode.PASSWORD_ERR);
                    disconnect();
                } else {
                    sensoroConnectionCallback.onConnectedFailure(ResultCode.INVALID_PARAM);
                    disconnect();
                }
            }

            // flow write
            UUID uuid = characteristic.getUuid();
            if (uuid.equals(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID)) {
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
            if (characteristic.getUuid().equals(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_SIGNAL_UUID)) {
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
            bluetoothLEHelper4.bluetoothGatt = gatt;
            parseCharacteristicRead(characteristic, status);
        }

        private void parseCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
            if (characteristic.getUuid().equals(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_READ_CHAR_UUID)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte value[] = characteristic.getValue();
                    //如果value 长度小于20,说明是个完整短包
                    byte[] total_data = new byte[2];
                    System.arraycopy(value, 0, total_data, 0, total_data.length);
                    buffer_total_length = SensoroUUID.bytesToInt(total_data, 0) - 1;//数据包长度
                    byte[] version_data = new byte[1];
                    System.arraycopy(value, 2, version_data, 0, version_data.length);
                    dataVersion = version_data[0];
                    byteBuffer = ByteBuffer.allocate(buffer_total_length); //减去version一个字节长度
                    byte[] data = new byte[value.length - 3];//第一包数据
                    System.arraycopy(value, 3, data, 0, data.length);
                    byteBuffer.put(data);

                    if (buffer_total_length <= (value.length - 3)) { //一次性数据包
                        try {
                            byte[] final_data = byteBuffer.array();
                            parseData(final_data);
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
            bluetoothLEHelper4.bluetoothGatt = gatt;
            try {
                parseChangedData(characteristic);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    private SensoroDeviceConnection(Context context, BLEDevice bleDevice) {
        this.context = context;
        handler = new Handler();
        bluetoothLEHelper4 = new BluetoothLEHelper4(context);
        writeCallbackHashMap = new HashMap<>();
        this.isContainSignal = false;
    }

    public SensoroDeviceConnection(Context context, String macAddress) {
        this.context = context;
        handler = new Handler();
        bluetoothLEHelper4 = new BluetoothLEHelper4(context);
        writeCallbackHashMap = new HashMap<>();
        this.isContainSignal = false;
        this.macAddress = macAddress;
    }


    public SensoroDeviceConnection(Context context, BLEDevice bleDevice, boolean isContainSignal) {
        this.context = context;
        handler = new Handler();
        bluetoothLEHelper4 = new BluetoothLEHelper4(context);
        writeCallbackHashMap = new HashMap<>();
        this.isContainSignal = isContainSignal;
        this.macAddress = bleDevice.getMacAddress();
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

        if (!bluetoothLEHelper4.connect(macAddress, bluetoothGattCallback)) {
            sensoroConnectionCallback.onConnectedFailure(ResultCode.INVALID_PARAM);
            disconnect();
        }
    }

    private void parseChangedData(BluetoothGattCharacteristic characteristic) throws InvalidProtocolBufferException {
        byte[] data = characteristic.getValue();
        UUID uuid = characteristic.getUuid();
        if (uuid.equals(BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_READ_CHAR_UUID)) { //出现先change后read情况
            if (byteBuffer == null) {
                buffer_data_length += data.length;
                tempBuffer = ByteBuffer.allocate(data.length);
                tempBuffer.put(data);
            }
        }
        int cmdType = bluetoothLEHelper4.getSendCmdType();
        switch (cmdType) {
            case CmdType.CMD_SET_SMOKE:
                parseSmokeData(characteristic);
                break;
            case CmdType.CMD_SIGNAL:
                parseSignalData(characteristic);
                break;
            case CmdType.CMD_SET_ZERO:
                if (data.length >= 4) {
                    byte retCode = data[3];
                    if (retCode == ResultCode.CODE_DEVICE_SUCCESS) {
                        writeCallbackHashMap.get(cmdType).onWriteSuccess(null, CmdType.CMD_SET_ZERO);
                    } else {
                        writeCallbackHashMap.get(cmdType).onWriteFailure(retCode, CmdType.CMD_SET_ZERO);
                    }
                }
                break;
            case CmdType.CMD_W_CFG:
                if (data.length >= 4) {
                    byte retCode = data[3];
                    if (retCode == ResultCode.CODE_DEVICE_SUCCESS) {
                        writeCallbackHashMap.get(cmdType).onWriteSuccess(null, CmdType.CMD_NULL);
                    } else {
                        writeCallbackHashMap.get(cmdType).onWriteFailure(retCode, CmdType.CMD_NULL);
                    }
                }
                break;
            case CmdType.CMD_R_CFG:
                //格式: length + version + retCode, 当数据为多包的情况下,onCharacteristicRead接收的第一个包数据不完整,因此,
                // onCharacteristicChanged会不断被接收到数据,直到每次接收到的数据累加等于length
                //多包的情况下,可将第一次包的数据放到BufferByte里
                //数据是否写入成功
                if (byteBuffer != null) {
                    try {
                        byteBuffer.put(data);
                        buffer_data_length += data.length;
                        if (buffer_data_length == buffer_total_length) {
                            byte array[] = byteBuffer.array();
                            parseData(array);
                            handler.removeCallbacks(connectTimeoutRunnable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        disconnect();
                        sensoroConnectionCallback.onConnectedFailure(ResultCode.PARSE_ERROR);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void parseSmokeData(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        byte retCode = data[3];
        if (retCode == ResultCode.CODE_DEVICE_SUCCESS) {
            writeCallbackHashMap.get(CmdType.CMD_SET_SMOKE).onWriteSuccess(null, CmdType.CMD_SET_SMOKE);
        } else {
            writeCallbackHashMap.get(CmdType.CMD_SET_SMOKE).onWriteFailure(retCode, CmdType.CMD_SET_SMOKE);
        }
    }

    private void parseSignalData(BluetoothGattCharacteristic characteristic) {
        if (!isBodyData) {
            isBodyData = true;
            byte value[] = characteristic.getValue();
            //如果value 长度小于20,说明是个完整短包
            byte[] total_data = new byte[2];
            System.arraycopy(value, 0, total_data, 0, total_data.length);
            signalBuffer_total_length = SensoroUUID.bytesToInt(total_data, 0) - 1;//数据包长度
            byte[] data = new byte[value.length - 3];//第一包数据
            System.arraycopy(value, 3, data, 0, data.length);
            signalByteBuffer = ByteBuffer.allocate(signalBuffer_total_length); //减去version一个字节长度
            signalByteBuffer.put(data);
            if (signalBuffer_total_length == (value.length - 3)) { //一次性数据包检验
                try {
                    ProtoMsgTest1U1.MsgTest msgCfg = ProtoMsgTest1U1.MsgTest.parseFrom(data);
                    if (msgCfg.hasRetCode()) {
                        if (msgCfg.getRetCode() == 0) {
                            writeCallbackHashMap.get(CmdType.CMD_SIGNAL).onWriteSuccess(null, CmdType.CMD_SIGNAL);
                            //指令发送成功,可以正常接收数据
                        } else {
                            writeCallbackHashMap.get(CmdType.CMD_SIGNAL).onWriteFailure(0, CmdType.CMD_SIGNAL);
                        }
                    }
                    writeCallbackHashMap.get(CmdType.CMD_SIGNAL).onWriteSuccess(msgCfg, CmdType.CMD_SIGNAL);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    writeCallbackHashMap.get(CmdType.CMD_SIGNAL).onWriteFailure(0, CmdType.CMD_SIGNAL);
                    disconnect();
                } finally {
                    signalByteBuffer.clear();
                    isBodyData = false;
                }
            } else {
                signalBuffer_data_length += (value.length - 3);
            }
        } else {
            if (signalByteBuffer != null) {
                try {
                    byte value[] = characteristic.getValue();
                    signalByteBuffer.put(value);
                    signalBuffer_data_length += value.length;
                    if (signalBuffer_data_length == signalBuffer_total_length) {
                        final byte array[] = signalByteBuffer.array();
                        ProtoMsgTest1U1.MsgTest msgCfg = ProtoMsgTest1U1.MsgTest.parseFrom(array);
                        writeCallbackHashMap.get(CmdType.CMD_SIGNAL).onWriteSuccess(msgCfg, CmdType.CMD_SIGNAL);
                        isBodyData = false;
                        signalByteBuffer.clear();
                        signalBuffer_data_length = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    disconnect();
                    writeCallbackHashMap.get(CmdType.CMD_SIGNAL).onWriteFailure(ResultCode.PARSE_ERROR, CmdType
                            .CMD_SIGNAL);
                }
            }
        }
    }

    private void parseData03(byte[] data) {
        SensoroDevice sensoroDevice = new SensoroDevice();
        try {
            ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = ProtoMsgCfgV1U1.MsgCfgV1u1.parseFrom(data);
            if (msgCfg.getSlotList().size() > 4) {
                byte ibeacon_data[] = msgCfg.getSlot(4).getFrame().toByteArray();
                byte uuid_data[] = new byte[16];
                System.arraycopy(ibeacon_data, 0, uuid_data, 0, 16);
                byte major_data[] = new byte[2];
                System.arraycopy(ibeacon_data, 16, major_data, 0, 2);
                byte minor_data[] = new byte[2];
                System.arraycopy(ibeacon_data, 18, minor_data, 0, 2);
                sensoroDevice.setProximityUUID(SensoroUtils.bytesToHexString(uuid_data));
                sensoroDevice.setMajor(SensoroUUID.byteArrayToInt(major_data));
                sensoroDevice.setMinor(SensoroUUID.byteArrayToInt(minor_data));
            }

            sensoroDevice.setAppEui(SensoroUtils.bytesToHexString(msgCfg.getAppEui().toByteArray()));
            sensoroDevice.setDevUi(SensoroUtils.bytesToHexString(msgCfg.getDevEui().toByteArray()));
            sensoroDevice.setAppKey(SensoroUtils.bytesToHexString(msgCfg.getAppKey().toByteArray()));
            sensoroDevice.setAppSkey(SensoroUtils.bytesToHexString(msgCfg.getAppSkey().toByteArray()));
            sensoroDevice.setNwkSkey(SensoroUtils.bytesToHexString(msgCfg.getNwkSkey().toByteArray()));
            sensoroDevice.setBleInt(msgCfg.getBleInt());
            sensoroDevice.setBleOnTime(msgCfg.getBleOnTime());
            sensoroDevice.setBleOffTime(msgCfg.getBleOffTime());
            sensoroDevice.setBleTxp(msgCfg.getBleTxp());
            sensoroDevice.setLoraInt(msgCfg.getLoraInt());
            sensoroDevice.setLoraDr(msgCfg.getLoraDr());
            sensoroDevice.setLoraAdr(msgCfg.getLoraAdr());
            sensoroDevice.setLoraTxp(msgCfg.getLoraTxp());
            sensoroDevice.setDevAdr(msgCfg.getDevAddr());
            sensoroDevice.setLoraAdr(msgCfg.getLoraAdr());
            sensoroDevice.setTempInterval(msgCfg.getTempInt());
            sensoroDevice.setLightInterval(msgCfg.getLightInt());
            sensoroDevice.setHumidityInterval(msgCfg.getHumiInt());
            List<ProtoMsgCfgV1U1.Slot> slotList = msgCfg.getSlotList();
            int slot_size = slotList.size();
            SensoroSlot sensoroSlotArray[] = new SensoroSlot[slot_size];
            for (int i = 0; i < slot_size; i++) {
                if (i < slot_size) {
                    SensoroSlot sensoroSlot = new SensoroSlot();
                    ProtoMsgCfgV1U1.Slot slot = slotList.get(i);
                    sensoroSlot.setActived(slot.getActived());
                    sensoroSlot.setIndex(slot.getIndex());
                    sensoroSlot.setType(slot.getType().getNumber());
                    if (slot.getType() == ProtoMsgCfgV1U1.SlotType.SLOT_EDDYSTONE_URL) {
                        byte[] url_data = slot.getFrame().toByteArray();

                        sensoroSlot.setFrame(SensoroUtils.decodeUrl(url_data));
                    } else {
                        sensoroSlot.setFrame(SensoroUtils.bytesToHexString(slot.getFrame().toByteArray()));
                    }

                    sensoroSlotArray[i] = sensoroSlot;
                }
            }
            sensoroDevice.setSlotArray(sensoroSlotArray);
            sensoroDevice.setDataVersion(DATA_VERSION_03);
            sensoroDevice.setHasAppParam(false);
            sensoroDevice.setHasSensorParam(false);
            sensoroDevice.setHasEddyStone(true);
            sensoroDevice.setHasBleParam(true);
            sensoroDevice.setHasIbeacon(true);
            sensoroDevice.setHasLoraParam(true);
            sensoroDevice.setHasAdr(true);
            sensoroDevice.setHasAppKey(true);
            sensoroDevice.setHasAppSkey(true);
            sensoroDevice.setHasNwkSkey(true);
            sensoroDevice.setHasDevAddr(true);
            sensoroDevice.setHasDevEui(true);
            sensoroDevice.setHasAppEui(true);
            sensoroDevice.setHasAppKey(true);
            sensoroDevice.setHasAppSkey(true);
            sensoroDevice.setHasDataRate(true);
            sensoroDevice.setHasNwkSkey(true);
            sensoroDevice.setHasLoraInterval(true);
            sensoroDevice.setHasSensorBroadcast(true);
            sensoroDevice.setHasCustomPackage(true);

        } catch (Exception e) {
            e.printStackTrace();
            sensoroConnectionCallback.onConnectedFailure(ResultCode.PARSE_ERROR);
            return;
        }
        sensoroConnectionCallback.onConnectedSuccess(sensoroDevice, CmdType.CMD_NULL);
    }

    private void parseData04(byte[] data) {
        SensoroDevice sensoroDevice = new SensoroDevice();
        try {
            ProtoStd1U1.MsgStd msgStd = ProtoStd1U1.MsgStd.parseFrom(data);
            sensoroDevice.setClassBEnabled(msgStd.getEnableClassB());
            sensoroDevice.setClassBDataRate(msgStd.getClassBDataRate());
            sensoroDevice.setClassBPeriodicity(msgStd.getClassBPeriodicity());

            byte[] custom_data = msgStd.getCustomData().toByteArray();

            ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = ProtoMsgCfgV1U1.MsgCfgV1u1.parseFrom(custom_data);
            if (msgCfg.getSlotList().size() > 4) {
                byte ibeacon_data[] = msgCfg.getSlot(4).getFrame().toByteArray();
                byte uuid_data[] = new byte[16];
                System.arraycopy(ibeacon_data, 0, uuid_data, 0, 16);
                byte major_data[] = new byte[2];
                System.arraycopy(ibeacon_data, 16, major_data, 0, 2);
                byte minor_data[] = new byte[2];
                System.arraycopy(ibeacon_data, 18, minor_data, 0, 2);
                sensoroDevice.setProximityUUID(SensoroUtils.bytesToHexString(uuid_data));
                sensoroDevice.setMajor(SensoroUUID.byteArrayToInt(major_data));
                sensoroDevice.setMinor(SensoroUUID.byteArrayToInt(minor_data));
            }

            sensoroDevice.setAppEui(SensoroUtils.bytesToHexString(msgCfg.getAppEui().toByteArray()));
            sensoroDevice.setDevUi(SensoroUtils.bytesToHexString(msgCfg.getDevEui().toByteArray()));
            sensoroDevice.setAppKey(SensoroUtils.bytesToHexString(msgCfg.getAppKey().toByteArray()));
            sensoroDevice.setAppSkey(SensoroUtils.bytesToHexString(msgCfg.getAppSkey().toByteArray()));
            sensoroDevice.setNwkSkey(SensoroUtils.bytesToHexString(msgCfg.getNwkSkey().toByteArray()));
            sensoroDevice.setBleInt(msgCfg.getBleInt());
            sensoroDevice.setBleOnTime(msgCfg.getBleOnTime());
            sensoroDevice.setBleOffTime(msgCfg.getBleOffTime());
            sensoroDevice.setBleTxp(msgCfg.getBleTxp());
            sensoroDevice.setLoraInt(msgCfg.getLoraInt());
            sensoroDevice.setLoraDr(msgCfg.getLoraDr());
            sensoroDevice.setLoraAdr(msgCfg.getLoraAdr());
            sensoroDevice.setLoraTxp(msgCfg.getLoraTxp());
            sensoroDevice.setDevAdr(msgCfg.getDevAddr());
            sensoroDevice.setLoraAdr(msgCfg.getLoraAdr());
            sensoroDevice.setTempInterval(msgCfg.getTempInt());
            sensoroDevice.setLightInterval(msgCfg.getLightInt());
            sensoroDevice.setHumidityInterval(msgCfg.getHumiInt());
            List<ProtoMsgCfgV1U1.Slot> slotList = msgCfg.getSlotList();
            int slot_size = slotList.size();
            SensoroSlot sensoroSlotArray[] = new SensoroSlot[slot_size];
            for (int i = 0; i < slot_size; i++) {
                if (i < slot_size) {
                    SensoroSlot sensoroSlot = new SensoroSlot();
                    ProtoMsgCfgV1U1.Slot slot = slotList.get(i);
                    sensoroSlot.setActived(slot.getActived());
                    sensoroSlot.setIndex(slot.getIndex());
                    sensoroSlot.setType(slot.getType().getNumber());
                    if (slot.getType() == ProtoMsgCfgV1U1.SlotType.SLOT_EDDYSTONE_URL) {
                        byte[] url_data = slot.getFrame().toByteArray();
                        sensoroSlot.setFrame(SensoroUtils.decodeUrl(url_data));
                    } else {
                        sensoroSlot.setFrame(SensoroUtils.bytesToHexString(slot.getFrame().toByteArray()));
                    }

                    sensoroSlotArray[i] = sensoroSlot;
                }
            }
            sensoroDevice.setHasAppParam(false);
            sensoroDevice.setHasSensorParam(false);
            sensoroDevice.setHasEddyStone(true);
            sensoroDevice.setHasBleParam(true);
            sensoroDevice.setHasIbeacon(true);
            sensoroDevice.setHasLoraParam(true);
            sensoroDevice.setHasAdr(true);
            sensoroDevice.setHasAppKey(true);
            sensoroDevice.setHasAppSkey(true);
            sensoroDevice.setHasNwkSkey(true);
            sensoroDevice.setHasDevAddr(true);
            sensoroDevice.setHasDevEui(true);
            sensoroDevice.setHasAppEui(true);
            sensoroDevice.setHasAppKey(true);
            sensoroDevice.setHasAppSkey(true);
            sensoroDevice.setHasDataRate(true);
            sensoroDevice.setHasNwkSkey(true);
            sensoroDevice.setHasSensorBroadcast(true);
            sensoroDevice.setHasCustomPackage(true);
            sensoroDevice.setHasLoraInterval(true);
            sensoroDevice.setSlotArray(sensoroSlotArray);
            sensoroDevice.setDataVersion(DATA_VERSION_04);
        } catch (Exception e) {
            e.printStackTrace();
            sensoroConnectionCallback.onConnectedFailure(ResultCode.PARSE_ERROR);
            return;
        }
        sensoroConnectionCallback.onConnectedSuccess(sensoroDevice, CmdType.CMD_NULL);
    }

    private void parseData05(byte[] data) {
        SensoroSensor sensoroSensor = new SensoroSensor();
        SensoroDevice sensoroDevice = new SensoroDevice();
        try {
            MsgNode1V1M5.MsgNode msgNode = MsgNode1V1M5.MsgNode.parseFrom(data);
            if (msgNode.hasAppParam()) {
                MsgNode1V1M5.AppParam appParam = msgNode.getAppParam();
                sensoroDevice.setUploadInterval(appParam.getUploadInterval());
                sensoroDevice.setHasUploadInterval(appParam.hasUploadInterval());
                sensoroDevice.setConfirm(appParam.getConfirm());
                sensoroDevice.setHasConfirm(appParam.hasConfirm());
            }
            sensoroDevice.setHasAppParam(msgNode.hasAppParam());
            if (msgNode.hasBleParam()) {
                MsgNode1V1M5.BleParam bleParam = msgNode.getBleParam();
                sensoroDevice.setBleInt(bleParam.getBleInterval());
                sensoroDevice.setHasBleInterval(bleParam.hasBleInterval());
                sensoroDevice.setBleOffTime(bleParam.getBleOffTime());
                sensoroDevice.setHasBleOffTime(bleParam.hasBleOffTime());
                sensoroDevice.setBleOnTime(bleParam.getBleOnTime());
                sensoroDevice.setHasBleOnTime(bleParam.hasBleOnTime());
                sensoroDevice.setBleTxp(bleParam.getBleTxp());
                sensoroDevice.setHasBleTxp(bleParam.hasBleTxp());
                sensoroDevice.setHasBleOnOff(bleParam.hasBleOnOff());
            }
            sensoroDevice.setHasBleParam(msgNode.hasBleParam());
            if (msgNode.hasLoraParam()) {
                MsgNode1V1M5.LoraParam loraParam = msgNode.getLoraParam();
                sensoroDevice.setLoraAdr(loraParam.getAdr());
                sensoroDevice.setHasAdr(loraParam.hasAdr());
                sensoroDevice.setAppKey(SensoroUtils.bytesToHexString(loraParam.getAppKey().toByteArray()));
                sensoroDevice.setHasAppKey(loraParam.hasAppKey());
                sensoroDevice.setAppSkey(SensoroUtils.bytesToHexString(loraParam.getAppSkey().toByteArray()));
                sensoroDevice.setHasAppSkey(loraParam.hasAppSkey());
                sensoroDevice.setNwkSkey(SensoroUtils.bytesToHexString(loraParam.getNwkSkey().toByteArray()));
                sensoroDevice.setHasNwkSkey(loraParam.hasAppSkey());
                sensoroDevice.setHasDevAddr(loraParam.hasDevAddr());
                sensoroDevice.setDevAdr(loraParam.getDevAddr());
                sensoroDevice.setLoraDr(loraParam.getDatarate());
                sensoroDevice.setHasDevEui(loraParam.hasDevEui());
                sensoroDevice.setDevUi(SensoroUtils.bytesToHexString(loraParam.getDevEui().toByteArray()));
                sensoroDevice.setHasAppEui(loraParam.hasAppEui());
                sensoroDevice.setAppEui(SensoroUtils.bytesToHexString(loraParam.getAppEui().toByteArray()));
                sensoroDevice.setHasAppKey(loraParam.hasAppKey());
                sensoroDevice.setAppKey(SensoroUtils.bytesToHexString(loraParam.getAppKey().toByteArray()));
                sensoroDevice.setHasAppSkey(loraParam.hasAppSkey());
                sensoroDevice.setAppSkey(SensoroUtils.bytesToHexString(loraParam.getAppSkey().toByteArray()));
                sensoroDevice.setHasDataRate(loraParam.hasDatarate());
                sensoroDevice.setClassBDataRate(loraParam.getDatarate());
                sensoroDevice.setHasNwkSkey(loraParam.hasNwkSkey());
                sensoroDevice.setNwkSkey(SensoroUtils.bytesToHexString(loraParam.getNwkSkey().toByteArray()));
                sensoroDevice.setHasLoraTxp(loraParam.hasTxPower());
                sensoroDevice.setLoraTxp(loraParam.getTxPower());
                sensoroDevice.setHasActivation(loraParam.hasActivition());
                sensoroDevice.setActivation(loraParam.getActivition().getNumber());
                sensoroDevice.setHasDelay(loraParam.hasDelay());
                sensoroDevice.setDelay(loraParam.getDelay());
                sensoroDevice.setChannelMaskList(loraParam.getChannelMaskList());
                sensoroDevice.setMaxEirp(loraParam.getMaxEIRP());
                sensoroDevice.setHasMaxEirp(loraParam.hasMaxEIRP());
                sensoroDevice.setSglStatus(loraParam.getSglStatus());
                sensoroDevice.setSglDatarate(loraParam.getSglDatarate());
                sensoroDevice.setSglFrequency(loraParam.getSglFrequency());

            }
            if (msgNode.hasFlame()) {//aae7e4 ble on off temp lower disable
                sensoroSensor.setHasFlame(msgNode.hasFlame());
                sensoroSensor.setFlame(msgNode.getFlame().getData());
            }
            if (msgNode.hasPitch()) {
                sensoroSensor.setPitchAngleAlarmHigh(msgNode.getPitch().getAlarmHigh());
                sensoroSensor.setPitchAngleAlarmLow(msgNode.getPitch().getAlarmLow());
                sensoroSensor.setPitchAngle(msgNode.getPitch().getData());
                sensoroSensor.setHasPitchAngle(msgNode.hasPitch());
            }
            if (msgNode.hasRoll()) {
                sensoroSensor.setRollAngleAlarmHigh(msgNode.getRoll().getAlarmHigh());
                sensoroSensor.setRollAngleAlarmLow(msgNode.getRoll().getAlarmLow());
                sensoroSensor.setRollAngle(msgNode.getRoll().getData());
                sensoroSensor.setHasRollAngle(msgNode.hasRoll());
            }
            if (msgNode.hasYaw()) {
                sensoroSensor.setYawAngleAlarmHigh(msgNode.getYaw().getAlarmHigh());
                sensoroSensor.setYawAngleAlarmLow(msgNode.getYaw().getAlarmLow());
                sensoroSensor.setYawAngle(msgNode.getYaw().getData());
                sensoroSensor.setHasYawAngle(msgNode.hasYaw());
            }
            if (msgNode.hasWaterPressure()) {
                sensoroSensor.setWaterPressureAlarmHigh(msgNode.getWaterPressure().getAlarmHigh());
                sensoroSensor.setWaterPressureAlarmLow(msgNode.getWaterPressure().getAlarmLow());
                sensoroSensor.setWaterPressure(msgNode.getWaterPressure().getData());
                sensoroSensor.setHasWaterPressure(msgNode.hasWaterPressure());
            }
            sensoroDevice.setHasLoraParam(msgNode.hasLoraParam());
            sensoroSensor.setCh20(msgNode.getCh2O().getData());
            sensoroSensor.setHasCh2O(msgNode.hasCh2O());
            sensoroSensor.setCh4(msgNode.getCh4().getData());
            sensoroSensor.setCh4AlarmHigh(msgNode.getCh4().getAlarmHigh());
            sensoroSensor.setHasCh4(msgNode.hasCh4());
            sensoroSensor.setCoverStatus(msgNode.getCover().getData());
            sensoroSensor.setHasCover(msgNode.hasCover());
            sensoroSensor.setCo(msgNode.getCo().getData());
            sensoroSensor.setCoAlarmHigh(msgNode.getCo().getAlarmHigh());
            sensoroSensor.setHasCo(msgNode.hasCo());
            sensoroSensor.setCo2(msgNode.getCo2().getData());
            sensoroSensor.setCo2AlarmHigh(msgNode.getCo2().getAlarmHigh());
            sensoroSensor.setHasCo2(msgNode.hasCo2());
            sensoroSensor.setNo2(msgNode.getNo2().getData());
            sensoroSensor.setNo2AlarmHigh(msgNode.getNo2().getAlarmHigh());
            sensoroSensor.setHasNo2(msgNode.hasNo2());
            sensoroSensor.setSo2(msgNode.getSo2().getData());
            sensoroSensor.setHasSo2(msgNode.hasSo2());
            sensoroSensor.setHumidity(msgNode.getHumidity().getData());
            sensoroSensor.setHasHumidity(msgNode.hasHumidity());
            sensoroSensor.setTemperature(msgNode.getTemperature().getData());
            sensoroSensor.setHasTemperature(msgNode.hasTemperature());
            sensoroSensor.setLight(msgNode.getLight().getData());
            sensoroSensor.setHasLight(msgNode.hasLight());
            sensoroSensor.setLevel(msgNode.getLevel().getData());
            sensoroSensor.setHasLevel(msgNode.hasLevel());
            sensoroSensor.setLpg(msgNode.getLpg().getData());
            sensoroSensor.setLpgAlarmHigh(msgNode.getLpg().getAlarmHigh());
            sensoroSensor.setHasLpg(msgNode.hasLpg());
            sensoroSensor.setO3(msgNode.getO3().getData());
            sensoroSensor.setHasO3(msgNode.hasO3());
            sensoroSensor.setPm1(msgNode.getPm1().getData());
            sensoroSensor.setHasPm1(msgNode.hasPm1());
            sensoroSensor.setPm25(msgNode.getPm25().getData());
            sensoroSensor.setPm25AlarmHigh(msgNode.getPm25().getAlarmHigh());
            sensoroSensor.setHasPm25(msgNode.hasPm25());
            sensoroSensor.setPm10(msgNode.getPm10().getData());
            sensoroSensor.setPm10AlarmHigh(msgNode.getPm10().getAlarmHigh());
            sensoroSensor.setHasPm10(msgNode.hasPm10());
            sensoroSensor.setTempAlarmHigh(msgNode.getTemperature().getAlarmHigh());
            sensoroSensor.setTempAlarmLow(msgNode.getTemperature().getAlarmLow());
            sensoroSensor.setHumidityAlarmHigh(msgNode.getHumidity().getAlarmHigh());
            sensoroSensor.setHumidityAlarmLow(msgNode.getHumidity().getAlarmLow());
            sensoroSensor.setSmoke(msgNode.getSmoke().getData());
            sensoroSensor.setSmokeStatus(msgNode.getSmoke().getError().getNumber());//None Noraml, Unknown fault
            sensoroSensor.setHasSmoke(msgNode.hasSmoke());

            sensoroDevice.setSensoroSensor(sensoroSensor);
            sensoroDevice.setDataVersion(DATA_VERSION_05);
            sensoroDevice.setHasSensorParam(true);
            sensoroDevice.setHasEddyStone(false);
            sensoroDevice.setHasIbeacon(false);
            sensoroDevice.setHasLoraInterval(false);
            sensoroDevice.setHasSensorBroadcast(false);
            sensoroDevice.setHasCustomPackage(false);


        } catch (Exception e) {
            e.printStackTrace();
            sensoroConnectionCallback.onConnectedFailure(ResultCode.PARSE_ERROR);
            return;
        }
        sensoroConnectionCallback.onConnectedSuccess(sensoroDevice, CmdType.CMD_NULL);
    }

    private void parseData(byte[] data) {
        switch (dataVersion) {
            case DATA_VERSION_03:
                parseData03(data);
                break;
            case DATA_VERSION_04:
                parseData04(data);
                break;
            case DATA_VERSION_05:
                parseData05(data);
                break;
        }
    }

    public void writeDeviceAdvanceConfiguration(SensoroDeviceConfiguration deviceConfiguration, SensoroWriteCallback
            writeCallback) throws InvalidProtocolBufferException {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        switch (dataVersion) {
            case DATA_VERSION_03: {
                ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();
                msgCfgBuilder.setDevEui(ByteString.copyFrom(SensoroUtils.HexString2Bytes((deviceConfiguration.devEui)
                )));
                msgCfgBuilder.setAppEui(ByteString.copyFrom(SensoroUtils.HexString2Bytes((deviceConfiguration.appEui)
                )));
                msgCfgBuilder.setAppKey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration.appKey)));
                msgCfgBuilder.setAppSkey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                        .appSkey)));
                msgCfgBuilder.setNwkSkey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                        .nwkSkey)));
                msgCfgBuilder.setDevAddr(deviceConfiguration.devAdr);
                msgCfgBuilder.setLoraDr(deviceConfiguration.getLoraDr());
                msgCfgBuilder.setLoraAdr(deviceConfiguration.loadAdr);
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();

                byte[] data = msgCfg.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(3, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            case DATA_VERSION_04: {
                ProtoStd1U1.MsgStd.Builder msgStdBuilder = ProtoStd1U1.MsgStd.newBuilder();
                ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();
                msgCfgBuilder.setDevEui(ByteString.copyFrom(SensoroUtils.HexString2Bytes((deviceConfiguration.devEui)
                )));
                msgCfgBuilder.setAppEui(ByteString.copyFrom(SensoroUtils.HexString2Bytes((deviceConfiguration.appEui)
                )));
                msgCfgBuilder.setAppKey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration.appKey)));
                msgCfgBuilder.setAppSkey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                        .appSkey)));
                msgCfgBuilder.setNwkSkey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                        .nwkSkey)));
                msgCfgBuilder.setDevAddr(deviceConfiguration.devAdr);
                msgCfgBuilder.setLoraDr(deviceConfiguration.getLoraDr());
                msgCfgBuilder.setLoraAdr(deviceConfiguration.loadAdr);
                msgStdBuilder.setCustomData(msgCfgBuilder.build().toByteString());
                msgStdBuilder.setEnableClassB(deviceConfiguration.classBEnabled);
                msgStdBuilder.setClassBDataRate(deviceConfiguration.classBDateRate);
                msgStdBuilder.setClassBPeriodicity(deviceConfiguration.classBPeriodicity);
                ProtoStd1U1.MsgStd msgStd = msgStdBuilder.build();

                byte[] data = msgStd.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(4, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            case DATA_VERSION_05: {
                MsgNode1V1M5.MsgNode.Builder builder = MsgNode1V1M5.MsgNode.newBuilder();
                MsgNode1V1M5.LoraParam.Builder loraParamBuilder = MsgNode1V1M5.LoraParam.newBuilder();
                if (deviceConfiguration.hasDevEui()) {
                    loraParamBuilder.setDevEui(ByteString.copyFrom(SensoroUtils.HexString2Bytes((deviceConfiguration
                            .devEui))));
                }
                if (deviceConfiguration.hasAppEui()) {
                    loraParamBuilder.setAppEui(ByteString.copyFrom(SensoroUtils.HexString2Bytes((deviceConfiguration
                            .appEui))));
                }
                if (deviceConfiguration.hasAppKey()) {
                    loraParamBuilder.setAppKey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                            .appKey)));
                }
                if (deviceConfiguration.hasAppSkey()) {
                    loraParamBuilder.setAppSkey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                            .appSkey)));
                }
                if (deviceConfiguration.hasNwkSkey()) {
                    loraParamBuilder.setNwkSkey(ByteString.copyFrom(SensoroUtils.HexString2Bytes(deviceConfiguration
                            .nwkSkey)));
                }
                if (deviceConfiguration.hasDevAddr()) {
                    loraParamBuilder.setDevAddr(deviceConfiguration.devAdr);
                }
                if (deviceConfiguration.hasDelay()) {
                    loraParamBuilder.setDelay(deviceConfiguration.delay);
                }
                List<Integer> channelList = deviceConfiguration.getChannelList();
                loraParamBuilder.addAllChannelMask(channelList);
                loraParamBuilder.setAdr(deviceConfiguration.getLoraAdr());
                loraParamBuilder.setDatarate(deviceConfiguration.getLoraDr());
                if (deviceConfiguration.hasActivation()) {
                    loraParamBuilder.setActivition(MsgNode1V1M5.Activtion.valueOf(deviceConfiguration.activation));
                }
                builder.setLoraParam(loraParamBuilder);
                byte[] data = builder.build().toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(5, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            default:
                break;
        }


    }

    public void writeModuleConfiguration(SensoroDeviceConfiguration deviceConfiguration, SensoroWriteCallback
            writeCallback) throws InvalidProtocolBufferException {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();
        msgCfgBuilder.setLoraTxp(deviceConfiguration.loraTxp);
        ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();
        ProtoStd1U1.MsgStd.Builder msgStdBuilder = ProtoStd1U1.MsgStd.newBuilder();
        msgStdBuilder.setCustomData(msgCfg.toByteString());
        ProtoStd1U1.MsgStd msgStd = msgStdBuilder.build();
        byte[] data = msgStd.toByteArray();
        int data_length = data.length;

        int total_length = data_length + 3;

        byte[] total_data = new byte[total_length];

        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
        System.arraycopy(length_data, 0, total_data, 0, 2);
        byte[] version_data = SensoroUUID.intToByteArray(4, 1);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);
        int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }

    public void writeData05Configuration(SensoroDeviceConfiguration sensoroDeviceConfiguration, SensoroWriteCallback
            writeCallback) throws InvalidProtocolBufferException {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        MsgNode1V1M5.MsgNode.Builder msgNodeBuilder = MsgNode1V1M5.MsgNode.newBuilder();
        SensoroSensorConfiguration sensorConfiguration = sensoroDeviceConfiguration.getSensorConfiguration();
        if (sensorConfiguration.hasCh4()) {
            MsgNode1V1M5.SensorData.Builder ch4Builder = MsgNode1V1M5.SensorData.newBuilder();
            ch4Builder.setAlarmHigh(sensorConfiguration.getCh4AlarmHigh());
            ch4Builder.setData(sensorConfiguration.getCh4Data());
            msgNodeBuilder.setCh4(ch4Builder);
        }
        if (sensorConfiguration.hasCo()) {
            MsgNode1V1M5.SensorData.Builder coBuilder = MsgNode1V1M5.SensorData.newBuilder();
            coBuilder.setAlarmHigh(sensorConfiguration.getCoAlarmHigh());
            coBuilder.setData(sensorConfiguration.getCoData());
            msgNodeBuilder.setCo(coBuilder);
        }

        if (sensorConfiguration.hasCo2()) {
            MsgNode1V1M5.SensorData.Builder co2Builder = MsgNode1V1M5.SensorData.newBuilder();
            co2Builder.setAlarmHigh(sensorConfiguration.getCo2AlarmHigh());
            co2Builder.setData(sensorConfiguration.getCo2Data());
            msgNodeBuilder.setCo2(co2Builder);
        }
        if (sensorConfiguration.hasNo2()) {
            MsgNode1V1M5.SensorData.Builder no2Builder = MsgNode1V1M5.SensorData.newBuilder();
            no2Builder.setAlarmHigh(sensorConfiguration.getNo2AlarmHigh());
            no2Builder.setData(sensorConfiguration.getNo2Data());
            msgNodeBuilder.setNo2(no2Builder);
        }
        if (sensorConfiguration.hasLpg()) {
            MsgNode1V1M5.SensorData.Builder lpgBuilder = MsgNode1V1M5.SensorData.newBuilder();
            lpgBuilder.setAlarmHigh(sensorConfiguration.getLpgAlarmHigh());
            lpgBuilder.setData(sensorConfiguration.getLpgData());
            msgNodeBuilder.setLpg(lpgBuilder);
        }

        if (sensorConfiguration.hasPm10()) {
            MsgNode1V1M5.SensorData.Builder pm10Builder = MsgNode1V1M5.SensorData.newBuilder();
            pm10Builder.setAlarmHigh(sensorConfiguration.getPm10AlarmHigh());
            pm10Builder.setData(sensorConfiguration.getPm10Data());
            msgNodeBuilder.setPm10(pm10Builder);
        }
        if (sensorConfiguration.hasPm25()) {
            MsgNode1V1M5.SensorData.Builder pm25Builder = MsgNode1V1M5.SensorData.newBuilder();
            pm25Builder.setAlarmHigh(sensorConfiguration.getPm25AlarmHigh());
            pm25Builder.setData(sensorConfiguration.getPm25Data());
            msgNodeBuilder.setPm25(pm25Builder);
        }
        if (sensorConfiguration.hasTemperature()) {
            MsgNode1V1M5.SensorData.Builder tempBuilder = MsgNode1V1M5.SensorData.newBuilder();
            tempBuilder.setAlarmHigh(sensorConfiguration.getTempAlarmHigh());
            tempBuilder.setAlarmLow(sensorConfiguration.getTempAlarmLow());
            msgNodeBuilder.setTemperature(tempBuilder);
        }
        if (sensorConfiguration.hasHumidity()) {
            MsgNode1V1M5.SensorData.Builder humidityBuilder = MsgNode1V1M5.SensorData.newBuilder();
            humidityBuilder.setAlarmHigh(sensorConfiguration.getHumidityHigh());
            humidityBuilder.setAlarmLow(sensorConfiguration.getHumidityLow());
            msgNodeBuilder.setHumidity(humidityBuilder);
        }
        if (sensorConfiguration.hasPitchAngle()) {
            MsgNode1V1M5.SensorData.Builder pitchBuilder = MsgNode1V1M5.SensorData.newBuilder();
            pitchBuilder.setAlarmHigh(sensorConfiguration.getPitchAngleAlarmHigh());
            pitchBuilder.setAlarmLow(sensorConfiguration.getPitchAngleAlarmLow());
            msgNodeBuilder.setPitch(pitchBuilder);
        }
        if (sensorConfiguration.hasRollAngle()) {
            MsgNode1V1M5.SensorData.Builder rollAngleBuilder = MsgNode1V1M5.SensorData.newBuilder();
            rollAngleBuilder.setAlarmHigh(sensorConfiguration.getRollAngleAlarmHigh());
            rollAngleBuilder.setAlarmLow(sensorConfiguration.getRollAngleAlarmLow());
            msgNodeBuilder.setRoll(rollAngleBuilder);
        }
        if (sensorConfiguration.hasYawAngle()) {
            MsgNode1V1M5.SensorData.Builder yawAngleBuilder = MsgNode1V1M5.SensorData.newBuilder();
            yawAngleBuilder.setAlarmHigh(sensorConfiguration.getYawAngleAlarmHigh());
            yawAngleBuilder.setAlarmLow(sensorConfiguration.getYawAngleAlarmLow());
            msgNodeBuilder.setYaw(yawAngleBuilder);
        }
        if (sensorConfiguration.hasWaterPressure()) {
            MsgNode1V1M5.SensorData.Builder waterPressureBuilder = MsgNode1V1M5.SensorData.newBuilder();
            waterPressureBuilder.setAlarmHigh(sensorConfiguration.getWaterPressureAlarmHigh());
            waterPressureBuilder.setAlarmLow(sensorConfiguration.getWaterPressureAlarmLow());
            msgNodeBuilder.setWaterPressure(waterPressureBuilder);
        }
        if (sensoroDeviceConfiguration.hasAppParam()) {
            MsgNode1V1M5.AppParam.Builder appBuilder = MsgNode1V1M5.AppParam.newBuilder();
            if (sensoroDeviceConfiguration.hasUploadInterval()) {
                appBuilder.setUploadInterval(sensoroDeviceConfiguration.getUploadIntervalData());
            }

            if (sensoroDeviceConfiguration.hasConfirm()) {
                appBuilder.setConfirm(sensoroDeviceConfiguration.getConfirmData());
            }
            msgNodeBuilder.setAppParam(appBuilder);
        }


        MsgNode1V1M5.LoraParam.Builder loraBuilder = MsgNode1V1M5.LoraParam.newBuilder();
        loraBuilder.setTxPower(sensoroDeviceConfiguration.getLoraTxp());
//        loraBuilder.setMaxEIRP(sensoroDeviceConfiguration.getLoraEirp());
//        loraBuilder.setSglStatus(sensoroDeviceConfiguration.getSglStatus());
//        loraBuilder.setSglFrequency(sensoroDeviceConfiguration.getSglFrequency());
//        loraBuilder.setSglDatarate(sensoroDeviceConfiguration.getSglDatarate());

        MsgNode1V1M5.BleParam.Builder bleBuilder = MsgNode1V1M5.BleParam.newBuilder();
        bleBuilder.setBleInterval(sensoroDeviceConfiguration.getBleInt());
        bleBuilder.setBleOffTime(sensoroDeviceConfiguration.getBleTurnOffTime());
        bleBuilder.setBleOnTime(sensoroDeviceConfiguration.getBleTurnOnTime());
        bleBuilder.setBleTxp(sensoroDeviceConfiguration.getBleTxp());
        msgNodeBuilder.setBleParam(bleBuilder);
        msgNodeBuilder.setLoraParam(loraBuilder);
        byte[] data = msgNodeBuilder.build().toByteArray();
        int data_length = data.length;

        int total_length = data_length + 3;

        byte[] total_data = new byte[total_length];

        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
        System.arraycopy(length_data, 0, total_data, 0, 2);
        byte[] version_data = SensoroUUID.intToByteArray(5, 1);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);
        int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }

    public void writeSmokeCmd(MsgNode1V1M5.AppParam.Builder builder, SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_SET_SMOKE, writeCallback);
        MsgNode1V1M5.MsgNode.Builder msgNodeBuilder = MsgNode1V1M5.MsgNode.newBuilder();
        msgNodeBuilder.setAppParam(builder);
        byte[] data = msgNodeBuilder.build().toByteArray();
        writeData05Cmd(data, CmdType.CMD_SET_SMOKE, writeCallback);
    }

    public void writeZeroCmd(SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_SET_ZERO, writeCallback);
        MsgNode1V1M5.MsgNode.Builder msgNodeBuilder = MsgNode1V1M5.MsgNode.newBuilder();
        MsgNode1V1M5.SensorData.Builder sensorDataBuilder = MsgNode1V1M5.SensorData.newBuilder();
        sensorDataBuilder.setCalibration(1);
        msgNodeBuilder.setPitch(sensorDataBuilder.build());
        msgNodeBuilder.setRoll(sensorDataBuilder.build());
        msgNodeBuilder.setYaw(sensorDataBuilder.build());
        byte[] data = msgNodeBuilder.build().toByteArray();
        writeData05Cmd(data, CmdType.CMD_SET_ZERO, writeCallback);
    }

    private void writeData05Cmd(byte data[], int cmdType, SensoroWriteCallback writeCallback) {
        int data_length = data.length;

        int total_length = data_length + 3;

        byte[] total_data = new byte[total_length];

        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
        System.arraycopy(length_data, 0, total_data, 0, 2);
        byte[] version_data = SensoroUUID.intToByteArray(5, 1);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);
        int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, cmdType, BluetoothLEHelper4.GattInfo
                .SENSORO_DEVICE_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }

    public void writeDataConfiguration(SensoroDeviceConfiguration deviceConfiguration, SensoroWriteCallback
            writeCallback) throws InvalidProtocolBufferException {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();
        msgCfgBuilder.setLoraInt(deviceConfiguration.loraInt.intValue());
        msgCfgBuilder.setLoraTxp(deviceConfiguration.loraTxp);

        msgCfgBuilder.setBleTxp(deviceConfiguration.bleTxp);
        msgCfgBuilder.setBleInt(deviceConfiguration.bleInt.intValue());
        msgCfgBuilder.setBleOnTime(deviceConfiguration.bleTurnOnTime);
        msgCfgBuilder.setBleOffTime(deviceConfiguration.bleTurnOffTime);

        SensoroSlot[] sensoroSlots = deviceConfiguration.sensoroSlots;
        for (int i = 0; i < sensoroSlots.length; i++) {
            ProtoMsgCfgV1U1.Slot.Builder builder = ProtoMsgCfgV1U1.Slot.newBuilder();
            SensoroSlot sensoroSlot = sensoroSlots[i];
            if (sensoroSlot.isActived() == 1) {
                switch (i) {
                    case 4:
                        byte uuid_data[] = SensoroUtils.HexString2Bytes(deviceConfiguration.proximityUUID);
                        byte major_data[] = SensoroUUID.intToByteArray(deviceConfiguration.major, 2);
                        byte minor_data[] = SensoroUUID.intToByteArray(deviceConfiguration.minor, 2);
                        byte ibeacon_data[] = new byte[20];
                        System.arraycopy(uuid_data, 0, ibeacon_data, 0, 16);
                        System.arraycopy(major_data, 0, ibeacon_data, 16, 2);
                        System.arraycopy(minor_data, 0, ibeacon_data, 18, 2);
                        builder.setFrame(ByteString.copyFrom(ibeacon_data));
                        break;
                    case 5:
                    case 6:
                    case 7:
                        String frameString = sensoroSlot.getFrame();
                        if (frameString != null) {
                            builder.setFrame(ByteString.copyFrom(SensoroUtils.HexString2Bytes(frameString)));
                        }

                        break;
                    default:
                        switch (sensoroSlot.getType()) {
                            case ProtoMsgCfgV1U1.SlotType.SLOT_EDDYSTONE_URL_VALUE:
                                builder.setFrame(ByteString.copyFrom(SensoroUtils.encodeUrl(sensoroSlot.getFrame())));
                                break;
                            default:
                                builder.setFrame(ByteString.copyFrom(SensoroUtils.HexString2Bytes(sensoroSlot
                                        .getFrame())));
                                break;
                        }
                        break;
                }

            }
            builder.setIndex(i);
            builder.setType(ProtoMsgCfgV1U1.SlotType.valueOf(sensoroSlot.getType()));
            builder.setActived(sensoroSlot.isActived());
            msgCfgBuilder.addSlot(i, builder.build());
        }

        switch (dataVersion) {
            case DATA_VERSION_03: {
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();

                byte[] data = msgCfg.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
                System.arraycopy(length_data, 0, total_data, 0, 2);
                byte[] version_data = SensoroUUID.intToByteArray(3, 1);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);
                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            case DATA_VERSION_04: {
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();
                ProtoStd1U1.MsgStd.Builder msgStdBuilder = ProtoStd1U1.MsgStd.newBuilder();
                msgStdBuilder.setCustomData(msgCfg.toByteString());
                ProtoStd1U1.MsgStd msgStd = msgStdBuilder.build();
                byte[] data = msgStd.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
                System.arraycopy(length_data, 0, total_data, 0, 2);
                byte[] version_data = SensoroUUID.intToByteArray(4, 1);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);
                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            default:
                break;
        }
    }

    public void writeMultiData05Configuration(SensoroDeviceConfiguration deviceConfiguration, SensoroWriteCallback
            writeCallback) throws InvalidProtocolBufferException {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        MsgNode1V1M5.MsgNode.Builder msgNodeBuilder = MsgNode1V1M5.MsgNode.newBuilder();
        if (deviceConfiguration.hasLoraParam()) {
            MsgNode1V1M5.LoraParam.Builder loraParamBuilder = MsgNode1V1M5.LoraParam.newBuilder();
            loraParamBuilder.setTxPower(deviceConfiguration.loraTxp);
            msgNodeBuilder.setLoraParam(loraParamBuilder);
        }
        if (deviceConfiguration.hasBleParam()) {
            MsgNode1V1M5.BleParam.Builder bleParamBuilder = MsgNode1V1M5.BleParam.newBuilder();
            bleParamBuilder.setBleTxp(deviceConfiguration.bleTxp);
            bleParamBuilder.setBleInterval(deviceConfiguration.bleInt.intValue());
            bleParamBuilder.setBleOnTime(deviceConfiguration.bleTurnOnTime);
            bleParamBuilder.setBleOffTime(deviceConfiguration.bleTurnOffTime);
            msgNodeBuilder.setBleParam(bleParamBuilder);
        }

        SensoroSensorConfiguration sensorConfiguration = deviceConfiguration.getSensorConfiguration();
        if (sensorConfiguration.hasCh4()) {
            MsgNode1V1M5.SensorData.Builder ch4Builder = MsgNode1V1M5.SensorData.newBuilder();
            ch4Builder.setAlarmHigh(sensorConfiguration.getCh4AlarmHigh());
            ch4Builder.setData(sensorConfiguration.getCh4Data());
            msgNodeBuilder.setCh4(ch4Builder);
        }
        if (sensorConfiguration.hasCo()) {
            MsgNode1V1M5.SensorData.Builder coBuilder = MsgNode1V1M5.SensorData.newBuilder();
            coBuilder.setAlarmHigh(sensorConfiguration.getCoAlarmHigh());
            coBuilder.setData(sensorConfiguration.getCoData());
            msgNodeBuilder.setCo(coBuilder);
        }

        if (sensorConfiguration.hasCo2()) {
            MsgNode1V1M5.SensorData.Builder co2Builder = MsgNode1V1M5.SensorData.newBuilder();
            co2Builder.setAlarmHigh(sensorConfiguration.getCo2AlarmHigh());
            co2Builder.setData(sensorConfiguration.getCo2Data());
            msgNodeBuilder.setCo2(co2Builder);
        }
        if (sensorConfiguration.hasNo2()) {
            MsgNode1V1M5.SensorData.Builder no2Builder = MsgNode1V1M5.SensorData.newBuilder();
            no2Builder.setAlarmHigh(sensorConfiguration.getNo2AlarmHigh());
            no2Builder.setData(sensorConfiguration.getNo2Data());
            msgNodeBuilder.setNo2(no2Builder);
        }
        if (sensorConfiguration.hasLpg()) {
            MsgNode1V1M5.SensorData.Builder lpgBuilder = MsgNode1V1M5.SensorData.newBuilder();
            lpgBuilder.setAlarmHigh(sensorConfiguration.getLpgAlarmHigh());
            lpgBuilder.setData(sensorConfiguration.getLpgData());
            msgNodeBuilder.setLpg(lpgBuilder);
        }

        if (sensorConfiguration.hasPm10()) {
            MsgNode1V1M5.SensorData.Builder pm10Builder = MsgNode1V1M5.SensorData.newBuilder();
            pm10Builder.setAlarmHigh(sensorConfiguration.getPm10AlarmHigh());
            pm10Builder.setData(sensorConfiguration.getPm10Data());
            msgNodeBuilder.setPm10(pm10Builder);
        }
        if (sensorConfiguration.hasPm25()) {
            MsgNode1V1M5.SensorData.Builder pm25Builder = MsgNode1V1M5.SensorData.newBuilder();
            pm25Builder.setAlarmHigh(sensorConfiguration.getPm25AlarmHigh());
            pm25Builder.setData(sensorConfiguration.getPm25Data());
            msgNodeBuilder.setPm25(pm25Builder);
        }
        if (sensorConfiguration.hasTemperature()) {
            MsgNode1V1M5.SensorData.Builder tempBuilder = MsgNode1V1M5.SensorData.newBuilder();
            tempBuilder.setAlarmHigh(sensorConfiguration.getTempAlarmHigh());
            tempBuilder.setAlarmLow(sensorConfiguration.getTempAlarmLow());
            msgNodeBuilder.setTemperature(tempBuilder);
        }
        if (sensorConfiguration.hasHumidity()) {
            MsgNode1V1M5.SensorData.Builder humidityBuilder = MsgNode1V1M5.SensorData.newBuilder();
            humidityBuilder.setAlarmHigh(sensorConfiguration.getHumidityHigh());
            humidityBuilder.setAlarmLow(sensorConfiguration.getHumidityLow());
            msgNodeBuilder.setHumidity(humidityBuilder);
        }
        if (sensorConfiguration.hasWaterPressure()) {
            MsgNode1V1M5.SensorData.Builder waterPressureBuilder = MsgNode1V1M5.SensorData.newBuilder();
            waterPressureBuilder.setAlarmHigh(sensorConfiguration.getWaterPressureAlarmHigh());
            waterPressureBuilder.setAlarmLow(sensorConfiguration.getWaterPressureAlarmLow());
            msgNodeBuilder.setWaterPressure(waterPressureBuilder);
        }
        if (deviceConfiguration.hasAppParam()) {//BB65
            if (deviceConfiguration.hasUploadInterval()) {
                MsgNode1V1M5.AppParam.Builder appBuilder = MsgNode1V1M5.AppParam.newBuilder();
                appBuilder.setUploadInterval(deviceConfiguration.getUploadIntervalData());
                msgNodeBuilder.setAppParam(appBuilder);
            }

            if (deviceConfiguration.hasConfirm()) {
                MsgNode1V1M5.AppParam.Builder appBuilder = MsgNode1V1M5.AppParam.newBuilder();
                appBuilder.setConfirm(deviceConfiguration.getConfirmData());
                msgNodeBuilder.setAppParam(appBuilder);
            }
        }
        byte[] data = msgNodeBuilder.build().toByteArray();
        int data_length = data.length;

        int total_length = data_length + 3;

        byte[] total_data = new byte[total_length];

        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
        System.arraycopy(length_data, 0, total_data, 0, 2);
        byte[] version_data = SensoroUUID.intToByteArray(5, 1);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);
        int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
        }
    }

    public void writeMultiDataConfiguration(SensoroDeviceConfiguration deviceConfiguration, SensoroWriteCallback
            writeCallback) throws InvalidProtocolBufferException {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();

        msgCfgBuilder.setLoraTxp(deviceConfiguration.loraTxp);
        msgCfgBuilder.setLoraInt(deviceConfiguration.loraInt.intValue());

        msgCfgBuilder.setBleTxp(deviceConfiguration.bleTxp);
        msgCfgBuilder.setBleInt(deviceConfiguration.bleInt.intValue());
        msgCfgBuilder.setBleOnTime(deviceConfiguration.bleTurnOnTime);
        msgCfgBuilder.setBleOffTime(deviceConfiguration.bleTurnOffTime);

        SensoroSlot[] sensoroSlots = deviceConfiguration.sensoroSlots;
        for (int i = 0; i < sensoroSlots.length; i++) {
            ProtoMsgCfgV1U1.Slot.Builder builder = ProtoMsgCfgV1U1.Slot.newBuilder();
            SensoroSlot sensoroSlot = sensoroSlots[i];
            if (sensoroSlot.isActived() == 1) {
                switch (i) {
                    case 4:
                        byte uuid_data[] = SensoroUtils.HexString2Bytes(deviceConfiguration.proximityUUID);
                        byte major_data[] = SensoroUUID.intToByteArray(deviceConfiguration.major, 2);
                        byte minor_data[] = SensoroUUID.intToByteArray(deviceConfiguration.minor, 2);
                        byte ibeacon_data[] = new byte[20];
                        System.arraycopy(uuid_data, 0, ibeacon_data, 0, 16);
                        System.arraycopy(major_data, 0, ibeacon_data, 16, 2);
                        System.arraycopy(minor_data, 0, ibeacon_data, 18, 2);
                        builder.setFrame(ByteString.copyFrom(ibeacon_data));
                        break;
                    case 5:
                    case 6:
                    case 7:
                        String frameString = sensoroSlot.getFrame();
                        if (frameString != null) {
                            builder.setFrame(ByteString.copyFrom(SensoroUtils.HexString2Bytes(frameString)));
                        }

                        break;
                    default:
                        switch (sensoroSlot.getType()) {
                            case ProtoMsgCfgV1U1.SlotType.SLOT_EDDYSTONE_URL_VALUE:
                                builder.setFrame(ByteString.copyFrom(SensoroUtils.encodeUrl(sensoroSlot.getFrame())));
                                break;
                            default:
                                builder.setFrame(ByteString.copyFrom(SensoroUtils.HexString2Bytes(sensoroSlot
                                        .getFrame())));
                                break;
                        }
                        break;
                }

            }
            builder.setIndex(i);
            builder.setType(ProtoMsgCfgV1U1.SlotType.valueOf(sensoroSlot.getType()));
            builder.setActived(sensoroSlot.isActived());
            msgCfgBuilder.addSlot(i, builder.build());

        }

        switch (dataVersion) {
            case DATA_VERSION_03: {
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();

                byte[] data = msgCfg.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
                System.arraycopy(length_data, 0, total_data, 0, 2);
                byte[] version_data = SensoroUUID.intToByteArray(3, 1);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);
                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            case DATA_VERSION_04: {
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();
                ProtoStd1U1.MsgStd.Builder msgStdBuilder = ProtoStd1U1.MsgStd.newBuilder();
                msgStdBuilder.setCustomData(msgCfg.toByteString());
                ProtoStd1U1.MsgStd msgStd = msgStdBuilder.build();
                byte[] data = msgStd.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
                System.arraycopy(length_data, 0, total_data, 0, 2);
                byte[] version_data = SensoroUUID.intToByteArray(4, 1);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);
                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
            default:
                break;
        }

    }

    public void writeCmd(SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        switch (dataVersion) {
            case DATA_VERSION_03: {
                ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();
                msgCfgBuilder.setCmd(2);
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();
                byte[] data = msgCfg.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(3, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(ResultCode.CODE_DEVICE_DFU_ERROR, CmdType.CMD_NULL);
                }
            }
            break;
            case DATA_VERSION_04: {
                ProtoMsgCfgV1U1.MsgCfgV1u1.Builder msgCfgBuilder = ProtoMsgCfgV1U1.MsgCfgV1u1.newBuilder();
                msgCfgBuilder.setCmd(2);
                ProtoMsgCfgV1U1.MsgCfgV1u1 msgCfg = msgCfgBuilder.build();

                ProtoStd1U1.MsgStd.Builder msgStdBuilder = ProtoStd1U1.MsgStd.newBuilder();
                msgStdBuilder.setCustomData(msgCfg.toByteString());
                ProtoStd1U1.MsgStd msgStd = msgStdBuilder.build();
                byte[] data = msgStd.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(4, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(ResultCode.CODE_DEVICE_DFU_ERROR, CmdType.CMD_NULL);
                }
            }
            break;
            case DATA_VERSION_05: {
                MsgNode1V1M5.MsgNode.Builder nodeBuilder = MsgNode1V1M5.MsgNode.newBuilder();
                MsgNode1V1M5.AppParam.Builder appBuilder = MsgNode1V1M5.AppParam.newBuilder();
                appBuilder.setCmd(MsgNode1V1M5.AppCmd.APP_CMD_DFU);
                nodeBuilder.setAppParam(appBuilder);
                byte[] data = nodeBuilder.build().toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(5, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_W_CFG,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(ResultCode.CODE_DEVICE_DFU_ERROR, CmdType.CMD_NULL);
                }
            }
            break;
            default:
                break;
        }
    }

    public void writeSignalData(int freq, int dr, int txPower, int interval, SensoroWriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_SIGNAL, writeCallback);
        switch (dataVersion) {
            default: {
                ProtoMsgTest1U1.MsgTest.Builder builder = ProtoMsgTest1U1.MsgTest.newBuilder();
                builder.setUplinkFreq(freq);
//                builder.setUplinkDR(dr);
//                builder.setUplinkTxPower(txPower);
                builder.setUplinkInterval(interval);
                ProtoMsgTest1U1.MsgTest msgTest = builder.build();
                byte[] data = msgTest.toByteArray();
                int data_length = data.length;

                int total_length = data_length + 3;

                byte[] total_data = new byte[total_length];

                byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);

                byte[] version_data = SensoroUUID.intToByteArray(1, 1);

                System.arraycopy(length_data, 0, total_data, 0, 2);
                System.arraycopy(version_data, 0, total_data, 2, 1);
                System.arraycopy(data, 0, total_data, 3, data_length);

                int resultCode = bluetoothLEHelper4.writeConfigurations(total_data, CmdType.CMD_SIGNAL,
                        BluetoothLEHelper4.GattInfo.SENSORO_DEVICE_SIGNAL_UUID);
                if (resultCode != ResultCode.SUCCESS) {
                    writeCallback.onWriteFailure(resultCode, CmdType.CMD_NULL);
                }
            }
            break;
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

    enum ListenType implements Serializable {
        SENSOR_CHAR, READ_CHAR, SIGNAL_CHAR, UNKNOWN;
    }
}
