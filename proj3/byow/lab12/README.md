> # Classes and Data Structures
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
that how many tiles on each side of the Hexagon.
3. <u>***private static Random RANDOM***</u>: The pseudorandom number generator generate
a pseudorandom number based on the random seed.
***
## Hexagon
This class represents a Hexagon which is a basic item used to build up HexWorld.
### Field
1. <u>***private final TETile pattern***</u>: The Tile pattern of the Hexagon.
2. <u>***private int hexagonSize***</u>: The size of the Hexagon.
3. <u>***public static final int PATTERNS = 5***</u>: The total number of Tile patterns of Hexagon.
***
> # Algorithm
<u>***HexWorld***</u> class identified the size of Hexagon and random seed from input arguments,
and pseudorandom generated Hexagons with them. Calculated the width and height of canvas needed
to build up the HexWorld, and initialized the rendering engine. Use class <u>***Hexagon***</u>'s method to draw Hexagon on to
canvas. Finally rendering the canvas and we have the HexWorld.

> # Persistence
Not included in this Phase.