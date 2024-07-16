package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class drawHexagonTest {
    @Test
    public void drawHexagonTest() {
        final int WIDTH = 60;
        final int HEIGHT = 30;
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        Hexagon h0 = new Hexagon(3, 3);
        h0.drawHexagon(world, h0.getHexagonSize(), 0);

        Hexagon h1 = new Hexagon(4, 0);
        h1.drawHexagon(world, h1.getHexagonSize(), 2 * h0.getHexagonSize());

        Hexagon h2 = new Hexagon(3, 1);
        h2.drawHexagon(world, 4 * h0.getHexagonSize() - 2, 0);

        // draws the world to the screen
        ter.renderFrame(world);
        StdDraw.show();
    }
}
