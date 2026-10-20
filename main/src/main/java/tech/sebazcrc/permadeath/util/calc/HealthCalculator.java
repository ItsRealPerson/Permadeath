package tech.sebazcrc.permadeath.util.calc;

/**
 * Calculador de dominio puro para la salud máxima del jugador en Permadeath.
 * Totalmente desacoplado de Bukkit y sin efectos secundarios.
 */
public final class HealthCalculator {

    public static final double BASE_HEALTH = 20.0D;
    public static final double HYPER_APPLE_BONUS = 4.0D;
    public static final double FULL_NETHERITE_BONUS = 8.0D;
    public static final double FULL_INFERNAL_BONUS = 10.0D;
    public static final double DAY_40_PENALTY = 8.0D;
    public static final double DAY_60_PENALTY = 8.0D;
    public static final double ORB_ABSENCE_PENALTY = 16.0D;
    public static final double MIN_HEALTH = 0.000001D;

    private HealthCalculator() {}

    /**
     * Calcula la salud máxima teórica en puntos de HP (2 HP = 1 corazón).
     *
     * @param day Día actual de Permadeath
     * @param hasOrb Si el evento de Orbe está activo o el jugador tiene el Orbe de Vida
     * @param ateHyperOne Si el jugador consumió la primera hiper manzana
     * @param ateHyperTwo Si el jugador consumió la segunda hiper manzana
     * @param netheritePieces Cantidad de piezas de armadura de Netherite equipadas (0-4)
     * @param infernalPieces Cantidad de piezas de armadura de Netherite Infernal equipadas (0-4)
     * @return Salud máxima calculada
     */
    public static double calculateMaxHealth(long day, boolean hasOrb, boolean ateHyperOne, boolean ateHyperTwo, int netheritePieces, int infernalPieces) {
        double maxHealth = BASE_HEALTH;

        if (ateHyperOne) {
            maxHealth += HYPER_APPLE_BONUS;
        }
        if (ateHyperTwo) {
            maxHealth += HYPER_APPLE_BONUS;
        }

        if (netheritePieces >= 4) {
            maxHealth += FULL_NETHERITE_BONUS;
        }

        if (infernalPieces >= 4) {
            maxHealth += FULL_INFERNAL_BONUS;
        }

        if (day >= 40) {
            maxHealth -= DAY_40_PENALTY; // 6 corazones restantes de base

            if (day >= 60) {
                maxHealth -= DAY_60_PENALTY; // 2 corazones restantes de base

                if (!hasOrb) {
                    maxHealth -= ORB_ABSENCE_PENALTY;
                }
            }
        }

        return Math.max(maxHealth, MIN_HEALTH);
    }
}
