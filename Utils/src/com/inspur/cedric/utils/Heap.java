package com.inspur.cedric.utils;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public class Heap <T extends Comparable<? super T>> {

    private static final int DEFAULT_SIZE = 1 << 4;
    
    public static enum Type { MAX, MIN }
    
    private T[] data;
    private int heapSize;
    private Type type;
    private Class<T> classType;
    private boolean isFull;
    
    public Heap(Type type, Class<T> classType) {
        this(null, type, classType);
    }
    
    @SuppressWarnings("unchecked")
    public Heap(T[] input, Type type, Class<T> classType) {
        int arrayLength = DEFAULT_SIZE;
        if(input == null) {
            this.heapSize = 0;
        } else {
            this.heapSize = input.length;
            arrayLength = getNearestPowerOfTwo(input.length);
        }
        this.type = type;
        this.classType = classType;
        this.data = (T[])Array.newInstance(this.classType, arrayLength);
        if(this.heapSize != 0) {
            for(int i = 0; i < this.heapSize; ++i) {
                this.data[i] = input[i];
            }
            build();
        }
    }
    
    private int getNearestPowerOfTwo(int length) {
        int i = 0;
        while((2 << i) < length) {
            ++i;
        }
        return 2 << i;
    }
    
    private void build() {
        if(heapSize <= 1) { return; }
        for(int i = getParent(heapSize-1); i >= 0; --i) {
            siftDown(i);
        }
    }
    
    private void siftUp(int start) {
        if(start <= 0) {
            return;
        }
        if(type == Type.MIN) {
            while(data[getParent(start)] == null || data[start].compareTo(data[getParent(start)]) < 0) {
                swap(start, getParent(start));
                start = getParent(start);
                if(start == 0) {
                    break;
                }
            }
        } else {
            while(data[getParent(start)] == null || data[start].compareTo(data[getParent(start)]) > 0) {
                swap(start, getParent(start));
                start = getParent(start);
                if(start == 0) {
                    break;
                }
            }
        }
    }
    
    private void siftDown(int start) {
        while(getLeft(start) < heapSize) {
            int target = 0;
            if(getRight(start) >= heapSize) {
                if(type == Type.MIN) {
                    if(data[start].compareTo(data[getLeft(start)]) > 0) {
                        target = getLeft(start);
                        swap(start, getLeft(start));
                    }
                } else {
                    if(data[start].compareTo(data[getLeft(start)]) < 0) {
                        target = getLeft(start);
                        swap(start, getLeft(start));
                    }
                }
            } else {
                if(type == Type.MIN) {
                    target = data[getLeft(start)].compareTo(data[getRight(start)]) < 0 ?
                            getLeft(start) : getRight(start);
                    if(data[start].compareTo(data[target]) > 0) {
                        swap(start, target);
                    }
                    
                } else {
                    target = data[getLeft(start)].compareTo(data[getRight(start)]) > 0 ?
                            getLeft(start) : getRight(start);
                    if(data[start].compareTo(data[target]) < 0) {
                        swap(start, target);
                    }
                }
            }
            if(target == 0) {
                break;
            }
            // search from target
            start = target;
        }
    }
    
    @SuppressWarnings("unchecked")
    public void insert(T t) {
        if(isFull) {
            int newLength = getNearestPowerOfTwo(heapSize) << 1;
            T[] newArray = (T[])Array.newInstance(classType, newLength);
            System.arraycopy(data, 0, newArray, 0, heapSize);
            // for sake of GC
            for(int i = 0; i < heapSize; ++i) {
                data[i] = null;
            }
            data = newArray;
            isFull = false;
        }
        data[heapSize] = t;
        siftUp(heapSize);
        heapSize++;
        if(heapSize == data.length-1) {
            isFull = true;
        }
    }
    
    @SuppressWarnings("unchecked")
    public void delete(T t) {
        if(heapSize == 0) {
            throw new NoSuchElementException("Empty heap!");
        }
        boolean found = false;
        for(int i = 0; i < heapSize; ++i) {
            // NOTE: here we use "equals"
            if(data[i].equals(t)) {
                found = true;
                swap(i, getCapacity()-1);
                swap(i, heapSize-1);
                // NOTE: there are only elements of heapSize-1
                for(int j = i; j < heapSize-1; ++j) {
                    siftUp(j);
                }
                data[getCapacity()-1] = null;
                --heapSize;
                // shrink
                if(heapSize < getCapacity() >> 1) {
                    int newLength = getNearestPowerOfTwo(heapSize);
                    T[] newArray = (T[])Array.newInstance(classType, newLength);
                    System.arraycopy(data, 0, newArray, 0, heapSize);
                    // for sake of GC
                    for(int k = 0; k < heapSize; ++k) {
                        data[k] = null;
                    }
                    data = newArray;
                }
            }
        }
        if(!found) {
            throw new NoSuchElementException("No such an element to be deleted!");
        }
    }
    
    public boolean check() {
        // 1. indexes in range
        // 2. satisfy the rule depending on type
        if(type == Type.MIN) {
            for(int i = 0; i <= getParent(heapSize-1); ++i) {
                if((getLeft(i) < heapSize && data[getLeft(i)].compareTo(data[i]) < 0)
                        || (getRight(i) < heapSize && data[getRight(i)].compareTo(data[i]) < 0)) {
                    return false;
                }
            }
        } else {
            for(int i = 0; i <= getParent(heapSize-1); ++i) {
                if((getLeft(i) < heapSize && data[getLeft(i)].compareTo(data[i]) > 0)
                        || (getRight(i) < heapSize && data[getRight(i)].compareTo(data[i]) > 0)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public T peek() {
        if(heapSize != 0) {
            return data[0];
        } else {
            return null;
        }
    }
    
    public int getSize() {
        return heapSize;
    }
    
    public int getCapacity() {
        return data.length;
    }
    
    private void swap(int i, int j) {
        T tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }
    
    private int getParent(int i) {
        if(i == 0) return -1;
        return (i-1) >> 1;
    }
    
    private int getLeft(int i) {
        return 2*i+1;
    }
    
    private int getRight(int i) {
        return 2*i+2;
    }
}
