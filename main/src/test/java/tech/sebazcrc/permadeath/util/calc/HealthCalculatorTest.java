package tech.sebazcrc.permadeath.util.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HealthCalculatorTest {

    @Test
    @DisplayName("Día 1: Salud base sin modificadores es 20.0 (10 corazones)")
    void testBaseHealth() {
        double hp = HealthCalculator.calculateMaxHealth(1, false, false, false, 0, 0);
        assertEquals(20.0D, hp, 0.001);
    }

    @Test
    @DisplayName("Hiper manzanas añaden +4 HP cada una (+2 corazones)")
    void testHyperApples() {
        double hpOne = HealthCalculator.calculateMaxHealth(1, false, true, false, 0, 0);
        assertEquals(24.0D, hpOne, 0.001);

        double hpTwo = HealthCalculator.calculateMaxHealth(1, false, true, true, 0, 0);
        assertEquals(28.0D, hpTwo, 0.001);
    }

    @Test
    @DisplayName("Set completo de Netherite añade +8 HP (+4 corazones)")
    void testNetheriteArmorBonus() {
        // Con menos de 4 piezas no da bonus
        double hpIncomplete = HealthCalculator.calculateMaxHealth(25, false, false, false, 3, 0);
        assertEquals(20.0D, hpIncomplete, 0.001);

        // Con 4 piezas da +8 HP
        double hpFull = HealthCalculator.calculateMaxHealth(25, false, false, false, 4, 0);
        assertEquals(28.0D, hpFull, 0.001);
    }

    @Test
    @DisplayName("Set completo Infernal añade +10 HP (+5 corazones)")
    void testInfernalArmorBonus() {
        double hp = HealthCalculator.calculateMaxHealth(25, false, false, false, 0, 4);
        assertEquals(30.0D, hp, 0.001);
    }

    @Test
    @DisplayName("Día 40 aplica penalización de -8 HP (-4 corazones)")
    void testDay40Penalty() {
        // Base sin nada pasa de 20 a 12 HP (6 corazones)
        double hp = HealthCalculator.calculateMaxHealth(40, false, false, false, 0, 0);
        assertEquals(12.0D, hp, 0.001);

        // Con netherite pasa de 28 a 20 HP (10 corazones)
        double hpNetherite = HealthCalculator.calculateMaxHealth(40, false, false, false, 4, 0);
        assertEquals(20.0D, hpNetherite, 0.001);
    }

    @Test
    @DisplayName("Día 60 con Orbe de Vida aplica penalización acumulada de -16 HP")
    void testDay60WithOrb() {
        // Base 20 - 8 (día 40) - 8 (día 60) = 4 HP (2 corazones)
        double hp = HealthCalculator.calculateMaxHealth(60, true, false, false, 0, 0);
        assertEquals(4.0D, hp, 0.001);

        // Con 2 hiper manzanas + set completo netherite: 20 + 8 + 8 - 16 = 20 HP
        double hpFull = HealthCalculator.calculateMaxHealth(60, true, true, true, 4, 0);
        assertEquals(20.0D, hpFull, 0.001);
    }

    @Test
    @DisplayName("Día 60 sin Orbe aplica penalización letal adicional pero nunca baja del mínimo positivo")
    void testDay60WithoutOrb() {
        // Base 20 - 8 - 8 - 16 = -12 HP -> clamped a MIN_HEALTH
        double hp = HealthCalculator.calculateMaxHealth(60, false, false, false, 0, 0);
        assertEquals(HealthCalculator.MIN_HEALTH, hp, 0.0000001);
        assertTrue(hp > 0, "La vida nunca debe ser cero ni negativa para evitar crash del motor de atributos");
    }
}
