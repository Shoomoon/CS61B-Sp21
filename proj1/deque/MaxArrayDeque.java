package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        if (super.size() == 0) {
            return null;
        }
        T mx_element = (T) super.get(0);
        for (int i = 1; i < super.size(); i++) {
            if (comparator.compare(mx_element, super.get(i)) < 0) {
                mx_element = super.get(i);
            }
        }
        return mx_element;
    }

    public T max(Comparator<T> c) {
        if (super.size() == 0) {
            return null;
        }
        T mx_element = (T) super.get(0);
        for (int i = 1; i < super.size(); i++) {
            if (c.compare(mx_element, super.get(i)) < 0) {
                mx_element = super.get(i);
            }
        }
        return mx_element;
    }
}
