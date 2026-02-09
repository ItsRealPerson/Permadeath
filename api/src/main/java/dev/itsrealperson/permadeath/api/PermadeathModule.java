package dev.itsrealperson.permadeath.api;

/**
 * Representa un módulo independiente del sistema Permadeath.
 * Permite separar la lógica del Main.java y gestionar el ciclo de vida de cada mecánica.
 */
public interface PermadeathModule {

    /**
     * @return El nombre identificador del módulo.
     */
    String getName();

    /**
     * Se ejecuta al activar el módulo (normalmente en el onEnable del plugin).
     */
    void onEnable();

    /**
     * Se ejecuta al desactivar el módulo (normalmente en el onDisable del plugin).
     */
    void onDisable();

    /**
     * Se ejecuta opcionalmente en cada tick o ciclo de actualización definido.
     */
    default void onTick() {}
}
