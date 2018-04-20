package com.sensoro.sensor.kit;

import android.util.Log;

public class Logger {
    public static final String TAG = "SensoroSDK";
    public static boolean DEBUG = true;

    private static Logger singleInstance;

    public static void setEnable(boolean enable) {
        DEBUG = enable;
    }

    public static void debug(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void error(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void warn(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }
}
