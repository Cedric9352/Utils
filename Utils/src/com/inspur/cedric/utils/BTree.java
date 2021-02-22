package com.inspur.cedric.utils;

import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<? super K>, V> {
    private static final int DEFAULT_ORDER = 3;

    static class BTreeNodeData<K extends Comparable<? super K>, V> {
        final K key;
        final V value;

        BTreeNodeData(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "{" +
                    (key == null ? "null" : key.toString()) +
                    ":" +
                    (value == null ? "null" : value.toString()) +
                    "}";
        }
    }

    static class BTreeNode<K extends Comparable<? super K>, V> {
        int order;
        int currentPairNumber;
        boolean isRoot;
        BTreeNodeData<K, V>[] records;
        BTreeNode<K, V>[] children;
        BTreeNode<K, V> parent;

        @SuppressWarnings("unchecked")
        BTreeNode(int order) {
            this.order = order;
            records = (BTreeNodeData<K, V>[]) new BTreeNodeData[this.order];
            children = (BTreeNode<K, V>[]) new BTreeNode[this.order + 1];
            isRoot = false;
            currentPairNumber = 0;
        }

        BTreeNode<K, V> getChild(int pos) {
            return children[pos];
        }

        void setChild(BTreeNode<K, V> child, int pos) {
            children[pos] = child;
        }

        BTreeNodeData<K, V> getRecord(int pos) {
            return records[pos];
        }

        void setRecord(BTreeNodeData<K, V> record, int pos) {
            records[pos] = record;
        }

        void setRoot(boolean isRoot) {
            this.isRoot = isRoot;
        }

        BTreeNode<K, V> getParent() {
            return parent;
        }

        void setParent(BTreeNode<K, V> parent) {
            this.parent = parent;
        }

        int getCurrentPairNumber() {
            return currentPairNumber;
        }

        void incCurrentPairNumber() {
            currentPairNumber++;
        }

        void decCurrentPairNumber() {
            currentPairNumber--;
        }

        int getMaxPairNumbers() {
            return order - 1;
        }

        int getMinPairNumbers() {
            int res = 1;
            if (!isRoot) {
                if (order % 2 == 0) {
                    res = (order >> 1) - 1;
                } else {
                    res = order >> 1;
                }
            }
            return res;
        }

        int getPosition(K key) {
            int start = 0, end = getCurrentPairNumber();
            while (start < end) {
                int mid = ((end - start) >> 1) + start;
                if (records[mid].getKey().compareTo(key) >= 0) {
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
            for (int i = 0; i < order - 1; ++i) {
                if (records[i] == null) {
                    stringBuilder.append("null ");
                } else {
                    stringBuilder.append(records[i].toString()).append(" ");
                }
            }
            stringBuilder.append(records[order - 1] == null ? "null" : records[order - 1].toString());
            return stringBuilder.toString();
        }

        boolean check() {
            boolean res = (currentPairNumber <= getMaxPairNumbers()) && (currentPairNumber >= getMinPairNumbers());
            if (res) {
                for (int i = 1; i < getMaxPairNumbers(); ++i) {
                    if (getRecord(i) != null && (getRecord(i).getKey().compareTo(getRecord(i - 1).getKey()) < 0)) {
                        res = false;
                        break;
                    }
                }
            }
            return res;
        }
    }

    private int order;
    private BTreeNode<K, V> root;

    public BTree() {
        this(DEFAULT_ORDER);
    }

    public BTree(int order) {
        this.order = order;
        root = new BTreeNode<>(this.order);
        root.setRoot(true);
    }

    public V search(K key) {
        V res = null;
        if (root != null) {
            BTreeNode<K, V> curr = root;
            int pos;
            while (curr != null) {
                pos = curr.getPosition(key);
                // find one, return the value
                if (curr.getRecord(pos) != null && curr.getRecord(pos).getKey().compareTo(key) == 0) {
                    res = curr.getRecord(pos).getValue();
                    break;
                    /*
                     * the value we calculated before is the first position that bigger than or equal with key,
                     * so this condition is that the key in this position is bigger than parameter-key
                     */
                } else if (curr.getChild(pos) != null) {
                    // just go to next level for leaf node!
                    curr = curr.getChild(pos);
                } else {
                    break;
                }
            }
        }
        return res;
    }

    public void insert(K key, V value) {
        if (root != null) {
            BTreeNode<K, V> curr = root;
            int pos = 0;
            while (curr != null) {
                pos = curr.getPosition(key);
                // find one, replace value
                if (curr.getRecord(pos) != null && curr.getRecord(pos).getKey().compareTo(key) == 0) {
                    curr.setRecord(new BTreeNodeData<>(key, value), pos);
                    return;
                    /*
                     * the value we calculated before is the first position that bigger than or equal with key,
                     * so this condition is that the key in this position is bigger than parameter-key
                     */
                } else if (curr.getChild(pos) != null) {
                    // just go to next level for leaf node!
                    curr = curr.getChild(pos);
                } else {
                    break;
                }
            }

            // prepare
            // 1.move elements that bigger than key towards a step in order to leave enough space for new node
            assert curr != null;
            for (int i = curr.getCurrentPairNumber(); i > pos; --i) {
                curr.setRecord(curr.getRecord(i - 1), i);
            }
            // 2.set key at correct position
            curr.setRecord(new BTreeNodeData<>(key, value), pos);
            curr.incCurrentPairNumber();
            while (curr.getCurrentPairNumber() > curr.getMaxPairNumbers()) {
                int mid = order >> 1;
                BTreeNode<K, V> parent = curr.getParent();
                // split
                if (parent != null) {
                    BTreeNode<K, V> sibling = new BTreeNode<>(order);
                    // 1.move origin's right part to new node's left part and reset parent
                    for (int i = mid + 1, idx = 0; i < order; ++i, ++idx) {
                        sibling.setRecord(curr.getRecord(i), idx);
                        sibling.incCurrentPairNumber();
                        curr.setRecord(null, i);
                        curr.decCurrentPairNumber();
                        sibling.setChild(curr.getChild(i), idx);
                        if (sibling.getChild(idx) != null) {
                            sibling.getChild(idx).setParent(sibling);
                        }
                        curr.setChild(null, i);
                    }
                    sibling.setChild(curr.getChild(order), sibling.getCurrentPairNumber());
                    if (sibling.getChild(sibling.getCurrentPairNumber()) != null) {
                        sibling.getChild(sibling.getCurrentPairNumber()).setParent(sibling);
                    }
                    curr.setChild(null, order);
                    sibling.setParent(parent);
                    // 2.save middle element
                    BTreeNodeData<K, V> midData = curr.getRecord(mid);
                    curr.setRecord(null, mid);
                    curr.decCurrentPairNumber();
                    // 3.move extra and children to right position
                    int correctPos = parent.getPosition(midData.getKey());
                    for (int i = parent.getCurrentPairNumber(); i > correctPos; --i) {
                        parent.setRecord(parent.getRecord(i - 1), i);
                        parent.setChild(parent.getChild(i), i + 1);
                    }
                    parent.setChild(sibling, correctPos + 1);
                    parent.setRecord(midData, correctPos);
                    parent.incCurrentPairNumber();
                    curr = parent;
                } else {
                    BTreeNode<K, V> newRoot = new BTreeNode<>(order);
                    BTreeNode<K, V> sibling = new BTreeNode<>(order);
                    // 1.move origin's right part to new node's left part and reset parent
                    for (int i = mid + 1, idx = 0; i < order; ++i, ++idx) {
                        sibling.setRecord(curr.getRecord(i), idx);
                        sibling.incCurrentPairNumber();
                        curr.setRecord(null, i);
                        curr.decCurrentPairNumber();
                        sibling.setChild(curr.getChild(i), idx);
                        if (sibling.getChild(idx) != null) {
                            sibling.getChild(idx).setParent(sibling);
                        }
                        curr.setChild(null, i);
                    }
                    sibling.setChild(curr.getChild(order), sibling.getCurrentPairNumber());
                    if (sibling.getChild(sibling.getCurrentPairNumber()) != null) {
                        sibling.getChild(sibling.getCurrentPairNumber()).setParent(sibling);
                    }
                    curr.setChild(null, order);
                    sibling.setParent(newRoot);
                    curr.setParent(newRoot);
                    // 2.save middle element
                    BTreeNodeData<K, V> midData = curr.getRecord(mid);
                    newRoot.setRecord(midData, 0);
                    newRoot.incCurrentPairNumber();
                    curr.setRecord(null, mid);
                    curr.decCurrentPairNumber();
                    curr.setRoot(false);
                    // 3.set children
                    newRoot.setChild(curr, 0);
                    newRoot.setChild(sibling, 1);
                    newRoot.setRoot(true);
                    root = newRoot;

                }
            }
        }
    }

    public boolean remove(K key) {
        boolean res = false, found = false;
        if (root != null) {
            BTreeNode<K, V> curr = root;
            int pos = 0;
            while (curr != null) {
                pos = curr.getPosition(key);
                // find one, return the value
                if (curr.getRecord(pos) != null && curr.getRecord(pos).getKey().compareTo(key) == 0) {
                    found = true;
                    break;
                    /*
                     * the value we calculated before is the first position that bigger than or equal with key,
                     * so this condition is that the key in this position is bigger than parameter-key
                     */
                } else if (curr.getChild(pos) != null) {
                    // just go to next level for leaf node!
                    curr = curr.getChild(pos);
                } else {
                    break;
                }
            }
            // find it
            if (found) {
                res = true;
                BTreeNode<K, V> adjacent = curr.getChild(pos + 1);
                if (adjacent != null) {
                    // 1.middle node, swap adjacent minimum element with current deleting
                    while (adjacent.getChild(0) != null) {
                        adjacent = adjacent.getChild(0);
                    }
                    curr.setRecord(adjacent.getRecord(0), pos);
                    curr = adjacent;
                    for (int i = 1; i < curr.getCurrentPairNumber(); ++i) {
                        curr.setRecord(curr.getRecord(i), i - 1);
                    }
                    curr.setRecord(null, curr.getCurrentPairNumber() - 1);
                    curr.decCurrentPairNumber();
                } else {
                    // 2.leaf node, just delete
                    for (int i = pos + 1; i < curr.getCurrentPairNumber(); ++i) {
                        curr.setRecord(curr.getRecord(i), i - 1);
                    }
                    curr.setRecord(null, curr.getCurrentPairNumber() - 1);
                    curr.decCurrentPairNumber();
                }
                // 3.finally, start loop
                while (curr.getCurrentPairNumber() < curr.getMinPairNumbers()) {
                    BTreeNode<K, V> parent = curr.getParent();
                    // find the position
                    pos = parent.getPosition(curr.getRecord(0).getKey());
                    BTreeNode<K, V> siblingLeft = null, siblingRight = null;
                    if (pos == 0) {
                        siblingRight = parent.getChild(1);
                    } else if (pos == parent.getCurrentPairNumber()) {
                        siblingLeft = parent.getChild(parent.getCurrentPairNumber() - 1);
                    } else {
                        siblingLeft = parent.getChild(pos - 1);
                        siblingRight = parent.getChild(pos + 1);
                    }
                    // borrow from siblings
                    if ((siblingLeft != null
                            && siblingLeft.getCurrentPairNumber() > siblingLeft.getMinPairNumbers())) {
                        for (int i = 0; i < curr.getCurrentPairNumber(); ++i) {
                            curr.setRecord(curr.getRecord(i), i + 1);
                        }
                        // move from parent to current
                        curr.setRecord(parent.getRecord(pos - 1), 0);
                        curr.incCurrentPairNumber();
                        // move from sibling to parent
                        parent.setRecord(siblingLeft.getRecord(siblingLeft.getCurrentPairNumber() - 1), pos - 1);
                        // move toward
                        siblingLeft.setRecord(null, siblingLeft.getCurrentPairNumber() - 1);
                        siblingLeft.decCurrentPairNumber();
                        break;
                    } else if (siblingRight != null
                            && siblingRight.getCurrentPairNumber() > siblingRight.getMinPairNumbers()) {
                        // move from parent to current
                        curr.setRecord(parent.getRecord(pos), curr.getCurrentPairNumber());
                        curr.incCurrentPairNumber();
                        // move from sibling to parent
                        parent.setRecord(siblingRight.getRecord(0), pos);
                        // move toward
                        for (int i = 1; i < siblingRight.getCurrentPairNumber(); ++i) {
                            siblingRight.setRecord(siblingRight.getRecord(i), i - 1);
                        }
                        siblingRight.setRecord(null, siblingRight.getCurrentPairNumber() - 1);
                        siblingRight.decCurrentPairNumber();
                        break;
                    } else {
                        if (siblingLeft != null) {
                            // move parent to left's tail
                            siblingLeft.setRecord(parent.getRecord(pos - 1), siblingLeft.getCurrentPairNumber());
                            siblingLeft.incCurrentPairNumber();
                            // move parent's everything toward
                            int len = parent.getCurrentPairNumber();
                            for (int i = len - 1; i >= pos; --i) {
                                parent.setRecord(parent.getRecord(i), i - 1);
                                parent.setChild(parent.getChild(i), i - 1);
                            }
                            parent.setChild(parent.getChild(len), len - 1);
                            parent.setRecord(null, len - 1);
                            parent.setChild(null, len);
                            parent.decCurrentPairNumber();
                            // merge current node
                            int lenLeft = siblingLeft.getCurrentPairNumber(), lenCurr = curr.getCurrentPairNumber();
                            for (int i = lenLeft, j = 0; j < lenCurr; ++i, ++j) {
                                siblingLeft.setRecord(curr.getRecord(j), i);
                                siblingLeft.setChild(curr.getChild(j), i);
                                siblingLeft.incCurrentPairNumber();
                                curr.setRecord(null, j);
                                curr.setChild(null, j);
                                curr.decCurrentPairNumber();
                            }
                            siblingLeft.setChild(curr.getChild(lenCurr), siblingLeft.getCurrentPairNumber());
                            // for sake of GC
                            curr.setParent(null);
                            // very import, make child pointer to siblingLeft instead of current
                            parent.setChild(siblingLeft, pos - 1);
                            curr = parent;
                        } else if (siblingRight != null) {
                            // move parent to current's tail
                            curr.setRecord(parent.getRecord(pos), curr.getCurrentPairNumber());
                            curr.incCurrentPairNumber();
                            // move parent's everything toward
                            int len = parent.getCurrentPairNumber();
                            for (int i = len - 1; i > pos; --i) {
                                parent.setRecord(parent.getRecord(i), i - 1);
                                parent.setChild(parent.getChild(i), i - 1);
                            }
                            parent.setChild(parent.getChild(len), len - 1);
                            parent.setRecord(null, len - 1);
                            parent.setChild(null, len);
                            parent.decCurrentPairNumber();
                            // merge siblingRight
                            int lenRight = siblingRight.getCurrentPairNumber(), lenCurr = curr.getCurrentPairNumber();
                            for (int i = lenCurr, j = 0; j < lenRight; ++i, ++j) {
                                curr.setRecord(siblingRight.getRecord(j), i);
                                curr.setChild(siblingRight.getChild(j), i);
                                curr.incCurrentPairNumber();
                                siblingRight.setRecord(null, j);
                                siblingRight.setChild(null, j);
                                siblingRight.decCurrentPairNumber();
                            }
                            curr.setChild(siblingRight.getChild(lenRight), curr.getCurrentPairNumber());
                            // for sake of GC
                            siblingRight.setParent(null);
                            // very import, make child pointer to current instead of siblingRight
                            parent.setChild(curr, pos);
                            curr = parent;
                        }
                        // this means root need to be merge
                        if (parent.getCurrentPairNumber() == 0) {
                            BTreeNode<K, V> newRoot = parent.getChild(0);
                            newRoot.setRoot(true);
                            newRoot.setParent(null);
                            parent.setChild(null, 0);
                            root = newRoot;
                            curr = root;
                        }
                    }
                }
            }
        }
        return res;
    }

    private boolean inOrderCheck(BTreeNode<K, V> node, List<K> collection) {
        if (node != null) {
            for (int i = 0; i < order; ++i) {
                if (node.check() && inOrderCheck(node.getChild(i), collection)) {
                    if (node.getRecord(i) != null) {
                        collection.add(node.getRecord(i).getKey());
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean check() {
        boolean res = true;
        List<K> collection = new ArrayList<>();
        if (inOrderCheck(root, collection)) {
            for (int i = 1; i < collection.size(); ++i) {
                if (collection.get(i).compareTo(collection.get(i - 1)) < 0) {
                    res = false;
                    break;
                }
            }
        } else {
            res = false;
        }
        collection.clear();
        return res;
    }
}
