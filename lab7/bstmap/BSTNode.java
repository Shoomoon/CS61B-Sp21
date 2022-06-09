package bstmap;

public class BSTNode<K extends Comparable<K>, V> {
    private K key;
    private V val;
    private BSTNode<K, V> left;
    private BSTNode<K, V> right;

    public BSTNode(K key, V val) {
        this.key = key;
        this.val = val;
        this.left = null;
        this.right = null;
    }
    public void setVal(V val) {
        this.val = val;
    }
    public K key() {
        return this.key;
    }
    public V val() {
        return this.val;
    }
    public BSTNode<K, V> left() {
        return this.left;
    }
    public BSTNode<K, V> right() {
        return this.right;
    }
    public void setLeft(BSTNode<K, V> left) {
        this.left = left;
    }
    public void setRight(BSTNode<K, V> right) {
        this.right = right;
    }
}
