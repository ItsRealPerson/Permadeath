package tech.sebazcrc.permadeath.util.calc;

/**
 * Calculador de dominio puro para la duración de las tormentas añadidas por muerte de jugadores.
 * Desacoplado de la API de Bukkit para permitir testing unitario exhaustivo.
 */
public final class StormDurationCalculator {

    private StormDurationCalculator() {}

    /**
     * Calcula los segundos base de tormenta añadidos por muerte según el día de Permadeath.
     * (Nota: en la implementación histórica de Permadeath, 'stormTicks' representa segundos,
     * los cuales se multiplican por 20 para obtener los game ticks del mundo de Minecraft).
     *
     * @param day Día actual de Permadeath
     * @return Segundos base de tormenta
     */
    public static long calculateStormSeconds(long day) {
        if (day <= 0) {
            return 0;
        }
        if (day <= 24) {
            return day * 3600L;
        }
        if (day < 50) {
            long define = day - 24;
            return define * 3600L;
        }
        if (day == 50) {
            return 3600L / 2;
        }
        if (day < 75) {
            long define = day - 49;
            return (define * 3600L) / 2;
        }
        return 0;
    }

    /**
     * Calcula las horas completas equivalentes de tormenta.
     */
    public static long calculateStormHours(long day) {
        return calculateStormSeconds(day) / 3600L;
    }

    /**
     * Calcula los ticks reales de Minecraft (20 ticks por segundo).
     */
    public static long calculateMinecraftTicks(long day) {
        return calculateStormSeconds(day) * 20L;
    }
}
