package com.sensoro.sensor.kit.update.ble.constants;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sensoro on 15/9/21.
 */

public class TLM implements Parcelable {
    int battery;
    float temperature;
    int advCount;
    int startTime;

    public TLM() {
        battery = 0;
        temperature = 0;
        advCount = 0;
        startTime = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(battery);
        dest.writeFloat(temperature);
        dest.writeInt(advCount);
        dest.writeInt(startTime);
    }

    public static final Creator<TLM> CREATOR = new Creator<TLM>() {

        @Override
        public TLM createFromParcel(Parcel parcel) {
            return new TLM(parcel);
        }

        @Override
        public TLM[] newArray(int size) {
            return new TLM[size];
        }
    };

    private TLM(Parcel in) {
        battery = in.readInt();
        temperature = in.readFloat();
        advCount = in.readInt();
        startTime = in.readInt();
    }

    public int getBattery() {
        return battery;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getAdvCount() {
        return advCount;
    }

    public int getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "TLM{" +
                "battery=" + battery +
                ", temperature=" + temperature +
                ", advCount=" + advCount +
                ", startTime=" + startTime +
                '}';
    }
}
