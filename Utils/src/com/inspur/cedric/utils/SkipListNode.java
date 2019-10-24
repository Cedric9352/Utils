package com.inspur.cedric.utils;

/**
 * created on 2019年6月14日<br/>
 * description: basic data structure of skip linked-list node<br/>
 * author: cedric<br/>
 * @param <T>
 */
public class SkipListNode<T> {

    private T key;
    private double score;
    private int[] span;
    private SkipListNode<T> next[];
    private SkipListNode<T> backward;
    
    @SuppressWarnings("unchecked")
    public SkipListNode(T key, int level, double score) {
        this.key = key;
        this.score = score;
        this.span = new int[level];
        for(int i = 0; i < level; ++i) {
            span[i] = -1;
        }
        this.next = (SkipListNode<T>[])new SkipListNode[level];
    }
    
    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public SkipListNode<T> getNext(int level) {
        return this.next[level];
    }

    public void setNext(SkipListNode<T> next, int level) {
        this.next[level] = next;
    }
    
    public int getLength() {
        return this.next.length;
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getSpan(int level) {
        return this.span[level];
    }

    public void setSpan(int span, int level) {
        this.span[level] = span;
    }

    public void incSpan(int level) {
        this.span[level]++;
    }

    public void incSpan(int level, int span) {
        this.span[level] += span;
    }

    public void decSpan(int level) {
        this.span[level]--;
    }

    public SkipListNode<T> getBackward() {
        return backward;
    }

    public void setBackward(SkipListNode<T> backward) {
        this.backward = backward;
    }
}
