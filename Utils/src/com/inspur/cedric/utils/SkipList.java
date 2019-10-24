package com.inspur.cedric.utils;

import java.util.Random;

/**
 * created on 2019年6月14日<br/>
 * description: basic data structure of skip linked-list<br/>
 * author: cedric<br/>
 */
public class SkipList <T extends Comparable<? super T>> {

    private int maxLevel;
    private int currLevel;
    private SkipListNode<T> root;
    private int powers[];
    private Random rd = new Random(49);
    
    public SkipList() {
        this(32);
    }
    
    public SkipList(int level) {
        maxLevel = level;
        currLevel = 1;
        // root represents a header, with a null object and a level array
        root = new SkipListNode<>(null, maxLevel, 0);
        powers = new int[maxLevel];
        calPower();
    }
    
    private void calPower() {
        powers[maxLevel-1] = 2 << (maxLevel-1);
        for(int i = maxLevel-2; i >= 0; --i) {
            powers[i] = 2 << i;
        }
    }
    
    private int chooseLevel() {
        int r = Math.abs(rd.nextInt()) % powers[maxLevel-1], res = 1;
        for(int i = maxLevel-2; i >= 0; --i) {
            if(r < powers[i]) {
                res = maxLevel - i;
            } else {
                break;
            }
        }
        return res;
    }
    
    public T search(T key) {
        SkipListNode<T> curr, prev;
        int level = currLevel - 1;
        prev = curr = root.getNext(level);
        while(curr != null) {
            // case 1: find the element
            if(key.equals(curr.getKey())) {
                return curr.getKey();
            // case 2: curr is bigger than key
            } else if(key.compareTo(curr.getKey()) < 0) {
                if(level == 0) {
                    return null;
                } else if(curr == root.getNext(level)) {
                    curr = root.getNext(--level);
                } else {
                    /**
                     * example: 
                     * a ------> c
                     * |         |
                     * a -> b -> c
                     * curr = c_upper, prev = a_upper, we find that e is bigger than what we want,
                     * so we move to lower floor's next element, which is a_lower's next: b
                     */
                    curr = prev.getNext(--level);
                }
            } else {
                prev = curr;
                if(curr.getNext(level) != null) {
                    curr = curr.getNext(level);
                } else {
                    for(level--; level >= 0 && curr.getNext(level) == null;level--);
                    if(level >= 0) {
                        curr = curr.getNext(level);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public void insert(T key, double score) {
        SkipListNode<T>[] needToBeUpdated = (SkipListNode<T>[])new SkipListNode[maxLevel];
        int[] rank = new int[maxLevel];
        SkipListNode<T> curr = root;
        for(int i = currLevel-1; i >= 0; --i) {
            rank[i] = i == currLevel-1 ? 0 : rank[i+1];
            while(curr.getNext(i) != null
                    && (curr.getNext(i).getScore() < score
                            || (Double.compare(curr.getNext(i).getScore(), score) == 0)
                            && curr.getNext(i).getKey().compareTo(key) < 0)
            ) {
                rank[i] += curr.getSpan(i);
                curr = curr.getNext(i);
            }
            needToBeUpdated[i] = curr;
        }
        int level = chooseLevel();
        curr = new SkipListNode<>(key, level, score);
        if(level > currLevel) {
            for(int i = currLevel; i < level; ++i) {
                rank[i] = 0;
                needToBeUpdated[i] = root;
                needToBeUpdated[i].setSpan(-1, i);
            }
            currLevel = level;
        }
        for(int i = 0; i < level; ++i) {
            curr.setNext(needToBeUpdated[i].getNext(i), i);
            needToBeUpdated[i].setNext(curr, i);
            /**
             * 1. rank[i] is the distance between root and position of ith layer that new node to be inserted
             * 2. span is the distance between node and its next node on ith layer
             */
            int span = needToBeUpdated[i].getSpan(i) == -1 ? -1 : needToBeUpdated[i].getSpan(i) - (rank[0] - rank[i]);
            curr.setSpan(span, i);
            needToBeUpdated[i].setSpan(rank[0] - rank[i] + 1, i);
        }
        for(int i = level; i < currLevel; ++i) {
            // all level above new node increment their span by 1 on ith layer
            needToBeUpdated[i].incSpan(i);
        }
        // update backward pointer
        curr.setBackward(needToBeUpdated[0] == root ? null : needToBeUpdated[0]);
        if(curr.getNext(0) != null) {
            curr.getNext(0).setBackward(curr);
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean remove(T key, double score) {
        SkipListNode<T>[] needToBeUpdated = (SkipListNode<T>[])new SkipListNode[currLevel];
        SkipListNode<T> curr = root;
        for(int i = currLevel-1; i >= 0; --i) {
            while(curr.getNext(i) != null
                    && (curr.getNext(i).getScore() < score
                            || (Double.compare(curr.getNext(i).getScore(), score) == 0)
                            && curr.getNext(i).getKey().compareTo(key) < 0)
            ) {
                curr = curr.getNext(i);
            }
            needToBeUpdated[i] = curr;
        }
        curr = curr.getNext(0);
        if(curr != null && curr.getKey().compareTo(key) == 0) {
            for(int i = 0; i < currLevel; ++i) {
                if(curr == needToBeUpdated[i].getNext(i)) {
                    if(curr.getNext(i) == null) {
                        needToBeUpdated[i].setSpan(-1, 0);
                    } else {
                        needToBeUpdated[i].incSpan(i, curr.getSpan(i)-1);
                    }
                    needToBeUpdated[i].setNext(curr.getNext(i), i);
                } else {
                    needToBeUpdated[i].decSpan(i);
                }
            }
            if(curr.getNext(0) != null) {
                curr.getNext(0).setBackward(curr.getBackward());
            }
            // decrement current level if it's empty
            while(currLevel > 1 && root.getNext(currLevel-1) == null) {
                currLevel--;
            }
            return true;
        }
        return false;
    }
    
    public void visualize() {
        if(root != null && root.getNext(0) != null) {
            SkipListNode<T> curr = root.getNext(0);
            while(curr != null) {
                System.out.print(curr.getKey() + "(" + curr.getLength() + ", " + curr.getScore() +  ")");
                System.out.print(" -> ");
                curr = curr.getNext(0);
            }
            System.out.println("NULL");
        }
    }
}
