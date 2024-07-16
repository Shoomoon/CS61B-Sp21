public class IntList {
    public int first;
    public IntList rest;
    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    public static IntList incrList(IntList L, int x) {
        // Returns an IntList identical to L, but with all values incremented by x.
        // Values in L cannot change
        if (L == null) {
            return null;
        }
        return new IntList(L.first + x, incrList(L.rest, x));
    }

    public static IntList dincrList(IntList L, int x) {
        // Returns an IntList identical to L, but with all values incremented by x.
        // Not allowed to use ‘new’ (to save memory)
        if (L != null) {
            L.first -= x;
            L.rest = dincrList(L.rest, x);
        }
        return L;
    }



}
