package tech.sebazcrc.permadeath.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import tech.sebazcrc.permadeath.api.interfaces.DeathModule;
import tech.sebazcrc.permadeath.api.interfaces.InfernalNetheriteBlock;
import tech.sebazcrc.permadeath.api.interfaces.NMSAccessor;
import tech.sebazcrc.permadeath.api.interfaces.NMSHandler;

import java.lang.reflect.InvocationTargetException;

public class NMS {
    @Getter
    private static NMSAccessor accessor;
    @Getter
    private static NMSHandler handler;
    @Getter
    private static InfernalNetheriteBlock netheriteBlock;

    private static Class<?> deathModuleClass;

    // Bloque estático para cargar la clase DeathModule dinámicamente
    static {
        try {
            // Intenta cargar la implementación de DeathModule para la versión actual
            deathModuleClass = Class.forName(search("entity.DeathModuleImpl"));
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Permadeath] No se pudo cargar DeathModuleImpl: " + e.getMessage());
        }
    }

    public static void loadNMSAccessor() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String className = search("NMSAccessorImpl");
        Class<?> clazz = Class.forName(className);
        accessor = (NMSAccessor) clazz.getConstructor().newInstance();
    }

    public static void loadNMSHandler(Plugin plugin) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String className = search("NMSHandlerImpl");
        Bukkit.getLogger().info("[Permadeath] Intentando cargar NMSHandler: " + className);
        Class<?> clazz = Class.forName(className);

        // A partir de la 1.21, pasamos la instancia del plugin para evitar dependencias circulares.
        if (className.contains("v1_21_R3")) {
            handler = (NMSHandler) clazz.getConstructor(Plugin.class).newInstance(plugin);
        } else {
            handler = (NMSHandler) clazz.getConstructor().newInstance();
        }
        Bukkit.getLogger().info("[Permadeath] NMSHandler cargado exitosamente.");
    }

    public static void loadInfernalNetheriteBlock() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String className = search("block.InfernalNetheriteBlockImpl");
        Class<?> clazz = Class.forName(className);
        netheriteBlock = (InfernalNetheriteBlock) clazz.getConstructor().newInstance();
    }

    /**
     * Construye la ruta de la clase basada en la versión actual del servidor.
     * Ejemplo: tech.sebazcrc.permadeath.nms.v1_21_R3.entity.DeathModuleImpl
     */
    public static String search(String classPath) {
        // Asegúrate de que VersionManager.getRev() devuelva algo como "1_21_R3"
        return search(VersionManager.getRev(), classPath);
    }

    public static String search(String rev, String classPath) {
        // Formato: tech.sebazcrc.permadeath.nms.v{VERSION}.{CLASE}
        return String.format("tech.sebazcrc.permadeath.nms.v%s.%s", rev, classPath);
    }

    public static void spawnDeathModule(Location location) {
        if (deathModuleClass == null) {
            Bukkit.getLogger().severe("[Permadeath] Error: DeathModuleImpl no fue cargado correctamente.");
            return;
        }
        try {
            DeathModule module = (DeathModule) deathModuleClass.getConstructor().newInstance();
            module.spawn(location);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}









