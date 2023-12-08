package yyy.ts.compress.camel;

import java.math.BigInteger;
import java.nio.ByteBuffer;

class AVLNode {
    byte[] key;
    int height;

    int count; //记录重复数值
    AVLNode left, right;

    public AVLNode(byte[] key, int height, AVLNode left, AVLNode right){
        this.key = key;
        this.height = height;
        this.left = left;
        this.right = right;
    }
    public AVLNode(byte[] key){
        this.key = key;
        this.height = 1;
        this.count = 1;
    }
}

class AVLTree {
    private AVLNode root;

    // 获取节点的高度
    private int height(AVLNode node) {
        return (node != null) ? node.height : 0;
    }

    // 获取平衡因子
    private int getBalance(AVLNode node) {
        return (node != null) ? height(node.left) - height(node.right) : 0;
    }

    // 更新节点的高度
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    // 右旋转
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // 左旋转
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // 插入节点
    private AVLNode insert(AVLNode node, byte[] key) {
        if (node == null) {
            return new AVLNode(key);
        }

        if (bytesToLong(key) < bytesToLong(node.key)) {
            node.left = insert(node.left, key);
        } else {
            node.right = insert(node.right, key);
        }

        // 更新节点高度
        updateHeight(node);

        // 获取平衡因子
        int balance = getBalance(node);

        // 平衡维护
        if (balance > 1) {
            if (bytesToLong(key) < bytesToLong(node.left.key)) {
                return rightRotate(node);
            } else {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        }

        if (balance < -1) {
            if (bytesToLong(key) > bytesToLong(node.right.key)) {
                return leftRotate(node);
            } else {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }

        return node;
    }

    // 公共方法插入节点
    public void insert(byte[] key) {
        root = insert(root, key);
    }


    public static int bytesToLong(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = (result << 1) | bytes[i];
        }
        return result;
    }

    public static void main(String[] args) {
        AVLTree avlTree = new AVLTree();

        avlTree.insert(new byte[]{1,0});
        avlTree.insert(new byte[]{1,1});
        avlTree.insert(new byte[]{1,1,1});
        avlTree.insert(new byte[]{1,0,1});
        avlTree.insert(new byte[]{0,1,1});
        // Add more insertions as needed

        // Perform other operations as needed
    }
}
