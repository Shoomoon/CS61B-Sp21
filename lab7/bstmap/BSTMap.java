package bstmap;

import java.lang.reflect.Array;
import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    /** Removes all of the mappings from this map. */
    private BSTNode<K, V> root;
    private int size = 0;

    public BSTMap() {
    }
    public void clear() {
        root = null;
        size = 0;
    }

    private BSTNode<K, V> _getNode(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        } else {
            int c = node.key().compareTo(key);
            if (c == 0) {
                return node;
            } else if (c < 0) {
                return _getNode(node.right(), key);
            } else {
                return _getNode(node.left(), key);
            }
        }
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls containsKey() with a null key");
        }
        return _getNode(root, key) != null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        BSTNode<K, V> target = _getNode(root, key);
        if (target == null) {
            return null;
        }
        return target.val();
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        if (value == null) {
            throw new IllegalArgumentException("calls put() with a null value");
        }
        BSTNode<K, V> node = _getNode(root, key);
        if (node != null) {
            node.setVal(value);
        } else {
            root = add(root, key, value);
        }
    }
    private BSTNode<K, V> add(BSTNode<K, V> node, K key, V value) {
        if (node == null) {
            size += 1;
            node = new BSTNode<K, V>(key, value);
        } else {
            int c = node.key().compareTo(key);
            if (c < 0) {
                node.setRight(add(node.right(), key, value));
            } else {
                node.setLeft(add(node.left(), key, value));
            }
        }
        return node;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<K>();
        BSTNode<K, V> cur = root;
        Stack<BSTNode<K, V>> sta = new Stack<BSTNode<K, V>>();
        while (cur != null | sta.size() != 0) {
            if (cur != null) {
                sta.push(cur);
                cur = cur.left();
            } else {
                cur = sta.pop();
                keys.add(cur.key());
                cur = cur.right();
            }
        }
        return keys;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

}
