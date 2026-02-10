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

import java.io.File;
import java.util.Arrays;

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
            if (Main.isRunningFolia()) {
                p.getScheduler().run(main, task -> {
                    if (!p.isOnline()) return;
                    if (p.getWorld().getEnvironment() == World.Environment.THE_END) {
                        if (p.getWorld().getName().equalsIgnoreCase("pdc_the_beginning") || p.getWorld().getName().endsWith("permadeath_beginning")) {
                            if (p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) {
                                p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
                            }
                        }
                    }
                }, null);
            } else {
                if (p.getWorld().getEnvironment() == World.Environment.THE_END) {
                    if (p.getWorld().getName().equalsIgnoreCase("pdc_the_beginning") || p.getWorld().getName().endsWith("permadeath_beginning")) {
                        if (p.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) {
                            p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
                        }
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
            this.beginningWorld.setSpawnLocation(0, 150, 0);
            setupWorldDefaults();
            
            // Forzar generación del portal de salida en los spawn chunks
            if (!data.generatedBeginningPortal()) {
                generatePortal(false, new Location(beginningWorld, 0, 150, 0));
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
            handlePortalTeleport(e.getPlayer(), e.getPlayer().getWorld(), b.getLocation(), e);
        }
    }

    // --- SISTEMA DE TELETRANSPORTE REFORZADO ---

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPaperGateway(PlayerTeleportEndGatewayEvent e) {
        handlePortalTeleport(e.getPlayer(), e.getFrom().getWorld(), e.getGateway().getLocation(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVanillaTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            handlePortalTeleport(e.getPlayer(), e.getFrom().getWorld(), e.getTo(), e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortalEvent(PlayerPortalEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            handlePortalTeleport(e.getPlayer(), e.getFrom().getWorld(), e.getTo(), e);
        }
    }

    private void handlePortalTeleport(Player p, World fromWorld, Location portalBlock, org.bukkit.event.Cancellable event) {
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

        // 1. Verificación de Día
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

        // --- LÓGICA DE ORIENTACIÓN VECTORIAL ---
        Location pLoc = p.getLocation();
        double dx = pLoc.getX() - (portalBlock.getBlockX() + 0.5);
        double dz = pLoc.getZ() - (portalBlock.getBlockZ() + 0.5);
        
        // Calculamos el ángulo hacia afuera del portal
        float exitYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

        // 3. Teletransporte al Beginning
        if (isOverworld) {
            if (event != null) event.setCancelled(true);
            
            // Usamos las coordenadas exactas del portal dentro del NBT (3, 152, 8)
            // Sumamos 0.5 para centrar al jugador en el bloque
            Location to = new Location(beginningWorld, 3.5, 152, 8.5); 
            to.setYaw(exitYaw); 
            to.setPitch(pLoc.getPitch());

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
            
            // Aplicamos el mismo offset relativo (3.5, 2, 8.5)
            Location to = target.clone().add(3.5, 2, 8.5);
            to.setYaw(exitYaw);
            to.setPitch(pLoc.getPitch());
            
            if (Main.isRunningFolia()) {
                p.getScheduler().run(main, task -> {
                    p.teleportAsync(to);
                }, null);
            } else {
                p.teleport(to);
            }
        }
    }

    public void debugStructures(Player p) {
        p.sendMessage(TextUtils.format("&b&l--- PDC Debug: Estructuras ---"));
        String[] structures = {"beginning_portal", "island1", "island2", "island3", "island4", "island5", "ytic"};
        
        File structuresDir = new File(main.getDataFolder(), "data/structures");
        p.sendMessage(TextUtils.format("&7Carpeta: &f" + (structuresDir.exists() ? "&aOK" : "&cNo existe")));

        for (String name : structures) {
            File f = new File(structuresDir, name + ".nbt");
            boolean exists = f.exists();
            
            String status = "&cERROR";
            if (exists) {
                try {
                    var struct = Bukkit.getStructureManager().loadStructure(f);
                    if (struct != null) status = "&aCARGADO OK";
                } catch (Exception e) {
                    status = "&eERROR DE LECTURA: " + e.getMessage();
                }
            } else {
                status = "&7ARCHIVO FALTANTE";
            }
            
            p.sendMessage(TextUtils.format("&7- &f" + name + ": " + status));
        }
        
        p.sendMessage(TextUtils.format("&7Bioma actual: &f" + p.getLocation().getBlock().getBiome().name()));
        p.sendMessage(TextUtils.format("&7Mundo: &f" + p.getWorld().getName()));
    }

    @EventHandler
    public void onSpawnerSpawn(org.bukkit.event.entity.SpawnerSpawnEvent e) {
        if (beginningWorld == null) return;
        if (e.getEntity().getWorld().equals(beginningWorld)) {
            
            // 1. Detectar si es un bloque técnico (Netherite Infernal / Bloques especiales)
            CreatureSpawner spawner = e.getSpawner();
            
            // Si el spawner no tiene tipo o tiene delay negativo, es un bloque técnico. NO TOCAR.
            if (spawner.getSpawnedType() == null || spawner.getDelay() < 0) {
                return;
            }

            // 2. Auto-reparar spawners de cerdos (comunes en NBT mal configurados) a Ghasts
            if (e.getEntityType() == EntityType.PIG) {
                e.setCancelled(true);
                spawner.setSpawnedType(EntityType.GHAST);
                spawner.update();
                spawnDefinitiveMob(e.getLocation(), EntityType.GHAST);
                return;
            } 
            
            // 3. Aplicar Atributos "Definitivos" según el tipo de mob
            buffBeginningMob(e.getEntity());
        }
    }

    private void spawnDefinitiveMob(Location loc, EntityType type) {
        Entity entity = main.getNmsHandler().spawnNMSEntity(type.name(), type, loc, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER);
        if (entity instanceof LivingEntity liv) buffBeginningMob(liv);
    }

    private void buffBeginningMob(Entity entity) {
        if (!(entity instanceof LivingEntity liv)) return;

        if (liv instanceof Wither) {
            // Los Withers no tienen nombre. Solo aplicamos vida si el NBT no la definió (es menor a 300)
            if (liv.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getBaseValue() <= 300.0) {
                main.getNmsAccessor().setMaxHealth(liv, 500.0D, true);
            }
            return; // No aplicamos los atributos comunes de 100 HP a los Withers
        }

        // Atributos comunes para el resto de mobs de The Beginning (100 HP)
        main.getNmsAccessor().setMaxHealth(liv, 100.0D, true);

        if (liv instanceof Vex) {
            liv.setCustomName(TextUtils.format("&6Vex Definitivo"));
        } else if (liv instanceof Ghast) {
            main.getNmsAccessor().setMaxHealth(liv, 150.0D, true);
            liv.setCustomName(TextUtils.format("&6Ender Ghast Definitivo"));
        } else if (liv instanceof Creeper c) {
            c.setCustomName(TextUtils.format("&6Quantum Creeper"));
            c.setExplosionRadius(7);
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
            boolean hasItems = Arrays.stream(chest.getBlockInventory().getContents())
                    .anyMatch(item -> item != null && item.getType() != Material.AIR);

            if (!hasItems && main.getDay() < 60) {
                new BeginningLootTable(this).populateChest(chest);
            }
            data.addPopulatedChest(chest.getLocation());
        }
    }

    public void generatePortal(boolean overworld, Location location) {
        if (!overworld) {
            // Force inner portal to spawn at 0, 150, 0
            location = new Location(beginningWorld, 0, 150, 0);
            beginningWorld.setSpawnLocation(0, 150, 0);
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
            }, 60L, 60L);
        } else {
            Bukkit.getScheduler().runTaskTimer(main, spawner, 60L, 60L);
        }
    }

    private Location findSpawnLocation(Location center) {
        java.util.SplittableRandom random = new java.util.SplittableRandom();
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(20) - 10;
            int z = random.nextInt(20) - 10;
            int y = random.nextInt(10) - 5;
            
            Location loc = center.clone().add(x, y, z);
            
            // Safe zone check: Don't spawn near portal (0, 150, 0)
            if (loc.distanceSquared(new Location(beginningWorld, 0, 150, 0)) < 25 * 25) continue;

            if (loc.getBlock().getType() == Material.AIR && loc.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                if (loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
                    return loc;
                }
            }
        }
        return null;
    }
}