package com.sensoro.sensor.kit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

/**
 * Created by fangping on 2016/7/11.
 */

public class SensoroDeviceManager {
    private static final String TAG = SensoroDeviceManager.class.getSimpleName();
    private static final int SERVICE_STATE_UNBOUND = 0;
    private static final int SERVICE_STATE_UNBINDING = 1;
    private static final int SERVICE_STATE_BOUND = 2;
    private static final int SERVICE_STATE_BINDING = 3;
    static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1100; // 默认前台device扫描时间
    static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 0; // 默认前台device扫描时间间隔
    static final long DEFAULT_BACKGROUND_SCAN_PERIOD = 1100; // 默认后台device扫描时间
    static final long DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD = 10 * 1000; // 默认后台device扫描时间间隔
    static final long DEFAULT_UPDATE_DEVICE_PERIOD = 1000; // device定时更新时间间隔

    static volatile long FOREGROUND_SCAN_PERIOD = DEFAULT_FOREGROUND_SCAN_PERIOD;
    static volatile long FOREGROUND_BETWEEN_SCAN_PERIOD = DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD;
    static volatile long BACKGROUND_SCAN_PERIOD = DEFAULT_BACKGROUND_SCAN_PERIOD;
    static volatile long BACKGROUND_BETWEEN_SCAN_PERIOD = DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD;
    static volatile long UPDATE_DEVICE_PERIOD = DEFAULT_UPDATE_DEVICE_PERIOD;
    static volatile long OUT_OF_RANGE_DELAY = 8 * 1000; // 如果在该时间间隔内没有扫描到已经发现的beacon，则认为这个beacon已经离开
    static final String BLUETOOTH_IS_NOT_ENABLED = "BluetoothIsNotEnabled";// 异常字符串蓝牙没有开启
    static final String BLUETOOTH_IS_NOT_SUPPORT = "BluetoothIsNotSupport";// 异常字符串不支持 ble
    public static final String MONITORED_DEVICE = "MONITORED_DEVICE";
    public static final String UPDATE_DEVICES = "UPDATE_DEVICES";
    private boolean isBleEnabled;// 蓝牙是否开启

    private SensoroDeviceService sensoroDeviceService = null;
    private int serviceState = SERVICE_STATE_UNBOUND;
    private static volatile SensoroDeviceManager instance = null;
    private Context mContext;
    private SensoroDeviceListener mListener ;
    private SensoroDeviceServiceBoundListener mBoundListener;
    private boolean isBackgroundMode;   // 是否开启后台模式

    private SensoroDeviceManager() {
        mBoundListener = new SensoroDeviceServiceBoundListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Object errorMessage) {

            }
        };
    }

    public static SensoroDeviceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SensoroDeviceManager.class) {
                if (instance==null){
                    instance = new SensoroDeviceManager();
                    instance.mContext = context;
                }
            }
        }
        return instance;
    }

    public void setSensoroDeviceListener(SensoroDeviceListener listener) {
        this.mListener = listener;
    }

    public SensoroDeviceListener getSensoroDeviceListener() {
        return mListener;
    }

    public void setBackgroundMode(boolean isBackgroundMode) {
        this.isBackgroundMode = isBackgroundMode;
        if (sensoroDeviceService != null) {
            sensoroDeviceService.setBackgroundMode(isBackgroundMode);
        }
    }

    protected void startService(Intent intent) throws Exception {
        if (!isBleEnabled) {
            throw new Exception(BLUETOOTH_IS_NOT_ENABLED);// 抛出蓝牙关闭异常
        }
        if (mContext != null) {
            mContext.startService(intent);
        }
    }

    public void startService() throws Exception {
        if (!isBLESuppotred()) {
            throw new Exception(BLUETOOTH_IS_NOT_SUPPORT);// 抛出蓝牙关闭异常
        }

        isBleEnabled = isBluetoothEnabled();// 获取当前蓝牙状态

        if (!isBleEnabled) {
            throw new Exception(BLUETOOTH_IS_NOT_ENABLED);// 抛出蓝牙关闭异常
        }

        bind(mBoundListener);
    }

    public void stopService() {
        unbind();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceState = SERVICE_STATE_UNBOUND;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceState = SERVICE_STATE_BOUND;
            mBoundListener.onSuccess();

            SensoroDeviceService.SensoroDeviceServiceV4Binder binder = (SensoroDeviceService.SensoroDeviceServiceV4Binder) service;
            sensoroDeviceService = binder.getService();
            if (sensoroDeviceService != null) {
                sensoroDeviceService.setBackgroundMode(isBackgroundMode);
            }
        }
    };


    protected void setUpdateDevicePeriod(long periodMills) {
        UPDATE_DEVICE_PERIOD = periodMills;
    }


    public void setForegroundScanPeriod(long periodMills) {
        FOREGROUND_SCAN_PERIOD = periodMills;
    }

    /**
     * Set the period (in milliseconds) between two scan periods in foreground
     *
     * @param periodMills Default value is 0 s(unit:ms)
     */
    public void setForegroundBetweenScanPeriod(long periodMills) {
        FOREGROUND_BETWEEN_SCAN_PERIOD = periodMills;
    }

    /**
     * set iBeaon scan period (in milliseconds) in background
     *
     * @param periodMills Default value is 10 s(unit:ms)
     */
    public void setBackgroundScanPeriod(long periodMills) {
        BACKGROUND_SCAN_PERIOD = periodMills;
    }

    /**
     * Set the period (in milliseconds) between two scan periods in background
     *
     * @param periodMills Default value is 5 min(unit:ms)
     */
    public void setBackgroundBetweenScanPeriod(long periodMills) {
        BACKGROUND_BETWEEN_SCAN_PERIOD = periodMills;
    }


    public void setOutOfRangeDelay(long delayMills) {
        OUT_OF_RANGE_DELAY = delayMills;
    }

    /**
     * Check whether the bluetooth is enabled.
     *
     * @return
     */
    public boolean isBluetoothEnabled() {
        if (mContext != null) {
            BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter.isEnabled()) {// 蓝牙开启
                return true;
            } else {// 蓝牙关闭
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Check whether the BLE is supported.
     *
     * @return
     */
    public boolean isBLESuppotred() {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (mContext != null) {
            if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private interface SensoroDeviceServiceBoundListener {
        public void onSuccess();

        public void onFailure(Object errorMessage);
    }


    private void bind(SensoroDeviceServiceBoundListener mBoundListener) {
        Logger.debug(TAG, "bind service");

        SensoroUtils.checkNotNull(mBoundListener, "mBoundListener is null");
        this.mBoundListener = mBoundListener;

        if (serviceState == SERVICE_STATE_BINDING || serviceState == SERVICE_STATE_BOUND) {
            Logger.debug(TAG, "service is binding or already bound");
            return;
        }

        if (!isBleEnabled) {
            this.mBoundListener.onFailure("bluetooth is not enabled");
            Logger.debug(TAG, "bluetooth is not enabled,start failure");
            return;
        }

        serviceState = SERVICE_STATE_BINDING;

        Intent intent = new Intent();
        intent.setClass(mContext, SensoroDeviceService.class);
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Logger.debug(TAG, "binding to service");
    }

    private void unbind() {
        Logger.debug(TAG, "unbind service");
        if (serviceState == SERVICE_STATE_BOUND) {
            serviceState = SERVICE_STATE_UNBINDING;
            mContext.unbindService(serviceConnection);
        } else {
            Logger.debug(TAG, "service is not bound:" + serviceState);
        }
    }

}
