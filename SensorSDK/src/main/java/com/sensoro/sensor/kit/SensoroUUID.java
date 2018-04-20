package com.sensoro.sensor.kit;

import java.math.BigDecimal;

/**
 * Created by Sensoro on 15/9/21.
 */
class SensoroUUID {

    static String parseSN(byte[] sn) {
        String serialNumber = null;
        if (sn.length == 3) {
            serialNumber = "0117C5" + SensoroUtils.bytesToHex(sn);
        } else if (sn.length == 6) {
            serialNumber = SensoroUtils.bytesToHex(sn);
        }else if (sn.length == 8) {
            serialNumber = SensoroUtils.bytesToHex(sn);
        }
        return serialNumber != null ? serialNumber.toUpperCase() : null;
    }

    /**
     * 解析 beacon 温度
     *
     * @param temperatureByte
     * @return
     */
    static Integer parseTemperature(byte temperatureByte) {
        int temperature = temperatureByte;
        if (temperature == 0xff) { // 温度传感器关闭
            return null;
        } else {
            return temperature - 10; // 实际温度-10
        }
    }

    /**
     * 解析 beacon 光线原始数值
     *
     * @param luxHighByte
     * @param luxLowByte
     * @return
     */
    static Double parseBrightnessLux(byte luxHighByte, byte luxLowByte) {
        int luxRawHigh = ((int) luxHighByte & 0xff);
        int luxRawLow = ((int) luxLowByte & 0xff);
        if (luxRawHigh == 0xff) { // 光线传感器关闭
            return null;
        } else {
            return calculateLux(luxRawHigh, luxRawLow);
        }
    }

    protected static double calculateLux(int luxRawHigh, int luxRawLow) {
        double light = Math.pow(2, luxRawHigh / 16) * ((luxRawHigh % 16) * 16 + luxRawLow % 16) * 0.045;
        BigDecimal bigDecimal = new BigDecimal(Double.toString(light)).setScale(3, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    protected static int calculateLuxToInt(int luxRawHigh, int luxRawLow) {
        double light = Math.pow(2, luxRawHigh / 16) * ((luxRawHigh % 16) * 16 + luxRawLow % 16) * 0.045;
        BigDecimal bigDecimal = new BigDecimal(Double.toString(light)).setScale(3, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
    }

    public static float byteArrayToFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * 将4字节的byte数组转成一个int值
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        byte[] a = new byte[4];
        int i = a.length - 1, j = b.length - 1;
        for (; i >= 0; i--, j--) {//从b的尾部(即int值的低位)开始copy数据
            if (j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;//如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }

    public static double byteArrayToDouble(byte[] b, int index) {
        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) b[7] << 56);
        return Double.longBitsToDouble(l);
    }
    public static byte[] intToByteArray(int source, int array_length) {
        byte[] bLocalArr = new byte[array_length];
        for (int i = 0; (i < 4) && (i < array_length); i++) {
            bLocalArr[i] = (byte) (source >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8));
        return value;
    }

}
