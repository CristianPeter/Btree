package com.cristianpeter.btree.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        private static final BTreeNode leftChild = new BTreeNode(BTreeNodeConstants.ORDER, null);
        private static final BTreeNode rightChild = new BTreeNode(BTreeNodeConstants.ORDER, null);
    }

    public static class ArgumentsProvider {

        static Stream<Arguments> provideChildrenWithExpectations() {
            BTreeNode existingChild = BTreeNodeConstants.rightChild;
            BTreeNode leftChild = BTreeNodeConstants.leftChild;

            return Stream.of(
                    Arguments.of(existingChild, leftChild, leftChild, existingChild),
                    Arguments.of(existingChild, null, existingChild, null),
                    Arguments.of(null, leftChild, leftChild, null)
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

}
