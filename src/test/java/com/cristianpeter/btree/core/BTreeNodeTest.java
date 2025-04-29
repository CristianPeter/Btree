package com.cristianpeter.btree.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.cristianpeter.btree.constants.BTreeNodeConstants;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;

class BTreeNodeTest {

    @Test
    @DisplayName("Constructor initializes state correctly")
    void testInitialStateOnConstructor() {
        BTreeNode node = new BTreeNode(BTreeNodeConstants.ORDER_4, null);

        assertAll("Initial State", () -> assertEquals(BTreeNodeConstants.ORDER_4, node.getOrder()),
                () -> assertEquals(BTreeNodeConstants.ORDER_4 + 1, node.getKeys().length),
                () -> assertEquals(BTreeNodeConstants.ORDER_4 + 2, node.getChildren().length), () -> assertNull(node.getParent()),
                () -> assertFalse(node.keysUnderflowing()), () -> assertFalse(node.keysOverflowing()),
                () -> assertFalse(node.canLendKey()));
    }

    @Test
    @DisplayName("Add a single key to BTreeNode")
    void addKeyTest() {
        BTreeNode node = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        node.addKey(BTreeNodeConstants.VALUE);

        assertAll("Add Key", () -> assertEquals(BTreeNodeConstants.VALUE, node.getKey(0)),
                () -> assertEquals(1, node.getKeysSize()));
    }

    // Nested classes need to be separated with $, not .: example.JUnitNestedMethodSourceTests$Source#getArgs.
    @ParameterizedTest(name = "Add child: {0}")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideChildrenWithExpectations")
    @DisplayName("Add a child node and validate position")
    void addChildTest(BTreeNode existingChild, BTreeNode childToAdd, BTreeNode expectedAt0, BTreeNode expectedAt1) {
        BTreeNode parent = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        parent.addChild(0, existingChild);
        parent.addChild(0, childToAdd);

        assertAll("Child Positions After Insert", () -> assertEquals(expectedAt0, parent.getChild(0)),
                () -> assertEquals(expectedAt1, parent.getChild(1)));
    }

    @ParameterizedTest
    @DisplayName("Keys overflowing test")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeAndOverflowingExpectation")
    void keysOverflowingTest(BTreeNode node, boolean expected) {
        assertEquals(expected, node.keysOverflowing());
    }

    @ParameterizedTest
    @DisplayName("keys underflowing test")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeAndUnderflowingExpectation")
    void keysUnderflowTest(BTreeNode node, boolean expected) {
        assertEquals(expected, node.keysUnderflowing());
    }

    @ParameterizedTest
    @DisplayName("can lend a key test")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeLendKeyTest")
    void canLendKeyTest(BTreeNode node, boolean expected) {
        assertEquals(expected, node.canLendKey());
    }

    @ParameterizedTest
    @DisplayName("can get a key at a position")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForGetKeys")
    void getKeyTest(BTreeNode node, int[] values) {
        assertAll("Getting keys", () -> assertEquals(values[0], node.getKey(0)), () -> assertEquals(values[1], node.getKey(1)),
                () -> assertEquals(values[2], node.getKey(2)));
    }

    @ParameterizedTest
    @DisplayName("can get a child at a position")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForGetChildren")
    void getChildTest(BTreeNode node, BTreeNode[] values) {
        assertAll("Getting keys", () -> assertEquals(values[0], node.getChild(0)),
                () -> assertEquals(values[1], node.getChild(1)), () -> assertEquals(values[2], node.getChild(2)),
                () -> assertEquals(values[3], node.getChild(3)),
                // accessing left index out of box, will return null
                () -> assertNull(node.getChild(-1)),
                // accessing right index out of box, will return null
                () -> assertNull(node.getChild(6)));
    }

    @ParameterizedTest
    @DisplayName("can get the index using a child")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForGetIndexByNode")
    void getChildIndexTest(BTreeNode parent, BTreeNode nodeForSearch, int expectedIndex) {
        assertEquals(expectedIndex, parent.getChildIndex(nodeForSearch));

        // if the node doesn't exist, the index will be the next free position for that node
        // in this case parent has 4 children, so index = 4
        assertEquals(4, parent.getChildIndex(new BTreeNode(BTreeNodeConstants.ORDER_4, parent)));
    }

    @ParameterizedTest
    @DisplayName("can get all the keys in a range")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForKeyRangeTest")
    void getKeysRangeTest(BTreeNode node, int start, int end, int[] keys) {
        int[] result = node.getKeyRange(start, end);

        for (int i = 0; i < result.length; i++) {
            assertEquals(result[i], keys[i]);
        }
    }

    @ParameterizedTest
    @DisplayName("can get children in a range")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForChildrenRangeTest")
    void getChildrenRangeTest(BTreeNode node, int start, int end, BTreeNode[] children) {
        BTreeNode[] result = node.getChildrenRange(start, end).orElse(null);
        if (children == null) {
            assertNull(result);
            return;
        }
        assertNotNull(result);
        for (int i = 0; i < result.length; i++) {
            assertEquals(result[i], children[i]);
        }
    }

    @ParameterizedTest
    @DisplayName("can merge array of keys in a node")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForMergeKeyTest")
    void mergeKeysTest(BTreeNode node, int[] keysForMerge, int[] expected) {
        node.mergeKeys(keysForMerge);
        for (int i = 0; i < node.getKeys().length; i++) {
            assertEquals(expected[i], node.getKey(i));
        }
    }

    @ParameterizedTest
    @DisplayName("can merge children in a node")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForMergeChildrenTest")
    void mergeChildrenTest(BTreeNode node, BTreeNode[] keysForMerge, BTreeNode[] expected) {
        node.mergeChildren(keysForMerge);
        for (int i = 0; i < node.getKeys().length; i++) {
            assertEquals(expected[i], node.getChild(i));
        }
        for (BTreeNode bTreeNode : keysForMerge) {
            assertEquals(node, bTreeNode.getParent());
        }
    }

    @ParameterizedTest
    @DisplayName("can get next index by key")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForGetNextIndexByKey")
    void getNextIndexByKeyTest(BTreeNode node, int value, int expectedPosition) throws KeyNotFoundException {
        assertEquals(expectedPosition, node.getNextIndexByKey(value));
    }

    @ParameterizedTest
    @DisplayName("can get index by key")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForGetIndexByKey")
    void getIndexByKeyTest(BTreeNode node, int value, int expectedPosition) throws KeyNotFoundException {
        assertEquals(expectedPosition, node.getIndexByKey(value));
    }

    @Test
    @DisplayName("can get index on safe mode")
    void safeKeysIndexTest() {
        BTreeNode node = BTreeNodeConstants.ROOT_NODE_1();
        assertEquals(0, node.safeKeysIndex(-1));
        assertEquals(0, node.safeKeysIndex(0));
        assertEquals(node.getOrder(), node.safeKeysIndex(node.getKeys().length - 1));
        assertEquals(node.getOrder() - 1, node.safeKeysIndex(node.getKeys().length - 2));
    }

    @Test
    @DisplayName("can get children on safe mode")
    void safeChildrenIndexTest() {
        BTreeNode node = BTreeNodeConstants.ROOT_NODE_1();
        assertEquals(0, node.safeChildrenIndex(-1));
        assertEquals(0, node.safeChildrenIndex(0));
        assertEquals(node.getOrder() + 1, node.safeChildrenIndex(node.getChildren().length - 1));
        assertEquals(node.getOrder(), node.safeChildrenIndex(node.getChildren().length - 2));
    }

    @ParameterizedTest(name = "Node: {0} : Key: {1} ")
    @DisplayName("search child test")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForSearchChild")
    void searchChildTest(BTreeNode node, int key, BTreeNode expected) throws NodeNotFoundException {
        assertEquals(expected, node.searchChild(key));
    }

    @Test
    @DisplayName("is leaf test")
    void isLeafTest() {
        BTreeNode root = BTreeNodeConstants.ROOT_NODE_1();
        root.addChild(0, new BTreeNode(BTreeNodeConstants.ORDER_4, root));
        assertFalse(root.isLeaf());

        BTreeNode leaf = BTreeNodeConstants.LEAF_NODE_1();
        assertTrue(leaf.isLeaf());
    }

    @Test
    @DisplayName("can remove lower key")
    void removeLowerKeyTest() {
        int firstKey = 1;
        BTreeNode node = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        node.addKey(firstKey);
        node.addKey(2);
        node.addKey(3);
        node.addKey(4);

        assertEquals(1, node.removeFirstKey());
        assertEquals(2, node.getKey(0));
        assertEquals(3, node.getKeysSize());
    }

    @Test
    @DisplayName("can remove lower key")
    void removeGreaterKeyTest() {
        int order = BTreeNodeConstants.ORDER_4;
        BTreeNode node = new BTreeNode(order, null);
        node.addKey(1);
        node.addKey(2);
        node.addKey(3);
        node.addKey(4);
        node.addKey(5);

        assertEquals(5, node.removeLastKey());
        assertEquals(0, node.getKey(order));
        assertEquals(4, node.getKeysSize());
    }

    @ParameterizedTest
    @DisplayName("can remove key by key")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForRemoveByKey")
    void removeKeyByKeyTest(BTreeNode node, int key, int[] expectedKeys) throws KeyNotFoundException {
        int initialKeysize = node.getKeysSize();
        node.removeByKey(key);
        for (int i = 0; i < node.getKeys().length; i++) {
            assertEquals(expectedKeys[i], node.getKey(i));
        }
        assertEquals(initialKeysize - 1, node.getKeysSize());
    }

    @ParameterizedTest
    @DisplayName("can remove key by index")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForRemoveByIndex")
    void removeKeyByIndexTest(BTreeNode node, int index, int[] expectedKeys) {
        int initialKeysize = node.getKeysSize();
        // do not reorganize
        node.removeByIndex(index);
        for (int i = 0; i < node.getKeys().length; i++) {
            assertEquals(expectedKeys[i], node.getKey(i));
        }
        assertEquals(initialKeysize - 1, node.getKeysSize());
    }

    @ParameterizedTest
    @DisplayName("can remove child by index")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForRemoveChildByIndex")
    void removeChildByIndexTest(BTreeNode node, int index, BTreeNode[] expectedKeys) {
        int initialChildrensize = node.getChildrenSize();
        // do not reorganize
        node.removeChildByIndex(index);
        for (int i = 0; i < node.getKeys().length; i++) {
            assertEquals(expectedKeys[i], node.getChild(i));
        }
        assertEquals(initialChildrensize - 1, node.getChildrenSize());
    }

    @ParameterizedTest
    @DisplayName("can remove by index")
    @MethodSource(value = "com.cristianpeter.btree.providers.BTreeNodeProvider#provideNodeForRemoveChildByChild")
    void removeChildByChildTest(BTreeNode node, BTreeNode child, BTreeNode[] expectedKeys) {
        int initialChildrensize = node.getChildrenSize();
        // do not reorganize
        node.removeChildByNode(child);
        for (int i = 0; i < node.getKeys().length; i++) {
            assertEquals(expectedKeys[i], node.getChild(i));
        }
        assertEquals(initialChildrensize - 1, node.getChildrenSize());
    }

}
