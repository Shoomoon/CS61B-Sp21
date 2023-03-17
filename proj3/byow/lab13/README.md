> # Classes and Data Structures
## MemoryGame
MemoryGame is much like the electronic toy Simon, 
but on a computer and with a keyboard instead of
with 4 colored buttons. The goal of the game will
be to type in a randomly generated target string
of characters after it is briefly displayed on
the screen one letter at a time. The target string
starts off as a single letter, but for each successful
string entered, the game gets harder by making the
target string longer.
### Field
1. <u>***private int width***</u>: The width of the window of this game.
2. <u>***private int height***</u>: The height of the window of this game.
3. <u>***private int round***</u>:The current round the user is on.
4. <u>***private Random rand***</u>:The Random object used to randomly generate Strings.
5. <u>***private boolean gameOver***</u>: Whether or not the game is over.
6. <u>***private boolean playerTurn***</u>: Whether or not it is the player's turn.
7. <u>***private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray()***</u>: The characters we
generate random Strings from.
8. <u>***private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
   "You got this!", "You're a star!", "Go Bears!",
   "Too easy for you!", "Wow, so impressive!"}***</u>: Encouraging phrases.

### Method
1. <u>***public static void main(String[] args)***</u>:
2. <u>***public String generateRandomString(int n)***</u>:
3. <u>***public void drawFrame(String centerMessage, String encourage)***</u>:
4. <u>***public void flashSequence(String letters)***</u>:
5. <u>***public String solicitNCharsInput(int n)***</u>: Read n letters of player input
6. <u>***public void startGame()***</u>: The entrance of the game.

> # Algorithms
Randomly generate string by <u>***generateRandomString()***</u>, the length of the string is the same as the round.
Then display each character one by one in the string, and blank the screen between characters by <u>***flashSequence()***</u>. Then on player round,
player should type in same length as the string, ended with "\n". The typed string is read and displayed by <u>***solicitNCharsInput()***</u>.
If the string inputted is the same as displayed, then go to next round, and the length of the string will increase by 1.
Otherwise, the game is over.

***
> # Persistence
Not included in this project.