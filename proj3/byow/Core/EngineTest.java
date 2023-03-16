package byow.Core;

import edu.princeton.cs.introcs.StdDraw;
import org.junit.Test;

import java.awt.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {
    @Test
    public void resultTest() {
        Random rand = new Random(453151489);
        for (int i = 0; i < 100; i++) {
            String arg = "N" + rand.nextInt(475754) + "S";
            Engine.main(new String[] {arg});
            StdDraw.pause(1000);
            StdDraw.clear(Color.BLACK);
        }
    }
    @Test
    public void argValidateTest() {
        String arg0 = "N347231S";
        assertEquals(Engine.argsValidate(arg0), 347231);
        String arg1 = "N3428941S";
        assertEquals(Engine.argsValidate(arg1), 3428941);
        String messageLength = "Input argument should not be empty.";
        String messagePattern = "Input argument should be N/n + digits + S/s pattern， for example N12335S, n5785S.";
        try {
            String arg = null;
            Engine.argsValidate(arg);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messageLength, ex.getMessage());
        }

        try {
            String arg3 = "";
            Engine.argsValidate(arg3);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messageLength, ex.getMessage());
        }

        try {
            String arg4 = "n3428941";
            Engine.argsValidate(arg4);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg5 = "3428941s";
            Engine.argsValidate(arg5);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg6 = "n3428941f";
            Engine.argsValidate(arg6);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg7 = "K3428941s";
            Engine.argsValidate(arg7);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        try {
            String arg8 = "K3428941t";
            Engine.argsValidate(arg8);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(messagePattern, ex.getMessage());
        }

        String arg9 = "N817690S";
        assertDoesNotThrow(() -> Engine.argsValidate(arg9));

        String arg10 = "n817690S";
        assertDoesNotThrow(() -> Engine.argsValidate(arg10));

        String arg11 = "N817690s";
        assertDoesNotThrow(() -> Engine.argsValidate(arg11));

        String arg12 = "n817690s";
        assertDoesNotThrow(() -> Engine.argsValidate(arg12));
    }
}
