package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    public Room(int i, int j, int w, int h) {
        this.x = i;
        this.y = j;
        this.width = w;
        this.height = h;
    }
    public void drawRoom(TETile[][] tiles) {
        // draw floor
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                tiles[x + dx][y + dy] = Tileset.FLOOR;
            }
        }
        //draw horizontal walls
        for (int dx = -1; dx <= width; dx++) {
            if (tiles[x + dx][y - 1].equal(Tileset.NOTHING)) {
                tiles[x + dx][y - 1] = Tileset.WALL;
            }
            if (tiles[x + dx][y + height].equal(Tileset.NOTHING)) {
                tiles[x + dx][y + height] = Tileset.WALL;
            }
        }
        // draw vertical walls
        for (int dy = 0; dy < height; dy++) {
            if (tiles[x  - 1][y + dy].equal(Tileset.NOTHING)) {
                tiles[x  - 1][y + dy] = Tileset.WALL;
            }
            if (tiles[x + width][y + dy].equal(Tileset.NOTHING)) {
                tiles[x + width][y + dy] = Tileset.WALL;
            }
        }
    }
    public int distance(Room r) {
        int w = Math.min(x + width, r.x + r.width) - Math.max(x, r.x);
        int h = Math.min(y + height, r.y + r.height) - Math.max(y, r.y);
        // if overlap or adjacent to each other, just return -1
        if ((w >= 0 && h > 0) || (w > 0 && h == 0)) {
            return -1;
        }
        // if only overlap along x-axis
        if (w > 0) {
            return -h;
        }
        // if only overlap along y-axis
        if (h > 0)
            return -w;
        return -w - h;
    }

    public void connectWithRoom(TETile[][] tiles, Random random, Room r) {
        // since iterate from left to right, bottom to top when generate rooms,
        // so Room r always to the left of this room
        // if r is on the top side of this room, connect this room.left side with r.bottom side by |_ shape hallway
        // if r is on the bottom side of this room, connect this room.left side with r.top side by |- shape hallway
        // otherwise connect this room.
        int wr = Math.min(x + width, r.x + r.width);
        int wl = Math.max(x, r.x);
        int ht = Math.min(y + height, r.y + r.height);
        int hb = Math.max(y, r.y);
        // if this 2 room have overlap or adjacent to each other, don't need to use hallway to connect with each other
        if (wr - wl >= 0 && ht - hb > 0 || wr - wl > 0 && ht - hb == 0) {
//            System.out.print("These two rooms have overlap, don't need to build up hallway to connect with each other.");
            return;
        }
        // if only have overlap alone x-axis, then use a vertical hallway to connect with each other
        if (wr > wl) {
            int i = RandomUtils.uniform(random, wl, wr);
            int w = Math.min(Engine.WIDTH - i - 1, RandomUtils.uniform(random, Engine.MINHALLWAYWIDTH, Engine.MAXHALLWAYWIDTH + 1));
            Room hallway = new Room(i, ht, w, hb - ht);
            hallway.drawRoom(tiles);
            return;
        }
        // if only have overlap alone y-axis, then use a horizontal hallway to connect with each other
        if (ht > hb) {
            int j = RandomUtils.uniform(random, hb, ht);
            int h = Math.min(Engine.HEIGHT - j - 1, RandomUtils.uniform(random, Engine.MINHALLWAYWIDTH, Engine.MAXHALLWAYWIDTH + 1));
            Room hallway = new Room(wr, j, wl - wr, h);
            hallway.drawRoom(tiles);
            return;
        }
        int hallwayWidth = RandomUtils.uniform(random, Engine.MINHALLWAYWIDTH, Engine.MAXHALLWAYWIDTH + 1);
        int xHorHallway = RandomUtils.uniform(random, r.x, r.x + r.width);
        int yHorHallway = RandomUtils.uniform(random, y, y + height);
        int heightHorHallway = x - xHorHallway;
        int yVerHallway = Math.min(r.y + r.height, yHorHallway);
        int heightVerHallway = Math.max(yHorHallway - yVerHallway, r.y - yHorHallway);
        Room hallwayHorizontal = new Room(xHorHallway, yHorHallway, heightHorHallway, Math.min(Engine.HEIGHT - yHorHallway - 1, hallwayWidth));
        Room hallwayVertical = new Room(xHorHallway, yVerHallway, Math.min(Engine.WIDTH - xHorHallway - 1, hallwayWidth), heightVerHallway);
        hallwayHorizontal.drawRoom(tiles);
        hallwayVertical.drawRoom(tiles);
    }
}
