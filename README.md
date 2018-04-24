# Android Sensor SDK 

## Step By Step

### 1.新建工程
Android Studio配置

将sensoro-sensor-kit.jar包放入道libs文件夹下,然后在当前工程下的build.gradle文件配置项中的dependencies新增内容,，如下compile files('libs/sensoro-sensor-kit.jar')

Eclipse配置

将sensoro-sensor-kit.jar包放入道libs文件夹下,右击工程propeties,选择Java build Path，在Library选项中添加sensoro-sensor-kit依赖关系

### 2.Android Manifest文件说明
在permission节点下新增以下权限和功能，以下权限和功能是必选项 

	<uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
添加service,如下

    <service android:name="com.sensoro.sensor.kit.SensoroDeviceService"></service>
    <service android:name="com.sensoro.sensor.kit.IntentProcessorService"></service>
    <service
            android:name="com.sensoro.sensor.kit.update.service.DfuService"
            android:exported="true" />
添加build.gradle依赖,如下

    compile 'no.nordicsemi.android:dfu:1.0.0'
    compile group: 'com.google.protobuf', name: 'protobuf-java', version: '2.5.0'
### 3.代码调用示例说明

1.sdk 核心功能调用示例

       SensoroDeviceManager sensoroDeviceManager = SensoroDeviceManager.getInstance(this);
        try {
            sensoroDeviceManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sensoroDeviceManager.setSensoroDeviceListener(new SensoroDeviceListener<SensoroDevice>() {
            @Override
            public void onNewDevice(SensoroDevice sensoroDevice) {//当发现新设备的时候该函数会被回调

            }

            @Override
            public void onGoneDevice(SensoroDevice sensoroDevice) {//设备消失的情况下，该函数被回调

            }

            @Override
            public void onUpdateDevices(final ArrayList<SensoroDevice> arrayList) {//定期回调数据更新函数
            }

        });

说明：SensoroDeviceManager 是传感器设备管理类，负责处理发现设备和设备消失以及设备信息更新功能，该类是单例类，可通过getInstance方法获得该对象
SensoroDeviceListener 用于回调通知发现设备，设备消息，和设备更新

### 4. 透传实现
1.启动任务

        SensoroDevice sensoroDevice = this.getIntent().getParcelableExtra("sensoroDevice");
        sensoroDeviceSession = new SensoroDeviceSession(this, sensoroDevice);
        sensoroDeviceSession.startSession("password", new SensoroDeviceSession.ConnectionCallback() {
            @Override
            public void onConnectFailed(int i) {//连接传感器失败
                
            }

            @Override
            public void onConnectSuccess() {//连接传感器成功

            }

            @Override
            public void onNotify(byte[] bytes) {//数据透传回调

            }
        });
2.写入数据

        byte []data = SensoroUtils.HexString2Bytes(str);
        if (sensoroDeviceSession != null) {
            sensoroDeviceSession.write(data, new 
            SensoroDeviceSession.WriteCallback() {
                @Override
                public void onWriteSuccess() {//数据写入成功
                    
                }

                @Override
                public void onWriteFailure(int i) {//数据写入失败

                }
            });
        }       
### 5. 设备升级说明
1.创建对象

        //参数1：上下文
        //参数2：SensoroDevice 对象（扫描获得）
        mSensoroDeviceSession = new SensoroDeviceSession(this.getApplicationContext(), mSensoroDevice);
        
  2.开始升级

        //参数1:升级文件路径
        //参数2：密码
        //参数3：监听
        mSensoroDeviceSession.startUpdate(path, "", new OnDeviceUpdateObserver() {
            @Override
            public void onEnteringDFU(String s, String s1, String s2) {
                loge("正在进入DFU-->>" + s + ",s1 = " + s1 + ",s2 = " + s2);
            }

            @Override
            public void onUpdateCompleted(String s, String s1, String s2) {
                loge("升级完成-->" + s + ",s1 = " + s1 + ",s2 = " + s2);
            }

            @Override
            public void onDFUTransfering(String s, int i, float v, float v1, int i1, int i2, String s1) {
                loge("onDFUTransfering==========s = " + s + ",i = " + i + ",v = " + v + ",v1 = " + v1 + ",i1 = " + i1
                        + ",i2 = " + i2 + ",s1 = " + s1);
            }

            @Override
            public void onUpdateValidating(String s, String s1) {
                loge("检验文件：onUpdateValidating=====" + s + "s1 = " + s1);
            }

            @Override
            public void onUpdateTimeout(int i, Object o, String s) {
                loge("超时");
            }

            @Override
            public void onDisconnecting() {
                loge("断开设备连接");
            }


            @Override
            public void onFailed(String s, String s1, Throwable throwable) {
                loge("升级失败======" + s + ",s1 = " + s1 + ",msg = " + (throwable == null ? "e 为空" : throwable
                        .getMessage()));
            }
        });;
        }
1.添加生命周期方法

    /**
     * 加入生命周期方法onSessionResume！！！
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensoroDeviceSession.onSessionResume();
    }

    /**
     * 加入生命周期方法onSessonPause！！！
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensoroDeviceSession.onSessonPause();
    }
### 6. 传感器设备对象说明
说明：SensoroDevice 为传感器设备对象，以下为对象属性

     serialNumber ---String; // SN
     macAddress---String; // MAC
     hardwareVersion---String;//硬件版本号
     firmwareVersion---String;//固件版本号
     batteryLevel---Integer;// 剩余电量
     temperature---Float;// 温度
     light----Float; // 光线照度
     humidity---Integer;//湿度
     accelerometerCount---Integer; // 加速度计数器
     rssi---int;
     customize----byte[];//自定义数据
     drip---Integer;//滴漏
     co---Float;//一氧化碳
     co2---Float;//二氧化碳
     no2---Float;//二氧化氮
     methane---Float;//甲烷
     lpg---Float;液化石油气
     pm1---Float;
     pm25---Float;//PM2.5
     pm10---Float;
     coverstatus---Integer;//井盖状态
     level---Float;//液位
     isDfu---boolean;//是否是DFU模式;



## 例子代码

[Github 代码](https://github.com/Sensoro/SensorKit-Demo-Android)

适用于Android studio

##修订历史
日期 | 版本 | 修订人 | 内容
---|---|---|---
2016-07-27|1.0|Will | 初始内容
2017-04-19|1.2|Will | 增加透传功能说明，及传感器支持
2018-04-19|1.3|ddong1031 | 增加升级模块和新传感器支持





















