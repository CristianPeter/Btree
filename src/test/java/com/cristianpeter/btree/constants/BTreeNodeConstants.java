package com.cristianpeter.btree.constants;

import com.cristianpeter.btree.core.BTreeNode;

public class BTreeNodeConstants {

    public static final int VALUE = 10;
    public static final int ORDER_3 = 3;
    public static final int ORDER_4 = 4;

    public static BTreeNode ROOT_NODE_1() {
        return new BTreeNode(BTreeNodeConstants.ORDER_4, null);
    }

    public static BTreeNode LEAF_NODE_1() {
        BTreeNode rootNode1 = BTreeNodeConstants.ROOT_NODE_1();
        return new BTreeNode(BTreeNodeConstants.ORDER_4, rootNode1);
    }

}
