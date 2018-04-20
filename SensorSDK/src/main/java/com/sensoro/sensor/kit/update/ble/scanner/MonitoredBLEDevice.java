package com.sensoro.sensor.kit.update.ble.scanner;

import android.os.Parcel;
import android.os.Parcelable;

import com.sensoro.sensor.kit.update.ble.BLEDevice;

/**
 * Created by Sensoro on 12/18/14.
 */
class MonitoredBLEDevice implements Parcelable {
    BLEDevice bleDevice;
    boolean inSide;

    public MonitoredBLEDevice(BLEDevice bleDevice, boolean inSide) {
        this.bleDevice = bleDevice;
        this.inSide = inSide;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(bleDevice, flags);
        out.writeByte((byte) (inSide ? 1 : 0));
    }

    public static final Creator<MonitoredBLEDevice> CREATOR
            = new Creator<MonitoredBLEDevice>() {
        public MonitoredBLEDevice createFromParcel(Parcel in) {
            return new MonitoredBLEDevice(in);
        }

        public MonitoredBLEDevice[] newArray(int size) {
            return new MonitoredBLEDevice[size];
        }
    };

    private MonitoredBLEDevice(Parcel in) {
        bleDevice = in.readParcelable(this.getClass().getClassLoader());
        inSide = in.readByte() == 1;
    }
}
