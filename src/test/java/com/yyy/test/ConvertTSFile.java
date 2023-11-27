package com.yyy.test;

import java.io.*;

public class ConvertTSFile {
    private static final String FILE_PATH = "src/test/resources/ElfTestData";
    public static void addLineNumbersToCSV(String inputFilePath, String outputFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                if (line.equals("\"\"")){
                    continue;
                }
                // 在每一行开头添加行号，并用逗号分隔
                String newLine = lineNumber + "," + line;
                // 写入新的CSV文件
                bw.write(newLine);
                bw.newLine();

                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





public static void main(String[] args) throws IOException {
        String[] inFileNames = {"/Air-pressure.csv",
                "/Basel-wind.csv",
                "/Basel-temp.csv",
                "/Bird-migration.csv",
                "/Air-sensor.csv",
                "/POI-lat.csv",
                "/POI-lon.csv",};
    String[] outFileNames = {"/Air-pressure-new.csv",
            "/Basel-wind-new.csv",
            "/Basel-temp-new.csv",
            "/Bird-migration-new.csv",
            "/Air-sensor-new.csv",
            "/POI-lat-new.csv",
            "/POI-lon-new.csv",};
        for (int i = 0; i < 7; i++){
            addLineNumbersToCSV(FILE_PATH+inFileNames[i], FILE_PATH+outFileNames[i]);
        }
    }

}
