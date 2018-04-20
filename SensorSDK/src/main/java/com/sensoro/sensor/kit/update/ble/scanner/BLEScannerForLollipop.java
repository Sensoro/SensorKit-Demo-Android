package com.sensoro.sensor.kit.update.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sensoro on 15/6/2.
 * used for Android 5.0 and above.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class BLEScannerForLollipop extends BLEScanner {
    private static final String TAG = BLEScannerForLollipop.class.getSimpleName();

    private BluetoothLeScanner bluetoothLeScanner;
    private List<ScanBLEFilter> scanBLEFilters;
    private ScanSettings scanSettings;

    protected BLEScannerForLollipop(Context context, BLEScanCallback bleScanCallback) {
        super(context, bleScanCallback);

        scanSettings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)).build();
    }

    @Override
    protected void setScanFilters(List<ScanBLEFilter> scanBLEFilters) {
        if (scanBLEFilters != null && scanBLEFilters.size() != 0) {
            this.scanBLEFilters = scanBLEFilters;
        }
    }

    @Override
    protected void startScan() {
        Log.d(TAG, "@=>start scan in Lollipop");
        try {
            if (getScanner() != null) {
                getScanner().startScan(getScanFilters(scanBLEFilters), scanSettings, leScanCallback);
            }
        } catch (IllegalStateException e) {
            Log.d(TAG, "Cannot start scan.  Bluetooth may be turned off.");
        }
    }

    @Override
    protected void stopScan() {
        Log.d(TAG, "@=>stop scan in Lollipop");
        if (getBluetoothAdapter() == null || getBluetoothAdapter().getState() != BluetoothAdapter.STATE_ON) {
            Log.d(TAG, "BT Adapter is not turned ON");
        } else {
            getScanner().stopScan(leScanCallback);
        }
    }

    private List<ScanFilter> getScanFilters(List<ScanBLEFilter> scanBLEFilters) {
        List<ScanFilter> scanFilters = new ArrayList<>();
        if (scanBLEFilters != null && scanBLEFilters.size() > 0) {
            for (ScanBLEFilter scanBLEFilter : scanBLEFilters) {
                ScanFilter scanFilter = null;
                ScanFilter.Builder builder = new ScanFilter.Builder()
                        .setDeviceName(scanBLEFilter.getDeviceName())
                        .setDeviceAddress(scanBLEFilter.getDeviceAddress())
                        .setServiceUuid(scanBLEFilter.getServiceUuid(), scanBLEFilter.getServiceUuidMask())
                        .setManufacturerData(scanBLEFilter.getManufacturerId(), scanBLEFilter.getManufacturerData(), scanBLEFilter.getManufacturerDataMask());
                if (scanBLEFilter.getServiceDataUuid() != null) {
                    scanFilter = builder.setServiceData(scanBLEFilter.getServiceDataUuid(), scanBLEFilter.getServiceData(), scanBLEFilter.getServiceDataMask()).build();
                } else {
                    scanFilter = builder.build();
                }

                scanFilters.add(scanFilter);
            }
        }
        return scanFilters;
    }

    private BluetoothLeScanner getScanner() {
        if (bluetoothLeScanner == null) {
            Log.d(TAG, "Making new Android L scanner");
            bluetoothLeScanner = getBluetoothAdapter().getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                Log.d(TAG, "Failed to make new Android L scanner");
            }
        }
        return bluetoothLeScanner;
    }

    private ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            ScanRecord scanRecord = scanResult.getScanRecord();
            ScanBLERecord scanBLERecord = new ScanBLERecord(scanRecord.getServiceUuids(), scanRecord.getManufacturerSpecificData(), scanRecord.getServiceData(),
                    scanRecord.getAdvertiseFlags(), scanRecord.getTxPowerLevel(), scanRecord.getDeviceName(), scanRecord.getBytes());
            ScanBLEResult scanBLEResult = new ScanBLEResult(scanResult.getDevice(), scanBLERecord, scanResult.getRssi(), scanResult.getTimestampNanos());
            bleScanCallback.onLeScan(scanBLEResult);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
//            Log.d(TAG, "onBatchScanResults");
            for (ScanResult scanResult : results) {
                ScanRecord scanRecord = scanResult.getScanRecord();
                ScanBLERecord scanBLERecord = new ScanBLERecord(scanRecord.getServiceUuids(), scanRecord.getManufacturerSpecificData(), scanRecord.getServiceData(),
                        scanRecord.getAdvertiseFlags(), scanRecord.getTxPowerLevel(), scanRecord.getDeviceName(), scanRecord.getBytes());
                ScanBLEResult scanBLEResult = new ScanBLEResult(scanResult.getDevice(), scanBLERecord, scanResult.getRssi(), scanResult.getTimestampNanos());
                bleScanCallback.onLeScan(scanBLEResult);
            }
        }

        @Override
        public void onScanFailed(int i) {
            Log.d(TAG, "onScanFailed=>" + i);
        }
    };
}
