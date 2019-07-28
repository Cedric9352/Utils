package com.inspur.cedric.utils;

public class BSTNode <T extends Comparable<? super T>>
{
    private BSTNode<T> left;
    private BSTNode<T> right;
    private T data;

    public BSTNode() {
        this(null);
    }

    public BSTNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setLeft(BSTNode<T> left) {
        this.left = left;
    }
    public BSTNode<T> getLeft() {
        return left;
    }

    public void setRight(BSTNode<T> right) {
        this.right = right;
    }
    public BSTNode<T> getRight() {
        return right;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }

    @Override
    public String toString() {
        return data == null ? "null" : data.toString();
    }
}
