package com.sensoro.sensor.kit.update.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by Sensoro on 15/6/2.
 * used for Android 4.3 and below.
 */
class BLEScannerForJellyBean extends BLEScanner {
    private static final String TAG = BLEScannerForJellyBean.class.getSimpleName();
    private List<ScanBLEFilter> scanBLEFilters;

    protected BLEScannerForJellyBean(Context context, BLEScanCallback bleScanCallback) {
        super(context, bleScanCallback);
    }

    @Override
    protected void setScanFilters(List<ScanBLEFilter> scanBLEFilters) {
        if (scanBLEFilters != null && scanBLEFilters.size() != 0) {
            this.scanBLEFilters = scanBLEFilters;
        }
    }

    @Override
    protected void startScan() {
        Log.d(TAG, "start scan in Jelly Bean");
        getBluetoothAdapter().startLeScan(leScanCallback);
    }

    @Override
    protected void stopScan() {
        Log.d(TAG, "stop scan in Jelly Bean");
        getBluetoothAdapter().stopLeScan(leScanCallback);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            ScanBLERecord scanBLERecord = ScanBLERecord.parseFromBytes(scanRecord);
            bluetoothCrashResolver.notifyScannedDevice(device, leScanCallback);
            if (scanBLEFilters != null && scanBLEFilters.size() != 0) {
                for (ScanBLEFilter scanBLEFilter : scanBLEFilters) {
                    boolean isValidResult;
                    isValidResult = filterScanResult(device, scanBLERecord, scanBLEFilter);
                    if (isValidResult) {
                        ScanBLEResult scanBLEResult = new ScanBLEResult(device, scanBLERecord, rssi, 0);
                        bleScanCallback.onLeScan(scanBLEResult);
                    }
                }
            } else {
                ScanBLEResult scanBLEResult = new ScanBLEResult(device, scanBLERecord, rssi, 0);
                bleScanCallback.onLeScan(scanBLEResult);
            }
        }
    };

    private boolean filterScanResult(BluetoothDevice device, ScanBLERecord scanBLERecord, ScanBLEFilter scanBLEFilter) {
        // Device match.
        if (scanBLEFilter.getDeviceAddress() != null
                && (device == null || !scanBLEFilter.getDeviceAddress().equals(device.getAddress()))) {
            return false;
        }
        // Scan record is null but there exist filters on it.
        if (scanBLERecord == null
                && (scanBLEFilter.getDeviceName() != null
                    || scanBLEFilter.getServiceUuid() != null
                    || scanBLEFilter.getManufacturerData() != null
                    || scanBLEFilter.getServiceData() != null)) {
            return false;
        }

        // Local name match.
        if (scanBLEFilter.getDeviceName() != null && !scanBLEFilter.getDeviceName().equals(scanBLERecord.getDeviceName())) {
            return false;
        }

        // UUID match.
        if (scanBLEFilter.getServiceUuid() != null
                && !matchesServiceUuids(scanBLEFilter.getServiceUuid(), scanBLEFilter.getServiceUuidMask(),
                    scanBLERecord.getServiceUuids())) {
            return false;
        }

        // Service data match
        if (scanBLEFilter.getServiceDataUuid() != null) {
            if (!matchesPartialData(scanBLEFilter.getServiceData(), scanBLEFilter.getServiceDataMask(),
                    scanBLERecord.getServiceData(scanBLEFilter.getServiceDataUuid()))) {
                return false;
            }
        }

        // Manufacturer data match.
        if (scanBLEFilter.getManufacturerId() >= 0) {
            if (!matchesPartialData(scanBLEFilter.getManufacturerData(), scanBLEFilter.getManufacturerDataMask(),
                    scanBLERecord.getManufacturerSpecificData(scanBLEFilter.getManufacturerId()))) {
                return false;
            }
        }
        // All filters match.
        return true;
    }

    // Check if the uuid pattern is contained in a list of parcel uuids.
    private boolean matchesServiceUuids(ParcelUuid uuid, ParcelUuid parcelUuidMask,
                                        List<ParcelUuid> uuids) {
        if (uuid == null) {
            return true;
        }
        if (uuids == null) {
            return false;
        }

        for (ParcelUuid parcelUuid : uuids) {
            UUID uuidMask = parcelUuidMask == null ? null : parcelUuidMask.getUuid();
            if (matchesServiceUuid(uuid.getUuid(), uuidMask, parcelUuid.getUuid())) {
                return true;
            }
        }
        return false;
    }

    // Check if the uuid pattern matches the particular service uuid.
    private boolean matchesServiceUuid(UUID uuid, UUID mask, UUID data) {
        if (mask == null) {
            return uuid.equals(data);
        }
        if ((uuid.getLeastSignificantBits() & mask.getLeastSignificantBits()) !=
                (data.getLeastSignificantBits() & mask.getLeastSignificantBits())) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & mask.getMostSignificantBits()) ==
                (data.getMostSignificantBits() & mask.getMostSignificantBits()));
    }

    // Check whether the data pattern matches the parsed data.
    private boolean matchesPartialData(byte[] data, byte[] dataMask, byte[] parsedData) {
        if (parsedData == null || parsedData.length < data.length) {
            return false;
        }
        if (dataMask == null) {
            for (int i = 0; i < data.length; ++i) {
                if (parsedData[i] != data[i]) {
                    return false;
                }
            }
            return true;
        }
        for (int i = 0; i < data.length; ++i) {
            if ((dataMask[i] & parsedData[i]) != (dataMask[i] & data[i])) {
                return false;
            }
        }
        return true;
    }
}
