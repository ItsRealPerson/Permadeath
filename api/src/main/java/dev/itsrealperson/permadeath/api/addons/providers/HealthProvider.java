package dev.itsrealperson.permadeath.api.addons.providers;

import org.bukkit.entity.Player;

/**
 * Permite a un addon tomar el control del cálculo de vida máxima.
 */
public interface HealthProvider {
    /**
     * Calcula la vida máxima que debería tener un jugador.
     * @param player El jugador.
     * @param currentDay El día actual del servidor.
     * @param baseHealth La vida base calculada por el Core (opcional usarla).
     * @return La nueva vida máxima (ej: 20.0 para 10 corazones).
     */
    double calculateMaxHealth(Player player, long currentDay, double baseHealth);
}
