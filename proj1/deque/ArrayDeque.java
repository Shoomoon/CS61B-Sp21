package deque;

public class ArrayDeque<T> implements Deque<T>{
    private Integer size;
    private T[] items;
    private Integer head;
    private final Integer MIN_SIZE = 8;
    public ArrayDeque() {
        size = 0;
        head = 0;
        items = (T[]) new Object[MIN_SIZE];
    }

    private void reSize(Integer capacity) {
        // resize items to the target capacity,
        // and copy items to new array starting from copyStart index.
        T[] tmp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            tmp[i] = get(i);
        }
        items = tmp;
        head = 0;
    }

    @Override
    public void addFirst(T item) {
        // Adds an item of type T to the front of the deque.
        // You can assume that item is never null.
        if (size >= items.length) {
            reSize(size * 2);
        }
        head = (head - 1 + items.length) % items.length;
        items[head] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        // Adds an item of type T to the back of the deque.
        // You can assume that item is never null.
        if (size >= items.length) {
            reSize(size * 2);
        }
        items[(head + size) % items.length] = item;
        size += 1;
    }

    @Override
    public boolean isEmpty() {
        // Returns true if deque is empty, false otherwise.
        return size == 0;
    }

    @Override
    public int size() {
        // Returns the number of items in the deque.
        return size;
    }

    @Override
    public void printDeque() {
        // Prints the items in the deque from first to last, separated by a space.
        // Once all the items have been printed, print out a new line.
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
    }

    @Override
    public T removeFirst() {
        // Removes and returns the item at the front of the deque.
        // If no such item exists, returns null.
        if (isEmpty()) {
            return null;
        }
        T first = get(0);
        items[head] = null;
        head = (head + 1) % items.length;
        size -= 1;
        if (size < items.length / 4 && items.length >= 4 * MIN_SIZE) {
            reSize(items.length / 4);
        }
        return first;
    }

    @Override
    public T removeLast() {
        // Removes and returns the item at the back of the deque.
        // If no such item exists, returns null.
        if (isEmpty()) {
            return null;
        }
        int last_idx = (head + size - 1) % items.length;
        T last = items[last_idx];
        items[last_idx] = null;
        size -= 1;
        if (size < items.length / 4 && items.length > 4) {
            reSize(items.length / 4);
        }
        return last;
    }

    @Override
    public T get(int index) {
        // Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
        // If no such item exists, returns null. Must not alter the deque!
        if (index >= size || index < 0) {
            return null;
        }
        return items[(head + index) % items.length];
    }

//    public Iterator<Item> iterator() {
//        // The Deque objects we’ll make are iterable (i.e. Iterable<T>)
//        // so we must provide this method to return an iterator.
//    }

    @Override
    public boolean equals(Object o) {
        // Returns whether or not the parameter o is equal to the Deque.
        // o is considered equal if it is a Deque and if it contains
        // the same contents (as goverened by the generic T’s equals method)
        // in the same order. (ADDED 2/12: You’ll need to use the instance of keywords for this)
        if (o instanceof ArrayDeque && ((ArrayDeque<?>) o).size() == size) {
            for (int i = 0; i < size; i++) {
                if (!(this.get(i).equals(((ArrayDeque<?>) o).get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
