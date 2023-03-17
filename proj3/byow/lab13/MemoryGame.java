package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(80, 60, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.rand = new Random(seed);
        this.round = 0;
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        char[] res = new char[n];
        for (int i = 0; i < n; i++) {
            res[i] = CHARACTERS[this.rand.nextInt(CHARACTERS.length)];
        }
        return String.valueOf(res);
    }

    public void drawFrame(String centerMessage, String encourage) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
        StdDraw.setPenColor(Color.WHITE);
        // draw round
        if (round > 0) {
            StdDraw.text(5, height - 1, "Round: " + round);
            // draw status: Watch! or Type!
            String status = "Watch!";
            if (playerTurn) {
                status = "Type!";
            }
            if (gameOver) {
                status = "Game Over!";
            }
            StdDraw.text(width / 2.0, height - 1, status);
            // draw encourage message
            StdDraw.text(width - 10, height - 1, encourage);
        } else {
            StdDraw.text(width / 2.0, height - 1, "Game Begin!");
        }
        // draw bar
        char[] bar = new char[2 * width];
        Arrays.fill(bar, '-');
        StdDraw.text(width / 2.0, height - 3, String.valueOf(bar));
        // draw message in the center
        double xCenter = width / 2.0;
        double yCenter = height / 2.0;
        StdDraw.text(xCenter, yCenter, centerMessage);

        StdDraw.show();
//        StdDraw.pause(5000);
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        int pauseTextPeriod = 1000;
        int pauseBlankPeriod = 500;
        String encourage = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
        for (char c: letters.toCharArray()) {
            drawFrame(String.valueOf(c), encourage);
            StdDraw.pause(pauseTextPeriod);
            StdDraw.clear(Color.BLACK);
            drawFrame("", encourage);
            StdDraw.pause(pauseBlankPeriod);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String encourage = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
        char[] chars = new char[n];
        for (int i = 0; i < n; i++) {
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    chars[i] = StdDraw.nextKeyTyped();
                    drawFrame(String.valueOf(chars).substring(0, i + 1), encourage);
                    break;
                }
            }
        }
        // return after '\n' is inputted by Player
        while (true) {
            if (StdDraw.hasNextKeyTyped() && StdDraw.nextKeyTyped() == '\n') {
                break;
            }
        }
        return String.valueOf(chars);
    }
    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        gameOver = false;
        playerTurn = false;
        // Show the welcome page
        drawFrame("Welcome to The Game!", "");
        round = 1;
        StdDraw.pause(2000);
        //TODO: Establish Engine loop
        while (!gameOver) {
            drawFrame("Round: " + round, "");
            StdDraw.pause(2000);
            String cur = generateRandomString(round);
            flashSequence(cur);
            playerTurn = true;
            drawFrame("", "");
            String typed = solicitNCharsInput(round);
            if (cur.equals(typed)) {
                round += 1;
                playerTurn = false;
            } else {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + round, "See you next time!");
                StdDraw.pause(2000);
                break;
            }
        }
    }

}
