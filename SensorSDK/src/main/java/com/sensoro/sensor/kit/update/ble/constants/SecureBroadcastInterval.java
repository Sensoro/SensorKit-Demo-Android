package com.sensoro.sensor.kit.update.ble.constants;

import java.io.Serializable;

/**
 * The secure broadcast interval.
 */
public enum SecureBroadcastInterval implements Serializable {
    /**
     * Disable secure broadcast.
     */
    DISABLED, // 0
    /**
     * The secure broadcast interval is 5 seconds.
     */
    SECURE_BROADCAST_INTERVAL_5_SECONDS, // 5
    /**
     * The secure broadcast interval is 1 minute.
     */
    SECURE_BROADCAST_INTERVAL_1_MINTE, // 60
    /**
     * The secure broadcast interval is 1 hour.
     */
    SECURE_BROADCAST_INTERVAL_1_HONR, // 60*60
    /**
     * The secure broadcast interval is 1 day.
     */
    SECURE_BROADCAST_INTERVAL_1_DAY, // 60*60*24
    /**
     * The secure broadcast interval is 7 days.
     */
    SECURE_BROADCAST_INTERVAL_7_DAYS, // 60*60*24*7
    /**
     * The secure broadcast interval is 7 days.
     */
    SECURE_BROADCAST_INTERVAL_30_DAYS, // 60*60*24*30
    /**
     * Unknown.
     */
    UNKNOWN;

    private static final int FIVE_SECONDS = 5;
    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = 3600;  // 60*60
    private static final int ONE_DAY = 86400;  // 60*60*24
    private static final int SEVEN_DAYS = 604800;  // 60*60*24*7
    private static final int THIRTY_DAYS = 2592000;  // 60*60*24*30

    public static SecureBroadcastInterval getSecureBroadcastInterval(int secureBroadcastIntervalInt) {
        switch (secureBroadcastIntervalInt) {
            case 0:
                return SecureBroadcastInterval.DISABLED;
            case FIVE_SECONDS:
                return SecureBroadcastInterval.SECURE_BROADCAST_INTERVAL_5_SECONDS;
            case ONE_MINUTE:
                return SecureBroadcastInterval.SECURE_BROADCAST_INTERVAL_1_MINTE;
            case ONE_HOUR:
                return SecureBroadcastInterval.SECURE_BROADCAST_INTERVAL_1_HONR;
            case ONE_DAY:
                return SecureBroadcastInterval.SECURE_BROADCAST_INTERVAL_1_DAY;
            case SEVEN_DAYS:
                return SecureBroadcastInterval.SECURE_BROADCAST_INTERVAL_7_DAYS;
            case THIRTY_DAYS:
                return SecureBroadcastInterval.SECURE_BROADCAST_INTERVAL_30_DAYS;
            default:
                return SecureBroadcastInterval.UNKNOWN;
        }
    }
}
