package yyy.ts.compress.camel;

import java.util.LinkedList;
import java.util.Random;

class SkiplistNode {

    public Double data;
    public SkiplistNode next;
    public SkiplistNode down;
    public int level;

    public SkiplistNode(Double data, int level) {
        this.data = data;
        this.level = level;
    }

}

public class SkipList {

    public LinkedList<SkiplistNode> headNodes;
    public LinkedList<SkiplistNode> tailNodes;

    public int curLevel;

    public Random random;

    public SkipList() {
        random = new Random();

        // headNodes用于存储每一层的头节点
        headNodes = new LinkedList<>();
        // tailNodes用于存储每一层的尾节点
        tailNodes = new LinkedList<>();

        // 初始化跳表时，跳表的层数随机指定
        curLevel = getRandomLevel();
        // 指定了跳表的初始的随机层数后，就需要将每一层的头节点和尾节点创建出来并构建好关系
        SkiplistNode head = new SkiplistNode(Double.MIN_VALUE, 0);
        SkiplistNode tail = new SkiplistNode(Double.MAX_VALUE, 0);
        for (int i = 0; i <= curLevel; i++) {
            head.next = tail;
            headNodes.addFirst(head);
            tailNodes.addFirst(tail);

            SkiplistNode headNew = new SkiplistNode(Double.MIN_VALUE, head.level + 1);
            SkiplistNode tailNew = new SkiplistNode(Double.MAX_VALUE, tail.level + 1);
            headNew.down = head;
            tailNew.down = tail;

            head = headNew;
            tail = tailNew;
        }
    }

    public boolean search(double target) {
        // 从顶层开始寻找，curNode表示当前遍历到的节点
        SkiplistNode curNode = headNodes.getFirst();
        while (curNode != null) {
            if (curNode.next.data == target) {
                // 找到了目标值对应的节点，此时返回true
                return true;
            } else if (curNode.next.data > target) {
                // curNode的后一节点值大于target
                // 说明目标节点在curNode和curNode.next之间
                // 此时需要向下层寻找
                curNode = curNode.down;
            } else {
                // curNode的后一节点值小于target
                // 说明目标节点在curNode的后一节点的后面
                // 此时在本层继续向后寻找
                curNode = curNode.next;
            }
        }
        return false;
    }

    public void add(Double num) {
        // 获取本次添加的值的层数
        int level = getRandomLevel();
        // 如果本次添加的值的层数大于当前跳表的层数
        // 则需要在添加当前值前先将跳表层数扩充
        if (level > curLevel) {
            expanLevel(level - curLevel);
        }

        // curNode表示num值在当前层对应的节点
        SkiplistNode curNode = new SkiplistNode(num, level);
        // preNode表示curNode在当前层的前一个节点
        SkiplistNode preNode = headNodes.get(curLevel - level);
        for (int i = 0; i <= level; i++) {
            // 从当前层的head节点开始向后遍历，直到找到一个preNode
            // 使得preNode.data < num <= preNode.next.data
            while (preNode.next.data < num) {
                preNode = preNode.next;
            }

            // 将curNode插入到preNode和preNode.next中间
            curNode.next = preNode.next;
            preNode.next = curNode;

            // 如果当前并不是0层，则继续向下层添加节点
            if (curNode.level > 0) {
                SkiplistNode downNode = new SkiplistNode(num, curNode.level - 1);
                // curNode指向下一层的节点
                curNode.down = downNode;
                // curNode向下移动一层
                curNode = downNode;
            }
            // preNode向下移动一层
            preNode = preNode.down;
        }
    }

    public boolean erase(int num) {
        // 删除节点的遍历过程与寻找节点的遍历过程是相同的
        // 不过在删除节点时如果找到目标节点，则需要执行节点删除的操作
        SkiplistNode curNode = headNodes.getFirst();
        while (curNode != null) {
            if (curNode.next.data == num) {
                // preDeleteNode表示待删除节点的前一节点
                SkiplistNode preDeleteNode = curNode;
                while (true) {
                    // 删除当前层的待删除节点，就是让待删除节点的前一节点指向待删除节点的后一节点
                    preDeleteNode.next = curNode.next.next;
                    // 当前层删除完后，需要继续删除下一层的待删除节点
                    // 这里让preDeleteNode向下移动一层
                    // 向下移动一层后，preDeleteNode就不一定是待删除节点的前一节点了
                    preDeleteNode = preDeleteNode.down;

                    // 如果preDeleteNode为null，说明已经将底层的待删除节点删除了
                    // 此时就结束删除流程，并返回true
                    if (preDeleteNode == null) {
                        return true;
                    }

                    // preDeleteNode向下移动一层后，需要继续从当前位置向后遍历
                    // 直到找到一个preDeleteNode，使得preDeleteNode.next的值等于目标值
                    // 此时preDeleteNode就又变成了待删除节点的前一节点
                    while (preDeleteNode.next.data != num) {
                        preDeleteNode = preDeleteNode.next;
                    }
                }
            } else if (curNode.next.data > num) {
                curNode = curNode.down;
            } else {
                curNode = curNode.next;
            }
        }
        return false;
    }

    private void expanLevel(int expanCount) {
        SkiplistNode head = headNodes.getFirst();
        SkiplistNode tail = tailNodes.getFirst();
        for (int i = 0; i < expanCount; i++) {
            SkiplistNode headNew = new SkiplistNode(Double.MIN_VALUE, head.level + 1);
            SkiplistNode tailNew = new SkiplistNode(Double.MAX_VALUE, tail.level + 1);
            headNew.down = head;
            tailNew.down = tail;

            head = headNew;
            tail = tailNew;

            headNodes.addFirst(head);
            tailNodes.addFirst(tail);
        }
    }

    private int getRandomLevel() {
        int level = 0;
        while (random.nextInt(2) > 1) {
            level++;
        }
        return level;
    }

    public static void main(String[] args) {
        SkipList skipList = new SkipList();
        Double[] elementsToInsert = {3.0, 6.0, 7.0, 9.0, 12.0, 19.0, 17.0, 26.0, 21.0, 25.0};
        for (double element : elementsToInsert) {
            skipList.add(element);
        }


        // 搜索示例
        double searchValue = 19.0;
        if (skipList.search(searchValue)) {
            System.out.println(searchValue + " found in the skip list.");
        } else {
            System.out.println(searchValue + " not found in the skip list.");
        }
    }

}
