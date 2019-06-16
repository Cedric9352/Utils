package com.inspur.cedric.utils;

import java.util.Random;

/**
 * created on 2019年6月14日<br/>
 * description: basic data structure of skip linked-list<br/>
 * author: cedric<br/>
 */
public class SkipList <T extends Comparable<? super T>> {

    private int maxLevel;
    private SkipListNode<T> root;
    private int powers[];
    private Random rd = new Random(49);
    
    public SkipList() {
        this(32);
    }
    
    public SkipList(int level) {
        this.maxLevel = level;
        // root represents a header, with a null object and a level array
        root = new SkipListNode<>(null, maxLevel);
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
        int r = Math.abs(rd.nextInt()) % powers[maxLevel-1], res = 0;
        for(int i = maxLevel-2; i >= 0; --i) {
            if(r < powers[i]) {
                res = maxLevel - i - 1;
            } else {
                break;
            }
        }
        return res;
    }
    
    public T search(T key) {
        int level;
        SkipListNode<T> curr, prev;
        for(level = maxLevel-1; level >= 0 && root.getNext(level) == null; --level); //first non-null node
        prev = curr = root.getNext(level);
        while(true) {
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
                     * d ------> e
                     * |         |
                     * a -> b -> c
                     * curr = e, prev = d, we find that e is bigger than what we want,
                     * so we move to lower floor's next element: b
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
    }
    
    public void insert(T key) {
        SkipListNode<T> prev = null;
        SkipListNode<T> newNode = null;
        int level = chooseLevel();
        SkipListNode<T> curr = root.getNext(level);
        for(int i = level; i >= 0; --i) {
            while(curr != null && curr.getKey() != null && curr.getKey().compareTo(key) < 0) {
                prev = curr;
                curr = curr.getNext(i);
            }
            if(curr != null && curr.getKey() != null && curr.getKey().equals(key)) {
                return;
            }
            // insert
            if(newNode == null) {
                newNode = new SkipListNode<>(key, level+1);
            }
            if(prev == null) {
                if(curr != root) {
                    newNode.setNext(curr, i);
                }
                root.setNext(newNode, i);
            } else {
                prev.setNext(newNode, i);
                newNode.setNext(curr, i);
            }
            /**
             * 1. this means moving to lower floor
             * 2. the first while-loop will help us find the right position
             * 3. curr is the first element larger than key
             * 4. prev is the first element smaller than key
             */
            if(i > 0) {
                if(prev == null) {
                    curr = root.getNext(i-1);
                } else {
                    curr = prev.getNext(i-1);
                }
            }
        }
    }
    
    public boolean remove(T key) {
        SkipListNode<T> prev = null;
        int level = chooseLevel();
        SkipListNode<T> curr = root.getNext(level);
        boolean found = false;
        for(int i = level; i >= 0; --i) {
            while(curr != null && curr.getKey() != null && curr.getKey().compareTo(key) < 0) {
                prev = curr;
                curr = curr.getNext(i);
            }
            if(curr != null && curr.getKey() != null && curr.getKey().equals(key)) {
                found = true;
                if(prev == null) {
                    root.setNext(curr.getNext(i), i);
                    curr.setNext(null, i);
                } else {
                    prev.setNext(curr.getNext(i), i);
                    curr.setNext(null, i);
                }
            }
            if(i > 0) {
                if(prev == null) {
                    curr = root.getNext(i-1);
                } else {
                    curr = prev.getNext(i-1);
                }
            }
        }
        return found;
    }
    
    public void visualize() {
        if(root != null && root.getNext(0) != null) {
            SkipListNode<T> curr = root.getNext(0);
            while(curr != null) {
                System.out.print(curr.getKey() + "(" + curr.getLength() + ")");
                System.out.print(" -> ");
                curr = curr.getNext(0);
            }
            System.out.println("NULL");
        }
    }
    
    public static void main(String[] args) {
        SkipList<Integer> skipList = new SkipList<>(4);
        skipList.insert(33);
        skipList.insert(2);
        skipList.insert(55);
        skipList.visualize();
        System.out.println(skipList.search(33) == null ? "Not Found" : "Bingo");
        System.out.println(skipList.search(45) == null ? "Not Found" : "Bingo");
        System.out.println(skipList.search(48) == null ? "Not Found" : "Bingo");
        skipList.remove(33);
        skipList.visualize();
        System.out.println(skipList.search(33) == null ? "Not Found" : "Bingo");
    }
}
