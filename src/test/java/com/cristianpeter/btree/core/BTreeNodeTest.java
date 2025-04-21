package com.cristianpeter.btree.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Named.named;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BTreeNodeTest {

    public static class BTreeNodeConstants {
        private static final int VALUE = 10;
        private static final int ORDER = 4;
        private static final BTreeNode ROOT_NODE_1 = new BTreeNode(BTreeNodeConstants.ORDER, null);
        private static final BTreeNode ROOT_NODE_2 = new BTreeNode(BTreeNodeConstants.ORDER, null);
        private static final BTreeNode ROOT_NODE_3 = new BTreeNode(BTreeNodeConstants.ORDER, null);

        private static final BTreeNode LEAF_NODE_1 = new BTreeNode(BTreeNodeConstants.ORDER, ROOT_NODE_1);
        private static final BTreeNode LEAF_NODE_2 = new BTreeNode(BTreeNodeConstants.ORDER, ROOT_NODE_2);
        private static final BTreeNode LEAF_NODE_3 = new BTreeNode(BTreeNodeConstants.ORDER, ROOT_NODE_3);

    }

    public static class ArgumentsProvider {

        public static Stream<Arguments> provideChildrenWithExpectations() {
            BTreeNode existingChild = BTreeNodeConstants.ROOT_NODE_2;
            BTreeNode leftChild = BTreeNodeConstants.ROOT_NODE_1;

            return Stream.of(
                    Arguments.of(existingChild, leftChild, leftChild, existingChild),
                    Arguments.of(existingChild, null, existingChild, null),
                    Arguments.of(null, leftChild, leftChild, null)
            );
        }

        public static Stream<Arguments> provideNodeAndOverflowingExpectation(){

            BTreeNode leftChild = BTreeNodeConstants.ROOT_NODE_1;

            // node is overflowing
            leftChild.addKey(BTreeNodeConstants.VALUE);
            leftChild.addKey(BTreeNodeConstants.VALUE + 1);
            leftChild.addKey(BTreeNodeConstants.VALUE + 2);
            leftChild.addKey(BTreeNodeConstants.VALUE + 3);
            leftChild.addKey(BTreeNodeConstants.VALUE + 4);

            return Stream.of(
                    Arguments.of(leftChild, true),
                    Arguments.of(BTreeNodeConstants.ROOT_NODE_2, false)
            );
        }

        public static Stream<Arguments> provideNodeAndUnderflowingExpectation(){

            BTreeNode normal = BTreeNodeConstants.LEAF_NODE_1;
            normal.addKey(BTreeNodeConstants.VALUE);
            normal.addKey(BTreeNodeConstants.VALUE + 1);
            normal.addKey(BTreeNodeConstants.VALUE + 2);

            BTreeNode upperMinimum = BTreeNodeConstants.LEAF_NODE_2;
            upperMinimum.addKey(BTreeNodeConstants.VALUE);
            upperMinimum.addKey(BTreeNodeConstants.VALUE + 1);

            BTreeNode underMinimun = BTreeNodeConstants.LEAF_NODE_3;
            underMinimun.addKey(BTreeNodeConstants.VALUE);

            return Stream.of(
                    Arguments.of(normal, false),
                    Arguments.of(upperMinimum, false),
                    Arguments.of(underMinimun, true)
            );
        }

    }

    @Test
    @DisplayName("Constructor initializes state correctly")
    void testInitialStateOnConstructor() {
        BTreeNode node = new BTreeNode(BTreeNodeConstants.ORDER, null);

        assertAll("Initial State", () -> assertEquals(BTreeNodeConstants.ORDER, node.getOrder()),
                () -> assertEquals(BTreeNodeConstants.ORDER + 1, node.getKeys().length),
                () -> assertEquals(BTreeNodeConstants.ORDER + 2, node.getChildren().length), () -> assertNull(node.getParent()),
                () -> assertFalse(node.keysUnderflowing()), () -> assertFalse(node.keysOverflowing()),
                () -> assertFalse(node.canLendKey()));
    }

    @Test
    @DisplayName("Add a single key to BTreeNode")
    void addKeyTest() {
        BTreeNode node = new BTreeNode(BTreeNodeConstants.ORDER, null);
        node.addKey(BTreeNodeConstants.VALUE);

        assertAll("Add Key", () -> assertEquals(BTreeNodeConstants.VALUE, node.getKey(0)),
                () -> assertEquals(1, node.getKeysSize()));
    }

    // Nested classes need to be separated with $, not .: example.JUnitNestedMethodSourceTests$Source#getArgs.
    @ParameterizedTest(name = "Add child: {0}")
    @MethodSource(value = "com.cristianpeter.btree.core.BTreeNodeTest$ArgumentsProvider#provideChildrenWithExpectations")
    @DisplayName("Add a child node and validate position")
    void addChildTest(BTreeNode existingChild, BTreeNode childToAdd, BTreeNode expectedAt0, BTreeNode expectedAt1) {
        BTreeNode parent = new BTreeNode(BTreeNodeConstants.ORDER, null);
        parent.addChild(0, existingChild);
        parent.addChild(0, childToAdd);

        assertAll("Child Positions After Insert", () -> assertEquals(expectedAt0, parent.getChild(0)),
                () -> assertEquals(expectedAt1, parent.getChild(1)));
    }


    @ParameterizedTest
    @DisplayName("Add a single key to BTreeNode")
    @MethodSource(value = "com.cristianpeter.btree.core.BTreeNodeTest$ArgumentsProvider#provideNodeAndOverflowingExpectation")
    void keysOverflowingTest(BTreeNode node, boolean expected) {
        assertEquals(expected, node.keysOverflowing());
    }

    @ParameterizedTest
    @DisplayName("Add a single key to BTreeNode")
    @MethodSource(value = "com.cristianpeter.btree.core.BTreeNodeTest$ArgumentsProvider#provideNodeAndUnderflowingExpectation")
    void keysUnderflowTest(BTreeNode node, boolean expected) {
        assertEquals(expected, node.keysUnderflowing());
    }
}
