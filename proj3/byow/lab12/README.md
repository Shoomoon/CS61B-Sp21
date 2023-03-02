# Classes and Data Structures
***
## HexWorld
HexWorld is a game that will randomly generate a Hexagon based
World map. Hexagon has 5 stiles. The size of Hexagon and random seed
 are input by <u>***args***</u>.
### Fields
1. <u>***private static final int SIZE = 4***</u>: 
The size of HexWorld, which means
how many Hexagons in each side of the Hexagon-shaped world map. 

2. <u>***private static int hexagonSize = 3***</u>: The size of Hexagon, which indicates
that how many tiles on each side of the Hexagon.3. <u>***private static Random RANDOM***</u>: The pseudorandom number generator generate
a pseudorandom number based on the random seed.

3. <u>***public static void main(String[] args)***</u>
This is the entry point to the game. It takes in
arguments <u>***args***</u> from the command line,
pseudorandom generate the HexWorld based 
on the size of Hexagon and random seed input by <u>***args***</u>.

4. <u>***public static int[] captureInput(String[] args)***</u>: 
Take in <u>***args***</u>, and validate it if it matches the input pattern <u>"N/n" + hexagonSize + "S/s" + randomSeed</u>.
capture the size of Hexagon and random seed if it matches.

5. <u>***private static void setHexagonSize(int num)***</u>: 
Set the size of Hexagon.

6. <u>***private static void setRandom(int seed)***</u>: Set the random generator by the random seed.

7. <u>***private static void initialize(TERenderer ter)***</u>: initialize the tile rendering engine with a window of size canvasWidth x canvasHeight.

8. <u>***public static int getCanvasWidth()***</u>: Calculate the width of the canvas.

9. <u>***public static int getCanvasHeight()***</u>:Calculate the height of the canvas.

10. <u>***public static int getHexagonWorldWidth()***</u>: Calculate the width of the HexWorld, namely the number of columns of Hexagons.

11. <u>***public static int getHexagonWorldHeight()***</u>: Calculate the height of the HexWorld, namely the maximum number of rows of Hexagons.

12. <u>***private static Hexagon[][] randomGenerateHexagonWorld()***</u>: Pseudorandom generate Hexagons for each position of the HexWorld.

13. <u>***public static void renderHexWorld(TERenderer ter, Hexagon[][] world)***</u>: Draws the world to the screen

14. <u>***public static int getRowDistance()***</u>: Return the distance between adjacent rows of Hexagons.

15. <u>***public static int getColumnDistance()***</u>: Return the distance between adjacent columns of Hexagons.

16. <u>***private static boolean hexWorldCoordinateValidation(int i, int j)***</u>: Validate if there is a Hexagon on coordinate (i, j).

17. <u>***public static int getHexagonY(int i, int j)***</u>: Return the Y coordinate of the bottom left corner Tile of the Hexagon.

18. <u>***public static int getHexagonX(int i, int j)***</u>: Return the X coordinate of the bottom left corner Tile of the Hexagon.
***
## Hexagon
This class represents a Hexagon which is a basic item used to build up HexWorld.
### Field
1. <u>***private final TETile pattern***</u>: The Tile pattern of the Hexagon.
2. <u>***private int hexagonSize***</u>: The size of the Hexagon
3. <u>***public static final int PATTERNS = 5***</u>: The total number of Tile patterns of Hexagon.
4. <u>***public Hexagon(int size, TETile TETilePattern)***</u>: 
5. <u>***public Hexagon(int size, int patternNum)***</u>
6. <u>***public int getHexagonSize()***</u>
7. <u>***public void drawHexagon(TETile[][] teTileWorld, int x0, int y0)***</u>

