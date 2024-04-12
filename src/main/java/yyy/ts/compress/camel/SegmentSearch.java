package yyy.ts.compress.camel;

import java.util.List;

/**
 * 块查询
 */
public class SegmentSearch {
    public List<TSNode> searchSegment (BPlusTree bPlusTree) {
        long start_time = System.nanoTime();
        List<TSNode> TSNodes = bPlusTree.levelOrderTraversalList(bPlusTree);
//        System.out.println("segmentSearch:" + (System.nanoTime()-start_time)/1000.0);
        return TSNodes;
    }

    public List<TSNode> searchSegmentDecimal (BPlusDecimalTree bPlusTree) {
        long start_time = System.nanoTime();
        List<TSNode> TSNodes = bPlusTree.getAllLeaf(bPlusTree);
//        System.out.println("segmentSearch:" + (System.nanoTime()-start_time)/1000.0);
        return TSNodes;
    }

}
