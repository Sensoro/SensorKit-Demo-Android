package com.sensoro.sensor.kit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sensoro on 12/18/14.
 */
class MonitoredSensoroDevice implements Parcelable {
    SensoroDevice sensoroDevice;
    boolean inSide;

    public MonitoredSensoroDevice(SensoroDevice sensoroDevice, boolean inSide) {
        this.sensoroDevice = sensoroDevice;
        this.inSide = inSide;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(sensoroDevice, flags);
        out.writeByte((byte) (inSide ? 1 : 0));
    }

    public static final Creator<MonitoredSensoroDevice> CREATOR
            = new Creator<MonitoredSensoroDevice>() {
        public MonitoredSensoroDevice createFromParcel(Parcel in) {
            return new MonitoredSensoroDevice(in);
        }

        public MonitoredSensoroDevice[] newArray(int size) {
            return new MonitoredSensoroDevice[size];
        }
    };

    private MonitoredSensoroDevice(Parcel in) {
        sensoroDevice = in.readParcelable(this.getClass().getClassLoader());
        inSide = in.readByte() == 1;
    }
}
