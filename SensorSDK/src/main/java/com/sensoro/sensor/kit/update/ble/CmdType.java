package com.sensoro.sensor.kit.update.ble;

/**
 * Created by Sensoro on 15/7/28.
 */
public class CmdType {
    public static final int CMD_NULL = 0xff;
    public static final int CMD_ON_DFU_MODE = 0x101;
    public static final int CMD_R_CFG = 0x00;
    public static final int CMD_W_CFG = 0x01;
    public static final int CMD_SET_PASSWORD = 0X02;
    public static final int CMD_SIGNAL = 0X03;
    public static final int CMD_RESET_TO_FACTORY = 0X04;
    public static final int CMD_FORCE_RELOAD = 0X05;
    public static final int CMD_RESET_ACC = 0X06;
    public static final int CMD_SET_BROADCAST_KEY = 0X07;
    public static final int CMD_SET_SMOKE = 0x08;
    public static final int CMD_SET_ZERO = 0x09;
    public static final int CMD_UPDATE_SENSOR = 0x40;
    public static final int CMD_W_DFU = 0x50;
    public static final int CMD_SN = 0x00;
    public static final int CMD_HARDWARE_VERSION = 0x01;
    public static final int CMD_BATTERY = 0X02;
    public static final int CMD_TEMPTURE = 0X03;
    public static final int CMD_HUMIDITY = 0X04;
    public static final int CMD_LIGHT = 0X05;
    public static final int CMD_ACCELERATION = 0X06;
    public static final int CMD_CUSTOMIZE = 0X07;
    public static final int CMD_LEAK = 0x8;
    public static final int CMD_CO = 0x9;
    public static final int CMD_CO2 = 0x0A;
    public static final int CMD_NO2 = 0x0B;
    public static final int CMD_METHANE = 0x0C;
    public static final int CMD_LPG = 0x0D;
    public static final int CMD_PM1 = 0x0E;
    public static final int CMD_PM25 = 0x0F;
    public static final int CMD_PM10 = 0x10;
    public static final int CMD_COVER = 0x11;
    public static final int CMD_LEVEL = 0x12;
    public static final int CMD_ANGLE_PITCH = 0x13;
    public static final int CMD_ANGLE_ROLL = 0x14;
    public static final int CMD_ANGLE_YAW = 0x15;
    public static final int CMD_FLAME = 0x16;
    public static final int CMD_ARTIFICIAL = 0x17;
    public static final int CMD_SMOKE = 0x18;
    public static final int CMD_PRESSURE = 0x19;
}
