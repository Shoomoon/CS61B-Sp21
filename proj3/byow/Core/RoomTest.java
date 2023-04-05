package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {
    static TERenderer ter = new TERenderer();
    static int width = 80;
    static int height = 60;
    static int seed = 12671;
    static Random random = new Random(seed);
    static TETile[][] tiles = new TETile[width][height];
    @Before
    public void before() {
        ter.initialize(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }
    @Test
    public void drawRoomTest() {
        Room room = new Room(2, 2, 4, 4);
        room.createRoom(tiles);
        ter.renderFrame(tiles);

        Room room1 = new Room(3, 4, 5, 8);
        room1.createRoom(tiles);
        ter.renderFrame(tiles);

        Room room2 = new Room(3, 18, 4, 4);
        room2.createRoom(tiles);
        ter.renderFrame(tiles);

        room2.connectWithRoom(tiles, random, room1);
        ter.renderFrame(tiles);

        Room room3 = new Room(30, 26, 8, 8);
        room3.createRoom(tiles);
        room3.connectWithRoom(tiles, random, room2);
        ter.renderFrame(tiles);
    }

    @Test
    public void distanceTest() {
        Room r1 = new Room(2, 2, 2, 2);

        Room r2 = new Room(4, 4, 2, 2);
        assertEquals(r1.distance(r2), 2);

        Room r3 = new Room(4, 4, 3,3 );
        assertEquals(r1.distance(r3), 2);

        Room r4 = new Room(4, 3, 2, 2);
        assertEquals(r1.distance(r4), -1);

        Room r5 = new Room(3, 4, 2, 2);
        assertEquals(r1.distance(r5), -1);

        Room r6 = new Room(5, 4, 2, 2);
        assertEquals(r1.distance(r6), 3);
    }
}
