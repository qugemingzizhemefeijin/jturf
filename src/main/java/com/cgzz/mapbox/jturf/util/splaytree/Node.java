package com.cgzz.mapbox.jturf.util.splaytree;

public class Node<K extends Comparable<K>, V> {

    K key;
    V data;
    Node<K, V> left;
    Node<K, V> right;
    Node<K, V> next;

    public Node(K key, V data) {
        this.key = key;
        this.data = data;
    }

    public K getKey() {
        return key;
    }

    public V getData() {
        return data;
    }

    public Node<K, V> getLeft() {
        return left;
    }

    public Node<K, V> getRight() {
        return right;
    }

    public Node<K, V> getNext() {
        return next;
    }
}
