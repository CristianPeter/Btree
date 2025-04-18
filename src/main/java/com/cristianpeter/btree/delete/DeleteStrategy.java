package com.cristianpeter.btree.delete;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.cristianpeter.btree.core.BTreeNode;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;
import com.cristianpeter.btree.exceptions.interfaces.ExceptionalBiPredicate;

public abstract class DeleteStrategy {

    public static boolean delete(BTreeNode root, int key) throws NodeNotFoundException, KeyNotFoundException {
        BTreeNode node = root.searchChild(key);
        if (node.isLeaf()) {
            return new LeafDeleteStrategy().deleteImpl(node, key);
        } else {
            return new InternalNodeDeleteStrategy().deleteImpl(node, key);
        }
    }

    public abstract Optional<Pair<BTreeNode, Function<BTreeNode, Integer>>> lendKeyProvider(BTreeNode node, int key);

    public abstract boolean borrowSiblingImpl(BTreeNode node, int key) throws KeyNotFoundException;

    public abstract boolean mergeSiblingsImpl(BTreeNode node, int key) throws KeyNotFoundException;

    /**
     * Borrow parent key to child, removing from parent and adding to child
     * And move it to the child node
     * if removeReference is true, the child reference of the parent will be deleted
     *
     * @param node
     *            child node
     */
    protected void borrowParentKeyToChild(BTreeNode node) throws KeyNotFoundException {
        // remove key from parent and add it to the child
        int parentKeyIndex = node.safeKeysIndex(node.getParent().getChildIndex(node) - 1);
        int parentKey = node.getParent().getKey(parentKeyIndex);
        node.getParent().removeByKey(parentKey);
        node.addKey(parentKey);
    }

    public boolean balanceTree(BTreeNode node, int key) throws KeyNotFoundException {
        List<ExceptionalBiPredicate<BTreeNode, Integer>> methods = List.of(this::borrowSiblingImpl, this::mergeSiblingsImpl);
        boolean result = executeInOrder(methods, node, key);
        if (node.getParent().keysUnderflowing()) {
            return this.balanceTree(node.getParent(), key);
        }
        return result;
    }

    private boolean executeInOrder(List<ExceptionalBiPredicate<BTreeNode, Integer>> methods, BTreeNode node, int key)
            throws KeyNotFoundException {
        for (ExceptionalBiPredicate<BTreeNode, Integer> method : methods) {
            if (Boolean.TRUE.equals(method.test(node, key))) {
                return true;
            }
        }
        return false;
    }

}
