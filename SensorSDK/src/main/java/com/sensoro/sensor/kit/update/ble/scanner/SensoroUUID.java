package com.sensoro.sensor.kit.update.ble.scanner;

import com.sensoro.sensor.kit.update.ble.SensoroUtils;

import java.math.BigDecimal;

/**
 * Created by Sensoro on 15/9/21.
 */
public class SensoroUUID {

    public static String parseSN(byte[] sn) {
        String serialNumber = null;
        if (sn.length == 3) {
            serialNumber = "0117C5" + SensoroUtils.bytesToHex(sn);
        } else if (sn.length == 8) {
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
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }

    public static int byteArrayToInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
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

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }


    public static short getShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static char getChar(byte[] bytes) {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static int getInt(byte[] bytes) {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16))
                | (0xff000000 & (bytes[3] << 24));
    }

    public static long getLong(byte[] bytes) {
        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16))
                | (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32))
                | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48))
                | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static double getDouble(byte[] bytes) {
        long l = getLong(bytes);
        System.out.println(l);
        return Double.longBitsToDouble(l);
    }


}
