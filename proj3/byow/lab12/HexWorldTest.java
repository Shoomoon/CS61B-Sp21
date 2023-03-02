package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class HexWorldTest {
    @Test
    public void matchTest() {
        int worldSize = 4;
        int seed = 24512;
        String s = "w" + worldSize + "s" + seed;
        String pattern = "^[Ww]([1-9]\\d*)[Ss]([1-9]\\d*)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        m.find();
        assertEquals(Integer.toString(worldSize), m.group(1));
        assertEquals(Integer.toString(seed), m.group(2));
    }
    @Test
    public void captureValidateTest() {
        int worldSize = 4;
        int seed = 24512;
        String s = "n" + worldSize + "s" + seed;
        String s1 = "N" + worldSize + "s" + seed;
        String s2 = "N" + worldSize + "S" + seed;
        String s3 = "n" + worldSize + "S" + seed;
        String s4 = "a" + worldSize + "S" + seed;
        String[] s5 = null;
        String[] s6 = new String[]{"N", "5", "S", "451"};
        assertArrayEquals(new int[]{worldSize, seed}, HexWorld.captureInput(new String[]{s}));
        assertArrayEquals(new int[]{worldSize, seed}, HexWorld.captureInput(new String[]{s1}));
        assertArrayEquals(new int[]{worldSize, seed}, HexWorld.captureInput(new String[]{s2}));
        assertArrayEquals(new int[]{worldSize, seed}, HexWorld.captureInput(new String[]{s3}));
        assertArrayEquals(null, HexWorld.captureInput(new String[]{s4}));
        assertArrayEquals(null, HexWorld.captureInput(s5));
        assertArrayEquals(null, HexWorld.captureInput(s6));
    }

    @Test
    public void hexagonWorldSizeTest() {
        if (HexWorld.getHexagonSize() == 3) {
            // hexagon size = 3
            assertEquals(5, HexWorld.getHexagonWorldHeight());
            assertEquals(5, HexWorld.getHexagonWorldWidth());
            assertEquals(27, HexWorld.getCanvasWidth());
            assertEquals(30, HexWorld.getCanvasHeight());
        } else if (HexWorld.getHexagonSize() == 4) {
            // hexagon size = 4
            assertEquals(5, HexWorld.getHexagonWorldHeight());
            assertEquals(5, HexWorld.getHexagonWorldWidth());
            assertEquals(38, HexWorld.getCanvasWidth());
            assertEquals(40, HexWorld.getCanvasHeight());
        } else {
            System.out.print("No hexagonWorldSizeTest cases tested.");
        }
    }
    @Test
    public void xyCoordinateTest() {
        // hexagon size = 3
        if (HexWorld.getHexagonSize() == 3) {
            assertEquals(2, HexWorld.getHexagonX(0, 0));
            assertEquals(6, HexWorld.getHexagonY(0, 0));
            assertEquals(7, HexWorld.getHexagonX(1, 0));
            assertEquals(3, HexWorld.getHexagonY(1, 0));
            assertEquals(7, HexWorld.getHexagonX(1, 1));
            assertEquals(9, HexWorld.getHexagonY(1, 1));
        } else if (HexWorld.getHexagonSize() == 4) {
            // hexagon size = 4
            assertEquals(3, HexWorld.getHexagonX(0, 0));
            assertEquals(12, HexWorld.getHexagonY(0, 0));
            assertEquals(10, HexWorld.getHexagonX(1, 0));
            assertEquals(8, HexWorld.getHexagonY(1, 0));
            assertEquals(10, HexWorld.getHexagonX(1, 1));
            assertEquals(16, HexWorld.getHexagonY(1, 1));
        } else {
            System.out.print("No xyCoordinateTest cases tested.");
        }
    }
}
