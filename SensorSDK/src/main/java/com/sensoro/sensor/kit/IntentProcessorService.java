package com.sensoro.sensor.kit;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by Sensoro on 12/18/14.
 */
public class IntentProcessorService extends IntentService {
    private MonitoredSensoroDevice monitoredSensoroDevice;
    private ArrayList<SensoroDevice> updateDevices;

    public IntentProcessorService() {
        super("IntentProcessor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            monitoredSensoroDevice = (MonitoredSensoroDevice) intent.getExtras().get(SensoroDeviceManager.MONITORED_DEVICE);
            updateDevices = (ArrayList<SensoroDevice>) intent.getExtras().get(SensoroDeviceManager.UPDATE_DEVICES);
        }

        if (monitoredSensoroDevice != null) {
            SensoroDeviceListener deviceManagerListener = SensoroDeviceManager.getInstance(getApplication()).getSensoroDeviceListener();
            if (deviceManagerListener != null) {
                if (monitoredSensoroDevice.inSide) {
                    deviceManagerListener.onNewDevice(monitoredSensoroDevice.sensoroDevice);
                } else {
                    deviceManagerListener.onGoneDevice(monitoredSensoroDevice.sensoroDevice);
                }
            }
        }
        if (updateDevices != null) {
            SensoroDeviceListener beaconManagerListener = SensoroDeviceManager.getInstance(getApplication()).getSensoroDeviceListener();
            if (beaconManagerListener != null) {
                beaconManagerListener.onUpdateDevices(updateDevices);
            }
        }
    }
}
