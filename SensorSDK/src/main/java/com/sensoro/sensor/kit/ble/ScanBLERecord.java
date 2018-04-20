/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensoro.sensor.kit.ble;

import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import com.sensoro.sensor.kit.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a scan record from Bluetooth LE scan.
 */
public final class ScanBLERecord {

    private static final String TAG = "ScanBLERecord";

    // The following data type values are assigned by Bluetooth SIG.
    // For more details refer to Bluetooth 4.1 specification, Volume 3, Part C, Section 18.
    private static final int DATA_TYPE_FLAGS = 0x01;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 0x02;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;
    private static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
    private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
    private static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
    private static final int DATA_TYPE_SERVICE_DATA = 0x16;
    private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

    // Flags of the advertising data.
    private final int mAdvertiseFlags;

    @Nullable
    private final List<ParcelUuid> mServiceUuids;

    private final SparseArray<byte[]> mManufacturerSpecificData;

    private final Map<ParcelUuid, byte[]> mServiceData;

    // Transmission power level(in dB).
    private final int mTxPowerLevel;

    // Local name of the Bluetooth LE device.
    private final String mDeviceName;

    // Raw bytes of scan record.
    private final byte[] mBytes;

    /**
     * Returns the advertising flags indicating the discoverable mode and capability of the device.
     * Returns -1 if the flag field is not set.
     */
    public int getAdvertiseFlags() {
        return mAdvertiseFlags;
    }

    /**
     * Returns a list of service UUIDs within the advertisement that are used to identify the
     * bluetooth GATT services.
     */
    public List<ParcelUuid> getServiceUuids() {
        return mServiceUuids;
    }

    /**
     * Returns a sparse array of manufacturer identifier and its corresponding manufacturer specific
     * data.
     */
    public SparseArray<byte[]> getManufacturerSpecificData() {
        return mManufacturerSpecificData;
    }

    /**
     * Returns the manufacturer specific data associated with the manufacturer id. Returns
     * {@code null} if the {@code manufacturerId} is not found.
     */
    @Nullable
    public byte[] getManufacturerSpecificData(int manufacturerId) {
        return mManufacturerSpecificData.get(manufacturerId);
    }

    /**
     * Returns a map of service UUID and its corresponding service data.
     */
    public Map<ParcelUuid, byte[]> getServiceData() {
        return mServiceData;
    }

    /**
     * Returns the service data byte array associated with the {@code serviceUuid}. Returns
     * {@code null} if the {@code serviceDataUuid} is not found.
     */
    @Nullable
    public byte[] getServiceData(ParcelUuid serviceDataUuid) {
        if (serviceDataUuid == null) {
            return null;
        }
        Iterator<Map.Entry<ParcelUuid, byte[]>> iterator =
                mServiceData.entrySet().iterator();
        while (iterator.hasNext()){
//            "00001234-0000-1000-8000-00805f9b34fb" ->
            Map.Entry<ParcelUuid, byte[]> next = iterator.next();
            ParcelUuid key = next.getKey();
            byte[] value = next.getValue();
        }
//        String s = serviceDataUuid.toString();
//        if (s.contains(SENSOR_SERVICE_UUID_E3412)){
//            s=s.replace(SENSOR_SERVICE_UUID_E3412,"1234");
//            serviceDataUuid=new ParcelUuid(UUID.fromString(s));
//        }

//        return new ParcelUuid(UUID.fromString(SERVICE_UUID.replace("XXXX", serviceDataUUID)));
//        if (serviceDataUuid.)
        return mServiceData.get(serviceDataUuid);
    }

    /**
     * Returns the transmission power level of the packet in dBm. Returns {@link Integer#MIN_VALUE}
     * if the field is not set. This value can be used to calculate the path loss of a received
     * packet using the following equation:
     * <p/>
     * <code>pathloss = txPowerLevel - rssi</code>
     */
    public int getTxPowerLevel() {
        return mTxPowerLevel;
    }

    /**
     * Returns the local name of the BLE device. The is a UTF-8 encoded string.
     */
    @Nullable
    public String getDeviceName() {
        return mDeviceName;
    }

    /**
     * Returns raw bytes of scan record.
     */
    public byte[] getBytes() {
        return mBytes;
    }

    protected ScanBLERecord(List<ParcelUuid> serviceUuids,
                            SparseArray<byte[]> manufacturerData,
                            Map<ParcelUuid, byte[]> serviceData,
                            int advertiseFlags, int txPowerLevel,
                            String localName, byte[] bytes) {
        mServiceUuids = serviceUuids;
        mManufacturerSpecificData = manufacturerData;
        mServiceData = serviceData;
        mDeviceName = localName;
        mAdvertiseFlags = advertiseFlags;
        mTxPowerLevel = txPowerLevel;
        mBytes = bytes;
    }

    /**
     * Parse scan record bytes to {@link ScanBLERecord}.
     * <p/>
     * The format is defined in Bluetooth 4.1 specification, Volume 3, Part C, Section 11 and 18.
     * <p/>
     * All numerical multi-byte entities and values shall use little-endian <strong>byte</strong>
     * order.
     *
     * @param ScanBLERecord The scan record of Bluetooth LE advertisement and/or scan response.
     * @hide
     */
    public static ScanBLERecord parseFromBytes(byte[] ScanBLERecord) {
        if (ScanBLERecord == null) {
            return null;
        }

        int currentPos = 0;
        int advertiseFlag = -1;
        List<ParcelUuid> serviceUuids = new ArrayList<ParcelUuid>();
        String localName = null;
        int txPowerLevel = Integer.MIN_VALUE;

        SparseArray<byte[]> manufacturerData = new SparseArray<byte[]>();
        Map<ParcelUuid, byte[]> serviceData = new ArrayMap<ParcelUuid, byte[]>();

        try {
            while (currentPos < ScanBLERecord.length) {
                // length is unsigned int.
                int length = ScanBLERecord[currentPos++] & 0xFF;
                if (length == 0) {
                    break;
                }
                // Note the length includes the length of the field type itself.
                int dataLength = length - 1;
                // fieldType is unsigned int.
                int fieldType = ScanBLERecord[currentPos++] & 0xFF;
                switch (fieldType) {
                    case DATA_TYPE_FLAGS:
                        advertiseFlag = ScanBLERecord[currentPos] & 0xFF;
                        break;
                    case DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL:
                    case DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE:
                        parseServiceUuid(ScanBLERecord, currentPos,
                                dataLength, BluetoothUuid.UUID_BYTES_16_BIT, serviceUuids);
                        break;
                    case DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL:
                    case DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE:
                        parseServiceUuid(ScanBLERecord, currentPos, dataLength,
                                BluetoothUuid.UUID_BYTES_32_BIT, serviceUuids);
                        break;
                    case DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL:
                    case DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE:
                        parseServiceUuid(ScanBLERecord, currentPos, dataLength,
                                BluetoothUuid.UUID_BYTES_128_BIT, serviceUuids);
                        break;
                    case DATA_TYPE_LOCAL_NAME_SHORT:
                    case DATA_TYPE_LOCAL_NAME_COMPLETE:
                        localName = new String(
                                extractBytes(ScanBLERecord, currentPos, dataLength));
                        break;
                    case DATA_TYPE_TX_POWER_LEVEL:
                        txPowerLevel = ScanBLERecord[currentPos];
                        break;
                    case DATA_TYPE_SERVICE_DATA:
                        // The first two bytes of the service data are service data UUID in little
                        // endian. The rest bytes are service data.
                        int serviceUuidLength = BluetoothUuid.UUID_BYTES_16_BIT;
                        byte[] serviceDataUuidBytes = extractBytes(ScanBLERecord, currentPos,
                                serviceUuidLength);
                        ParcelUuid serviceDataUuid = BluetoothUuid.parseUuidFrom(
                                serviceDataUuidBytes);
                        byte[] serviceDataArray = extractBytes(ScanBLERecord,
                                currentPos + serviceUuidLength, dataLength - serviceUuidLength);
                        serviceData.put(serviceDataUuid, serviceDataArray);
                        break;
                    case DATA_TYPE_MANUFACTURER_SPECIFIC_DATA:
                        // The first two bytes of the manufacturer specific data are
                        // manufacturer ids in little endian.
                        int manufacturerId = ((ScanBLERecord[currentPos + 1] & 0xFF) << 8) +
                                (ScanBLERecord[currentPos] & 0xFF);
                        byte[] manufacturerDataBytes = extractBytes(ScanBLERecord, currentPos + 2,
                                dataLength - 2);
                        manufacturerData.put(manufacturerId, manufacturerDataBytes);
                        break;
                    default:
                        // Just ignore, we don't handle such data type.
                        break;
                }
                currentPos += dataLength;
            }

            if (serviceUuids.isEmpty()) {
                serviceUuids = null;
            }
            return new ScanBLERecord(serviceUuids, manufacturerData, serviceData,
                    advertiseFlag, txPowerLevel, localName, ScanBLERecord);
        } catch (Exception e) {
            Logger.error(TAG, "unable to parse scan record: " + Arrays.toString(ScanBLERecord));
            // As the record is invalid, ignore all the parsed results for this packet
            // and return an empty record with raw ScanBLERecord bytes in results
            return new ScanBLERecord(null, null, null, -1, Integer.MIN_VALUE, null, ScanBLERecord);
        }
    }

    @Override
    public String toString() {
        return "ScanBLERecord [mAdvertiseFlags=" + mAdvertiseFlags + ", mServiceUuids=" + mServiceUuids
                + ", mManufacturerSpecificData=" + BluetoothLeUtils.toString(mManufacturerSpecificData)
                + ", mServiceData=" + BluetoothLeUtils.toString(mServiceData)
                + ", mTxPowerLevel=" + mTxPowerLevel + ", mDeviceName=" + mDeviceName + "]";
    }

    // Parse service UUIDs.
    private static int parseServiceUuid(byte[] ScanBLERecord, int currentPos, int dataLength,
                                        int uuidLength, List<ParcelUuid> serviceUuids) {
        while (dataLength > 0) {
            byte[] uuidBytes = extractBytes(ScanBLERecord, currentPos,
                    uuidLength);
            serviceUuids.add(BluetoothUuid.parseUuidFrom(uuidBytes));
            dataLength -= uuidLength;
            currentPos += uuidLength;
        }
        return currentPos;
    }

    // Helper method to extract bytes from byte array.
    private static byte[] extractBytes(byte[] ScanBLERecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(ScanBLERecord, start, bytes, 0, length);
        return bytes;
    }
}
