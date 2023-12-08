package yyy.ts.compress.camel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class IntKeyNode{
    byte[] key;
    BPlusDecimalTree bPlusDecimalTree;

    public IntKeyNode(byte[] key, BPlusDecimalTree bPlusDecimalTree) {
        this.key = key;
        this.bPlusDecimalTree = bPlusDecimalTree;

    }

}



class BPlusTreeNode {
    boolean isLeaf;
    List<IntKeyNode> keys;
    List<BPlusTreeNode> children;


    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }
}


public class BPlusTree {
    private BPlusTreeNode root;

    private int order;

    // 用一个指针永远指向下一个值
    public static KeyNode previousTSNode = null;


    public BPlusTree(int order) {
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }



    public void insert(BPlusDecimalTree decimalTree, byte[] key, byte[] compressInt, byte[] compressDecimal, long timestamp) {
        // 查询是否存在整数部分，如果存在就插入到小数部分的树中
        boolean isExist = searchKey(root, key);
        if (isExist) {
            System.out.println("重复值" + key);
            return;
        }
        // 检查当前树的根节点是否已经达到了其最大容量 如果已经达到最大容量，需要进行分裂操作
        if (root.keys.size() == (2 * order) - 1) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, decimalTree, key, compressInt, compressDecimal, timestamp);
    }

    private void insertNonFull(BPlusTreeNode node, BPlusDecimalTree decimalTree, byte[] key, byte[] compressInt, byte[] compressDecimal, long timestamp) {
        int i = node.keys.size() - 1;
        // 获取小数位数
        byte[] decimalCount = new byte[]{compressDecimal[0], compressDecimal[1]};
        // 获取是否通过XOR的flag
        byte flag = compressDecimal[2];
        byte[] xorVal = null;
        byte[] decimalVal = null;

        if (flag == 1) { // 通过XOR之后的centerbits和保存的值
            xorVal = Arrays.copyOfRange(compressDecimal, 3, (int) (3 + bytesToLong(decimalCount)));
            decimalVal = Arrays.copyOfRange(compressDecimal, (int) (3 + bytesToLong(decimalCount)), compressDecimal.length);
        } else { // 没有通过XOR的保存的值
            decimalVal = Arrays.copyOfRange(compressDecimal, 3, compressDecimal.length);
        }


        //查询所有底层的node

        // 如果重复的整数不插入，直接插入到二级索引 (补充逻辑)
        if (node.isLeaf) {
            while (i >= 0 && bytesToLong(key) < bytesToLong(node.keys.get(i).key)) {
                i--;
            }
            decimalTree.insert(flag, xorVal, compressInt, decimalVal, timestamp);

            node.keys.add(i + 1, new IntKeyNode(compressInt, decimalTree));

        } else {
            while (i >= 0 && bytesToLong(key) < bytesToLong(node.keys.get(i).key)) {
                i--;
            }
            i++;

            if (node.children.get(i).keys.size() == (2 * order) - 1) {
                splitChild(node, i);
                if (bytesToLong(key) > bytesToLong(node.keys.get(i).key)) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), decimalTree, key, compressInt, compressDecimal, timestamp);
        }
    }


    private void splitChild(BPlusTreeNode parent, int index) {
        BPlusTreeNode child = parent.children.get(index);
        BPlusTreeNode newChild = new BPlusTreeNode(child.isLeaf);

        parent.keys.add(index, child.keys.get(order - 1));

        for (int j = 0; j < order - 1; j++) {
            newChild.keys.add(child.keys.remove(order));
        }

        if (!child.isLeaf) {
            for (int j = 0; j < order; j++) {
                newChild.children.add(child.children.remove(order));
            }
        }

        parent.children.add(index + 1, newChild);
    }

    public boolean search(byte[] key) {
        return searchKey(root, key);
    }

    private boolean searchKey(BPlusTreeNode node, byte[] key) {
        int i = 0;
        while (i < node.keys.size() && bytesToLong(key) > bytesToLong(node.keys.get(i).key)) {
            i++;
        }

        if (i < node.keys.size() && key == node.keys.get(i).key) {
            // Key found
            return true;
        }

        if (node.isLeaf) {
            // Key not found
            return false;
        }

        // Recur to the next level
        return searchKey(node.children.get(i), key);
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    public static void main(String[] args) {
        // 创建一个B+树，假设阶数为3
        BPlusTree bPlusTree = new BPlusTree(3);
        BPlusDecimalTree deciamlPlusTree = new BPlusDecimalTree(3);
        // 根据位数创建一个小数位数的索引
        deciamlPlusTree = deciamlPlusTree.buildTree(deciamlPlusTree, 2);
        // 插入一些关键字
        byte[][] keysToInsert = {new byte[]{10}, new byte[]{10}, new byte[]{10}, new byte[]{10}, new byte[]{10}, new byte[]{10}, new byte[]{10}};
        byte[][] compressInt = {new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};
        byte[][] compressDecimal = {new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};
        byte[][] decimalCount = {new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};

        long[] timestamp = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};
        for (int i=0; i< keysToInsert.length; i++) {
            bPlusTree.insert(deciamlPlusTree, keysToInsert[i], compressInt[i], compressDecimal[i], timestamp[i]);
        }

        // 查找关键字并输出结果
        byte[][] keysToSearch = {new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};

        System.out.println(bPlusTree.root.keys);
        for (byte[] key : keysToSearch) {
            boolean found = bPlusTree.search(key);
            System.out.println("Key " + key + " found: " + found);
        }
    }
}

