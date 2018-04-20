package com.sensoro.sensor.kit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangping on 2016/7/11.
 */

public interface SensoroDeviceListener<T> {
    void onNewDevice(final T sensoroDevice);
    void onGoneDevice(final T sensoroDevice);
    void onUpdateDevices(final ArrayList<T> deviceList);
}
