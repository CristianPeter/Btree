package com.cristianpeter.btree.delete;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.cristianpeter.btree.core.BTreeNode;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;

public class LeafDeleteStrategy extends DeleteStrategy {

    public boolean deleteImpl(BTreeNode node, int key) throws KeyNotFoundException {
        // first remove the key
        node.removeByKey(key);
        // balance tree if needed
        if (node.keysUnderflowing()) {
            return this.balanceTree(node, key);
        }
        return false;
    }

    @Override
    public boolean borrowSiblingImpl(BTreeNode node, int key) throws KeyNotFoundException {
        return this.borrowSibling(node, key);
    }

    @Override
    public boolean mergeSiblingsImpl(BTreeNode node, int key) throws KeyNotFoundException {
        return this.mergeSiblings(node, key);
    }

    public Optional<Pair<BTreeNode, Function<BTreeNode, Integer>>> lendKeyProvider(BTreeNode node, int key) {
        BTreeNode leftSibling = node.leftSibling();
        BTreeNode rightSibling = node.rightSibling();

        if (leftSibling != null) {
            return Optional.of(ImmutablePair.of(leftSibling, BTreeNode::removeGreaterKey));
        } else if (rightSibling != null) {
            return Optional.of(ImmutablePair.of(rightSibling, BTreeNode::removeLowerKey));
        } else {
            return Optional.empty();
        }
    }

    private boolean borrowSibling(BTreeNode node, int key) throws KeyNotFoundException {
        Pair<BTreeNode, Function<BTreeNode, Integer>> pair = this.lendKeyProvider(node, key).orElse(null);
        if (pair != null && pair.getLeft() != null) {
            // remove lend key from sibling
            int lendKey = pair.getRight().apply(pair.getLeft());

            this.borrowParentKeyToChild(node);

            // add key from de sibling to the parent
            node.getParent().addKey(lendKey);

            if (!node.isLeaf()) {
                int index = pair.getLeft().safeChildrenIndex(pair.getLeft().getNextIndexByKey(lendKey) + 1);
                BTreeNode rightChild = pair.getLeft().getChild(index);
                // remove reference cause right child will be moved to left side of current node
                pair.getLeft().removeChild(index);
                node.addChild(0, rightChild);
            }

            return true;
        }
        return false;

    }

    protected boolean mergeSiblings(BTreeNode node, int key) throws KeyNotFoundException {
        // merge this node with the right or left sibling if exists
        BTreeNode sibling = this.lendKeyProvider(node, key).map(Pair::getLeft).orElseThrow(RuntimeException::new);

        sibling.mergeKeys(node.getKeys());

        // add the parent key to the sibling node
        // remove child reference for the node where the key was deleted
        borrowParentKeyToChild(sibling);

        // remove from parent the node reference that is going to disappear
        node.getParent().removeChild(node);
        return true;
    }

}
