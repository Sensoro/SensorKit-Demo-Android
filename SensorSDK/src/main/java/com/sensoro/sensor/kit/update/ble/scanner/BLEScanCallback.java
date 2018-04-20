package com.sensoro.sensor.kit.update.ble.scanner;

/**
 * Created by Sensoro on 15/6/2.
 */
public interface BLEScanCallback {
    public void onLeScan(ScanBLEResult scanBLEResult);

    public void onScanCycleFinish();
}
