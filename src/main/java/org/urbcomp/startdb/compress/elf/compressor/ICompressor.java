package org.urbcomp.startdb.compress.elf.compressor;
import yyy.ts.compress.camel.BPlusTree;
public interface ICompressor {
    void addValue(double v);
    int getSize();
    byte[] getBytes();
    void close();
    BPlusTree getbPlusTree();
    default String getKey() {
        return getClass().getSimpleName();
    }
}
