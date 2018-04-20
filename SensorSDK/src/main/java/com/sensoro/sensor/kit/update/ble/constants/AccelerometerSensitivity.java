package com.sensoro.sensor.kit.update.ble.constants;

import java.io.Serializable;

/**
 * The enum of the acceleration sensor sensitivity.
 */
public enum AccelerometerSensitivity implements Serializable {
    /**
     * Acceleration sensor disabled.
     */
    DISABLED,   //0x00
    /**
     * The lowest sensitivity of acceleration sensor.
     */
    MIN,    //0x70
    /**
     * The medium sensitivity of acceleration sensor.
     */
    MEDIUM, //0x5d
    /**
     * The highest sensitivity of acceleration sensor.
     */
    MAX, //0x4b
    /**
     * Unknown.
     */
    UNKNOWN;

    public static AccelerometerSensitivity getAccleromerterSensitivity(int value) {
        switch (value) {
            case 0:
                return AccelerometerSensitivity.DISABLED;
            case 0x70:
                return AccelerometerSensitivity.MIN;
            case 0x5d:
                return AccelerometerSensitivity.MEDIUM;
            case 0x4b:
                return AccelerometerSensitivity.MAX;
            default:
                return AccelerometerSensitivity.UNKNOWN;
        }
    }

    public static byte getAccelerometerSensitivityValue(AccelerometerSensitivity accelerometerSensitivityKey) {
        switch (accelerometerSensitivityKey) {
            case DISABLED:
                return 0;
            case MIN:
                return 0x70;
            case MEDIUM:
                return 0x5d;
            case MAX:
                return 0x4b;
            default:
                return 0;
        }
    }
}