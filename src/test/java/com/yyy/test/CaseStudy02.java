package com.yyy.test;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) throws IOException {

        for (String fileName : FILENAMES) {
            System.out.println("fileName:" + fileName);
            Map<Integer, Integer> diffInterNumMap = getDiffInterNum(fileName);
            // 使用entrySet()方法遍历Map
            for (Map.Entry<Integer, Integer> entry : diffInterNumMap.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();
                System.out.println("int_val: " + key + ", num: " + value);
            }
        }

    }
}
