package com.sensoro.sensordemo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.sensor.kit.SensoroDevice;
import com.sensoro.sensor.kit.SensoroDeviceListener;
import com.sensoro.sensor.kit.SensoroDeviceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SensoroDeviceManager sensoroDeviceManager;
    private ArrayList<SensoroDevice> deviceArrayList = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    private RecyclerView mRecycleView;
    private MyHandler myHandler = new MyHandler();
    private TextView leftTitle;
    private TextView rightTitle;
    private String filter_sn;
    public static final String SENSORO_DEVICE ="sensoro_device";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initSDK();
    }

    public boolean containsDevice(SensoroDevice sensoroDevice) {
        boolean isContains = false;
        for (int i = 0; i < deviceArrayList.size(); i++) {
            SensoroDevice tempDevice = deviceArrayList.get(i);
            if (tempDevice.getSerialNumber().equalsIgnoreCase(sensoroDevice.getSerialNumber())) {
                isContains = true;
            }
        }
        return isContains;
    }

    public void removeDevice(SensoroDevice sensoroDevice) {

        for (int i = 0; i < deviceArrayList.size(); i++) {
            SensoroDevice tempDevice = deviceArrayList.get(i);
            if (tempDevice.getSerialNumber().equalsIgnoreCase(sensoroDevice.getSerialNumber())) {
                deviceArrayList.remove(i);
            }
        }

    }

    private boolean requireLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else {
            return true;
        }
    }

    private void initSDK() {
        deviceArrayList.clear();
        sensoroDeviceManager = SensoroDeviceManager.getInstance(getApplicationContext());
        sensoroDeviceManager.setSensoroDeviceListener(new SensoroDeviceListener<SensoroDevice>() {
            @Override
            public void onNewDevice(final SensoroDevice sensoroDevice) {
                String macAddress = sensoroDevice.getMacAddress();

                String s = sensoroDevice.toString();
                Log.e("ddong1031", "onNewDevice: sensoroDevice = " + s);
                if (sensoroDevice.getSerialNumber().contains("697062")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    System.out.println("device" + sensoroDevice.getSerialNumber() + ".onNewDevice==>" + str);
                }
//                Log.e("ddong1031", "onNewDevice: macAddress = " + macAddress);
//                Log.e("ddong1031", "onNewDevice: " + sensoroDevice.getSerialNumber());
                if (!containsDevice(sensoroDevice)) {
//                    if (sensoroDevice.getSerialNumber().contains("CB70")){
                    deviceArrayList.add(sensoroDevice);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceListAdapter.notifyDataSetChanged();
                        }
                    });
//                    }

                }

            }

            @Override
            public void onGoneDevice(final SensoroDevice sensoroDevice) {
                String macAddress = sensoroDevice.getMacAddress();
                if (sensoroDevice.getSerialNumber().contains("697062")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    System.out.println("device" + sensoroDevice.getSerialNumber() + ".onGoneDevice==>" + str);
                }
//                Log.e("ddong1031", "onNewDevice: macAddress = " + macAddress);
//                Log.e("ddong1031", "onGoneDevice: " + sensoroDevice.getSerialNumber());
//                removeDevice(sensoroDevice);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        deviceArrayList.add(sensoroDevice);
//                        deviceListAdapter.notifyDataSetChanged();
//                    }
//                });
            }

            @Override
            public void onUpdateDevices(final ArrayList<SensoroDevice> arrayList) {

//                Log.e("ddong1031", "onUpdateDevices: " + arrayList.size());
                for (int i = 0; i < arrayList.size(); i++) {
                    SensoroDevice tempDevice = arrayList.get(i);
                    for (int j = 0; j < deviceArrayList.size(); j++) {
                        SensoroDevice sensoroDevice = deviceArrayList.get(j);
                        String macAddress = sensoroDevice.getMacAddress();
//                        Log.e("ddong1031", "onNewDevice: macAddress = " + macAddress);
//                        Log.e("ddong1031", "onUpdateDevices: " + sensoroDevice.getSerialNumber());
                        if (tempDevice.getSerialNumber().equalsIgnoreCase(sensoroDevice.getSerialNumber())) {
                            deviceArrayList.set(j, tempDevice);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }

        });
        try {
            sensoroDeviceManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void connect(SensoroDevice sensoroDevice) {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("connecting...");
//        progressDialog.show();
//        SensoroDeviceSession sensoroDeviceSession = new SensoroDeviceSession(this, sensoroDevice);
//        sensoroDeviceSession.startSession("ePa6jc7MrY.5X[}}", new SensoroDeviceSession.SessionCallback() {
//            @Override
//            public void onFailed(int i) {
//                System.out.println("onFailed===>" + i);
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onSuccess() {
//                System.out.println("onSuccess===>" );
//                progressDialog.dismiss();
//            }
//        });
    }


    private void initData() {
        mRecycleView = (RecyclerView) findViewById(R.id.main_list);
        deviceListAdapter = new DeviceListAdapter(this, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(SENSORO_DEVICE, deviceArrayList.get(position));
                MainActivity.this.startActivity(intent);
//                connect(deviceArrayList.get(position));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(deviceListAdapter);
        deviceListAdapter.setData(deviceArrayList);
        leftTitle = (TextView) findViewById(R.id.main_left_title);
//        leftTitle.setText(R.string.start);
        leftTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (leftTitle.getText().toString().equals("Start")) {
//                    SensoroDeviceManager.getInstance(MainActivity.this).stopService();
//                    leftTitle.setText(R.string.start);
//                } else {
//                    try {
//                        SensoroDeviceManager.getInstance(MainActivity.this).startService();
//                        leftTitle.setText(R.string.stop);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        leftTitle.setText(R.string.start);
//                    }
//                }
                showDialog();
            }
        });
        verifyStoragePermissions();
        requireLocationPermission();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    public void verifyStoragePermissions() {
        try {
            //检测是否有写的权限
            int permission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
//            this, "android.permission.WRITE_EXTERNAL_STORAGE"
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog() {
        final String sensorArray[] = new String[deviceArrayList.size() + 1];
        sensorArray[0] = "全部";
        for (int i = 0; i < deviceArrayList.size(); i++) {
            sensorArray[i + 1] = deviceArrayList.get(i).getSerialNumber();
        }
        if (sensorArray.length > 0) {
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("Sensor List").
                    setIcon(R.mipmap.ic_launcher)
                    .setItems(sensorArray, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, sensorArray[which], Toast.LENGTH_SHORT).show();
                            if (which == 0) {
                                filter_sn = null;
                                deviceListAdapter.setData(deviceArrayList);
                                deviceListAdapter.notifyDataSetChanged();
                            } else {
                                filter_sn = sensorArray[which];
                                ArrayList<SensoroDevice> tempList = new ArrayList<SensoroDevice>();
                                for (int i = 0; i < deviceArrayList.size(); i++) {
                                    if (filter_sn.equalsIgnoreCase(deviceArrayList.get(i).getSerialNumber())) {
                                        tempList.add(deviceArrayList.get(i));
                                        deviceListAdapter.setData(tempList);
                                        deviceListAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }

                        }
                    }).
                            setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).
                            create();
            alertDialog.show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensoroDeviceManager.stopService();
    }

    static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    class DeviceListAdapter extends RecyclerView.Adapter<DeviceListItemViewHolder> {

        private Context mContext;
        private ArrayList<SensoroDevice> mList;
        private RecycleViewItemClickListener listener;

        public DeviceListAdapter(Context context, RecycleViewItemClickListener listener) {
            this.mContext = context;
            this.listener = listener;
        }

        public void setData(ArrayList<SensoroDevice> list) {
            this.mList = list;
        }

        @Override
        public DeviceListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_device, null);
            return new DeviceListItemViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(DeviceListItemViewHolder holder, int position) {

            if (mList == null) {
                return;
            }
            SensoroDevice sensoroDevice = mList.get(position);
            if (sensoroDevice.getSerialNumber() == null || sensoroDevice.getSerialNumber().equals("")) {
                holder.snTextView.setText(mList.get(position).getMacAddress());
            } else {
                holder.snTextView.setText(mList.get(position).getSerialNumber());
            }
            String drip = "" + sensoroDevice.getDrip();
            String co = sensoroDevice.getCo() + "";
            String co2 = "" + sensoroDevice.getCo2();
            String no2 = "" + sensoroDevice.getNo2();
            String methane = "" + sensoroDevice.getMethane();
            String lpg = "" + sensoroDevice.getLpg();
            String pm1 = "" + sensoroDevice.getPm1();
            String pm25 = "" + sensoroDevice.getPm25();
            String pm10 = "" + sensoroDevice.getPm10();
            String cover = "" + sensoroDevice.getCoverstatus();
            String level = "" + sensoroDevice.getLevel();
            holder.dripTextView.setText("滴漏:" + drip);
            holder.coTextView.setText("一氧化碳:" + co);
            holder.co2TextView.setText("二氧化碳:" + co2);
            holder.no2TextView.setText("二氧化氮:" + no2);
            holder.methaneTextView.setText("甲烷:" + methane);
            holder.lpgTextView.setText("液化气:" + lpg);
            holder.pm1TextView.setText("PM1:" + pm1);
            holder.pm25TextView.setText("PM25:" + pm25);
            holder.pm10TextView.setText("PM10:" + pm10);
            holder.coverTextView.setText("井盖:" + cover);
            holder.levelTextView.setText("液位:" + level);


        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class DeviceListItemViewHolder extends RecyclerView.ViewHolder {

        TextView snTextView;
        TextView rssiTextView;
        TextView dripTextView;
        TextView coTextView;
        TextView co2TextView;
        TextView no2TextView;
        TextView methaneTextView;
        TextView lpgTextView;
        TextView pm1TextView;
        TextView pm25TextView;
        TextView pm10TextView;
        TextView coverTextView;
        TextView levelTextView;
        View itemView;
        RecycleViewItemClickListener itemClickListener;

        DeviceListItemViewHolder(View itemView, RecycleViewItemClickListener listener) {
            super(itemView);
            this.itemView = itemView;
            snTextView = (TextView) itemView.findViewById(R.id.device_sn);
            rssiTextView = (TextView) itemView.findViewById(R.id.device_rssi);
            dripTextView = (TextView) itemView.findViewById(R.id.device_drip);
            coTextView = (TextView) itemView.findViewById(R.id.device_co);
            co2TextView = (TextView) itemView.findViewById(R.id.device_co2);
            no2TextView = (TextView) itemView.findViewById(R.id.device_no2);
            methaneTextView = (TextView) itemView.findViewById(R.id.device_methane);
            lpgTextView = (TextView) itemView.findViewById(R.id.device_lpg);
            pm1TextView = (TextView) itemView.findViewById(R.id.device_pm1);
            pm25TextView = (TextView) itemView.findViewById(R.id.device_pm25);
            pm10TextView = (TextView) itemView.findViewById(R.id.device_pm10);
            coverTextView = (TextView) itemView.findViewById(R.id.device_cover);
            levelTextView = (TextView) itemView.findViewById(R.id.device_level);
            this.itemClickListener = listener;
            itemView.setOnClickListener(onItemClickListener);
        }

        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        };
    }

    interface RecycleViewItemClickListener {
        void onItemClick(View view, int position);
    }
}
