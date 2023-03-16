package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    Random random;
//    public static final long MAXSEED = 9223372036854775807;

    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    public static final double ROOMPROBABILITY = 0.01;
    public static final double RANDOMSELECTROOMPROBABILITY = 0.1;
    public static final int MINROOMWIDTH = 3;
    public static final int MAXROOMWIDTH = 10;
    public static final int MINROOMHEIGHT = 3;
    public static final int MAXROOMHEIGHT = 10;
    public static final int MINHALLWAYWIDTH = 1;
    public static final int MAXHALLWAYWIDTH = 2;


    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] tiles = engine.interactWithInputString(args[0]);
        engine.renderFrame(tiles);
    }
    public Engine() {
    }
    public void renderFrame(TETile[][] tiles) {
        ter.renderFrame(tiles);
    }

    private void randomGenerateWorld(TETile[][] tiles) {
        fillTileWorldWithNothing(tiles);
        ArrayList<Room> rooms = new ArrayList<>();
        for (int i = 1; i < WIDTH; i++) {
            for (int j = 1; j < HEIGHT; j++) {
                if (tiles[i][j].equals(Tileset.NOTHING) && isARoom(i, j)) {
                    Room room = generateRoom(i, j);
                    room.drawRoom(tiles);
//                    ter.renderFrame(tiles);
                    // choose the nearest room and connect to it
                    Room connectRoom = selectRoom(room, rooms);
                    // if room has overlap with other room, don't need to draw hallway
                    if (connectRoom != null) {
                        room.connectWithRoom(tiles, random, connectRoom);
//                        ter.renderFrame(tiles);
                    }
                    rooms.add(room);
                }
            }
        }
    }

    private Room selectRoom(Room room, ArrayList<Room> rooms) {
//        // randomly select a room
//        if (rooms == null || rooms.isEmpty()) {
//            return null;
//        }
//        return rooms.get(RandomUtils.uniform(random, rooms.size()));
        // select the nearest room
        // if there are more than 1 nearest room, uniformly choose one of them
        // if there is no other room, or the room is overlapped with any room, return null
        if (rooms == null || rooms.isEmpty()) {
            return null;
        }
        if (random.nextDouble() < RANDOMSELECTROOMPROBABILITY) {
            return rooms.get(RandomUtils.uniform(random, rooms.size()));
        }
        Room nearestRoom = rooms.get(0);
        int count = 0;
        int minDis = WIDTH + HEIGHT;
        for (Room r:rooms) {
            int curDis = room.distance(r);
        // if distance< 0, then this two room has overlap with other room, just return null
            if (curDis < 0) {
                return null;
            }
            if (curDis < minDis) {
                minDis = curDis;
                nearestRoom = r;
                count = 1;
            } else if (curDis == minDis) {
                // randomly select the room with the nearest distance
                count += 1;
                if (RandomUtils.uniform(random, count) == 0) {
                    nearestRoom = r;
                }
            }
        }
        return nearestRoom;
    }

    private Room generateRoom(int i, int j) {
        int width = RandomUtils.uniform(random, MINROOMWIDTH, 1 + Math.min(WIDTH - i - 1, MAXROOMWIDTH));
        int height = RandomUtils.uniform(random, MINROOMHEIGHT, 1 + Math.min(HEIGHT - j - 1, MAXROOMHEIGHT));
        return new Room(i, j, width, height);
    }

    private boolean isARoom(int i, int j) {
        //randomly decides if there is a room whose bottom left corner located at (i, j)
        // must have enough space to draw the room (including walls)
        return WIDTH - i >= MINROOMWIDTH + 1 && HEIGHT - j >= MINROOMHEIGHT + 1 && random.nextDouble() < ROOMPROBABILITY;
    }

    private void fillTileWorldWithNothing(TETile[][] tiles) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }

    public static int argsValidate(String arg) {
        if (arg == null || arg.isEmpty()) {
            throw new IllegalArgumentException("Input argument should not be empty.");
        }
        String pattern = "^[Nn][1-9]\\d*[Ss]$";
        if (!arg.matches(pattern)) {
            throw new IllegalArgumentException("Input argument should be N/n + digits + S/s pattern， for example N12335S, n5785S.");
        }
        return Integer.parseInt(arg.substring(1, arg.length() - 1));
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        int seed = argsValidate(input);
        random = new Random(seed);
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        randomGenerateWorld(finalWorldFrame);
        return finalWorldFrame;
    }
}
