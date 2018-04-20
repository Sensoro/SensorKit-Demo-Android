package com.sensoro.sensor.kit;

/**
 * Created by Sensoro on 15/9/14.
 */
public class Utils {
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
    public static byte[] Integer2Bytes(int src) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (src >> 8);
        ret[1] = (byte) src;
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" -> byte[]{0x2B, 0×44, 0xEF,
     * 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        int length = src.length() / 2;
        byte[] ret = new byte[length];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < length; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF" -> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    private static byte uniteBytes(byte src0, byte src1) {
        try {
            byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
            _b0 = (byte) (_b0 << 4);
            byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
            byte ret = (byte) (_b0 ^ _b1);
            return ret;
        } catch (Exception e) {
            throw new IllegalArgumentException("region uuid is invalid");
        }
    }

    public static byte[] Integer22Bytes(int src) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (src >> 8);
        ret[1] = (byte) src;
        return ret;
    }
}

