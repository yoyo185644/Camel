package com.yyy.test;
import gr.aueb.delorean.chimp.InputBitStream;
import gr.aueb.delorean.chimp.OutputBitStream;

import java.io.IOException;

public class CaseStudy04 {
    // 保存正整数为二进制数
    public static String savePositiveIntegerToBinary(int value) {
        return Integer.toBinaryString(value);
    }

    // 保存负整数为二进制数（补码表示）
    public static String saveNegativeIntegerToBinary(int value) {
        if (value >= 0) {
            throw new IllegalArgumentException("Input must be a negative integer.");
        }
        int absValue = -value;  // 计算绝对值
        String absBinary = Integer.toBinaryString(absValue);  // 计算绝对值的二进制表示
        int bitLength = absBinary.length();  // 二进制位数
        // 补足位数到32位（int的位数）

        String binaryWithSignBit = "1" + repeatString("0",31 - bitLength) + absBinary;

        return binaryWithSignBit;
    }
    public static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }


    // 复原二进制数为正负整数
    public static int restoreBinaryToInteger(String binary) {
        int signBit = binary.charAt(0) - '0';  // 获取符号位
        int value;
        if (signBit == 0) {
            value = Integer.parseInt(binary, 2);  // 正数情况，直接转换
        } else {
            String absBinary = binary.substring(1);  // 去掉符号位
            value = -Integer.parseInt(absBinary, 2);  // 负数情况，取相反数
        }
        return value;
    }


    public static void main(String[] args) throws IOException {

//        OutputBitStream out = new OutputBitStream(new byte[1000]);
//        double d = -23;
//        long v =  Double.doubleToRawLongBits(d);
////        out.writeLong(-2, 3);
//
//        out.writeLong(v, 5);
//        byte[] bits = out.getBuffer();
//        InputBitStream in = new InputBitStream(bits);
//        long res_long = in.readLong(5);
//        double va = Double.longBitsToDouble(res_long);
        int val = -13;
        String bitsStr = saveNegativeIntegerToBinary(val);

        int toVal = restoreBinaryToInteger(bitsStr);
    }
}
