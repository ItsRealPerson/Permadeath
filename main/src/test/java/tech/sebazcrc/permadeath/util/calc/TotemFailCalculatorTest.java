package tech.sebazcrc.permadeath.util.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TotemFailCalculatorTest {

    @Test
    @DisplayName("Tótems requeridos según época/día")
    void testRequiredTotems() {
        // Día 1 al 39: 1 tótem
        assertEquals(1, TotemFailCalculator.getRequiredTotems(1));
        assertEquals(1, TotemFailCalculator.getRequiredTotems(25));
        assertEquals(1, TotemFailCalculator.getRequiredTotems(39));

        // Día 40 al 59: 2 tótems
        assertEquals(2, TotemFailCalculator.getRequiredTotems(40));
        assertEquals(2, TotemFailCalculator.getRequiredTotems(59));

        // Día 60+: 3 tótems
        assertEquals(3, TotemFailCalculator.getRequiredTotems(60));
        assertEquals(3, TotemFailCalculator.getRequiredTotems(100));
    }

    @Test
    @DisplayName("Ajuste de límites de probabilidad [1, 100]")
    void testClampProbability() {
        assertEquals(1, TotemFailCalculator.clampProbability(-10));
        assertEquals(0, TotemFailCalculator.clampProbability(0)); // 0% de probabilidad es válido
        assertEquals(50, TotemFailCalculator.clampProbability(50));
        assertEquals(100, TotemFailCalculator.clampProbability(100));
        assertEquals(100, TotemFailCalculator.clampProbability(150));
    }

    @Test
    @DisplayName("Evaluación de fallo con 100% de probabilidad siempre falla")
    void testHundredPercentFails() {
        assertTrue(TotemFailCalculator.rollsFailure(100, 1));
        assertTrue(TotemFailCalculator.rollsFailure(100, 50));
        assertTrue(TotemFailCalculator.rollsFailure(100, 100));
    }

    @Test
    @DisplayName("Evaluación de tiradas con probabilidad del 30%")
    void testThirtyPercentProbability() {
        // threshold = 100 - 30 = 70.
        // randomRoll > 70 falla (71 al 100 = 30 valores = 30%)
        // randomRoll <= 70 salva (1 al 70 = 70 valores = 70%)
        assertFalse(TotemFailCalculator.rollsFailure(30, 70));
        assertFalse(TotemFailCalculator.rollsFailure(30, 1));
        assertTrue(TotemFailCalculator.rollsFailure(30, 71));
        assertTrue(TotemFailCalculator.rollsFailure(30, 100));
    }

    @Test
    @DisplayName("Ajuste visual del número mostrado al jugador para evitar confusión si coincide con el umbral")
    void testDisplayRollAdjustment() {
        // failProb 30 -> threshold 70
        // Si falló y el roll fue 70, toShow se decrementa a 69
        int adjustedFail = TotemFailCalculator.calculateDisplayRoll(30, 70, true);
        assertEquals(69, adjustedFail);

        // Si tuvo éxito y el roll fue 70, raShow se decrementa a 69
        int adjustedSuccess = TotemFailCalculator.calculateDisplayRoll(30, 70, false);
        assertEquals(69, adjustedSuccess);

        // Si no coincide, permanece idéntico
        assertEquals(70, TotemFailCalculator.calculateDisplayRoll(30, 75, true));
        assertEquals(65, TotemFailCalculator.calculateDisplayRoll(30, 65, false));
    }
}
