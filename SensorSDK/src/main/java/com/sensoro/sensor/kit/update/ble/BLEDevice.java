package com.sensoro.sensor.kit.update.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sensoro on 16/8/12.
 */

public class BLEDevice implements Parcelable, Cloneable {

    public static final int TYPE_DEVICE = 0x00;
    public static final int TYPE_SENSOR = 0x01;
    public static final int TYPE_STATION = 0x02;
    public String sn;
    public String hardwareVersion;// hardware version.
    public String firmwareVersion;// firmware version.
    public String macAddress; // MAC
    public int batteryLevel;// battery left
    public int rssi;
    public int type;
    public long lastFoundTime;

    public BLEDevice() {
        lastFoundTime = System.currentTimeMillis();
    }

    public BLEDevice(Parcel in) {
        sn = in.readString();
        hardwareVersion = in.readString();
        firmwareVersion = in.readString();
        macAddress = in.readString();
        lastFoundTime = in.readLong();
        type = in.readInt();
        batteryLevel = in.readInt();
        rssi = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sn);
        parcel.writeString(hardwareVersion);
        parcel.writeString(firmwareVersion);
        parcel.writeString(macAddress);
        parcel.writeLong(lastFoundTime);
        parcel.writeInt(type);
        parcel.writeInt(batteryLevel);
        parcel.writeInt(rssi);
    }

    public static final Parcelable.Creator<BLEDevice> CREATOR = new Parcelable.Creator<BLEDevice>() {

        @Override
        public BLEDevice createFromParcel(Parcel parcel) {
            return new BLEDevice(parcel);
        }

        @Override
        public BLEDevice[] newArray(int size) {
            return new BLEDevice[size];
        }
    };

    @Override
    public BLEDevice clone() throws CloneNotSupportedException {
        BLEDevice newDevice = null;
        try {
            newDevice = (BLEDevice) super.clone();
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

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof BLEDevice)) {
            return false;
        }
        BLEDevice thatDevice = (BLEDevice) that;

        return (thatDevice.macAddress.equals(this.macAddress));
    }


    @Override
    public int hashCode() {
        return macAddress.hashCode();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
