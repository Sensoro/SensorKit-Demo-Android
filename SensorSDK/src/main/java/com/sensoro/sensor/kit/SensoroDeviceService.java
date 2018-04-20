package com.sensoro.sensor.kit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sensoro.sensor.kit.ble.BLEScanCallback;
import com.sensoro.sensor.kit.ble.BLEScanner;
import com.sensoro.sensor.kit.ble.ScanBLEFilter;
import com.sensoro.sensor.kit.ble.ScanBLEResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fangping on 2016/7/13.
 */

public class SensoroDeviceService extends Service implements BLEScanCallback {

    private ConcurrentHashMap<String, SensoroDevice> scanDeviceHashMap = new ConcurrentHashMap<>();
    private ArrayList<SensoroDevice> updateDevices = new ArrayList<SensoroDevice>();
    private BLEScanner bleScanner;

    @Override
    public void onCreate() {
        super.onCreate();
        bleScanner = BLEScanner.createScanner(this, this);
        List<ScanBLEFilter> scanBLEResults = new ArrayList<>();
        ScanBLEFilter scanBLEFilter = new ScanBLEFilter.Builder()
                .build();

        scanBLEResults.add(scanBLEFilter);
        bleScanner.setScanBLEFilters(scanBLEResults);
        bleScanner.setScanPeriod(SensoroDeviceManager.FOREGROUND_SCAN_PERIOD);
        bleScanner.setBetweenScanPeriod(SensoroDeviceManager.FOREGROUND_BETWEEN_SCAN_PERIOD);
        bleScanner.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        bleScanner.stop();
        SensoroDeviceFactory.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SensoroDeviceServiceV4Binder();
    }

    public class SensoroDeviceServiceV4Binder extends Binder {
        public SensoroDeviceService getService() {
            return SensoroDeviceService.this;
        }
    }

    public void setBackgroundMode(boolean isBackgroundMode) {
        if (bleScanner != null) {
            if (isBackgroundMode) {
                bleScanner.setScanPeriod(SensoroDeviceManager.BACKGROUND_SCAN_PERIOD);
                bleScanner.setBetweenScanPeriod(SensoroDeviceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);
            } else {
                bleScanner.setScanPeriod(SensoroDeviceManager.FOREGROUND_SCAN_PERIOD);
                bleScanner.setBetweenScanPeriod(SensoroDeviceManager.FOREGROUND_BETWEEN_SCAN_PERIOD);
                bleScanner.stop();
                bleScanner.start();
            }
        }
    }

    private void processScanDevice(SensoroDevice device) {
        SensoroDevice containedDevice = scanDeviceHashMap.get(device.macAddress);
        if (containedDevice == null) {
            scanDeviceHashMap.put(device.macAddress, device);
            if (device.getSerialNumber() == null) {
                Log.e("", "processScanDevice: null");
            }
            enterDevice(device);
        } else {
            updateDeviceInfo(device, containedDevice);
        }
    }

    private void enterDevice(SensoroDevice device) {
        // new device
        SensoroDevice newDevice = null;
        try {
            newDevice = device.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            newDevice = device;
        }
        Intent intent = new Intent();
        intent.setClass(SensoroDeviceService.this, IntentProcessorService.class);
        intent.putExtra(SensoroDeviceManager.MONITORED_DEVICE, new MonitoredSensoroDevice(newDevice, true));
        startService(intent);
    }

    private void updateDeviceInfo(SensoroDevice device, SensoroDevice containedDevice) {
        containedDevice.sn = device.sn;
        containedDevice.rssi = device.rssi;
        containedDevice.hardwareVersion = device.hardwareVersion;
        containedDevice.firmwareVersion = device.firmwareVersion;
        containedDevice.batteryLevel = device.batteryLevel;
        containedDevice.temperature = device.temperature;
        containedDevice.light = device.light;
        containedDevice.humidity = device.humidity;
        containedDevice.accelerometerCount = device.accelerometerCount;
        containedDevice.lastFoundTime = device.lastFoundTime;
        containedDevice.customize = device.customize;
        containedDevice.drip = device.drip;
        containedDevice.co = device.co;
        containedDevice.co2 = device.co2;
        containedDevice.no2 = device.no2;
        containedDevice.methane = device.methane;
        containedDevice.lpg = device.lpg;
        containedDevice.pm1 = device.pm1;
        containedDevice.pm25 = device.pm25;
        containedDevice.pm10 = device.pm10;
        containedDevice.coverstatus = device.coverstatus;
        containedDevice.level = device.level;
    }

    private void updateDevices() {
        ArrayList<SensoroDevice> updateDevicesClone = (ArrayList<SensoroDevice>) updateDevices.clone();
        Intent intent = new Intent();
        intent.setClass(SensoroDeviceService.this, IntentProcessorService.class);
        intent.putParcelableArrayListExtra(SensoroDeviceManager.UPDATE_DEVICES, updateDevicesClone);
        startService(intent);
    }

    private void exitDevice() {
        // 清空updateDevices
        updateDevices.clear();
        Iterator deviceIterator = scanDeviceHashMap.entrySet().iterator();
        while (deviceIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) deviceIterator.next();
            SensoroDevice monitoredDevice = (SensoroDevice) entry.getValue();

            if (System.currentTimeMillis() - monitoredDevice.lastFoundTime > SensoroDeviceManager.OUT_OF_RANGE_DELAY) {
                SensoroDevice goneDevice = null;
                try {
                    goneDevice = monitoredDevice.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    goneDevice = monitoredDevice;
                }
                Intent intent = new Intent();
                intent.setClass(SensoroDeviceService.this, IntentProcessorService.class);
                intent.putExtra(SensoroDeviceManager.MONITORED_DEVICE, new MonitoredSensoroDevice(goneDevice, false));
                startService(intent);
                scanDeviceHashMap.remove(monitoredDevice.getMacAddress());
            } else {
                updateDevices.add(monitoredDevice);
            }
        }
    }

    private void processScanCycleDevices() {
        exitDevice();
        updateDevices();
    }

    @Override
    public void onLeScan(ScanBLEResult scanBLEResult) {
        SensoroDeviceFactory sensoroDeviceFactory = new SensoroDeviceFactory(scanBLEResult);
        SensoroDevice sensoroDevice = sensoroDeviceFactory.createDevice();
        if (sensoroDevice != null) {
            processScanDevice(sensoroDevice);
        }

    }


    @Override
    public void onScanCycleFinish() {
        processScanCycleDevices();
    }
}
