package com.cristianpeter.btree.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cristianpeter.btree.BTree;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;

class BTreeTest {

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
