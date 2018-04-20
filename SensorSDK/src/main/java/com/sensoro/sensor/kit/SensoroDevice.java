package com.sensoro.sensor.kit;

import android.os.Parcel;
import android.os.Parcelable;

import com.sensoro.sensor.kit.update.ble.BLEDevice;

import java.util.Arrays;

/**
 * Created by fangping on 2016/7/11.
 */

public class SensoroDevice extends
        BLEDevice implements Parcelable, Cloneable, Comparable {
    public Float pitchAngle;
    public Float rollAngle;
    public Float yawAngle;
    public Float gas;
    public Integer smokeStatus;
    public Float waterPressure;
    public float sf;//BL间隔
    public int workStatus;
    public int wifiStatus;
    public int ethStatus;
    public int cellularStatus;
    Float coverStatus;
    Integer leak;
    Float temperature;// 温度
    Float light; // 光线照度
    Float humidity;//湿度
    int accelerometerCount; // 加速度计数器
    byte customize[];
    Integer drip;
    Float co;
    Float co2;
    Float no2;
    Float methane;
    Float lpg;
    Float pm1;
    Float pm25;
    Float pm10;
    Float pitch = null;
    Float roll = null;
    Float yaw = null;
    Integer flame = null;
    Integer smoke = null;
    Float artificial_gas = null;
    Float water_pressure = null;
    Integer coverstatus;
    Float level;
    boolean isDfu;

    //////////
    public int getTransmitPower() {
        return transmitPower;
    }

    public void setTransmitPower(int transmitPower) {
        this.transmitPower = transmitPower;
    }

    //    @Override
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    int transmitPower;

    public int getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(int wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    public int getEthStatus() {
        return ethStatus;
    }

    public void setEthStatus(int ethStatus) {
        this.ethStatus = ethStatus;
    }

    public int getCellularStatus() {

        return cellularStatus;
    }

    public void setCellularStatus(int cellularStatus) {
        this.cellularStatus = cellularStatus;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public Float getPitchAngle() {
        return pitchAngle;
    }

    public void setPitchAngle(Float pitchAngle) {
        this.pitchAngle = pitchAngle;
    }

    public Float getRollAngle() {
        return rollAngle;
    }

    public void setRollAngle(Float rollAngle) {
        this.rollAngle = rollAngle;
    }

    public Float getYawAngle() {
        return yawAngle;
    }

    public void setYawAngle(Float yawAngle) {
        this.yawAngle = yawAngle;
    }

    public Float getGas() {
        return gas;
    }

    public void setGas(Float gas) {
        this.gas = gas;
    }

    public Integer getSmokeStatus() {
        return smokeStatus;
    }

    public void setSmokeStatus(Integer smokeStatus) {
        this.smokeStatus = smokeStatus;
    }

    public Float getWaterPressure() {
        return waterPressure;
    }

    public void setWaterPressure(Float waterPressure) {
        this.waterPressure = waterPressure;
    }

    public float getSf() {
        return sf;
    }

    public void setSf(float sf) {
        this.sf = sf;
    }

    public long getLastFoundTime() {
        return lastFoundTime;
    }

    public void setLastFoundTime(long lastFoundTime) {
        this.lastFoundTime = lastFoundTime;
    }

    public Integer getLeak() {
        return leak;
    }

    public void setLeak(Integer leak) {
        this.leak = leak;
    }

    public Float getCoverStatus() {
        return coverStatus;
    }

    public void setCoverStatus(Float coverStatus) {
        this.coverStatus = coverStatus;
    }


    public SensoroDevice() {
        lastFoundTime = System.currentTimeMillis();
    }

    private SensoroDevice(Parcel in) {
        super(in);
        temperature = (Float) in.readSerializable();
        light = (Float) in.readSerializable();
        humidity = (Float) in.readSerializable();
        accelerometerCount = in.readInt();
        rssi = in.readInt();
        lastFoundTime = in.readLong();
        customize = in.createByteArray();
        drip = (Integer) in.readSerializable();
        co = (Float) in.readSerializable();
        co2 = (Float) in.readSerializable();
        no2 = (Float) in.readSerializable();
        methane = (Float) in.readSerializable();
        lpg = (Float) in.readSerializable();
        pm1 = (Float) in.readSerializable();
        pm25 = (Float) in.readSerializable();
        pm10 = (Float) in.readSerializable();
        coverstatus = (Integer) in.readSerializable();
        level = (Float) in.readSerializable();
        pitch = (Float) in.readSerializable();
        roll = (Float) in.readSerializable();
        yaw = (Float) in.readSerializable();
        flame = (Integer) in.readSerializable();
        artificial_gas = (Float) in.readSerializable();
        smoke = (Integer) in.readSerializable();
        water_pressure = (Float) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(temperature);
        parcel.writeSerializable(light);
        parcel.writeSerializable(humidity);
        parcel.writeInt(accelerometerCount);
        parcel.writeInt(rssi);
        parcel.writeLong(lastFoundTime);
        parcel.writeByteArray(customize);
        parcel.writeSerializable(drip);
        parcel.writeSerializable(co);
        parcel.writeSerializable(co2);
        parcel.writeSerializable(no2);
        parcel.writeSerializable(methane);
        parcel.writeSerializable(lpg);
        parcel.writeSerializable(pm1);
        parcel.writeSerializable(pm25);
        parcel.writeSerializable(pm10);
        parcel.writeSerializable(coverstatus);
        parcel.writeSerializable(level);
        parcel.writeSerializable(pitch);
        parcel.writeSerializable(roll);
        parcel.writeSerializable(yaw);
        parcel.writeSerializable(flame);
        parcel.writeSerializable(artificial_gas);
        parcel.writeSerializable(smoke);
        parcel.writeSerializable(water_pressure);
    }

    public static final Creator<SensoroDevice> CREATOR = new Creator<SensoroDevice>() {

        @Override
        public SensoroDevice createFromParcel(Parcel parcel) {
            return new SensoroDevice(parcel);
        }

        @Override
        public SensoroDevice[] newArray(int size) {
            return new SensoroDevice[size];
        }
    };

    @Override
    public SensoroDevice clone() throws CloneNotSupportedException {
        SensoroDevice newDevice = null;
        try {
            newDevice = (SensoroDevice) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();

        }
        return newDevice;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getLight() {
        return light;
    }

    public void setLight(Float light) {
        this.light = light;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public int getAccelerometerCount() {
        return accelerometerCount;
    }

    public void setAccelerometerCount(int accelerometerCount) {
        this.accelerometerCount = accelerometerCount;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getDrip() {
        return drip;
    }

    public void setDrip(Integer drip) {
        this.drip = drip;
    }

    public Float getCo() {
        return co;
    }

    public void setCo(Float co) {
        this.co = co;
    }

    public Float getCo2() {
        return co2;
    }

    public void setCo2(Float co2) {
        this.co2 = co2;
    }

    public Float getNo2() {
        return no2;
    }

    public void setNo2(Float no2) {
        this.no2 = no2;
    }

    public Float getMethane() {
        return methane;
    }

    public void setMethane(Float methane) {
        this.methane = methane;
    }

    public Float getLpg() {
        return lpg;
    }

    public void setLpg(Float lpg) {
        this.lpg = lpg;
    }

    public Float getPm1() {
        return pm1;
    }

    public void setPm1(Float pm1) {
        this.pm1 = pm1;
    }

    public Float getPm25() {
        return pm25;
    }

    public void setPm25(Float pm25) {
        this.pm25 = pm25;
    }

    public Float getPm10() {
        return pm10;
    }

    public void setPm10(Float pm10) {
        this.pm10 = pm10;
    }

    public Integer getCoverstatus() {
        return coverstatus;
    }

    public void setCoverstatus(Integer coverstatus) {
        this.coverstatus = coverstatus;
    }

    public Float getLevel() {
        return level;
    }

    public void setLevel(Float level) {
        this.level = level;
    }

    public byte[] getCustomize() {
        return customize;
    }

    public void setCustomize(byte[] customize) {
        this.customize = customize;
    }

    public Float getPitch() {
        return pitch;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    public Float getRoll() {
        return roll;
    }

    public void setRoll(Float roll) {
        this.roll = roll;
    }

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
    }

    public Integer getFlame() {
        return flame;
    }

    public void setFlame(Integer flame) {
        this.flame = flame;
    }

    public Float getArtificial_gas() {
        return artificial_gas;
    }

    public void setArtificial_gas(Float artificial_gas) {
        this.artificial_gas = artificial_gas;
    }

    public Integer getSmoke() {
        return smoke;
    }

    public void setSmoke(Integer smoke) {
        this.smoke = smoke;
    }

    public Float getWater_pressure() {
        return water_pressure;
    }

    public void setWater_pressure(Float water_pressure) {
        this.water_pressure = water_pressure;
    }

    @Override
    public int compareTo(Object o) {
        SensoroDevice anotherDevice = (SensoroDevice) o;
        if (this.getRssi() > anotherDevice.getRssi()) {
            return -1;
        } else if (this.getRssi() == anotherDevice.getRssi()) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        return super.equals(that);
    }

    public boolean isDfu() {
        return isDfu;
    }

    public void setDfu(boolean dfu) {
        isDfu = dfu;
    }

    @Override
    public String toString() {
        return "SensoroDevice{" +
                "serialNumber='" + sn + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", hardwareVersion='" + hardwareVersion + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", temperature=" + temperature +
                ", light=" + light +
                ", humidity=" + humidity +
                ", accelerometerCount=" + accelerometerCount +
                ", rssi=" + rssi +
                ", lastFoundTime=" + lastFoundTime +
                ", customize=" + Arrays.toString(customize) +
                ", drip=" + drip +
                ", co=" + co +
                ", co2=" + co2 +
                ", no2=" + no2 +
                ", methane=" + methane +
                ", lpg=" + lpg +
                ", pm1=" + pm1 +
                ", pm25=" + pm25 +
                ", pm10=" + pm10 +
                ", pitch=" + pitch +
                ", roll=" + roll +
                ", yaw=" + yaw +
                ", flame=" + flame +
                ", smoke=" + smoke +
                ", artificial_gas=" + artificial_gas +
                ", water_pressure=" + water_pressure +
                ", coverstatus=" + coverstatus +
                ", level=" + level +
                ", isDfu=" + isDfu +
                '}';
    }
}
