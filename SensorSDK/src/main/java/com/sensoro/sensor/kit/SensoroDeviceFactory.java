package com.sensoro.sensor.kit;

import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;

import com.sensoro.sensor.kit.ble.ScanBLEResult;
import com.sensoro.sensor.kit.update.ble.BLEDevice;
import com.sensoro.sensor.kit.update.ble.scanner.BLEFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangping on 2016/7/13.
 */

public class SensoroDeviceFactory {


    protected static HashMap<String, String> snMap = new HashMap<>();
    protected static HashMap<String, String> hardwareVersionMap = new HashMap<>();
    protected static HashMap<String, String> firmwareVersionMap = new HashMap<>();
    protected static HashMap<String, Integer> accelerometerCountMap = new HashMap<>();
    protected static HashMap<String, byte[]> customizeMap = new HashMap<>();
    protected static HashMap<String, Float> temperatureMap = new HashMap<>();
    protected static HashMap<String, Float> humidityMap = new HashMap<>();
    protected static HashMap<String, Float> lightMap = new HashMap<>();
    protected static HashMap<String, Integer> batteryMap = new HashMap<>();
    protected static HashMap<String, Integer> dripMap = new HashMap<>();
    protected static HashMap<String, Float> coMap = new HashMap<>();
    protected static HashMap<String, Float> co2Map = new HashMap<>();
    protected static HashMap<String, Float> no2Map = new HashMap<>();
    protected static HashMap<String, Float> methaneMap = new HashMap<>();
    protected static HashMap<String, Float> lpgMap = new HashMap<>();
    protected static HashMap<String, Float> pm1Map = new HashMap<>();
    protected static HashMap<String, Float> pm25Map = new HashMap<>();
    protected static HashMap<String, Float> pm10Map = new HashMap<>();
    protected static HashMap<String, Float> coverstatusMap = new HashMap<>();
    protected static HashMap<String, Float> levelMap = new HashMap<>();

    protected static HashMap<String, Float> pitchMap = new HashMap<>();
    protected static HashMap<String, Float> rollMap = new HashMap<>();
    protected static HashMap<String, Float> yawMap = new HashMap<>();
    protected static HashMap<String, Integer> flameMap = new HashMap<>();
    protected static HashMap<String, Float> gasMap = new HashMap<>();
    protected static HashMap<String, Integer> smokeMap = new HashMap<>();
    protected static HashMap<String, Float> waterPressureMap = new HashMap<>();

    private static final String TAG = "SensoroDeviceFactory";
    private ScanBLEResult scanBLEResult;

    public SensoroDeviceFactory(ScanBLEResult scanBLEResult) {
        this.scanBLEResult = scanBLEResult;
    }

    public SensoroDevice createDevice() {
        if (scanBLEResult == null) {
            return null;
        }
        Map<ParcelUuid, byte[]> serviceData = scanBLEResult.getScanRecord().getServiceData();
        if (serviceData == null) {
            return null;
        }
        ParcelUuid deviceParcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.DEVICE_SERVICE_DATA_UUID);
        ParcelUuid sensorParcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.SENSOR_SERVICE_UUID_E3412);
        byte sensor_data[] = scanBLEResult.getScanRecord().getServiceData(sensorParcelUuid);
        byte device_data[] = scanBLEResult.getScanRecord().getServiceData(deviceParcelUuid);


        if (sensor_data != null) {
            SensoroDevice sensoroSensor = new SensoroDevice();
            E3214 e3214 = E3214.parseE3214(sensor_data);
            if (e3214 != null) {
                sensoroSensor.setHardwareVersion(e3214.hardwareVersion);
                sensoroSensor.setFirmwareVersion(e3214.firmwareVersion);
                sensoroSensor.setBatteryLevel(e3214.batteryLevel == null ? 0 : e3214.batteryLevel);
                sensoroSensor.setAccelerometerCount(e3214.accelerometerCount);
                String address = scanBLEResult.getDevice().getAddress();
                sensoroSensor.setMacAddress(address);
                sensoroSensor.setRssi(scanBLEResult.getRssi());
                if (e3214.sn != null) {
                    snMap.put(address, e3214.sn);
                }
                sensoroSensor.setSerialNumber(snMap.get(address));

                if (e3214.humidity != null) {
                    humidityMap.put(address, (float) e3214.humidity);

                }
                sensoroSensor.setHumidity(humidityMap.get(address));

                if (e3214.temperature != null) {
                    temperatureMap.put(address, e3214.temperature);
                }
                sensoroSensor.setTemperature(temperatureMap.get(address));
                if (e3214.light != null) {
                    lightMap.put(address, e3214.light);
                }
                sensoroSensor.setLight(lightMap.get(address));

                if (e3214.accelerometerCount != 0) {
                    accelerometerCountMap.put(address, e3214.accelerometerCount);
                }

                Integer accelerometerCount = accelerometerCountMap.get(address);
                if (accelerometerCount != null) {
                    sensoroSensor.accelerometerCount = accelerometerCount;
                }

                if (e3214.customize != null) {
                    customizeMap.put(address, e3214.customize);
                }
                sensoroSensor.customize = customizeMap.get(sensoroSensor.macAddress);


                if (e3214.leak != null) {
                    leakMap.put(sensoroSensor.macAddress, e3214.leak);
                }
                sensoroSensor.leak = leakMap.get(sensoroSensor.macAddress);

                if (e3214.co != null) {
                    coMap.put(sensoroSensor.macAddress, e3214.co);
                }
                sensoroSensor.co = coMap.get(sensoroSensor.macAddress);

                if (e3214.co2 != null) {
                    co2Map.put(sensoroSensor.macAddress, e3214.co2);
                }
                sensoroSensor.co2 = co2Map.get(sensoroSensor.macAddress);

                if (e3214.no2 != null) {
                    no2Map.put(sensoroSensor.macAddress, e3214.no2);
                }
                sensoroSensor.no2 = no2Map.get(sensoroSensor.macAddress);

                if (e3214.methane != null) {
                    methaneMap.put(sensoroSensor.macAddress, e3214.methane);
                }
                sensoroSensor.methane = methaneMap.get(sensoroSensor.macAddress);

                if (e3214.lpg != null) {
                    lpgMap.put(sensoroSensor.macAddress, e3214.lpg);
                }
                sensoroSensor.lpg = lpgMap.get(sensoroSensor.macAddress);

                if (e3214.pm1 != null) {
                    pm1Map.put(sensoroSensor.macAddress, e3214.pm1);
                }
                sensoroSensor.pm1 = pm1Map.get(sensoroSensor.macAddress);

                if (e3214.pm25 != null) {
                    pm25Map.put(sensoroSensor.macAddress, e3214.pm25);
                }
                sensoroSensor.pm25 = pm25Map.get(sensoroSensor.macAddress);

                if (e3214.pm10 != null) {
                    pm10Map.put(sensoroSensor.macAddress, e3214.pm10);
                }
                sensoroSensor.pm10 = pm10Map.get(sensoroSensor.macAddress);

                if (e3214.coverstatus != null) {
                    coverstatusMap.put(sensoroSensor.macAddress, (float) e3214.coverstatus);
                }
                sensoroSensor.coverStatus = coverstatusMap.get(sensoroSensor.macAddress);

                if (e3214.level != null) {
                    levelMap.put(sensoroSensor.macAddress, e3214.level);
                }
                sensoroSensor.level = levelMap.get(sensoroSensor.macAddress);

                if (e3214.pitchAngle != null) {
                    pitchAngleMap.put(sensoroSensor.macAddress, e3214.pitchAngle);
                }
                sensoroSensor.pitchAngle = pitchAngleMap.get(sensoroSensor.macAddress);

                if (e3214.rollAngle != null) {
                    rollAngleMap.put(sensoroSensor.macAddress, e3214.rollAngle);
                }
                sensoroSensor.rollAngle = rollAngleMap.get(sensoroSensor.macAddress);

                if (e3214.yawAngle != null) {
                    yawAngleMap.put(sensoroSensor.macAddress, e3214.yawAngle);
                }
                sensoroSensor.yawAngle = yawAngleMap.get(sensoroSensor.macAddress);
                if (e3214.flame != null) {
                    flameMap.put(sensoroSensor.macAddress, e3214.flame);
                }
                sensoroSensor.flame = flameMap.get(sensoroSensor.macAddress);

                if (e3214.artificial_gas != null) {
                    gasMap.put(sensoroSensor.macAddress, e3214.artificial_gas);
                }
                sensoroSensor.gas = gasMap.get(sensoroSensor.macAddress);

                if (e3214.smoke != null) {
                    smokeMap.put(sensoroSensor.macAddress, e3214.smoke);
                }
                sensoroSensor.smokeStatus = smokeMap.get(sensoroSensor.macAddress);

                if (e3214.pressure != null) {
                    pressureMap.put(sensoroSensor.macAddress, e3214.pressure);
                }
                sensoroSensor.waterPressure = pressureMap.get(sensoroSensor.macAddress);

                sensoroSensor.setType(BLEDevice.TYPE_SENSOR);
                if (sensoroSensor.getSerialNumber() == null) {
                    sensoroSensor = null;
                    Log.e(TAG, "createDevice: sensoroSensor.getSerialNumber() == null");
                }
                return sensoroSensor;
            } else {
                return null;
            }
        }
        if (device_data != null) {
            SensoroDevice sensoroSensor = new SensoroDevice();
            Log.e(TAG, "createDevice: device_data = " + device_data);
//            SensoroDevice bleDevice = new SensoroDevice();
            byte[] sn = new byte[8];
            System.arraycopy(device_data, 0, sn, 0, sn.length);
            String address = scanBLEResult.getDevice().getAddress();
            sensoroSensor.setMacAddress(address);

            String serialNumber = SensoroUUID.parseSN(sn);
            if (!TextUtils.isEmpty(serialNumber)) {
                if (serialNumber != null) {
                    snMap.put(address, serialNumber);
                }

                sensoroSensor.setSerialNumber(snMap.get(scanBLEResult.getDevice().getAddress()));
            }

            byte[] hardware = new byte[2];
            System.arraycopy(device_data, 8, hardware, 0, hardware.length);
            int hardwareCode = (int) hardware[0] & 0xff;
            String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
            if (!TextUtils.isEmpty(hardwareVersion)) {
                sensoroSensor.setHardwareVersion(hardwareVersion);
            }
            int firmwareCode = (int) hardware[1] & 0xff;
            String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer.toHexString
                    (firmwareCode % 16).toUpperCase();
            if (!TextUtils.isEmpty(firmwareVersion)) {
                sensoroSensor.setFirmwareVersion(firmwareVersion);
            }
            int batteryLevel = ((int) device_data[10] & 0xff);
            sensoroSensor.setBatteryLevel(batteryLevel);

            int power = ((int) device_data[11] & 0xff);
            sensoroSensor.setTransmitPower(power);

            byte[] sf = new byte[4];
            System.arraycopy(device_data, 12, sf, 0, sf.length);
            float sf1 = SensoroUUID.byteArrayToFloat(sf, 0);
            sensoroSensor.setSf(sf1);

            sensoroSensor.setMacAddress(scanBLEResult.getDevice().getAddress());
            int last_index = device_data.length - 1;
            if (device_data[last_index] == 0x01) { // dfu
                sensoroSensor.setDfu(true);
            } else {
                sensoroSensor.setDfu(false);
            }
//            if (sensoroSensor != null) {
//                bleDevice.setSensoroSensor(sensoroSensor);
//            }
            sensoroSensor.setRssi(scanBLEResult.getRssi());
            sensoroSensor.setType(BLEDevice.TYPE_SENSOR);
            if (sensoroSensor.getSerialNumber() == null) {
                sensoroSensor = null;
                Log.e(TAG, "createDevice: sensoroSensor.getSerialNumber() == null");
            }
            return sensoroSensor;

        }
        return null;

        //////////////////////

    }

    public static void clear() {
        snMap.clear();
        hardwareVersionMap.clear();
        firmwareVersionMap.clear();
        humidityMap.clear();
        batteryMap.clear();
        lightMap.clear();
        temperatureMap.clear();
        accelerometerCountMap.clear();
        customizeMap.clear();
        dripMap.clear();
        coMap.clear();
        co2Map.clear();
        no2Map.clear();
        methaneMap.clear();
        lpgMap.clear();
        pm1Map.clear();
        pm25Map.clear();
        pm10Map.clear();
        coverstatusMap.clear();
        levelMap.clear();
        pitchMap.clear();
        rollMap.clear();
        yawMap.clear();
        flameMap.clear();
        gasMap.clear();
        smokeMap.clear();
        waterPressureMap.clear();
        leakMap.clear();
        pitchAngleMap.clear();
        rollAngleMap.clear();
        yawAngleMap.clear();
        pressureMap.clear();
    }

    protected static HashMap<String, Integer> leakMap = new HashMap<>();
    protected static HashMap<String, Float> pitchAngleMap = new HashMap<>();
    protected static HashMap<String, Float> rollAngleMap = new HashMap<>();
    protected static HashMap<String, Float> yawAngleMap = new HashMap<>();
    protected static HashMap<String, Float> pressureMap = new HashMap<>();
}


