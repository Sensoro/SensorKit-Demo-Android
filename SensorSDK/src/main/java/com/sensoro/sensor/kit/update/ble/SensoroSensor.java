package com.sensoro.sensor.kit.update.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sensoro on 17/1/19.
 */

public class SensoroSensor extends BLEDevice implements Parcelable, Cloneable {

    Float temperature;// 温度
    Float light; // 光线照度
    Float humidity;//湿度
    Float co;
    Float co2;
    Float no2;
    Float methane;
    Float level;

    Integer accelerometerCount; // 加速度计数器
    byte[] customize;

    Float ch20;
    Float ch4;
    Float coverStatus;
    Float so2;
    Float gps;
    Integer leak;
    Float lpg;
    Float magnetism;
    Float o3;
    Float pm1;
    Float pm25;
    Float pm10;
    Float smoke;
    Float pitchAngle;
    Float rollAngle;
    Float yawAngle;
    Integer smokeStatus;
    Float gas;
    Integer flame;
    Float waterPressure;
    Float coAlarmHigh;
    Float co2AlarmHigh;
    Float no2AlarmHigh;
    Float ch4AlarmHigh;
    Float lpgAlarmHigh;
    Float pm25AlarmHigh;
    Float pm10AlarmHigh;
    Float tempAlarmHigh;
    Float tempAlarmLow;
    Float humidityAlarmHigh;
    Float humidityAlarmLow;
    Float pitchAngleAlarmHigh;
    Float pitchAngleAlarmLow;
    Float rollAngleAlarmHigh;
    Float rollAngleAlarmLow;
    Float yawAngleAlarmHigh;
    Float yawAngleAlarmLow;
    Float waterPressureAlarmHigh;
    Float waterPressureAlarmLow;

    boolean hasAcceleration;
    boolean hasAngle;
    boolean hasBattery;
    boolean hasCh2O;
    boolean hasCh4;
    boolean hasCover;
    boolean hasGps;
    boolean hasCo;
    boolean hasCo2;
    boolean hasNo2;
    boolean hasSo2;
    boolean hasGyroscope;
    boolean hasLeak;
    boolean hasHumidity;
    boolean hasTemperature;
    boolean hasLevel;
    boolean hasLight;
    boolean hasLpg;
    boolean hasMagnetism;
    boolean hasO3;
    boolean hasPm1;
    boolean hasPm25;
    boolean hasPm10;
    boolean hasSmoke;
    boolean hasPitchAngle;
    boolean hasRollAngle;
    boolean hasYawAngle;
    boolean hasFlame;
    boolean hasGas;
    boolean hasWaterPressure;

    public long lastFoundTime;

    public SensoroSensor() {
        lastFoundTime = System.currentTimeMillis();
        hasAcceleration = false;
        hasAngle = false;
        hasBattery = false;
        hasCo = false;
        hasCo2 = false;
        hasNo2 = false;
        hasSo2 = false;
        hasGyroscope = false;
        hasLeak = false;
        hasHumidity = false;
        hasTemperature = false;
        hasLevel = false;
        hasLight = false;
        hasLpg = false;
        hasMagnetism = false;
        hasPm1 = false;
        hasPm25 = false;
        hasPm10 = false;
        hasO3 = false;
        hasSmoke = false;
        hasPitchAngle = false;
        hasRollAngle = false;
        hasYawAngle = false;
        hasFlame = false;
        hasGas = false;
        hasWaterPressure = false;
    }

    protected SensoroSensor(Parcel in) {
        super(in);
        temperature = (Float) in.readSerializable();
        light = (Float) in.readSerializable();
        humidity = (Float) in.readSerializable();
        accelerometerCount = (Integer) in.readSerializable();
        leak = (Integer) in.readSerializable();
        co = (Float) in.readSerializable();
        co2 = (Float) in.readSerializable();
        no2 = (Float) in.readSerializable();
        methane = (Float) in.readSerializable();
        lpg = (Float) in.readSerializable();
        pm1 = (Float) in.readSerializable();
        pm25 = (Float) in.readSerializable();
        pm10 = (Float) in.readSerializable();
        coverStatus = (Float) in.readSerializable();
        level = (Float) in.readSerializable();
        coAlarmHigh = (Float) in.readSerializable();
        co2AlarmHigh = (Float) in.readSerializable();
        no2AlarmHigh = (Float) in.readSerializable();
        ch4AlarmHigh = (Float) in.readSerializable();
        lpgAlarmHigh = (Float) in.readSerializable();
        pm25AlarmHigh = (Float) in.readSerializable();
        pm10AlarmHigh = (Float) in.readSerializable();
        tempAlarmHigh = (Float) in.readSerializable();
        tempAlarmLow = (Float) in.readSerializable();
        humidityAlarmHigh = (Float) in.readSerializable();
        humidityAlarmLow = (Float) in.readSerializable();
        smokeStatus = (Integer) in.readSerializable();
        flame = (Integer) in.readSerializable();
        pitchAngle = (Float) in.readSerializable();
        rollAngle = (Float) in.readSerializable();
        yawAngle = (Float) in.readSerializable();
        gas = (Float) in.readSerializable();
        waterPressure = (Float) in.readSerializable();
        pitchAngleAlarmHigh = (Float) in.readSerializable();
        pitchAngleAlarmLow = (Float) in.readSerializable();
        rollAngleAlarmHigh = (Float) in.readSerializable();
        rollAngleAlarmLow = (Float) in.readSerializable();
        yawAngleAlarmHigh = (Float) in.readSerializable();
        yawAngleAlarmLow = (Float) in.readSerializable();
        waterPressureAlarmHigh = (Float) in.readSerializable();
        waterPressureAlarmLow = (Float) in.readSerializable();
        customize = in.createByteArray();
        hasAcceleration = in.readByte() != 0;
        hasAngle = in.readByte() != 0;
        hasBattery = in.readByte() != 0;
        hasCh2O = in.readByte() != 0;
        hasCh4 = in.readByte() != 0;
        hasCo = in.readByte() != 0;
        hasCo2 = in.readByte() != 0;
        hasCover = in.readByte() != 0;
        hasGps = in.readByte() != 0;
        hasGyroscope = in.readByte() != 0;
        hasHumidity = in.readByte() != 0;
        hasLeak = in.readByte() != 0;
        hasLevel = in.readByte() != 0;
        hasLight = in.readByte() != 0;
        hasLpg = in.readByte() != 0;
        hasMagnetism = in.readByte() != 0;
        hasNo2 = in.readByte() != 0;
        hasO3 = in.readByte() != 0;
        hasPm1 = in.readByte() != 0;
        hasPm25 = in.readByte() != 0;
        hasPm10 = in.readByte() != 0;
        hasSmoke = in.readByte() != 0;
        hasSo2 = in.readByte() != 0;
        hasTemperature = in.readByte() != 0;
        hasPitchAngle = in.readByte() != 0;
        hasRollAngle = in.readByte() != 0;
        hasYawAngle = in.readByte() != 0;
        hasFlame = in.readByte() != 0;
        hasGas = in.readByte() != 0;
        hasWaterPressure = in.readByte() != 0;
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
        parcel.writeSerializable(accelerometerCount);
        parcel.writeSerializable(leak);
        parcel.writeSerializable(co);
        parcel.writeSerializable(co2);
        parcel.writeSerializable(no2);
        parcel.writeSerializable(methane);
        parcel.writeSerializable(lpg);
        parcel.writeSerializable(pm1);
        parcel.writeSerializable(pm25);
        parcel.writeSerializable(pm10);
        parcel.writeSerializable(coverStatus);
        parcel.writeSerializable(level);
        parcel.writeSerializable(coAlarmHigh);
        parcel.writeSerializable(co2AlarmHigh);
        parcel.writeSerializable(no2AlarmHigh);
        parcel.writeSerializable(ch4AlarmHigh);
        parcel.writeSerializable(lpgAlarmHigh);
        parcel.writeSerializable(pm25AlarmHigh);
        parcel.writeSerializable(pm10AlarmHigh);
        parcel.writeSerializable(tempAlarmHigh);
        parcel.writeSerializable(tempAlarmLow);
        parcel.writeSerializable(humidityAlarmHigh);
        parcel.writeSerializable(humidityAlarmLow);
        parcel.writeSerializable(smokeStatus);
        parcel.writeSerializable(flame);
        parcel.writeSerializable(pitchAngle);
        parcel.writeSerializable(rollAngle);
        parcel.writeSerializable(yawAngle);
        parcel.writeSerializable(gas);
        parcel.writeSerializable(waterPressure);
        parcel.writeSerializable(pitchAngleAlarmHigh);
        parcel.writeSerializable(pitchAngleAlarmLow);
        parcel.writeSerializable(rollAngleAlarmHigh);
        parcel.writeSerializable(rollAngleAlarmLow);
        parcel.writeSerializable(yawAngleAlarmHigh);
        parcel.writeSerializable(yawAngleAlarmLow);
        parcel.writeSerializable(waterPressureAlarmHigh);
        parcel.writeSerializable(waterPressureAlarmLow);
        parcel.writeByteArray(customize);
        parcel.writeByte((byte) (hasAcceleration ? 1 : 0));
        parcel.writeByte((byte) (hasAngle ? 1 : 0));
        parcel.writeByte((byte) (hasBattery ? 1 : 0));
        parcel.writeByte((byte) (hasCh2O ? 1 : 0));
        parcel.writeByte((byte) (hasCh4 ? 1 : 0));
        parcel.writeByte((byte) (hasCo ? 1 : 0));
        parcel.writeByte((byte) (hasCo2 ? 1 : 0));
        parcel.writeByte((byte) (hasCover ? 1 : 0));
        parcel.writeByte((byte) (hasGps ? 1 : 0));
        parcel.writeByte((byte) (hasGyroscope ? 1 : 0));
        parcel.writeByte((byte) (hasHumidity ? 1 : 0));
        parcel.writeByte((byte) (hasLeak ? 1 : 0));
        parcel.writeByte((byte) (hasLevel ? 1 : 0));
        parcel.writeByte((byte) (hasLight ? 1 : 0));
        parcel.writeByte((byte) (hasLpg ? 1 : 0));
        parcel.writeByte((byte) (hasMagnetism ? 1 : 0));
        parcel.writeByte((byte) (hasNo2 ? 1 : 0));
        parcel.writeByte((byte) (hasO3 ? 1 : 0));
        parcel.writeByte((byte) (hasPm1 ? 1 : 0));
        parcel.writeByte((byte) (hasPm25 ? 1 : 0));
        parcel.writeByte((byte) (hasPm10 ? 1 : 0));
        parcel.writeByte((byte) (hasSmoke ? 1 : 0));
        parcel.writeByte((byte) (hasSo2 ? 1 : 0));
        parcel.writeByte((byte) (hasTemperature ? 1 : 0));
        parcel.writeByte((byte) (hasPitchAngle ? 1 : 0));
        parcel.writeByte((byte) (hasRollAngle ? 1 : 0));
        parcel.writeByte((byte) (hasYawAngle ? 1 : 0));
        parcel.writeByte((byte) (hasFlame ? 1 : 0));
        parcel.writeByte((byte) (hasGas ? 1 : 0));
        parcel.writeByte((byte) (hasWaterPressure ? 1 : 0));
    }

    public static final Creator<SensoroSensor> CREATOR = new Creator<SensoroSensor>() {

        @Override
        public SensoroSensor createFromParcel(Parcel parcel) {
            return new SensoroSensor(parcel);
        }

        @Override
        public SensoroSensor[] newArray(int size) {
            return new SensoroSensor[size];
        }
    };

    @Override
    public SensoroSensor clone() throws CloneNotSupportedException {
        SensoroSensor newSensor = null;
        try {
            newSensor = (SensoroSensor) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return newSensor;
    }

    public int getAccelerometerCount() {
        return accelerometerCount;
    }

    public void setAccelerometerCount(int accelerometerCount) {
        this.accelerometerCount = accelerometerCount;
    }


    public String getSerialNumber() {
        return sn;
    }

    public void setSerialNumber(String serialNumber) {
        this.sn = serialNumber;
    }


    public void setAccelerometerCount(Integer accelerometerCount) {
        this.accelerometerCount = accelerometerCount;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getLight() {
        return light;
    }

    public void setLight(Float light) {
        this.light = light;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getCo2() {
        return co2;
    }

    public void setCo2(Float co2) {
        this.co2 = co2;
    }

    public Float getCo() {
        return co;
    }

    public void setCo(Float co) {
        this.co = co;
    }

    public Integer getLeak() {
        return leak;
    }

    public void setLeak(Integer leak) {
        this.leak = leak;
    }

    public Float getLevel() {
        return level;
    }

    public void setLevel(Float level) {
        this.level = level;
    }

    public Float getLpg() {
        return lpg;
    }

    public void setLpg(Float lpg) {
        this.lpg = lpg;
    }

    public Float getMethane() {
        return methane;
    }

    public void setMethane(Float methane) {
        this.methane = methane;
    }

    public Float getNo2() {
        return no2;
    }

    public void setNo2(Float no2) {
        this.no2 = no2;
    }

    public Float getPm10() {
        return pm10;
    }

    public void setPm10(Float pm10) {
        this.pm10 = pm10;
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

    public byte[] getCustomize() {
        return customize;
    }

    public void setCustomize(byte[] customize) {
        this.customize = customize;
    }

    public boolean hasAcceleration() {
        return hasAcceleration;
    }

    public void setHasAcceleration(boolean hasAcceleration) {
        this.hasAcceleration = hasAcceleration;
    }

    public boolean hasAngle() {
        return hasAngle;
    }

    public void setHasAngle(boolean hasAngle) {
        this.hasAngle = hasAngle;
    }

    public boolean hasBattery() {
        return hasBattery;
    }

    public void setHasBattery(boolean hasBattery) {
        this.hasBattery = hasBattery;
    }


    public boolean hasCh2O() {
        return hasCh2O;
    }

    public void setHasCh2O(boolean hasCh2O) {
        this.hasCh2O = hasCh2O;
    }

    public boolean hasCh4() {
        return hasCh4;
    }

    public void setHasCh4(boolean hasCh4) {
        this.hasCh4 = hasCh4;
    }

    public boolean hasCo2() {
        return hasCo2;
    }

    public void setHasCo2(boolean hasCo2) {
        this.hasCo2 = hasCo2;
    }

    public boolean hasCo() {
        return hasCo;
    }

    public void setHasCo(boolean hasCo) {
        this.hasCo = hasCo;
    }

    public boolean hasCover() {
        return hasCover;
    }

    public void setHasCover(boolean hasCover) {
        this.hasCover = hasCover;
    }

    public boolean hasGps() {
        return hasGps;
    }

    public void setHasGps(boolean hasGps) {
        this.hasGps = hasGps;
    }

    public boolean hasGyroscope() {
        return hasGyroscope;
    }

    public void setHasGyroscope(boolean hasGyroscope) {
        this.hasGyroscope = hasGyroscope;
    }

    public boolean hasHumidity() {
        return hasHumidity;
    }

    public void setHasHumidity(boolean hasHumidity) {
        this.hasHumidity = hasHumidity;
    }

    public boolean hasLeak() {
        return hasLeak;
    }

    public void setHasLeak(boolean hasLeak) {
        this.hasLeak = hasLeak;
    }

    public boolean hasLevel() {
        return hasLevel;
    }

    public void setHasLevel(boolean hasLevel) {
        this.hasLevel = hasLevel;
    }

    public boolean hasLight() {
        return hasLight;
    }

    public void setHasLight(boolean hasLight) {
        this.hasLight = hasLight;
    }


    public boolean hasLpg() {
        return hasLpg;
    }

    public void setHasLpg(boolean hasLpg) {
        this.hasLpg = hasLpg;
    }

    public boolean hasMagnetism() {
        return hasMagnetism;
    }

    public void setHasMagnetism(boolean hasMagnetism) {
        this.hasMagnetism = hasMagnetism;
    }

    public boolean hasNo2() {
        return hasNo2;
    }

    public void setHasNo2(boolean hasNo2) {
        this.hasNo2 = hasNo2;
    }

    public boolean hasO3() {
        return hasO3;
    }

    public void setHasO3(boolean hasO3) {
        this.hasO3 = hasO3;
    }

    public boolean hasPm10() {
        return hasPm10;
    }

    public void setHasPm10(boolean hasPm10) {
        this.hasPm10 = hasPm10;
    }

    public boolean hasPm1() {
        return hasPm1;
    }

    public void setHasPm1(boolean hasPm1) {
        this.hasPm1 = hasPm1;
    }

    public boolean hasPm25() {
        return hasPm25;
    }

    public void setHasPm25(boolean hasPm25) {
        this.hasPm25 = hasPm25;
    }

    public boolean hasSmoke() {
        return hasSmoke;
    }

    public void setHasSmoke(boolean hasSmoke) {
        this.hasSmoke = hasSmoke;
    }

    public boolean hasSo2() {
        return hasSo2;
    }

    public void setHasSo2(boolean hasSo2) {
        this.hasSo2 = hasSo2;
    }

    public boolean hasTemperature() {
        return hasTemperature;
    }

    public void setHasTemperature(boolean hasTemperature) {
        this.hasTemperature = hasTemperature;
    }

    public Float getCh20() {
        return ch20;
    }

    public void setCh20(Float ch20) {
        this.ch20 = ch20;
    }

    public Float getCh4() {
        return ch4;
    }

    public void setCh4(Float ch4) {
        this.ch4 = ch4;
    }

    public Float getCoverStatus() {
        return coverStatus;
    }

    public void setCoverStatus(Float coverStatus) {
        this.coverStatus = coverStatus;
    }

    public Float getGps() {
        return gps;
    }

    public void setGps(Float gps) {
        this.gps = gps;
    }

    public Float getMagnetism() {
        return magnetism;
    }

    public void setMagnetism(Float magnetism) {
        this.magnetism = magnetism;
    }

    public Float getO3() {
        return o3;
    }

    public void setO3(Float o3) {
        this.o3 = o3;
    }

    public Float getSmoke() {
        return smoke;
    }

    public void setSmoke(Float smoke) {
        this.smoke = smoke;
    }

    public Float getSo2() {
        return so2;
    }

    public void setSo2(Float so2) {
        this.so2 = so2;
    }

    public Float getCh4AlarmHigh() {
        return ch4AlarmHigh;
    }

    public void setCh4AlarmHigh(Float ch4AlarmHigh) {
        this.ch4AlarmHigh = ch4AlarmHigh;
    }

    public Float getCo2AlarmHigh() {
        return co2AlarmHigh;
    }

    public void setCo2AlarmHigh(Float co2AlarmHigh) {
        this.co2AlarmHigh = co2AlarmHigh;
    }

    public Float getCoAlarmHigh() {
        return coAlarmHigh;
    }

    public void setCoAlarmHigh(Float coAlarmHigh) {
        this.coAlarmHigh = coAlarmHigh;
    }

    public Float getLpgAlarmHigh() {
        return lpgAlarmHigh;
    }

    public void setLpgAlarmHigh(Float lpgAlarmHigh) {
        this.lpgAlarmHigh = lpgAlarmHigh;
    }

    public Float getNo2AlarmHigh() {
        return no2AlarmHigh;
    }

    public void setNo2AlarmHigh(Float no2AlarmHigh) {
        this.no2AlarmHigh = no2AlarmHigh;
    }

    public Float getPm10AlarmHigh() {
        return pm10AlarmHigh;
    }

    public void setPm10AlarmHigh(Float pm10AlarmHigh) {
        this.pm10AlarmHigh = pm10AlarmHigh;
    }

    public Float getPm25AlarmHigh() {
        return pm25AlarmHigh;
    }

    public void setPm25AlarmHigh(Float pm25AlarmHigh) {
        this.pm25AlarmHigh = pm25AlarmHigh;
    }

    public Float getTempAlarmHigh() {
        return tempAlarmHigh;
    }

    public void setTempAlarmHigh(Float tempAlarmHigh) {
        this.tempAlarmHigh = tempAlarmHigh;
    }

    public Float getTempAlarmLow() {
        return tempAlarmLow;
    }

    public void setTempAlarmLow(Float tempAlarmLow) {
        this.tempAlarmLow = tempAlarmLow;
    }

    public Float getHumidityAlarmHigh() {
        return humidityAlarmHigh;
    }

    public void setHumidityAlarmHigh(Float humidityAlarmHigh) {
        this.humidityAlarmHigh = humidityAlarmHigh;
    }

    public Float getHumidityAlarmLow() {
        return humidityAlarmLow;
    }

    public void setHumidityAlarmLow(Float humidityAlarmLow) {
        this.humidityAlarmLow = humidityAlarmLow;
    }

    public Integer getSmokeStatus() {
        return smokeStatus;
    }

    public void setSmokeStatus(Integer smokeStatus) {
        this.smokeStatus = smokeStatus;
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

    public Float getPitchAngleAlarmHigh() {
        return pitchAngleAlarmHigh;
    }

    public void setPitchAngleAlarmHigh(Float pitchAngleAlarmHigh) {
        this.pitchAngleAlarmHigh = pitchAngleAlarmHigh;
    }

    public Float getPitchAngleAlarmLow() {
        return pitchAngleAlarmLow;
    }

    public void setPitchAngleAlarmLow(Float pitchAngleAlarmLow) {
        this.pitchAngleAlarmLow = pitchAngleAlarmLow;
    }

    public Float getRollAngleAlarmHigh() {
        return rollAngleAlarmHigh;
    }

    public void setRollAngleAlarmHigh(Float rollAngleAlarmHigh) {
        this.rollAngleAlarmHigh = rollAngleAlarmHigh;
    }

    public Float getRollAngleAlarmLow() {
        return rollAngleAlarmLow;
    }

    public void setRollAngleAlarmLow(Float rollAngleAlarmLow) {
        this.rollAngleAlarmLow = rollAngleAlarmLow;
    }

    public Float getYawAngleAlarmHigh() {
        return yawAngleAlarmHigh;
    }

    public void setYawAngleAlarmHigh(Float yawAngleAlarmHigh) {
        this.yawAngleAlarmHigh = yawAngleAlarmHigh;
    }

    public Float getYawAngleAlarmLow() {
        return yawAngleAlarmLow;
    }

    public void setYawAngleAlarmLow(Float yawAngleAlarmLow) {
        this.yawAngleAlarmLow = yawAngleAlarmLow;
    }

    public boolean hasPitchAngle() {
        return hasPitchAngle;
    }

    public void setHasPitchAngle(boolean hasPitchAngle) {
        this.hasPitchAngle = hasPitchAngle;
    }

    public boolean hasRollAngle() {
        return hasRollAngle;
    }

    public void setHasRollAngle(boolean hasRollAngle) {
        this.hasRollAngle = hasRollAngle;
    }

    public boolean hasYawAngle() {
        return hasYawAngle;
    }

    public void setHasYawAngle(boolean hasYawAngle) {
        this.hasYawAngle = hasYawAngle;
    }

    public Integer getFlame() {
        return flame;
    }

    public void setFlame(Integer flame) {
        this.flame = flame;
    }

    public boolean hasFlame() {
        return hasFlame;
    }

    public void setHasFlame(boolean hasFlame) {
        this.hasFlame = hasFlame;
    }

    public Float getGas() {
        return gas;
    }

    public void setGas(Float gas) {
        this.gas = gas;
    }

    public Float getWaterPressure() {
        return waterPressure;
    }

    public void setWaterPressure(Float waterPressure) {
        this.waterPressure = waterPressure;
    }

    public Float getWaterPressureAlarmHigh() {
        return waterPressureAlarmHigh;
    }

    public void setWaterPressureAlarmHigh(Float waterPressureAlarmHigh) {
        this.waterPressureAlarmHigh = waterPressureAlarmHigh;
    }

    public Float getWaterPressureAlarmLow() {
        return waterPressureAlarmLow;
    }

    public void setWaterPressureAlarmLow(Float waterPressureAlarmLow) {
        this.waterPressureAlarmLow = waterPressureAlarmLow;
    }

    public boolean hasWaterPressure() {
        return hasWaterPressure;
    }

    public void setHasWaterPressure(boolean hasWaterPressure) {
        this.hasWaterPressure = hasWaterPressure;
    }
}
