package tech.sebazcrc.permadeath.world.abyss;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Monster;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.AdvancementManager;

import tech.sebazcrc.permadeath.util.item.PermadeathItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbyssManager implements Listener {

    private static final String WORLD_NAME = "permadeath/abyss";
    private final Main plugin;
    private World abyssWorld;
    private final Map<UUID, BossBar> pressureBars = new HashMap<>();

    public AbyssManager(Main plugin) {
        this.plugin = plugin;
        this.abyssWorld = Bukkit.getWorld(WORLD_NAME);
    }

    public void startSpawnerTask() {
        Runnable task = () -> {
            if (abyssWorld == null) return;
            
            for (Player p : abyssWorld.getPlayers()) {
                if (p.getGameMode() == GameMode.SPECTATOR || !p.isOnline()) continue;
                
                // En Folia, debemos ejecutar el chequeo en el hilo de la región del jugador
                if (Main.isRunningFolia()) {
                    p.getScheduler().run(plugin, t -> {
                        checkAndSpawnAbyssMobs(p);
                    }, null);
                } else {
                    checkAndSpawnAbyssMobs(p);
                }
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), 60L, 60L); // Cada 3s
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, 60L, 60L);
        }
    }

    private void checkAndSpawnAbyssMobs(Player p) {
        // Cap local de mobs cerca del jugador aumentado a 70
        long nearbyMobs = p.getNearbyEntities(64, 64, 64).stream()
                .filter(e -> e instanceof org.bukkit.entity.Monster || e instanceof org.bukkit.entity.Bat)
                .count();
        
        if (nearbyMobs < 70) {
            int toSpawn = 4 + new java.util.Random().nextInt(5);
            for (int i = 0; i < toSpawn; i++) {
                Location loc = findAbyssSpawnLocation(p.getLocation());
                if (loc != null) {
                    spawnAbyssMob(loc);
                }
            }
        }
    }

    private Location findAbyssSpawnLocation(Location base) {
        java.util.Random random = new java.util.Random();
        // Aumentar intentos y rango vertical para encontrar islas
        for (int i = 0; i < 30; i++) {
            int x = base.getBlockX() + random.nextInt(41) - 20;
            int z = base.getBlockZ() + random.nextInt(41) - 20;
            int y = base.getBlockY() + random.nextInt(31) - 15;
            
            if (y < -60 || y > 120) continue;

            Location loc = new Location(base.getWorld(), x + 0.5, y, z + 0.5);
            if (loc.getBlock().getType().isAir() && loc.clone().add(0, 1, 0).getBlock().getType().isAir() && loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
                if (loc.distanceSquared(base) > 8 * 8) return loc;
            }
        }
        return null;
    }

    private void spawnAbyssMob(Location loc) {
        String[] deepDarkMobs = {"SilentSeeker", "SculkParasite", "EchoArcher", "HollowGuard"};
        String selected = deepDarkMobs[new java.util.Random().nextInt(deepDarkMobs.length)];
        
        org.bukkit.entity.Entity e = plugin.getNmsHandler().spawnNMSCustomEntity(selected, null, loc, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (e instanceof org.bukkit.entity.LivingEntity liv) {
            liv.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
            liv.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
        }
    }

    @EventHandler
    public void onWorldInit(org.bukkit.event.world.WorldInitEvent event) {
        if (event.getWorld().getName().endsWith("permadeath_abyss") || event.getWorld().getName().endsWith("permadeath/abyss")) {
            if (event.getWorld().getPopulators().stream().noneMatch(p -> p instanceof AbyssPopulator)) {
                event.getWorld().getPopulators().add(new AbyssPopulator());
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Poblador abisal inyectado durante la inicialización de " + event.getWorld().getName());
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        String name = event.getWorld().getName();
        if (name.endsWith("permadeath_abyss") || name.endsWith("permadeath/abyss")) {
            this.abyssWorld = event.getWorld();
            
            // Fallback por si WorldInit no se disparó
            if (this.abyssWorld.getPopulators().stream().noneMatch(p -> p instanceof AbyssPopulator)) {
                this.abyssWorld.getPopulators().add(new AbyssPopulator());
            }

            hideDragonBar(this.abyssWorld);
        }
    }

    private void hideDragonBar(World world) {
        try {
            Object battle = world.getClass().getMethod("getDragonBattle").invoke(world);
            if (battle != null) {
                Object bar = battle.getClass().getMethod("getBossBar").invoke(battle);
                if (bar != null) {
                    bar.getClass().getMethod("setVisible", boolean.class).invoke(bar, false);
                }
            }
        } catch (Exception ignored) {}
    }

    public void loadWorld() {
        this.abyssWorld = Bukkit.getWorld(WORLD_NAME);
        
        // Si no se encuentra por el nombre estándar, buscar en la lista de mundos cargados
        if (this.abyssWorld == null) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getName().endsWith("permadeath_abyss") || w.getName().endsWith("permadeath/abyss")) {
                    this.abyssWorld = w;
                    break;
                }
            }
        }
        
        if (this.abyssWorld == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] La dimensión abisal no está activa. Intentando cargar " + WORLD_NAME + "...");
            try {
                WorldCreator creator = new WorldCreator(WORLD_NAME);
                creator.environment(World.Environment.NORMAL);
                creator.generateStructures(false);
                this.abyssWorld = Bukkit.createWorld(creator);
                
                if (this.abyssWorld != null) {
                    // Registrar el poblador inmediatamente si no está
                    if (this.abyssWorld.getPopulators().stream().noneMatch(p -> p instanceof AbyssPopulator)) {
                        this.abyssWorld.getPopulators().add(new AbyssPopulator());
                    }
                    
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Dimensión Abisal cargada correctamente.");
                    hideDragonBar(this.abyssWorld);
                }
            } catch (Exception e) {
                String error = e.getMessage() != null ? e.getMessage() : e.toString();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Permadeath] ERROR al cargar el Abismo: " + error);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Mundos detectados: " + 
                    Bukkit.getWorlds().stream().map(World::getName).collect(java.util.stream.Collectors.joining(", ")));
            }
        }
    }

    public void teleportToAbyss(Player player) {
        if (!Main.instance.isExtendedDifficulty()) {
            player.sendMessage(ChatColor.RED + "El Abismo está sellado. Necesitas despertar el Corazón del Abismo para entrar.");
            return;
        }

        if (abyssWorld == null) loadWorld();

        if (abyssWorld != null) {
            // Buscar un lugar seguro (Y=60 es donde el poblador genera las islas)
            Location spawn = new Location(abyssWorld, 8, 62, 8);
            
            if (Main.isRunningFolia()) {
                player.teleportAsync(spawn).thenAccept(success -> {
                    if (success) playAbyssSound(player);
                });
            } else {
                player.teleport(spawn);
                playAbyssSound(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Error: La dimensión permadeath:abyss no está cargada. ¿Instalaste el datapack?");
        }
    }

    @EventHandler
    public void onConsumePotion(org.bukkit.event.player.PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() == Material.POTION && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Respiración Abisal")) {
            Player p = e.getPlayer();
            // Establecer tiempo de inmunidad (120 segundos)
            long expiry = System.currentTimeMillis() + (120 * 1000);
            p.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyss_potion_expiry"), PersistentDataType.LONG, expiry);
            p.sendMessage(ChatColor.AQUA + "Has ganado inmunidad a la presión abisal por 2 minutos.");
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 2.0f);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(org.bukkit.event.player.PlayerChangedWorldEvent e) {
        String worldName = e.getPlayer().getWorld().getName();
        if (!(worldName.endsWith("permadeath_abyss") || worldName.endsWith("permadeath/abyss"))) {
            removePressureBar(e.getPlayer());
        }
    }

    @EventHandler
    public void onTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
        if (e.getTo() == null) return;
        String toWorld = e.getTo().getWorld().getName();
        if (!(toWorld.endsWith("permadeath_abyss") || toWorld.endsWith("permadeath/abyss"))) {
            removePressureBar(e.getPlayer());
        }
    }

    public void tickAbyssEffects(Player player) {
        String worldName = player.getWorld().getName();
        if (!(worldName.endsWith("permadeath_abyss") || worldName.endsWith("permadeath/abyss"))) {
            removePressureBar(player);
            return;
        }

        if (this.abyssWorld == null) this.abyssWorld = player.getWorld();

        BossBar bar = pressureBars.computeIfAbsent(player.getUniqueId(), id -> 
            Bukkit.createBossBar(ChatColor.DARK_AQUA + "Presión del Abismo", BarColor.BLUE, BarStyle.SEGMENTED_10));
        
        if (!bar.getPlayers().contains(player)) {
            bar.addPlayer(player);
            bar.setVisible(true);
        }

        // --- Chequeo de Poción ---
        long now = System.currentTimeMillis();
        long expiry = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "abyss_potion_expiry"), PersistentDataType.LONG, 0L);
        boolean hasPotionEffect = now < expiry;

        if (hasPotionEffect) {
            long remaining = expiry - now;
            double progress = (double) remaining / (120 * 1000);
            
            bar.setTitle(ChatColor.LIGHT_PURPLE + "Respiración Abisal (" + (remaining / 1000) + "s)");
            bar.setColor(BarColor.PURPLE);
            bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
            
            // Limpiar borde si tiene poción
            if (player.getWorldBorder() != null) player.setWorldBorder(null);
            return;
        }

        // --- Lógica de la Máscara en Accesorios ---
        ItemStack mask = null;
        int maskIndex = -1;
        ItemStack[] acc = tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.load(player);

        if (acc != null) {
            for (int i = 0; i < acc.length; i++) {
                if (PermadeathItems.isAbyssalMask(acc[i])) {
                    mask = acc[i];
                    maskIndex = i;
                    break;
                }
            }
        }

        double currentPressure = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, 1.0);

        if (mask != null && mask.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable damageable) {
            player.getPersistentDataContainer().remove(new NamespacedKey(plugin, "abyss_grace_ticks"));
            
            short maxDurability = mask.getType().getMaxDurability();
            int currentDamage = damageable.getDamage();
            
            // Sincronizar presión con durabilidad
            double durabilityPercent = 1.0 - ((double) currentDamage / maxDurability);
            currentPressure = durabilityPercent;

            int enchantLevel = 0;
            try {
                NamespacedKey key = new NamespacedKey("permadeath", "abyssal_breathing");
                enchantLevel = mask.getEnchantmentLevel(org.bukkit.Registry.ENCHANTMENT.get(key));
            } catch (Exception ignored) {}

            int chance = 5 + (enchantLevel * 5);

            if (new java.util.Random().nextInt(chance) == 0) { 
                damageable.setDamage(currentDamage + 1);
                mask.setItemMeta(damageable);
                
                if (acc != null && maskIndex != -1) {
                    acc[maskIndex] = mask;
                    tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.saveFromItems(player, acc);
                }
                currentPressure = 1.0 - ((double) (currentDamage + 1) / maxDurability);
            }
            
            bar.setTitle(ChatColor.AQUA + "Oxígeno de la Máscara");
            bar.setColor(currentPressure > 0.3 ? BarColor.BLUE : BarColor.YELLOW);
        } else {
            // Sin máscara: Tiempo de Gracia
            int grace = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "abyss_grace_ticks"), PersistentDataType.INTEGER, 30);
            
            if (grace > 0) {
                grace--;
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyss_grace_ticks"), PersistentDataType.INTEGER, grace);
                bar.setTitle(ChatColor.DARK_AQUA + "Presión del Abismo (" + grace + "s)");
                bar.setProgress((double) grace / 30.0);
                bar.setColor(BarColor.YELLOW);
                
                if (player.getWorldBorder() != null) player.setWorldBorder(null);
                return;
            }

            currentPressure -= 0.005;
            bar.setTitle(ChatColor.DARK_AQUA + "Presión del Abismo");
            bar.setColor(BarColor.BLUE);
        }

        if (currentPressure <= 0) {
            currentPressure = 0;
            player.damage(2.0);
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.WITHER, 40, 1));
            bar.setColor(BarColor.RED);
            bar.setTitle(ChatColor.RED + "¡OXÍGENO AGOTADO!");
        }
        
        bar.setProgress(Math.max(0.0, Math.min(1.0, currentPressure)));
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, currentPressure);

        // Efecto visual de borde rojo
        if (currentPressure < 0.2 && currentPressure > 0) {
            WorldBorder wb = player.getWorldBorder();
            if (wb == null || wb.getSize() > 50000) {
                wb = Bukkit.createWorldBorder();
                wb.setCenter(player.getLocation());
                wb.setSize(10000);
                wb.setWarningDistance(10000);
                player.setWorldBorder(wb);
            } else {
                wb.setCenter(player.getLocation());
            }
        } else if (player.getWorldBorder() != null) {
            player.setWorldBorder(null);
        }

        Location loc = player.getLocation();
        player.spawnParticle(Particle.ASH, loc, 50, 8, 4, 8, 0.02);
        
        if (new java.util.Random().nextInt(40) == 0) {
            Sound[] abyssSounds = {Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, Sound.ENTITY_WARDEN_HEARTBEAT, Sound.AMBIENT_CAVE, Sound.BLOCK_SCULK_CATALYST_BLOOM};
            player.playSound(loc, abyssSounds[new java.util.Random().nextInt(abyssSounds.length)], 0.4f, 0.5f);
        }
    }

    private void removePressureBar(Player player) {
        BossBar bar = pressureBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.setVisible(false);
            bar.removeAll();
        }
        
        if (player.getWorldBorder() != null) {
            player.setWorldBorder(null);
        }
        
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, "abyss_grace_ticks"));
    }

    private void playAbyssSound(Player player) {
        player.sendMessage(ChatColor.DARK_GRAY + "Has descendido al Abismo Profundo...");
        player.playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 1.0f, 0.5f);
        AdvancementManager.grantAdvancement(player, AdvancementManager.PDA.VOID_EXPLORER);
    }

    private ItemStack findAbyssalMask(Player player) {
        // Solo buscar en accesorios
        ItemStack[] acc = tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.load(player);
        if (acc != null) {
            for (ItemStack item : acc) {
                if (PermadeathItems.isAbyssalMask(item)) return item;
            }
        }
        return null;
    }

    public World getAbyssWorld() { return abyssWorld; }

    public void onAbyssSpawn(org.bukkit.event.entity.CreatureSpawnEvent event) {
        if (abyssWorld == null || !event.getLocation().getWorld().equals(abyssWorld)) return;
        
        // --- Permitir Wardens Vanilla ---
        if (event.getEntityType() == EntityType.WARDEN) {
            long wardenCount = event.getLocation().getWorld().getNearbyEntities(event.getLocation(), 100, 100, 100).stream()
                    .filter(e -> e.getType() == EntityType.WARDEN)
                    .count();
            
            if (wardenCount >= 10) {
                event.setCancelled(true);
            }
            return; // No cancelar si cumple el límite
        }

        // Bloquear todos los spawns naturales que no sean nuestros custom
        if (event.getSpawnReason() != org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
            Location loc = event.getLocation();
            java.util.Random random = new java.util.Random();
            
            if (random.nextInt(100) < 35) {
                String[] deepDarkMobs = {"SilentSeeker", "SculkParasite", "EchoArcher", "HollowGuard", "TwistedWarden"};
                String selected = deepDarkMobs[random.nextInt(deepDarkMobs.length)];
                
                if (selected.equals("TwistedWarden") && random.nextInt(10) != 0) {
                    selected = "SilentSeeker";
                }
                
                org.bukkit.entity.Entity e = plugin.getNmsHandler().spawnNMSCustomEntity(selected, null, loc, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
                if (e instanceof org.bukkit.entity.LivingEntity liv) {
                    liv.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
                    liv.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
                }
            }
        }
    }
}
