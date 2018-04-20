package com.sensoro.sensor.kit.update.ble;

import android.os.ParcelUuid;

import com.sensoro.sensor.kit.update.ble.scanner.BLEFilter;
import com.sensoro.sensor.kit.update.ble.scanner.ScanBLEResult;
import com.sensoro.sensor.kit.update.ble.scanner.SensoroUUID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangping on 2016/7/13.
 */

public class BLEDeviceFactory {
    private static final String TAG = "BLEDeviceFactory";
    private ScanBLEResult scanBLEResult;
    private static HashMap<String, String> snMap = new HashMap<>();
    protected static HashMap<String, Integer> accelerometerCountMap = new HashMap<>();
    protected static HashMap<String, byte[]> customizeMap = new HashMap<>();
    private static HashMap<String, Float> temperatureMap = new HashMap<>();
    private static HashMap<String, Float> humidityMap = new HashMap<>();
    private static HashMap<String, Float> lightMap = new HashMap<>();
    protected static HashMap<String, Integer> leakMap = new HashMap<>();
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
    protected static HashMap<String, Float> pitchAngleMap = new HashMap<>();
    protected static HashMap<String, Float> rollAngleMap = new HashMap<>();
    protected static HashMap<String, Float> yawAngleMap = new HashMap<>();
    protected static HashMap<String, Integer> flameMap = new HashMap<>();
    protected static HashMap<String, Float> gasMap = new HashMap<>();
    protected static HashMap<String, Integer> smokeMap = new HashMap<>();
    protected static HashMap<String, Float> pressureMap = new HashMap<>();


    public BLEDeviceFactory(ScanBLEResult scanBLEResult) {
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
        ParcelUuid parcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.DEVICE_SERVICE_DATA_UUID);
        byte device_data[] = scanBLEResult.getScanRecord().getServiceData(parcelUuid);

        if (device_data != null) {
            SensoroDevice bleDevice = new SensoroDevice();
            byte[] sn = new byte[8];
            System.arraycopy(device_data, 0, sn, 0, sn.length);
            bleDevice.setSerialNumber(SensoroUUID.parseSN(sn));

            byte[] hardware = new byte[2];
            System.arraycopy(device_data, 8, hardware, 0, hardware.length);
            int hardwareCode = (int) hardware[0] & 0xff;
            String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
            bleDevice.setHardwareVersion(hardwareVersion);

            int firmwareCode = (int) hardware[1] & 0xff;
            String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer.toHexString(firmwareCode % 16).toUpperCase();
            bleDevice.setFirmwareVersion(firmwareVersion);

            int batteryLevel = ((int) device_data[10] & 0xff);
            bleDevice.setBatteryLevel(batteryLevel);

            int power = ((int) device_data[11] & 0xff);
            bleDevice.setPower(power);

            byte[] sf = new byte[4];
            System.arraycopy(device_data, 12, sf, 0, sf.length);
            bleDevice.setSf(SensoroUUID.byteArrayToFloat(sf, 0));

            bleDevice.setMacAddress(scanBLEResult.getDevice().getAddress());
            int last_index = device_data.length - 1;
            if (device_data[last_index] == 0x01) { // dfu
                bleDevice.setDfu(true);
            } else {
                bleDevice.setDfu(false);
            }
            bleDevice.setRssi(scanBLEResult.getRssi());
            bleDevice.setType(BLEDevice.TYPE_DEVICE);
            return bleDevice;
        }
        return null;

    }

    @Deprecated
    public SensoroSensor createSensor() {
        if (scanBLEResult == null) {
            return null;
        }
        try {
            E3214 e3214 = E3214.createE3214(scanBLEResult);
            if (e3214 != null) {
                SensoroSensor sensoroSensor = new SensoroSensor();
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

                if (e3214.humidity != null ) {
                    humidityMap.put(address, (float)e3214.humidity);

                }
                sensoroSensor.setHumidity(humidityMap.get(address));

                if (e3214.temperature != null ) {
                    temperatureMap.put(address, e3214.temperature);
                }
                sensoroSensor.setTemperature(temperatureMap.get(address));
                if (e3214.light != null) {
                    lightMap.put(address, e3214.light);
                }
                sensoroSensor.setLight(lightMap.get(address));

                if (e3214.accelerometerCount != null) {
                    accelerometerCountMap.put(address, e3214.accelerometerCount);
                }
                sensoroSensor.accelerometerCount = accelerometerCountMap.get(address);

                if (e3214.customize != null) {
                    customizeMap.put(address, e3214.customize);
                }
                sensoroSensor.customize = customizeMap.get(sensoroSensor.macAddress);


                if (e3214.leak != null) {
                    leakMap.put(sensoroSensor.macAddress, e3214.leak);
                }
                sensoroSensor.leak = leakMap.get(sensoroSensor.macAddress) ;

                if (e3214.co != null) {
                    coMap.put(sensoroSensor.macAddress, e3214.co);
                }
                sensoroSensor.co = coMap.get(sensoroSensor.macAddress);

                if (e3214.co2 != null) {
                    co2Map.put(sensoroSensor.macAddress, e3214.co2) ;
                }
                sensoroSensor.co2 = co2Map.get(sensoroSensor.macAddress);

                if (e3214.no2 != null) {
                    no2Map.put(sensoroSensor.macAddress, e3214.no2);
                }
                sensoroSensor.no2 = no2Map.get(sensoroSensor.macAddress) ;

                if (e3214.methane != null) {
                    methaneMap.put(sensoroSensor.macAddress , e3214.methane);
                }
                sensoroSensor.methane = methaneMap.get(sensoroSensor.macAddress);

                if (e3214.lpg != null) {
                    lpgMap.put(sensoroSensor.macAddress , e3214.lpg);
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
                    coverstatusMap.put(sensoroSensor.macAddress, (float)e3214.coverstatus);
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
                if(sensoroSensor.getSerialNumber() == null) {
                    return null;
                }

                return sensoroSensor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Deprecated
    public SensoroStation createStation() {
        if (scanBLEResult == null) {
            return null;
        }
        Map<ParcelUuid, byte[]> serviceData = scanBLEResult.getScanRecord().getServiceData();
        if (serviceData == null) {
            return null;
        }
        ParcelUuid parcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.STATION_SERVICE_DATA_UUID);
        byte station_data[] = scanBLEResult.getScanRecord().getServiceData(parcelUuid);
        if (station_data != null) {
            SensoroStation bleDevice = new SensoroStation();
            byte[] sn = new byte[8];
            System.arraycopy(station_data, 0, sn, 0, sn.length);
            bleDevice.setSerialNumber(SensoroUUID.parseSN(sn));

            byte[] hardware = new byte[2];
            System.arraycopy(station_data, 8, hardware, 0, hardware.length);
            int hardwareCode = (int) hardware[0] & 0xff;
            String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
            bleDevice.setHardwareVersion(hardwareVersion);

            int firmwareCode = (int) hardware[1] & 0xff;
            String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer.toHexString(firmwareCode % 16).toUpperCase();
            bleDevice.setFirmwareVersion(firmwareVersion);

            int workStatus = ((int) station_data[10] & 0xff);
            bleDevice.setWorkStatus(workStatus);
            //03, 0c, 30
//            int netStatus = ((int) station_data[11] & 0xff);
            int wifiStatus = (int)station_data[11] & 0x03;
            int ethStatus = ((int) station_data[11] & 0x0c) >> 2;
            int celluarStatus = ((int) station_data[11] & 0x30) >> 4;
//            bleDevice.setNetStatus(netStatus);
            bleDevice.setWifiStatus(wifiStatus);
            bleDevice.setEthStatus(ethStatus);
            bleDevice.setCellularStatus(celluarStatus);
            bleDevice.setRssi(scanBLEResult.getRssi());
            bleDevice.setMacAddress(scanBLEResult.getDevice().getAddress());
            bleDevice.setType(BLEDevice.TYPE_STATION);
            return bleDevice;
        }
        return null;
    }

    public  BLEDevice create() {
        if (scanBLEResult == null) {
            return null;
        }
        Map<ParcelUuid, byte[]> serviceData = scanBLEResult.getScanRecord().getServiceData();
        if (serviceData == null) {
            return null;
        }
        ParcelUuid stationParcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.STATION_SERVICE_DATA_UUID);
        ParcelUuid deviceParcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.DEVICE_SERVICE_DATA_UUID);
        ParcelUuid sensorParcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.SENSOR_SERVICE_UUID_E3412);
        byte sensor_data[] = scanBLEResult.getScanRecord().getServiceData(sensorParcelUuid);
        byte device_data[] = scanBLEResult.getScanRecord().getServiceData(deviceParcelUuid);
        byte station_data[] = scanBLEResult.getScanRecord().getServiceData(stationParcelUuid);
        SensoroSensor sensoroSensor = null;
        if (sensor_data != null) {
            E3214 e3214 = E3214.parseE3214(sensor_data);
            if (e3214 != null) {
                sensoroSensor = new SensoroSensor();
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

                if (e3214.accelerometerCount != null) {
                    accelerometerCountMap.put(address, e3214.accelerometerCount);
                }
                sensoroSensor.accelerometerCount = accelerometerCountMap.get(address);

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
                }
            }
        }
        if (device_data != null) {
            SensoroDevice bleDevice = new SensoroDevice();
            byte[] sn = new byte[8];
            System.arraycopy(device_data, 0, sn, 0, sn.length);
            bleDevice.setSerialNumber(SensoroUUID.parseSN(sn));

            byte[] hardware = new byte[2];
            System.arraycopy(device_data, 8, hardware, 0, hardware.length);
            int hardwareCode = (int) hardware[0] & 0xff;
            String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
            bleDevice.setHardwareVersion(hardwareVersion);

            int firmwareCode = (int) hardware[1] & 0xff;
            String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer.toHexString(firmwareCode % 16).toUpperCase();
            bleDevice.setFirmwareVersion(firmwareVersion);

            int batteryLevel = ((int) device_data[10] & 0xff);
            bleDevice.setBatteryLevel(batteryLevel);

            int power = ((int) device_data[11] & 0xff);
            bleDevice.setPower(power);

            byte[] sf = new byte[4];
            System.arraycopy(device_data, 12, sf, 0, sf.length);
            bleDevice.setSf(SensoroUUID.byteArrayToFloat(sf, 0));

            bleDevice.setMacAddress(scanBLEResult.getDevice().getAddress());
            int last_index = device_data.length - 1;
            if (device_data[last_index] == 0x01) { // dfu
                bleDevice.setDfu(true);
            } else {
                bleDevice.setDfu(false);
            }
            if (sensoroSensor != null) {
                bleDevice.setSensoroSensor(sensoroSensor);
            }
            bleDevice.setRssi(scanBLEResult.getRssi());
            bleDevice.setType(BLEDevice.TYPE_DEVICE);
            return bleDevice;
        } else if (station_data != null) {
            SensoroStation bleDevice = new SensoroStation();
            byte[] sn = new byte[8];
            System.arraycopy(station_data, 0, sn, 0, sn.length);
            bleDevice.setSerialNumber(SensoroUUID.parseSN(sn));

            byte[] hardware = new byte[2];
            System.arraycopy(station_data, 8, hardware, 0, hardware.length);
            int hardwareCode = (int) hardware[0] & 0xff;
            String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
            bleDevice.setHardwareVersion(hardwareVersion);

            int firmwareCode = (int) hardware[1] & 0xff;
            String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer.toHexString(firmwareCode % 16).toUpperCase();
            bleDevice.setFirmwareVersion(firmwareVersion);

            int workStatus = ((int) station_data[10] & 0xff);
            bleDevice.setWorkStatus(workStatus);
            //03, 0c, 30
//            int netStatus = ((int) station_data[11] & 0xff);
            int wifiStatus = (int)station_data[11] & 0x03;
            int ethStatus = ((int) station_data[11] & 0x0c) >> 2;
            int celluarStatus = ((int) station_data[11] & 0x30) >> 4;
//            bleDevice.setNetStatus(netStatus);
            bleDevice.setWifiStatus(wifiStatus);
            bleDevice.setEthStatus(ethStatus);
            bleDevice.setCellularStatus(celluarStatus);
            bleDevice.setRssi(scanBLEResult.getRssi());
            bleDevice.setMacAddress(scanBLEResult.getDevice().getAddress());
            bleDevice.setType(BLEDevice.TYPE_STATION);
            return bleDevice;
        } else {
            if (sensor_data != null) {
                return sensoroSensor;
            } else {
                return null;
            }

        }
    }

    protected static void clear() {
        snMap.clear();
        lightMap.clear();
        temperatureMap.clear();
        humidityMap.clear();
        leakMap.clear();
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
        pitchAngleMap.clear();
        rollAngleMap.clear();
        yawAngleMap.clear();
        flameMap.clear();
        gasMap.clear();
        smokeMap.clear();
        pressureMap.clear();
    }
}


