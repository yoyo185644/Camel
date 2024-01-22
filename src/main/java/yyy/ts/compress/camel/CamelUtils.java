package yyy.ts.compress.camel;

import com.github.jsonldjava.utils.Obj;

import javax.jws.Oneway;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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

    public static int integerBinaryToInt(byte[] binaryArray) {
        int result = 0;
        for (int i = 1; i < binaryArray.length; i++) {
            result = (result << 1) | binaryArray[i];
        }
        return binaryArray[0]==1 ? -1*result : result;
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


    public static Map<String, Object> countXORedVal(int decimalCount, BigDecimal decimal_value) {
        Map<String, Object> res = new HashMap<>();
        // 计算小数位数
        int decimal_count = countDecimalPlaces(decimal_value);
        BigDecimal decimal_value_prime = decimal_value;

        // 如果小数位数大于4 只保留4位的小数
        if (decimal_count > 4) {
            decimal_value_prime = decimal_value.setScale(4, RoundingMode.HALF_UP);
            decimal_count = 4;
        }
        // 计算m的值
        BigDecimal threshold = BigDecimal.valueOf(Math.pow(2, -decimal_count)) ;
        BigDecimal m = decimal_value_prime;
        if (decimal_value.compareTo(threshold) >= 0) {  // 计算m的值
            // 标志位：是否计算m的值
            m = decimal_value_prime.subtract(threshold.multiply(decimal_value_prime.divide(threshold, 0, BigDecimal.ROUND_DOWN)));
            // 对于m进行XOR操作
            long xor = Double.doubleToLongBits(decimal_value_prime.doubleValue() + 1) ^
                    Double.doubleToLongBits(m.doubleValue() + 1);
            int trailingZeros = Long.numberOfTrailingZeros(xor);
            res.put("XORed", binaryLongToBinary(xor, trailingZeros));
            res.put("m", m);
        } else {
            res.put("XORed", new byte[]{0});
            res.put("m", m);
        }
        return res;
    }

    public static int countDecimalPlaces(BigDecimal value) {
        String valueStr = value.toString();
        int decimalPointIndex = valueStr.indexOf('.');

        if (decimalPointIndex >= 0) {
            return valueStr.length() - decimalPointIndex - 1;
        } else {
            // No decimal point, so there are no decimal places
            return 0;
        }
    }

    public static byte[] compressInteger(int diff_value) {
        // 用2个bit表示差值的范围
        byte[] compressRes;
        if (Math.abs(diff_value) >= 0 && Math.abs(diff_value) < 2) { // [0,2)
            compressRes = convertToBinary(diff_value, 2);
        } else if (Math.abs(diff_value) >= 2 && Math.abs(diff_value) < 4) { // [2,4)
            compressRes = convertToBinary(diff_value, 3);
        } else if (Math.abs(diff_value) >= 4 && Math.abs(diff_value) < 8) { // [4,8)
            compressRes = convertToBinary(diff_value, 4);
        } else {
            compressRes = convertToBinary(diff_value, 16);
        }
        return compressRes;
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
        byte[] res = convertToBinary(-1, 2);
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
