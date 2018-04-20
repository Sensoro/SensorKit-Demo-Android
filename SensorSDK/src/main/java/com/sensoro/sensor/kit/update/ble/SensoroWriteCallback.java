package com.sensoro.sensor.kit.update.ble;

/**
 * Created by sensoro on 17/5/4.
 */

public interface SensoroWriteCallback {
    void onWriteSuccess(Object o, int cmd);

    void onWriteFailure(int errorCode, int cmd);

}
