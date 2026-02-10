package dev.itsrealperson.permadeath.api;

public class PermadeathAPI {

    private static PermadeathAPIProvider provider;

    public static long getDay() {
        if (provider == null) {
            return -1;
        }
        return provider.getDay();
    }

    public static String getPrefix() {
        if (provider == null) {
            return "";
        }
        return provider.getPrefix();
    }

    public static boolean optifineItemsEnabled() {
        if (provider == null) {
            return false;
        }
        return provider.isOptifineEnabled();
    }

    public static boolean isExtended() {
        if (provider == null) {
            return false;
        }
        return provider.isExtendedDifficulty();
    }

    public static ModuleManagerAPI getModuleManager() {
        if (provider == null) return null;
        return provider.getModuleManager();
    }

    public static dev.itsrealperson.permadeath.api.storage.PlayerDataStorage getPlayerStorage() {
        if (provider == null) return null;
        return provider.getPlayerStorage();
    }

    public static LootManagerAPI getLootManager() {
        if (provider == null) return null;
        return provider.getLootManager();
    }

    public static ItemRegistryAPI getItemRegistry() {
        if (provider == null) return null;
        return provider.getItemRegistry();
    }

    public static EntityRegistryAPI getEntityRegistry() {
        if (provider == null) return null;
        return provider.getEntityRegistry();
    }

    public static NetworkManagerAPI getNetworkManager() {
        if (provider == null) return null;
        return provider.getNetworkManager();
    }

    public static EventManagerAPI getEventManager() {
        if (provider == null) return null;
        return provider.getEventManager();
    }

    public static dev.itsrealperson.permadeath.api.interfaces.NMSHandler getNmsHandler() {
        if (provider == null) return null;
        return provider.getNmsHandler();
    }

    public static dev.itsrealperson.permadeath.api.interfaces.NMSAccessor getNmsAccessor() {
        if (provider == null) return null;
        return provider.getNmsAccessor();
    }

    /**
     * Obtiene una carpeta dedicada para un addon dentro de Permadeath/addons/
     * @param addonName Nombre del addon
     * @return La carpeta del addon
     */
    public static java.io.File getAddonDataFolder(String addonName) {
        if (provider == null) return null;
        return provider.getAddonDataFolder(addonName);
    }

    public static void setProvider(PermadeathAPIProvider newProvider) {
        if (provider != null) {
            // To prevent multiple initializations, although not strictly necessary.
            return;
        }
        provider = newProvider;
    }
}


