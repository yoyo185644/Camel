package yyy.ts.compress.camel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RangeSearch {
    public List<TSNode> searchRangeValue(BPlusTree bPlusTree, double begin, double end){
        BigDecimal begin_value = BigDecimal.valueOf(begin);
        BigDecimal begin_decimal_value = begin_value.subtract(BigDecimal.valueOf(begin_value.intValue()));
        int begin_decimal_count = CamelUtils.countDecimalPlaces(begin_decimal_value);
        Map<String, Object> begin_decimalRes = CamelUtils.countXORedVal(begin_decimal_count, begin_decimal_value);

        BigDecimal end_value = BigDecimal.valueOf(begin);
        BigDecimal end_decimal_value = end_value.subtract(BigDecimal.valueOf(end_value.intValue()));
        int end_decimal_count = CamelUtils.countDecimalPlaces(end_decimal_value);
        Map<String, Object> end_decimalRes = CamelUtils.countXORedVal(end_decimal_count, end_decimal_value);

        List<TSNode> res = new ArrayList<>();
        int firstVal = 0;
        int begin_diff_val = begin_value.intValue() - firstVal;
        int end_diff_val = end_value.intValue() - firstVal;
        for (int i = begin_diff_val; i <= end_diff_val ; i++) {
            IntKeyNode intKeyNode =  bPlusTree.searchKeyNode(bPlusTree.getRoot(bPlusTree), begin_diff_val);
            BPlusDecimalTree bPlusDecimalTree = intKeyNode.bPlusDecimalTree;
            

        }
        return res;

    }
    public static void main(String[] args) {

    }
}
