package dev.itsrealperson.permadeath.api.storage;

import java.util.Collection;
import java.util.Optional;

/**
 * Interfaz para el almacenamiento de datos de jugadores.
 */
public interface PlayerDataStorage {

    /**
     * Inicializa la conexión o el sistema de archivos.
     */
    void init() throws Exception;

    /**
     * Guarda los datos de un jugador.
     */
    void savePlayer(PlayerData data);

    /**
     * Carga los datos de un jugador por su nombre.
     */
    Optional<PlayerData> loadPlayer(String name);

    /**
     * @return Una colección con todos los nombres de jugadores registrados.
     */
    Collection<String> getSavedPlayers();

    /**
     * Cierra las conexiones activas.
     */
    void close();
}
