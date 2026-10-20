package tech.sebazcrc.permadeath.util.calc;

/**
 * Calculador de dominio puro para la probabilidad y requerimientos de tótem de la inmortalidad.
 * Desacoplado de Bukkit para permitir testing unitario exhaustivo.
 */
public final class TotemFailCalculator {

    private TotemFailCalculator() {}

    /**
     * Obtiene la cantidad de tótems consumidos/requeridos al morir según el día.
     * Día 1-39: 1 tótem.
     * Día 40-59: 2 tótems.
     * Día 60+: 3 tótems.
     */
    public static int getRequiredTotems(long day) {
        if (day < 40) {
            return 1;
        } else if (day < 60) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Ajusta la probabilidad de fallo dentro del rango válido de Permadeath [1, 100].
     */
    public static int clampProbability(int failProb) {
        if (failProb >= 101) return 100;
        if (failProb < 0) return 1;
        return failProb;
    }

    /**
     * Determina si el tótem falla dado un porcentaje de fallo y una tirada aleatoria (1-100).
     *
     * @param failProb Probabilidad de fallo configurada (ej: 30 para 30%)
     * @param randomRoll Valor obtenido (1 al 100)
     * @return true si el tótem falla, false si salva al jugador
     */
    public static boolean rollsFailure(int failProb, int randomRoll) {
        int clamped = clampProbability(failProb);
        if (clamped == 100) return true;
        int threshold = 100 - clamped;
        return randomRoll > threshold;
    }

    /**
     * Calcula el valor de visualización ajustado para el mensaje de broadcast.
     */
    public static int calculateDisplayRoll(int failProb, int randomRoll, boolean failed) {
        int clamped = clampProbability(failProb);
        int threshold = 100 - clamped;
        if (failed) {
            int toShow = threshold;
            if (threshold == randomRoll) toShow--;
            return toShow;
        } else {
            int raShow = randomRoll;
            if (randomRoll == threshold) raShow--;
            return raShow;
        }
    }
}
