package com.sensoro.sensor.kit.update.ble.scanner;

import java.util.ArrayList;

/**
 * Created by fangping on 2016/7/11.
 */

public interface BLEDeviceListener<T> {
    void onNewDevice(final T bleDevice);
    void onGoneDevice(final T bleDevice);
    void onUpdateDevices(final ArrayList<T> deviceList);
}
