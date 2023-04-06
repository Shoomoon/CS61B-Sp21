package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    TERenderer ter = new TERenderer();
    Random random;
//    public static final long MAXSEED = 9223372036854775807;

    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    public static final double ROOMPROBABILITY = 0.01;
    public static final double CONNECTROOMPROBABILITY = 0.5;
    public static final int MINROOMWIDTH = 3;
    public static final int MAXROOMWIDTH = 10;
    public static final int MINROOMHEIGHT = 3;
    public static final int MAXROOMHEIGHT = 10;
    public static final int MINHALLWAYWIDTH = 1;
    public static final int MAXHALLWAYWIDTH = 2;
    // The blank tiles on the left.
    public static final int XDOWNSET = 0;
    // The blank tiles on the top.
    public static final int YDOWNSET = 4;
    public static final Font MESSAGEFONT = new Font("Monaco", Font.BOLD, 30);
    public static final Font INITINFOFONT = new Font("Monaco", Font.BOLD, 30);
    public static final Font TILEFONT = new Font("Monaco", Font.BOLD, 14);
    // The directory to store worldmaps
    public static final File BLOBS = new File(System.getProperty("user.dir"), ".maps");
    public static final File RANDOM = new File(BLOBS, "random.txt");
    public static final File WORLD = new File(BLOBS, "world.txt");
    public static final File PLAYER = new File(BLOBS, "player.txt");
    public static final File DOOR = new File(BLOBS, "door.txt");
    public static final File GUARDIANS = new File(BLOBS, "guardians.txt");
    public static final File TREASURES = new File(BLOBS, "treasures.txt");

    private static final File LIGHTS = new File(BLOBS, "lights.txt");
    public static final Position livesPosStart = new Position(2, HEIGHT + YDOWNSET / 2);
    public static final int[][] DIR = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    public static final Color BACKGROUND = Color.BLACK;
    public static final Color TEXTCOLOR = Color.WHITE;
    //maximum treasures
    public static final int MAXTREASURES = 4;
    //maximum guardians
    public static final int MAXGUARDIANS = 8;
    // maximum initial lives
    public static final int MAXINITLIVES = 3;
    public static final int MAXLIGHTS = 6;
    public static final int MINLIGHTS = 3;
    public static final int LIGHTRANGE = 6;
    public static final int MAXROOMCONNECTION = 3;
    private Position player;
    private Position door;
    private Position[] treasures;
    private Position[] guardians;
    private Position[] lights;
    private int lives;
    // Turn lights on or off, toggled with key 'H'
    private boolean enableLights = false;


    public void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] tiles = engine.interactWithInputString(args[0]);
    }
    public Engine() {
        if (!BLOBS.exists()) {
            BLOBS.mkdir();
        }
        lives = MAXINITLIVES;
    }
    public void renderFrame(TETile[][] tiles) {
        StdDraw.setFont(TILEFONT);
        ter.renderFrame(tiles);
        drawLives();
        StdDraw.show();
    }
    private void drawLives() {
        StdDraw.setFont(TILEFONT);
        TETile live = Tileset.FLOWER;
        for (int i = 0; i < lives; i++) {
            live.draw(livesPosStart.x + i, livesPosStart.y);
        }
    }
    private void removeLiveOnce() {
        lives -= 1;
        StdDraw.setFont(TILEFONT);
        Tileset.NOTHING.draw(livesPosStart.x + lives, livesPosStart.y);
        StdDraw.show();
    }
    private void addLiveOnce() {
        StdDraw.setFont(TILEFONT);
        Tileset.FLOWER.draw(livesPosStart.x + lives, livesPosStart.y);
        lives += 1;
        StdDraw.show();
    }
    private void randomGenerateWorld(TETile[][] tiles) {
        // random generate world map
        fillTileWorldWithNothing(tiles);
        ArrayList<Room> rooms = new ArrayList<>();
        // random generate rooms and randomly connect to previous room
        for (int i = 1; i < WIDTH; i++) {
            for (int j = 1; j < HEIGHT; j++) {
                if (tiles[i][j].equal(Tileset.NOTHING) && isRoom(i, j)) {
                    Room room = generateRoom(i, j);
                    room.createRoom(tiles);
//                    ter.renderFrame(tiles);
                    // choose the nearest room and connect to it via hallway, and do nothing if has overlap
                    List<Room> connectRoomsCandidates = nearest2Rooms(room, rooms);
                    room.connectWithRoom(tiles, random, connectRoomsCandidates.get(0));
                    // randomly connect to the 2nd nearest room
                    if (RandomUtils.uniform(random) < CONNECTROOMPROBABILITY) {
                        room.connectWithRoom(tiles, random, connectRoomsCandidates.get(1));
                    }

//                    Room connectRoom = nearestRoom(room, rooms);
//                    room.connectWithRoom(tiles, random, connectRoom);
//                    // randomly select another room to connect with
//                    if (!rooms.isEmpty() && RandomUtils.uniform(random) < CONNECTROOMPROBABILITY) {
//                        Room anotherRoom = rooms.get(RandomUtils.uniform(random, rooms.size()));
//                        room.connectWithRoom(tiles, random, anotherRoom);
//                    }
                    rooms.add(room);
                }
            }
        }

        // random generate player
        player = randomGenerateMultiPos(tiles, 1, 1, Tileset.AVATAR)[0];
        // random outgoing door
        door = randomGenerateDoor(tiles);
        // random treasures
        treasures = randomGenerateMultiPos(tiles, 1, MAXTREASURES, Tileset.TREASURE);
        // random generate guardians
        guardians = randomGenerateMultiPos(tiles, 1, MAXGUARDIANS, Tileset.GUARDIAN);
        // random generate lights
        lights = randomGenerateMultiPos(tiles, MINLIGHTS, MAXLIGHTS, Tileset.LIGHT);
    }

    private Position[] randomGenerateMultiPos(TETile[][] tiles, int minCount, int maxCount, TETile tile) {
        int size = RandomUtils.uniform(random, minCount, maxCount + 1);
        Position[] res = new Position[size];
        int floorCount = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].equal(Tileset.FLOOR)) {
                    floorCount += 1;
                    if (floorCount <= size) {
                        res[floorCount - 1] = new Position(i, j);
                    } else if (RandomUtils.uniform(random, floorCount) < size) {
                        res[RandomUtils.uniform(random, size)] = new Position(i, j);
                    }
                }
            }
        }
        for (Position p: res) {
            tiles[p.x][p.y] = tile;
        }
        return res;
    }

    private Position randomGenerateDoor(TETile[][] tiles) {
        int count = 0;
        int x = 0;
        int y = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (validDoor(tiles, i, j)) {
                    count += 1;
                    if (RandomUtils.uniform(random, count) == 0) {
                        x = i;
                        y = j;
                    }
                }
            }
        }
        tiles[x][y] = Tileset.LOCKED_DOOR;
        return new Position(x, y);
    }

    private boolean validDoor(TETile[][] tiles, int i, int j) {
//        if (i < 0 || i >= tiles.length || j < 0 || j >= tiles[0].length) {
//            throw new IllegalArgumentException("Inaccessible position!");
//        }
        if (!tiles[i][j].equal(Tileset.WALL)) {
            return false;
        }
        // from down tile
        if ((j + 1 == tiles[0].length || tiles[i][j + 1].equal(Tileset.NOTHING)) && j > 0 && tiles[i][j - 1].equal(Tileset.FLOOR)) {
            return true;
        }
        // from up tile
        if ((j == 0 || tiles[i][j - 1].equal(Tileset.NOTHING)) && j + 1 < tiles[0].length && tiles[i][j + 1].equal(Tileset.FLOOR)) {
            return true;
        }
        // from right tile
        if ((i == 0 || tiles[i - 1][j].equal(Tileset.NOTHING)) && i + 1 < tiles.length && tiles[i + 1][j].equal(Tileset.FLOOR)) {
            return true;
        }
        // from left tile
        return (i + 1 == tiles.length || tiles[i + 1][j].equal(Tileset.NOTHING)) && i > 0 && tiles[i - 1][j].equal(Tileset.FLOOR);
    }
    // select the 2 nearest rooms
    // if there are more than 2 rooms, uniformly select any of them
    private List<Room> nearest2Rooms(Room room, List<Room> rooms) {
        // only need to store at most 2 nearest rooms
        // and one 2nd nearest room
        Room r0 = room;
        Room r1 = room;
        int d0 = WIDTH + HEIGHT + 1;
        int count0 = 0;
        int d1 = WIDTH + HEIGHT + 1;
        int count1 = 0;
        for (Room r: rooms) {
            int curDis = room.distance(r);
            if (curDis < d0) {
                r1 = r0;
                d1 = d0;
                count1 = count0;

                r0 = r;
                d0 = curDis;
                count0 = 1;
            } else if (curDis == d0) {
                count0 += 1;
                if (RandomUtils.uniform(random, count0) == 0) {
                    r0 = r;
                }
            } else if (curDis < d1) {
                r1 = r;
                d1 = curDis;
                count1 = 1;
            } else if (curDis == d1) {
                count1 += 1;
                if (RandomUtils.uniform(random, count1) == 0) {
                    r1 = r;
                }
            }
        }
        List<Room> res = new ArrayList<>();
        res.add(r0);
        res.add(r1);
        return res;
    }
    private Room nearestRoom(Room room, ArrayList<Room> rooms) {
//        // randomly select a room
//        if (rooms == null || rooms.isEmpty()) {
//            return null;
//        }
//        return rooms.get(RandomUtils.uniform(random, rooms.size()));
        // select the nearest room
        // if there are more than 1 nearest room, uniformly choose one of them
        // if there is no other room, or the room is overlapped with any room, return room itself
        Room nearestRoom = room;
        int count = 0;
        int minDis = WIDTH + HEIGHT;
        for (Room r:rooms) {
            int curDis = room.distance(r);
        // if distance< 0, then this two room has overlap with other room, just return room itself
            if (curDis < 0) {
                return room;
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

    private boolean isRoom(int i, int j) {
        //randomly decides if there is a room whose bottom left corner located at (i, j)
        // must have enough space to draw the room (including walls)
        return WIDTH - i >= MINROOMWIDTH + 1 && HEIGHT - j >= MINROOMHEIGHT + 1 && RandomUtils.uniform(random) < ROOMPROBABILITY;
    }

    public void fillTileWorldWithNothing(TETile[][] tiles) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }

    public String[] getInfo(String arg){
        // validate the input argument and return all information
        if (arg == null || arg.isEmpty()) {
            throw new IllegalArgumentException("Input argument should not be empty.");
        }
        String regex = "^((?<load>[Ll])|([Nn](?<seed>[1-9]\\d*)[Ss]))(?<actions>[WwAaSsDd]*)(?<end>:[Qq])?.*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(arg);
        if (!m.matches()) {
            throw new IllegalArgumentException("Input argument should start with L/l or N/n + digits + S/s，followed by actions W/wA/aS/sD/d, and maybe end with :Qq" +
                    "for example N12335SSSAW:Q, LWSSW:Q, N2464S, N8544S:Q.");
        }
        // capture load, seed, actions, and end requirement
        String load = m.replaceAll("${load}");
        String inputSeed = m.replaceAll("${seed}");
        String actions = m.replaceAll("${actions}");
        String end = m.replaceAll("${end}");
//        System.out.println(load);
//        System.out.println(inputSeed);
//        System.out.println(actions);
//        System.out.println(end);
        return new String[]{load, inputSeed, actions, end};
    }
    public void clearRecord() {
        if (WORLD.exists()) {
            WORLD.delete();
        }
        if (RANDOM.exists()) {
            RANDOM.delete();
        }
        if (PLAYER.exists()) {
            PLAYER.delete();
        }
        if (DOOR.exists()) {
            DOOR.delete();
        }
        if (TREASURES.exists()) {
            TREASURES.delete();
        }
        if (GUARDIANS.exists()) {
            GUARDIANS.delete();
        }
        if (LIGHTS.exists()) {
            LIGHTS.delete();
        }
    }
    public boolean hasSavedGame() {
        return WORLD.exists() && RANDOM.exists()
                && PLAYER.exists() && DOOR.exists()
                && TREASURES.exists() && GUARDIANS.exists() && LIGHTS.exists();
    }

    public void saveGame(TETile[][] world) {
        Utiles.writeObject(WORLD, world);
        Utiles.writeObject(RANDOM, random);
        Utiles.writeObject(PLAYER, player);
        Utiles.writeObject(DOOR, door);
        Utiles.writeObject(GUARDIANS, guardians);
        Utiles.writeObject(TREASURES, treasures);
        Utiles.writeObject(LIGHTS, lights);
    }

    public TETile[][] getSavedGame() {
        random = getSavedRandom();
        player = getSavedItem(PLAYER);
        door = getSavedItem(DOOR);
        guardians = getSavedMultiItems(GUARDIANS);
        treasures = getSavedMultiItems(TREASURES);
        lights = getSavedMultiItems(LIGHTS);
        return getSavedWorld();
    }

    private Position[] getSavedMultiItems(File file) {
        return Utiles.readObject(file, Position[].class);
    }
    private Position getSavedItem(File file) {
        return Utiles.readObject(file, Position.class);
    }
    private Random getSavedRandom() {
        return Utiles.readObject(RANDOM, Random.class);
    }
    private TETile[][] getSavedWorld() {
        return Utiles.readObject(WORLD, TETile[][].class);
    }

    private void initializeCanvas() {
        ter.initialize(WIDTH + XDOWNSET, HEIGHT + YDOWNSET);
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        initializeCanvas();
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        boolean worldInitialized = false;
        drawMenu();
        while (true) {
            char c = Character.toUpperCase(getNextKeyTyped());
            if (c == 'L') {
                clearMenu();
                drawInitInfo(String.valueOf(c), 500);
                finalWorldFrame = getSavedGame();
                renderFrame(finalWorldFrame);
                worldInitialized = true;
            } else if (c == 'N') {
                clearMenu();
                StringBuilder seed = new StringBuilder();
                drawInitInfo("Seed: ");
                while (true) {
                    char c1 = Character.toUpperCase(getNextKeyTyped());
                    if (Character.isDigit(c1)) {
                        seed.append(c1);
                        drawInitInfo("Seed: " + seed.toString());
                    } else if (c1 == 'S') {
                        if (seed.isEmpty()) {
                            drawInitInfo("Invalid Input! Please input random seed first!", 1000);
                            clearInitInfo();
                            continue;
                        }
                        random = new Random(Long.parseLong(seed.toString()));
                        randomGenerateWorld(finalWorldFrame);
                        renderFrame(finalWorldFrame);
                        worldInitialized = true;
                        break;
                    } else {
                        continue;
                    }
                }
            } else if (c == ':') {
                if (!worldInitialized) {
                    clearMenu();
                    drawInitInfo(":");
                }
                char c1 = Character.toUpperCase(getNextKeyTyped());
                if (c1 == 'Q') {
                    if (!worldInitialized) {
                        drawInitInfo(":Q", 500);
                        flashInitInfo("No data can be saved!");
                    } else {
                        saveGame(finalWorldFrame);
                        flashMessage("Game was Saved!", 1000);
                    }
                    break;
                } else {
                    if (worldInitialized) {
                        drawMessage("Please enter :Q if you want to save and quit.", 2000);
                    } else {
                        drawInitInfo(":" + c1, 500);
                        drawInitInfo("Invalid Input!", 1000);
                    }
                }
            } else if ("WASD".indexOf(c) >= 0) {
                if (!worldInitialized) {
                    drawInitInfo("No Game Exists!", 1000);
                }
                if (move(finalWorldFrame, c)) {
                    break;
                }
            } else {
                drawMessage("Invalid Input! Please Try Again!", 2000);
            }
        }
        StdDraw.clear(Color.BLACK);
        drawInitInfo("Please Close the Game!");
    }

    private void flashMessage(String s, int flashTime) {
        while (flashTime >= 0) {
            drawMessage(s, 200);
            drawMessage("", 200);
            flashTime -= 200;
        }
        drawMessage(s);
    }
    private void flashInitInfo(String s) {
        int t = 1000;
        while (t >= 0) {
            drawInitInfo(s, 200);
            drawInitInfo("", 200);
            t -= 200;
        }
        drawInitInfo(s);
    }
    private void drawMenu() {
        StdDraw.clear(BACKGROUND);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 60));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 8, "CS61B: THE GAME");
        StdDraw.setFont(INITINFOFONT);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Load Game (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 2, "Quit (Q)");
        StdDraw.show();
    }
    private void drawInitInfo(String info, int ms) {
        clearInitInfo();
        StdDraw.setFont(INITINFOFONT);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, info);
        StdDraw.show(ms);
        clearInitInfo();
    }
    private void clearInitInfo() {
        clearMessageHelper(WIDTH / 2.0, HEIGHT / 2.0, 25, 2);
    }
    private void clearMenu() {
        StdDraw.clear(BACKGROUND);
        StdDraw.show();
    }
    private void drawInitInfo(String info) {
        clearInitInfo();
        StdDraw.setFont(INITINFOFONT);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, info);
        StdDraw.show();
    }

    private char getNextKeyTyped() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return StdDraw.nextKeyTyped();
            }
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard).
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
        // initiate or retrieve random and world map
        String[] info = getInfo(input);
        TETile[][] finalWorldFrame;
        ter.initialize(WIDTH + XDOWNSET, HEIGHT + YDOWNSET);
        if (!info[0].isEmpty()) {
            if (!hasSavedGame()) {
                return null;
            } else {
                finalWorldFrame = getSavedGame();
                renderFrame(finalWorldFrame);
            }
        } else {
            random = new Random(Long.parseLong(info[1]));
            finalWorldFrame = new TETile[WIDTH][HEIGHT];
            randomGenerateWorld(finalWorldFrame);
            renderFrame(finalWorldFrame);
            StdDraw.pause(500);
        }
        // for each action, move the player in the direction
        if (!info[2].isEmpty()) {
            for (int i = 0; i < info[2].length(); i++) {
                if (move(finalWorldFrame, info[2].charAt(i))) {
                    return finalWorldFrame;
                }
                StdDraw.pause(500);
            }
        }

        // save game
        if (!info[3].isEmpty()) {
            saveGame(finalWorldFrame);
        }
        return finalWorldFrame;
    }

    private boolean move(TETile[][] finalWorldFrame, char c) {
        // if lost all lives or success entering the door, return true, otherwise return false
        int dx = 0;
        int dy = 0;
        if (c == 'W') {
            dy = 1;
        } else if (c == 'A') {
            dx = -1;
        } else if (c == 'S') {
            dy = -1;
        } else if (c == 'D') {
            dx = 1;
        }
        Position nextPos = new Position(player.x + dx, player.y + dy);
        // if next tile is wall
        if (finalWorldFrame[nextPos.x][nextPos.y].equal(Tileset.WALL)) {
            return false;
        } else if (nextPos.equal(door)) {
            // deal with door
            congratulations(finalWorldFrame);
            return true;
        }

        for (Position guardian: guardians) {
            // deal with guardians
            if (nextPos.equal(guardian)) {
                if (lives <= 0) {
                    // game is over, flash the message "Game Over!"
                    flashMessage("Game Over!", 1000);
                    StdDraw.clear(BACKGROUND);
                    drawInitInfo("Game Over!", 2000);
                    return true;
                }
                deadOnce();
                return false;
            }
        }
        // deal with treasure
        for (Position treasure: treasures) {
            if (nextPos.equal(treasure)) {
                openTreasure(treasure);
            }
        }
        // update world map
        StdDraw.setFont(TILEFONT);
        finalWorldFrame[player.x][player.y] = Tileset.FLOOR;
        finalWorldFrame[player.x][player.y].draw(player.x, player.y);
        player = nextPos;
        finalWorldFrame[player.x][player.y] = Tileset.AVATAR;
        finalWorldFrame[player.x][player.y].draw(player.x, player.y);
        StdDraw.show();
        return false;
//        renderFrame(finalWorldFrame);
    }

    private void drawMessage(String message) {
        drawMessageHelper(message);
        StdDraw.show();
    }
    private void drawMessage(String message, int microsecond) {
        drawMessageHelper(message);
        StdDraw.show(microsecond);
        clearMessage();
    }
    private void drawMessageHelper(String message) {
        clearMessage();
        StdDraw.setFont(MESSAGEFONT);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.text(WIDTH / 2.0, livesPosStart.y, message);
    }
    public void clearMessage() {
        clearMessageHelper(WIDTH / 2.0, livesPosStart.y, 30,1);
    }
    public void clearMessageHelper(double centerX, double centerY, double halfWidth, double halfHeight) {
        StdDraw.setPenColor(BACKGROUND);
        StdDraw.filledRectangle(centerX, centerY, halfWidth, halfHeight);
//        int messageSize = 20;
//        for (int i = 0; i < messageSize; i++) {
//            Tileset.NOTHING.draw(WIDTH / 2 - 10 + i, livesPosStart.y);
//            Tileset.NOTHING.draw(WIDTH / 2 - 10 + i, livesPosStart.y - 1);
//        }
        StdDraw.show();
    }

    private void openTreasure(Position treasure) {
        flashTile(treasure, Tileset.TREASURE, 1000, 200);
        addLiveOnce();
    }

    private void deadOnce() {
        drawMessage("Dead Once!");
        flashTile(player, Tileset.AVATAR, 1000, 200);
        removeLiveOnce();
        clearMessage();
    }

    // flash between Tile NOTHING and tile
    private void flashTile(Position pos, TETile tile, int flashTime, int flashBreak) {
        StdDraw.setFont(TILEFONT);
        while (flashTime >= 0) {
            Tileset.NOTHING.draw(pos.x, pos.y);
            StdDraw.show();
            StdDraw.pause(flashBreak);
            tile.draw(pos.x, pos.y);
            StdDraw.show();
            StdDraw.pause(flashBreak);
            flashTime -= flashBreak;
        }
    }

    private void congratulations(TETile[][] finalWorldFrame) {
        finalWorldFrame[player.x][player.y] = Tileset.FLOOR;
        finalWorldFrame[player.x][player.y].draw(player.x, player.y);
        finalWorldFrame[door.x][door.y] = Tileset.UNLOCKED_DOOR;
        finalWorldFrame[door.x][door.y].draw(door.x, door.y);
        flashMessage("Congratulations!", 1000);
        StdDraw.show();
    }
}
