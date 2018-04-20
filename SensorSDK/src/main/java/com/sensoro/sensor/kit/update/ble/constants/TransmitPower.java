package com.sensoro.sensor.kit.update.ble.constants;


import com.sensoro.sensor.kit.update.ble.SensoroDevice;

import java.io.Serializable;

/**
 * The transmit power enum of the beacon. </br>
 * The coverage of the LEVEL0 is minimum, and the coverage of LEVEL11 is maximum.</br>
 * Not all values apply to all kinds of beacon. For more details to see {@link <a href="https://github.com/Sensoro/SDK-Android">https://github.com/Sensoro/SDK-Android</a>}.
 */
public enum TransmitPower implements Serializable {
    LEVEL0, // 0x00
    LEVEL1, // 0x01
    LEVEL2, // 0x02
    LEVEL3, // 0x03
    LEVEL4, // 0x04
    LEVEL5, // 0x05
    LEVEL6, // 0x06
    LEVEL7, // 0x07
    LEVEL8, // 0x08
    LEVEL9, // 0x09
    LEVEL10, // 0x0A
    LEVEL11, // 0x0B

    /**
     * Unknown.
     */
    UNKNOWN;

    /**
     * @param transmitPower
     * @param model
     * @return
     */
    public static Boolean isMicroTX(TransmitPower transmitPower, String model) {
        if (model.equals(SensoroDevice.HW_A0)) {
            return false;
        } else if (model.equals(SensoroDevice.HW_B0)) {
            return false;
        } else if (model.equals(SensoroDevice.HW_C0) || model.equals(SensoroDevice.HW_C1)) {
            if (transmitPower == UNKNOWN) {
                return null;
            } else if (transmitPower.compareTo(LEVEL4) < 0) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    public static TransmitPower getTransmitPower(int index) {
        switch (index) {
            case 0:
                return TransmitPower.LEVEL0;
            case 1:
                return TransmitPower.LEVEL1;
            case 2:
                return TransmitPower.LEVEL2;
            case 3:
                return TransmitPower.LEVEL3;
            case 4:
                return TransmitPower.LEVEL4;
            case 5:
                return TransmitPower.LEVEL5;
            case 6:
                return TransmitPower.LEVEL6;
            case 7:
                return TransmitPower.LEVEL7;
            case 8:
                return TransmitPower.LEVEL8;
            case 9:
                return TransmitPower.LEVEL9;
            case 10:
                return TransmitPower.LEVEL10;
            case 11:
                return TransmitPower.LEVEL11;
            default:
                return TransmitPower.UNKNOWN;
        }
    }

    /**
     * @param transmitPower
     * @param model
     * @return
     */
    public static Integer getTransmitPowerValue(TransmitPower transmitPower, String model) {
        if (model.equals(SensoroDevice.HW_A0)) {
            switch (transmitPower) {
                case LEVEL0:
                    return -23;
                case LEVEL1:
                    return -6;
                case LEVEL2:
                    return 0;
                default:
                    return null;
            }
        } else if (model.equals(SensoroDevice.HW_B0)) {
            switch (transmitPower) {
                case LEVEL0:
                    return -30;
                case LEVEL1:
                    return -20;
                case LEVEL2:
                    return -16;
                case LEVEL3:
                    return -12;
                case LEVEL4:
                    return -8;
                case LEVEL5:
                    return -4;
                case LEVEL6:
                    return 0;
                case LEVEL7:
                    return +4;
                default:
                    return null;
            }
        } else if (model.equals(SensoroDevice.HW_C0) || model.equals(SensoroDevice.HW_C1)) {
            switch (transmitPower) {
                case LEVEL0:
                    return -30;
                case LEVEL1:
                    return -20;
                case LEVEL2:
                    return -16;
                case LEVEL3:
                    return -12;
                case LEVEL4:
                    return -30;
                case LEVEL5:
                    return -20;
                case LEVEL6:
                    return -16;
                case LEVEL7:
                    return -12;
                case LEVEL8:
                    return -8;
                case LEVEL9:
                    return -4;
                case LEVEL10:
                    return 0;
                case LEVEL11:
                    return +4;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
}