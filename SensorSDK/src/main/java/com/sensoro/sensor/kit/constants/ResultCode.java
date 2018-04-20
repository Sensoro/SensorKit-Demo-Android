package com.sensoro.sensor.kit.constants;

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
}
