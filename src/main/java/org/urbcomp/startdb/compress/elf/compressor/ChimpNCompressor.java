package org.urbcomp.startdb.compress.elf.compressor;

import gr.aueb.delorean.chimp.ChimpN;
import yyy.ts.compress.camel.BPlusTree;

public class ChimpNCompressor implements ICompressor {
    private final ChimpN chimpN;

    public ChimpNCompressor(int previousValues) {
        chimpN = new ChimpN(previousValues);
    }

    @Override public void addValue(double v) {
        chimpN.addValue(v);
    }

    @Override public int getSize() {
        return chimpN.getSize();
    }

    @Override public byte[] getBytes() {
        return chimpN.getOut();
    }

    @Override public void close() {
        chimpN.close();
    }

    @Override
    public BPlusTree getbPlusTree() {
        return null;
    }
}
