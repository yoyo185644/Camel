package org.urbcomp.startdb.compress.elf.compressor;
import yyy.ts.compress.camel.BPlusDecimalTree;
import yyy.ts.compress.camel.BPlusTree;
public interface ICompressor {
    void addValue(double v);
    int getSize();
    byte[] getBytes();
    void close();
    BPlusTree getbPlusTree();
    BPlusDecimalTree getbPlusDecimalTree();
    default String getKey() {
        return getClass().getSimpleName();
    }
}
