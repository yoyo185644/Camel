package com.yyy.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class test {
    public static void main(String[] args) {
//        double factor = 1;
//        int decimalPlaces = 0;
//        double value = 0.58; // 示例双精度浮点数
//        double epsilon = 0.0000001; // 设置一个很小的阈值
//        while (Math.abs(value * factor - Math.round(value * factor)) > epsilon) {
//            factor *= 10.0;
//            decimalPlaces++;
//        }
        double value = 0.08;
//        // 将浮点数转换为整数
//        long intValue = (long) (value * 1000L);

        // 获取小数部分的整数值
        long decimalValue = ((long) (value * 1000L) % 1000L)/10;

        System.out.println("小数部分的位数估算是: " + decimalValue);

//        while (Math.abs(value * factor - Math.round(value * factor)) > epsilon) {
//            System.out.println(value * factor);
//            System.out.println(Math.round(value * factor));
//            factor *= 10.0;
//            decimalPlaces++;
//        }

//         尾数位的长度即是小数部分的位数估算

//        int xor = 2;
//        // 根据leadingZeroSNum和XOR拼接xorVal
//        long shiftedValue = xor << (52 - 1);
//        String xorString = String.format("%64s", Long.toBinaryString(shiftedValue)).replace(' ', '0');
//        System.out.println(xorString);
//        long shiftedValue2 = 0x1234567890ABCDEFL; // 示例值
//        long mask = -1L << (64 - Long.toBinaryString(shiftedValue2).length()); // 创建掩码
//        long paddedValue = shiftedValue2 | mask; // 应用掩码
//
//        // 将64位长的值转换为二进制字符串
//        String binaryString = Long.toBinaryString(paddedValue);
//        System.out.println(binaryString);
    }

    private static int countTrailingZeros(BigDecimal number) {
        // 确保number是一个正数


        // 返回尾数中0的数量
        return 0;
    }
}
