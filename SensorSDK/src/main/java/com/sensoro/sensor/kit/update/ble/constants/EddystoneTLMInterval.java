package com.sensoro.sensor.kit.update.ble.constants;

/**
 * Created by Sensoro on 15/7/27.
 */
public enum EddystoneTLMInterval {
    /**
     * 1 : 1
     */
    EDDYSTONE_TLM_INTERVAL_ONE_TO_ONE,
    /**
     *  1 minute
     */
    EDDYSTONE_TLM_INTERVAL_ONE_TO_FIVE,
    /**
     *  1 hour
     */
    EDDYSTONE_TLM_INTERVAL_ONE_TO_TEN,
    /**
     *  1 day
     */
    EDDYSTONE_TLM_INTERVAL_ONE_TO_ONE_HUNDRED,
    /**
     *  unknowns
     */
    UNKNOWN;

    public static EddystoneTLMInterval getEddystoneTLMInterval(int index) {
        switch (index) {
            case 0:
                return EddystoneTLMInterval.EDDYSTONE_TLM_INTERVAL_ONE_TO_ONE;
            case 1:
                return EddystoneTLMInterval.EDDYSTONE_TLM_INTERVAL_ONE_TO_FIVE;
            case 2:
                return EddystoneTLMInterval.EDDYSTONE_TLM_INTERVAL_ONE_TO_TEN;
            case 3:
                return EddystoneTLMInterval.EDDYSTONE_TLM_INTERVAL_ONE_TO_ONE_HUNDRED;
            default:
                return EddystoneTLMInterval.UNKNOWN;
        }
    }

}
