package com.sensoro.sensor.kit.ble;

/**
 * Created by Sensoro on 15/6/2.
 */
public interface BLEScanCallback {
    public void onLeScan(ScanBLEResult scanBLEResult);

    public void onScanCycleFinish();
}
