package com.cristianpeter.btree;

import com.cristianpeter.btree.exceptions.NodeNotFoundException;

public class Main {
    public static void main(String[] args) {
        BTree tree = new BTree(3);
        try {
            tree.add(1);
        } catch (NodeNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
