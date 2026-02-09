package dev.itsrealperson.permadeath.world.beginning;

import com.destroystokyo.paper.event.player.PlayerTeleportEndGatewayEvent;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.data.BeginningDataManager;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.world.WorldEditPortal;
import dev.itsrealperson.permadeath.world.beginning.generator.BeginningLootTable;

public class BeginningManager implements Listener, PermadeathModule {

    private static final String WORLD_NAME = "permadeath/beginning";
    private Main main;
    private World beginningWorld;
    private BeginningDataManager data;
    private boolean closed = false;

    public boolean isClosed() {
        return closed;
    }

    public BeginningManager(Main main) {
        this.main = main;
        this.data = main.getBeData();
    }

    @Override
    public String getName() {
        return "BeginningModule";
    }

    @Override
    public void onEnable() {
        this.beginningWorld = Bukkit.getWorld(WORLD_NAME);
        Bukkit.getPluginManager().registerEvents(this, main);
        
        DateManager dm = DateManager.getInstance();
        if (main.getDay() >= dm.getBeginningDay()) {
            if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null && Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") == null) {
                Bukkit.broadcastMessage(TextUtils.format(main.getPrefix() + "&4&lNo se pudo registrar TheBeginning ya que no se ha encontrado el plugin &7WorldEdit"));
                return;
            }
            loadWorld();
            startSpawnerTask();
            Bukkit.getConsoleSender().sendMessage(TextUtils.format(main.getPrefix() + "&eSe han registrado cambios de TheBeginning"));
        }
    }

    @Override
    public void onDisable() {
        // Limpieza si es necesaria
    }

    @Override
    public void onTick() {
        if (main.getDay() < 30) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().getEnvironment() == World.Environment.THE_END) {
                if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.LEVITATION, 10 * 20, 9));
                }
                if (p.getWorld().getName().equalsIgnoreCase("pdc_the_beginning") || p.getWorld().getName().endsWith("permadeath_beginning")) {
                    if (p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) {
                        p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        if (event.getWorld().getName().endsWith("permadeath_beginning") || event.getWorld().getName().endsWith("permadeath/beginning")) {
            if (event.getWorld().getPopulators().stream().noneMatch(p -> p instanceof BeginningPopulator)) {
                event.getWorld().getPopulators().add(new BeginningPopulator());
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Poblador de The Beginning inyectado.");
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        String name = event.getWorld().getName();
        if (name.endsWith("permadeath_beginning") || name.endsWith("permadeath/beginning")) {
            this.beginningWorld = event.getWorld();
            setupWorldDefaults();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Dimensión The Beginning vinculada.");
        }
    }

    public void loadWorld() {
        this.beginningWorld = Bukkit.getWorld(WORLD_NAME);
        
        if (this.beginningWorld == null) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getName().endsWith("permadeath_beginning")) {
                    this.beginningWorld = w;
                    break;
                }
            }
        }

        if (this.beginningWorld == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] Cargando The Beginning...");
            try {
                WorldCreator creator = new WorldCreator(WORLD_NAME);
                creator.environment(World.Environment.THE_END);
                creator.generateStructures(false);
                this.beginningWorld = Bukkit.createWorld(creator);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Permadeath] Error al cargar The Beginning: " + e.getMessage());
            }
        }

        if (this.beginningWorld != null) {
            this.beginningWorld.setSpawnLocation(0, 100, 0);
            setupWorldDefaults();
            
            // Forzar generación del portal de salida en los spawn chunks
            if (!data.generatedBeginningPortal()) {
                generatePortal(false, new Location(beginningWorld, 0, 100, 0));
            }
        }
    }

    private void setupWorldDefaults() {
        if (beginningWorld == null) return;
        
        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().run(main, task -> {
                beginningWorld.setGameRule(GameRule.MOB_GRIEFING, false);
                if (main.getConfig().getBoolean("Toggles.Doble-Mob-Cap")) {
                    beginningWorld.setMonsterSpawnLimit(140);
                }
            });
        } else {
            beginningWorld.setGameRule(GameRule.MOB_GRIEFING, false);
            if (main.getConfig().getBoolean("Toggles.Doble-Mob-Cap")) {
                beginningWorld.setMonsterSpawnLimit(140);
            }
        }
    }

    public void closeBeginning() {
        if (beginningWorld == null) return;
        beginningWorld.getPlayers().forEach(p -> {
            if (Main.isRunningFolia()) {
                p.teleportAsync(main.world.getSpawnLocation());
            } else {
                p.teleport(main.world.getSpawnLocation());
            }
            p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
        });
        Bukkit.broadcastMessage(TextUtils.format(main.prefix + "&eThe Beginning ha cerrado temporalmente (DeathTrain)."));
        this.closed = true;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public World getBeginningWorld() {
        return beginningWorld;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        if (beginningWorld == null) return;
        
        Block b = e.getTo().getBlock();
        if (b.getType() == Material.END_GATEWAY) {
            Player p = e.getPlayer();
            World w = p.getWorld();
            
            boolean isOverworld = w.getEnvironment() == World.Environment.NORMAL;
            boolean isBeginning = w.getName().endsWith("permadeath_beginning") || w.getName().endsWith("permadeath/beginning");
            
            if (isOverworld || isBeginning) {
                handlePortalTeleport(p, w, e);
            }
        }
    }

    // --- SISTEMA DE TELETRANSPORTE REFORZADO ---

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPaperGateway(PlayerTeleportEndGatewayEvent e) {
        handlePortalTeleport(e.getPlayer(), e.getFrom().getWorld(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVanillaTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            handlePortalTeleport(e.getPlayer(), e.getFrom().getWorld(), e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortalEvent(PlayerPortalEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            handlePortalTeleport(e.getPlayer(), e.getFrom().getWorld(), e);
        }
    }

    private void handlePortalTeleport(Player p, World fromWorld, org.bukkit.event.Cancellable event) {
        if (beginningWorld == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Permadeath-Debug] Abortado: beginningWorld es NULL");
            return;
        }

        // Cooldown Check
        if (p.hasMetadata("pdc_tp_cooldown")) {
            long lastTp = p.getMetadata("pdc_tp_cooldown").get(0).asLong();
            if (System.currentTimeMillis() - lastTp < 2000) { // 2 seconds cooldown
                if (event != null && !(event instanceof org.bukkit.event.player.PlayerMoveEvent)) {
                    event.setCancelled(true);
                }
                return;
            }
        }
        p.setMetadata("pdc_tp_cooldown", new org.bukkit.metadata.FixedMetadataValue(main, System.currentTimeMillis()));

        boolean isOverworld = fromWorld.getEnvironment() == World.Environment.NORMAL;
        boolean isBeginning = fromWorld.getName().endsWith("permadeath_beginning") || fromWorld.getName().endsWith("permadeath/beginning");

        if (!isOverworld && !isBeginning) return;

        // 1. Verificaci\u00f3n de D\u00eda
        if (main.getDay() < DateManager.getInstance().getBeginningAccessDay()) {
            if (event != null) event.setCancelled(true);
            p.setNoDamageTicks(p.getMaximumNoDamageTicks());
            p.damage(p.getHealth() + 1.0D);
            Bukkit.broadcastMessage(TextUtils.format("&c&lEl jugador &4&l" + p.getName() + " &c&lentró a The Beginning antes de tiempo."));
            return;
        }

        // 2. Verificación de Estado (Cerrado)
        if (isClosed()) {
            if (event != null) event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "The Beginning está cerrado actualmente.");
            return;
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath-Debug] Intentando teleportar " + p.getName() + " desde " + fromWorld.getName());

        // 3. Teletransporte al Beginning
        if (isOverworld) {
            if (event != null) event.setCancelled(true);
            
            // Aparecer justo encima del portal en The Beginning (0, 100, 0)
            Location to = new Location(beginningWorld, 0.5, 101, 0.5); 
            to.setYaw(0f); 

            if (Main.isRunningFolia()) {
                p.getScheduler().run(main, task -> {
                    p.teleportAsync(to).thenAccept(success -> {
                        if (success) {
                            p.sendMessage(TextUtils.format("&eBienvenido a &b&lThe Beginning&e."));
                        }
                    });
                }, null);
            } else {
                p.teleport(to);
                p.sendMessage(TextUtils.format("&eBienvenido a &b&lThe Beginning&e."));
            }
        }

        // 4. Teletransporte de vuelta al Overworld
        if (isBeginning) {
            if (event != null) event.setCancelled(true);
            
            Location target = data.getOverWorldPortal();
            if (target == null) target = main.world.getSpawnLocation();
            
            // Aparecer justo encima del portal del Overworld
            Location to = target.clone().add(0.5, 1, 0.5);
            
            if (Main.isRunningFolia()) {
                p.getScheduler().run(main, task -> {
                    p.teleportAsync(to);
                }, null);
            } else {
                p.teleport(to);
            }
        }
    }

    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        if (beginningWorld == null || !e.getPlayer().getWorld().equals(beginningWorld)) return;
        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Chest holder) {
            populateChest(holder);
        }
    }

    private void populateChest(Chest chest) {
        if (!data.hasPopulatedChest(chest.getLocation())) {
            if (main.getDay() < 60) {
                new BeginningLootTable(this).populateChest(chest);
            }
            data.addPopulatedChest(chest.getLocation());
        }
    }

    public void generatePortal(boolean overworld, Location location) {
        if (!overworld) {
            // Force inner portal to spawn at 0, 100, 0
            location = new Location(beginningWorld, 0, 100, 0);
            beginningWorld.setSpawnLocation(0, 100, 0);
        }
        WorldEditPortal.generatePortal(overworld, location);
    }

    private void startSpawnerTask() {
        Runnable spawner = () -> {
            if (beginningWorld == null) return;
            
            for (Player p : beginningWorld.getPlayers()) {
                if (p.getGameMode() == GameMode.SPECTATOR) continue;
                
                // Count mobs near player to avoid overcrowding
                long nearbyMobs = p.getNearbyEntities(30, 30, 30).stream()
                        .filter(e -> e instanceof Monster || e instanceof Ghast)
                        .count();
                
                if (nearbyMobs < 10) {
                    Location spawnLoc = findSpawnLocation(p.getLocation());
                    if (spawnLoc != null) {
                        BeginningMobs.spawnMob(spawnLoc);
                    }
                }
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(main, t -> {
                if (beginningWorld == null) return;
                for (Player p : beginningWorld.getPlayers()) {
                     p.getScheduler().run(main, task -> {
                         if (p.getGameMode() == GameMode.SPECTATOR) return;
                         long nearbyMobs = p.getNearbyEntities(30, 30, 30).stream()
                                 .filter(e -> e instanceof Monster || e instanceof Ghast)
                                 .count();
                         if (nearbyMobs < 10) {
                             Location spawnLoc = findSpawnLocation(p.getLocation());
                             if (spawnLoc != null) {
                                 BeginningMobs.spawnMob(spawnLoc);
                             }
                         }
                     }, null);
                }
            }, 100L, 100L);
        } else {
            Bukkit.getScheduler().runTaskTimer(main, spawner, 100L, 100L);
        }
    }

    private Location findSpawnLocation(Location center) {
        java.util.SplittableRandom random = new java.util.SplittableRandom();
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(20) - 10;
            int z = random.nextInt(20) - 10;
            int y = random.nextInt(10) - 5;
            
            Location loc = center.clone().add(x, y, z);
            
            // Safe zone check: Don't spawn near portal (0, 100, 0)
            if (loc.distanceSquared(new Location(beginningWorld, 0, 100, 0)) < 25 * 25) continue;

            if (loc.getBlock().getType() == Material.AIR && loc.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                if (loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
                    return loc;
                }
            }
        }
        return null;
    }
}
