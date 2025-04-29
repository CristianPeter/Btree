package com.cristianpeter.btree.delete;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.cristianpeter.btree.core.BTreeNode;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;

public class InternalNodeDeleteStrategy extends DeleteStrategy {

    public boolean deleteImpl(BTreeNode node, int key) throws KeyNotFoundException {
        return this.balanceTree(node, key);
    }

    @Override
    public boolean borrowSiblingImpl(BTreeNode node, int key) throws KeyNotFoundException {
        return this.borrowSibling(node, key);
    }

    @Override
    public boolean mergeSiblingsImpl(BTreeNode node, int key) throws KeyNotFoundException {
        return this.mergeSiblings(node, key);
    }

    /**
     * Will return the node with maximum key in the left subtree or node with minimum key in right subtree
     * key - 1 will return the node with maximum value in left subtree (recursive)
     * key + 1 will return the node with minimum key in right subtree (recursive)
     * 
     * @param node
     * @param key
     * @return
     */
    @Override
    public Optional<Pair<BTreeNode, Function<BTreeNode, Integer>>> lendKeyProvider(BTreeNode node, int key) {

        BTreeNode leftSibling = node.greaterLSB(key - 1).orElse(null);
        BTreeNode rightSibling = node.lowerRSB(key + 1).orElse(null);

        if (leftSibling != null && leftSibling.canLendKey()) {
            return Optional.of(ImmutablePair.of(leftSibling, BTreeNode::removeLastKey));
        } else if (rightSibling != null && rightSibling.canLendKey()) {
            return Optional.of(ImmutablePair.of(rightSibling, BTreeNode::removeFirstKey));
        } else {
            return Optional.empty();
        }
    }

    private boolean borrowSibling(BTreeNode node, int key) throws KeyNotFoundException {
        Pair<BTreeNode, Function<BTreeNode, Integer>> pair = this.lendKeyProvider(node, key).orElse(null);
        if (pair != null && pair.getLeft() != null) {
            // first remove the key
            node.removeByKey(key);

            // remove lend key from sibling
            int lendKey = pair.getRight().apply(pair.getLeft());

            // add key to current node
            node.addKey(lendKey);

            return true;
        }
        return false;
    }

    private boolean mergeSiblings(BTreeNode node, int key) throws KeyNotFoundException {
        int rightChild = node.getNextIndexByKey(key);
        BTreeNode leftChildNode = node.getChild(rightChild - 1);
        BTreeNode rightChildNode = node.getChild(rightChild);

        leftChildNode.mergeKeys(rightChildNode.getKeys());

        // remove the right child from the inner node
        node.removeChildByIndex(node.getChildIndex(rightChildNode));

        // remove the key from the node
        node.removeByKey(key);
        return true;
    }

}
