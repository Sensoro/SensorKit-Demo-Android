/*
 * Copyright (c) 2014. Sensoro Inc.
 * All rights reserved.
 */

package com.sensoro.sensor.kit.update.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class BluetoothLEHelper4 implements Serializable {
    private static final String PASSWORD_KEY = "CtKnQ8BVb3C2khd6HQv6FFBuoHzxWi";
    private static final String BROADCAST_KEY_SECRET = "password";

    private Context context = null;
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private String bluetoothDeviceAddress = null;
    public BluetoothGatt bluetoothGatt = null;

    private ArrayList<byte[]> writePackets;
    private int sendPacketNumber = 1;
    private int sendCmdType = -1;
    protected BluetoothGattService sensoroService = null;

    public BluetoothLEHelper4(Context context) {
        this.context = context;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
//                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
//            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(String address, BluetoothGattCallback gattCallback) {
        if (bluetoothAdapter == null || address == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress) && bluetoothGatt != null) {
//            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return bluetoothGatt.connect();
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        bluetoothDeviceAddress = address;
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
//        Log.d(TAG, "Trying to create a new connection.");
        System.out.println("device.getBondState==" + device.getBondState());

        return true;
    }

    public int setPassword(String password, UUID uuid) {
        if (sensoroService != null) {
            BluetoothGattCharacteristic writeChar = sensoroService.getCharacteristic(uuid);
            if (writeChar != null) {
                // package bytes

                byte[] passwordBytes = null;
                if (password == null) {
                    passwordBytes = new byte[16];
                } else {
                    passwordBytes = convertPassword2Bytes(password);
                }

                if (passwordBytes == null) {
                    return ResultCode.INVALID_PARAM;
                }

//                CmdSetPasswordRequest cmdSetPasswordRequest = new CmdSetPasswordRequest(beacon, passwordBytes);
//                byte[] requireWritePermissionBytes = cmdSetPasswordRequest.getBytes();
//                return writeCharAllBytes(writeChar, requireWritePermissionBytes, cmdSetPasswordRequest.getCmdType());
            }
        }
        return ResultCode.SYSTEM_ERROR;
    }

    public int requireWritePermission(String password, UUID uuid) {
        if (sensoroService != null) {
            BluetoothGattCharacteristic authorizationChar = sensoroService.getCharacteristic(uuid);
            if (authorizationChar != null) {
                byte[] passwordBytes = convertPassword2Bytes(password);
                if (passwordBytes == null) {
                    return ResultCode.INVALID_PARAM;
                }
                return writeCharacteristic(authorizationChar, passwordBytes);
            }
        }
        return ResultCode.SYSTEM_ERROR;
    }

    private boolean isNIDInValid(String uid) {
        if (uid.length() != 32) {
            return true;
        }
        String nid = uid.substring(0, 20);
        String regex = "[a-f0-9A-F]{20}";
        if (Pattern.matches(regex, nid)) {
            //匹配成功
            return false;
        }
        return true;
    }

    private boolean isBIDInValid(String uid) {
        if (uid.length() != 32) {
            return true;
        }
        String bid = uid.substring(20, 32);
        String regex = "[a-f0-9A-F]{12}";
        if (Pattern.matches(regex, bid)) {
            //匹配成功
            return false;
        }
        return true;
    }

    private boolean isURLInValid(String url) {
        String regex = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        if (Pattern.matches(regex, url)) {
            //匹配成功
            return false;
        }
        byte[] urlBytes = SensoroUtils.encodeUrl(url);
        if (urlBytes != null && urlBytes.length <= 17) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isExpired(String broadcastKey) {
        int keyLength = broadcastKey.length();
        if (keyLength == 90) {
            String decrypt = SensoroUtils.decrypt_AES_256(broadcastKey.substring(2, keyLength), BROADCAST_KEY_SECRET);
            if (decrypt == null) {
                return true;
            } else {
                long expiryDate = Long.valueOf(Integer.parseInt(decrypt.substring(40, decrypt.length()), 16));
                long currentDate = System.currentTimeMillis();
                if (currentDate > expiryDate * 1000) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 生成 LED 序列命令
     *
     * @param cmdLEDBytes
     */
    private void createLEDBytes(byte sequence, int cycle, byte[] cmdLEDBytes) {
        cmdLEDBytes[0] = 0x00;
        cmdLEDBytes[1] = sequence;
        cmdLEDBytes[2] = (byte) (cycle & 0xff);
    }


    private byte[] convertPassword2Bytes(String password) {
        // encrypt paasword by HMAC-SHA512，get 16 bytes before.
        byte[] newPassword = new byte[16];
        if (password == null) {
            for (int i = 0 ; i <newPassword.length; i++) {
                newPassword[i] = 0x00;
            }
            return newPassword;
        }
        byte[] passwordBytes = null;
        try {
            passwordBytes = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return passwordBytes;
    }

    /**
     * close gatt connection
     */
    public boolean close() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * check beacon service.
     *
     * @param gattServiceList
     * @return
     */
    private boolean checkGattServices(List<BluetoothGattService> gattServiceList) {
        for (BluetoothGattService bluetoothGattService : gattServiceList) {
            UUID serviceUUID = bluetoothGattService.getUuid();
            if (serviceUUID.equals(GattInfo.SENSORO_DEVICE_SERVICE_UUID)) {
                sensoroService = bluetoothGattService;
            }
        }

        if (sensoroService != null) {
            return true;
        }

        return false;
    }

    public boolean checkGattServices(List<BluetoothGattService> gattServiceList, UUID targetServiceUUID) {
        for (BluetoothGattService bluetoothGattService : gattServiceList) {
            UUID serviceUUID = bluetoothGattService.getUuid();
            if (serviceUUID.equals(targetServiceUUID)) {
                sensoroService = bluetoothGattService;
            }
        }

        if (sensoroService != null) {
            return true;
        }

        return false;
    }

    public void listenDescriptor(UUID targetReadCharUUID) {
        try {
            this.sendCmdType = CmdType.CMD_R_CFG;
            BluetoothGattCharacteristic readChar = sensoroService.getCharacteristic(targetReadCharUUID);
            bluetoothGatt.setCharacteristicNotification(readChar, true);
            BluetoothGattDescriptor readDescriptor = readChar.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
            readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            bluetoothGatt.writeDescriptor(readDescriptor);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenSignalChar(UUID targetReadCharUUID) {
        try {
            this.sendCmdType = CmdType.CMD_R_CFG;
            BluetoothGattCharacteristic readChar = sensoroService.getCharacteristic(targetReadCharUUID);
            bluetoothGatt.setCharacteristicNotification(readChar, true);
            BluetoothGattDescriptor readDescriptor = readChar.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
            readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            bluetoothGatt.writeDescriptor(readDescriptor);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void listenOnCharactertisticRead(UUID targetReadCharUUID) {
        BluetoothGattCharacteristic readChar = sensoroService.getCharacteristic(targetReadCharUUID);
        bluetoothGatt.readCharacteristic(readChar);
    }

    public void resetSendPacket() {
        writePackets = null;
        sendPacketNumber = 1;
    }

    public int getSendCmdType() {
        return sendCmdType;
    }

    public void setSendCmdType(int cmdType) {
        this.sendCmdType = cmdType;
    }

    /**
     * series to write package.
     *
     * @param characteristic
     */
    public boolean sendPacket(BluetoothGattCharacteristic characteristic) {
        if (writePackets != null && sendPacketNumber == writePackets.size()) {
            // packets are all writen.
            resetSendPacket();
            return true;
        } else {
            byte[] writePacket = writePackets.get(sendPacketNumber);
            // packet not write over.
            writeCharacteristic(characteristic, writePacket);
            sendPacketNumber++;
            return false;
        }
    }

    /**
     * check UUID.
     *
     * @param uuid
     * @return
     */
    private boolean checkUUIDLegal(String uuid) {
        // length error
        if (uuid.length() != 36) {
            return false;
        }
        // postion 7,14,19,24 is not '-'
        if (uuid.charAt(8) != '-' && uuid.charAt(13) != '-' && uuid.charAt(18) != '-' && uuid.charAt(23) != '-') {
            return false;
        }

        // check bytes( '0~f' and '-')
        uuid = uuid.toLowerCase();
        for (int i = 0; i < uuid.length(); i++) {
            if (uuid.charAt(i) >= 'a' && uuid.charAt(i) <= 'f' || uuid.charAt(i) <= '9' && uuid.charAt(i) >= '0' || uuid.charAt(i) == '-') {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    private int writeDeviceConfigurations(byte[] writeConfigurationBytes, int cmdType) {
        if (sensoroService != null) {
            BluetoothGattCharacteristic writeChar = sensoroService.getCharacteristic(GattInfo.SENSORO_DEVICE_WRITE_CHAR_UUID);
            if (writeChar != null) {
                return writeCharAllBytes(writeChar, writeConfigurationBytes, cmdType);
            }
        }
        return ResultCode.SYSTEM_ERROR;
    }

    public int writeConfigurations(byte[] writeConfigurationBytes, int cmdType, UUID targetWriteCharUUID) {
        if (sensoroService != null) {
            BluetoothGattCharacteristic writeChar = sensoroService.getCharacteristic(targetWriteCharUUID);
            if (writeChar != null) {
                return writeCharAllBytes(writeChar, writeConfigurationBytes, cmdType);
            }
        }
        return ResultCode.SYSTEM_ERROR;
    }

    /**
     * write all bytes to char.
     *
     * @param writeChar
     * @param writeBytes
     * @return
     */
    private int writeCharAllBytes(BluetoothGattCharacteristic writeChar, byte[] writeBytes, int sendCmdType) {
        if (writeChar == null || writeBytes == null || writeBytes.length < 0) {
            return ResultCode.SYSTEM_ERROR;
        }
        if (writePackets == null) {
            this.sendCmdType = sendCmdType;
            writePackets = createWritePackets(writeBytes);
            if (writePackets == null || writePackets.size() == 0) {
                return ResultCode.SYSTEM_ERROR;
            }
            return writeCharacteristic(writeChar, writePackets.get(0));
        } else {
            return ResultCode.MCU_BUSY;
        }
    }

    /**
     * package all writing bytes.
     *
     * @param writeBytes
     * @return
     */
    private ArrayList<byte[]> createWritePackets(byte[] writeBytes) {
        if (writeBytes == null) {
            return null;
        }

        ArrayList<byte[]> writePackages = new ArrayList<byte[]>();
        for (int i = 0; i < writeBytes.length; i = i + 20) {
            if (writeBytes.length <= i + 20) {
                byte[] onePackage = new byte[writeBytes.length - i];
                System.arraycopy(writeBytes, i, onePackage, 0, writeBytes.length - i);
                writePackages.add(onePackage);
            } else {
                byte[] onePackage = new byte[20];
                System.arraycopy(writeBytes, i, onePackage, 0, 20);
                writePackages.add(onePackage);
            }
        }
        return writePackages;
    }


    /**
     * write bytes to char.
     *
     * @param writeChar
     * @param writeBytes
     * @return
     */
    private int writeCharacteristic(BluetoothGattCharacteristic writeChar, byte[] writeBytes) {
        if (writeChar == null || writeBytes == null || writeBytes.length < 0 || writeBytes.length > 20) {
            return ResultCode.SYSTEM_ERROR;
        } else {
            writeChar.setValue(writeBytes);
            boolean b = bluetoothGatt.writeCharacteristic(writeChar);
            return ResultCode.SUCCESS;
        }
    }

    public static class GattInfo {
        public static final UUID SENSORO_DEVICE_SERVICE_UUID = UUID.fromString("DEAE0300-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_DEVICE_SERVICE_UUID_ON_DFU_MODE = UUID.fromString("00001530-1212-EFDE-1523-785FEABCD123");
        public static final UUID SENSORO_DEVICE_AUTHORIZATION_CHAR_UUID = UUID.fromString("DEAE0302-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_DEVICE_WRITE_CHAR_UUID = UUID.fromString("DEAE0301-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_DEVICE_READ_CHAR_UUID = UUID.fromString("DEAE0301-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_DEVICE_SIGNAL_UUID = UUID.fromString("DEAE0303-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_SENSOR_CHAR_UUID = UUID.fromString("DEAE0301-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        public static final UUID SENSORO_STATION_SERVICE_UUID = UUID.fromString("DEAE0400-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_STATION_AUTHORIZATION_CHAR_UUID = UUID.fromString("DEAE0402-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_STATION_WRITE_CHAR_UUID = UUID.fromString("DEAE0401-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_STATION_READ_CHAR_UUID = UUID.fromString("DEAE0401-7A4E-1BA2-834A-50A30CCAE0E4");

    }
}