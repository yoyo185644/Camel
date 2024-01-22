package yyy.ts.compress.camel;
import fi.iki.yak.ts.compression.gorilla.Value;
import org.checkerframework.checker.units.qual.C;
import yyy.ts.compress.camel.CamelUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValueSearch {
    public List<TSNode> searchValue(BPlusDecimalTree bPlusDecimalTree, BPlusTree2 bPlusTree2, double value){
        BigDecimal big_value = BigDecimal.valueOf(value);
        BigDecimal decimal_value = big_value.subtract(BigDecimal.valueOf(big_value.intValue()));
        int decimal_count = CamelUtils.countDecimalPlaces(decimal_value);
//        byte[] intBytes = CamelUtils.compressInteger((int)value);
//        byte[] decimalBytes = CamelUtils.compressDecimal(decimal_count, decimal_value.intValue());

        // 小数部分的list
        List<TSNode> decimalList = new ArrayList<>();
        Map<String, Object> decimalRes = CamelUtils.countXORedVal(decimal_count, decimal_value);
        byte[] XORed = (byte[]) decimalRes.get("XORed");
        BigDecimal m = (BigDecimal) decimalRes.get("m");
        KeyNode keyNode = bPlusDecimalTree.searchKeyNode(m.intValue());
        if (XORed.length == 1 && XORed[0] == 0) {
            decimalList = keyNode.flagFalseNode.tsNodeList;
        } else {
            DecimalNode decimalNode = bPlusDecimalTree.searchDecimalNode(keyNode.flagTrueNode.decimalNodes, XORed);
            decimalList = decimalNode.tsNodeList;
        }

        // 整数部分的list
        List<TSNode> integerList = new ArrayList<>();
        int firstVal = 0;
        int diffVal = big_value.intValue()-firstVal;

        IntKeyNode2 intKeyNode2 = bPlusTree2.searchKeyNode(bPlusTree2.getRoot(bPlusTree2), diffVal);
        integerList = intKeyNode2.tsNodesList;

        // 找到两个List的交集
        // 使用Stream API找到交集
        List<TSNode> intersection = integerList.stream()
                .filter(decimalList::contains)
                .collect(Collectors.toList());
        return intersection;
    }
    public static void main(String[] args) {

    }
}


