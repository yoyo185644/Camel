package yyy.ts.compress.camel;

public class TSNode {
    byte[] valueInt;
    byte[] valueDecimal;
    long timeStamp;
    TSNode nextTS;
    TSNode beforeTS;

    public TSNode(byte[] valueInt, byte[] valueDecimal, long timeStamp) {
        this.valueInt = valueInt;
        this.valueDecimal = valueDecimal;
        this.timeStamp = timeStamp;
        this.nextTS = null;
    }
}
