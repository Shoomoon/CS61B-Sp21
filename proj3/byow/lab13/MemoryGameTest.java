package byow.lab13;

import edu.princeton.cs.algs4.StdDraw;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryGameTest {
    private MemoryGame game;
    @Before
    public void init() {
        game = new MemoryGame(80, 60, 21154);
    }
    @Test
    public void generateStringTest() {
        for (int i = 1; i < 10; i++) {
            String s = game.generateRandomString(i);
            System.out.print(s + "\n");
            assertEquals(s.length(), i);
        }
    }
    @Test
    public void drawFrameTest() {
        String s = game.generateRandomString(10);
        System.out.print(s);
        game.drawFrame(s, "EncourageMessage");
        StdDraw.pause(5000);
    }
    @Test
    public void flashSequenceTest() {
        String s = game.generateRandomString(10);
        System.out.print(s);
        game.flashSequence(s);
    }
    @Test
    public void solicitNCharsInputTest() {
        int stringLength = 5;
        String s = game.solicitNCharsInput(stringLength);
        System.out.print(s);
    }
    @Test
    public void startGameTest() {
        game.startGame();
    }
}
