package dev.itsrealperson.permadeath.api;

import dev.itsrealperson.permadeath.api.storage.PlayerDataStorage;

public interface PermadeathAPIProvider {

    long getDay();

    boolean isOptifineEnabled();

    String getPrefix();

    boolean isExtendedDifficulty();

    /**
     * @return El gestor de módulos activo.
     */
    ModuleManagerAPI getModuleManager();

    /**
     * @return El sistema de persistencia de datos de jugadores.
     */
    PlayerDataStorage getPlayerStorage();

    /**
     * @return El gestor de loot dinámico.
     */
    LootManagerAPI getLootManager();

    /**
     * @return El registro central de ítems.
     */
    ItemRegistryAPI getItemRegistry();

    /**
     * @return El registro de entidades personalizadas.
     */
    EntityRegistryAPI getEntityRegistry();

    /**
     * @return El gestor de red global.
     */
    NetworkManagerAPI getNetworkManager();

    /**
     * @return El controlador de eventos globales.
     */
    EventManagerAPI getEventManager();

    /**
     * @return El gestor de lógica NMS para spawneo de entidades.
     */
    dev.itsrealperson.permadeath.api.interfaces.NMSHandler getNmsHandler();

    /**
     * @return El acceso a propiedades internas NMS de entidades.
     */
    dev.itsrealperson.permadeath.api.interfaces.NMSAccessor getNmsAccessor();

    /**
     * Obtiene una carpeta dedicada para un addon dentro de Permadeath/addons/
     * @param addonName Nombre del addon
     * @return La carpeta del addon
     */
    java.io.File getAddonDataFolder(String addonName);
}
