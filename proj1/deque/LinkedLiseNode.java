package deque;

public class LinkedLiseNode<T>{
    private T val;
    private LinkedLiseNode<T> pre;
    private LinkedLiseNode<T> next;

    public LinkedLiseNode() {
        val = null;
        pre = null;
        next = null;
    }
    public LinkedLiseNode(T val) {
        this.val = val;
        pre = null;
        next = null;
    }
    public LinkedLiseNode(T val, LinkedLiseNode<T> pre, LinkedLiseNode<T> next) {
        this.val = val;
        this.pre = pre;
        this.next = next;
    }
    public LinkedLiseNode<T> pre() {
        return pre;
    }

    public LinkedLiseNode<T> next() {
        return next;
    }

    public T val() {
        return val;
    }

    public void setPre(LinkedLiseNode<T> pre) {
        this.pre = pre;
    }

    public void setNext(LinkedLiseNode<T> next) {
        this.next = next;
    }

    public void setVal(T val) {
        this.val = val;
    }
}
