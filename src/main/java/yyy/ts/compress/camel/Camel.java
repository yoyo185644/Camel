package yyy.ts.compress.camel;

import gr.aueb.delorean.chimp.OutputBitStream;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import static yyy.ts.compress.camel.CamelUtils.*;
public class Camel {

    private long storedVal = 0;
    private long firstVal = 0;
    private boolean first = true;
    private int size;
    private final static long END_SIGN = Double.doubleToLongBits(Double.NaN);

    private final static int DECIMAL_MAX_COUNT = 4;

    // 按照寻找到的m的值进行保存
    public final static int[] mValueBits = {3, 5, 7, 10, 15};

    public static Map<String, byte[]> compressVal = new HashMap<>();

    private final OutputBitStream out;

    private boolean TreeFlag = true;


    private static BPlusTree bPlusTree;

    private static BPlusDecimalTree bPlusDecimalTree;



    // We should have access to the series?
    public Camel() {
        out = new OutputBitStream(new byte[10000]);  // for elf, we need one more bit for each at the worst case
        size = 0;
    }

    public OutputBitStream getOutputStream() {
        return this.out;
    }

    /**
     * Adds a new long value to the series. Note, values must be inserted in order.
     *
     * @param value next floating point value in the series
     */
    public int addValue(long value) {
        if(first) {
            return writeFirst(value);
        } else {
            return compressValue(value);
        }
    }

    /**
     * Adds a new double value to the series. Note, values must be inserted in order.
     *
     * @param value next floating point value in the series
     */
    public int addValue(double value) {
        if(first) {
            return writeFirst(Double.doubleToRawLongBits(value));
        } else {
            return compressValue(Double.doubleToRawLongBits(value));
        }
    }

    // 写入第一个数据
    private int writeFirst(long value) {
        first = false;
        // 保存第一个数字的整数进行差值计算
        storedVal = (int) Double.longBitsToDouble(value);
        firstVal = (int) Double.longBitsToDouble(value);
        out.writeLong(value, 64);
        size += 64;
        compressVal.put("compressInt", convertToBinary((int) value, 64));
        return size;
    }

    /**
     * Closes the block and writes the remaining stuff to the BitOutput.
     */
    public void close() {
//        addValue(END_SIGN);
        out.writeBit(false);
        out.flush();
    }

    // 数据压缩
    private int compressValue(long value_bits) {
        double value = Double.longBitsToDouble(value_bits);
        // 压缩整数位
        size = compressIntegerValue((int)value);

        // 压缩小数位 默认小数位是1.**
        BigDecimal big_value = BigDecimal.valueOf(value);

        BigDecimal decimal_value = big_value.subtract(BigDecimal.valueOf(big_value.intValue()));

        size = compressDecimalValue(decimal_value);

        if (TreeFlag) {
            TreeFlag = false;
            bPlusTree = new BPlusTree(3);
            bPlusDecimalTree = new BPlusDecimalTree(3);
            // 根据位数创建一个小数位数的索引
            bPlusDecimalTree = bPlusDecimalTree.buildTree(bPlusDecimalTree, compressVal.get("decimalCount"), compressVal.get("xorFlag"), compressVal.get("xorVal"));
        } else {
            bPlusTree.insert(bPlusDecimalTree, compressVal.get("compressInt"), compressVal.get("compressInt"), compressVal.get("decimalCount"),
                    compressVal.get("xorFlag"), compressVal.get("xorVal"), compressVal.get("compressDecimal"), 1);
        }
        this.setbPlusTree(bPlusTree);

        return size;
    }


    public int countDecimalPlaces(BigDecimal value) {
        String valueStr = value.toString();
        int decimalPointIndex = valueStr.indexOf('.');

        if (decimalPointIndex >= 0) {
            return valueStr.length() - decimalPointIndex - 1;
        } else {
            // No decimal point, so there are no decimal places
            return 0;
        }
    }


    // 压缩小数部分
    private int compressDecimalValue(BigDecimal decimal_value) {
        // 计算小数位数
        int decimal_count = countDecimalPlaces(decimal_value);
        BigDecimal decimal_value_prime = decimal_value;

        // 如果小数位数大于4 只保留4位的小数
        if (decimal_count > DECIMAL_MAX_COUNT) {
            decimal_value_prime = decimal_value.setScale(DECIMAL_MAX_COUNT, RoundingMode.HALF_UP);
            decimal_count = DECIMAL_MAX_COUNT;
        }

        out.writeInt(decimal_count, 2); // 保存字节数 00-1 01-2 10-3 11-4
        compressVal.put("decimalCount", convertToBinary(decimal_count, 2));
        size += 2;

//        // 没有小数部分
//        if (decimal_count == 0) {
//            return this.size;
//        }

        // 计算m的值
        BigDecimal threshold = BigDecimal.valueOf(Math.pow(2, -decimal_count)) ;
        BigDecimal m = decimal_value_prime;

        if (decimal_value.compareTo(threshold) >= 0) {  // 计算m的值
            // 标志位：是否计算m的值
            out.writeBit(true);
            size += 1;
            compressVal.put("xorFlag", convertToBinary(1, 1));
            m = decimal_value_prime.subtract(threshold.multiply(decimal_value_prime.divide(threshold, 0, BigDecimal.ROUND_DOWN)));
            // 对于m进行XOR操作
            long xor = Double.doubleToLongBits(decimal_value_prime.doubleValue() + 1) ^
                    Double.doubleToLongBits(m.doubleValue() + 1);
            int trailingZeros = Long.numberOfTrailingZeros(xor);
            // 保存小数位数长度的centerBits 保存decimal_count （四位最多就是1000）
            out.writeLong(xor >>> trailingZeros, decimal_count);
            size += decimal_count;// Store the meaningful bits of XOR
            compressVal.put("xorVal", binaryLongToBinary(xor, trailingZeros));
        } else {  // m就为原来的值
            out.writeBit(false);
            size += 1;
            compressVal.put("xorFlag", convertToBinary(0, 1));
        }

        int m_int = (m.multiply(BigDecimal.valueOf(Math.pow(10, decimal_count)))).intValue();
        // 保存m的值
        if (decimal_count <= 1) { // 如果是1 直接往后读decimal_count+1位
            out.writeInt(m_int, decimal_count+1);
            size += decimal_count+1;
            compressVal.put("compressDecimal", convertToBinary(m_int, decimal_count+1));
        } else if (decimal_count ==2) {
            if (m_int < 8) {
                out.writeInt(0, 1);
                out.writeInt(m_int, 3);
                size += 4;
                compressVal.put("compressDecimal", convertToBinary(m_int, 3));
            }else {
                out.writeInt(1, 1);
                out.writeInt(m_int, 5);
                size += 6;
                compressVal.put("compressDecimal", convertToBinary(m_int, 5));
            }

        } else if (decimal_count == 3) {
            if (m_int < 4) {
                out.writeInt(0, 2);
                out.writeInt(m_int, 2);
                size += 4;
                compressVal.put("compressDecimal", convertToBinary(m_int, 2));
            }else if (m_int < 8){
                out.writeInt(1, 2);
                out.writeInt(m_int, 3);
                size += 5;
                compressVal.put("compressDecimal", convertToBinary(m_int, 3));
            }else if (m_int < 16) {
                out.writeInt(2, 2);
                out.writeInt(m_int, 4);
                size += 6;
                compressVal.put("compressDecimal", convertToBinary(m_int, 4));
            }else {
                out.writeInt(3, 2);
                out.writeInt(m_int, mValueBits[decimal_count-1]);
                size += 2;
                size += mValueBits[decimal_count-1];
                compressVal.put("compressDecimal", convertToBinary(m_int, mValueBits[decimal_count-1]));
            }

        } else {
            if (m_int < 16) {
                out.writeInt(0, 2);
                out.writeInt(m_int, 4);
                size += 6;
                compressVal.put("compressDecimal", convertToBinary(m_int, 4));
            }else if (m_int < 64){
                out.writeInt(1, 2);
                out.writeInt(m_int, 6);
                size += 8;
                compressVal.put("compressDecimal", convertToBinary(m_int, 6));
            }else if (m_int < 256) {
                out.writeInt(2, 2);
                out.writeInt(m_int, 8);
                size += 10;
                compressVal.put("compressDecimal", convertToBinary(m_int, 8));
            }else {
                out.writeInt(3, 2);
                out.writeInt(m_int, mValueBits[decimal_count-1]);
                size += 2;
                size += mValueBits[decimal_count-1];
                compressVal.put("compressDecimal", convertToBinary(m_int, mValueBits[decimal_count-1]));
            }

        }

        return this.size;
    }

    // 压缩整数部分
    private int compressIntegerValue(long int_value) {

        long diff_value =  int_value - storedVal;
        // 用于建索引
        long first_diff_value = int_value - firstVal;
        // 用一个bit表示差值的正负 0表示负数 1表示正数
        size += 1;
        if (diff_value < 0){
            out.writeBit(false);
            diff_value = -diff_value;
        } else {
            out.writeBit(true);
        }

        // 用2个bit表示差值的范围
        size += 2;
        if (diff_value >=0 && diff_value < 2){ // [0,2)
            out.writeInt(0, 2); // 00
            out.writeInt((int) diff_value, 1);
            compressVal.put("compressInt", convertToBinary((int) first_diff_value, 1));
            size += 1;
        } else if (diff_value >=2 && diff_value < 4) { // [2,4)
            out.writeInt(1, 2); // 01
            out.writeInt((int) diff_value, 2);
            compressVal.put("compressInt", convertToBinary((int) first_diff_value, 2));
            size += 2;
        } else if (diff_value >=4 && diff_value < 8) { // [4,8)
            out.writeInt(2, 2); // 10
            out.writeInt((int) diff_value, 3);
            compressVal.put("compressInt", convertToBinary((int) first_diff_value, 3));
            size += 3;
        } else {
            out.writeInt(3, 2); //11  // [8,...)
            out.writeInt((int) diff_value, 16); // 暂用16个字节表示
            compressVal.put("compressInt", convertToBinary((int) first_diff_value, 16));
            size += 16;
        }
        storedVal = int_value;

        return this.size;


    }

    public BPlusTree getbPlusTree() {
        return bPlusTree;
    }

    public BPlusDecimalTree getbPlusDecimalTree() {
        return bPlusDecimalTree;
    }

    public void setbPlusTree(BPlusTree bPlusTree) {
        Camel.bPlusTree = bPlusTree;
    }

    public void setbPlusDecimalTree(BPlusDecimalTree bPlusDecimalTree) {
        Camel.bPlusDecimalTree = bPlusDecimalTree;
    }

    public int getSize() {
        return size;
    }

    public byte[] getOut() {
        return out.getBuffer();
    }
}
