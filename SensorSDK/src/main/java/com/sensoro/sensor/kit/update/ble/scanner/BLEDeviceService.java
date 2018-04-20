package com.sensoro.sensor.kit.update.ble.scanner;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sensoro.sensor.kit.update.ble.BLEDevice;
import com.sensoro.sensor.kit.update.ble.BLEDeviceFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.sensoro.sensor.kit.update.ble.BLEDevice;
//import com.sensoro.sensor.kit.update.ble.BLEDeviceFactory;

/**
 * Created by fangping on 2016/7/13.
 */

public class BLEDeviceService extends Service implements BLEScanCallback {

    private ConcurrentHashMap<String, BLEDevice> scanDeviceHashMap = new ConcurrentHashMap<>();
    private ArrayList<BLEDevice> updateDevices = new ArrayList<BLEDevice>();
    private BLEScanner bleScanner;
    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        bleScanner = BLEScanner.createScanner(this, this);
        executorService = Executors.newCachedThreadPool();
        List<ScanBLEFilter> scanBLEResults = new ArrayList<>();
        ScanBLEFilter scanBLEFilter = new ScanBLEFilter.Builder()
                .build();
        scanBLEResults.add(scanBLEFilter);
        bleScanner.setScanBLEFilters(scanBLEResults);
        bleScanner.setScanPeriod(BLEDeviceManager.FOREGROUND_SCAN_PERIOD);
        bleScanner.setBetweenScanPeriod(BLEDeviceManager.FOREGROUND_BETWEEN_SCAN_PERIOD);
        bleScanner.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        bleScanner.stop();
        executorService.shutdown();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BLEDeviceServiceV4Binder();
    }

    public class BLEDeviceServiceV4Binder extends Binder {
        public BLEDeviceService getService() {
            return BLEDeviceService.this;
        }
    }

    public void setBackgroundMode(boolean isBackgroundMode) {
        if (bleScanner != null) {
            if (isBackgroundMode) {
                bleScanner.setScanPeriod(BLEDeviceManager.BACKGROUND_SCAN_PERIOD);
                bleScanner.setBetweenScanPeriod(BLEDeviceManager.BACKGROUND_BETWEEN_SCAN_PERIOD);
            } else {
                bleScanner.setScanPeriod(BLEDeviceManager.FOREGROUND_SCAN_PERIOD);
                bleScanner.setBetweenScanPeriod(BLEDeviceManager.FOREGROUND_BETWEEN_SCAN_PERIOD);
                bleScanner.stop();
                bleScanner.start();
            }
        }
    }

    private void processScanDevice(BLEDevice device) {
        BLEDevice containedDevice = scanDeviceHashMap.get(device.getMacAddress());
        if (containedDevice == null) {
            scanDeviceHashMap.put(device.getMacAddress(), device);
            enterDevice(device);
        } else {
            updateDeviceInfo(device, containedDevice);
        }
    }

    private void enterDevice(BLEDevice device) {
        try {
            BLEDevice newDevice = device.clone();
            Intent intent = new Intent();
            intent.setClass(BLEDeviceService.this, IntentProcessorService.class);
            intent.putExtra(BLEDeviceManager.MONITORED_DEVICE, new MonitoredBLEDevice(newDevice, true));
            startService(intent);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

    private void updateDeviceInfo(BLEDevice device, BLEDevice containedDevice) {
        try {
            containedDevice.setSerialNumber(device.getSerialNumber());
            containedDevice.setHardwareVersion(device.getHardwareVersion());
            containedDevice.setFirmwareVersion(device.getFirmwareVersion());
            containedDevice.lastFoundTime = device.lastFoundTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateDevices() {
        try {
            ArrayList<BLEDevice> updateDevicesClone = (ArrayList<BLEDevice>) updateDevices.clone();
            Intent intent = new Intent();
            intent.setClass(BLEDeviceService.this, IntentProcessorService.class);
            intent.putParcelableArrayListExtra(BLEDeviceManager.UPDATE_DEVICES, updateDevicesClone);
            startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void exitDevice() {
        // 清空updateDevices
        try {
            updateDevices.clear();
            Iterator deviceIterator = scanDeviceHashMap.entrySet().iterator();
            while (deviceIterator.hasNext()) {
                Map.Entry entry = (Map.Entry) deviceIterator.next();
                BLEDevice monitoredDevice = (BLEDevice) entry.getValue();
                if (System.currentTimeMillis() - monitoredDevice.lastFoundTime > BLEDeviceManager.OUT_OF_RANGE_DELAY) {
                    final BLEDevice goneDevice = monitoredDevice.clone();
                    Intent intent = new Intent();
                    intent.setClass(BLEDeviceService.this, IntentProcessorService.class);
                    intent.putExtra(BLEDeviceManager.MONITORED_DEVICE, new MonitoredBLEDevice(goneDevice, false));
                    startService(intent);

                    scanDeviceHashMap.remove(monitoredDevice.getMacAddress());
                } else {
                    updateDevices.add(monitoredDevice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processScanCycleDevices() {
        exitDevice();
        updateDevices();
    }

    @Override
    public void onLeScan(final ScanBLEResult scanBLEResult) {
        try {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    BLEDeviceFactory deviceFactory = new BLEDeviceFactory(scanBLEResult);
                    BLEDevice bleDevice = deviceFactory.create();
                    if (bleDevice != null) {//&& bleDevice.getSerialNumber().equals("10310117C5A3FD2D")
                        processScanDevice(bleDevice);
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onScanCycleFinish() {
        processScanCycleDevices();
    }

}
