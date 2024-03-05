package com.yyy.test;

public class test {
    public static int countDecimalDigits(double number) {
        // 判断输入参数是否合法
        // 取绝对值，因为我们只关心小数位数，不关心符号
        // 计算缩放因子，即小数点移动的位数
        int scaleFactor = 0;
        while (number < 1) {
            number *= 10;
            scaleFactor++;
        }

        // 如果数字正好是1，说明是一个整数，没有小数部分
        return scaleFactor - (number == 1 ? 1 : 0);
    }

    public static void main(String[] args) {
        double num = 1453.45674523;

        System.out.println("小数 " + num + " 的位数为：" + Math.abs((int)(Math.log10(num)) + 1));
    }
}
