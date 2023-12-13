package yyy.ts.compress.camel;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * camel算法专用工具类
 */
public class CamelUtils {
    public final static int[] mValueBits = {3, 5, 7, 10, 15};
    public static int binaryToInt(byte[] binaryArray) {
        int result = 0;
        for (int i = 0; i < binaryArray.length; i++) {
            result = (result << 1) | binaryArray[i];
        }
        return result;
    }

    public static byte[] convertToBinary(int num, int length) {
        // 将整数转换为二进制字符串
        byte[] binaryArray = new byte[length];

        for (int i = 0; i < length; i++) {
            binaryArray[length - i - 1] = (byte) ((num >> i) & 0x01);
        }

        return binaryArray;
    }

//    public static byte[] binaryStringToByteArray(String binaryString) {
//        int length = binaryString.length();
//        if (length % 8 != 0) {
//            throw new IllegalArgumentException("Binary string length must be a multiple of 8");
//        }
//
//        int byteArrayLength = length / 8;
//        byte[] byteArray = new byte[byteArrayLength];
//
//        for (int i = 0; i < byteArrayLength; i++) {
//            String byteString = binaryString.substring(i * 8, (i + 1) * 8);
//            byteArray[i] = (byte) Integer.parseInt(byteString, 2);
//        }
//
//        return byteArray;
//    }

    //压缩小数部分
    public static byte[] compressDecimal(int decimalCount, int key) {
        if (decimalCount <= 1) { // 如果是1 直接往后读decimal_count+1位
            return convertToBinary(key, decimalCount+1);
        } else if (decimalCount ==2) {
            if (key < 8) {
                return convertToBinary(key, 3);
            }else {
                return convertToBinary(key, 5);
            }

        } else if (decimalCount == 3) {
            if (key < 4) {
                return convertToBinary(key, 2);
            }else if (key < 8){
                return convertToBinary(key, 3);
            }else if (key < 16) {
                return convertToBinary(key, 4);
            }else {
                return convertToBinary(key, mValueBits[decimalCount-1]);
            }

        } else {
            if (key < 16) {
                return convertToBinary(key, 4);
            }else if (key < 64){
                return convertToBinary(key, 6);
            }else if (key < 256) {
                return convertToBinary(key, 8);
            }else {
                return convertToBinary(key, mValueBits[decimalCount-1]);
            }

        }
    }

    public static byte[] binaryLongToBinary(long num, int m) {
        String str = Long.toBinaryString(num>>m);
        byte[] bytes = str.getBytes();
        for (int i = 0 ; i< bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] - 48);
        }
        return bytes;
    }

    public static String getIEEE754(Double doubleValue) {
//        float floatValue = 12.345f; // 你要转换的浮点数

        // 使用Double.doubleToRawLongBits()方法将双精度浮点数转换为64位长整数
        long longValue = Double.doubleToRawLongBits(doubleValue);

        // 将长整数表示为64位二进制字符串
        String binaryString = Long.toBinaryString(longValue);

        // 补足到64位
        while (binaryString.length() < 64) {
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }


    public static void main(String[] args) {
        byte[] res = convertToBinary(1, 1);
        int val = binaryToInt(res);

        long xor = Double.doubleToLongBits(  1.8) ^
                Double.doubleToLongBits(  1.3);
        String str = Long.toBinaryString(xor>>50);
        byte[] bytes = str.getBytes();
        for (int i = 0 ; i< bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] - 48);
        }
//        byte[] bytes=  binaryStringToByteArray(str);
        System.out.println(res);
    }
}
