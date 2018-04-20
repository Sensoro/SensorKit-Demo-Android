package com.sensoro.sensor.kit.update.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sensoro on 16/8/24.
 */
public class SensoroSlot implements Parcelable {
    private int type;
    private int index;
    private int isActived;
    private String frame;

    public SensoroSlot() {

    }

    public SensoroSlot(Parcel in) {
        type = in.readInt();
        index = in.readInt();
        isActived = in.readInt();
        frame = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeInt(index);
        parcel.writeInt(isActived);
        parcel.writeString(frame);
    }

    public static final Creator<SensoroSlot> CREATOR = new Creator<SensoroSlot>() {

        @Override
        public SensoroSlot createFromParcel(Parcel parcel) {
            return new SensoroSlot(parcel);
        }

        @Override
        public SensoroSlot[] newArray(int size) {
            return new SensoroSlot[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int isActived() {
        return isActived;
    }

    public void setActived(int actived) {
        isActived = actived;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
