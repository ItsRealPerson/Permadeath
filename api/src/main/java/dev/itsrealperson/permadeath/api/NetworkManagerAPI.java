package dev.itsrealperson.permadeath.api;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Interfaz para interactuar con la red de servidores de Permadeath.
 */
public interface NetworkManagerAPI {

    /**
     * @return true si el servidor está en modo NETWORK y conectado a Redis.
     */
    boolean isNetworkActive();

    /**
     * Envía un mensaje personalizado a todos los servidores de la red.
     * @param type El tipo de mensaje (ej: "MY_ADDON_EVENT").
     * @param data Los datos en formato String.
     */
    void sendCustomMessage(String type, String data);

    /**
     * Registra un escuchador para mensajes personalizados de la red.
     * @param type El tipo de mensaje a escuchar.
     * @param listener Consumer que recibe (originServerId, data).
     */
    void registerNetworkListener(String type, BiConsumer<String, String> listener);

    /**
     * @return El ID de este servidor (ej: "overworld-1").
     */
    String getServerId();

    /**
     * @return Una colección de todos los jugadores conectados a la red actualmente.
     */
    Collection<dev.itsrealperson.permadeath.api.storage.GlobalPlayer> getGlobalPlayers();
}
