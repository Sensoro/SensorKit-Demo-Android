package com.sensoro.sensordemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.sensor.kit.SensoroDevice;
import com.sensoro.sensor.kit.SensoroDeviceSession;
import com.sensoro.sensor.kit.callback.OnDeviceUpdateObserver;

import java.io.File;

import static com.sensoro.sensordemo.MainActivity.SENSORO_DEVICE;

/**
 * Created by fangping on 2016/7/14.
 */

public class DetailActivity extends Activity implements CustomPopupWindow.OnItemClickListener {

    private static final String EXTERN_DIRECTORY_NAME = "ddong1031";
    private String keyArray[] = {"SN", "RSSI", "硬件版本号", "固件版本号", "电量", "温度", "湿度", "光线", "加速度", "自定义", "滴漏", "CO",
            "CO2", "NO2", "甲烷", "液化石油气", "PM1", "PM2.5", "PM10", "井盖状态", "液位"};
    private RecyclerView mRecyclerView;
    private ProgressDialog progressDialog;
    private SensoroDevice mSensoroDevice;
    private CustomPopupWindow mPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
    }

    public void init() {
        mPop = new CustomPopupWindow(this);
        mPop.setOnItemClickListener(this);
        mSensoroDevice = this.getIntent().getParcelableExtra(SENSORO_DEVICE);
        loge("=========" + mSensoroDevice.toString());
        mRecyclerView = (RecyclerView) findViewById(R.id.detail_list);
        DeviceInfoAdapter deviceInfoAdapter = new DeviceInfoAdapter(this, keyArray);
        deviceInfoAdapter.setData(mSensoroDevice);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(deviceInfoAdapter);
        final TextView textView = (TextView) findViewById(R.id.detail_right_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPop.showAtLocation(textView, Gravity.BOTTOM |
                                Gravity.CENTER_HORIZONTAL, 0,
                        0);
            }
        });
        deviceInfoAdapter.notifyDataSetChanged();
        mSensoroDeviceSession = new SensoroDeviceSession(this.getApplicationContext(), mSensoroDevice);
    }

    @Override
    public void setOnItemClick(View v) {
        switch(v.getId()){
            case R.id.bt_trance:
                Intent intent = new Intent(this, WriteActivity.class);
                intent.putExtra(SENSORO_DEVICE,mSensoroDevice);
                startActivity(intent);
                mPop.dismiss();
                break;
            case R.id.bt_update:
                showNoticeDialog();
                mPop.dismiss();
                break;
            case R.id.bt_cancle:
                mPop.dismiss();
                break;
        }

    }

    class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoItemViewHolder> {

        private Context mContext;
        private String[] keyArrarStr;
        private SensoroDevice sensoroDevice;

        public DeviceInfoAdapter(Context context, String[] keyArrarStr) {
            this.mContext = context;
            this.keyArrarStr = keyArrarStr;
        }

        public void setData(SensoroDevice sensoroDevice) {
            this.sensoroDevice = sensoroDevice;
        }

        @Override
        public DeviceInfoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_info, null);
            return new DeviceInfoItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DeviceInfoItemViewHolder holder, int position) {
            if (sensoroDevice == null) {
                return;
            }
            holder.keyTextView.setText(keyArrarStr[position]);
            switch (position) {
                case 0:
                    holder.valueTextView.setText(sensoroDevice.getSerialNumber());
                    break;
                case 1:
                    holder.valueTextView.setText("" + sensoroDevice.getRssi());
                    break;
                case 2:
                    holder.valueTextView.setText(sensoroDevice.getHardwareVersion());
                    break;
                case 3:
                    holder.valueTextView.setText(sensoroDevice.getFirmwareVersion());
                    break;
                case 4:
                    String batteryLevel = "" + sensoroDevice.getBatteryLevel();
                    holder.valueTextView.setText(batteryLevel);
                    break;
                case 5:
                    String temperature = "" + sensoroDevice.getTemperature();
                    holder.valueTextView.setText(temperature);
                    break;
                case 6:
                    String humidity = "" + sensoroDevice.getHumidity();
                    holder.valueTextView.setText(humidity);
                    break;
                case 7:
                    String light = "" + sensoroDevice.getLight();
                    holder.valueTextView.setText(light);
                    break;
                case 8:
                    holder.valueTextView.setText(String.valueOf(sensoroDevice.getCustomize()));
                    break;
                case 9:

                    holder.valueTextView.setText(String.valueOf(sensoroDevice.getAccelerometerCount()));
                    break;
                case 10:
                    String drip = "" + sensoroDevice.getDrip();
                    holder.valueTextView.setText(drip);
                    break;
                case 11:
                    String co = "" + sensoroDevice.getCo();
                    holder.valueTextView.setText(co);
                    break;
                case 12:
                    String co2 = "" + sensoroDevice.getCo2();
                    holder.valueTextView.setText(co2);
                    break;
                case 13:
                    String no2 = "" + sensoroDevice.getNo2();
                    holder.valueTextView.setText(no2);
                    break;
                case 14:
                    String methane = "" + sensoroDevice.getMethane();
                    holder.valueTextView.setText(methane);
                    break;
                case 15:
                    String lpg = "" + sensoroDevice.getLpg();
                    holder.valueTextView.setText(lpg);
                    break;
                case 16:
                    String pm1 = "" + sensoroDevice.getPm1();
                    holder.valueTextView.setText(pm1);
                    break;
                case 17:
                    String pm25 = "" + sensoroDevice.getPm25();
                    holder.valueTextView.setText(pm25);
                    break;

                case 18:
                    String pm10 = "" + sensoroDevice.getPm10();
                    holder.valueTextView.setText(pm10);
                    break;
                case 19:
                    String coverstatus = "" + sensoroDevice.getCoverstatus();
                    holder.valueTextView.setText(coverstatus);
                    break;
                case 20:
                    String level = "" + sensoroDevice.getLevel();
                    holder.valueTextView.setText(level);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return keyArrarStr.length;
        }
    }

    class DeviceInfoItemViewHolder extends RecyclerView.ViewHolder {

        TextView keyTextView;
        TextView valueTextView;

        public DeviceInfoItemViewHolder(View itemView) {
            super(itemView);
            keyTextView = (TextView) itemView.findViewById(R.id.device_key);
            valueTextView = (TextView) itemView.findViewById(R.id.device_value);
        }
    }

    private void testUpdate() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + EXTERN_DIRECTORY_NAME;
        File file = new File(path);
        if (!file.exists()) {
            toast("----" + file.mkdir());
        }
        String fileName = "tracker_dfu_test.zip";
        path = file.getAbsolutePath() + "/" + fileName;
        mSensoroDeviceSession.startUpdate(path, "", new OnDeviceUpdateObserver() {
            @Override
            public void onEnteringDFU(String s, String s1, String s2) {
                toast("正在进入DFU-->>");
                loge("正在进入DFU-->>" + s + ",s1 = " + s1 + ",s2 = " + s2);
            }

            @Override
            public void onUpdateCompleted(String s, String s1, String s2) {
                loge("升级完成-->" + s + ",s1 = " + s1 + ",s2 = " + s2);
                toast("升级完成-->" + s + ",s1 = " + s1 + ",s2 = " + s2);
                dismissDownloadDialog();
            }

            @Override
            public void onDFUTransfering(String s, int i, float v, float v1, int i1, int i2, String s1) {
                loge("onDFUTransfering==========s = " + s + ",i = " + i + ",v = " + v + ",v1 = " + v1 + ",i1 = " + i1
                        + ",i2 = " + i2 + ",s1 = " + s1);
                updateProgress(i);
            }

            @Override
            public void onUpdateValidating(String s, String s1) {
                toast("onUpdateValidating=====" + s + "s1 = " + s1);
                loge("onUpdateValidating=====" + s + "s1 = " + s1);
            }

            @Override
            public void onUpdateTimeout(int i, Object o, String s) {
                loge("超时");
            }

            @Override
            public void onDisconnecting() {
                toast("即将断开设备连接！");
                loge("断开设备连接");
            }


            @Override
            public void onFailed(String s, String s1, Throwable throwable) {
                toast("升级失败======" + s + ",s1 = " + s1 + ",msg = " + (throwable == null ? "e 为空" : throwable
                        .getMessage()));
                loge("升级失败======" + s + ",s1 = " + s1 + ",msg = " + (throwable == null ? "e 为空" : throwable
                        .getMessage()));
                dismissDownloadDialog();
            }
        });
    }

    private SensoroDeviceSession mSensoroDeviceSession;

    private void loge(String msg) {
        Log.e("ddong1031", "loge: ---------" + msg);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 加入生命周期方法onSessionResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensoroDeviceSession.onSessionResume();
    }

    /**
     * 加入生命周期方法onSessonPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensoroDeviceSession.onSessonPause();
    }

    private void showNoticeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("即将升级")
                .setMessage("设备MacAddress：" + mSensoroDevice.getMacAddress())
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showDownloadDialog();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 显示现在进度
     */
    private void showDownloadDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("请稍后...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        testUpdate();
    }

    private void updateProgress(int progress) {
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    private void dismissDownloadDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}