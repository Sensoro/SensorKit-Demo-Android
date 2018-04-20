package com.sensoro.sensor.kit.update.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.sensoro.sensor.kit.Utils;

import java.util.List;

/**
 * Created by Sensoro on 15/6/2.
 * Compatible for every android version.
 */
public abstract class BLEScanner {
    private static final String TAG = BLEScanner.class.getSimpleName();
    private static final long DEFAULT_SCAN_PERIOD = 1000;
    private static final long DEFAULT_BETWEEN_SCAN_PERIOD = 0;

    protected Context context;
    protected BLEScanCallback bleScanCallback;
    protected BluetoothCrashResolver bluetoothCrashResolver;

    private Handler handler = new Handler();
    private HandlerThread handlerThread;
    private BluetoothAdapter bluetoothAdapter;
    private boolean scannerEnable;
    private boolean scanCycleStarted;

    private volatile long scanPeriod = DEFAULT_SCAN_PERIOD;
    private volatile long betweenScanPeriod = DEFAULT_BETWEEN_SCAN_PERIOD;

    protected BLEScanner(Context context, BLEScanCallback bleScanCallback) {
        Utils.checkNotNull(context, "context is null");
        Utils.checkNotNull(bleScanCallback, "bleScanCallback is null");
//        handlerThread = new HandlerThread("BlueScanner Thread");
//        handlerThread.start();
//        handler = new Handler(handlerThread.getLooper());
        this.context = context;
        this.bleScanCallback = bleScanCallback;
        bluetoothCrashResolver = new BluetoothCrashResolver(context);
    }

    public static BLEScanner createScanner(Context context, BLEScanCallback bleScanCallback) {
        boolean useAndroidLScanner;
        if (android.os.Build.VERSION.SDK_INT < 18) {
            Log.d(TAG, "Not supported prior to API 18.");
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d(TAG, "This is not Android 5.0.  We are using old scanning APIs");
            useAndroidLScanner = false;
        } else {
            Log.d(TAG, "This Android 5.0.  We are using new scanning APIs");
            useAndroidLScanner = true;
        }

        if (useAndroidLScanner) {
            return new BLEScannerForLollipop(context, bleScanCallback);
        } else {
            return new BLEScannerForJellyBean(context, bleScanCallback);
        }
    }

    public void start() {
        Log.d(TAG, "scanner start");
        scannerEnable = true;
        if (!scanCycleStarted) {
            Log.d(TAG, "scanning not start,remove cycle start,start scan");
            handler.removeCallbacks(scanCycleStartRunnanle);
            scanLeDevice(true);
        } else {
            Log.d(TAG, "scanning already started");
        }
    }

    public void stop() {
        Log.d(TAG, "scanner stop");
        scannerEnable = false;
        if (scanCycleStarted) {
            Log.d(TAG, "scanning start,remove cycle stop,stop scan");
            handler.removeCallbacks(scanCycleStopRunnanle);
            scanLeDevice(false);
        } else {
            Log.d(TAG, "scanning already stop,remove cycle start");
            handler.removeCallbacks(scanCycleStartRunnanle);
        }
    }

    public void setScanBLEFilters(List<ScanBLEFilter> scanBLEFilters) {
        setScanFilters(scanBLEFilters);
    }

    public void setScanPeriod(long periodMills) {
        scanPeriod = periodMills;
    }

    public void setBetweenScanPeriod(long periodMills) {
        betweenScanPeriod = periodMills;
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Log.d(TAG, "Failed to construct a BluetoothAdapter");
            }
        }
        return bluetoothAdapter;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.d(TAG, "scanLeDevice true: starting a new scan cycle");
            if (!scanCycleStarted) {
                scanCycleStarted = true;
                if (bluetoothCrashResolver != null && bluetoothCrashResolver.isRecoveryInProgress()) {
                    Log.d(TAG, "Skipping scan because crash recovery is in progress.");
                } else {
                    if (scannerEnable) {
//                        Log.d(TAG, "scanner enable - start san");

                        startScan();
//                        Log.d(TAG, "scanner enable - schedule stop scan cycle");
                        handler.postDelayed(scanCycleStopRunnanle, scanPeriod);
                    } else {
                        Log.d(TAG, "scanner not enable, unnecessary to scan");
                    }
                }
            } else {
                Log.d(TAG, "already scanning");
            }
        } else {
//            Log.d(TAG, "scanLeDevice false: disabling scan");
            stopScan();
            scanCycleStarted = false;
        }
    }

    private void scanCycleFinish() {
        Log.d(TAG, "scan cycle finish");
        bleScanCallback.onScanCycleFinish();
        if (scannerEnable) {
//            Log.d(TAG, "scanner enable - schedule start scan cycle");
            handler.postDelayed(scanCycleStartRunnanle, betweenScanPeriod);
        } else {
            Log.d(TAG, "scanner not enable - no more scan");
        }
    }

    private Runnable scanCycleStartRunnanle = new Runnable() {

        @Override
        public void run() {
            scanLeDevice(true);
        }
    };

    private Runnable scanCycleStopRunnanle = new Runnable() {

        @Override
        public void run() {
            scanLeDevice(false);
            scanCycleFinish();
        }
    };

    protected abstract void setScanFilters(List<ScanBLEFilter> scanBLEFilters);

    protected abstract void startScan();

    protected abstract void stopScan();

}
