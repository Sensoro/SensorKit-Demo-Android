package com.sensoro.sensor.kit;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sensoro.sensor.kit.callback.OnDeviceUpdateObserver;
import com.sensoro.sensor.kit.connection.BluetoothLEHelper;
import com.sensoro.sensor.kit.constants.ResultCode;
import com.sensoro.sensor.kit.update.SensoroDeviceConnection;
import com.sensoro.sensor.kit.update.ble.BLEDevice;
import com.sensoro.sensor.kit.update.ble.SensoroConnectionCallback;
import com.sensoro.sensor.kit.update.ble.SensoroWriteCallback;
import com.sensoro.sensor.kit.update.service.DfuService;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static com.sensoro.sensor.kit.update.ble.CmdType.CMD_ON_DFU_MODE;

/**
 * Created by fangping on 2017/4/17.
 */

public class SensoroDeviceSession {
    private static final String TAG = SensoroDeviceSession.class.getSimpleName();
    private Context context;
    private SensoroDevice sensoroDevice;
    private ConnectionCallback callback;
    private boolean isConnected;
    private Handler handler;
    private TimeOutRunnable timeOutRunnable;
    private ConnectTimeOutRunnable connectTimeOutRunnable;
    private BluetoothLEHelper bluetoothLEHelper;
    private String password;
    private Map<Integer, WriteCallback> writeCallbackHashMap;
    private ByteBuffer byteBuffer = null;
    private int buffer_total_length = 0;
    private int buffer_data_length = 0;
    private boolean isBodyData = false;
    private volatile SensoroDeviceConnection mSensoroDeviceConnection;
    private String tempAddress;
    private ProgressDialog progressDialog;

    public SensoroDeviceSession(Context context, SensoroDevice sensoroDevice) {
        this.context = context;
        handler = new Handler();
        this.sensoroDevice = sensoroDevice;
        bluetoothLEHelper = new BluetoothLEHelper(context);
        writeCallbackHashMap = new HashMap<>();
    }

    public void startSession(String password, ConnectionCallback callback) {
        this.callback = callback;
        this.password = password;
        if (!bluetoothLEHelper.initialize()) {
            callback.onConnectFailed(ResultCode.BLUETOOTH_ERROR);
            disconnect();
        }

        if (!bluetoothLEHelper.connect(sensoroDevice.getMacAddress(), bluetoothGattCallback)) {
            callback.onConnectFailed(ResultCode.INVALID_PARAM);
            disconnect();
        }
    }

    public void write(byte[] data, WriteCallback writeCallback) {
        writeCallbackHashMap.put(CmdType.CMD_W_CFG, writeCallback);
        int data_length = data.length;
        int total_length = data_length + 3;
        byte[] total_data = new byte[total_length];
        byte[] length_data = SensoroUUID.intToByteArray(data_length + 1, 2);
        System.arraycopy(length_data, 0, total_data, 0, 2);
        byte[] version_data = SensoroUUID.intToByteArray(1, 1);
        System.arraycopy(version_data, 0, total_data, 2, 1);
        System.arraycopy(data, 0, total_data, 3, data_length);
        int resultCode = bluetoothLEHelper.writeConfiguration(total_data, CmdType.CMD_W_CFG);
        if (resultCode != ResultCode.SUCCESS) {
            writeCallback.onWriteFailure(resultCode);
        }
    }


    private void parseChangeData(BluetoothGattCharacteristic characteristic) {
        if (!isBodyData) {
            isBodyData = true;
            byte value[] = characteristic.getValue();
            //如果value 长度小于20,说明是个完整短包
            byte[] total_data = new byte[2];
            System.arraycopy(value, 0, total_data, 0, total_data.length);
            buffer_total_length = SensoroUUID.bytesToInt(total_data, 0) - 1;//数据包长度
            byte[] data = new byte[value.length - 3];//第一包数据
            System.arraycopy(value, 3, data, 0, data.length);
            byteBuffer = ByteBuffer.allocate(buffer_total_length); //减去version一个字节长度
            byteBuffer.put(data);
            if (buffer_total_length == (value.length - 3)) { //一次性数据包检验
                try {
                    callback.onNotify(data);
                    byteBuffer.clear();
                    isBodyData = false;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else {
                buffer_data_length += (value.length - 3);
            }
        } else {
            if (byteBuffer != null) {
                try {
                    byte value[] = characteristic.getValue();
                    byteBuffer.put(value);
                    buffer_data_length += value.length;
                    if (buffer_data_length == buffer_total_length) {
                        final byte array[] = byteBuffer.array();
                        callback.onNotify(array);
                        isBodyData = false;
                        byteBuffer.clear();
                        buffer_data_length = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void disconnect() {
        handler.removeCallbacks(connectTimeoutRunnable);
        bluetoothLEHelper.close();
    }

    /**
     * Close the connection of the beacon.
     */
    private boolean close() {
        return bluetoothLEHelper.close();
    }


    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt.discoverServices();
                } else {
                    callback.onConnectFailed(ResultCode.BLUETOOTH_ERROR);
                    disconnect();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServiceList = gatt.getServices();
                if (bluetoothLEHelper.checkGattServices(gattServiceList)) {
                    bluetoothLEHelper.listenNotifyChar();

                } else {
                    callback.onConnectFailed(ResultCode.SYSTEM_ERROR);
                    disconnect();
                }
            } else {
                callback.onConnectFailed(ResultCode.SYSTEM_ERROR);
                disconnect();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                int resultCode = bluetoothLEHelper.requireWritePermission(password);
                if (resultCode != ResultCode.SUCCESS) {
                    callback.onConnectFailed(resultCode);
                    disconnect();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Logger.debug(TAG, "==>onCharacteristicWrite");
            if (characteristic.getUuid().equals(BluetoothLEHelper.GattInfo.SENSORO_AUTHORIZATION_CHAR_UUID)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    handler.removeCallbacks(connectTimeoutRunnable);
                    callback.onConnectSuccess();
                }
            }
            // flow write
            if (characteristic.getUuid().equals(BluetoothLEHelper.GattInfo.SENSORO_SENSOR_WRITE_UUID)) {
                Logger.debug(TAG, "==>onCharacteristicWrite success");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (bluetoothLEHelper.getSendPacketNumber() == bluetoothLEHelper.getWritePackets().size()) {
                        writeCallbackHashMap.get(CmdType.CMD_W_CFG).onWriteSuccess();
                    }
                    bluetoothLEHelper.sendPacket(characteristic);
                } else {
                    Logger.debug(TAG, "==>onCharacteristicWrite failure" + status);
                    // failure
                    writeCallbackHashMap.get(CmdType.CMD_W_CFG).onWriteFailure(ResultCode.SYSTEM_ERROR);
                    bluetoothLEHelper.resetSendPacket();
                }
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Logger.debug(TAG, "==>onCharacteristicChanged");
            if (characteristic.getUuid().equals(BluetoothLEHelper.GattInfo.SENSORO_SENSOR_INDICATE_UUID)) {
                parseChangeData(characteristic);
            }
        }
    };

    public interface ConnectionCallback {
        void onConnectFailed(int resultCode);

        void onConnectSuccess();

        void onNotify(byte[] data);
    }

    public interface WriteCallback {
        void onWriteSuccess();

        void onWriteFailure(int errorCode);
    }

    private Runnable connectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            callback.onConnectFailed(ResultCode.TASK_TIME_OUT);
            disconnect();
        }
    };

    class TimeOutRunnable implements Runnable {
        @Override
        public void run() {
            if (!isConnected) {
                close();
                callback.onConnectFailed(ResultCode.TASK_TIME_OUT);
                if (Logger.DEBUG) {
                    Log.d(TAG, "TimeOutRunnable---callback connect failure:connect time out");
                }
            }
        }
    }

    class ConnectTimeOutRunnable implements Runnable {
        @Override
        public void run() {
            if (!isConnected) {
                close();
                if (Logger.DEBUG) {
                    Log.d(TAG, "ConnectTimeOutRunnable---callback connect failure:connect time out");
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //连接回调
    private final SensoroConnectionCallback mSensoroConnectionCallback = new SensoroConnectionCallback() {
        @Override
        public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {
            Log.e(TAG, "onConnectedSuccess: sensoroDevice ------" + sensoroDevice.toString());
            //DFU模式直接连接
            if (sensoroDevice.isDfu || cmd == CMD_ON_DFU_MODE) {
//                mSensoroDeviceConnection.disconnect();
                sensoroDevice.setDfu(true);
                dfuStart();
            } else {
                mSensoroDeviceConnection.writeCmd(mWriteCallback);
            }

        }

        @Override
        public void onConnectedFailure(final int errorCode) {
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onFailed(sensoroDevice.getMacAddress(), "连接失败:errorCode = " +
                                errorCode, null);
                    }
                });
            }
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "onDisconnected: mSensoroDeviceConnection.connect(pwd, mSensoroConnectionCallback);----断开！！");
//            if (mOnDeviceUpdateObserver != null) {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mOnDeviceUpdateObserver.onDisconnecting();
//                    }
//                });
//            }
        }
    };
    //写入会掉
    private final SensoroWriteCallback mWriteCallback = new SensoroWriteCallback() {
        @Override
        public void onWriteSuccess(Object o, int cmd) {
            mSensoroDeviceConnection.disconnect();
            sensoroDevice.setDfu(true);
            dfuStart();
        }

        @Override
        public void onWriteFailure(int errorCode, int cmd) {

        }

    };

    //升级接口
    public void startUpdate(String updateFilePath, String pwd, final OnDeviceUpdateObserver onDeviceUpdateObserver) {
        mOnDeviceUpdateObserver = onDeviceUpdateObserver;
        mTempUpdateFilePath = updateFilePath;
        mSensoroDeviceConnection = new SensoroDeviceConnection(context, sensoroDevice.getMacAddress());
        try {
            mSensoroDeviceConnection.connect(pwd, mSensoroConnectionCallback);
        } catch (final Exception e) {
            e.printStackTrace();
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mOnDeviceUpdateObserver != null) {
                        mOnDeviceUpdateObserver.onFailed(sensoroDevice.getMacAddress(), "startUpdate抛出异常--" + e
                                .getMessage(), e);
                    }
                }
            });
        }
    }

    //生命周期方法onresume
    public void onSessionResume() {
        DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener);
    }

    //生命周期方法onpause
    public void onSessonPause() {
        DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener);
    }

    //DFU监听
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Log.e(TAG, "DFU---onDeviceConnecting: deviceAddress = " + deviceAddress);
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.e(TAG, "DFU----开始连接DFU设备：onDeviceConnected: deviceAddress = " + deviceAddress);
//            if (mOnDeviceUpdateObserver != null) {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mOnDeviceUpdateObserver.onDisconnecting();
//                    }
//                });
//            }
        }

        //准备阶段
        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            Log.e(TAG, "DFU--onDfuProcessStarting: deviceAddress= " + deviceAddress);
        }

        //等待传输固件
        @Override
        public void onDfuProcessStarted(final String deviceAddress) {
            Log.e(TAG, "DFU---onDfuProcessStarted: deviceAddress = " + deviceAddress);
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onEnteringDFU(deviceAddress, mTempUpdateFilePath, "正在进入DFU");
                    }
                });
            }
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.e(TAG, "DFU--onEnablingDfuMode: deviceAddress = " + deviceAddress);
        }

        //进度更新
        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float
                avgSpeed, final int
                                              currentPart, final int partsTotal) {
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onDFUTransfering(deviceAddress, percent, speed, avgSpeed,
                                currentPart, partsTotal, "正在传输数据");
                    }
                });
            }
        }

        //校验文件
        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onUpdateValidating(deviceAddress, "正在校验文件");
                    }
                });
            }
            Log.e(TAG, "DFU--onFirmwareValidating: deviceAddress = " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.e(TAG, "DFU---onDeviceDisconnecting: deviceAddress = " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onDisconnecting();
                    }
                });
            }
        }

        //传输完成
        @Override
        public void onDfuCompleted(final String deviceAddress) {
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onUpdateCompleted(mTempUpdateFilePath, deviceAddress, "升级完成！");
                    }
                });
            }
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.e(TAG, "DFU--onDfuAborted: deviceAddress = " + deviceAddress);
        }

        @Override
        public void onError(final String deviceAddress, final int error, int errorType, final String message) {
            Log.e(TAG, "DFU--onError: deviceAddress = " + deviceAddress);
            if (mOnDeviceUpdateObserver != null) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnDeviceUpdateObserver.onFailed(deviceAddress, "错误信息：errorCode = " + error + ",errotType = "
                                + error + ",错误信息= " + message, null);
                    }
                });
            }
        }
    };

    private String mTempUpdateFilePath = "";

    private void dfuStart() {
        String macAddress = sensoroDevice.getMacAddress();
        Log.e(TAG, "dfuStart: address = " + macAddress);
        DfuServiceInitiator initiator = new DfuServiceInitiator(sensoroDevice.getMacAddress())
                .setDisableNotification(true)
                .setZip(mTempUpdateFilePath);
        initiator.start(context, DfuService.class);

    }

    //升级监听
    private OnDeviceUpdateObserver mOnDeviceUpdateObserver;

    //主线程切换
    private void runOnMainThread(Runnable task) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task.run();
        } else {
            mainHandler.post(task);
        }
    }

    private final Handler mainHandler = new Handler(Looper.getMainLooper());


}
