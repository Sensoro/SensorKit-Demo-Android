package com.sensoro.sensor.kit.update.ble.scanner;

import android.os.ParcelUuid;

import com.sensoro.sensor.kit.Utils;

import java.util.UUID;

/**
 * Created by Sensoro on 15/9/15.
 */
public class BLEFilter {
    private static final byte[] iBeaconHeader = new byte[]{0x02, 0x15};
    private static final String SERVICE_UUID = "0000XXXX-0000-1000-8000-00805F9B34FB";
    public static final String SENSOR_SERVICE_UUID_E3412 = "3412";
    public static final String DEVICE_SERVICE_DATA_UUID = "6CCE";
    public static final String STATION_SERVICE_DATA_UUID = "A418";

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
        majorBytes = Utils.Integer2Bytes(major);
        System.arraycopy(majorBytes, 0, result, 18, 2);

        if (minor == null) {
            return result;
        }
        minorBytes = Utils.Integer2Bytes(minor);
        System.arraycopy(minorBytes, 0, result, 20, 2);
        return result;
    }

    public static ParcelUuid createServiceDataUUID(String serviceDataUUID) {
        return new ParcelUuid(UUID.fromString(SERVICE_UUID.replace("XXXX", serviceDataUUID)));
    }
}