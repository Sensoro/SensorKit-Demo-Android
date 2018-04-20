package com.sensoro.sensor.kit.update.ble.constants;

import java.io.Serializable;

/**
 * The advertising interval enum of the beacon.
 */
public enum AdvertisingInterval implements Serializable {
    /**
     * The advertising interval is 100 ms.
     */
    ADVERTISING_INTERVAL_100,    //0x00 100ms
    /**
     * The advertising interval is 152.5 ms.
     */
    ADVERTISING_INTERVAL_152_5,  //0x01 152.5ms
    /**
     * The advertising interval is 211.25 ms.
     */
    ADVERTISING_INTERVAL_211_25,    //0x02 211.25ms
    /**
     * The advertising interval is 318.75 ms.
     */
    ADVERTISING_INTERVAL_318_75,    //0x03 318.75ms
    /**
     * The advertising interval is 417.5 ms.
     */
    ADVERTISING_INTERVAL_417_5, //0x04 417.5ms
    /**
     * The advertising interval is 546.25 ms.
     */
    ADVERTISING_INTERVAL_546_25, //0x05 546.25ms
    /**
     * The advertising interval is 760 ms.
     */
    ADVERTISING_INTERVAL_760,    //0x06 760ms
    /**
     * The advertising interval is 852.5 ms.
     */
    ADVERTISING_INTERVAL_852_5,   //0x07 852.5ms
    /**
     * The advertising interval is 1022.5 ms.
     */
    ADVERTISING_INTERVAL_1022_5,    //0x08 1022.5ms
    /**
     * The advertising interval is 1285 ms.
     */
    ADVERTISING_INTERVAL_1285,  //0x09 1285ms

    /**
     * Unknown.
     */
    UNKNOWN;

    /**
     * Get the millisecond of AdvertisingInterval.</br>
     *
     * @param advertisingInterval
     * @return
     */
    public static Double getAdvertisingIntervalValue(AdvertisingInterval advertisingInterval) {
        switch (advertisingInterval) {
            case ADVERTISING_INTERVAL_100:
                return 100.0;
            case ADVERTISING_INTERVAL_152_5:
                return 152.5;
            case ADVERTISING_INTERVAL_211_25:
                return 211.25;
            case ADVERTISING_INTERVAL_318_75:
                return 318.75;
            case ADVERTISING_INTERVAL_417_5:
                return 417.5;
            case ADVERTISING_INTERVAL_546_25:
                return 546.25;
            case ADVERTISING_INTERVAL_760:
                return 760.0;
            case ADVERTISING_INTERVAL_852_5:
                return 852.5;
            case ADVERTISING_INTERVAL_1022_5:
                return 1022.5;
            case ADVERTISING_INTERVAL_1285:
                return 1285.0;
            default:
                return null;
        }
    }

    public static AdvertisingInterval getAdvertisingInterval(int index) {
        switch (index) {
            case 0:
                return AdvertisingInterval.ADVERTISING_INTERVAL_100;
            case 1:
                return AdvertisingInterval.ADVERTISING_INTERVAL_152_5;
            case 2:
                return AdvertisingInterval.ADVERTISING_INTERVAL_211_25;
            case 3:
                return AdvertisingInterval.ADVERTISING_INTERVAL_318_75;
            case 4:
                return AdvertisingInterval.ADVERTISING_INTERVAL_417_5;
            case 5:
                return AdvertisingInterval.ADVERTISING_INTERVAL_546_25;
            case 6:
                return AdvertisingInterval.ADVERTISING_INTERVAL_760;
            case 7:
                return AdvertisingInterval.ADVERTISING_INTERVAL_852_5;
            case 8:
                return AdvertisingInterval.ADVERTISING_INTERVAL_1022_5;
            case 9:
                return AdvertisingInterval.ADVERTISING_INTERVAL_1285;
            default:
                return AdvertisingInterval.UNKNOWN;
        }
    }
}
