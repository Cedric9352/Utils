package com.inspur.cedric.utils;

/**
 * created on 2019年6月14日<br/>
 * description: basic data structure of skip linked-list node<br/>
 * author: cedric<br/>
 * @param <T>
 */
public class SkipListNode<T> {

    private T key;
    private SkipListLevel level[];

    private class SkipListLevel {
        private SkipListNode<T> next;
    }
    
    @SuppressWarnings("unchecked")
    public SkipListNode(T key, int level) {
        this.key = key;
        this.level = (SkipListNode<T>.SkipListLevel[])new SkipListNode.SkipListLevel[level];
        for(int i = 0; i < level; ++i) {
            this.level[i] = new SkipListLevel();
        }
    }
    
    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public SkipListNode<T> getNext(int level) {
        return this.level[level].next;
    }

    public void setNext(SkipListNode<T> next, int level) {
        this.level[level].next = next;
    }
    
    public int getLength() {
        return this.level.length;
    }
}
