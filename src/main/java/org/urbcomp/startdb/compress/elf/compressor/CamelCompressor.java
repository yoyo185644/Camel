package org.urbcomp.startdb.compress.elf.compressor;

import yyy.ts.compress.camel.BPlusDecimalTree;
import yyy.ts.compress.camel.BPlusTree;
import yyy.ts.compress.camel.BPlusTree2;
import yyy.ts.compress.camel.Camel;

public class CamelCompressor implements ICompressor{
    private final Camel camel;

    public CamelCompressor() {
        this.camel = new Camel();
    }

    @Override
    public void addValue(double v) {
        camel.addValue(v);
    }

    @Override
    public int getSize() {
        return camel.getSize();
    }


    public BPlusTree getbPlusTree() {
        return camel.getbPlusTree();
    }

    @Override
    public BPlusTree2 getbPlusTre2() {
        return camel.getbPlusTree2();
    }

    public BPlusDecimalTree getbPlusDecimalTree() {
        return camel.getbPlusDecimalTree();
    }


    @Override
    public byte[] getBytes() {
        return camel.getOut();
    }

    @Override
    public void close() {
        this.camel.close();
    }

    @Override
    public String getKey() {
        return ICompressor.super.getKey();
    }
}
