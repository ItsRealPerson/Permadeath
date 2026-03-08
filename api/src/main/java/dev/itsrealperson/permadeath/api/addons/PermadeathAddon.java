package dev.itsrealperson.permadeath.api.addons;

import dev.itsrealperson.permadeath.api.PermadeathAPI;
import java.io.File;

/**
 * Interfaz base que debe implementar cualquier Addon de Permadeath.
 */
public interface PermadeathAddon {

    /**
     * @return El nombre único del addon.
     */
    String getName();

    /**
     * @return La versión del addon.
     */
    String getVersion();

    /**
     * Inicializa el addon con la API de Permadeath.
     */
    void onInitialize(dev.itsrealperson.permadeath.api.PermadeathAPIProvider api);

    /**
     * Se ejecuta cuando el addon es habilitado.
     */
    void onEnable();

    /**
     * Se ejecuta cuando el plugin se desactiva.
     */
    void onDisable();

    /**
     * @return La carpeta de datos dedicada para este addon.
     */
    File getDataFolder();
}