package byow.Core;

import java.io.Serializable;

public class Position implements Serializable {
    int x;
    int y;
    int val;
    public Position(int i, int j) {
        x = i;
        y = j;
        val = (x << 8) + y;
    }
    public Position(Position p, int dx, int dy) {
        x = p.x + dx;
        y = p.y + dy;
        val = (x << 8) + y;
    }
    public boolean equal(Position p) {
        return val == p.val;
    }
}
