package com.cristianpeter.btree.contracts;

import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;

public interface IBTree {

    boolean add(int key) throws NodeNotFoundException;

    boolean delete(int key) throws NodeNotFoundException, KeyNotFoundException;

    boolean contains(int key);
}
