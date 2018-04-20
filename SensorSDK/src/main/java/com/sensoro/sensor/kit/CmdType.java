package com.sensoro.sensor.kit;

/**
 * Created by Sensoro on 15/7/28.
 */
public class CmdType {
    public static final int CMD_SN = 0x00;
    public static final int CMD_HARDWARE_VERSION = 0x01;
    public static final int CMD_BATTERY = 0X02;
    public static final int CMD_TEMPERATURE = 0X03;
    public static final int CMD_HUMIDITY = 0X04;
    public static final int CMD_LIGHT = 0X05;
    public static final int CMD_ACCELERATION = 0X06;
    public static final int CMD_CUSTOMIZE = 0X07;
    public static final int CMD_DRIP = 0x8;
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
    public static final int CMD_PITCH = 0x13;
    public static final int CMD_ROLL = 0x14;
    public static final int CMD_YAW = 0x15;
    public static final int CMD_FLAME = 0X16;
    public static final int CMD_ARTIFICIAL_GAS = 0x17;
    public static final int CMD_SMOKE = 0x18;
    public static final int CMD_WATER_PRESSURE = 0x19;
    public static final int CMD_RETENTION_DATA1 = 0xFD;
    public static final int CMD_RETENTION_DATA2 = 0xFE;
    public static final int CMD_RETENTION_DATA3 = 0xFF;
    //更改CMD_R_CFG 未知
    public static final int CMD_R_CFG = 0x100;
    public static final int CMD_W_CFG = 0x101;
    //////////
    public static final int CMD_NULL = 0x1ff;
    public static final int CMD_SET_PASSWORD = 0X102;
    public static final int CMD_SIGNAL = 0X103;
    public static final int CMD_RESET_TO_FACTORY = 0X104;
    public static final int CMD_FORCE_RELOAD = 0X105;
    public static final int CMD_RESET_ACC = 0X106;
    public static final int CMD_SET_BROADCAST_KEY = 0X107;
    public static final int CMD_SET_SMOKE = 0x108;
    public static final int CMD_SET_ZERO = 0x109;
    public static final int CMD_UPDATE_SENSOR = 0x140;
    public static final int CMD_W_DFU = 0x150;
    public static final int CMD_LEAK = 0x8;
    public static final int CMD_ANGLE_PITCH = 0x13;
    public static final int CMD_ANGLE_ROLL = 0x14;
    public static final int CMD_ANGLE_YAW = 0x15;
    public static final int CMD_ARTIFICIAL = 0x17;
    public static final int CMD_PRESSURE = 0x19;
}
