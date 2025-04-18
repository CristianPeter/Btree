package com.cristianpeter.btree.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BTreeNodeTest {

    @BeforeEach
    void init() {

    }

    @Test
    void testSomething() {

    }

    @Test
    void testInitialStateOnConstructor() {
        int order = 4;
        BTreeNode btreeNode = new BTreeNode(order, null);
        assertEquals(btreeNode.getOrder(), order);
        assertEquals(btreeNode.getKeys().length, order + 1);
        assertEquals(btreeNode.getChildren().length, order + 2);
        assertNull(btreeNode.getParent());
        assertFalse(btreeNode.keysUnderflowing());
        assertFalse(btreeNode.keysOverflowing());
        assertFalse(btreeNode.canLendKey());
    }
}
