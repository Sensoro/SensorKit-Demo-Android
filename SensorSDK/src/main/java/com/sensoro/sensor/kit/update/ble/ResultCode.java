package com.sensoro.sensor.kit.update.ble;

/**
 * error code.only used for V4
 */
public class ResultCode {
    public static final int SUCCESS = 0x0000;
    public static final int MCU_BUSY = 0x0100;
    public static final int INVALID_CMD_TYPE = 0x0400;
    public static final int INVALID_PARAM = 0x0600;
    public static final int BLUETOOTH_ERROR = 0x8000;
    public static final int SYSTEM_ERROR = 0x8100;
    public static final int TASK_TIME_OUT = 0x8200;
    public static final int MODEL_NOT_SUPPORT = 0x8300;
    public static final int NO_PERMISSION = 0x8400; // need pwd.no permission.
    public static final int PASSWORD_ERR = 0x8500; // password error.
    public static final int EDDYSTONE_ONLY_ERR = 0x8600; // Eddystone only,can not be setted by sdk.
    public static final int PARSE_ERROR = 0x8700;

    public static final int CODE_DEVICE_SUCCESS = 0x00;
    public static final int CODE_DEVICE_INTERNAL_ERROR = 0x01;
    public static final int CODE_DEVICE_INVAILD_SN = 0x02;
    public static final int CODE_DEVICE_INVAILD_DEVUI = 0x03;
    public static final int CODE_DEVICE_INVAILD_APPEUI = 0x04;
    public static final int CODE_DEVICE_INVAILD_APPKEY = 0x05;
    public static final int CODE_DEVICE_INVAILD_APPSKEY = 0x06;
    public static final int CODE_DEVICE_INVAILD_NWKSKEY = 0x07;
    public static final int CODE_DEVICE_INVAILD_DEVADR = 0x08;
    public static final int CODE_DEVICE_INVAILD_IBCNUMM = 0x09;
    public static final int CODE_DEVICE_INVAILD_SECUREKEY = 0x0a;
    public static final int CODE_DEVICE_INVAILD_PASSWORD = 0x0b;
    public static final int CODE_DEVICE_INVAILD_CFG = 0x0c;
    public static final int CODE_DEVICE_INVAILD_DTM = 0x0d;
    public static final int CODE_DEVICE_DFU_ERROR = 0x0e;

    public static final int CODE_STATION_RET_NONE          = 0;
    public static final int CODE_STATION_RET_SUCCCESS      = 1;
    public static final int CODE_STATION_RET_INVALID_CMD   = 2;
    public static final int CDOE_STATION_RET_INVALID_ARG   = 3;
    public static final int CODE_STATION_RET_INVALID_STATE = 4;



}
