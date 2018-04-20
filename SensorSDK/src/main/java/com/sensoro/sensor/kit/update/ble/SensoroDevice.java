package com.sensoro.sensor.kit.update.ble;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fangping on 2016/7/25.
 */

public class SensoroDevice extends BLEDevice implements Parcelable, Cloneable {

    // hardware version in String
    public static final String HW_A0 = "A0";
    public static final String HW_B0 = "B0";
    public static final String HW_C0 = "C0";
    public static final String HW_C1 = "C1";
    // Firmware version
    public static final float FV_1_2 = 1.2f;
    int major; // major
    int minor; // minor
    String proximityUUID; // proximityUuid

    int accelerometerCount; // accelerometer count.
    int power;//功率
    float sf;//BL间隔


    String devUi;
    String appEui;
    String appKey;
    String appSkey;
    String nwkSkey;
    String password;
    String dfuInfo;
    String band;
    int devAdr;
    int loraDr;
    int loraAdr;
    int loraTxp;
    int dfuProgress;
    float loraInt;
    int bleTxp;
    float bleInt;
    int bleOnTime;
    int bleOffTime;
    int tempInterval;
    int lightInterval;
    int humidityInterval;
    int classBEnabled;
    int classBDataRate;
    int classBPeriodicity;
    Integer uploadInterval;
    Integer confirm;
    Integer activation;
    Integer delay;
    transient List<Integer> channelMaskList;
    transient int maxEirp;
    transient int sglStatus;
    transient int sglFrequency;
    transient int sglDatarate;
    byte dataVersion;
    boolean isIBeaconEnabled; // is beacon function enable.
    boolean isDfu;
    boolean hasBleInterval;
    boolean hasBleOffTime;
    boolean hasBleOnTime;
    boolean hasBleOnOff;
    boolean hasBleTxp;
    boolean hasAdr;
    boolean hasAppEui;
    boolean hasAppKey;
    boolean hasAppSkey;
    boolean hasDevAddr;
    boolean hasDevEui;
    boolean hasNwkSkey;
    boolean hasNwkAddress;
    boolean hasLoraSf;
    boolean hasDataRate;
    boolean hasActivation;
    boolean hasLoraTxp;
    boolean hasLoraInterval;
    boolean hasLoraParam;
    boolean hasBleParam;
    boolean hasAppParam;
    boolean hasConfirm;
    boolean hasUploadInterval;
    boolean hasEddyStone;
    boolean hasIbeacon;
    boolean hasSensorBroadcast;
    boolean hasSensorParam;
    boolean hasCustomPackage;
    boolean hasDelay;
    transient boolean hasMaxEirp;
    SensoroSlot slotArray[];
    SensoroSensor sensoroSensor;
//    public long lastFoundTime;
//        public String hardwareVersion;// hardware version.
//    public String firmwareVersion;// firmware version.
//    public String macAddress; // MAC
//    public int batteryLevel;// battery left
//    public int rssi;
    public SensoroDevice() {
//        lastFoundTime = System.currentTimeMillis();
        sn = null;
        major = 0;
        minor = 0;
        proximityUUID = null;
        macAddress = null;
        batteryLevel = 0;
        hardwareVersion = null;
        firmwareVersion = null;
        dfuInfo = null;
        accelerometerCount = 0;
        dfuProgress = 0;
        delay = 0;
        isIBeaconEnabled = true;
        isDfu = false;
        hasBleInterval = false;
        hasBleOffTime = false;
        hasBleOnTime = false;
        hasBleOnOff = false;
        hasBleTxp = false;
        hasAdr = false;
        hasAppEui = false;
        hasAppKey = false;
        hasAppSkey = false;
        hasDevAddr = false;
        hasDevEui = false;
        hasNwkSkey = false;
        hasNwkAddress = false;
        hasLoraSf = false;
        hasDataRate = false;
        hasActivation = false;
        hasLoraTxp = false;
        hasLoraInterval = false;
        hasLoraParam = false;
        hasBleParam = false;
        hasAppParam = false;
        hasConfirm = false;
        hasUploadInterval = false;
        hasIbeacon = false;
        hasEddyStone = false;
        hasCustomPackage = false;
        hasSensorBroadcast = false;
        hasSensorParam = false;
        hasDelay = false;
        hasMaxEirp = false;
    }

    protected SensoroDevice(Parcel in) {
        super(in);
        major = in.readInt();
        minor = in.readInt();
        proximityUUID = in.readString();
//        macAddress = in.readString();
//        batteryLevel = in.readInt();
        accelerometerCount = in.readInt();
        power = in.readInt();
        sf = in.readFloat();
//        lastFoundTime = in.readLong();
        devUi = in.readString();
        appEui = in.readString();
        appKey = in.readString();
        appSkey = in.readString();
        nwkSkey = in.readString();
        password = in.readString();
        dfuInfo = in.readString();
        devAdr = in.readInt();
        loraDr = in.readInt();
        loraAdr = in.readInt();
        loraTxp = in.readInt();
        loraInt = in.readFloat();
        bleTxp = in.readInt();
        bleInt = in.readFloat();
        bleOnTime = in.readInt();
        bleOffTime = in.readInt();
        isIBeaconEnabled = in.readByte() != 0;
        tempInterval = in.readInt();
        lightInterval = in.readInt();
        humidityInterval = in.readInt();
        isDfu = (in.readByte() != 0);
        slotArray = (SensoroSlot[]) in.readParcelableArray(SensoroSlot.class.getClassLoader());
        sensoroSensor = in.readParcelable(SensoroSensor.class.getClassLoader());
        classBEnabled = in.readInt();
        classBDataRate = in.readInt();
        classBPeriodicity = in.readInt();
        dfuProgress = in.readInt();
        dataVersion = in.readByte();
        band = in.readString();
        uploadInterval = (Integer) in.readSerializable();
        confirm = (Integer) in.readSerializable();
        activation = (Integer)in.readSerializable();
        delay = (Integer)in.readSerializable();
        hasBleInterval = in.readByte() != 0;
        hasBleOffTime = in.readByte() != 0;
        hasBleOnOff = in.readByte() != 0;
        hasBleOnTime = in.readByte() != 0;
        hasBleTxp = in.readByte() != 0;
        hasAdr = in.readByte() != 0;
        hasAppEui = in.readByte() != 0;
        hasAppKey = in.readByte() != 0;
        hasAppSkey = in.readByte() != 0;
        hasDevAddr = in.readByte() != 0;
        hasDevEui = in.readByte() != 0;
        hasNwkSkey = in.readByte() != 0;
        hasLoraTxp = in.readByte() != 0;
        hasActivation = in.readByte() != 0;
        hasAppParam = in.readByte() != 0;
        hasBleParam = in.readByte() != 0;
        hasLoraParam = in.readByte() != 0;
        hasUploadInterval = in.readByte() != 0;
        hasConfirm = in.readByte() != 0;
        hasLoraInterval = in.readByte() != 0;
        hasEddyStone = in.readByte() != 0;
        hasIbeacon = in.readByte() != 0;
        hasCustomPackage = in.readByte() != 0;
        hasSensorBroadcast = in.readByte() != 0;
        hasSensorParam = in.readByte() != 0;
        hasDelay = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(major);
        out.writeInt(minor);
        out.writeString(proximityUUID);
//        out.writeString(macAddress);
//        out.writeInt(batteryLevel);
        out.writeInt(accelerometerCount);
        out.writeInt(power);
        out.writeFloat(sf);
//        out.writeLong(lastFoundTime);
        out.writeString(devUi);
        out.writeString(appEui);
        out.writeString(appKey);
        out.writeString(appSkey);
        out.writeString(nwkSkey);
        out.writeString(password);
        out.writeString(dfuInfo);
        out.writeInt(devAdr);
        out.writeInt(loraDr);
        out.writeInt(loraAdr);
        out.writeInt(loraTxp);
        out.writeFloat(loraInt);
        out.writeInt(bleTxp);
        out.writeFloat(bleInt);
        out.writeInt(bleOnTime);
        out.writeInt(bleOffTime);
        out.writeByte((byte) (isIBeaconEnabled ? 1 : 0));
        out.writeInt(tempInterval);
        out.writeInt(lightInterval);
        out.writeInt(humidityInterval);
        out.writeByte((byte) (isDfu ? 1 : 0));
        out.writeParcelableArray(slotArray, flags);
        out.writeParcelable(sensoroSensor, flags);
        out.writeInt(classBEnabled);
        out.writeInt(classBDataRate);
        out.writeInt(classBPeriodicity);
        out.writeInt(dfuProgress);
        out.writeByte(dataVersion);
        out.writeString(band);
        out.writeSerializable(uploadInterval);
        out.writeSerializable(confirm);
        out.writeSerializable(activation);
        out.writeSerializable(delay);
        out.writeByte((byte) (hasBleInterval ? 1 : 0));
        out.writeByte((byte) (hasBleOffTime ? 1 : 0));
        out.writeByte((byte) (hasBleOnOff ? 1 : 0));
        out.writeByte((byte) (hasBleOnTime ? 1 : 0));
        out.writeByte((byte) (hasBleTxp ? 1 : 0));
        out.writeByte((byte) (hasAdr ? 1 : 0));
        out.writeByte((byte) (hasAppEui ? 1 : 0));
        out.writeByte((byte) (hasAppKey ? 1 : 0));
        out.writeByte((byte) (hasAppSkey ? 1 : 0));
        out.writeByte((byte) (hasDevAddr ? 1 : 0));
        out.writeByte((byte) (hasDevEui ? 1 : 0));
        out.writeByte((byte) (hasNwkSkey ? 1 : 0));
        out.writeByte((byte) (hasLoraTxp ? 1 : 0));
        out.writeByte((byte) (hasActivation ? 1 : 0));
        out.writeByte((byte) (hasAppParam ? 1 : 0));
        out.writeByte((byte) (hasBleParam ? 1 : 0));
        out.writeByte((byte) (hasLoraParam ? 1 : 0));
        out.writeByte((byte) (hasUploadInterval ? 1 : 0));
        out.writeByte((byte) (hasConfirm ? 1 : 0));
        out.writeByte((byte) (hasLoraInterval ? 1 : 0));
        out.writeByte((byte) (hasEddyStone ? 1 : 0));
        out.writeByte((byte) (hasIbeacon ? 1 : 0));
        out.writeByte((byte) (hasCustomPackage ? 1 : 0));
        out.writeByte((byte) (hasSensorBroadcast ? 1 : 0));
        out.writeByte((byte) (hasSensorParam ? 1 : 0));
        out.writeByte((byte) (hasDelay ? 1 : 0));
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


    public String getSerialNumber() {
        return sn;
    }

    public void setSerialNumber(String serialNumber) {
        this.sn = serialNumber;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(String proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

//    public String getMacAddress() {
//        return macAddress;
//    }
//
//    public void setMacAddress(String macAddress) {
//        this.macAddress = macAddress;
//    }

//
//    public int getBatteryLevel() {
//        return batteryLevel;
//    }
//
//    public void setBatteryLevel(int batteryLevel) {
//        this.batteryLevel = batteryLevel;
//    }


    public int getAccelerometerCount() {
        return accelerometerCount;
    }

    public void setAccelerometerCount(int accelerometerCount) {
        this.accelerometerCount = accelerometerCount;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public float getSf() {
        return sf;
    }

    public void setSf(float sf) {
        this.sf = sf;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getDevUi() {
        return devUi;
    }

    public void setDevUi(String devUi) {
        this.devUi = devUi;
    }

    public String getAppEui() {
        return appEui;
    }

    public void setAppEui(String appEui) {
        this.appEui = appEui;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSkey() {
        return appSkey;
    }

    public void setAppSkey(String appSkey) {
        this.appSkey = appSkey;
    }

    public String getNwkSkey() {
        return nwkSkey;
    }

    public void setNwkSkey(String nwkSkey) {
        this.nwkSkey = nwkSkey;
    }

    public int getDevAdr() {
        return devAdr;
    }

    public void setDevAdr(int devAdr) {
        this.devAdr = devAdr;
    }

    public int getLoraDr() {
        return loraDr;
    }

    public void setLoraDr(int loraDr) {
        this.loraDr = loraDr;
    }

    public int getLoraAdr() {
        return loraAdr;
    }

    public void setLoraAdr(int loraAdr) {
        this.loraAdr = loraAdr;
    }

    public int getLoraTxp() {
        return loraTxp;
    }

    public void setLoraTxp(int loraTxp) {
        this.loraTxp = loraTxp;
    }

    public float getLoraInt() {
        return loraInt;
    }

    public void setLoraInt(int loraInt) {
        this.loraInt = loraInt;
    }

    public int getBleTxp() {
        return bleTxp;
    }

    public void setBleTxp(int bleTxp) {
        this.bleTxp = bleTxp;
    }

    public float getBleInt() {
        return bleInt;
    }

    public void setBleInt(float bleInt) {
        this.bleInt = bleInt;
    }

    public int getBleOnTime() {
        return bleOnTime;
    }

    public void setBleOnTime(int bleOnTime) {
        this.bleOnTime = bleOnTime;
    }

    public int getBleOffTime() {
        return bleOffTime;
    }

    public void setBleOffTime(int bleOffTime) {
        this.bleOffTime = bleOffTime;
    }

    public void setLoraInt(float loraInt) {
        this.loraInt = loraInt;
    }

    public boolean isDfu() {
        return isDfu;
    }

    public void setDfu(boolean isDfu) {
        this.isDfu = isDfu;
    }


    public String getDfuInfo() {
        return dfuInfo;
    }

    public void setDfuInfo(String dfuInfo) {
        this.dfuInfo = dfuInfo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTempInterval() {
        return tempInterval;
    }

    public void setTempInterval(int tempInterval) {
        this.tempInterval = tempInterval;
    }

    public int getLightInterval() {
        return lightInterval;
    }

    public void setLightInterval(int lightInterval) {
        this.lightInterval = lightInterval;
    }

    public int getHumidityInterval() {
        return humidityInterval;
    }

    public void setIBeaconEnabled(boolean IBeaconEnabled) {
        isIBeaconEnabled = IBeaconEnabled;
    }

    public void setHumidityInterval(int humidityInterval) {
        this.humidityInterval = humidityInterval;
    }


    public SensoroSlot[] getSlotArray() {
        return slotArray;
    }

    public void setSlotArray(SensoroSlot[] slotArray) {
        this.slotArray = slotArray;
    }

    public int getClassBEnabled() {
        return classBEnabled;
    }

    public void setClassBEnabled(int classBEnabled) {
        this.classBEnabled = classBEnabled;
    }

    public int getClassBDataRate() {
        return classBDataRate;
    }

    public void setClassBDataRate(int classBDataRate) {
        this.classBDataRate = classBDataRate;
    }

    public int getClassBPeriodicity() {
        return classBPeriodicity;
    }

    public void setClassBPeriodicity(int classBPeriodicity) {
        this.classBPeriodicity = classBPeriodicity;
    }

//    public int getRssi() {
//        return rssi;
//    }
//
//    public void setRssi(int rssi) {
//        this.rssi = rssi;
//    }

    public int getDfuProgress() {
        return dfuProgress;
    }

    public void setDfuProgress(int dfuProgress) {
        this.dfuProgress = dfuProgress;
    }

    public byte getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(byte dataVersion) {
        this.dataVersion = dataVersion;
    }

    public boolean isIBeaconEnabled() {
        return isIBeaconEnabled;
    }

    public List<Integer> getChannelMaskList() {
        return channelMaskList;
    }

    public void setChannelMaskList(List<Integer> channelMaskList) {
        this.channelMaskList = channelMaskList;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof SensoroDevice)) {
            return false;
        }
        SensoroDevice thatBeacon = (SensoroDevice) that;

        return (thatBeacon.macAddress.equals(this.macAddress));
    }

    @Override
    public int hashCode() {
        return macAddress.hashCode();
    }

    public boolean hasAdr() {
        return hasAdr;
    }

    public void setHasAdr(boolean hasAdr) {
        this.hasAdr = hasAdr;
    }

    public boolean hasAppEui() {
        return hasAppEui;
    }

    public void setHasAppEui(boolean hasAppEui) {
        this.hasAppEui = hasAppEui;
    }

    public boolean hasAppKey() {
        return hasAppKey;
    }

    public void setHasAppKey(boolean hasAppKey) {
        this.hasAppKey = hasAppKey;
    }

    public boolean hasAppSkey() {
        return hasAppSkey;
    }

    public void setHasAppSkey(boolean hasAppSkey) {
        this.hasAppSkey = hasAppSkey;
    }

    public boolean hasBleInterval() {
        return hasBleInterval;
    }

    public void setHasBleInterval(boolean hasBleInterval) {
        this.hasBleInterval = hasBleInterval;
    }

    public boolean hasBleOffTime() {
        return hasBleOffTime;
    }

    public void setHasBleOffTime(boolean hasBleOffTime) {
        this.hasBleOffTime = hasBleOffTime;
    }

    public boolean hasBleOnOff() {
        return hasBleOnOff;
    }

    public void setHasBleOnOff(boolean hasBleOnOff) {
        this.hasBleOnOff = hasBleOnOff;
    }

    public boolean hasBleOnTime() {
        return hasBleOnTime;
    }

    public void setHasBleOnTime(boolean hasBleOnTime) {
        this.hasBleOnTime = hasBleOnTime;
    }


    public int getActivation() {
        return activation;
    }

    public void setActivation(int activation) {
        this.activation = activation;
    }

    public boolean hasActivation() {
        return hasActivation;
    }

    public void setHasActivation(boolean hasActivation) {
        this.hasActivation = hasActivation;
    }

    public boolean hasBleTxp() {
        return hasBleTxp;
    }

    public void setHasBleTxp(boolean hasBleTxp) {
        this.hasBleTxp = hasBleTxp;
    }

    public boolean hasDataRate() {
        return hasDataRate;
    }

    public void setHasDataRate(boolean hasDataRate) {
        this.hasDataRate = hasDataRate;
    }

    public boolean hasDevAddr() {
        return hasDevAddr;
    }

    public void setHasDevAddr(boolean hasDevAddr) {
        this.hasDevAddr = hasDevAddr;
    }

    public boolean hasDevEui() {
        return hasDevEui;
    }

    public void setHasDevEui(boolean hasDevEui) {
        this.hasDevEui = hasDevEui;
    }

    public boolean hasLoraSf() {
        return hasLoraSf;
    }

    public void setHasLoraSf(boolean hasLoraSf) {
        this.hasLoraSf = hasLoraSf;
    }

    public boolean hasNwkAddress() {
        return hasNwkAddress;
    }

    public void setHasNwkAddress(boolean hasNwkAddress) {
        this.hasNwkAddress = hasNwkAddress;
    }

    public boolean hasNwkSkey() {
        return hasNwkSkey;
    }

    public void setHasNwkSkey(boolean hasNwkSkey) {
        this.hasNwkSkey = hasNwkSkey;
    }

    public boolean hasLoraTxp() {
        return hasLoraTxp;
    }

    public void setHasLoraTxp(boolean hasTxPower) {
        this.hasLoraTxp = hasTxPower;
    }


    public boolean hasAppParam() {
        return hasAppParam;
    }

    public void setHasAppParam(boolean hasAppParam) {
        this.hasAppParam = hasAppParam;
    }

    public boolean hasBleParam() {
        return hasBleParam;
    }

    public void setHasBleParam(boolean hasBleParam) {
        this.hasBleParam = hasBleParam;
    }

    public boolean hasLoraParam() {
        return hasLoraParam;
    }

    public void setHasLoraParam(boolean hasLoraParam) {
        this.hasLoraParam = hasLoraParam;
    }


    public Integer getUploadInterval() {
        return uploadInterval;
    }

    public void setUploadInterval(Integer uploadInterval) {
        this.uploadInterval = uploadInterval;
    }

    public boolean hasUploadInterval() {
        return hasUploadInterval;
    }

    public void setHasUploadInterval(boolean hasUploadInterval) {
        this.hasUploadInterval = hasUploadInterval;
    }

    public Integer getConfirm() {
        return confirm;
    }

    public void setConfirm(Integer confirm) {
        this.confirm = confirm;
    }

    public boolean hasConfirm() {
        return hasConfirm;
    }

    public void setHasConfirm(boolean hasConfirm) {
        this.hasConfirm = hasConfirm;
    }

    public SensoroSensor getSensoroSensor() {
        return sensoroSensor;
    }

    public void setSensoroSensor(SensoroSensor sensoroSensor) {
        this.sensoroSensor = sensoroSensor;
    }

    public boolean hasLoraInterval() {
        return hasLoraInterval;
    }

    public void setHasLoraInterval(boolean hasLoraInterval) {
        this.hasLoraInterval = hasLoraInterval;
    }

    public boolean hasEddyStone() {
        return hasEddyStone;
    }

    public void setHasEddyStone(boolean hasEddyStone) {
        this.hasEddyStone = hasEddyStone;
    }

    public boolean hasIbeacon() {
        return hasIbeacon;
    }

    public void setHasIbeacon(boolean hasIbeacon) {
        this.hasIbeacon = hasIbeacon;
    }

    public void setActivation(Integer activation) {
        this.activation = activation;
    }

    public boolean hasCustomPackage() {
        return hasCustomPackage;
    }

    public void setHasCustomPackage(boolean hasCustomPackage) {
        this.hasCustomPackage = hasCustomPackage;
    }

    public boolean hasSensorBroadcast() {
        return hasSensorBroadcast;
    }

    public void setHasSensorBroadcast(boolean hasSensorBroadcast) {
        this.hasSensorBroadcast = hasSensorBroadcast;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public int getMaxEirp() {
        return maxEirp;
    }

    public SensoroDevice setMaxEirp(int maxEirp) {
        this.maxEirp = maxEirp;
        return this;
    }

    public int getSglStatus() {
        return sglStatus;
    }

    public SensoroDevice setSglStatus(int sglStatus) {
        this.sglStatus = sglStatus;
        return this;
    }

    public int getSglFrequency() {
        return sglFrequency;
    }

    public SensoroDevice setSglFrequency(int sglFrequency) {
        this.sglFrequency = sglFrequency;
        return this;
    }

    public int getSglDatarate() {
        return sglDatarate;
    }

    public SensoroDevice setSglDatarate(int sglDatarate) {
        this.sglDatarate = sglDatarate;
        return this;
    }

    public boolean hasMaxEirp() {
        return hasMaxEirp;
    }

    public void setHasMaxEirp(boolean hasMaxEirp) {
        this.hasMaxEirp = hasMaxEirp;
    }

    public boolean hasDelay() {
        return hasDelay;
    }

    public void setHasDelay(boolean hasDelay) {
        this.hasDelay = hasDelay;
    }

    public boolean hasSensorParam() {
        return hasSensorParam;
    }

    public void setHasSensorParam(boolean hasSensorParam) {
        this.hasSensorParam = hasSensorParam;
    }
}

