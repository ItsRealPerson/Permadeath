package dev.itsrealperson.permadeath.api;

/**
 * Interfaz para controlar los eventos globales de Permadeath.
 */
public interface EventManagerAPI {

    /**
     * Inicia un Death Train manualmente.
     * @param durationTicks Duración en ticks.
     */
    void startDeathTrain(int durationTicks);

    /**
     * @return true si el evento de Life Orb está actualmente activo.
     */
    boolean isLifeOrbActive();

    /**
     * Activa o desactiva el estado del Life Orb.
     */
    void setLifeOrbActive(boolean active);
}
