package com.cristianpeter.btree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cristianpeter.btree.constants.BTreeNodeConstants;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;

class BTreeTest {

    private BTree tree;

    @BeforeEach
    void beforeEach() {
        tree = new BTree(BTreeNodeConstants.ORDER_3);
    }

    @Test
    void leftSiblingLendKeyTest() throws NodeNotFoundException, KeyNotFoundException {
        tree.add(679);
        tree.add(960);
        tree.add(518);
        tree.add(360);
        tree.add(46);
        tree.add(849);
        tree.add(243);
        tree.add(39);
        tree.add(321);
        tree.add(357);
        tree.add(201);
        tree.add(236);
        tree.add(717);
        tree.delete(236);
        assertEquals("360 -> 46 -> 243 -> 39 -> 201 -> 321 -> 357 -> 679 -> 518 -> 717 -> 849 -> 960", tree.preOrder());
        assertEquals("39 -> 46 -> 201 -> 243 -> 321 -> 357 -> 360 -> 518 -> 679 -> 717 -> 849 -> 960", tree.inOrder());
        assertEquals("39 -> 201 -> 321 -> 357 -> 46 -> 243 -> 518 -> 717 -> 849 -> 960 -> 679 -> 360", tree.postOrder());
    }

    @Test
    void rightSiblingLendKeyTest() throws NodeNotFoundException, KeyNotFoundException {
        tree.add(679);
        tree.add(960);
        tree.add(518);
        tree.add(360);
        tree.add(46);
        tree.add(849);
        tree.add(243);
        tree.add(39);
        tree.add(321);
        tree.add(357);
        tree.add(201);
        tree.add(236);
        tree.add(717);

        tree.delete(518);
        assertEquals("360 -> 201 -> 243 -> 39 -> 46 -> 236 -> 321 -> 357 -> 717 -> 679 -> 849 -> 960", tree.preOrder());
        assertEquals("39 -> 46 -> 201 -> 236 -> 243 -> 321 -> 357 -> 360 -> 679 -> 717 -> 849 -> 960", tree.inOrder());
        assertEquals("39 -> 46 -> 236 -> 321 -> 357 -> 201 -> 243 -> 679 -> 849 -> 960 -> 717 -> 360", tree.postOrder());
    }

    @Test
    void siblingMergeTestCase() throws NodeNotFoundException, KeyNotFoundException {
        tree.add(679);
        tree.add(518);
        tree.add(360);
        tree.add(46);
        tree.add(243);
        tree.add(39);
        tree.add(321);
        tree.add(357);
        tree.add(201);
        tree.add(236);
        tree.add(400);

        tree.delete(400);

        // todo: fix -> at the end
        assertEquals("360 -> 201 -> 243 -> 39 -> 46 -> 236 -> 321 -> 357 -> 679 -> 518 -> ", tree.preOrder());
        assertEquals("39 -> 46 -> 201 -> 236 -> 243 -> 321 -> 357 -> 360 -> 518 -> 679 -> ", tree.inOrder());
        assertEquals("39 -> 46 -> 236 -> 321 -> 357 -> 201 -> 243 -> 518 ->  -> 679 -> 360", tree.postOrder());
    }

    @Test
    void internalNodeLeftSubtreeBorrowKeyTest() throws NodeNotFoundException, KeyNotFoundException {
        tree.add(20);
        tree.add(40);
        tree.add(10);
        tree.add(30);
        tree.add(33);
        tree.add(50);
        tree.add(60);
        tree.add(5);
        tree.add(15);
        tree.add(25);
        tree.add(28);
        tree.add(31);
        tree.add(32);

        tree.delete(30);
        assertEquals("33 -> 15 -> 28 -> 5 -> 10 -> 20 -> 25 -> 31 -> 32 -> 50 -> 40 -> 60", tree.preOrder());
        assertEquals("5 -> 10 -> 15 -> 20 -> 25 -> 28 -> 31 -> 32 -> 33 -> 40 -> 50 -> 60", tree.inOrder());
        assertEquals("5 -> 10 -> 20 -> 25 -> 31 -> 32 -> 15 -> 28 -> 40 -> 60 -> 50 -> 33", tree.postOrder());
    }

    @Test
    void internalNodeRightSubtreeBorrowKeyTest() throws NodeNotFoundException, KeyNotFoundException {
        tree.add(20);
        tree.add(40);
        tree.add(10);
        tree.add(30);
        tree.add(33);
        tree.add(50);
        tree.add(60);
        tree.add(5);
        tree.add(15);
        tree.add(25);
        tree.add(28);
        tree.add(31);
        tree.add(32);
        tree.add(70);

        tree.delete(50);

        assertEquals("33 -> 15 -> 30 -> 5 -> 10 -> 20 -> 25 -> 28 -> 31 -> 32 -> 60 -> 40 -> 70", tree.preOrder());
        assertEquals("5 -> 10 -> 15 -> 20 -> 25 -> 28 -> 30 -> 31 -> 32 -> 33 -> 40 -> 60 -> 70", tree.inOrder());
        assertEquals("5 -> 10 -> 20 -> 25 -> 28 -> 31 -> 32 -> 15 -> 30 -> 40 -> 70 -> 60 -> 33", tree.postOrder());
    }

    @Test
    void internalNodeMergeRightLeftSibling() throws NodeNotFoundException, KeyNotFoundException {
        tree.add(20);
        tree.add(40);
        tree.add(10);
        tree.add(30);
        tree.add(33);
        tree.add(50);
        tree.add(60);
        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        tree.delete(10);

        assertEquals("30 -> 7 -> 5 -> 6 -> 8 -> 20 -> 50 -> 33 -> 40 -> 60", tree.preOrder());
        assertEquals("5 -> 6 -> 7 -> 8 -> 20 -> 30 -> 33 -> 40 -> 50 -> 60", tree.inOrder());
        assertEquals("5 -> 6 -> 8 -> 20 -> 7 -> 33 -> 40 -> 60 -> 50 -> 30", tree.postOrder());
    }

    @Test
    @DisplayName(value = "Test btree inorder sequence")
    void testInorderOutput() throws NodeNotFoundException {
        int order = 4;
        int[] keys = new int[] { 34, 44, 54, 24, 14 };
        int[] expected = new int[] { 14, 24, 34, 44, 54 };
        BTree tree = new BTree(order);
        tree.add(keys[0]);
        tree.add(keys[1]);
        tree.add(keys[2]);
        tree.add(keys[3]);
        tree.add(keys[4]);
        assertEquals(tree.inOrder(), Arrays.stream(expected).boxed().map(String::valueOf).collect(Collectors.joining(" -> ")));

    }

    @Test
    @DisplayName(value = "Test btree preOrder sequence")
    void testPreOrderOutput() throws NodeNotFoundException {
        int[] keys = new int[] { 10, 20, 5, 15, 25, 30, 35, 26, 28, 14, 18, 16 };
        int[] expected = new int[] { 26, 15, 20, 5, 10, 14, 16, 18, 25, 30, 28, 35 };

        int order = 3;
        BTree tree = new BTree(order);
        tree.add(keys[0]);
        tree.add(keys[1]);
        tree.add(keys[2]);
        tree.add(keys[3]);
        tree.add(keys[4]);
        tree.add(keys[5]);
        tree.add(keys[6]);
        tree.add(keys[7]);
        tree.add(keys[8]);
        tree.add(keys[9]);
        tree.add(keys[10]);
        tree.add(keys[11]);
        assertEquals(tree.preOrder(), Arrays.stream(expected).boxed().map(String::valueOf).collect(Collectors.joining(" -> ")));
    }

    @Test
    @DisplayName(value = "Test btree postOrder sequence")
    void testPostOrderOutput() throws NodeNotFoundException {
        int[] keys = new int[] { 10, 20, 5, 15, 25, 30, 35, 26, 28, 14, 18, 16 };
        int[] expected = new int[] { 5, 10, 14, 16, 18, 25, 15, 20, 28, 35, 30, 26 };
        int order = 3;
        BTree tree = new BTree(order);
        tree.add(keys[0]);
        tree.add(keys[1]);
        tree.add(keys[2]);
        tree.add(keys[3]);
        tree.add(keys[4]);
        tree.add(keys[5]);
        tree.add(keys[6]);
        tree.add(keys[7]);
        tree.add(keys[8]);
        tree.add(keys[9]);
        tree.add(keys[10]);
        tree.add(keys[11]);
        assertEquals(tree.postOrder(), Arrays.stream(expected).boxed().map(String::valueOf).collect(Collectors.joining(" -> ")));
    }

}
