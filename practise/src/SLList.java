public class SLList {
    private static class IntNode {
        public int item;
        public IntNode next;
        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }

    private IntNode sentinel;
    private int size;
    public SLList(int x) {
        sentinel = new IntNode(0, new IntNode(x, null));
        size = 1;
    }
    public SLList() {
        sentinel = new IntNode(0, null);
        size = 0;
    }

    public void addFist(int x) {
        sentinel.next = new IntNode(x, sentinel.next);
        size += 1;
    }
    public IntNode getFirst() {
        return sentinel.next;
    }

    public void addLast(int x) {
        IntNode cur = sentinel;
        while (cur.next != null) {
            cur = cur.next;
        }
        cur.next = new IntNode(x, null);
        size += 1;
    }

    public int size() {
        return size;
    }

}
