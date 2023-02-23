import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

public class TestStdDraw {
    public static void main(String[] args) {
//        StdDraw.setPenRadius(0.05);
////        StdDraw.setPenColor(StdDraw.BLUE);
////        StdDraw.point(0.5, 0.5);
////        StdDraw.setPenColor(StdDraw.MAGENTA);
////        StdDraw.line(0.2, 0.2, 0.8, 0.3);
////        StdDraw.setPenRadius(0.02);
////        StdDraw.square(0.2, 0.2, 0.1);
////        StdDraw.ellipse(0.2, 0.2, 0.1, 0.2);
////        StdDraw.setPenColor(StdDraw.GREEN);
////        StdDraw.circle(0.2, 0.2, 0.2);
////        StdDraw.rectangle(0.2, 0.2, 0.1, 0.2);
////        double[] x0 = { 0.1, 0.2, 0.3, 0.2 };
////        double[] y0 = { 0.2, 0.3, 0.2, 0.1 };
////        StdDraw.filledPolygon(x0, y0);
////        StdDraw.setTitle("imageTest");
//        StdDraw.clear(); // clear the canvas to white
//        StdDraw.text(0.1, 0.1, "#");
//        StdDraw.show();
//        StdDraw.enableDoubleBuffering();

//        for (double t = 0.0; true; t += 0.02) {
//            double x = Math.sin(t);
//            double y = Math.cos(t);
//            StdDraw.clear();
//            StdDraw.filledCircle(x, y, 0.05);
//            StdDraw.filledCircle(-x, -y, 0.05);
//            StdDraw.text(0, 0, "#");
//            StdDraw.show();
//            StdDraw.pause(20);
//        }

//
//        int tileSize = 16;
//        int width = 60;
//        int height = 30;
//
////        StdDraw.setCanvasSize(width * tileSize, height * tileSize);
//        StdDraw.setXscale(0, width);
//        StdDraw.setYscale(0, height);
//        StdDraw.setPenColor(StdDraw.BLACK);
//        StdDraw.clear();
//        StdDraw.text(20 + 0.5, 10 + 0.5, "#");
//        StdDraw.show();
////
        int WIDTH = 60;
        int HEIGHT = 30;
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        world[0][0] = Tileset.FLOWER;

//
//        // fills in a block 14 tiles wide by 4 tiles tall
//        for (int x = 20; x < 35; x += 1) {
//            for (int y = 5; y < 10; y += 1) {
//                world[x][y] = Tileset.WALL;
//            }
//        }
        // draws the world to the screen
        ter.renderFrame(world);
    }
}
  