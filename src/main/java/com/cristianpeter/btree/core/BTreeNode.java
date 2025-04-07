package com.cristianpeter.btree.core;


import com.cristianpeter.btree.enums.SiblingOffset;
import com.cristianpeter.btree.exceptions.KeyNotFoundException;
import com.cristianpeter.btree.exceptions.NodeNotFoundException;
import com.cristianpeter.btree.exceptions.interfaces.ExceptionalFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public class BTreeNode{

    private int keysSize = 0;
    private int childrenSize = 0;
    private int[] keys;
    private final BTreeNode[] children;
    @Setter
    private BTreeNode parent;
    private final int order;
    private final int minKeys;
    private final int maxKeys;
    private final int minChildren;
    private final int maxChildren;

    /**
     *
     * @param order
     * Here order stand for maximum keys per node
     */
    public BTreeNode(int order, BTreeNode parent){
        this.order = order;
        // overpass maximum by 1 for making split more easy
        keys = new int[order + 1];
        // overpass maximum by 1 for making split more easy
        children = new BTreeNode[order + 2];
        this.parent = parent;
        this.minKeys = (int)Math.ceil((double) (order + 1) / 2 - 1);
        this.maxKeys = order;
        this.maxChildren = order + 1;
        this.minChildren = (int) Math.ceil((double) (order + 1) / 2);
    }

    /**
     * Add key and increase key size
     * @param value to add to keys array
     */
    public void addKey(int value){
        keys[keysSize++] = value;
        Arrays.sort(keys, 0, keysSize);
    }

    /**
     * Add a child to the index position, carry the rest to the right
     * If there is a child at this position(not null) will call another method for adding children
     * @param index the position where the child must be added
     * @param child the new node
     */
    public void addChild(int index, BTreeNode child){
        if (children[index] != null){
            newChildCarryingToRight(this, index, child);
            return;
        }
        children[index] = child;
        childrenSize++;
    }

    /**
     * Will displace children to the right starting from index
     * At the index will be added the child node, childrenSize counter will be increased.
     * @param index starting point
     */
    private void newChildCarryingToRight(BTreeNode parent, int index, BTreeNode child) {
        BTreeNode carry = child;
        BTreeNode actual;
        // simulate a new child was already added with childrenSize + 1
        for (; index < parent.childrenSize + 1 ; index++) {
            actual = parent.getChild(index);
            parent.children[index] = carry;
            carry = actual;
        }
        // increase the counter
        parent.childrenSize++;
    }


    /**
     * Key size greater that allowed
     * @return
     */
    public boolean keysOverflowing(){
        return this.keysSize > maxKeys;
    }
    /**
     * Key size less that allowed
     * @return
     */
    public boolean keysUnderflowing(){
        return  ! isRoot() && this.keysSize  < minKeys;
    }

    public boolean canLendKey(){
        return  (this.keysSize - 1) >= minKeys;
    }

    /**
     * Return the key at specified index
     * @param index position
     * @return int
     */
    public int getKey(int index) {
        return keys[index];
    }

    public BTreeNode getChild(int index) {
        // control index out of bounds
        if (index < 0 || index > children.length) return null;
        return children[index];
    }
    /**
     * Return the index of the node in children array
     */
    public int getChildIndex(BTreeNode node) {
        for (int i = 0; i < keysSize; i++) {
            if (getChild(i) == node) return i;
        }
        return keysSize;
    }

    /**
     * Get an array of keys starting at
     * startIndex and finish at endIndex(not include on result)
     * @param startIndex
     * @param endIndex
     * @return
     */
    public int[] getKeyRange(int startIndex, int endIndex) {
        return Arrays.copyOfRange(keys, startIndex, endIndex);
    }

    /**
     * Return all the children in a range
     */
    public Optional<BTreeNode[]> getChildrenRange(int startIndex, int endIndex) {
        if (startIndex > endIndex){
            return Optional.empty();
        }
        return Optional.of(Arrays.copyOfRange(children, startIndex, endIndex));
    }

    public boolean isRoot(){
        return parent == null;
    }

    /**
     * Given an array of keys will overwrite the next keys starting by the keySize value
     * Will increase also keySize
     * @param leftKeys
     */
    public void mergeKeys(int[] leftKeys) {
        for (int key: leftKeys){
            if (key != 0){
                keys[keysSize] = key;
                keysSize++;
            }
        }
    }
    /**
     * Given an array of keys will overwrite the next children starting by the childrenSize value
     * Will change parent reference to new node
     * Will increase also childrenSize
     */
    public void mergeChildren(BTreeNode[] nodes, BTreeNode parent) {
        for (BTreeNode node: nodes){
            node.parent = parent;
            children[childrenSize] = node;
            childrenSize++;
        }
    }

    /**
     * @param key
     * Receive a key and return the next index position
     * Return the first index greater than the key, or keySize for default
     * @return index position
     */
    public int getNextIndexByKey(int key) {
        for (int i = 0; i < keys.length; i++){
            if (keys[i] > key) return i;
        }
        return keysSize;
    }

    /**
     * @param key
     * Receive a key and return the index position
     * Return the first lower key index, or keySize for default
     * @return index position
     */
    public int getIndexByKey(int key) throws KeyNotFoundException {
        int i = this.getNextIndexByKey(key) - 1;
        int index =  safeKeysIndex(i);
        if (keys[index] != key){
            throw new KeyNotFoundException();
        }
        return index;
    }

    /**
     * Receive and index and prevent to access index out of bound
     * @param i index position
     * @return safe index
     */
    public int safeKeysIndex(int i){
        if (i < 0) return 0;
        else if (i > keys.length - 1) return keys.length - 1;
        return i;
    }
    /**
     * Receive and index and prevent to access index out of bound
     * @param i index position
     * @return safe index
     */
    public int safeChildrenIndex(int i){
        if (i < 0) return 0;
        else if (i > children.length - 1) return children.length - 1;
        return i;
    }


    /**
     * Will return the node where key must be added
     * If the key already exists, will return that node
     * @param key key that we want to insert
     */
    public BTreeNode searchChild(int key) throws NodeNotFoundException {
        if (childrenSize == 0){
            return this;
        }
        for (int i = 0; i < keys.length; i++){
            if (getKey(i) == key){
                return this;
            }
            if (getKey(i) > key && children[i] != null){
                return children[i].searchChild(key);
            }
        }

        // if are all less, search the at the rightest
        if (children[keysSize] != null){
            return children[keysSize].searchChild(key);
        }

        throw new NodeNotFoundException();
    }

    public boolean isLeaf(){
        return this.childrenSize == 0;
    }

    public int removeLowerKey(){
        int first = keys[0];
        keys = Arrays.copyOfRange(keys, 1, keys.length);
        keysSize--;
        return first;
    }
    public int removeGreaterKey(){
        int lastIndexWithValue = keysSize - 1;
        int last = keys[lastIndexWithValue];
        keys[lastIndexWithValue] = 0;
        keysSize--;
        return last;
    }

    /**
     * Given a key, remove it, and reorganize the keys
     *
     */
    public void removeByKey(int key) throws KeyNotFoundException {
        int index = this.getIndexByKey(key);
        removeByIndex(index);
        this.reorganizekeys();
    }

    /**
     * Only delete the key at a index position, not perform reorganization
     * @param index position where perform delete
     */
    public void removeByIndex(int index) {
        keys[index] = 0;
        keysSize--;
    }


    /**
     * Remove a child reference at index position
     */
    public void removeChild(int index) {
        children[index] = null;
        childrenSize--;
    }

    public void removeChild(BTreeNode node) {
        children[this.getChildIndex(node)] = null;
        childrenSize--;
    }

    /**
     * Move all keys with value of 0 to the end
     * All positions with 0 means not value present
     */
    public void reorganizekeys(){
        // move all values greater than 0 to first positions
        int index = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != 0){
                keys[index++] = keys[i];
            }
        }
        Arrays.sort(keys, 0, index);
        Arrays.fill(keys, index, keys.length, 0);

    }


    private BTreeNode getSibling(SiblingOffset offset){
        for (int i = 0; i < parent.children.length; i++) {
            if (parent.getChild(i) == this) return parent.getChild(i + offset.getOffset());
        }
        return null;
    }


    public BTreeNode leftSibling(){
        return this.getSibling(SiblingOffset.LEFT_SIBLING);
    }
    public BTreeNode rightSibling(){
        return this.getSibling(SiblingOffset.RIGHT_SIBLING);
    }
    public Optional<BTreeNode> greaterLSB(int key) {
        return withoutException(this::searchChild, key);
    }

    public Optional<BTreeNode> lowerRSB(int key) {
        return withoutException(this::searchChild, key);
    }

    public Optional<BTreeNode> withoutException(ExceptionalFunction<Integer, BTreeNode> function, int key) {
        try {
            return Optional.ofNullable(function.apply(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
