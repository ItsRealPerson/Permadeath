package dev.itsrealperson.permadeath;

import dev.itsrealperson.permadeath.util.TextUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test básico para verificar que la lógica de formateo de texto funciona correctamente.
 */
public class TextUtilsTest {

    @Test
    public void testFormat() {
        String input = "&cHola &lMundo";
        String expected = "§cHola §lMundo";
        assertEquals(expected, TextUtils.format(input));
    }

    @Test
    public void testFormatInterval() {
        // 3661 segundos = 1 hora, 1 minuto, 1 segundo
        String result = TextUtils.formatInterval(3661);
        assertTrue(result.contains("01:01:01"));
    }
}
