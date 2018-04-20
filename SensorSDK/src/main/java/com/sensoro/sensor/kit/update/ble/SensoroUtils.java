package com.sensoro.sensor.kit.update.ble;

import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.URLUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Sensoro on 15/1/16.
 */
public class SensoroUtils {
    private static final char[] HEX_CHAR_TABLE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final SparseArray<String> URI_SCHEMES = new SparseArray<String>() {{
        put((byte) 0, "http://www.");
        put((byte) 1, "https://www.");
        put((byte) 2, "http://");
        put((byte) 3, "https://");
        put((byte) 4, "urn:uuid:");
    }};

//    public static byte[] hexStringToByte(String hex) {
//        int len = (hex.length() / 2);
//        byte[] result = new byte[len];
//        char[] achar = hex.toCharArray();
//        for (int i = 0; i < len; i++) {
//            int pos = i * 2;
//            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
//        }
//        return result;
//    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     *
     * @param b
     *            byte[]
     * @return String
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += "0x" + hex.toUpperCase();
        }
        return ret;
    }



    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static final SparseArray<String> URL_CODES = new SparseArray<String>() {{
        put((byte) 0, ".com/");
        put((byte) 1, ".org/");
        put((byte) 2, ".edu/");
        put((byte) 3, ".net/");
        put((byte) 4, ".info/");
        put((byte) 5, ".biz/");
        put((byte) 6, ".gov/");
        put((byte) 7, ".com");
        put((byte) 8, ".org");
        put((byte) 9, ".edu");
        put((byte) 10, ".net");
        put((byte) 11, ".info");
        put((byte) 12, ".biz");
        put((byte) 13, ".gov");
    }};

    private static final int FIVE_SECONDS = 5;
    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = 3600;  // 60*60
    private static final int ONE_DAY = 86400;  // 60*60*24
    private static final int SEVEN_DAYS = 604800;  // 60*60*24*7
    private static final int THIRTY_DAYS = 2592000;  // 60*60*24*30


    /**
     * 将UUID转换为字节数组
     *
     * @param uuid
     * @return
     */
    public static byte[] convertUUIDToBytes(String uuid) {
        uuid = uuid.replace("-", ""); // 去掉 uuid 中的 '-'
        byte[] uuidBytes = SensoroUtils.HexString2Bytes(uuid);
        return uuidBytes;
    }

    /**
     * 将 bytes 数据解析为byte[] 列表
     *
     * @param bytes
     * @return
     */
    public static ArrayList<byte[]> parseBytes2ByteList(byte[] bytes) {
        ArrayList<byte[]> byteList = null;
        if (bytes != null) {
            byteList = new ArrayList<byte[]>();
            for (int i = 0; i < bytes.length; i++) {
                int length = (int) bytes[i] & 0xff;
                if (length == 0) {
                    return byteList;
                } else {
                    byte[] byteData = new byte[length + 1];
                    System.arraycopy(bytes, i, byteData, 0, length + 1);
                    byteList.add(byteData);
                    i = i + length;
                }
            }
        }
        return byteList;
    }

    public static byte[] encodeUrl(String url) {
        for (int i = 0; i < URI_SCHEMES.size(); i++) {
            if (url.startsWith(URI_SCHEMES.get(i))) {
                url = url.replace(URI_SCHEMES.get(i), String.valueOf((char) ((byte) i)));
            }
        }

        for (int i = 0; i < URL_CODES.size(); i++) {
            url = url.replace(URL_CODES.get(i), String.valueOf((char) ((byte) i)));
        }

        byte[] bytes = new byte[url.length()];
        for (int i = 0; i < url.length(); i++) {
            bytes[i] = ((byte) url.charAt(i));
        }
        return bytes;
    }

    public static String decodeUrl(byte[] urlBytes) {
        StringBuilder url = new StringBuilder();
        int offset = 0;
        try {
        byte b = urlBytes[offset++];
        String scheme = URI_SCHEMES.get(b);
        if (scheme != null) {
            url.append(scheme);
            if (URLUtil.isNetworkUrl(scheme)) {
                return decodeUrl(urlBytes, offset, url);
            }
        }
        return url.toString();
        } catch (Exception e) {
            return null;
        }

    }

    private static String decodeUrl(byte[] serviceData, int offset, StringBuilder urlBuilder) {
        while (offset < serviceData.length) {
            byte b = serviceData[offset++];
            String code = URL_CODES.get(b);
            if (code != null) {
                urlBuilder.append(code);
            } else {
                urlBuilder.append((char) b);
            }
        }
        return urlBuilder.toString();
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
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * HMacSHA512 加密
     *
     * @param data
     * @param passwordKey
     * @return
     */
    public static byte[] HMacSHA512(byte[] data, String passwordKey) {
        Mac shaMac;
        byte[] secretBytes = passwordKey.getBytes();
        byte[] signatureBytes = null;
        try {
            shaMac = Mac.getInstance("HmacSHA512");
            SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA512");
            shaMac.init(secretKey);
            signatureBytes = shaMac.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return signatureBytes;
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHAR_TABLE[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHAR_TABLE[v & 0x0F];
        }
        return new String(hexChars).toUpperCase();
    }

    public static int getHexCharValue(char c) {
        int index = 0;
        for (char c1 : HEX_CHAR_TABLE) {
            if (c == c1) {
                return index;
            }
            index++;
        }
        return 0;
    }

    // 解密
    public static byte[] decrypt_AES_128(byte[] src, byte[] key) {
        byte[] original = null;
        // 判断Key是否正确
        if (key == null) {
            return null;
        }
        // 判断Key是否为16位
        if (key.length != 16) {
            return null;
        }
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            original = cipher.doFinal(src);
        } catch (Exception e) {
            return null;
        }
        return original;
    }

    // AES256 解密
    public static String decrypt_AES_256(String src, String key) {
        // 判断Key是否正确
        if (key == null) {
            return null;
        }
        // 判断src是否正确
        if (src == null) {
            return null;
        }
        try {
            SecretKey secretKey = getKey(key);

            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encrypedPwdBytes = Base64.decode(src, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes);
            return decrypedValue;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates a SecretKeySpec for given password
     *
     * @param password
     * @return SecretKeySpec
     * @throws UnsupportedEncodingException
     */
    private static SecretKeySpec getKey(String password) throws UnsupportedEncodingException {

        // You can change it to 128 if you wish
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        // explicitly fill with zeros
        Arrays.fill(keyBytes, (byte) 0x0);

        // if password is shorter then key length, it will be zero-padded
        // to key length
        byte[] passwordBytes = password.getBytes("UTF-8");
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    public static double calculateAccuracy(int txPower, double rssi) {
        if (txPower == 0 || txPower == Integer.MAX_VALUE) {
            Log.v("", "");
            return -1.0;
        }
        if (rssi == 0) {
            return -1.0; // 无法确定距离,返回-1
        }

        double accuracy = 0;
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            accuracy = Math.pow(ratio, 10);
        } else {
            accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
        return new BigDecimal(Double.toString(accuracy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static String parseEddystoneURL(byte[] eddystoneURLBytes) {
        String url = null;
        return url;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
