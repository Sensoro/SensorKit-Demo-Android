package com.sensoro.sensordemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sensoro.sensor.kit.SensoroDevice;
import com.sensoro.sensor.kit.SensoroDeviceSession;
import com.sensoro.sensor.kit.SensoroUtils;

import static com.sensoro.sensordemo.MainActivity.SENSORO_DEVICE;

/**
 * Created by fangping on 2017/4/18.
 */

public class WriteActivity extends Activity implements SensoroDeviceSession.ConnectionCallback, SensoroDeviceSession.WriteCallback{
    SensoroDeviceSession sensoroDeviceSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        initWidget();
    }

    public void initWidget() {
        final SensoroDevice sensoroDevice = this.getIntent().getParcelableExtra(SENSORO_DEVICE);
        Button connectBtn = (Button) findViewById(R.id.btn_connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensoroDeviceSession = new SensoroDeviceSession(WriteActivity.this, sensoroDevice);
                sensoroDeviceSession.startSession("ePa6jc7MrY.5X[}}", WriteActivity.this);
            }
        });
        final EditText editText = (EditText) findViewById(R.id.et_input);

        Button writeBtn = (Button) findViewById(R.id.btn_write);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = editText.getText().toString();
//                String hexString = str2HexStr(str);
                byte []data = SensoroUtils.HexString2Bytes(str);
                if (sensoroDeviceSession != null) {
                    sensoroDeviceSession.write(data, WriteActivity.this);
                }
            }
        });
    }

//    public void write(String str){
//        byte []data = SensoroUtils.HexString2Bytes(str);
//        if (sensoroDeviceSession != null) {
//            sensoroDeviceSession.write(data, new SensoroDeviceSession.WriteCallback() {
//                @Override
//                public void onWriteSuccess() {
//
//                }
//
//                @Override
//                public void onWriteFailure(int i) {
//
//                }
//            });
//        }
//    }
//
//    public void startSession() {
//        SensoroDevice sensoroDevice = this.getIntent().getParcelableExtra("sensoroDevice");
//        sensoroDeviceSession = new SensoroDeviceSession(this, sensoroDevice);
//        sensoroDeviceSession.startSession("ePa6jc7MrY.5X[}}", new SensoroDeviceSession.ConnectionCallback() {
//            @Override
//            public void onConnectFailed(int i) {
//
//            }
//
//            @Override
//            public void onConnectSuccess() {
//
//            }
//
//            @Override
//            public void onNotify(byte[] bytes) {
//
//            }
//        });
//    }
    public static String str2HexStr(String str)
    {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensoroDeviceSession != null) {
            sensoroDeviceSession.disconnect();
        }
    }

    @Override
    public void onConnectFailed(int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WriteActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WriteActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotify(byte[] bytes) {
        System.out.println("==>");
    }

    @Override
    public void onWriteSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WriteActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onWriteFailure(int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WriteActivity.this, "写入失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
