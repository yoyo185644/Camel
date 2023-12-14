package yyy.ts.compress.camel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static yyy.ts.compress.camel.CamelUtils.*;

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

    public void insert(BPlusDecimalTree decimalTree, byte[] key, byte[] compressInt,byte[] decimalCount, byte[] xorFlag, byte[] xorVal, byte[] compressDecimal, long timestamp) {
        // 查询是否存在整数部分，如果存在就插入到小数部分的树中
        IntKeyNode intKeyNode = searchKeyNode(root, binaryToInt(key));
        if (intKeyNode!=null) {
            decimalTree.insert(xorFlag, xorVal, compressInt, compressDecimal, timestamp);
            intKeyNode.bPlusDecimalTree  = decimalTree;
            return;
        }
        // 检查当前树的根节点是否已经达到了其最大容量 如果已经达到最大容量，需要进行分裂操作
        if (root.keys.size() == (2 * order) - 1) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, decimalTree, key, compressInt, decimalCount, xorFlag, xorVal, compressDecimal, timestamp);
    }

    private void insertNonFull(BPlusTreeNode node, BPlusDecimalTree decimalTree, byte[] key, byte[] compressInt,
                               byte[] decimalCount, byte[] xorFlag, byte[] xorVal, byte[] compressDecimal, long timestamp) {
        int i = node.keys.size() - 1;

        // 对于第一次出现的整数，直接插入到二级索引 (补充逻辑)
        if (node.isLeaf) {
            while (i >= 0 && binaryToInt(key) < binaryToInt(node.keys.get(i).key)) {
                i--;
            }
            decimalTree.insert(xorFlag, xorVal, compressInt, compressDecimal, timestamp);

            node.keys.add(i + 1, new IntKeyNode(compressInt, decimalTree));

        } else {
            while (i >= 0 && binaryToInt(key) < binaryToInt(node.keys.get(i).key)) {
                i--;
            }
            i++;

            try {
                if (node.children.get(i).keys.size() == (2 * order) - 1) {
                    splitChild(node, i);
                    if (binaryToInt(key) > binaryToInt(node.keys.get(i).key)) {
                        i++;
                    }
                }
                insertNonFull(node.children.get(i), decimalTree, key, compressInt, decimalCount, xorFlag, xorVal, compressDecimal, timestamp);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

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
                if (order >= child.children.size()){
                    break;
                }
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
        while (i < node.keys.size() && binaryToInt(key) > binaryToInt(node.keys.get(i).key)) {
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

    private IntKeyNode searchKeyNode(BPlusTreeNode node, int key) {
        int i = 0;
        while (i < node.keys.size() && key > binaryToInt(node.keys.get(i).key)) {
            i++;
        }

        if (node.isLeaf) {
            // Key not found
            List<IntKeyNode> keyNodes = node.keys;
            IntKeyNode keyNode = binarySearchByKey(keyNodes, key);
            return keyNode;
        }

        if (i >= node.children.size()) {
            return searchKeyNode(node.children.get(node.children.size()-1), key);
        }

        // Recur to the next level
        return searchKeyNode(node.children.get(i), key);
    }

    private static IntKeyNode binarySearchByKey(List<IntKeyNode> keys, Integer targetKey) {
        int low = 0;
        int high = keys.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;

            int compareResult = targetKey.compareTo(binaryToInt(keys.get(mid).key));

            if (compareResult == 0) {
                return keys.get(mid); // 找到关键字对应的值
            } else if (compareResult < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return null; // 未找到关键字对应的值
    }

    // 层次遍历
    public long levelOrderTraversal(BPlusTree tree){
        int size = 0;
        if (tree == null) {
            System.out.println("The tree is empty.");
            return size;
        }

        Queue<BPlusTreeNode> queue = new LinkedList<>();
        queue.offer(tree.root);

        while (!queue.isEmpty()) {
            BPlusTreeNode current = queue.poll();
            int keySize  = current.keys.size();
            for (int i =0; i < keySize; i++) {
                size = size + current.keys.get(i).key.length;
            }

            if (current.children != null) {
                for (BPlusTreeNode child : current.children) {
                    if (child != null) {
                        queue.offer(child);
                    }
                }
            }
        }
        return size;
    }



    public static void main(String[] args) {
        // 创建一个B+树，假设阶数为3
        BPlusTree bPlusTree = new BPlusTree(3);
        BPlusDecimalTree deciamlPlusTree = new BPlusDecimalTree(3);
        // 根据位数创建一个小数位数的索引
//        deciamlPlusTree = deciamlPlusTree.buildTree(deciamlPlusTree, 2);
        // 插入一些关键字
        byte[][] keysToInsert = {new byte[]{1,0,1}, new byte[]{1,0,1},  new byte[]{1,0,1}, new byte[]{1,0,1},  new byte[]{1,0,1}, new byte[]{1,0,1}, new byte[]{1,0,1}};
        byte[][] compressInt = {new byte[]{1,0,1},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};
        byte[][] compressDecimal = {new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};
        byte[][] decimalCount = {new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10},new byte[]{10}};

        long[] timestamp = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};
        for (int i=0; i< keysToInsert.length; i++) {
//            bPlusTree.insert(deciamlPlusTree, keysToInsert[i], compressInt[i], compressDecimal[i], timestamp[i]);
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

