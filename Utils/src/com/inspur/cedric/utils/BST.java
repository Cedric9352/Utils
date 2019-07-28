package com.inspur.cedric.utils;

import java.util.Stack;

import com.inspur.cedric.utils.BSTNode;

public class BST <T extends Comparable<? super T>>
{
    private BSTNode<T> root;

    public BST() {
        this.root = null;
    }

    private BSTNode<T> removeMin(BSTNode<T> node, BSTNode<T> parent, boolean isLeft) {
        BSTNode<T> currNode = node, parentNode = parent;
        while(currNode.getLeft() != null) {
            parentNode = currNode;
            currNode = currNode.getLeft();
        }
        if(parentNode != parent) {
            // node has at least one left child
            parentNode.setLeft(currNode.getRight());
        } else {
            // node has no left child, delete itself
            if(isLeft) {
                parentNode.setLeft(currNode.getRight());
            } else {
                parentNode.setRight(currNode.getRight());
            }
        }
        currNode.setRight(null);
        return currNode;
    }

    public BSTNode<T> search(T t) {
        BSTNode<T> currNode = root;
        while(currNode != null) {
            if(currNode.getData().compareTo(t) == 0) {
                break;
            } else if(currNode.getData().compareTo(t) > 0) {
                currNode = currNode.getLeft();
            } else {
                currNode = currNode.getRight();
            }
        }
        return currNode;
    }

    public void insert(T t) {
        if(root == null) {
            root = new BSTNode<>(t);
        } else {
            BSTNode<T> currNode = root, parentNode = null;
            while(true) {
                if(currNode.getData().compareTo(t) == 0) {
                    // already have one
                    return;
                } else {
                    parentNode = currNode;
                    if(currNode.getData().compareTo(t) > 0) {
                        currNode = currNode.getLeft();
                        if(currNode == null) {
                            BSTNode<T> newNode = new BSTNode<>(t);
                            parentNode.setLeft(newNode);
                            return;
                        }
                    } else {
                        currNode = currNode.getRight();
                        if(currNode == null) {
                            BSTNode<T> newNode = new BSTNode<>(t);
                            parentNode.setRight(newNode);
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean remove(T t) {
        boolean res = false;
        if(root != null) {
            BSTNode<T> currNode = root, parentNode = null;
            boolean isLeft = false;
            while(currNode != null) {
                if(currNode.getData().compareTo(t) > 0) {
                    parentNode = currNode;
                    currNode = currNode.getLeft();
                    isLeft = true;
                } else if(currNode.getData().compareTo(t) < 0) {
                    parentNode = currNode;
                    currNode = currNode.getRight();
                    isLeft = false;
                } else {
                    // find it
                    if(!currNode.hasLeft() && !currNode.hasRight()) {
                        if(parentNode != null) {
                            if(isLeft) {
                                parentNode.setLeft(null);
                            } else {
                                parentNode.setRight(null);
                            }
                        } else {
                            // delete root and has no children
                            root = null;
                        }
                        res = true;
                        break;
                    } else if (currNode.hasLeft() && !currNode.hasRight()) {
                        if(parentNode != null) {
                            if(isLeft) {
                                parentNode.setLeft(currNode.getLeft());
                            } else {
                                parentNode.setRight(currNode.getLeft());
                            }
                        } else {
                            // delete root and has a left child
                            root = currNode.getLeft();
                        }
                        // for sake of garbage collection
                        currNode.setLeft(null);
                        res = true;
                        break;
                    } else if(!currNode.hasLeft() && currNode.hasRight()) {
                        if(parentNode != null) {
                            if(isLeft) {
                                parentNode.setLeft(currNode.getRight());
                            } else {
                                parentNode.setRight(currNode.getRight());
                            }
                        } else {
                            // delete root and has a right child
                            root = currNode.getRight();
                        }
                        // for sake of garbage collection
                        currNode.setRight(null);
                        res = true;
                        break;
                    } else {
                        if(parentNode != null) {
                            BSTNode<T> rightMin = removeMin(currNode.getRight(), currNode, false);
                            if(isLeft) {
                                parentNode.setLeft(rightMin);
                            } else {
                                parentNode.setRight(rightMin);
                            }
                            rightMin.setLeft(currNode.getLeft());
                            rightMin.setRight(currNode.getRight());
                        } else {
                            root = removeMin(currNode.getRight(), currNode, false);
                            root.setLeft(currNode.getLeft());
                            root.setRight(currNode.getRight());
                        }
                        // for sake of garbage collection
                        currNode.setLeft(null);
                        currNode.setRight(null);
                        res = true;
                        break;
                    }
                }
            }
        }
        return res;
    }

    // in-order traversal
    public boolean check() {
        if(root == null) {
            return true;
        } else {
            BSTNode<T> currNode = root, previousNode = null;
            Stack<BSTNode<T>> stack = new Stack<>();
            while(currNode != null || !stack.isEmpty()) {
                if(currNode != null) {
                    stack.push(currNode);
                    currNode = currNode.getLeft();
                } else {
                    currNode = stack.pop();
                    if(previousNode != null && previousNode.getData().compareTo(currNode.getData()) > 0) {
                        return false;
                    }
                    previousNode = currNode;
                    currNode = currNode.getRight();
                }
            }
            return true;
        }
    }
}
