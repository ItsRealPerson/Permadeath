package dev.itsrealperson.permadeath;

import com.github.retrooper.packetevents.PacketEvents;
import dev.itsrealperson.permadeath.api.PermadeathAPI;
import dev.itsrealperson.permadeath.api.PermadeathAPIProvider;
import dev.itsrealperson.permadeath.api.interfaces.InfernalNetheriteBlock;
import dev.itsrealperson.permadeath.api.interfaces.NMSAccessor;
import dev.itsrealperson.permadeath.api.interfaces.NMSHandler;
import dev.itsrealperson.permadeath.data.*;
import dev.itsrealperson.permadeath.discord.DiscordPortal;
import dev.itsrealperson.permadeath.end.EndManager;
import dev.itsrealperson.permadeath.event.HostileEntityListener;
import dev.itsrealperson.permadeath.event.block.BlockListener;
import dev.itsrealperson.permadeath.event.entity.EntityEvents;
import dev.itsrealperson.permadeath.event.entity.SpawnListener;
import dev.itsrealperson.permadeath.event.entity.TotemListener;
import dev.itsrealperson.permadeath.event.paper.PaperListeners;
import dev.itsrealperson.permadeath.event.player.AnvilListener;
import dev.itsrealperson.permadeath.event.player.PlayerListener;
import dev.itsrealperson.permadeath.event.player.SlotBlockListener;
import dev.itsrealperson.permadeath.event.raid.RaidEvents;
import dev.itsrealperson.permadeath.event.world.WorldEvents;
import dev.itsrealperson.permadeath.task.EndTask;
import dev.itsrealperson.permadeath.util.*;
import dev.itsrealperson.permadeath.util.events.LifeOrbEvent;
import dev.itsrealperson.permadeath.util.events.ShellEvent;
import dev.itsrealperson.permadeath.util.inventory.AccessoryListener;
import dev.itsrealperson.permadeath.util.item.RecipeManager;
import dev.itsrealperson.permadeath.util.lib.UpdateChecker;
import dev.itsrealperson.permadeath.util.log.Log4JFilter;
import dev.itsrealperson.permadeath.util.log.PDCLog;
import dev.itsrealperson.permadeath.util.mob.CustomSkeletons;
import dev.itsrealperson.permadeath.world.abyss.AbyssManager;
import dev.itsrealperson.permadeath.world.beginning.BeginningManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.apache.logging.log4j.LogManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends JavaPlugin implements Listener, PermadeathAPIProvider {

    private static final int CURRENT_CONFIG_VERSION = 3;
    public static boolean DEBUG = false;
    public static boolean SPEED_RUN_MODE = false;
    public static boolean OPTIMIZE_SPAWNS = false;
    public static boolean PANIC_MODE = false; // Nuevo: Detiene procesos críticos
    public static Main instance;
    public static String prefix = "";
    public static boolean runningPaperSpigot = false;
    public static boolean runningFolia = false;
    public World world = null;
    public World endWorld = null;
    private int playTime = 0;
    public HostileEntityListener hostile;
    public RecipeManager recipes;
    public Messages messages;
    public EndTask task = null;
    public EndManager endManager;
    public MobFactory factory;
    public BeginningManager begginingManager;
    public BeginningDataManager beData;
    public EndDataManager endData;
    public AbyssManager abyssManager;
    public dev.itsrealperson.permadeath.util.BackupManager backupManager;
    public dev.itsrealperson.permadeath.util.ResetManager resetManager;
    public dev.itsrealperson.permadeath.util.GameRuleManager gameRuleManager;
    private Map<Integer, Boolean> registeredDays = new HashMap<>();
    private ArrayList<Player> doneEffectPlayers = new ArrayList<>();
    private boolean loaded = false;
    private boolean alreadyRegisteredChanges = false;
    public ShellEvent shulkerEvent;
    public LifeOrbEvent orbEvent;
    public SpawnListener spawnListener;
    private int deathTrainVersion = 0;
    private dev.itsrealperson.permadeath.util.LootManager lootManager;
    private dev.itsrealperson.permadeath.util.NetworkManager networkManager;
    private dev.itsrealperson.permadeath.util.ShardManager shardManager;
    private FileConfiguration abyssConfig;
    private File abyssFile;
    private ModuleManager moduleManager;
    private dev.itsrealperson.permadeath.util.ConfigManager configManager;
    private dev.itsrealperson.permadeath.api.storage.PlayerDataStorage playerStorage;
    private dev.itsrealperson.permadeath.command.CommandManager commandManager;
    private dev.itsrealperson.permadeath.api.EventManagerAPI eventManager;

    private dev.itsrealperson.permadeath.util.item.ItemRegistryImpl itemRegistry;
    private dev.itsrealperson.permadeath.util.entity.EntityRegistryImpl entityRegistry;
    private dev.itsrealperson.permadeath.util.placeholder.PlaceholderManager placeholderManager;

    public dev.itsrealperson.permadeath.util.ConfigManager getPdcConfigManager() {
        return configManager;
    }

    @Override
    public dev.itsrealperson.permadeath.api.storage.PlayerDataStorage getPlayerStorage() {
        return playerStorage;
    }

    @Override
    public dev.itsrealperson.permadeath.api.ModuleManagerAPI getModuleManager() {
        return moduleManager;
    }

    @Override
    public dev.itsrealperson.permadeath.api.LootManagerAPI getLootManager() {
        return lootManager;
    }

    @Override
    public dev.itsrealperson.permadeath.api.ItemRegistryAPI getItemRegistry() {
        return itemRegistry;
    }

    @Override
    public dev.itsrealperson.permadeath.api.EntityRegistryAPI getEntityRegistry() {
        return entityRegistry;
    }

    @Override
    public dev.itsrealperson.permadeath.api.EventManagerAPI getEventManager() {
        return eventManager;
    }

    public FileConfiguration getAbyssConfig() {
        return configManager.getConfig("data/abyss.yml");
    }
    private long weatherDurationCache = 0;

    public void incrementDeathTrainVersion() { this.deathTrainVersion++; }
    public int getDeathTrainVersion() { return deathTrainVersion; }
    public long getWeatherDuration() { return weatherDurationCache; }

    public static boolean optifineItemsEnabled() {
        if (instance == null) return false;
        return instance.getConfig().getBoolean("Toggles.OptifineItems");
    }

    // Interface implementation
    @Override
    public boolean isOptifineEnabled() {
        return Main.optifineItemsEnabled();
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public long getDay() {
        return DateManager.getInstance().getDay();
    }

    public void setupFoliaWorldConfig(org.bukkit.command.CommandSender sender) {
        if (!runningFolia) {
            sender.sendMessage(ChatColor.RED + "Este comando solo es necesario en Folia.");
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "Configurando parámetros de mundo para Folia...");
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            sender.sendMessage(ChatColor.GREEN + "Mundo " + world.getName() + " configurado.");
        }
    }

    @Override
    public boolean isExtendedDifficulty() {
        // Solo puede estar activa si el toggle global está en true Y el corazón ha sido usado
        if (!getConfig().getBoolean("Toggles.ExtendToDay90")) return false;
        return getConfig().getBoolean("DontTouch.ExtendedDifficultyActive", false);
    }

    @Override
    public void onLoad() {
        instance = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false);
        PacketEvents.getAPI().load();

        this.abyssManager = new AbyssManager(this);

        try {
            NMS.loadInfernalNetheriteBlock();
            NMS.loadNMSAccessor();
            NMS.loadNMSHandler(this);

            getNmsAccessor().registerHostileMobs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        
        // 1. Asegurar carpeta y config base (Imprescindible para que nada sea null)
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        
        // Inicializar Gestor de Configuraciones
        this.configManager = new dev.itsrealperson.permadeath.util.ConfigManager(this);
        configManager.registerConfig("data/abyss.yml");
        configManager.registerConfig("data/discord.yml");
        configManager.registerConfig("data/loot.yml");
        configManager.registerConfig("data/sharding.yml");
        
        this.saveDefaultConfig();
        setupConsoleFilter();
        
        PacketEvents.getAPI().init();

        this.moduleManager = new ModuleManager(this);
        this.eventManager = new dev.itsrealperson.permadeath.event.impl.EventManagerImpl(this);
        this.itemRegistry = new dev.itsrealperson.permadeath.util.item.ItemRegistryImpl();
        this.entityRegistry = new dev.itsrealperson.permadeath.util.entity.EntityRegistryImpl();
        this.placeholderManager = new dev.itsrealperson.permadeath.util.placeholder.PlaceholderManager(this);

        // 2. Inicializar almacenamiento
        setupStorage();

        // 3. Inicializar gestores de datos
        this.beData = new BeginningDataManager(this);
        this.endData = new EndDataManager(this);

        // 4. Registrar módulos
        // Solo puede estar activa si el toggle global está en true Y el corazón ha sido usado
        if (getConfig().getBoolean("Toggles.Abyss-World-Active") && getConfig().getBoolean("DontTouch.HeartUsed")) {
            this.abyssManager = new AbyssManager(this);
            this.moduleManager.registerModule(this.abyssManager);
        }

        this.begginingManager = new BeginningManager(this);
        this.moduleManager.registerModule(this.begginingManager);

        this.moduleManager.registerModule(new dev.itsrealperson.permadeath.world.EnvironmentModule(this));
        this.moduleManager.registerModule(new dev.itsrealperson.permadeath.world.CatastropheModule(this));
        this.moduleManager.registerModule(new dev.itsrealperson.permadeath.HealthModule(this));
        this.moduleManager.registerModule(new dev.itsrealperson.permadeath.InterfaceModule(this));
        this.moduleManager.registerModule(new dev.itsrealperson.permadeath.world.MobModule(this));
        this.moduleManager.registerModule(new dev.itsrealperson.permadeath.world.EventModule(this));

        // 5. Gestores de utilidad
        this.backupManager = new dev.itsrealperson.permadeath.util.BackupManager(this);
        this.resetManager = new dev.itsrealperson.permadeath.util.ResetManager(this);
        this.gameRuleManager = new dev.itsrealperson.permadeath.util.GameRuleManager(this);
        this.networkManager = new dev.itsrealperson.permadeath.util.NetworkManager(this);
        this.networkManager.init();
        this.shardManager = new dev.itsrealperson.permadeath.util.ShardManager(this);
        
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (this.networkManager.isNetworkActive()) {
            getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.event.NetworkListener(this), this);
        }

        if (getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.util.hook.MythicMobsHook(this), this);
            getLogger().info("Hook de MythicMobs (5.6.1) activado: Los mobs de terceros ahora escalarán con los días.");
        }
        
        runningFolia = isRunningFolia();
        this.lootManager = new dev.itsrealperson.permadeath.util.LootManager(this);

        // 6. Registrar proveedor de API
        PermadeathAPI.setProvider(this);
        
        DEBUG = getConfig().getBoolean("Toggles.Debug", false);

        // 7. Configurar mundos y reglas
        String worldState = setupWorld(); // Detectar mundos
        this.gameRuleManager.applyRules(); // Aplicar gamerules (Hard, etc)

        // 8. Registrar comandos
        setupCommands();

        prefix = TextUtils.format((getConfig().contains("Prefix") ? getConfig().getString("Prefix") : "&c&lPERMADEATH&4&l &7âž¤ &f"));

        // Cargar mundos si existen en la configuración
        if (getConfig().contains("Worlds")) {
            for (String s : getConfig().getStringList("Worlds")) {
                org.bukkit.Bukkit.createWorld(new org.bukkit.WorldCreator(s));
            }
        }

        this.playTime = getConfig().getInt("DontTouch.PlayTime");

        DiscordPortal.onEnable();
        tickAll();
    }

    @Override
    public void onDisable() {
        if (networkManager != null) {
            networkManager.shutdown();
        }
        PacketEvents.getAPI().terminate();
        if (playerStorage != null) {
            playerStorage.close();
        }
        getConfig().set("DontTouch.PlayTime", this.playTime);
        if (this.orbEvent != null) {
            this.orbEvent.saveTime();
        }
        saveConfig();
        reloadConfig();

        DiscordPortal.onDisable();

        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&f&m------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("             &c&lPERMADEATH"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("     &7- Desactivando el Plugin."));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&f&m------------------------------------------"));

        this.instance = null;
    }

    // --- GETTERS PARA COMPATIBILIDAD ---
    public static Main getInstance() { return instance; }
    public Messages getMessages() { return messages; }
    public LifeOrbEvent getOrbEvent() { return orbEvent; }
    public ShellEvent getShulkerEvent() { return shulkerEvent; }
    public MobFactory getFactory() { return factory; }
    public BeginningDataManager getBeData() { return beData; }
    public EndDataManager getEndData() { return endData; }
    public BeginningManager getBeginningManager() { return begginingManager; }
    public AbyssManager getAbyssManager() { return abyssManager; }
    public dev.itsrealperson.permadeath.api.NetworkManagerAPI getNetworkManager() { return networkManager; }
    public dev.itsrealperson.permadeath.util.ShardManager getShardManager() { return shardManager; }
    public SpawnListener getSpawnListener() { return spawnListener; }
    public NMSHandler getNmsHandler() { return NMS.getHandler(); }
    public NMSAccessor getNmsAccessor() { return NMS.getAccessor(); }

    @Override
    public File getAddonDataFolder(String addonName) {
        File folder = new File(getDataFolder(), "addons/" + addonName);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    public InfernalNetheriteBlock getNetheriteBlock() { return NMS.getNetheriteBlock(); }
    public EndTask getTask() { return task; }
    public void setTask(EndTask task) { this.task = task; }
    public int getPlayTime() { return playTime; }
    public void setPlayTime(int playTime) { this.playTime = playTime; }
    public ArrayList<Player> getDoneEffectPlayers() { return doneEffectPlayers; }
    public boolean isSmallIslandsEnabled() { return true; }
    public static boolean isRunningPaperSpigot() { return runningPaperSpigot; }

    public static boolean isRunningFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public org.bukkit.generator.ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (worldName.equalsIgnoreCase("pdc_the_beginning")) {
            return new dev.itsrealperson.permadeath.world.beginning.generator.BeginningGenerator();
        }
        if (worldName.equalsIgnoreCase("pdc_the_abyss")) {
            return new dev.itsrealperson.permadeath.world.abyss.generator.DeepDarkAbyssGenerator();
        }
        return null;
    }

    private void tickAll() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!getFile().exists()) saveDefaultConfig();
                if (!loaded) {
                    loaded = true; // Marcar como cargado primero para evitar bucles si startPlugin falla
                    try {
                        startPlugin();
                        setupConfig();
                        registerListeners();
                    } catch (Exception e) {
                        getLogger().log(Level.SEVERE, "Error crítico durante la carga del plugin:", e);
                    }
                }
                DateManager.getInstance().tick();

                if (world != null) {
                    weatherDurationCache = world.getWeatherDuration();
                }

                moduleManager.tickModules();

                if (Bukkit.getOnlinePlayers().size() >= 1 && SPEED_RUN_MODE) {
                    playTime++;

                    if (playTime % (3600) == 0) {
                        Bukkit.broadcastMessage(prefix + TextUtils.format("&cFelicitaciones, han avanzado a la hora número: " + getDay()));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100.0F, 100.0F);
                        }
                    }
                }

                // tickPlayers() y tickEvents() eliminados - Toda la lógica ha sido migrada a módulos.
            }
        };

        if (runningFolia) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, t -> task.run(), 1, 20L);

            // Tarea periódica para asegurar que todos los jugadores tengan su scheduler de tick activo
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, t -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    // Usamos una metadata TEMPORAL (en memoria) para saber si ya le asignamos la tarea
                    if (p.hasMetadata("pdc_ticking")) continue;

                    // Programar la tarea en el scheduler de la entidad (hilo correcto para inventario)
                    p.setMetadata("pdc_ticking", new FixedMetadataValue(this, true));

                    // Logro Día 60
                    if (getDay() >= 60) {
                        dev.itsrealperson.permadeath.util.AdvancementManager.grantAdvancement(p, dev.itsrealperson.permadeath.util.AdvancementManager.PDA.SURVIVOR_60);
                    }
                }
            }, 20L, 20L); // Revisar cada segundo
        } else {
            Bukkit.getScheduler().runTaskTimer(this, task, 0, 20L); // Ajustado a cada segundo
        }
    }

    private void setupStorage() {
        String type = getConfig().getString("Storage.Type", "YAML").toUpperCase();
        if (type.equals("SQLITE")) {
            this.playerStorage = new dev.itsrealperson.permadeath.data.storage.SQLitePlayerDataStorage(this);
        } else if (type.equals("MYSQL")) {
            this.playerStorage = new dev.itsrealperson.permadeath.data.storage.MySQLPlayerDataStorage(this);
        } else {
            this.playerStorage = new dev.itsrealperson.permadeath.data.storage.YamlPlayerDataStorage(this);
        }
        try {
            this.playerStorage.init();
            getLogger().info("Sistema de almacenamiento inicializado: " + type);
            migrateIfNecessary();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error al inicializar el almacenamiento: " + type, e);
            this.playerStorage = new dev.itsrealperson.permadeath.data.storage.YamlPlayerDataStorage(this);
            try { this.playerStorage.init(); } catch (Exception ignored) {}
        }
    }

    private void migrateIfNecessary() {
        if (playerStorage instanceof dev.itsrealperson.permadeath.data.storage.SQLitePlayerDataStorage && !getConfig().getBoolean("DontTouch.StorageMigrated", false)) {
            getLogger().info("Iniciando migracion de datos YAML a SQLite...");
            dev.itsrealperson.permadeath.data.storage.YamlPlayerDataStorage yaml = new dev.itsrealperson.permadeath.data.storage.YamlPlayerDataStorage(this);
            try {
                yaml.init();
                Collection<String> players = yaml.getSavedPlayers();
                for (String name : players) {
                    yaml.loadPlayer(name).ifPresent(playerStorage::savePlayer);
                }
                getConfig().set("DontTouch.StorageMigrated", true);
                saveConfig();
                getLogger().info("Migracion completada con exito: " + players.size() + " jugadores migrados.");
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Error durante la migracion de datos.", e);
            }
        }
    }

    private void startPlugin() {
        this.messages = new Messages(this);

        this.shulkerEvent = new ShellEvent(this);
        this.orbEvent = new LifeOrbEvent(this);
        this.factory = new MobFactory(this);

        // Extraer estructuras NBT para el sistema de pegado manual (Portales)
        extractStructures();

        int HelmetValue = instance.getConfig().getInt("Toggles.Netherite.Helmet", 10);
        int ChestplateValue = instance.getConfig().getInt("Toggles.Netherite.Chestplate", 10);
        int LeggingsValue = instance.getConfig().getInt("Toggles.Netherite.Leggings", 10);
        int BootsValue = instance.getConfig().getInt("Toggles.Netherite.Boots", 10);
        if (HelmetValue > 100 || HelmetValue < 1) {
            PDCLog.getInstance().log("[ERROR] Error al cargar la probabilidad de 'Helmet' en 'config.yml', asegurate de introducir un numero valido del 1 al 100.", true);
            PDCLog.getInstance().log("[ERROR] Ha ocurrido un error al cargar el archivo config.yml, si este error persiste avisanos por discord.", true);
        }
        if (ChestplateValue > 100 || ChestplateValue < 1) {
            PDCLog.getInstance().log("[ERROR] Error al cargar la probabilidad de 'Chestplate' en 'config.yml', asegurate de introducir un numero valido del 1 al 100.", true);
            PDCLog.getInstance().log("[ERROR] Ha ocurrido un error al cargar el archivo config.yml, si este error persiste avisanos por discord.", true);
        }
        if (LeggingsValue > 100 || LeggingsValue < 1) {
            PDCLog.getInstance().log("[ERROR] Error al cargar la probabilidad de 'Leggings' en 'config.yml', asegurate de introducir un numero valido del 1 al 100.", true);
            PDCLog.getInstance().log("[ERROR] Ha ocurrido un error al cargar el archivo config.yml, si este error persiste avisanos por discord.", true);
        }
        if (BootsValue > 100 || BootsValue < 1) {
            PDCLog.getInstance().log("[ERROR] Error al cargar la probabilidad de 'BootsValue' en 'config.yml', asegurate de introducir un numero valido del 1 al 100.", true);
            PDCLog.getInstance().log("[ERROR] Ha ocurrido un error al cargar el archivo config.yml, si este error persiste avisanos por discord.", true);
        }
        String compatibleVersion = VersionManager.getVersion() != null ? ("&aCompatible") : "&cIncompatible";

        String software = "";
        try {
            if (Class.forName("org.spigotmc.SpigotConfig") != null) {
                software = "SpigotMC (Compatible)";
            }
        } catch (ClassNotFoundException e) {
            software = "Bukkit";
        }

        try {
            if (Class.forName("com.destroystokyo.paper.PaperConfig") != null) {
                software = "PaperMC (Compatible)";
                runningPaperSpigot = true;
            }
        } catch (ClassNotFoundException e) {
        }

        if (runningFolia) {
            software = "Folia (Compatible)";
        }

        String worldState = setupWorld();

        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&f&m------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("             &c&lPERMADEATH"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("     &7- Versión: &e" + this.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("     &7- Versión del Servidor: &e" + VersionManager.getFormattedVersion()));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&f&m------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format(" &7>> &e&lINFORME DE ESTADO"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &bEstado de mundos: " + worldState));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("    &7> &eEnd&7: &8" + this.endWorld.getName()));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("    &7> &aOverworld&7: &8" + this.world.getName()));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &bEstado de Compatibilidad: " + compatibleVersion));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &bSoftware: " + software));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &b&lCambios:"));
        Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7>   &aDías disponibles: &71-60"));

        if (software.contains("Bukkit")) {
            Bukkit.broadcastMessage(TextUtils.format(prefix + "&7> &4&lADVERTENCIA&7: &eEl plugin NO es compatible con CraftBukkit, cambia a SpigotMC o PaperSpigot"));
            PDCLog.getInstance().disable("No es compatible con Bukkit.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        setupListeners();
        checkSpigotConfig();

        new UpdateChecker(this).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefix + "&bVersión del plugin: &aVersión más reciente instalada."));
            } else {
                Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefix + "&eNueva versión detectada."));
                Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &aDescarga: &7" + Utils.GITHUB_LINK));
            }
        });

        registerChanges();
        generateOfflinePlayerData();

        PDCLog.getInstance().log("Se ha activado el plugin.");
    }

    private void registerListeners() {
        String prefixStr = "&e[PermaDeath] &7> ";

        if (!registeredDays.getOrDefault(1, false)) {
            registeredDays.put(1, true);

            this.getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        }

        if (DateManager.getInstance().getDay() >= 20 && !registeredDays.getOrDefault(20, false)) {
            registeredDays.put(20, true);

            this.hostile = new HostileEntityListener(this);
            getServer().getPluginManager().registerEvents(hostile, instance);
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefixStr + "&eSe han registrado los cambios de Mobs pacíficos hostiles."));
        }

        if (DateManager.getInstance().getDay() >= 30 && endManager == null && endData == null && !registeredDays.getOrDefault(30, false)) {
            registeredDays.put(30, true);

            this.endManager = new EndManager(instance);
            getServer().getPluginManager().registerEvents(endManager, instance);

            this.endData = new EndDataManager(instance);

            if (runningPaperSpigot) {
                getServer().getPluginManager().registerEvents(new PaperListeners(instance), instance);
                Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefixStr + "&eSe han registrado cambios especiales para &c&lPaperMC&e."));
            }
        }

        if (DateManager.getInstance().getDay() >= 40 && !registeredDays.getOrDefault(40, false)) {

            registeredDays.put(40, true);
            if (this.recipes == null) this.recipes = new RecipeManager(this);
            this.recipes.registerRecipes();
            this.getNmsHandler().addMushrooms();
            getServer().getPluginManager().registerEvents(new SlotBlockListener(instance), instance);
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefixStr + "&eSe han registrado cambios para el día &b40"));
        }

        if (DateManager.getInstance().getDay() >= 50 && !registeredDays.getOrDefault(50, false)) {

            if (this.recipes == null) {
                this.recipes = new RecipeManager(this);
                this.recipes.registerRecipes();
            }

            Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefixStr + "&eSe han registrado cambios para el día &b50"));
            this.recipes.registerD50Recipes();
            registeredDays.put(50, true);
        }

        if (DateManager.getInstance().getDay() >= 60 && !registeredDays.getOrDefault(60, false)) {

            if (this.recipes == null) {
                this.recipes = new RecipeManager(this);
                this.recipes.registerRecipes();
                this.recipes.registerD50Recipes();
            }

            this.recipes.registerD60Recipes();
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefixStr + "&eSe han registrado cambios para el día &b60"));
            registeredDays.put(60, true);
        }
    }

    protected void registerChanges() {
        if (alreadyRegisteredChanges) return;
        alreadyRegisteredChanges = true;
    }

    public void generateOfflinePlayerData() {

        for (OfflinePlayer off : Bukkit.getOfflinePlayers()) {

            if (off == null) return;

            PlayerDataManager manager = new PlayerDataManager(off.getName(), this);
            manager.generateDayData();
        }
    }

    protected String setupWorld() {

        String mainWorldName = instance.getConfig().getString("Worlds.MainWorld");
        String endWorldName = instance.getConfig().getString("Worlds.EndWorld");

        if (mainWorldName != null && Bukkit.getWorld(mainWorldName) != null) {
            this.world = Bukkit.getWorld(mainWorldName);
        } else {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == World.Environment.NORMAL) {
                    this.world = w;
                    instance.getConfig().set("Worlds.MainWorld", w.getName());
                    saveConfig();
                    break;
                }
            }
        }

        if (endWorldName != null && Bukkit.getWorld(endWorldName) != null) {
            this.endWorld = Bukkit.getWorld(endWorldName);
        } else {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == World.Environment.THE_END) {
                    this.endWorld = w;
                    instance.getConfig().set("Worlds.EndWorld", w.getName());
                    saveConfig();
                    break;
                }
            }
        }

        if (this.world == null) {
            PDCLog.getInstance().log("[ERROR] No se pudo encontrar un mundo principal (OVERWORLD).", true);
        }
        if (this.endWorld == null) {
            PDCLog.getInstance().log("[ERROR] No se pudo encontrar un mundo del End (THE_END).", true);
        }

        boolean dobleCap = getConfig().getBoolean("Toggles.Doble-Mob-Cap") && getDay() >= 10;
        if (dobleCap) Bukkit.getConsoleSender().sendMessage(prefix + "&eDoblando la mob-cap en todos los mundos.");

        for (World w : Bukkit.getWorlds()) {
            if (dobleCap) {
                if (runningFolia) {
                    Bukkit.getGlobalRegionScheduler().execute(this, () -> w.setMonsterSpawnLimit(140));
                } else {
                    w.setMonsterSpawnLimit(140);
                }
            }

            if (isRunningPaperSpigot()) {
                try {
                    Object nmsW = w.getClass().getDeclaredMethod("getHandle").invoke(w);

                    final Field f = nmsW.getClass().getDeclaredField("paperConfig");
                    f.setAccessible(true);
                    Object paperConfig = f.get(nmsW);

                    final Field creepers = paperConfig.getClass().getDeclaredField("disableCreeperLingeringEffect");
                    creepers.setAccessible(true);
                    creepers.set(paperConfig, true);

                    Bukkit.getConsoleSender().sendMessage(prefix + "&eDeshabilitando Creeper-Lingering-Effect...");

                } catch (Exception ignored) {
                }
            }
        }

        return "&aOverworld: &b" + (this.world != null ? this.world.getName() : "null") + " &eEnd: &b" + (this.endWorld != null ? this.endWorld.getName() : "null");
    }


    private void setupListeners() {
        getServer().getPluginManager().registerEvents(this, this);

        if (this.abyssManager != null) {
            getServer().getPluginManager().registerEvents(this.abyssManager, instance);
        }

        this.spawnListener = new SpawnListener(this);
        getServer().getPluginManager().registerEvents(spawnListener, instance);
        getServer().getPluginManager().registerEvents(new AccessoryListener(), instance);
        getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.util.item.AbyssalCauldronListener(), instance);
        getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.util.gui.GUIManager(), instance);
        getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.util.gui.GUIListener(), instance);
        getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.event.ShardListener(this), instance);
        getServer().getPluginManager().registerEvents(new CustomSkeletons(instance), instance);
        getServer().getPluginManager().registerEvents(new PlayerListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityEvents(), instance);
        getServer().getPluginManager().registerEvents(new TotemListener(), instance);
        getServer().getPluginManager().registerEvents(new RaidEvents(), instance);
        getServer().getPluginManager().registerEvents(new WorldEvents(), instance);
        getServer().getPluginManager().registerEvents(new dev.itsrealperson.permadeath.event.player.CustomEnchantmentListener(), instance);
        registeredDays.put(1, false);
        registeredDays.put(20, false);
        registeredDays.put(30, false);
        registeredDays.put(40, false);
        registeredDays.put(50, false);
        registeredDays.put(60, false);
    }

    private void setupConsoleFilter() {
        try {
            Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
            org.apache.logging.log4j.core.Logger logger;
            logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
            logger.addFilter(new Log4JFilter());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            Filter f = (Filter) new Log4JFilter();
            Bukkit.getLogger().setFilter(f);
            Logger.getLogger("Minecraft").setFilter(f);
        }
    }

    private void setupCommands() {
        this.commandManager = new dev.itsrealperson.permadeath.command.CommandManager(this);

        // Registro único y ordenado de subcomandos
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.ConfigCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.ReloadCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.DayCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.InfoCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.RecipesCommand());
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.MessageCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.AbyssCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.AccessoryCommand());
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.AwakeCommand());
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.StormCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.SpeedRunCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.GiveCommand());
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.LocateCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.DayChangeCommand());
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.BackupCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.ResetAllCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.LanguageCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.SpawnCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.DebugCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.BeginningCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.BossCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.EventCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.DurationCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.ChangesCommand());
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.AFKCommand(this));
        commandManager.registerSubCommand(new dev.itsrealperson.permadeath.command.impl.SetupBeginningCommand(this));

        try {
            java.lang.reflect.Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            org.bukkit.command.CommandMap commandMap = (org.bukkit.command.CommandMap) commandMapField.get(Bukkit.getServer());

            java.lang.reflect.Constructor<org.bukkit.command.PluginCommand> constructor =
                org.bukkit.command.PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            constructor.setAccessible(true);

            org.bukkit.command.PluginCommand command = constructor.newInstance("pdc", this);

            command.setExecutor(commandManager);
            command.setTabCompleter(commandManager);
            command.setDescription("Comando principal de Permadeath");
            command.setAliases(java.util.Arrays.asList("permadeath"));

            commandMap.register("permadeath", command);
            getLogger().info("Comando /pdc registrado correctamente vía reflexión.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error al registrar comandos:", e);
        }
    }

    private void setupConfig() {
        // Aseguramos que los valores por defecto del JAR se copien si faltan
        getConfig().options().copyDefaults(true);
        saveConfig();

        OPTIMIZE_SPAWNS = getConfig().getBoolean("Toggles.Optimizar-Mob-Spawns");
    }

    public void checkSpigotConfig() {
        File spigotFile = new File("spigot.yml");
        if (spigotFile.exists()) {
            YamlConfiguration spigotConfig = YamlConfiguration.loadConfiguration(spigotFile);
            double currentMax = spigotConfig.getDouble("settings.attribute.max_health.max", 1024.0);

            if (currentMax < 2048.0) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "Detectado límite de vida bajo en spigot.yml (" + currentMax + "). Actualizando a 2048.0...");
                spigotConfig.set("settings.attribute.max_health.max", 2048.0);
                try {
                    spigotConfig.save(spigotFile);
                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "¡spigot.yml actualizado! Los cambios surtirán efecto tras el PRÓXIMO REINICIO.");
                } catch (IOException e) {
                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + "No se pudo guardar spigot.yml automáticamente.");
                }
            }
        }
    }

    private void extractStructures() {
        File structuresDir = new File(getDataFolder(), "data/structures");
        if (!structuresDir.exists()) structuresDir.mkdirs();

        String[] structures = {
            "beginning_portal.nbt",
            "island1.nbt",
            "island2.nbt",
            "island3.nbt",
            "island4.nbt",
            "island5.nbt",
            "ytic.nbt"
        };

        for (String name : structures) {
            File outFile = new File(structuresDir, name);
            if (!outFile.exists()) {
                // Buscamos en la ruta del datapack interno
                try (InputStream in = getResource("internal_datapack/data/permadeath/structures/" + name)) {
                    if (in != null) {
                        Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    getLogger().warning("No se pudo extraer la estructura: " + name);
                }
            }
        }
    }

    public void deathTrainEffects(LivingEntity entity) {
        if (entity instanceof Player) return;

        if (getDay() >= 25) {

            int lvl = (getDay() >= 50 ? 1 : 0);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, lvl));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, lvl));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, lvl));

            if (getDay() >= 50 && getDay() < 60) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            }

            // Evolución de mobs (Día 60 a 90)
            if (getDay() >= 60) {
                double multiplier = 1.0 + ((getDay() - 60) * 0.01); // +1% cada día
                
                // Disparar evento API (Hook de Escalado)
                dev.itsrealperson.permadeath.api.event.PermadeathMobScaleEvent scaleEvent = 
                    new dev.itsrealperson.permadeath.api.event.PermadeathMobScaleEvent(entity, getDay(), multiplier, multiplier);
                Bukkit.getPluginManager().callEvent(scaleEvent);
                
                double hMult = scaleEvent.getHealthMultiplier();
                double dMult = scaleEvent.getDamageMultiplier();

                org.bukkit.attribute.AttributeInstance health = entity.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                org.bukkit.attribute.AttributeInstance damage = entity.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE);

                if (health != null) health.setBaseValue(health.getBaseValue() * hMult);
                if (damage != null) damage.setBaseValue(damage.getBaseValue() * dMult);
                entity.setHealth(entity.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());

                // Día 90+ Buffs Especiales
                if (getDay() >= 90) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                }
            }
        }
    }
}
