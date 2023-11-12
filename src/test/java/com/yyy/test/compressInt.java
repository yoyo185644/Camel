package com.yyy.test;

import java.util.Arrays;

public class compressInt {
    // 压缩数据
    public static int[] compressData(int[] data) {
        int[] compressedData = new int[data.length];
        compressedData[0] = data[0]; // 第一个值不需要差分编码
        for (int i = 1; i < data.length; i++) {
            compressedData[i] = data[i] - data[i - 1];
        }
        return compressedData;
    }

    // 解压数据
    public static int[] decompressData(int[] compressedData) {
        int[] decompressedData = new int[compressedData.length];
        decompressedData[0] = compressedData[0];
        for (int i = 1; i < compressedData.length; i++) {
            decompressedData[i] = decompressedData[i - 1] + compressedData[i];
        }
        return decompressedData;
    }

    public static void main(String[] args) {
        int[] originalData = {10, 15, 12, 18, 22, 30};

        // 压缩数据
        int[] compressedData = compressData(originalData);

        // 解压数据
        int[] decompressedData = decompressData(compressedData);
        System.out.println("原始数据: " + Arrays.toString(originalData));
        System.out.println("压缩后的数据: " + Arrays.toString(compressedData));
        System.out.println("解压后的数据: " + Arrays.toString(decompressedData));



    }
}
