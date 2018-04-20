package com.sensoro.sensor.kit;

import android.os.ParcelUuid;

import com.sensoro.sensor.kit.update.ble.scanner.BLEFilter;
import com.sensoro.sensor.kit.update.ble.scanner.ScanBLEResult;

/**
 * Created by fangping on 2016/7/12.
 */

public class E3214 extends SensoroUUID {

    //    String sn = null ;
//    String hardwareVersion = null ;
//    String firmwareVersion = null;
//    Integer batteryLevel = null ;// 剩余电量
//    Float temperature = null ;// 温度
//    Float light = null ; // 光线照度
//    Integer humidity = null ;//湿度
//    Integer accelerometerCount = null ; // 加速度计数器
//    Integer drip = null;
//    Float co = null;
//    Float co2 = null ;
//    Float no2 = null ;
//    Float methane = null;
//    Float lpg = null;
//    Float pm1 = null;
//    Float pm25 = null ;
//    Float pm10 = null;
//    Float pitch = null;
//    Float roll = null;
//    Float yaw = null;
//    Integer flame = null;
//    Integer smoke = null;
//    Float artificial_gas = null;
//    Float water_pressure = null;
//    Integer coverstatus = null;
//    Float level = null;
//    byte customize[] = null;
//
//    static E3214 createE3214(ScanBLEResult scanBLEResult) {
//        Map<ParcelUuid, byte[]> serviceData = scanBLEResult.getScanRecord().getServiceData();
//        if (serviceData == null) {
//            return null;
//        }
//        ParcelUuid parcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.SERVICE_UUID_E3412);
//        byte[] e780Bytes = scanBLEResult.getScanRecord().getServiceData(parcelUuid);
//        if (e780Bytes != null) {
//           return  parseE3214(e780Bytes);
//        }
//        return null;
//    }
//
//    private static E3214 parseE3214(byte[] e3214Bytes) {
//        E3214 e3214 = new E3214();
//        int cur_pos = 0;
//        int data_length = e3214Bytes.length;
//        for (int i = 0; i < data_length; ) {
//            int cur_type = e3214Bytes[cur_pos];
//            switch (cur_type) {
//                case CmdType.CMD_SN:
//                    byte[] sn = new byte[8];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, sn, 0, sn.length);
//                    e3214.sn = parseSN(sn);
//                    cur_pos += 9;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_HARDWARE_VERSION:
//                    byte[] hardware = new byte[2];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, hardware, 0, hardware.length);
//                    int hardwareCode = (int) hardware[0] & 0xff;
//                    String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
//                    e3214.hardwareVersion = hardwareVersion;
//                    int firmwareCode = (int) hardware[1] & 0xff;
//                    String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer
// .toHexString(firmwareCode % 16).toUpperCase();
//                    e3214.firmwareVersion = firmwareVersion;
//                    cur_pos += 3;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_BATTERY:
//                    int batteryLevel = ((int) e3214Bytes[cur_pos + 1] & 0xff);
//                    e3214.batteryLevel = batteryLevel;
//                    cur_pos += 2;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_TEMPERATURE:
//                    byte[] temperature = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, temperature, 0, temperature.length);
//                    e3214.temperature = byteArrayToFloat(temperature, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_HUMIDITY:
//                    int humidity = ((int) e3214Bytes[cur_pos + 1] & 0xff);
//                    e3214.humidity = humidity;
//                    cur_pos += 2;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_LIGHT:
//                    byte[] light = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, light, 0, light.length);
//                    e3214.light = byteArrayToFloat(light, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_ACCELERATION:
//                    byte[] acceleration = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, acceleration, 0, acceleration.length);
//                    e3214.accelerometerCount =  byteArrayToInt(acceleration);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_DRIP :
//                    byte[] drip = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, drip, 0, drip.length);
//                    e3214.drip = byteArrayToInt(drip);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_CO:
//                    byte[] co = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, co, 0, co.length);
//                    e3214.co = byteArrayToFloat(co, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_CO2:
//                    byte[] co2 = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, co2, 0, co2.length);
//                    e3214.co2 = byteArrayToFloat(co2, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_NO2:
//                    byte[] no2 = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, no2, 0, no2.length);
//                    e3214.no2 = byteArrayToFloat(no2, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_METHANE:
//                    byte[] methane = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, methane, 0, methane.length);
//                    e3214.methane = byteArrayToFloat(methane, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_LPG:
//                    byte[] lpg = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, lpg, 0, lpg.length);
//                    e3214.lpg = byteArrayToFloat(lpg, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_PM1:
//                    byte[] pm1 = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, pm1, 0, pm1.length);
//                    e3214.pm1 = byteArrayToFloat(pm1, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_PM25:
//                    byte[] pm25 = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, pm25, 0, pm25.length);
//                    e3214.pm25 = byteArrayToFloat(pm25, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_PM10:
//                    byte[] pm10 = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, pm10, 0, pm10.length);
//                    e3214.pm10 = byteArrayToFloat(pm10, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_COVER:
//                    int cover = ((int) e3214Bytes[cur_pos + 1] & 0xff);
//                    e3214.coverstatus = cover;
//                    cur_pos += 2;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_LEVEL:
//                    byte[] level = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, level, 0, level.length);
//                    e3214.level = byteArrayToFloat(level, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_PITCH:
//                    byte[] pitch = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, pitch, 0, pitch.length);
//                    e3214.pitch = byteArrayToFloat(pitch, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_ROLL:
//                    byte[] roll = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, roll, 0, roll.length);
//                    e3214.roll = byteArrayToFloat(roll, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_YAW:
//                    byte[] yaw = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, yaw, 0, yaw.length);
//                    e3214.yaw = byteArrayToFloat(yaw, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_FLAME:
//                    int flame = ((int) e3214Bytes[cur_pos + 1] & 0xff);
//                    e3214.flame = flame;
//                    cur_pos += 2;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_ARTIFICIAL_GAS:
//                    byte[] gas = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, gas, 0, gas.length);
//                    e3214.artificial_gas = byteArrayToFloat(gas, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_CUSTOMIZE:
//                    byte[] customize = new byte[e3214Bytes.length - 1];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, customize, 0, customize.length);
//                    e3214.customize = customize;
//                    cur_pos += customize.length + 1;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_SMOKE:
//                    int smoke = ((int) e3214Bytes[cur_pos + 1] & 0xff);
//                    e3214.smoke = smoke;
//                    cur_pos += 2;
//                    i = cur_pos;
//                    break;
//                case CmdType.CMD_WATER_PRESSURE:
//                    byte[] water_pressure = new byte[4];
//                    System.arraycopy(e3214Bytes, cur_pos + 1, water_pressure, 0, water_pressure.length);
//                    e3214.water_pressure = byteArrayToFloat(water_pressure, 0);
//                    cur_pos += 5;
//                    i = cur_pos;
//                    break;
//                default:
//                    i = data_length;
//                    break;
//            }
//
//        }
//        return e3214;
//    }
    //////////////////////////////////////////////////////////////////
    public String sn;
    public String hardwareVersion;
    public String firmwareVersion;
    public Integer batteryLevel;// 剩余电量
    public Float temperature;// 温度
    public Float light; // 光线照度
    public Integer humidity;//湿度
    public int accelerometerCount; // 加速度计数器
    public Integer leak = null;
    public Float co = null;
    public Float co2 = null;
    public Float no2 = null;
    public Float methane = null;
    public Float lpg = null;
    public Float pm1 = null;
    public Float pm25 = null;
    public Float pm10 = null;
    public Integer coverstatus = null;
    public Integer flame = null;
    public Integer smoke = null;
    public Float artificial_gas = null;
    public Float level = null;
    public Float pitchAngle = null;
    public Float rollAngle = null;
    public Float yawAngle = null;
    public Float pressure = null;
    public byte customize[] = null;


    public static E3214 createE3214(ScanBLEResult scanBLEResult) {
        try {
            ParcelUuid parcelUuid = BLEFilter.createServiceDataUUID(BLEFilter.SENSOR_SERVICE_UUID_E3412);
            byte[] e3214Bytes = scanBLEResult.getScanRecord().getServiceData(parcelUuid);
            if (e3214Bytes != null) {
                return parseE3214(e3214Bytes);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static E3214 parseE3214(byte[] e3214Bytes) {

        try {
            E3214 e3214 = new E3214();
            int cur_pos = 0;
            int data_length = e3214Bytes.length;
            for (int i = 0; i < data_length; ) {
                int cur_type = e3214Bytes[cur_pos];
                switch (cur_type) {
                    case CmdType.CMD_SN:
                        byte[] sn = new byte[8];
                        System.arraycopy(e3214Bytes, cur_pos + 1, sn, 0, sn.length);
                        e3214.sn = parseSN(sn);
                        cur_pos += 9;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_HARDWARE_VERSION:
                        byte[] hardware = new byte[2];
                        System.arraycopy(e3214Bytes, cur_pos + 1, hardware, 0, hardware.length);
                        int hardwareCode = (int) hardware[0] & 0xff;
                        String hardwareVersion = Integer.toHexString(hardwareCode).toUpperCase();
                        e3214.hardwareVersion = hardwareVersion;
                        int firmwareCode = (int) hardware[1] & 0xff;
                        String firmwareVersion = Integer.toHexString(firmwareCode / 16).toUpperCase() + "." + Integer
                                .toHexString(firmwareCode % 16).toUpperCase();
                        e3214.firmwareVersion = firmwareVersion;
                        cur_pos += 3;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_BATTERY:
                        int batteryLevel = ((int) e3214Bytes[cur_pos + 1] & 0xff);
                        e3214.batteryLevel = batteryLevel;
                        cur_pos += 2;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_TEMPERATURE:
                        byte[] temperature = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, temperature, 0, temperature.length);
                        e3214.temperature = byteArrayToFloat(temperature, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_HUMIDITY:
                        int humidity = ((int) e3214Bytes[cur_pos + 1] & 0xff);
                        e3214.humidity = humidity;
                        cur_pos += 2;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_LIGHT:
                        byte[] light = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, light, 0, light.length);
                        e3214.light = byteArrayToFloat(light, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_ACCELERATION:
                        byte[] acceleration = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, acceleration, 0, acceleration.length);
                        e3214.accelerometerCount = byteArrayToInt(acceleration);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_LEAK:
                        byte[] drip = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, drip, 0, drip.length);
                        e3214.leak = byteArrayToInt(drip);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_CO:
                        byte[] co = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, co, 0, co.length);
                        e3214.co = byteArrayToFloat(co, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_CO2:
                        byte[] co2 = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, co2, 0, co2.length);
                        e3214.co2 = byteArrayToFloat(co2, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_NO2:
                        byte[] no2 = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, no2, 0, no2.length);
                        e3214.no2 = byteArrayToFloat(no2, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_METHANE:
                        byte[] methane = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, methane, 0, methane.length);
                        e3214.methane = byteArrayToFloat(methane, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_LPG:
                        byte[] lpg = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, lpg, 0, lpg.length);
                        e3214.lpg = byteArrayToFloat(lpg, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_PM1:
                        byte[] pm1 = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, pm1, 0, pm1.length);
                        e3214.pm1 = byteArrayToFloat(pm1, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_PM25:
                        byte[] pm25 = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, pm25, 0, pm25.length);
                        e3214.pm25 = byteArrayToFloat(pm25, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_PM10:
                        byte[] pm10 = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, pm10, 0, pm10.length);
                        e3214.pm10 = byteArrayToFloat(pm10, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_COVER:
                        int cover = ((int) e3214Bytes[cur_pos + 1] & 0xff);
                        e3214.coverstatus = cover;
                        cur_pos += 2;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_LEVEL:
                        byte[] level = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, level, 0, level.length);
                        e3214.level = byteArrayToFloat(level, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_ANGLE_PITCH:
                        byte[] pitch = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, pitch, 0, pitch.length);
                        e3214.pitchAngle = byteArrayToFloat(pitch, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_ANGLE_ROLL:
                        byte[] roll = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, roll, 0, roll.length);
                        e3214.rollAngle = byteArrayToFloat(roll, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_ANGLE_YAW:
                        byte[] yaw = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, yaw, 0, yaw.length);
                        e3214.yawAngle = byteArrayToFloat(yaw, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_CUSTOMIZE:
                        byte[] customize = new byte[e3214Bytes.length - 1];
                        System.arraycopy(e3214Bytes, cur_pos + 1, customize, 0, customize.length);
                        e3214.customize = customize;
                        cur_pos += customize.length + 1;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_FLAME:
                        int flame = ((int) e3214Bytes[cur_pos + 1] & 0xff);
                        e3214.flame = flame;
                        cur_pos += 2;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_ARTIFICIAL:
                        byte[] gas = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, gas, 0, gas.length);
                        e3214.artificial_gas = byteArrayToFloat(gas, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_SMOKE:
                        int smoke = ((int) e3214Bytes[cur_pos + 1] & 0xff);
                        e3214.smoke = smoke;
                        cur_pos += 2;
                        i = cur_pos;
                        break;
                    case CmdType.CMD_PRESSURE:
                        byte[] pressure = new byte[4];
                        System.arraycopy(e3214Bytes, cur_pos + 1, pressure, 0, pressure.length);
                        e3214.pressure = byteArrayToFloat(pressure, 0);
                        cur_pos += 5;
                        i = cur_pos;
                        break;
                    default:
                        System.out.println("defualt====>" + cur_type);
                        i = data_length;
                        break;
                }
            }
            return e3214;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
