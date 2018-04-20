package com.sensoro.sensor.kit.ble;

import android.os.ParcelUuid;

import com.sensoro.sensor.kit.Utils;

import java.util.UUID;

/**
 * Created by Sensoro on 15/9/15.
 */
public class BLEFilter {
    private static final byte[] iBeaconHeader = new byte[]{0x02, 0x15};
    private static final String SERVICE_UUID = "0000XXXX-0000-1000-8000-00805F9B34FB";

    public static final int MANUFACTURER_ID_APPLE = 0x004C;
    public static final String SERVICE_UUID_EDDYSTONE = "FEAA";
    public static final String SERVICE_UUID_E3412 = "3412";
    public static final String SERVICE_UUID_E780 = "80E7";
    public static final String SERVICE_UUID_E781 = "81E7";
    public static final byte[] MANUFACTURER_DATA_MASK_IBEACON_U = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00};
    public static final byte[] MANUFACTURER_DATA_MASK_IBEACON_UM = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00};
    public static final byte[] MANUFACTURER_DATA_MASK_IBEACON_UMM = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00};

    public static byte[] umm2ScanFilter(String uuid, Integer major, Integer minor) {
        byte[] result = new byte[23];
        byte[] uuidBytes = null;
        byte[] majorBytes = null;
        byte[] minorBytes = null;

        // UUID
        if (uuid == null) {
            return result;
        }
        uuidBytes = Utils.HexString2Bytes(uuid.replace("-", ""));
        System.arraycopy(iBeaconHeader, 0, result, 0, 2);
        System.arraycopy(uuidBytes, 0, result, 2, 16);

        // Major
        if (major == null) {
            return result;
        }
        majorBytes = Utils.Integer22Bytes(major);
        System.arraycopy(majorBytes, 0, result, 18, 2);

        if (minor == null) {
            return result;
        }
        minorBytes = Utils.Integer22Bytes(minor);
        System.arraycopy(minorBytes, 0, result, 20, 2);
        return result;
    }

    public static ParcelUuid createServiceDataUUID(String serviceDataUUID) {
        return new ParcelUuid(UUID.fromString(SERVICE_UUID.replace("XXXX", serviceDataUUID)));
    }
}