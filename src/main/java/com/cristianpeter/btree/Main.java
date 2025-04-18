package com.cristianpeter.btree;

import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;

public class Main {
    public static void main(String[] args) {
        BTree tree = new BTree(3);
        try {
            testCase5(tree);
        } catch (NodeNotFoundException | KeyNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    // left sibling lend key
    private static void testCase1(BTree tree) throws NodeNotFoundException, KeyNotFoundException {
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
    }

    // right sibling lend key
    private static void testCase2(BTree tree) throws NodeNotFoundException, KeyNotFoundException {
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
    }

    // sibling merge
    private static void testCase3(BTree tree) throws NodeNotFoundException, KeyNotFoundException {
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
    }

    // internal node - left subtree borrow key
    private static void testCase4(BTree tree) throws NodeNotFoundException, KeyNotFoundException {
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
    }

    // internal node - right subtree borrow key
    private static void testCase5(BTree tree) throws NodeNotFoundException, KeyNotFoundException {
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
    }

    // internal node - merge right-left childs
    private static void testCase6(BTree tree) throws NodeNotFoundException, KeyNotFoundException {
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
    }

}
