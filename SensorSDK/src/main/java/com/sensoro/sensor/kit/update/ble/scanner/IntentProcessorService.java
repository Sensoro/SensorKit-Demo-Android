package com.sensoro.sensor.kit.update.ble.scanner;

import android.app.IntentService;
import android.content.Intent;

import com.sensoro.sensor.kit.update.ble.BLEDevice;

import java.util.ArrayList;

/**
 * Created by Sensoro on 12/18/14.
 */
public class IntentProcessorService extends IntentService {
    private MonitoredBLEDevice monitoredBLEDevice;
    private ArrayList<BLEDevice> updateDevices;

    public IntentProcessorService() {
        super("IntentProcessor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null && intent.getExtras() != null) {
                monitoredBLEDevice = (MonitoredBLEDevice) intent.getExtras().get(BLEDeviceManager.MONITORED_DEVICE);
                updateDevices = (ArrayList<BLEDevice>) intent.getExtras().get(BLEDeviceManager.UPDATE_DEVICES);
            }

            if (monitoredBLEDevice != null) {
                BLEDeviceListener deviceManagerListener = BLEDeviceManager.getInstance(getApplication()).getBLEDeviceListener();
                if (deviceManagerListener != null) {
                    if (monitoredBLEDevice.inSide) {
                        deviceManagerListener.onNewDevice(monitoredBLEDevice.bleDevice);
                    } else {
                        deviceManagerListener.onGoneDevice(monitoredBLEDevice.bleDevice);
                    }
                }
            }
            if (updateDevices != null) {
                BLEDeviceListener beaconManagerListener = BLEDeviceManager.getInstance(getApplication()).getBLEDeviceListener();
                if (beaconManagerListener != null) {
                    beaconManagerListener.onUpdateDevices(updateDevices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
