package com.inspur.cedric.utils;

public class BTreeNodeData <K extends Comparable<? super K>, V>
{
    private final K key;
    private final V value;
    public BTreeNodeData(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() {
        return key;
    }
    public V getValue() {
        return value;
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append(key == null ? "null" : key.toString());
        stringBuilder.append(":");
        stringBuilder.append(value == null ? "null" : value.toString());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
