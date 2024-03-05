package com.yyy.test;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 整数部分的case study实验
 */
public class CaseStudy02 {
    private static final String FILE_PATH = "src/test/resources/ElfTestData";

    private int preVal;
    private static final String[] FILENAMES = {
            "/init.csv",    //First run a dataset to ensure the relevant hbase settings of the zstd and snappy compressors
            "/Air-pressure.csv",
            "/Air-sensor.csv",
            "/Basel-temp.csv",
            "/Basel-wind.csv",
            "/Bird-migration.csv",
            "/Bitcoin-price.csv",
            "/Blockchain-tr.csv",
            "/City-temp.csv",
            "/City-lat.csv",
            "/City-lon.csv",
            "/Dew-point-temp.csv",
            "/electric_vehicle_charging.csv",
            "/Food-price.csv",
            "/IR-bio-temp.csv",
            "/PM10-dust.csv",
            "/SSD-bench.csv",
            "/POI-lat.csv",
            "/POI-lon.csv",
            "/Stocks-DE.csv",
            "/Stocks-UK.csv",
            "/Stocks-USA.csv",
            "/Wind-Speed.csv",
    };
//    private static final String[] FILENAMES = {"/init.csv"};
    private static final String STORE_RESULT = "src/test/resources/result/result.csv";


    // 提取每个文件的数据的整数部分并计算不同数据的数量
    public static Map<Integer, Integer> getDiffInterNum(String fileName) throws IOException {
        FileReader fileReader = new FileReader(FILE_PATH + fileName);
        BufferedReader reader = new BufferedReader(fileReader);
        String value;
        List<Integer> int_list = new ArrayList<>();
        while ((value = reader.readLine()) != null){
//            System.out.println(value);
            if ("\"\"".equals(value) || value.isEmpty()){
                continue;
            }
            double doubleValue = Double.parseDouble(value);
            int intValue = (int) doubleValue;

            int_list.add(intValue);
        }
        Map<Integer, Integer> diffInterNumMap = new HashMap<>();
        // 遍历整数列表并更新Map
        for (int num : int_list) {
            if (diffInterNumMap.containsKey(num)) {
                diffInterNumMap.put(num, diffInterNumMap.get(num) + 1);
            } else {
                diffInterNumMap.put(num, 1);
            }
        }

        return diffInterNumMap;
    }

    // 计算与前一个值的差值
    public static Map<Integer, Integer> calculateDifferences(String fileName, Map<Integer, Integer> differencesMap) throws IOException {
        Map<Integer, Integer> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH + fileName))) {
            String line;
            int previousValue = 0;

            while ((line = reader.readLine()) != null) {
                int currentValue = 0;
                if ("\"\"".equals(line) || line.isEmpty()){
                   currentValue = previousValue;
                } else {
                   currentValue = (int)Double.parseDouble(line);
                }

                int difference = Math.abs(currentValue - previousValue);

                // 更新差值在Map中的数量
                differencesMap.put(difference, differencesMap.getOrDefault(difference, 0) + 1);
                // 更新差值在Map中的数量
                map.put(difference, differencesMap.getOrDefault(difference, 0) + 1);

                // 更新前一个值
                previousValue = currentValue;}

        }
        System.out.println(fileName + map);

        return differencesMap;
    }



    public static void main(String[] args) throws IOException {
        Map<Integer, Integer> differencesMap =  new HashMap<>();
        for (String fileName : FILENAMES) {
//            System.out.println("fileName:" + fileName);
//            Map<Integer, Integer> diffInterNumMap = getDiffInterNum(fileName);
//            // 使用entrySet()方法遍历Map
//            for (Map.Entry<Integer, Integer> entry : diffInterNumMap.entrySet()) {
//                int key = entry.getKey();
//                int value = entry.getValue();
//                System.out.println("int_val: " + key + ", num: " + value);
//            }

            try {
                differencesMap = calculateDifferences(fileName, differencesMap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Differences Map: " + differencesMap);

        int totalValues = 0;
        int tempValue = 0;
        for (int value : differencesMap.values()) {
            totalValues += value;
        }
        System.out.println("Total Values: " + totalValues);
        double percentages = 0;
        // 计算每一个key对应value的占比
        for (Map.Entry<Integer, Integer> entry : differencesMap.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            double percentage = (double) value / totalValues * 100;
            percentages += percentage;
//            System.out.println("Key: " + key + ", Value: " + value + ", Percentage: " + percentage + "%");
            if (key>=100 ) {
                tempValue += value;
            }
        }
//        System.out.println(percentages);
//        System.out.println("tempRatio:" + tempValue + ":" + (double) tempValue / totalValues * 100);
    }
}
