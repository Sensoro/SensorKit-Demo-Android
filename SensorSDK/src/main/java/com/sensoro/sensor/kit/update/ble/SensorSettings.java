/*
 * Copyright (c) 2014 Sensoro Inc.
 * All rights reserved.
 */

package com.sensoro.sensor.kit.update.ble;

import android.os.Parcel;
import android.os.Parcelable;

import com.sensoro.sensor.kit.update.ble.constants.AccelerometerSensitivity;

import java.io.Serializable;

/**
 * The class for setting the beacon sensors.
 */
public class SensorSettings implements Parcelable {
    static final int DATA_DEFAULT = -2;
    static final int SENSO_DATA_MIN = 1000;
    static final int SENSO_DATA_MAX = 65535;

    int temperatureSamplingInterval; // 温度采样间隔
    int lightSamplingInterval;  // 光感采样间隔
    AccelerometerSensitivity accelerometerSensitivity;    // 加速度传感器灵敏度

    public SensorSettings() {
        temperatureSamplingInterval = DATA_DEFAULT;
        lightSamplingInterval = DATA_DEFAULT;
        accelerometerSensitivity = AccelerometerSensitivity.UNKNOWN;
    }

    @Override
    public String toString() {
		return "SensorSettings{" + "temperatureSamplingInterval=" + temperatureSamplingInterval + ", brightnessSamplingInterval=" + lightSamplingInterval + ", accelerometerSensitivity=" + accelerometerSensitivity + '}';
    }

    /**
	 * <p>
	 * Set the interval of sampling the temperature.
	 * </p>
     *
     * @param temperatureSamplingInterval .Range:0x00(close temperature sensor),1000~65535(unit: ms)
     */
    public void setTemperatureSamplingInterval(int temperatureSamplingInterval) {
        this.temperatureSamplingInterval = temperatureSamplingInterval;
    }

    /**
	 * Get the interval of sampling the temperature.
     *
	 * @return The interval of sampling temperature (unit:ms).
     */
    public int getTemperatureSamplingInterval() {
        return temperatureSamplingInterval;
    }

    /**
	 * <p>
	 * Set the interval of sampling light.
	 * </p>
     *
     * @param lightSamplingInterval new interval of sampling light.Range:0x00(close light
	 *            sensor),1000~65535(unit: ms)
     */
    public void setLightSamplingInterval(int lightSamplingInterval) {
        this.lightSamplingInterval = lightSamplingInterval;
    }

    /**
	 * Get the interval of sampling light.
     *
	 * @return The interval of sampling light (unit:ms).
     */
    public int getLightSamplingInterval() {
        return lightSamplingInterval;
    }

    /**
	 * <p>
	 * Set the acceleration sensor sensitivity of the beacon.
	 * </p>
     *
     * @param accelerometerSensitivity acceleration sensor sensitivity <br/>
	 *
     */
    public void setAccelerometerSensitivity(AccelerometerSensitivity accelerometerSensitivity) {
        this.accelerometerSensitivity = accelerometerSensitivity;
    }

    /**
	 * Get the accelerator sensor sensitivity of the beacon.
     *
	 * @return The acceleration sensor sensitivity <br/>
	 *
     */
    public AccelerometerSensitivity getAccelerometerSensitivity() {
        return accelerometerSensitivity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(temperatureSamplingInterval);
        out.writeInt(lightSamplingInterval);
        out.writeInt(accelerometerSensitivity.ordinal());
    }

    public static final Creator<SensorSettings> CREATOR = new Creator<SensorSettings>() {

        @Override
        public SensorSettings createFromParcel(Parcel parcel) {
            return new SensorSettings(parcel);
        }

        @Override
        public SensorSettings[] newArray(int size) {
            return new SensorSettings[size];
        }
    };

    private SensorSettings(Parcel in) {
        temperatureSamplingInterval = in.readInt();
        lightSamplingInterval = in.readInt();
        accelerometerSensitivity = AccelerometerSensitivity.values()[in.readInt()];
    }

    public class FlashLightCommand implements Serializable {
        public static final byte LIGHT_FLASH_NORMAL = (byte) 0xAA;
        public static final byte LIGHT_FLASH_22 = (byte) 0xCC;
        public static final byte LIGHT_FLASH_12 = (byte) 0x88;
        public static final byte LIGHT_FLASH_31 = (byte) 0xEE;
        public static final byte LIGHT_FLASH_40 = (byte) 0xF0;
        public static final byte LIGHT_FLASH_53 = (byte) 0xF8;
    }
}
