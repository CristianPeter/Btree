package com.cristianpeter.btree;

import java.util.Optional;

import com.cristianpeter.btree.contracts.IBTree;
import com.cristianpeter.btree.core.BTreeNode;
import com.cristianpeter.btree.delete.DeleteStrategy;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;

public class BTree implements IBTree {
    private BTreeNode root;

    public BTree(int grade) {
        root = new BTreeNode(grade, null);
    }

    public boolean add(int value) throws NodeNotFoundException {
        return navigateThroughtNode(root, value);
    }

    public boolean delete(int key) throws NodeNotFoundException, KeyNotFoundException {
        return DeleteStrategy.delete(root, key);
    }

    @Override
    public boolean contains(int key) {
        return false;
    }

    private boolean navigateThroughtNode(BTreeNode node, int value) throws NodeNotFoundException {
        // There are not children so insert key in the root node
        if (node.getChildrenSize() == 0) {
            node.addKey(value);
            // if node is not overflowing, it's ok
            if (!node.keysOverflowing()) {
                return true;
            }
            // if it comes here, split is required
            split(node);
            return true;
        }
        // There are children, so navigate to correct one
        BTreeNode optimalNode = node.searchChild(value);
        navigateThroughtNode(optimalNode, value);

        return false;
    }

    private void split(BTreeNode node) {
        int numKeys = node.getKeysSize();
        int pivot = numKeys / 2;
        int pivotValue = node.getKey(pivot);

        // elevate to a new node
        if (node.getParent() == null) {
            BTreeNode newFather = new BTreeNode(node.getOrder(), null);
            // change root node
            this.root = elevateToFather(node, newFather, pivot, pivotValue);

        } else {
            // if has parent, elevate the pivot to father
            elevateToFather(node, node.getParent(), pivot, pivotValue);
        }

    }

    private BTreeNode elevateToFather(BTreeNode leftNode, BTreeNode parent, int pivotIndex, int pivotValue) {
        // the value will be inserted on the edges
        parent.addKey(pivotValue);

        // create a new node for right values
        BTreeNode rightNode = new BTreeNode(parent.getOrder(), parent);
        rightNode.mergeKeys(leftNode.getKeyRange(pivotIndex + 1, leftNode.getKeysSize()));

        // we must get all the right children of the pivot and assign them to the right node
        Optional<BTreeNode[]> rightNodes = leftNode.getChildrenRange(pivotIndex + 1, leftNode.getChildrenSize());

        rightNodes.ifPresent(rightNode::mergeChildren);

        // we maintain left the node, and clear the pivot, the right values and right children
        cleanNode(leftNode, pivotIndex, leftNode.getKeysSize());

        // add parent reference
        leftNode.setParent(parent);
        rightNode.setParent(parent);

        // we only add the left child if parent is a new node
        if (parent.getChildrenSize() == 0) {
            parent.addChild(0, leftNode);
        }
        // find index of pivot, to insert
        parent.addChild(parent.getNextIndexByKey(pivotValue), rightNode);

        // if the parent is overflowing, must split
        if (parent.keysOverflowing()) {
            split(parent);
        }
        return parent;
    }

    /**
     * Clean all keys from start to end index
     * 
     * @param node
     *            node to clean
     * @param startIndex
     *            first key where clean up start
     * @param endIndex
     *            last key clean up, not included
     */
    private void cleanNode(BTreeNode node, int startIndex, int endIndex) {
        for (; startIndex < endIndex; startIndex++) {
            // 0 represents empty value
            node.removeByIndex(startIndex);
            if (node.getChild(startIndex + 1) != null) {
                node.removeChildByIndex(startIndex + 1);
            }
        }
    }

    public String preOrder() {
        return root.preOrder();
    }

    public String postOrder() {
        return root.postOrder();
    }

    public String inOrder() {
        return root.inOrder();
    }

}
