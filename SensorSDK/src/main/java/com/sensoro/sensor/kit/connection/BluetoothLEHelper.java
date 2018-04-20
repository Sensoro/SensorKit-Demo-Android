/*
 * Copyright (c) 2014. Sensoro Inc.
 * All rights reserved.
 */

package com.sensoro.sensor.kit.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;


import com.sensoro.sensor.kit.SensoroUtils;
import com.sensoro.sensor.kit.constants.ResultCode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class BluetoothLEHelper implements Serializable {
    private static final String PASSWORD_KEY = "CtKnQ8BVb3C2khd6HQv6FFBuoHzxWi";
    private static final String BROADCAST_KEY_SECRET = "password";

    private Context context = null;
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private String bluetoothDeviceAddress = null;
    private BluetoothGatt bluetoothGatt = null;

    protected ArrayList<byte[]> writePackets;
    protected int sendPacketNumber = 1;
    private int sendCmdType = -1;

    private BluetoothGattService sensoroService = null;

    public BluetoothLEHelper(Context context) {
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

    public int requireWritePermission(String password) {
        if (sensoroService != null) {
            BluetoothGattCharacteristic authorizationChar = sensoroService.getCharacteristic(GattInfo.SENSORO_AUTHORIZATION_CHAR_UUID);
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
    public boolean checkGattServices(List<BluetoothGattService> gattServiceList) {
        for (BluetoothGattService bluetoothGattService : gattServiceList) {
            UUID serviceUUID = bluetoothGattService.getUuid();
            if (serviceUUID.equals(GattInfo.SENSORO_SENSOR_SERVICE_UUID)) {
                sensoroService = bluetoothGattService;
            }
        }

        if (sensoroService != null) {
            return true;
        }

        return false;
    }

    public void resetSendPacket() {
        writePackets = null;
        sendPacketNumber = 1;
    }

    public int getSendPacketNumber() {
        return sendPacketNumber;
    }

    public int getSendCmdType() {
        return sendCmdType;
    }

    public ArrayList<byte[]> getWritePackets() {
        return  writePackets;
    }

    public void listenNotifyChar() {
        BluetoothGattCharacteristic notifyChar = sensoroService.getCharacteristic(GattInfo.SENSORO_SENSOR_INDICATE_UUID);
        bluetoothGatt.setCharacteristicNotification(notifyChar, true);
        BluetoothGattDescriptor notifyDescriptor = notifyChar.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        notifyDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        bluetoothGatt.writeDescriptor(notifyDescriptor);
    }

    /**
     * series to write package.
     *
     * @param characteristic
     */
    public void sendPacket(BluetoothGattCharacteristic characteristic) {
        if (writePackets != null && sendPacketNumber == writePackets.size()) {
            // packets are all writen.
            resetSendPacket();
        } else {
            byte[] writePacket = writePackets.get(sendPacketNumber);
            // packet not write over.
            writeCharacteristic(characteristic, writePacket);
            sendPacketNumber++;
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

    public int writeConfiguration(byte []data, int sendCmdType) {
        if (sensoroService != null) {
            BluetoothGattCharacteristic writeChar = sensoroService.getCharacteristic(GattInfo.SENSORO_SENSOR_WRITE_UUID);
            if (writeChar != null) {
                // check beaconConfiguration
                return writeCharAllBytes(writeChar, data, sendCmdType);
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

    private int writeCharSingleBytes(BluetoothGattCharacteristic writeChar, byte[] writeBytes, int sendCmdType) {
        this.sendCmdType = sendCmdType;
        return writeCharacteristic(writeChar, writeBytes);
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
            bluetoothGatt.writeCharacteristic(writeChar);
            return ResultCode.SUCCESS;
        }
    }

    public static class GattInfo {
        public static final UUID SENSORO_SENSOR_SERVICE_UUID = UUID.fromString("DEAE0500-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_AUTHORIZATION_CHAR_UUID = UUID.fromString("DEAE0503-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_SENSOR_WRITE_UUID = UUID.fromString("DEAE0501-7A4E-1BA2-834A-50A30CCAE0E4");
        public static final UUID SENSORO_SENSOR_INDICATE_UUID = UUID.fromString("DEAE0502-7A4E-1BA2-834A-50A30CCAE0E4");

        public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    }
}
