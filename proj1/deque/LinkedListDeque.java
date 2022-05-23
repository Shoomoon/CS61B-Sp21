package deque;

public class LinkedListDeque<T> {
    private LinkedLiseNode<T> head;

    private Integer size;

    public LinkedListDeque() {
        head = new LinkedLiseNode<T>();
        head.setNext(head);
        head.setPre(head);
        size = 0;
    }

    public void addFirst(T item) {
        // Adds an item of type T to the front of the deque.
        // You can assume that item is never null.
        LinkedLiseNode<T> cur = new LinkedLiseNode<T>(item, head, head.next());
        head.next().setPre(cur);
        head.setNext(cur);
        size += 1;
    }

    public void addLast(T item) {
        // Adds an item of type T to the back of the deque.
        // You can assume that item is never null.
        LinkedLiseNode<T> cur = new LinkedLiseNode<T>(item, head.pre(), head);
        head.pre().setNext(cur);
        head.setPre(cur);
        size += 1;
    }

    public boolean isEmpty() {
        // Returns true if deque is empty, false otherwise.
        return size == 0;
    }

    public int size() {
        // Returns the number of items in the deque.
        return size;
    }

    public void printDeque() {
        // Prints the items in the deque from first to last, separated by a space.
        // Once all the items have been printed, print out a new line.
        LinkedLiseNode<T> cur = head;
        for (int i = 0; i < size; i++) {
            System.out.print(cur.next().val() + " ");
            cur = cur.next();
        }
    }

    public T removeFirst() {
        // Removes and returns the item at the front of the deque.
        // If no such item exists, returns null.
        if (isEmpty()) {
            return null;
        }
        LinkedLiseNode<T> removedNode = head.next();
        removedNode.next().setPre(head);
        head.setNext(removedNode.next());
        size -= 1;
        return removedNode.val();
    }

    public T removeLast() {
        // Removes and returns the item at the back of the deque.
        // If no such item exists, returns null.
        if (isEmpty()) {
            return null;
        }
        LinkedLiseNode<T> removedNode = head.pre();
        removedNode.pre().setNext(head);
        head.setPre(removedNode.pre());
        size -= 1;
        return removedNode.val();
    }

    public T get(int index) {
        // Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
        // If no such item exists, returns null. Must not alter the deque!
        if (index >= size || index < 0) {
            return null;
        }
        LinkedLiseNode<T> cur = head.next();
        for (int i = 0; i < index; i++) {
            cur = cur.next();
        }
        return cur.val();
    }

    private T _getRecursive(LinkedLiseNode<T> curNode, Integer distance) {
        // gets the item at index which is at the distance of current node curNode after
        if (distance == 0) {
            return curNode.val();
        } else {
            return _getRecursive(curNode.next(), distance - 1);
        }
    }
    public T getRecursive(int index) {
        // Same as get, but uses recursion.
        if (index >= size) {
            return null;
        }
        return _getRecursive(head.next(), index);
    }

//    public Iterator<Item> iterator() {
//        // The Deque objects we’ll make are iterable (i.e. Iterable<T>)
//        // so we must provide this method to return an iterator.
//    }

    public boolean equals(Object o) {
        // Returns whether or not the parameter o is equal to the Deque.
        // o is considered equal if it is a Deque and if it contains
        // the same contents (as goverened by the generic T’s equals method)
        // in the same order. (ADDED 2/12: You’ll need to use the instance of keywords for this)
        if (o instanceof LinkedListDeque && ((LinkedListDeque<?>) o).size() == size) {
            LinkedLiseNode<T> curNode = head.next();
            LinkedLiseNode<?> oNode = ((LinkedListDeque<?>) o).head.next();
            for (int i = 0; i < size; i++) {
                if (!(curNode.val().equals(oNode.val()))) {
                    return false;
                }
                curNode = curNode.next();
                oNode = oNode.next();
            }
            return true;
        }
        return false;
    }
}
