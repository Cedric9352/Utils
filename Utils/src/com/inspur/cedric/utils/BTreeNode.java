package com.inspur.cedric.utils;

public class BTreeNode <K extends Comparable<? super K>, V>
{
    private int order;
    private int currentPairNumber;
    private boolean isRoot;
    private BTreeNodeData<K, V>[] records;
    private BTreeNode<K, V>[] children;
    private BTreeNode<K, V> parent;

    @SuppressWarnings("unchecked")
    public BTreeNode(int order) {
        this.order = order;
        records = (BTreeNodeData<K, V>[])new BTreeNodeData[this.order];
        children = (BTreeNode<K, V>[])new BTreeNode[this.order+1];
        isRoot = false;
        currentPairNumber = 0;
    }
    public BTreeNode<K, V> getChild(int pos) {
        return children[pos];
    }
    public void setChild(BTreeNode<K, V> child, int pos) {
        children[pos] = child;
    }
    public BTreeNodeData<K, V> getRecord(int pos) {
        return records[pos];
    }
    public void setRecord(BTreeNodeData<K, V> record, int pos) {
        records[pos] = record;
    }
    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }
    public BTreeNode<K, V> getParent() {
        return parent;
    }
    public void setParent(BTreeNode<K, V> parent) {
        this.parent = parent; 
    }
    public int getCurrentPairNumber() {
        return currentPairNumber;
    }
    public void incCurrentPairNumber() {
        currentPairNumber++;
    }
    public void decCurrentPairNumber() {
        currentPairNumber--;
    }
    public int getMaxPairNumbers() {
        return order-1;
    }
    public int getMinPairNumbers() {
        int res = 1;
        if(!isRoot) {
            if(order % 2 == 0) {
                res = order >> 1 - 1;
            } else {
                res = order >> 1;
            }
        }
        return res;
    }
    public int getPosition(K key) {
        int start = 0, end = getCurrentPairNumber();
        while(start < end) {
            int mid = ((end - start) >> 1) + start;
            if(records[mid].getKey().compareTo(key) >= 0) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < order-1; ++i) {
            if(records[i] == null) {
                stringBuilder.append("null ");
            } else {
                stringBuilder.append(records[i].toString() + " ");
            }
        }
        stringBuilder.append(records[order-1] == null ? "null" : records[order-1].toString());
        return stringBuilder.toString();
    }
    public boolean check() {
        boolean res =  (currentPairNumber <= getMaxPairNumbers()) && (currentPairNumber >= getMinPairNumbers());
        if(res) {
            for(int i = 1; i < getMaxPairNumbers(); ++i) {
                if(getRecord(i) != null && (getRecord(i).getKey().compareTo(getRecord(i-1).getKey()) < 0)) {
                    res &= false;
                    break;
                }
            }
        }
        return res;
    }
}
