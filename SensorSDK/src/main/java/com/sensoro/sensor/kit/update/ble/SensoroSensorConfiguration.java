package com.sensoro.sensor.kit.update.ble;

/**
 * Created by sensoro on 17/4/28.
 */

public class SensoroSensorConfiguration  {

    Float coAlarmHigh;
    Float co2AlarmHigh;
    Float no2AlarmHigh;
    Float ch4AlarmHigh;
    Float lpgAlarmHigh;
    Float pm1AlarmHigh;
    Float pm25AlarmHigh;
    Float pm10AlarmHigh;
    Float coData;
    Float co2Data;
    Float no2Data;
    Float ch4Data;
    Float lpgData;
    Float pm25Data;
    Float pm10Data;
    Float tempAlarmHigh;
    Float tempAlarmLow;
    Float humidityHigh;
    Float humidityLow;
    Float pitchAngleData;
    Float rollAngleData;
    Float yawAngleData;
    Float pitchAngleAlarmHigh;
    Float pitchAngleAlarmLow;
    Float rollAngleAlarmHigh;
    Float rollAngleAlarmLow;
    Float yawAngleAlarmHigh;
    Float yawAngleAlarmLow;
    Float waterPressureAlarmHigh;
    Float waterPressureAlarmLow;
    boolean hasCo;
    boolean hasCo2;
    boolean hasNo2;
    boolean hasCh4;
    boolean hasLpg;
    boolean hasPm25;
    boolean hasPm10;
    boolean hasTemperature;
    boolean hasHumidity;
    boolean hasPitchAngle;
    boolean hasRollAngle;
    boolean hasYawAngle;
    boolean hasWaterPressure;


    protected SensoroSensorConfiguration(Builder builder) {
        coAlarmHigh = builder.coAlarmHigh;
        co2AlarmHigh = builder.co2AlarmHigh;
        no2AlarmHigh = builder.no2AlarmHigh;
        ch4AlarmHigh = builder.ch4AlarmHigh;
        lpgAlarmHigh = builder.lpgAlarmHigh;
        pm1AlarmHigh = builder.pm1AlarmHigh;
        pm25AlarmHigh = builder.pm25AlarmHigh;
        pm10AlarmHigh = builder.pm10AlarmHigh;
        coData = builder.coData;
        co2Data = builder.co2Data;
        no2Data = builder.no2Data;
        ch4Data = builder.ch4Data;
        lpgData = builder.lpgData;
        pm25Data = builder.pm25Data;
        pm10Data = builder.pm10Data;
        tempAlarmHigh = builder.tempAlarmHigh;
        tempAlarmLow = builder.tempAlarmLow;
        humidityHigh = builder.humidityHigh;
        humidityLow = builder.humidityLow;
        pitchAngleData = builder.pitchAngleData;
        rollAngleData = builder.rollAngleData;
        yawAngleData = builder.yawAngleData;
        pitchAngleAlarmHigh = builder.pitchAngleAlarmHigh;
        pitchAngleAlarmLow = builder.pitchAngleAlarmLow;
        rollAngleAlarmHigh = builder.rollAngleAlarmHigh;
        rollAngleAlarmLow = builder.rollAngleAlarmLow;
        yawAngleAlarmHigh = builder.yawAngleAlarmHigh;
        yawAngleAlarmLow = builder.yawAngleAlarmLow;
        hasCo = builder.hasCo;
        hasCo2 = builder.hasCo2;
        hasNo2 = builder.hasNo2;
        hasCh4 = builder.hasCh4;
        hasLpg = builder.hasLpg;
        hasPm10 = builder.hasPm10;
        hasPm25 = builder.hasPm25;
        hasTemperature = builder.hasTemperature;
        hasHumidity = builder.hasHumidity;
        hasPitchAngle = builder.hasPitchAngle;
        hasRollAngle = builder.hasRollAngle;
        hasYawAngle = builder.hasYawAngle;
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

    public Float getPm1AlarmHigh() {
        return pm1AlarmHigh;
    }

    public void setPm1AlarmHigh(Float pm1AlarmHigh) {
        this.pm1AlarmHigh = pm1AlarmHigh;
    }

    public Float getPm25AlarmHigh() {
        return pm25AlarmHigh;
    }

    public void setPm25AlarmHigh(Float pm25AlarmHigh) {
        this.pm25AlarmHigh = pm25AlarmHigh;
    }

    public Float getCh4Data() {
        return ch4Data;
    }

    public void setCh4Data(Float ch4Data) {
        this.ch4Data = ch4Data;
    }

    public Float getCo2Data() {
        return co2Data;
    }

    public void setCo2Data(Float co2Data) {
        this.co2Data = co2Data;
    }

    public Float getCoData() {
        return coData;
    }

    public void setCoData(Float coData) {
        this.coData = coData;
    }

    public Float getLpgData() {
        return lpgData;
    }

    public void setLpgData(Float lpgData) {
        this.lpgData = lpgData;
    }

    public Float getNo2Data() {
        return no2Data;
    }

    public void setNo2Data(Float no2Data) {
        this.no2Data = no2Data;
    }

    public Float getPm10Data() {
        return pm10Data;
    }

    public void setPm10Data(Float pm10Data) {
        this.pm10Data = pm10Data;
    }

    public Float getPm25Data() {
        return pm25Data;
    }

    public void setPm25Data(Float pm25Data) {
        this.pm25Data = pm25Data;
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

    public boolean hasLpg() {
        return hasLpg;
    }

    public void setHasLpg(boolean hasLpg) {
        this.hasLpg = hasLpg;
    }

    public boolean hasNo2() {
        return hasNo2;
    }

    public void setHasNo2(boolean hasNo2) {
        this.hasNo2 = hasNo2;
    }

    public boolean hasPm10() {
        return hasPm10;
    }

    public void setHasPm10(boolean hasPm10) {
        this.hasPm10 = hasPm10;
    }

    public boolean hasPm25() {
        return hasPm25;
    }

    public void setHasPm25(boolean hasPm25) {
        this.hasPm25 = hasPm25;
    }

    public boolean hasHumidity() {
        return hasHumidity;
    }

    public void setHasHumidity(boolean hasHumidity) {
        this.hasHumidity = hasHumidity;
    }

    public boolean hasTemperature() {
        return hasTemperature;
    }

    public void setHasTemperature(boolean hasTemperature) {
        this.hasTemperature = hasTemperature;
    }

    public Float getHumidityHigh() {
        return humidityHigh;
    }

    public void setHumidityHigh(Float humidityHigh) {
        this.humidityHigh = humidityHigh;
    }

    public Float getHumidityLow() {
        return humidityLow;
    }

    public void setHumidityLow(Float humidityLow) {
        this.humidityLow = humidityLow;
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

    public Float getPitchAngleData() {
        return pitchAngleData;
    }

    public SensoroSensorConfiguration setPitchAngleData(Float pitchAngleData) {
        this.pitchAngleData = pitchAngleData;
        return this;
    }

    public Float getRollAngleData() {
        return rollAngleData;
    }

    public SensoroSensorConfiguration setRollAngleData(Float rollAngleData) {
        this.rollAngleData = rollAngleData;
        return this;
    }

    public Float getYawAngleData() {
        return yawAngleData;
    }

    public SensoroSensorConfiguration setYawAngleData(Float yawAngleData) {
        this.yawAngleData = yawAngleData;
        return this;
    }

    public Float getPitchAngleAlarmHigh() {
        return pitchAngleAlarmHigh;
    }

    public SensoroSensorConfiguration setPitchAngleAlarmHigh(Float pitchAngleAlarmHigh) {
        this.pitchAngleAlarmHigh = pitchAngleAlarmHigh;
        return this;
    }

    public Float getPitchAngleAlarmLow() {
        return pitchAngleAlarmLow;
    }

    public SensoroSensorConfiguration setPitchAngleAlarmLow(Float pitchAngleAlarmLow) {
        this.pitchAngleAlarmLow = pitchAngleAlarmLow;
        return this;
    }

    public Float getRollAngleAlarmHigh() {
        return rollAngleAlarmHigh;
    }

    public SensoroSensorConfiguration setRollAngleAlarmHigh(Float rollAngleAlarmHigh) {
        this.rollAngleAlarmHigh = rollAngleAlarmHigh;
        return this;
    }

    public Float getRollAngleAlarmLow() {
        return rollAngleAlarmLow;
    }

    public SensoroSensorConfiguration setRollAngleAlarmLow(Float rollAngleAlarmLow) {
        this.rollAngleAlarmLow = rollAngleAlarmLow;
        return this;
    }

    public Float getYawAngleAlarmHigh() {
        return yawAngleAlarmHigh;
    }

    public SensoroSensorConfiguration setYawAngleAlarmHigh(Float yawAngleAlarmHigh) {
        this.yawAngleAlarmHigh = yawAngleAlarmHigh;
        return this;
    }

    public Float getYawAngleAlarmLow() {
        return yawAngleAlarmLow;
    }

    public SensoroSensorConfiguration setYawAngleAlarmLow(Float yawAngleAlarmLow) {
        this.yawAngleAlarmLow = yawAngleAlarmLow;
        return this;
    }

    public boolean hasPitchAngle() {
        return hasPitchAngle;
    }

    public SensoroSensorConfiguration setHasPitchAngle(boolean hasPitchAngle) {
        this.hasPitchAngle = hasPitchAngle;
        return this;
    }

    public boolean hasRollAngle() {
        return hasRollAngle;
    }

    public SensoroSensorConfiguration setHasRollAngle(boolean hasRollAngle) {
        this.hasRollAngle = hasRollAngle;
        return this;
    }

    public boolean hasYawAngle() {
        return hasYawAngle;
    }

    public SensoroSensorConfiguration setHasYawAngle(boolean hasYawAngle) {
        this.hasYawAngle = hasYawAngle;
        return this;
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

    public static class Builder {
        Float coAlarmHigh;
        Float co2AlarmHigh;
        Float no2AlarmHigh;
        Float ch4AlarmHigh;
        Float lpgAlarmHigh;
        Float pm1AlarmHigh;
        Float pm25AlarmHigh;
        Float pm10AlarmHigh;
        Float tempAlarmHigh;
        Float tempAlarmLow;
        Float humidityHigh;
        Float humidityLow;
        Float coData;
        Float co2Data;
        Float no2Data;
        Float ch4Data;
        Float lpgData;
        Float pm1Data;
        Float pm25Data;
        Float pm10Data;
        Float pitchAngleData;
        Float rollAngleData;
        Float yawAngleData;
        Float pitchAngleAlarmHigh;
        Float pitchAngleAlarmLow;
        Float rollAngleAlarmHigh;
        Float rollAngleAlarmLow;
        Float yawAngleAlarmHigh;
        Float yawAngleAlarmLow;
        Float waterPressureAlarmHigh;
        Float waterPressureAlarmLow;
        boolean hasCo;
        boolean hasCo2;
        boolean hasNo2;
        boolean hasCh4;
        boolean hasLpg;
        boolean hasPm25;
        boolean hasPm10;
        boolean hasTemperature;
        boolean hasHumidity;
        boolean hasPitchAngle;
        boolean hasRollAngle;
        boolean hasYawAngle;
        boolean hasWaterPressure;

        public Builder() {
            coAlarmHigh = null;
            co2AlarmHigh = null;
            no2AlarmHigh = null;
            ch4AlarmHigh = null;
            lpgAlarmHigh = null;
            pm1AlarmHigh = null;
            pm25AlarmHigh = null;
            pm10AlarmHigh = null;
            coData = null;
            co2Data = null;
            no2Data = null;
            ch4Data = null;
            lpgData = null;
            pm1Data = null;
            pm25Data = null;
            pm10Data = null;
            tempAlarmHigh = null;
            tempAlarmLow = null;
            humidityHigh = null;
            humidityLow = null;
            pitchAngleData = null;
            rollAngleData = null;
            yawAngleData = null;
            pitchAngleAlarmHigh = null;
            pitchAngleAlarmLow = null;
            rollAngleAlarmHigh = null;
            rollAngleAlarmLow = null;
            yawAngleAlarmHigh = null;
            yawAngleAlarmLow = null;
            waterPressureAlarmHigh = null;
            waterPressureAlarmLow = null;
            hasCo = false;
            hasCo2 = false;
            hasNo2 = false;
            hasCh4 = false;
            hasLpg = false;
            hasPm10 = false;
            hasPm25 = false;
            hasTemperature = false;
            hasHumidity = false;
            hasPitchAngle = false;
            hasRollAngle = false;
            hasYawAngle = false;
            hasWaterPressure = false;
        }

        public Builder setCh4AlarmHigh(Float ch4AlarmHigh) {
            this.ch4AlarmHigh = ch4AlarmHigh;
            return this;
        }

        public Builder setCo2AlarmHigh(Float co2AlarmHigh) {
            this.co2AlarmHigh = co2AlarmHigh;
            return this;
        }

        public Builder setCoAlarmHigh(Float coAlarmHigh) {
            this.coAlarmHigh = coAlarmHigh;
            return this;
        }

        public Builder setLpgAlarmHigh(Float lpgAlarmHigh) {
            this.lpgAlarmHigh = lpgAlarmHigh;
            return this;
        }

        public Builder setNo2AlarmHigh(Float no2AlarmHigh) {
            this.no2AlarmHigh = no2AlarmHigh;
            return this;
        }

        public Builder setPm10AlarmHigh(Float pm10AlarmHigh) {
            this.pm10AlarmHigh = pm10AlarmHigh;
            return this;
        }

        public Builder setPm1AlarmHigh(Float pm1AlarmHigh) {
            this.pm1AlarmHigh = pm1AlarmHigh;
            return this;
        }

        public Builder setPm25AlarmHigh(Float pm25AlarmHigh) {
            this.pm25AlarmHigh = pm25AlarmHigh;
            return this;
        }

        public Builder setCh4Data(Float ch4Data) {
            this.ch4Data = ch4Data;
            return this;
        }

        public Builder setCo2Data(Float co2Data) {
            this.co2Data = co2Data;
            return this;
        }

        public Builder setCoData(Float coData) {
            this.coData = coData;
            return this;
        }

        public Builder setLpgData(Float lpgData) {
            this.lpgData = lpgData;
            return this;
        }

        public Builder setNo2Data(Float no2Data) {
            this.no2Data = no2Data;
            return this;
        }

        public Builder setPm10Data(Float pm10Data) {
            this.pm10Data = pm10Data;
            return this;
        }

        public Builder setPm1Data(Float pm1Data) {
            this.pm1Data = pm1Data;
            return this;
        }

        public Builder setPm25Data(Float pm25Data) {
            this.pm25Data = pm25Data;
            return this;
        }

        public Builder setHasCh4(boolean hasCh4) {
            this.hasCh4 = hasCh4;
            return this;
        }

        public Builder setHasCo2(boolean hasCo2) {
            this.hasCo2 = hasCo2;
            return this;
        }

        public Builder setHasCo(boolean hasCo) {
            this.hasCo = hasCo;
            return this;
        }

        public Builder setHasLpg(boolean hasLpg) {
            this.hasLpg = hasLpg;
            return this;
        }

        public Builder setHasNo2(boolean hasNo2) {
            this.hasNo2 = hasNo2;
            return this;
        }

        public Builder setHasPm10(boolean hasPm10) {
            this.hasPm10 = hasPm10;
            return this;
        }

        public Builder setHasPm25(boolean hasPm25) {
            this.hasPm25 = hasPm25;
            return this;
        }

        public Builder setHumidityHigh(Float humidityHigh) {
            this.humidityHigh = humidityHigh;
            return this;
        }

        public Builder setHumidityLow(Float humidityLow) {
            this.humidityLow = humidityLow;
            return this;
        }

        public Builder setTempAlarmHigh(Float tempAlarmHigh) {
            this.tempAlarmHigh = tempAlarmHigh;
            return this;
        }

        public Builder setTempAlarmLow(Float tempAlarmLow) {
            this.tempAlarmLow = tempAlarmLow;
            return this;
        }

        public Builder setHasHumidity(boolean hasHumidity) {
            this.hasHumidity = hasHumidity;
            return this;
        }

        public Builder setHasTemperature(boolean hasTemperature) {
            this.hasTemperature = hasTemperature;
            return this;
        }

        public Builder setPitchAngleData(Float pitchAngleData) {
            this.pitchAngleData = pitchAngleData;
            return this;
        }

        public Builder setRollAngleData(Float rollAngleData) {
            this.rollAngleData = rollAngleData;
            return this;
        }

        public Builder setYawAngleData(Float yawAngleData) {
            this.yawAngleData = yawAngleData;
            return this;
        }

        public Builder setPitchAngleAlarmHigh(Float pitchAngleAlarmHigh) {
            this.pitchAngleAlarmHigh = pitchAngleAlarmHigh;
            return this;
        }

        public Builder setPitchAngleAlarmLow(Float pitchAngleAlarmLow) {
            this.pitchAngleAlarmLow = pitchAngleAlarmLow;
            return this;
        }

        public Builder setRollAngleAlarmHigh(Float rollAngleAlarmHigh) {
            this.rollAngleAlarmHigh = rollAngleAlarmHigh;
            return this;
        }

        public Builder setRollAngleAlarmLow(Float rollAngleAlarmLow) {
            this.rollAngleAlarmLow = rollAngleAlarmLow;
            return this;
        }

        public Builder setYawAngleAlarmHigh(Float yawAngleAlarmHigh) {
            this.yawAngleAlarmHigh = yawAngleAlarmHigh;
            return this;
        }

        public Builder setYawAngleAlarmLow(Float yawAngleAlarmLow) {
            this.yawAngleAlarmLow = yawAngleAlarmLow;
            return this;
        }

        public Builder setWaterPressureAlarmHigh(Float waterPressureAlarmHigh) {
            this.waterPressureAlarmHigh = waterPressureAlarmHigh;
            return this;
        }

        public Builder setWaterPressureAlarmLow(Float waterPressureAlarmLow) {
            this.waterPressureAlarmLow = waterPressureAlarmLow;
            return this;
        }

        public Builder setHasPitchAngle(boolean hasPitchAngle) {
            this.hasPitchAngle = hasPitchAngle;
            return this;
        }

        public Builder setHasRollAngle(boolean hasRollAngle) {
            this.hasRollAngle = hasRollAngle;
            return this;
        }

        public Builder setHasYawAngle(boolean hasYawAngle) {
            this.hasYawAngle = hasYawAngle;
            return this;
        }


        public Builder setHasWaterPressure(boolean hasWaterPressure) {
            this.hasWaterPressure = hasWaterPressure;
            return this;
        }

        public SensoroSensorConfiguration build() {
            return new SensoroSensorConfiguration(this);
        }
    }

}
