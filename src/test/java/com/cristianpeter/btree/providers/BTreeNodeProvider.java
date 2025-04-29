package com.cristianpeter.btree.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.cristianpeter.btree.constants.BTreeNodeConstants;
import com.cristianpeter.btree.core.BTreeNode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BTreeNodeProvider {

    public static Stream<Arguments> provideChildrenWithExpectations() {
        BTreeNode existingChild = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode leftChild = BTreeNodeConstants.ROOT_NODE_1();

        return Stream.of(Arguments.of(existingChild, leftChild, leftChild, existingChild),
                Arguments.of(existingChild, null, existingChild, null), Arguments.of(null, leftChild, leftChild, null));
    }

    public static Stream<Arguments> provideNodeAndOverflowingExpectation() {

        BTreeNode leftChild = BTreeNodeConstants.ROOT_NODE_1();

        // node is overflowing
        leftChild.addKey(BTreeNodeConstants.VALUE);
        leftChild.addKey(BTreeNodeConstants.VALUE + 1);
        leftChild.addKey(BTreeNodeConstants.VALUE + 2);
        leftChild.addKey(BTreeNodeConstants.VALUE + 3);
        leftChild.addKey(BTreeNodeConstants.VALUE + 4);

        return Stream.of(Arguments.of(leftChild, true), Arguments.of(BTreeNodeConstants.ROOT_NODE_1(), false));
    }

    public static Stream<Arguments> provideNodeAndUnderflowingExpectation() {

        BTreeNode normal = BTreeNodeConstants.LEAF_NODE_1();
        normal.addKey(BTreeNodeConstants.VALUE);
        normal.addKey(BTreeNodeConstants.VALUE + 1);
        normal.addKey(BTreeNodeConstants.VALUE + 2);

        BTreeNode upperMinimum = BTreeNodeConstants.LEAF_NODE_1();
        upperMinimum.addKey(BTreeNodeConstants.VALUE);
        upperMinimum.addKey(BTreeNodeConstants.VALUE + 1);

        BTreeNode underMinimun = BTreeNodeConstants.LEAF_NODE_1();
        underMinimun.addKey(BTreeNodeConstants.VALUE);

        return Stream.of(Arguments.of(normal, false), Arguments.of(upperMinimum, false), Arguments.of(underMinimun, true));
    }

    public static Stream<Arguments> provideNodeLendKeyTest() {

        // can lend key
        BTreeNode canLend = BTreeNodeConstants.LEAF_NODE_1();
        canLend.addKey(BTreeNodeConstants.VALUE);
        canLend.addKey(BTreeNodeConstants.VALUE + 1);
        canLend.addKey(BTreeNodeConstants.VALUE + 2);

        BTreeNode canNotLend = BTreeNodeConstants.LEAF_NODE_1();
        canNotLend.addKey(BTreeNodeConstants.VALUE);
        canNotLend.addKey(BTreeNodeConstants.VALUE + 1);

        return Stream.of(Arguments.of(canLend, true), Arguments.of(canNotLend, false));
    }

    public static Stream<Arguments> provideNodeForGetKeys() {
        BTreeNode nodeWithKeys = BTreeNodeConstants.LEAF_NODE_1();
        nodeWithKeys.addKey(BTreeNodeConstants.VALUE);
        nodeWithKeys.addKey(BTreeNodeConstants.VALUE + 1);
        nodeWithKeys.addKey(BTreeNodeConstants.VALUE + 2);

        return Stream.of(Arguments.of(nodeWithKeys,
                new int[] { BTreeNodeConstants.VALUE, BTreeNodeConstants.VALUE + 1, BTreeNodeConstants.VALUE + 2 }));
    }

    public static Stream<Arguments> provideNodeForGetChildren() {
        BTreeNode nodeWithChildren = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren);
        nodeWithChildren.addChild(0, node1);
        BTreeNode node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren);
        nodeWithChildren.addChild(1, node2);
        BTreeNode node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren);
        nodeWithChildren.addChild(2, node3);
        BTreeNode node4 = new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren);
        nodeWithChildren.addChild(3, node4);

        return Stream.of(Arguments.of(nodeWithChildren, new BTreeNode[] { node1, node2, node3, node4 }));
    }

    public static Stream<Arguments> provideNodeForGetIndexByNode() {
        BTreeNode nodeWithChildren = BTreeNodeConstants.ROOT_NODE_1();
        nodeWithChildren.addChild(0, new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren));
        nodeWithChildren.addChild(1, new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren));
        BTreeNode node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren);
        nodeWithChildren.addChild(2, node1);
        nodeWithChildren.addChild(3, new BTreeNode(BTreeNodeConstants.ORDER_4, nodeWithChildren));

        return Stream.of(Arguments.of(nodeWithChildren, node1, 2));
    }

    public static Stream<Arguments> provideNodeForKeyRangeTest() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        node1.addKey(1);
        node1.addKey(2);
        node1.addKey(3);
        node1.addKey(4);

        BTreeNode node2 = BTreeNodeConstants.ROOT_NODE_1();
        node2.addKey(1);
        node2.addKey(2);

        return Stream.of(Arguments.of(node1, 0, 4, new int[] { 1, 2, 3, 4 }), Arguments.of(node2, 0, 2, new int[] { 1, 2 }),
                Arguments.of(node2, 2, 4, new int[] { 0, 0 }));
    }

    public static Stream<Arguments> provideNodeForChildrenRangeTest() {
        BTreeNode node = BTreeNodeConstants.LEAF_NODE_1();
        BTreeNode child1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node);
        node.addChild(0, child1);
        BTreeNode child2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node);
        node.addChild(1, child2);
        BTreeNode child3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node);
        node.addChild(2, child3);
        BTreeNode child4 = new BTreeNode(BTreeNodeConstants.ORDER_4, node);
        node.addChild(3, child4);
        BTreeNode child5 = new BTreeNode(BTreeNodeConstants.ORDER_4, node);
        node.addChild(4, child5);

        BTreeNode secondNode = BTreeNodeConstants.LEAF_NODE_1();
        secondNode.addChild(0, child1);
        secondNode.addChild(1, child3);

        return Stream.of(Arguments.of(node, 0, 5, new BTreeNode[] { child1, child2, child3, child4, child5 }),
                Arguments.of(secondNode, 0, 2, new BTreeNode[] { child1, child3 }),
                Arguments.of(secondNode, 2, 5, new BTreeNode[] { null, null, null }),
                // if start > end, will return Optional.empty
                Arguments.of(secondNode, 5, 2, null)

        );
    }

    public static Stream<Arguments> provideNodeForMergeKeyTest() {
        BTreeNode emptyNode = BTreeNodeConstants.ROOT_NODE_1();

        BTreeNode nodeWithKeys = BTreeNodeConstants.ROOT_NODE_1();
        nodeWithKeys.addKey(1);
        nodeWithKeys.addKey(2);
        return Stream.of(Arguments.of(emptyNode, new int[] { 10, 11, 12, 13, 14 }, new int[] { 10, 11, 12, 13, 14 }),
                Arguments.of(nodeWithKeys, new int[] { 10, 11 }, new int[] { 1, 2, 10, 11, 0 }));
    }

    public static List<Arguments> provideNodeForMergeChildrenTest() {
        List<Arguments> arguments = new ArrayList<>();
        BTreeNode nodeWithoutChildren = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1 = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode child2 = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode child3 = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode child4 = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode child5 = new BTreeNode(BTreeNodeConstants.ORDER_4, null);

        arguments.add(Arguments.of(nodeWithoutChildren, new BTreeNode[] { child1, child2, child3, child4, child5 },
                new BTreeNode[] { child1, child2, child3, child4, child5, null }));

        BTreeNode nodeWithKeys = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode childOne = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode childTwo = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode childThree = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        BTreeNode childFour = new BTreeNode(BTreeNodeConstants.ORDER_4, null);
        nodeWithKeys.addChild(0, childOne);
        nodeWithKeys.addChild(1, childTwo);
        arguments.add(Arguments.of(nodeWithKeys, new BTreeNode[] { childThree, childFour },
                new BTreeNode[] { childOne, childTwo, childThree, childFour, null, null }));

        return arguments;
    }

    public static Stream<Arguments> provideNodeForGetNextIndexByKey() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        node1.addKey(10);
        node1.addKey(11);
        node1.addKey(12);
        node1.addKey(13);
        node1.addKey(14);

        return Stream.of(Arguments.of(node1, 13, 4), Arguments.of(node1, 10, 1), Arguments.of(node1, 14, 5));
    }

    public static Stream<Arguments> provideNodeForGetIndexByKey() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        node1.addKey(10);
        node1.addKey(11);
        node1.addKey(12);
        node1.addKey(13);
        node1.addKey(14);

        return Stream.of(Arguments.of(node1, 13, 3), Arguments.of(node1, 10, 0), Arguments.of(node1, 14, 4));
    }

    public static Stream<Arguments> provideNodeForSearchChild() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        node1.addKey(10);
        node1.addKey(12);
        node1.addKey(14);
        node1.addKey(16);

        BTreeNode child0 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child4 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);

        node1.addChild(0, child0);
        // key = 10
        node1.addChild(1, child1);
        // key = 12
        node1.addChild(2, child2);
        // key = 14
        node1.addChild(3, child3);
        // key = 16
        node1.addChild(4, child4);

        return Stream.of(
                // if the node has the key, will be return
                Arguments.of(node1, 14, node1), Arguments.of(node1, 15, child3), Arguments.of(node1, 9, child0),
                Arguments.of(node1, 11, child1),
                // if the node has the key, will be return
                Arguments.of(node1, 16, node1), Arguments.of(node1, 17, child4));
    }

    public static Stream<Arguments> provideNodeForRemoveByKey() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        node1.addKey(10);
        node1.addKey(11);
        node1.addKey(12);
        node1.addKey(13);
        node1.addKey(14);

        BTreeNode node2 = BTreeNodeConstants.ROOT_NODE_1();
        node2.addKey(10);
        node2.addKey(11);
        node2.addKey(12);
        node2.addKey(13);
        node2.addKey(14);

        BTreeNode node3 = BTreeNodeConstants.ROOT_NODE_1();
        node3.addKey(10);
        node3.addKey(11);
        node3.addKey(12);
        node3.addKey(13);
        node3.addKey(14);

        return Stream.of(Arguments.of(node1, 14, new int[] { 10, 11, 12, 13, 0 }),
                Arguments.of(node2, 10, new int[] { 11, 12, 13, 14, 0 }),
                Arguments.of(node3, 12, new int[] { 10, 11, 13, 14, 0 }));
    }

    public static Stream<Arguments> provideNodeForRemoveByIndex() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        node1.addKey(10);
        node1.addKey(11);
        node1.addKey(12);
        node1.addKey(13);
        node1.addKey(14);

        BTreeNode node2 = BTreeNodeConstants.ROOT_NODE_1();
        node2.addKey(10);
        node2.addKey(11);
        node2.addKey(12);
        node2.addKey(13);
        node2.addKey(14);

        BTreeNode node3 = BTreeNodeConstants.ROOT_NODE_1();
        node3.addKey(10);
        node3.addKey(11);
        node3.addKey(12);
        node3.addKey(13);
        node3.addKey(14);

        return Stream.of(Arguments.of(node1, 4, new int[] { 10, 11, 12, 13, 0 }),
                Arguments.of(node2, 0, new int[] { 0, 11, 12, 13, 14 }), Arguments.of(node3, 2, new int[] { 10, 11, 0, 13, 14 }));
    }

    public static Stream<Arguments> provideNodeForRemoveChildByIndex() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child2Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child3Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child4Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child5Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        node1.addChild(0, child1Node1);
        node1.addChild(1, child2Node1);
        node1.addChild(2, child3Node1);
        node1.addChild(3, child4Node1);
        node1.addChild(4, child5Node1);

        BTreeNode node2 = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child2Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child3Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child4Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child5Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        node2.addChild(0, child1Node2);
        node2.addChild(1, child2Node2);
        node2.addChild(2, child3Node2);
        node2.addChild(3, child4Node2);
        node2.addChild(4, child5Node2);

        BTreeNode node3 = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child2Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child3Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child4Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child5Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        node3.addChild(0, child1Node3);
        node3.addChild(1, child2Node3);
        node3.addChild(2, child3Node3);
        node3.addChild(3, child4Node3);
        node3.addChild(4, child5Node3);

        return Stream.of(
                Arguments.of(node1, 4, new BTreeNode[] { child1Node1, child2Node1, child3Node1, child4Node1, null, null }),
                Arguments.of(node2, 0, new BTreeNode[] { null, child2Node2, child3Node2, child4Node2, child5Node2, null }),
                Arguments.of(node3, 2, new BTreeNode[] { child1Node3, child2Node3, null, child4Node3, child5Node3, null }));
    }

    public static Stream<Arguments> provideNodeForRemoveChildByChild() {
        BTreeNode node1 = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child2Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child3Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child4Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        BTreeNode child5Node1 = new BTreeNode(BTreeNodeConstants.ORDER_4, node1);
        node1.addChild(0, child1Node1);
        node1.addChild(1, child2Node1);
        node1.addChild(2, child3Node1);
        node1.addChild(3, child4Node1);
        node1.addChild(4, child5Node1);

        BTreeNode node2 = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child2Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child3Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child4Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        BTreeNode child5Node2 = new BTreeNode(BTreeNodeConstants.ORDER_4, node2);
        node2.addChild(0, child1Node2);
        node2.addChild(1, child2Node2);
        node2.addChild(2, child3Node2);
        node2.addChild(3, child4Node2);
        node2.addChild(4, child5Node2);

        BTreeNode node3 = BTreeNodeConstants.ROOT_NODE_1();
        BTreeNode child1Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child2Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child3Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child4Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        BTreeNode child5Node3 = new BTreeNode(BTreeNodeConstants.ORDER_4, node3);
        node3.addChild(0, child1Node3);
        node3.addChild(1, child2Node3);
        node3.addChild(2, child3Node3);
        node3.addChild(3, child4Node3);
        node3.addChild(4, child5Node3);

        return Stream.of(
                Arguments.of(node1, child5Node1,
                        new BTreeNode[] { child1Node1, child2Node1, child3Node1, child4Node1, null, null }),
                Arguments.of(node2, child1Node2,
                        new BTreeNode[] { null, child2Node2, child3Node2, child4Node2, child5Node2, null }),
                Arguments.of(node3, child3Node3,
                        new BTreeNode[] { child1Node3, child2Node3, null, child4Node3, child5Node3, null }));
    }

}
