package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class parameters {
    public static void main(String[] args) {
        TERenderer tr = new TERenderer();
        tr.initialize(2, 2);
        TETile[][] w = new TETile[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                w[i][j] = Tileset.FLOWER;
            }
        }
        changeTile(0, 0, Tileset.NOTHING, w);
        tr.renderFrame(w);
    }
    private static void changeTile(int i, int j, TETile tile, TETile[][] w) {
        w[i][j] = tile;
    }
}
