package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class Room implements Serializable {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private Position light;
    private final boolean hasLight;
    private static final int LIGHTRANGE = 6;
    public static final Color LIGHTCOLOR = new Color(62, 78, 240);
    public static final double BASE = 0.78;
    private boolean enableLight = false;
    // generate a room with light
    public Room(int i, int j, int w, int h, Position light) {
        this.x = i;
        this.y = j;
        this.width = w;
        this.height = h;
        this.light = light;
        this.hasLight = true;
    }
    // generate a room without light, for example hallways
    public Room(int i, int j, int w, int h) {
        this.x = i;
        this.y = j;
        this.width = w;
        this.height = h;
        this.hasLight = false;
    }
    public void createRoom(TETile[][] tiles) {
        // draw floor, and don't rewrite light
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                if (tiles[x + dx][y + dy].equal(Tileset.NOTHING) || tiles[x + dx][y + dy].equal(Tileset.WALL)) {
                    tiles[x + dx][y + dy] = Tileset.FLOOR;
                }
            }
        }
        // draw light
        if (hasLight) {
            tiles[light.x][light.y] = Tileset.LIGHT;
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
        toggleLight(tiles);
    }
    public void toggleLight(TETile[][] tiles) {
        if (!hasLight) {
            return;
        }
        this.enableLight = !this.enableLight;
        int l = Math.max(x, light.x - LIGHTRANGE);
        int r = Math.min(x + width, light.x + LIGHTRANGE + 1);
        int b = Math.max(y, light.y - LIGHTRANGE);
        int t = Math.min(y + height, light.y + LIGHTRANGE + 1);
        for (int i = l; i < r; i++) {
            for (int j = b; j < t; j++) {
                int d = Math.max(Math.abs(light.x - i), Math.abs(light.y - j));
                // don't change back color if it is not floor or light
                if (d != 0 && !tiles[i][j].equal(Tileset.FLOOR) && !tiles[i][j].equal(Tileset.AVATAR)) {
                    continue;
                }
                if (d == 0) {
                    tiles[i][j] = Tileset.LIGHT;
                } else {
                    tiles[i][j] = Tileset.FLOOR;
                }
                if (enableLight) {
                    double factor = Math.pow(BASE, d);
                    int redVal = (int) (LIGHTCOLOR.getRed() * factor);
                    int greenVal =  (int) (LIGHTCOLOR.getGreen() * factor);
                    int blueVal =  (int) (LIGHTCOLOR.getBlue() * factor);
                    tiles[i][j] = tiles[i][j].changeBackgroundColor(new Color(redVal, greenVal, blueVal));
                } else {
                    tiles[i][j] = tiles[i][j].changeBackgroundColor(Engine.BACKGROUND);
                }
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
            hallway.createRoom(tiles);
            return;
        }
        // if only have overlap alone y-axis, then use a horizontal hallway to connect with each other
        if (ht > hb) {
            int j = RandomUtils.uniform(random, hb, ht);
            int h = Math.min(Engine.HEIGHT - j - 1, RandomUtils.uniform(random, Engine.MINHALLWAYWIDTH, Engine.MAXHALLWAYWIDTH + 1));
            Room hallway = new Room(wr, j, wl - wr, h);
            hallway.createRoom(tiles);
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
        hallwayHorizontal.createRoom(tiles);
        hallwayVertical.createRoom(tiles);
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Position getLight() {
        return light;
    }
}
