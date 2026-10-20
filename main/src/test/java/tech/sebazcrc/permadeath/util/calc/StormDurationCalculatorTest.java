package tech.sebazcrc.permadeath.util.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StormDurationCalculatorTest {

    @Test
    @DisplayName("Día 1 al 24: duración lineal de 1 hora por día")
    void testEarlyDays() {
        assertEquals(3600L, StormDurationCalculator.calculateStormSeconds(1));
        assertEquals(1L, StormDurationCalculator.calculateStormHours(1));

        assertEquals(24L * 3600L, StormDurationCalculator.calculateStormSeconds(24));
        assertEquals(24L, StormDurationCalculator.calculateStormHours(24));
    }

    @Test
    @DisplayName("Día 25 al 49: reinicio relativo desde día 24 (day - 24 horas)")
    void testDeathTrainEra() {
        // Día 25: 25 - 24 = 1 hora
        assertEquals(3600L, StormDurationCalculator.calculateStormSeconds(25));
        assertEquals(1L, StormDurationCalculator.calculateStormHours(25));

        // Día 40: 40 - 24 = 16 horas
        assertEquals(16L * 3600L, StormDurationCalculator.calculateStormSeconds(40));
        assertEquals(16L, StormDurationCalculator.calculateStormHours(40));
    }

    @Test
    @DisplayName("Día 50: media hora (1800 segundos)")
    void testDay50Special() {
        assertEquals(1800L, StormDurationCalculator.calculateStormSeconds(50));
        assertEquals(0L, StormDurationCalculator.calculateStormHours(50)); // En enteros 1800 / 3600 = 0
    }

    @Test
    @DisplayName("Día 51 al 74: escalado a media hora por día transcurrido desde día 49")
    void testDay51To74() {
        // Día 51: (51 - 49) * 3600 / 2 = 3600 segundos (1 hora)
        assertEquals(3600L, StormDurationCalculator.calculateStormSeconds(51));
        assertEquals(1L, StormDurationCalculator.calculateStormHours(51));

        // Día 60: (60 - 49) * 1800 = 11 * 1800 = 19800 segundos
        assertEquals(11L * 1800L, StormDurationCalculator.calculateStormSeconds(60));
        assertEquals(5L, StormDurationCalculator.calculateStormHours(60)); // 19800 / 3600 = 5 horas
    }

    @Test
    @DisplayName("Conversión exacta a game ticks de Minecraft (20 ticks por segundo)")
    void testMinecraftTicks() {
        assertEquals(3600L * 20L, StormDurationCalculator.calculateMinecraftTicks(1));
        assertEquals(1800L * 20L, StormDurationCalculator.calculateMinecraftTicks(50));
    }
}
