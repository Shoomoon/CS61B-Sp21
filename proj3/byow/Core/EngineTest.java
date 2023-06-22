package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {
    Engine engine;
    TETile[][] world;
    String messageLength = "Input argument should not be empty.";
    String messagePattern = "Input argument should start with L/l or N/n + digits + S/s，followed by actions W/wA/aS/sD/d, and maybe end with :Qq" +
            "for example N12335SSSAW:Q, LWSSW:Q, N2464S, N8544S:Q.";
    String messageNoRecord = "No previous game can be loaded!";
    @Before
    public void iniate() {
        engine = new Engine();
        world = new TETile[Engine.WIDTH][Engine.HEIGHT];
    }
    @After
    public void end() {
        StdDraw.pause(5000);
        StdDraw.clear(Color.BLACK);
    }
    @Test
    public void hashMapTest() {
        HashMap<Integer, Integer> prePos = new HashMap<>();
        prePos.put(0, 1);
        prePos.put(1, 1);
        System.out.print(prePos.get(0).equals(prePos.get(1)));
        prePos.put(2, new Integer(1));
        prePos.put(3, new Integer(1));
        System.out.print(prePos.get(2).equals(prePos.get(3)));
    }
    @Test
    public void showTest() {
        engine.fillTileWorldWithNothing();
        engine.ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        StdDraw.setFont(Engine.MESSAGEFONT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2, "Hello World!");
        StdDraw.show(1000);
        StdDraw.pause(1000);
        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2 + 8, "Hello World!");
        StdDraw.show();
    }
    @Test
    public void messageTest() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // first make engine.printMessage public
        // change engine.printMessage to private at the end
        String arg = "N543SWWWWAA";
        engine.main(new String[] {arg});
//        engine.drawMessage("Hello World", 2000);
        engine.clearMessage();
        StdDraw.pause(2000);
//        engine.drawMessage("&&&", 2000);
        engine.clearMessage();
        StdDraw.pause(2000);
    }
    @Test
    public void livesDrawTest() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String arg = "N543SWWWWAA";
        engine.main(new String[] {arg});
    }
    @Test
    public void saveTest() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        world = new TETile[1][1];
        world[0][0] = Tileset.WALL;
        engine.random = new Random(100);
        engine.saveGame();
        Random r = new Random(100);
        for (int i = 0; i < 100; i++) {
            // switch random.nextInt = 0: save and reload
            // 1: nextInt;
            if (engine.random.nextInt(2) == 0) {
                r.nextInt(2);
                engine.saveGame();
                world = engine.getWorld();
                assertEquals(world.length, 1);
                assertEquals(world[0].length, 1);
                assertEquals(world[0][0].character(), '#');
                assertEquals(engine.random.nextInt(215), r.nextInt(215));
            } else {
                r.nextInt(2);
                assertEquals(engine.random.nextInt(546), r.nextInt(546));
            }
        }
    }
    @Test
    public void randomGenerateWorldAndActionTest() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Random rand = new Random(453151489);
        String arg = "N" + rand.nextInt(475753) + "S" + "WDASAA:Q";
        engine.main(new String[] {arg});
        arg = "N543SWWWWAA";
        engine.main(new String[] {arg});
    }
    @Test
    public void reloadAndActionTest() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String arg = "LWASDDDDDD:Q";
        engine.main(new String[]{arg});
    }
    @Test
    public void equalTest() {
        TETile t0 = Tileset.WALL;
        TETile t1 = Tileset.WALL;
        TETile t2 = Tileset.FLOOR;
        assertEquals(true, t0.equal(t1));
        assertEquals(true, t1.equal(t0));
        assertEquals(false, t0.equal(t2));
        assertEquals(false, t2.equal(t0));
    }
    @Test
    public void initialTest() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        engine.clearRecord();
        try {
            String arg13 = "LWWDS:Q";
            engine.getInfo(arg13);
            fail();
        } catch (IllegalCallerException ex) {
            assertEquals(messageNoRecord, ex.getMessage());
        }

        try {
            String arg11 = "WWDS:Q";
            engine.getInfo(arg11);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg10 = "NSWWDS:Q";
            engine.getInfo(arg10);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        engine.random = new Random(100);
        engine.random.nextInt(1546);
        engine.fillTileWorldWithNothing();
        engine.saveGame();

        String arg12 = "LWASD:Q";
        assertDoesNotThrow(() -> engine.getInfo(arg12));
        assertEquals(engine.getInfo(arg12), -1);

        String arg14 = "LWASD:Qwd:Qas";
        assertDoesNotThrow(() -> engine.getInfo(arg14));
        assertEquals(engine.getInfo(arg14), -1);


        String arg15 = "LWASD:Q";
        assertEquals(engine.getInfo(arg15), -1);

        String arg16 = "N1533SWDDSSWAS:Q";
        assertEquals(engine.getInfo(arg16), 1533);


    }
    @Test
    public void argValidateTest() {
        String arg0 = "N347231S";
        assertEquals(engine.getInfo(arg0)[1], "347231");
        String arg1 = "N3428941S";
        assertEquals(engine.getInfo(arg1)[1], "3428941");
        try {
            String arg = null;
            engine.getInfo(arg);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messageLength, ex.getMessage());
        }

        try {
            String arg3 = "";
            engine.getInfo(arg3);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messageLength, ex.getMessage());
        }

        try {
            String arg4 = "n3428941";
            engine.getInfo(arg4);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg5 = "3428941s";
            engine.getInfo(arg5);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg6 = "n3428941f";
            engine.getInfo(arg6);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg7 = "K3428941s";
            engine.getInfo(arg7);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg8 = "K3428941t";
            engine.getInfo(arg8);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        String arg9 = "N817690S:Q";
        assertDoesNotThrow(() -> engine.getInfo(arg9));

        String arg10 = "n817690S";
        assertDoesNotThrow(() -> engine.getInfo(arg10));

        String arg11 = "N817690s";
        assertDoesNotThrow(() -> engine.getInfo(arg11));

        String arg12 = "n817690s";
        assertDoesNotThrow(() -> engine.getInfo(arg12));

    }
}
