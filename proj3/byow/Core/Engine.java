package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
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
    public static final double CONNECTROOMPROBABILITY = 0.3;
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
    public static final int XOFFSET = 0;
    public static final int YOFFSET = 2;
    public static final Font MESSAGEFONT = new Font("Monaco", Font.BOLD, 30);
    public static final Font INITINFOFONT = new Font("Monaco", Font.BOLD, 30);
    public static final Font TILEFONT = new Font("Monaco", Font.BOLD, 14);
    public static final Font INSTRUCTIONFONT = new Font("Monaco", Font.BOLD, 16);
    // The directory to store world maps and variables
    public static final File BLOBS = new File(System.getProperty("user.dir"), ".maps");
    public static final File SAVEDGAME = new File(BLOBS, "savedGame.txt");
    public static final Position livesPosStart = new Position(2, HEIGHT + YOFFSET + YDOWNSET / 2);
    public static final Color BACKGROUND = Color.BLACK;
    public static final Color TEXTCOLOR = Color.WHITE;
    //maximum treasures
    public static final int MAXTREASURES = 4;
    //maximum guardians
    public static final int MAXGUARDIANS = 4;
    // maximum initial lives
    public static final int MAXINITLIVES = 1;
    public static final int SIGHTRANGE = 6;
    public static final Position MESSAGEPOS = new Position(WIDTH / 2 + XOFFSET, HEIGHT + YDOWNSET / 2 + YOFFSET);
    public static final Position INSTRUCTIONPOS = new Position(WIDTH / 2 + XOFFSET, YOFFSET / 2);
    public static final int[][] dir = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
    private TETile[][] world;
    private Position avatar;
    private Position door;
    private List<Position> treasures;
    private List<Position> guardians;
    private List<List<Position>> guardiansChasePaths;
    private List<Room> roomsList;
    private int lives;
    // Turn lights on or off, toggled with key 'H'
    private boolean avatarSightOnly = false;
    private boolean chasePathDisplay = false;
    private StringBuilder commandRecord;
    private long randomSeed;
    private int commandReplayIndex = -1;


    public void main(String[] args) {
        Engine engine = new Engine();
        world = engine.interactWithInputString(args[0]);
    }
    public Engine() {
        if (!BLOBS.exists()) {
            BLOBS.mkdir();
        }
    }
    public void renderFrame() {
        clearCanvas();
        StdDraw.setFont(TILEFONT);
        if (!avatarSightOnly) {
            ter.renderFrame(world);
            showMovableItems();
        } else {
            renderAvatarSightOnly();
        }
        if (chasePathDisplay) {
            showChasePaths();
        }
        showMovableItems();
        drawLives();
        drawInstruction();
        StdDraw.show();
    }
    public void showMovableItems() {
        // display all movable items: Avatar, treasures, guardians
        // display avatar
        drawTile(Tileset.AVATAR, avatar);
        // display treasures
        for (Position t: treasures) {
            if (!avatarSightOnly || inAvatarSight(t)) {
                drawTile(Tileset.TREASURE, t);
            }
        }
        // display guardians
        for (Position g: guardians) {
            if (!avatarSightOnly || inAvatarSight(g)) {
                drawTile(Tileset.GUARDIAN, g);
            }
        }
    }
    public void drawTile(TETile tile, Position p) {
        drawTile(tile, p.x, p.y);
    }
    public void drawTile(TETile tile, int x, int y) {
        StdDraw.setFont(TILEFONT);
        tile.draw(x + XOFFSET, y + YOFFSET);
    }

    private boolean inAvatarSight(Position p) {
        int d = Math.abs(p.x - avatar.x) + Math.abs(p.y - avatar.y);
        return d <= SIGHTRANGE;
    }

    private void drawInstruction() {
        drawInstruction("Up(W) Down(S) Left(A) Right(D) Save & Quit(:Q) New Game(N) Randomly Toggle Light(G) Avatar Sight(H) Chase Path(P)");
    }
    private void drawInstruction(String s) {
        clearInstruction();
        StdDraw.setFont(INSTRUCTIONFONT);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.text(WIDTH / 2.0 + XOFFSET, YOFFSET / 2.0, s);
    }

    public void renderRoom(Room room) {
        int x = room.getX();
        int y = room.getY();
        int w = room.getWidth();
        int h = room.getHeight();
        for (int i = -1; i <= w; i++) {
            for (int j = -1; j <= h; j++) {
                int distance = Math.abs(x + i - avatar.x) + Math.abs(y + j - avatar.y);
                if (!avatarSightOnly || distance <= SIGHTRANGE) {
                    drawTile(x + i, y + j);
                }
            }
        }
        if (chasePathDisplay) {
            showChasePaths();
        }
        showMovableItems();
        StdDraw.show();
    }
    private void renderAvatarSightOnly() {
        clearMap();
        for (int d = SIGHTRANGE; d >= 0; d--) {
            for (int i = d - SIGHTRANGE; i <= SIGHTRANGE - d; i++) {
                drawTile(avatar.x + i, avatar.y + d);
                drawTile(avatar.x + i, avatar.y - d);
            }
        }
        if (chasePathDisplay) {
            showChasePaths();
        }
        showMovableItems();
    }
    private void drawTile(Position p) {
        drawTile(p.x, p.y);
    }
    private void drawTile(int i, int j) {
        if (i < 0 || i >= WIDTH || j < 0 || j >= HEIGHT) {
//            System.out.print("Invalid index!\n");
            return;
        }
        drawTile(world[i][j], i, j);
    }
    private void drawLives() {
        StdDraw.setFont(TILEFONT);
        TETile live = Tileset.FLOWER;
        for (int i = 0; i < lives; i++) {
            live.draw(livesPosStart.x + i, livesPosStart.y);
        }
    }
    private void lostLiveKTimes(int k) {
        StdDraw.setFont(TILEFONT);
        for (int i = 0; i < k; i++) {
            lives -= 1;
            Tileset.NOTHING.draw(livesPosStart.x + lives, livesPosStart.y);
            StdDraw.show(200);
        }
    }
    private void addLiveOnce() {
        StdDraw.setFont(TILEFONT);
        Tileset.FLOWER.draw(livesPosStart.x + lives, livesPosStart.y);
        lives += 1;
        StdDraw.show();
    }
    private void randomGenerateWorld() {
        // random generate world map
        roomsList = new ArrayList<>();
        // random generate rooms and randomly connect to previous room
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j].equal(Tileset.NOTHING) && isRoom(i, j)) {
                    Room room = generateRoomAt(i, j);
                    if (room == null) {
                        continue;
                    }
                    room.createRoom(world);
//                    ter.renderFrame(tiles);
                    // choose the nearest room and connect to it via hallway, and do nothing if has overlap
                    List<Room> connectRoomsCandidates = nearest2Rooms(room);
                    room.connectWithRoom(world, random, connectRoomsCandidates.get(0));
                    // randomly connect to the 2nd nearest room
                    if (RandomUtils.uniform(random) < CONNECTROOMPROBABILITY) {
                        room.connectWithRoom(world, random, connectRoomsCandidates.get(1));
                    }

//                    Room connectRoom = nearestRoom(room, rooms);
//                    room.connectWithRoom(tiles, random, connectRoom);
//                    // randomly select another room to connect with
//                    if (!rooms.isEmpty() && RandomUtils.uniform(random) < CONNECTROOMPROBABILITY) {
//                        Room anotherRoom = rooms.get(RandomUtils.uniform(random, rooms.size()));
//                        room.connectWithRoom(tiles, random, anotherRoom);
//                    }
                    roomsList.add(room);
                }
            }
        }
        // random generate avatar, treasures and guardians
        int treasuresCount = RandomUtils.uniform(random, 1, MAXTREASURES + 1);
        int guardiansCount = RandomUtils.uniform(random, 2, MAXGUARDIANS + 1);
        List<Position> items = randomSelectMultiPos(world, 1 + treasuresCount + guardiansCount);
        avatar = items.get(0);
        treasures = new ArrayList<>(items.subList(1, treasuresCount + 1));
        guardians = new ArrayList<>(items.subList(treasuresCount + 1, 1 + treasuresCount + guardiansCount));
        updateChasePaths();
        // random outgoing door
        door = randomGenerateDoor();

        avatarSightOnly = false;
        chasePathDisplay = false;
        lives = MAXINITLIVES;
        commandRecord = new StringBuilder();
    }

    private List<Position> randomSelectMultiPos(TETile[][] tiles, int size) {
        // uniformly select size arraylist from tiles
        List<Position> res = new ArrayList<>();
        int floorCount = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].equal(Tileset.FLOOR)) {
                    floorCount += 1;
                    int k = RandomUtils.uniform(random, floorCount);
                    if (floorCount <= size) {
                        res.add(new Position(i, j));
                        Position tmp = res.get(k);
                        res.set(k, res.get(floorCount - 1));
                        res.set(floorCount - 1, tmp);
                    } else if (k < size) {
                        res.set(k, new Position(i, j));
                    }
                }
            }
        }
        return res;
    }

    private Position randomGenerateDoor() {
        int count = 0;
        int x = 0;
        int y = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (validDoor(i, j)) {
                    count += 1;
                    if (RandomUtils.uniform(random, count) == 0) {
                        x = i;
                        y = j;
                    }
                }
            }
        }
        world[x][y] = Tileset.LOCKED_DOOR;
        return new Position(x, y);
    }
    public TETile[][] getWorld() {
        return world;
    }

    private boolean validDoor(int i, int j) {
//        if (i < 0 || i >= tiles.length || j < 0 || j >= tiles[0].length) {
//            throw new IllegalArgumentException("Inaccessible position!");
//        }
        if (!world[i][j].equal(Tileset.WALL)) {
            return false;
        }
        // from down tile
        if ((j + 1 == world[0].length || world[i][j + 1].equal(Tileset.NOTHING)) && j > 0 && world[i][j - 1].equal(Tileset.FLOOR)) {
            return true;
        }
        // from up tile
        if ((j == 0 || world[i][j - 1].equal(Tileset.NOTHING)) && j + 1 < world[0].length && world[i][j + 1].equal(Tileset.FLOOR)) {
            return true;
        }
        // from right tile
        if ((i == 0 || world[i - 1][j].equal(Tileset.NOTHING)) && i + 1 < world.length && world[i + 1][j].equal(Tileset.FLOOR)) {
            return true;
        }
        // from left tile
        return (i + 1 == world.length || world[i + 1][j].equal(Tileset.NOTHING)) && i > 0 && world[i - 1][j].equal(Tileset.FLOOR);
    }
    // select the 2 nearest rooms
    // if there are more than 2 rooms, uniformly select any of them
    private List<Room> nearest2Rooms(Room room) {
        // only need to store at most 2 nearest rooms
        // and one 2nd nearest room
        Room r0 = room;
        Room r1 = room;
        int d0 = WIDTH + HEIGHT + 1;
        int count0 = 0;
        int d1 = WIDTH + HEIGHT + 1;
        int count1 = 0;
        for (Room r: roomsList) {
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
    private Room nearestRoom(Room room) {
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
        for (Room r: roomsList) {
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

    private Room generateRoomAt(int i, int j) {
        // room at (i, j) must have no overlap with other rooms
        // since rooms are created from left to right, bottom to top,
        // so for room at (i, j), the width could be as big as possible,
        // and only rooms on the top left will impact the height of the room
        int width = RandomUtils.uniform(random, MINROOMWIDTH, 1 + Math.min(WIDTH - i - 1, MAXROOMWIDTH));
        int maxHeight = Math.min(HEIGHT - j - 1, MAXROOMHEIGHT);
        for (Room r: roomsList) {
            // // at least 1 tile horizontally between 2 rooms
            if (r.getX() + r.getWidth() == i && i >= r.getX() && i < r.getY() + r.getHeight()) {
                return null;
            }
            // at least 1 tile vertically between 2 rooms
            if (r.getX() + r.getWidth() - 1 >= i && r.getY() > j) {
                maxHeight = Math.min(maxHeight, r.getY() - j - 1);
            }
        }
        if (maxHeight < MINROOMHEIGHT) {
            return null;
        }
        int height = RandomUtils.uniform(random, MINROOMHEIGHT, 1 + maxHeight);
        Position light = new Position(RandomUtils.uniform(random, i, i + width), RandomUtils.uniform(random, j, j + height));
        return new Room(i, j, width, height, light);
    }

    private boolean isRoom(int i, int j) {
        //randomly decides if there is a room whose bottom left corner located at (i, j)
        // must have enough space to draw the room (including walls)
        return WIDTH - i >= MINROOMWIDTH + 1 && HEIGHT - j >= MINROOMHEIGHT + 1 && RandomUtils.uniform(random) < ROOMPROBABILITY;
    }

    public void fillTileWorldWithNothing() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    public String[] getInfo(String arg){
        // validate the input argument and return all information
        if (arg == null || arg.isEmpty()) {
            throw new IllegalArgumentException("Input argument should not be empty.");
        }
        arg = arg.toUpperCase();
        String regex = "^((?<load>L)|(N(?<seed>[1-9]\\d*)S)|(?<replay>R))(?<actions>[WASDGHP$]*)(?<end>(:Q)?).*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(arg);
        if (!m.matches()) {
            throw new IllegalArgumentException("Input argument should start with L/l or N/n + digits + S/s，followed by actions W/wA/aS/sD/dG/gH/hP/p, and maybe end with :Q/q" +
                    "for example N12335SSSAW:Q, LWSSW:Q, N2464S, N8544S:Q.");
        }
        // capture load, seed, actions, and end requirement
        String load = m.replaceAll("${load}");
        String inputSeed = m.replaceAll("${seed}");
        String actions = m.replaceAll("${actions}");
        String end = m.replaceAll("${end}");
        String replay = m.replaceAll("${replay}");
//        System.out.println(load);
//        System.out.println(inputSeed);
//        System.out.println(actions);
//        System.out.println(end);
        return new String[]{load, inputSeed, actions, end, replay};
    }
    public void clearRecord() {
        if (SAVEDGAME.exists()) {
            SAVEDGAME.delete();
        }
    }
    public boolean hasSavedGame() {
        return SAVEDGAME.exists();
    }
    private Record toRecord() {
        return new Record(world, random, avatar, door, treasures, guardians, guardiansChasePaths,
                roomsList, lives, avatarSightOnly, chasePathDisplay, commandRecord, randomSeed);
    }

    public void saveGame() {
        Record currentGame = toRecord();
        Utiles.writeObject(SAVEDGAME, currentGame);
    }

    public void getSavedGame() {
        Record savedGame = Utiles.readObject(SAVEDGAME, Record.class);
        world = savedGame.getWorld();
        random = savedGame.getRandom();
        avatar = savedGame.getAvatar();
        door = savedGame.getDoor();
        guardians = savedGame.getGuardians();
        guardiansChasePaths = savedGame.getGuardiansChasePath();
        treasures = savedGame.getTreasures();
        roomsList = savedGame.getRoomsList();
        lives = savedGame.getLives();
        avatarSightOnly = savedGame.getAvatarSightOnly();
        chasePathDisplay = savedGame.getChasePathDisplay();
        commandRecord = savedGame.getCommandRecord();
        randomSeed = savedGame.getSeed();
    }

    private void initializeCanvas() {
        ter.initialize(WIDTH + XDOWNSET + XOFFSET, HEIGHT + YDOWNSET + YOFFSET, XOFFSET, YOFFSET);
    }
    private void clearCanvas() {
        StdDraw.clear(BACKGROUND);
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public TETile[][] interactWithKeyboard() {
        initializeCanvas();
        drawMenu();
        while (true) {
            char c0 = getNextKeyTypedUpperCase();
            if (c0 == 'L') {
                drawInitInfo(String.valueOf(c0), 500);
                if (!hasSavedGame()) {
                    drawInitInfo("No previous game can be loaded!", 1000);
                    drawMenu();
                } else {
                    getSavedGame();
                    renderFrame();
                    break;
                }
            } else if (c0 == 'N') {
                generateNewGame();
                break;
            } else if (c0 == 'R') {
                replayGame();
                break;
            }
        }
        while (true) {
            char c = getNextKeyTypedUpperCase();
            if (c == 'L') {
                drawInitInfo(String.valueOf(c), 500);
                if (!hasSavedGame()) {
                    drawInitInfo("No previous game can be loaded!", 1000);
                } else {
                    getSavedGame();
                }
                renderFrame();
            } else if (c == 'N') {
                generateNewGame();
            } else if (c == 'R') {
                replayGame();
            } else {
                if (c == ':') {
                    drawMessage(":");
                    char c1 = getNextKeyTypedUpperCase();
                    if (c1 == 'Q') {
                        drawMessage(":Q", 500);
                        saveGame();
                        flashMessage("Game was Saved!", 1000);
                        break;
                    } else {
                        drawMessage("Please enter :Q if you want to save and quit.", 1000);
                    }
                } else if ("WASDGHP".indexOf(c) >= 0) {
                    commandRecord.append(c);
                    if (c == 'G') {
                        randomToggleLight();
                    } else if (c == 'H') {
                        toggleAvatarSight();
                    } else if (c == 'P') {
                        toggleChasePath();
                    } else if (move(c)) {
                        break;
                    }
                }
            }
        }
        StdDraw.clear(Color.BLACK);
        drawInitInfo("Please Close the Game!");
        return world;
    }

    private void generateNewGame() {
        world = new TETile[WIDTH][HEIGHT];
        fillTileWorldWithNothing();
        randomSeed = getInputSeed();
        random = new Random(randomSeed);
        randomGenerateWorld();
        renderFrame();
    }

    private Long getInputSeed() {
        clearCanvas();
        drawInitInfo("Seed: ");
        drawInstruction("Input seed (0 < seed <= 9,223,372,036,854,775,807), ending with 'S'.");
        StringBuilder seed = new StringBuilder();
        while (true) {
            char c = getNextKeyTypedUpperCase();
            if (Character.isDigit(c)) {
                if (seed.isEmpty() && c == '0') {
                    continue;
                }
                seed.append(c);
                drawInitInfo("Seed: " + seed);
            } else if (c == 'S') {
                if (seed.isEmpty()) {
                    drawInitInfo("Please input seed first!", 1000);
                    drawInitInfo("Seed: ");
                    continue;
                }
                if (!validSeed(seed)) {
                    drawInitInfo("Seed must be in range (0, 9 223 372 036 854 775 807] !", 1000);
                    seed = new StringBuilder();
                    drawInitInfo("Seed: ");
                    continue;
                }
                break;
            }
        }
        return Long.parseLong(seed.toString());
    }

    private void toggleChasePath() {
        chasePathDisplay = !chasePathDisplay;
        if (!chasePathDisplay) {
            clearChasePaths();
        } else {
            showChasePaths();
        }
        showMovableItems();
        StdDraw.show();
    }

    private boolean validSeed(StringBuilder seed) {
        String maxLong = "9223372036854775807";
        if (seed.length() == maxLong.length()) {
            for (int i = 0; i < maxLong.length(); i++) {
                if (seed.charAt(i) != maxLong.charAt(i)) {
                    return seed.charAt(i) < maxLong.charAt(i);
                }
            }
        }
        return seed.length() <= maxLong.length();
    }

    private void randomToggleLight() {
        Room room = roomsList.get(RandomUtils.uniform(random, roomsList.size()));
        room.toggleLight(world);
        renderRoom(room);
    }
    private void toggleAvatarSight() {
        this.avatarSightOnly = !this.avatarSightOnly;
        renderFrame();
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
        clearCanvas();
        double centerX = WIDTH / 2.0 + XOFFSET;
        double centerY = HEIGHT / 2.0  + YOFFSET;
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 60));
        StdDraw.text(centerX, centerY + 8, "CS61B: THE GAME");
        StdDraw.setFont(INITINFOFONT);
        StdDraw.text(centerX, centerY + 2, "New Game (N)");
        StdDraw.text(centerX, centerY, "Load Game (L)");
        StdDraw.text(centerX, centerY - 2, "Replay (R)");
        StdDraw.text(centerX, centerY - 4, "Quit (Q)");
        StdDraw.show();
    }
    private void drawInitInfo(String info, int ms) {
        clearMap();
        StdDraw.setFont(INITINFOFONT);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.text(WIDTH / 2.0 + XOFFSET, HEIGHT / 2.0 + YOFFSET, info);
        StdDraw.show(ms);
        clearMap();
    }
    private void clearMap() {
        StdDraw.setPenColor(BACKGROUND);
        StdDraw.filledRectangle(WIDTH / 2.0 + XOFFSET, HEIGHT / 2.0 + YOFFSET, WIDTH / 2.0, HEIGHT / 2.0);
        StdDraw.show();
    }
    public void clearMessage() {
        StdDraw.setPenColor(BACKGROUND);
        StdDraw.filledRectangle(MESSAGEPOS.x, MESSAGEPOS.y, WIDTH / 2.0 + XOFFSET - (livesPosStart.x + MAXINITLIVES + MAXTREASURES), YDOWNSET / 2.0);
        StdDraw.show();
    }
    private void clearLives() {
        StdDraw.setPenColor(BACKGROUND);
        StdDraw.filledRectangle(livesPosStart.x + (MAXINITLIVES + MAXTREASURES) / 2.0, HEIGHT + YDOWNSET / 2.0 + YOFFSET, (3 + MAXTREASURES) / 2.0, YDOWNSET / 2.0);
        StdDraw.show();
    }
    private void clearInstruction() {
        StdDraw.setPenColor(BACKGROUND);
        StdDraw.filledRectangle(INSTRUCTIONPOS.x, INSTRUCTIONPOS.y, WIDTH / 2.0, YOFFSET / 2.0);
        StdDraw.show();
    }
    // draw message shown in the center
    private void drawInitInfo(String info) {
        clearMap();
        StdDraw.setFont(INITINFOFONT);
        StdDraw.setPenColor(TEXTCOLOR);
        StdDraw.text(WIDTH / 2.0 + XOFFSET, HEIGHT / 2.0 + YOFFSET, info);
        StdDraw.show();
    }

    private char getNextKeyTypedUpperCase() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
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
        initializeCanvas();
        if (!info[0].isEmpty()) {
            if (!hasSavedGame()) {
                drawInitInfo("No previous game can be loaded!", 1000);
                return null;
            } else {
                getSavedGame();
                renderFrame();
            }
        } else if (!info[1].isEmpty()) {
            drawInitInfo("Seed: " + info[1]);
            StdDraw.pause(1000);
            randomSeed = Long.parseLong(info[1]);
            random = new Random(randomSeed);
            world = new TETile[WIDTH][HEIGHT];
            fillTileWorldWithNothing();
            randomGenerateWorld();
            renderFrame();
            StdDraw.pause(500);
        } else {
            replayGame();
        }
        // for each action, move the avatar in the direction
        // in replay mode, commandRecord equals to info[2]
        if (!info[2].isEmpty()) {
            if (commandReplayIndex >= 0) {
                commandRecord = new StringBuilder(info[2]);
            }
            for (int i = 0; i < info[2].length();) {
                char c = info[2].charAt(i);
                // if in replay mode, increment commandReplayIndex and don't record, otherwise just append c
                if (commandReplayIndex < 0) {
                    commandRecord.append(c);
                } else {
                    commandReplayIndex += 1;
                }
                if (c == 'G') {
                    randomToggleLight();
                } else if (c == 'H') {
                    toggleAvatarSight();
                } else if (c == 'P') {
                    toggleChasePath();
                } else if (move(c)) {
                    return world;
                }
                i = Math.max(i + 1, commandReplayIndex);
                StdDraw.pause(300);
            }
        }

        // save game
        if (!info[3].isEmpty()) {
            drawMessage(":Q", 500);
            saveGame();
            flashMessage("Game was Saved!", 1000);
        }
        return world;
    }

    private void replayGame() {
        getSavedGame();
        commandReplayIndex = 0;
        interactWithInputString("N" + randomSeed + "S" + commandRecord.toString());
        commandReplayIndex = -1;
    }
    private boolean move(char c) {
        // if game ended, namely lost all lives or success entering the door, return true, otherwise return false
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
        Position nextAvatarPos = new Position(avatar, dx, dy);
        if (nextAvatarPos.equal(door)) {
            congratulations();
            return true;
        }

        if (world[nextAvatarPos.x][nextAvatarPos.y].equal(Tileset.WALL)) {
            nextAvatarPos = avatar;
        } else {
            boolean meet = false;
            for (int i = 0; i < treasures.size(); i++) {
                if (nextAvatarPos.equal(treasures.get(i))) {
                    meet = true;
//                    meetItem(nextAvatarPos, Tileset.TREASURE, "You found a treasure! Add live once!");
//                    addLiveOnce();
//                    drawTile(treasures.get(i));
                    treasures.set(i, treasures.get(treasures.size() - 1));
                    break;
                }
            }
            if (meet) {
                treasures = new ArrayList<>(treasures.subList(0, treasures.size() - 1));
                coinsCollection();
                commandReplayIndex += 1;
                renderFrame();
            }
        }

        int countGuardiansMeet = 0;
        for (int i = 0; i < guardians.size() - countGuardiansMeet; i++) {
            Position guardian = guardians.get(i);
            Position nextGuardianPos = guardiansChasePaths.get(i).get(0);
            if (nextAvatarPos.equal(guardian) || nextGuardianPos.equal(nextAvatarPos)) {
                countGuardiansMeet += 1;
                if (lives < countGuardiansMeet) {
                    lives -= countGuardiansMeet;
                    // game is over, flash the message "Game Over!"
                    flashMessage("Game Over!", 1000);
                    clearMap();
                    drawInitInfo("Game Over!", 2000);
                    return true;
                }
                drawTile(guardian);
                guardians.set(i, guardians.get(guardians.size() - countGuardiansMeet));
                guardiansChasePaths.set(i, guardiansChasePaths.get(guardiansChasePaths.size() - countGuardiansMeet));
                i -= 1;
            }
        }
        if (countGuardiansMeet > 0) {
            meetItem(avatar, Tileset.AVATAR, String.format("You meet %d Guardians! Lost %d lives!", countGuardiansMeet, countGuardiansMeet));
            lostLiveKTimes(countGuardiansMeet);
            guardians = new ArrayList<>(guardians.subList(0, guardians.size() - countGuardiansMeet));
        }

        // update canvas
        // update guardians and avatar position and show
        clearMovableItems();
        avatar = nextAvatarPos;
        for (int i = 0; i < guardians.size(); i++) {
            guardians.set(i, guardiansChasePaths.get(i).get(0));
        }
        if (avatarSightOnly) {
            renderAvatarSightOnly();
        }
        // update chase paths
        if (chasePathDisplay) {
            clearChasePaths();
        }
        updateChasePaths();
        if (chasePathDisplay) {
            showChasePaths();
        }
        showMovableItems();
        StdDraw.show();
        return false;
    }
    private void coinsCollection() {
        clearCanvas();
        drawInitInfo("You have 10 seconds to collect all of the coins! Good luck!", 2000);
        int w = 20;
        int h = 10;
        int curXOffset = WIDTH / 2 - w / 2;
        int curYOffset = YOFFSET + 1;
        TETile coinTile = Tileset.LIGHT;
        TETile curFloorTile = Tileset.FLOOR.changeBackgroundColor(new Color(62, 78, 240));
        TETile curWall = Tileset.WALL;
        TETile[][] coinsCollectWorld = new TETile[w][h];
        int coinsTotal = 5;
        int coinsCollected = 0;
        // draw walls and floors
        StdDraw.setFont(TILEFONT);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                coinsCollectWorld[i][j] = curFloorTile;
                curFloorTile.draw(i + curXOffset, j + curYOffset);
            }
            curWall.draw(i + curXOffset, -1 + curYOffset);
            curWall.draw(i + curXOffset, h + curYOffset);
        }
        for (int j = -1; j <= h; j++) {
            curWall.draw(-1 + curXOffset, j + curYOffset);
            curWall.draw(w + curXOffset, j + curYOffset);
        }
        List<Position> coins = randomSelectMultiPos(coinsCollectWorld, coinsTotal + 1);
        for (Position coin: coins) {
            coinsCollectWorld[coin.x][coin.y] = coinTile;
            coinTile.draw(coin.x + curXOffset, coin.y + curYOffset);
        }
        Position curAvatar = coins.get(0);
        Tileset.AVATAR.draw(curAvatar.x + curXOffset, curAvatar.y + curYOffset);
        StdDraw.show();
        final boolean[] finished = {false};
        if (commandReplayIndex < 0) {
            Timer timer = new Timer(true);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    finished[0] = true;
                }
            };
            timer.schedule(task, 10000);
            while (!finished[0]) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                    Position nextAvatarPos;
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
                    nextAvatarPos = new Position(curAvatar, dx, dy);
                    if (dx == 0 && dy == 0 || !validPosition(nextAvatarPos, w, h)) {
                        continue;
                    }
                    commandRecord.append(c);
                    if (coinsCollectWorld[nextAvatarPos.x][nextAvatarPos.y].equal(coinTile)) {
                        coinsCollected += 1;
                        if (coinsCollected == coinsTotal) {
                            finished[0] = true;
                        }
                    }
                    coinsCollectWorld[curAvatar.x][curAvatar.y] = curFloorTile;
                    curFloorTile.draw(curAvatar.x + curXOffset, curAvatar.y + curYOffset);
                    curAvatar = nextAvatarPos;
                    coinsCollectWorld[curAvatar.x][curAvatar.y] = Tileset.AVATAR;
                    Tileset.AVATAR.draw(curAvatar.x + curXOffset, curAvatar.y + curYOffset);
                    StdDraw.show();
                }
            }
            // add symbol '$' indicate that operations in coinCollection end
            commandRecord.append('$');
        } else {
            char c = commandRecord.charAt(commandReplayIndex);
            while (c != '$') {
                commandReplayIndex += 1;
                Position nextAvatarPos;
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
                nextAvatarPos = new Position(curAvatar, dx, dy);
                c = commandRecord.charAt(commandReplayIndex);
                if (dx == 0 && dy == 0 || !validPosition(nextAvatarPos, w, h)) {
                    continue;
                }
                if (coinsCollectWorld[nextAvatarPos.x][nextAvatarPos.y].equal(coinTile)) {
                    coinsCollected += 1;
                }
                coinsCollectWorld[curAvatar.x][curAvatar.y] = curFloorTile;
                curFloorTile.draw(curAvatar.x + curXOffset, curAvatar.y + curYOffset);
                curAvatar = nextAvatarPos;
                coinsCollectWorld[curAvatar.x][curAvatar.y] = Tileset.AVATAR;
                Tileset.AVATAR.draw(curAvatar.x + curXOffset, curAvatar.y + curYOffset);
                StdDraw.show(300);
            }
        }
        clearCanvas();
        if (coinsCollected >= coinsTotal) {
            lives += 1;
            drawInitInfo("You got all of the coins! Nice work! Add live once!");
        } else {
            drawInitInfo("Oops! You lost a change to gain an extra live!");
        }
        StdDraw.pause(1000);
    }
    private void clearMovableItems() {
        drawTile(avatar);
        for (Position guardian: guardians) {
            drawTile(guardian);
        }
        for (Position treasure: treasures) {
            drawTile(treasure);
        }
    }


    private void updateChasePaths() {
        // start from avatar, use bfs to find the minimum path to every guardian
        guardiansChasePaths = new ArrayList<>();
        HashMap<Integer, Position> prePos = new HashMap<>();
        Queue<Position> toVisit = new LinkedList<>();
        prePos.put(avatar.val, null);
        toVisit.add(avatar);
        int guardiansFound = 0;
        while (guardiansFound < guardians.size()) {
            Position curPos = toVisit.poll();
            for (int i = 0; i < 4; i++) {
                assert curPos != null;
                Position nextPos = new Position(curPos, dir[i][0], dir[i][1]);
                if (!validPosition(nextPos, WIDTH, HEIGHT)) {
                    continue;
                }
                if (!prePos.containsKey(nextPos.val) && !world[nextPos.x][nextPos.y].equal(Tileset.WALL)) {
                    prePos.put(nextPos.val, curPos);
                    toVisit.add(nextPos);
                    for (Position guardian: guardians) {
                        if (guardian.equal(nextPos)) {
                            guardiansFound += 1;
                        }
                    }
                }
            }
        }
        for (Position guardian: guardians) {
            List<Position> path = new ArrayList<>();
            Position p = prePos.get(guardian.val);
            while (p != null) {
                path.add(p);
                p = prePos.get(p.val);
            }
            guardiansChasePaths.add(path);
        }
    }
    private boolean validPosition(Position p, int width, int height) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }
    private void clearChasePaths() {
        for (List<Position> path: guardiansChasePaths) {
            for (Position p: path) {
                if (!avatarSightOnly || inAvatarSight(p)) {
                    drawTile(p);
                }
            }
        }
        showMovableItems();
        StdDraw.show();
    }

    private void showChasePaths() {
        StdDraw.setFont();
        StdDraw.setPenColor(Color.RED);
        for (List<Position> path: guardiansChasePaths) {
            for (Position p : path) {
                if (!avatarSightOnly || inAvatarSight(p)) {
                    StdDraw.text(p.x + XOFFSET + 0.5, p.y + YOFFSET + 0.5, "•");
                }
            }
        }
        // redraw avatar since avatar tile has been drawn text "-" previously.
        drawTile(avatar);
        StdDraw.show();
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
        StdDraw.text(MESSAGEPOS.x, MESSAGEPOS.y, message);
    }
//    public void clearMessage() {
//        StdDraw.setPenColor(BACKGROUND);
//        StdDraw.filledRectangle(WIDTH / 2.0 + XOFFSET, YOFFSET + HEIGHT + YDOWNSET / 2.0, 30, 1);
//        StdDraw.show();
//    }

    private void meetItem(Position p, TETile tile, String s) {
        drawMessage(s);
        flashTile(p, tile, 600, 200);
        clearMessage();
    }

    // flash between Tile NOTHING and tile
    private void flashTile(Position pos, TETile tile, int flashTime, int flashBreak) {
        StdDraw.setFont(TILEFONT);
        while (flashTime >= 0) {
            drawTile(Tileset.NOTHING, pos);
            StdDraw.show();
            StdDraw.pause(flashBreak);
            drawTile(tile, pos);
            StdDraw.show();
            StdDraw.pause(flashBreak);
            flashTime -= flashBreak;
        }
    }

    private void congratulations() {
        drawTile(avatar.x, avatar.y);
        avatar = door;
        world[door.x][door.y] = Tileset.UNLOCKED_DOOR;
        drawTile(door.x, door.y);
        flashMessage("Congratulations!", 1000);
        StdDraw.show();
    }
}
