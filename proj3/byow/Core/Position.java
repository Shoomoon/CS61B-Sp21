package byow.Core;

import java.io.Serializable;

public class Position implements Serializable {
    int x;
    int y;
    public Position(int i, int j) {
        x = i;
        y = j;
    }
    public boolean equal(Position p) {
        return x == p.x && y == p.y;
    }
}
