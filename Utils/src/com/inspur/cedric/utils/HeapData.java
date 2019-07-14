package com.inspur.cedric.utils;

public class HeapData <T extends Comparable<? super T>>
{
    private T data;

    public HeapData() {
        this(null);
    }

    public HeapData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        if(data != null) {
            return data.toString();
        } else {
            return "null";
        }
    }
}
