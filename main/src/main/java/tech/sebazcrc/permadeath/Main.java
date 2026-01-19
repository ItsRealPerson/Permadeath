package tech.sebazcrc.permadeath;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.api.interfaces.InfernalNetheriteBlock;
import tech.sebazcrc.permadeath.api.interfaces.NMSAccessor;
import tech.sebazcrc.permadeath.api.interfaces.NMSHandler;
import tech.sebazcrc.permadeath.data.*;
import tech.sebazcrc.permadeath.discord.DiscordPortal;
import tech.sebazcrc.permadeath.end.EndManager;
import tech.sebazcrc.permadeath.event.HostileEntityListener;
import tech.sebazcrc.permadeath.event.block.BlockListener;
import tech.sebazcrc.permadeath.event.entity.EntityEvents;
import tech.sebazcrc.permadeath.event.entity.SpawnListener;
import tech.sebazcrc.permadeath.event.entity.TotemListener;
import tech.sebazcrc.permadeath.event.paper.PaperListeners;
import tech.sebazcrc.permadeath.event.player.AnvilListener;
import tech.sebazcrc.permadeath.event.player.PlayerListener;
import tech.sebazcrc.permadeath.event.player.SlotBlockListener;
import tech.sebazcrc.permadeath.event.raid.RaidEvents;
import tech.sebazcrc.permadeath.event.world.WorldEvents;
import tech.sebazcrc.permadeath.task.EndTask;
import tech.sebazcrc.permadeath.util.*;
import tech.sebazcrc.permadeath.util.events.LifeOrbEvent;
import tech.sebazcrc.permadeath.util.events.ShellEvent;
import tech.sebazcrc.permadeath.util.item.NetheriteArmor;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;
import tech.sebazcrc.permadeath.util.item.RecipeManager;
import tech.sebazcrc.permadeath.util.lib.FileAPI;
import tech.sebazcrc.permadeath.util.lib.UpdateChecker;
import tech.sebazcrc.permadeath.util.log.Log4JFilter;
import tech.sebazcrc.permadeath.util.log.PDCLog;
import tech.sebazcrc.permadeath.util.mob.CustomSkeletons;
import tech.sebazcrc.permadeath.api.PermadeathAPI;
import tech.sebazcrc.permadeath.api.PermadeathAPIProvider;
import tech.sebazcrc.permadeath.world.abyss.AbyssManager;
import tech.sebazcrc.permadeath.world.beginning.BeginningManager;

import tech.sebazcrc.permadeath.util.inventory.AccessoryListener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Filter;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Main extends JavaPlugin implements Listener, PermadeathAPIProvider {

    private static final int CURRENT_CONFIG_VERSION = 3;
    public static boolean DEBUG = false;
    public static boolean DISABLED_LINGERING = false;
    public static boolean SPEED_RUN_MODE = false;
    public static Main instance;
    public static String prefix = "";
    public static boolean runningPaperSpigot = false;
    public static boolean runningFolia = false;
    public static boolean worldEditFound;
    private final SplittableRandom random = new SplittableRandom();
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
    private Map<Integer, Boolean> registeredDays = new HashMap<>();
    private ArrayList<Player> doneEffectPlayers = new ArrayList<>();
    private boolean loaded = false;
    private boolean alreadyRegisteredChanges = false;
    public ShellEvent shulkerEvent;
    public LifeOrbEvent orbEvent;
    public SpawnListener spawnListener;

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

    @Override
    public boolean isExtendedDifficulty() {
        return getConfig().getBoolean("DontTouch.ExtendedDifficultyActive");
    }

    @Override
    public void onEnable() {
        instance = this;
        runningFolia = isRunningFolia();
        
        // Registrar proveedor de API
        PermadeathAPI.setProvider(this);

        this.saveDefaultConfig();
        setupConsoleFilter();

        prefix = TextUtils.format((getConfig().contains("Prefix") ? getConfig().getString("Prefix") : "&cPermadeath &7➤ &f"));

        tickAll();

        this.playTime = getConfig().getInt("DontTouch.PlayTime");
    }

    @Override
    public void onLoad() {
        instance = this;
        this.abyssManager = new AbyssManager(this);
        try {
            NMS.loadInfernalNetheriteBlock();
            NMS.loadNMSAccessor();
            NMS.loadNMSHandler(this);

            getNmsAccessor().registerHostileMobs();
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        getConfig().set("DontTouch.PlayTime", this.playTime);
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
    public SpawnListener getSpawnListener() { return spawnListener; }
    public NMSHandler getNmsHandler() { return NMS.getHandler(); }
    public NMSAccessor getNmsAccessor() { return NMS.getAccessor(); }
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

    public void setupFoliaWorldConfig(CommandSender sender) {
        File bukkitConfig = new File("bukkit.yml");
        if (!bukkitConfig.exists()) bukkitConfig = new File(Bukkit.getWorldContainer(), "bukkit.yml");
        if (!bukkitConfig.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bukkitConfig);
        if (!config.contains("worlds.pdc_the_beginning")) {
            config.set("worlds.pdc_the_beginning.generator", "Permadeath");
            try { config.save(bukkitConfig); } catch (Exception ignored) {}
        }
    }

    @Override
    public org.bukkit.generator.ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (worldName.equalsIgnoreCase("pdc_the_beginning")) {
            return new tech.sebazcrc.permadeath.world.beginning.generator.BeginningGenerator();
        }
        if (worldName.equalsIgnoreCase("pdc_the_abyss")) {
            return new tech.sebazcrc.permadeath.world.abyss.generator.DeepDarkAbyssGenerator();
        }
        return null;
    }

    private void tickAll() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!getFile().exists()) saveDefaultConfig();
                if (!loaded) {
                    startPlugin();
                    setupConfig();
                    loaded = true;
                }
                DateManager.getInstance().tick();
                registerListeners();

                if (Bukkit.getOnlinePlayers().size() >= 1 && SPEED_RUN_MODE) {
                    playTime++;

                    if (playTime % (3600) == 0) {
                        Bukkit.broadcastMessage(prefix + TextUtils.format("&cFelicitaciones, han avanzado a la hora número: " + getDay()));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100.0F, 100.0F);
                        }
                    }
                }

                tickEvents();
                if (!runningFolia) {
                    tickPlayers();
                }
                tickWorlds();

                // Chequeo constante del evento de Orbe (como en master)
                if (getDay() >= 60 && !getConfig().getBoolean("DontTouch.Event.LifeOrbEnded") && !getOrbEvent().isRunning()) {
                    if (SPEED_RUN_MODE) orbEvent.setTimeLeft(60 * 8);
                    orbEvent.setRunning(true);
                    Bukkit.getOnlinePlayers().forEach(orbEvent::addPlayer);
                }
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
                    p.getScheduler().runAtFixedRate(this, taskPlayer -> {
                        if (!p.isOnline()) {
                            taskPlayer.cancel(); // Cancelar si se desconecta
                            return;
                        }
                        // if (DEBUG) Bukkit.getLogger().info("[Folia] Ticking player: " + p.getName());
                        tickPlayer(p);
                    }, null, 1, 20L);
                    
                    p.setMetadata("pdc_ticking", new FixedMetadataValue(this, true));
                }
            }, 20L, 40L); // Revisar cada 2 segundos
        } else {
            Bukkit.getScheduler().runTaskTimer(this, task, 0, 20L);
        }
    }

    private void tickWorlds() {
        if (this.getDay() >= 40) {
            for (World w : Bukkit.getWorlds().stream().filter(world1 -> world1.getEnvironment() != World.Environment.THE_END).collect(Collectors.toList())) {
                for (Ravager ravager : w.getEntitiesByClass(Ravager.class)) {
                    if (ravager.getPersistentDataContainer().has(new NamespacedKey(instance, "ultra_ravager"), PersistentDataType.BYTE)) {
                        List<Block> b = ravager.getLineOfSight(null, 5);

                        for (Block block : b) {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    for (int k = -1; k <= 1; k++) {
                                        Block r = block.getRelative(i, j, k);
                                        if (r.getType() == Material.NETHERRACK) {
                                            r.setType(Material.AIR);
                                            r.getWorld().playSound(r.getLocation(), Sound.BLOCK_STONE_BREAK, 2.0F, 1.0F);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void tickPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            tickPlayer(p);
        }
    }

    private void tickPlayer(Player player) {
        if (!player.isOnline()) return;

        long segundosbrutos = world != null ? world.getWeatherDuration() / 20 : 0;
        long hours = segundosbrutos % 86400 / 3600;
        long minutes = (segundosbrutos % 3600) / 60;
        long seconds = segundosbrutos % 60;
        long days = segundosbrutos / 86400;

        final String time = String.format((days >= 1 ? String.format("%02d día(s) ", days) : "") + "%02d:%02d:%02d", hours, minutes, seconds);

        if (this.shulkerEvent != null && this.shulkerEvent.isRunning()) {
            if (!this.shulkerEvent.getBossBar().getPlayers().contains(player)) {
                this.shulkerEvent.getBossBar().addPlayer(player);
            }
        }

        if (this.orbEvent != null && this.orbEvent.isRunning()) {
            if (!this.orbEvent.getBossBar().getPlayers().contains(player)) {
                this.orbEvent.getBossBar().addPlayer(player);
            }
        }

        NetheriteArmor.setupHealth(player);
        // Debug log for slotBlock
        // if (DEBUG) Bukkit.getLogger().info("Executing slotBlock for " + player.getName());
        PermadeathItems.slotBlock(player);
        player.updateInventory();

        if (SPEED_RUN_MODE) {
            String actionBar = "";
            if (world != null && world.hasStorm()) {
                actionBar = getMessages().getMessageByPlayer("Server-Messages.ActionBarMessage", player.getName()).replace("%tiempo%", time) + " - ";
            }
            actionBar = actionBar + ChatColor.GRAY + "Tiempo total: " + TextUtils.formatInterval(playTime);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBar));
        } else {
            if (world != null && world.hasStorm()) {
                String msg = getMessages().getMessageByPlayer("Server-Messages.ActionBarMessage", player.getName()).replace("%tiempo%", time);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        }

        if (player.getWorld().getEnvironment() == World.Environment.THE_END && getDay() >= 30) {
            if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10 * 20, 9));
            }
            if (player.getWorld().getName().equalsIgnoreCase("pdc_the_beginning")) {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }

        // Efecto Darkness aleatorio en el Abismo
        if (player.getWorld().getName().equalsIgnoreCase("pdc_the_abyss")) {
            if (instance.getAbyssManager() != null) {
                instance.getAbyssManager().tickAbyssEffects(player);
            }
            
            if (random.nextInt(100) < 5) { // 5% de probabilidad por segundo
                int duration = 100 + random.nextInt(100); // 5 a 10 segundos
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0));
            }
        }

        if (getDay() >= 40) {
            if (player.getWorld().hasStorm() && player.getGameMode() != GameMode.SPECTATOR) {
                Location block = player.getWorld().getHighestBlockAt(player.getLocation()).getLocation();
                int highestY = block.getBlockY();

                if (highestY < player.getLocation().getY()) {
                    int probability = random.nextInt(10000) + 1;
                    int blind = (getDay() < 50 ? 1 : 300);
                    if (probability <= blind) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 0));
                    }
                    if (getDay() >= 50) {
                        if (probability == 301) {
                            int duration = random.nextInt(17) + 3;
                            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration * 20, 0));
                        }
                    }
                }
            }
        }

        if (getDay() >= 50) {
            if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) {
                PotionEffect e = player.getPotionEffect(PotionEffectType.MINING_FATIGUE);
                if (e.getDuration() >= 4 * 60 * 20 && !getDoneEffectPlayers().contains(player)) {
                    int min = 10 * 60;
                    player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, min * 20, 2));
                    getDoneEffectPlayers().add(player);
                }
                if (e.getDuration() == 4 * 60 * 20 - 1 && getDoneEffectPlayers().contains(player)) {
                    getDoneEffectPlayers().remove(player);
                }
            }

            if (player.getWorld().getEnvironment() == World.Environment.NETHER && getDay() < 60) {
                int randomVal = this.random.nextInt(4500) + 1;
                if (randomVal <= 10 && player.getWorld().getLivingEntities().size() < 110) {
                    Location ploc = player.getLocation().clone();
                    ArrayList<Location> spawns = new ArrayList<>();
                    spawns.add(ploc.clone().add(10, 25, -5));
                    spawns.add(ploc.clone().add(5, 25, 5));
                    spawns.add(ploc.clone().add(-5, 25, 5));

                    for (Location l : spawns) {
                        if (player.getWorld().getBlockAt(l).getType() == Material.AIR && player.getWorld().getBlockAt(l.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            int randomEntities = this.random.nextInt(3) + 1;
                            for (int i = 0; i < randomEntities; i++) {
                                getNmsHandler().spawnNMSEntity("PigZombie", EntityType.valueOf(VersionManager.isRunningPostNetherUpdate() ? "ZOMBIFIED_PIGLIN" : "PIG_ZOMBIE"), l, CreatureSpawnEvent.SpawnReason.CUSTOM);
                            }
                        }
                    }
                }
            }
        }

        if (getDay() >= 60) {
            if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOUL_SAND) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30 * 20, 2));
            }
            Integer timeForWither = player.getPersistentDataContainer().get(new NamespacedKey(this, "wither"), PersistentDataType.INTEGER);
            if (timeForWither == null) timeForWither = 0;
            
            if (timeForWither % (60 * 60) == 0 && player.getGameMode() == GameMode.SURVIVAL) {
                timeForWither = 0;
                Wither wither = player.getWorld().spawn(player.getLocation().clone().add(0, 5, 0), Wither.class);
                try {
                    Object nmsw = wither.getClass().getDeclaredMethod("getHandle").invoke(wither);
                    nmsw.getClass().getDeclaredMethod("r", int.class).invoke(nmsw, 100);
                } catch (Exception ignored) {}
            }
            player.getPersistentDataContainer().set(new NamespacedKey(this, "wither"), PersistentDataType.INTEGER, ++timeForWither);

            if (getConfig().getBoolean("Toggles.Mike-Creeper-Spawn")) {
                Location l = player.getLocation().clone();
                if (random.nextInt(30) == 0 && player.getNearbyEntities(30, 30, 30).stream().filter(e -> e instanceof Creeper).count() < 10) {
                    int pX = (random.nextBoolean() ? -1 : 1) * (random.nextInt(15)) + 15;
                    int pZ = (random.nextBoolean() ? -1 : 1) * (random.nextInt(15)) + 15;
                    int y = (int) l.getY();

                    Block block = l.getWorld().getBlockAt(l.getBlockX() + pX, y, l.getBlockZ() + pZ);
                    Block up = block.getRelative(BlockFace.UP);

                    if (block.getType() != Material.AIR && up.getType() == Material.AIR) {
                        getFactory().spawnEnderQuantumCreeper(up.getLocation(), null);
                    }
                }
            }
        }
    }

    private void tickEvents() {

        if (this.orbEvent != null && this.orbEvent.isRunning()) {
            if (this.orbEvent.getTimeLeft() > 0) {

                this.orbEvent.reduceTime();

                int res = this.orbEvent.getTimeLeft();

                int hrs = res / 3600;
                int minAndSec = res % 3600;
                int min = minAndSec / 60;
                int sec = minAndSec % 60;

                String tiempo = String.format("%02d:%02d:%02d", hrs, min, sec);

                this.orbEvent.getBossBar().setColor(BarColor.values()[random.nextInt(BarColor.values().length)]);
                this.orbEvent.setTitle(TextUtils.format("&6&l" + tiempo + " para obtener el Life Orb"));
            } else {

                Bukkit.broadcastMessage(TextUtils.format(instance.prefix + "&cSe ha acabado el tiempo para obtener el Life Orb, ¡sufrid! ahora tendreís 8 contenedores de vida menos."));
                this.orbEvent.setRunning(false);
                this.orbEvent.clearPlayers();
                this.orbEvent.setTimeLeft((SPEED_RUN_MODE ? 60 * 8 : 60 * 60 * 8));
                this.orbEvent.getBossBar().removeAll();

                getConfig().set("DontTouch.Event.LifeOrbEnded", true);
                saveConfig();
                reloadConfig();
            }
        }

        if (this.shulkerEvent != null && this.shulkerEvent.isRunning()) {

            if (this.shulkerEvent.getTimeLeft() > 0) {

                this.shulkerEvent.setTimeLeft(this.shulkerEvent.getTimeLeft() - 1);

                int res = this.shulkerEvent.getTimeLeft();

                int hrs = res / 3600;
                int minAndSec = res % 3600;
                int min = minAndSec / 60;
                int sec = minAndSec % 60;

                String tiempo = String.format("%02d:%02d:%02d", hrs, min, sec);

                this.shulkerEvent.setTitle(TextUtils.format("&e&lX2 Shulker Shells: &b&n" + tiempo));
            } else {

                Bukkit.broadcastMessage(TextUtils.format(instance.prefix + "&eEl evento de &c&lX2 Shulker Shells &eha acabado."));
                this.shulkerEvent.setRunning(false);
                this.shulkerEvent.clearPlayers();
                this.shulkerEvent.setTimeLeft(60 * 60 * 4);
                this.shulkerEvent.getBossBar().removeAll();
            }
        }
    }

    private void startPlugin() {
        this.messages = new Messages(this);
        this.shulkerEvent = new ShellEvent(this);
        this.orbEvent = new LifeOrbEvent(this);
        this.factory = new MobFactory(this);

        if (VersionManager.getMinecraftVersion().isAboveOrEqual(MinecraftVersion.v1_20_R1)) {
            // Schematics nuevas
            new FileAPI.FileOut(instance, "updated_schematics/beginning_portal.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "updated_schematics/ytic.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "updated_schematics/island1.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "updated_schematics/island2.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "updated_schematics/island3.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "updated_schematics/island4.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "updated_schematics/island5.schem", "schematics/", true);
        } else {
            new FileAPI.FileOut(instance, "original_schematics/beginning_portal.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "original_schematics/ytic.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "original_schematics/island1.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "original_schematics/island2.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "original_schematics/island3.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "original_schematics/island4.schem", "schematics/", true);
            new FileAPI.FileOut(instance, "original_schematics/island5.schem", "schematics/", true);
        }

        int HelmetValue = Integer.parseInt(Objects.requireNonNull(instance.getConfig().getString("Toggles.Netherite.Helmet")));
        int ChestplateValue = Integer.parseInt(Objects.requireNonNull(instance.getConfig().getString("Toggles.Netherite.Chestplate")));
        int LeggingsValue = Integer.parseInt(Objects.requireNonNull(instance.getConfig().getString("Toggles.Netherite.Leggings")));
        int BootsValue = Integer.parseInt(Objects.requireNonNull(instance.getConfig().getString("Toggles.Netherite.Boots")));
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

        worldEditFound = (Bukkit.getPluginManager().getPlugin("WorldEdit") != null || Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null);
        if (!worldEditFound) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &4&lADVERTENCIA: &7No se ha encontrado el plugin &7World Edit"));
            Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &7Algunas funciones pueden no funcionar correctamente. Ten en cuenta que las estructuras de The Beginning no podrán ser generadas en días avanzados."));
            PDCLog.getInstance().log("No se encontró WorldEdit");
        }

        if (software.contains("Bukkit")) {
            Bukkit.broadcastMessage(TextUtils.format(prefix + "&7> &4&lADVERTENCIA&7: &eEl plugin NO es compatible con CraftBukkit, cambia a SpigotMC o PaperSpigot"));
            PDCLog.getInstance().disable("No es compatible con Bukkit.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        setupListeners();
        setupCommands();

        new UpdateChecker(this).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefix + "&bVersión del plugin: &aVersión más reciente instalada."));
            } else {
                Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefix + "&eNueva versión detectada."));
                Bukkit.getConsoleSender().sendMessage(TextUtils.format("&7> &aDescarga: &7" + Utils.SPIGOT_LINK));
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

            if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
                Bukkit.broadcastMessage(TextUtils.format(prefixStr + "&4&lNo se pudo registrar TheBeginning ya que no se ha encontrado el plugin &7WorldEdit"));
                Bukkit.broadcastMessage(TextUtils.format(prefixStr + "&7Si necesitas soporte entra a este discord: &e" + Utils.SPIGOT_LINK));
                return;
            }
            this.beData = new BeginningDataManager(this);
            this.begginingManager = new BeginningManager(this);
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(prefixStr + "&eSe han registrado cambios de TheBeginning"));
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

        if (Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.MainWorld"))) == null) {

            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == World.Environment.NORMAL) {
                    this.world = w;
                    break;
                }
            }

            PDCLog.getInstance().log("[ERROR] Error al cargar el mundo principal, esto hará que los Death Train no se presenten.", true);
            PDCLog.getInstance().log("[ERROR] Abre el archivo config.yml y establece el mundo principal en la opción: MainWorld", true);
            if (world != null) {
                PDCLog.getInstance().log("[INFO] El plugin utilizará el mundo " + world.getName() + " como mundo principal.", true);
            }
            PDCLog.getInstance().log("[INFO] Si deseas utilizar otro mundo, configura en el archivo config.yml.", true);

        } else {
            world = Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.MainWorld")));
        }

        if (Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.EndWorld"))) == null) {

            PDCLog.getInstance().log("[ERROR] Error al cargar el mundo del end, esto hará que el end no funcione como debe.", true);
            PDCLog.getInstance().log("[ERROR] Abre el archivo config.yml y establece el mundo del end en la opción: EndWorld", true);

            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == World.Environment.THE_END) {
                    this.endWorld = w;
                    PDCLog.getInstance().log("[INFO] El plugin utilizará el mundo " + w.getName() + " como mundo del End.", true);
                    break;
                }
            }

        } else {
            endWorld = Bukkit.getWorld(Objects.requireNonNull(instance.getConfig().getString("Worlds.EndWorld")));
        }

        boolean dobleCap = getConfig().getBoolean("Toggles.Doble-Mob-Cap") && getDay() >= 10;
        if (dobleCap) Bukkit.getConsoleSender().sendMessage(prefix + "&eDoblando la mob-cap en todos los mundos.");

        for (World w : Bukkit.getWorlds()) {
            if (dobleCap) {
                w.setMonsterSpawnLimit(140);
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

    public void reload(CommandSender sender) {

        this.setupConfig();
        reloadConfig();
        this.messages.reloadFiles();
        DateManager.getInstance().reloadDate();
        setupWorld();

        sender.sendMessage(TextUtils.format("&aSe ha recargado el archivo de configuración y los mensajes."));
        sender.sendMessage(TextUtils.format("&eAlgunos cambios pueden requerir un reinicio para funcionar correctamente."));
        sender.sendMessage(TextUtils.format("&c&lNota importante: &7Algunos cambios pueden requerir un reinicio y la fecha puede no ser exacta."));
        prefix = TextUtils.format((getConfig().contains("Prefix") ? getConfig().getString("Prefix") : "&c&lPERMADEATH&4&l &7➤ &f"));

        PDCLog.getInstance().log("Se ha recargado el plugin");
        DiscordPortal.reload();
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(this, this);

        if (this.abyssManager != null) {
            getServer().getPluginManager().registerEvents(this.abyssManager, instance);
        }

        this.spawnListener = new SpawnListener(this);
        getServer().getPluginManager().registerEvents(spawnListener, instance);
        getServer().getPluginManager().registerEvents(new AccessoryListener(), instance);
        getServer().getPluginManager().registerEvents(new CustomSkeletons(instance), instance);
        getServer().getPluginManager().registerEvents(new PlayerListener(), instance);
        getServer().getPluginManager().registerEvents(new BlockListener(), instance);
        getServer().getPluginManager().registerEvents(new EntityEvents(), instance);
        getServer().getPluginManager().registerEvents(new TotemListener(), instance);
        getServer().getPluginManager().registerEvents(new RaidEvents(), instance);
        getServer().getPluginManager().registerEvents(new WorldEvents(), instance);
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
        Objects.requireNonNull(getCommand("pdc")).setExecutor(new PDCCommand(instance));
        Objects.requireNonNull(getCommand("pdc")).setTabCompleter(new PDCCommandCompleter());
    }

    private void setupConfig() {

        File f = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        FileAPI.UtilFile c = FileAPI.select(instance, f, config);

        c.set("config-version", 3);
        c.set("Prefix", "&cPermadeath &7➤ &f");
        c.set("ban-enabled", true);
        c.set("anti-afk-enabled", false);
        c.set("AntiAFK.DaysForBan", 7);
        c.set("Toggles.OptifineItems", false);
        c.set("Toggles.DefaultDeathSoundsEnabled", true);
        c.set("Toggles.Netherite.Helmet", 10);
        c.set("Toggles.Netherite.Chestplate", 10);
        c.set("Toggles.Netherite.Leggings", 10);
        c.set("Toggles.Netherite.Boots", 10);
        c.set("Toggles.End.Mob-Spawn-Limit", 70);
        c.set("Toggles.End.Ender-Ghast-Count", 170);
        c.set("Toggles.End.Ender-Creeper-Count", 20);
        c.set("Toggles.End.Protect-End-Spawn", false);
        c.set("Toggles.End.Protect-Radius", 10);
        c.set("Toggles.End.PermadeathDemon.DisplayName", "&6&lPERMADEATH DEMON");
        c.set("Toggles.End.PermadeathDemon.DisplayNameEnraged", "&6&lENRAGED PERMADEATH DEMON");
        c.set("Toggles.End.PermadeathDemon.Health", 1350);
        c.set("Toggles.End.PermadeathDemon.EnragedHealth", 1350);
        c.set("Toggles.End.PermadeathDemon.Optimizar-TNT", false);
        c.set("Toggles.TheBeginning.YticGenerateChance", 100000);
        c.set("Toggles.Spider-Effect", true);
        c.set("Toggles.OP-Ban", true);
        c.set("Toggles.Doble-Mob-Cap", false);
        c.set("Toggles.Replace-Mobs-On-Chunk-Load", true);
        c.set("Toggles.Quantum-Explosion-Power", 60);
        c.set("Toggles.Mike-Creeper-Spawn", true);
        c.set("Toggles.Optimizar-Mob-Spawns", false);
        c.set("Toggles.Gatos-Supernova.Destruir-Bloques", true);
        c.set("Toggles.Gatos-Supernova.Fuego", true);
        c.set("Toggles.Gatos-Supernova.Explosion-Power", 200);
        c.set("Toggles.ExtendToDay90", false);
        c.set("Server-Messages.coords-msg-enable", true);
        c.set("TotemFail.Enable", true);
        c.set("TotemFail.Medalla", "&7¡El jugador %player% ha usado su medalla de superviviente!");
        c.set("TotemFail.ChatMessage", "&7¡El tótem de &c%player% &7ha fallado!");
        c.set("TotemFail.ChatMessageTotems", "&7¡Los tótems de &c%player% &7han fallado!");
        c.set("TotemFail.NotEnoughTotems", "&7¡%player% no tenía suficientes tótems en el inventario!");
        c.set("TotemFail.PlayerUsedTotemMessage", "&7El jugador %player% ha consumido un tótem (Probabilidad: %totem_fail% %porcent% %number%)");
        c.set("TotemFail.PlayerUsedTotemsMessage", "&7El jugador %player% ha consumido {ammount} tótems (Probabilidad: %totem_fail% %porcent% %number%)");
        c.set("Worlds.MainWorld", "world");
        c.set("Worlds.EndWorld", "world_the_end");
        c.set("DontTouch.PlayTime", 0);

        c.save();
        c.load();
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
        }
    }
}
