> # Classes and Data Structures
***
## Engine
Engine is a class that implement method
<u>***Core.Engine.interactWithInputSting(String s)***</u> 
and <u>***Core.Engine.interactWithKeyboard()***</u>.
### Fields
1. <u>***TERenderer ter***</u>:
   The render engine of the project.
2. <u>***public static final int WIDTH = 80***</u>;
   The width of the canvas.
3. <u>***public static final int HEIGHT = 80***</u>;
   The height of the canvas.
4. <u>***private static final double ROOMRATIO = 0.01***</u>;
   The probability of being a room for any position (x, y)
5. <u>***Random random***</u>: The pseudorandom number generator generate
   a pseudorandom number based on the given seed. 
6. <u>***private static final int MINROOMWIDTH = 3***</u>;
The minimum width of a room;
7. <u>***private static final int MAXROOMWIDTH = 10***</u>;
   The maximum width of a room;
8. <u>***private static final int MINROOMHEIGHT = 3***</u>;
   The minimum height of a room;
9. <u>***private static final int MAXROOMHEIGHT = 10***</u>;
   The maximum height of a room;
10. <u>***private static final int MINHALLWAYWIDTH = 1***</u>;
    The minimum width of a hallway;
11. <u>***private static final int MAXHALLWAYWIDTH = 2***</u>;
    The maximum width of a hallway;
12. <u>***public static final int XDOWNSET = 0***</u>: The blank tiles on the left.
13. <u>***public static final int YDOWNSET = 4***</u>: The blank tiles on the top.
14. <u>***public static final Font MESSAGEFONT = new Font("Monaco", Font.BOLD, 30)***</u>;
15. <u>***public static final Font INITINFOFONT = new Font("Monaco", Font.BOLD, 30)***</u>: Font of message shown at the center.
16. <u>***public static final Font TILEFONT = new Font("Monaco", Font.BOLD, 14)***</u>: Font of tiles.
17. <u>***public static final File BLOBS = new File(System.getProperty("user.dir"), ".maps")***</u>: The directory to store game record.
18. <u>***public static final File RANDOM = new File(BLOBS, "random.txt")***</u>: The file to store random.
19. <u>***public static final File WORLD = new File(BLOBS, "world.txt")***</u>: The file to store game map.
20. <u>***public static final File PLAYER = new File(BLOBS, "player.txt")***</u>: The file to store Avatar.
21. <u>***public static final File DOOR = new File(BLOBS, "door.txt")***</u>: The file to store door.
22. <u>***public static final File GUARDIANS = new File(BLOBS, "guardians.txt")***</u>: The file to store guardians.
23. <u>***public static final File TREASURES = new File(BLOBS, "treasures.txt")***</u>: The file to store treasures.
24. <u>***public static final Position livesPosStart = new Position(2, HEIGHT + YDOWNSET / 2)***</u>: The start position of lives shown.
25. <u>***public static final int[][] DIR = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}}***</u>;
26. <u>***public static final Color BACKGROUND = Color.BLACK***</u>: The background color of the game.
27. <u>***public static final Color TEXTCOLOR = Color.WHITE***</u>: The text color of the game.
28. <u>***public static final int MAXTREASURES = 4***</u>: Maximum number of treasures.
29. <u>***public static final int MAXGUARDIANS = 8***</u>: Maximum number of guardians.
30. <u>***public static final int MAXINITLIVES = 3***</u>: Maximum number of initial lives.
31. <u>***public static final int MAXLIGHTS = 6***</u>: Maximum number of lights.
32. <u>***public static final int LIGHTRANGE = 6***</u>: The maximum lighting range of the light.
33. <u>***private Position player***</u>: Position of Avatar.
34. <u>***private Position door***</u>: Position of door.
35. <u>***private Position[] treasures***</u>: Position of treasures.
36. <u>***private Position[] guardians***</u>: Position of guardians.
37. <u>***private int lives***</u>: The number of extra lives left.
38. <u>***private boolean enableLights = false***</u>: Turn lights or not, toggled with key 'H'.

### Method
1. <u>***private void randomGenerateWorld(TETile[][] tiles)***</u>:
Randomly generate the world based on the random generator.
The positions and number of rooms, the width and height of the rooms are all random.
Any room will be connected to the nearest room via hallways,
and the width and the height of the hallways are all random too.
2. <u>***private Room selectRoom(Room room, ArrayList<Room> rooms)***</u>:
If the random double number < random_select_room_probability, then choose a random room to connect to.
Otherwise, Select one from rooms which is nearest to room. If there are more than one
nearest room, uniformly select one of them. 
If there is no other room, or the room is overlapped with any room, return null
3. <u>***private Room generateRoom(int i, int j)***</u>: Generate room on position (i, j),
the width and height of the room are both random.
4. <u>***private boolean isARoom(int i, int j)***</u>: Randomly determine if there is a room on position (i, j).
5. <u>***private void fillTileWorldWithNothing(TETile[][] tiles)***</u>: Fill the world map with Tile.NOTHING.
6. <u>***public static int argsValidate(String[] args)***</u>: Validate the input arguments, if valid, capture the seed, otherwise throw exception.

## Room
Class Room represents a room in the World.
### Field
1. <u>***private final int x***</u>: The x coordinate of the bottom-left corner of this room.
2. <u>***private final int y***</u>: The y coordinate of the bottom-left corner of this room.
3. <u>***private final int width***</u>: The width of the room.
4. <u>***private final int height***</u>: The height of the room.
   These fields are all final since those information cannot be modified after the object created.
### Method
1. <u>***public void drawRoom(TETile[][] tiles)***</u>: Draw the room onto the tile world, including floor and wall.
2. <u>***public int distance(Room r)***</u>: Measure the distance to <u>***r***</u>. If has overlap or is adjacent to each other,
   just return -1. If only overlap along x-axis, return the vertical distance.
   If only overlap along y-axis, return horizontal distance. Otherwise, return vertical distance plus horizontal distance.
3. <u>***public void connectWithRoom(TETile[][] tiles, Random random, Room r)***</u>: Connect this to another room <u>***r***</u> via hallway.
   If already connected to each other, do nothing.
   If there are no overlap on both x-axis and y-axis, use a L shape hallway to connect them. Otherwise, use a vertical or horizontal hallway.
   The position, width and height of the hallway are all random, except the height of vertical hallway and width of horizontal hallway.
   Draw the hallway on the tile world at the end.

> # Algorithms
In order to randomly generate the world, iterate the x and y coordinates from left to right, bottom to top.
For each position ***(x, y)***, use ***Engine.isARoom()*** to check if there is enough space to place a room, and use random generate to
 decide if there is a room here. If YES, randomly generate a room by ***Engine.generateRoom()***, and draw the room onto tile world by ***Random.drawRoom***.
Then select the nearest room by ***Engine.selectRoom()***, and connect to it via hallway by ***Room.connectWithRoom()***.

***
> # Persistence
Not included in this Phase.