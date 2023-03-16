package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    // number of Hexagons on each side of HexWorld
    private static final int SIZE = 3;
    // number of Tiles on each side of Hexagon
    private static int hexagonSize = 3;
    // Random generator
    private static Random RANDOM;

    // input is the size of Hexagon, whose void value is 3
    public static void main(String[] args) {
        // Validate input arguments and capture hexgonSize and randomSeed
        // input should be "N/n" + hexagonSize + "S/s" + randomSeed
        int[] nums = captureInput(args);
        if (nums == null) {
            throw new IllegalArgumentException("Illegal input!\n" +
                    "Please input the size of hexagons and seed of randomness as N/n + hexagonSize + S/s + randomSeed.");
        }
        // set hexagon size and random generator
        setHexagonSize(nums[0]);
        setRandom(nums[1]);

        // random generate hexagons
        Hexagon[][] world = randomGenerateHexagonWorld();

        // initialize the tile rendering engine
        TERenderer ter = new TERenderer();
        initialize(ter);

        // draws the world to the screen
        renderHexWorld(ter, world);
    }

    //capture the size of Hexagon and the random seed
    public static int[] captureInput(String[] args) {
        if (args == null || args.length != 1) {
            return null;
        }
        String hexagonSizePattern = "^[Nn]([1-9]\\d*)[Ss]([1-9]\\d*)$";
        Pattern pattern = Pattern.compile(hexagonSizePattern);
        Matcher match = pattern.matcher(args[0]);
        if (!match.find()) {
            return null;
        }
        return new int[]{Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2))};
    }

    // set the size of Hexagon in this HexWorld
    private static void setHexagonSize(int num) {
        hexagonSize = num;
    }

    // set the random generator of this HexWorld
    private static void setRandom(int seed) {
        RANDOM = new Random(seed);
    }

    // initialize the Tile engine
    private static void initialize(TERenderer ter) {
        // initialize the tile rendering engine with a window of size width x height
        // width and height depends on the size of HexWorld and the size of Hexagon
        int canvasWidth = getCanvasWidth();
        int canvasHeight = getCanvasHeight();
        ter.initialize(canvasWidth, canvasHeight);
    }

    // calculate the width of canvas of this HexWorld
    public static int getCanvasWidth() {
        return getHexagonWorldWidth() * getColumnDistance() + (hexagonSize - 1);
    }

    // calculate the height of canvas of this HexWorld
    public static int getCanvasHeight() {
        return getHexagonWorldHeight() * getRowDistance();
    }

    // calculate the number of columns of Hexagons
    public static int getHexagonWorldWidth() {
        return 2 * SIZE - 1;
    }

    // calculate the number of rows of Hexagons
    public static int getHexagonWorldHeight() {
        return 2 * SIZE - 1;
    }

    // randomly generate the Hexagons of this HexWorld
    private static Hexagon[][] randomGenerateHexagonWorld() {
        int width = getHexagonWorldWidth();
        int height = getHexagonWorldHeight();
        Hexagon[][] w = new Hexagon[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (hexWorldCoordinateValidation(x, y)) {
                    int hexagonNum = RANDOM.nextInt(Hexagon.PATTERNS);
                    w[x][y] = new Hexagon(hexagonSize, hexagonNum);
                } else {
                    w[x][y] = null;
                }
            }
        }
        return w;
    }

    //render the HexWorld
    public static void renderHexWorld(TERenderer ter, Hexagon[][] world) {
        // generate tiles by Hexagon[][] world and initialized as Tile.NOTHING
        TETile[][] TETileWorld = new TETile[getCanvasWidth()][getCanvasHeight()];
        for (int i = 0; i < TETileWorld.length; i++) {
            for (int j = 0; j < TETileWorld[0].length; j++) {
                TETileWorld[i][j] = Tileset.NOTHING;
            }
        }
        // if the position has a Hexagon, then draw the Hexagon onto TETileWorld
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j] != null) {
                    int x = getHexagonX(i, j);
                    int y = getHexagonY(i, j);
                    world[i][j].drawHexagon(TETileWorld, x, y);
                }
            }
        }
        ter.renderFrame(TETileWorld);
    }

    public static int getRowDistance() {
        return 2 * hexagonSize;
    }
    public static int getColumnDistance() {
        return 2 * hexagonSize - 1;
    }
    private static boolean hexWorldCoordinateValidation(int i, int j) {
        // since all columns of hexagons are 0-indexed
        // the height of middle column is getHexagonWorldHeight() = 2 * SIZE - 1
        // then the height of i-th column will decrease by 1
        // as long as the distance to middle column increase by 1
        // Hence, the height of i-th column is getHexagonWorldHeight() - abs(i - (hexagonSize - 1))
        return i >= 0 && i < getHexagonWorldWidth() && j >= 0 && j < getHexagonWorldHeight() - Math.abs(i - (SIZE - 1));
    }

    // return the Y coordinate of the bottom left corner Tile of the Hexagon
    public static int getHexagonY(int i, int j) {
        // assume that all columns of hexagons are 0-indexed
        // then the middle column's index is (hexagonSize - 1)
        // y-coordinate will increase by hexagonSize as long as the distance increase by 1
        return Math.abs(i - (SIZE - 1)) * hexagonSize + j * getRowDistance();
    }

    // return the X coordinate of the bottom left corner Tile of the Hexagon
    public static int getHexagonX(int i, int j) {
        // bias is hexagonSize - 1, and the distance between adjacent columns is 2 * hexagonSize - 1
        return hexagonSize - 1 + i * getColumnDistance();
    }
    public static int getHexagonSize() {
        return hexagonSize;
    }
}
