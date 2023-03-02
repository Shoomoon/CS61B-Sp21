package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Hexagon {
    private final TETile pattern;
    private final int hexagonSize;
    public static final int PATTERNS = 5;
    public Hexagon(int size, TETile TETilePattern) {
        this.hexagonSize = size;
        this.pattern = TETilePattern;
    }
    public Hexagon(int size, int patternNum) {
        this.hexagonSize = size;
        switch (patternNum) {
            case 0 -> this.pattern = Tileset.GRASS;
            case 1 -> this.pattern = Tileset.FLOWER;
            case 2 -> this.pattern = Tileset.SAND;
            case 3 -> this.pattern = Tileset.MOUNTAIN;
            case 4 -> this.pattern = Tileset.TREE;
            default -> throw new IllegalArgumentException("Hexagon's pattern number must be 0 to 4.");
        }
    }
    public int getHexagonSize(){
        return hexagonSize;
    }
    public void drawHexagon(TETile[][] teTileWorld, int x0, int y0) {
        int hexagonHeight = 2 * getHexagonSize();
        for (int y = y0; y < y0 + hexagonHeight; y++) {
            // xLeft is the left coordinate, which is the maximum number of 3 line: y=-x+x0+y0, x=x0-s+1, and y=x-(x0-s+1)+y0+s
            int xLeft = Math.max(y - y0 + x0 - 2 * getHexagonSize() + 1, Math.max(y0 + x0 - y, x0 - getHexagonSize() + 1));
            // xWidth is the width of x-th row, which is the minimum number of 2 function: w=2*(y-y0)+s, w=-2*(y-(y0+2s-1))+s
            int xWidth = Math.min(2 * (y - y0) + getHexagonSize(), -2 * (y - y0) + 5 * getHexagonSize() - 2);
            for (int x = xLeft; x < xLeft + xWidth; x++) {
                teTileWorld[x][y] = this.pattern;
            }
        }
    }
}
