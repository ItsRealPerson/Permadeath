package tech.sebazcrc.permadeath.world.abyss;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase(WORLD_NAME)) {
            this.abyssWorld = event.getWorld();
            
            if (this.abyssWorld.getPopulators().stream().noneMatch(p -> p instanceof AbyssPopulator)) {
                this.abyssWorld.getPopulators().add(new AbyssPopulator());
            }

            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Dimensión Abisal del Datapack vinculada.");
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
        
        if (this.abyssWorld == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Permadeath] La dimensión " + WORLD_NAME + " no está activa. Intentando cargar...");
            try {
                // Forzamos el generador vacío para que no aparezcan bloques de Overworld
                WorldCreator creator = new WorldCreator(WORLD_NAME);
                creator.environment(World.Environment.NORMAL);
                creator.generator(new tech.sebazcrc.permadeath.world.abyss.generator.EmptyGenerator());
                creator.generateStructures(false);
                this.abyssWorld = Bukkit.createWorld(creator);
                
                if (this.abyssWorld != null) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Permadeath] Dimensión " + WORLD_NAME + " cargada exitosamente.");
                    hideDragonBar(this.abyssWorld);
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Permadeath] ERROR crítico al cargar el Abismo: " + e.getMessage());
                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Mundos disponibles: " + 
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

    public void tickAbyssEffects(Player player) {
        if (abyssWorld == null || !player.getWorld().equals(abyssWorld)) {
            removePressureBar(player);
            return;
        }

        BossBar bar = pressureBars.computeIfAbsent(player.getUniqueId(), id -> 
            Bukkit.createBossBar(ChatColor.DARK_AQUA + "Presión del Abismo", BarColor.BLUE, BarStyle.SEGMENTED_10));
        
        if (!bar.getPlayers().contains(player)) bar.addPlayer(player);

        // --- Chequeo de Poción ---
        long expiry = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "abyss_potion_expiry"), PersistentDataType.LONG, 0L);
        boolean hasPotionEffect = System.currentTimeMillis() < expiry;

        if (hasPotionEffect) {
            bar.setTitle(ChatColor.LIGHT_PURPLE + "Respiración Abisal (Activa)");
            bar.setColor(BarColor.PURPLE);
            bar.setProgress(1.0);
            return; // Inmunidad total, no desgasta máscara ni baja presión
        }

        // --- Lógica de la Máscara en Accesorios ---
        org.bukkit.inventory.Inventory openInv = player.getOpenInventory().getTopInventory();
        boolean isMenuOpen = player.getOpenInventory().getTitle().equals(TextUtils.format("&8Inventario de Accesorios"));
        
        ItemStack mask = null;
        int maskIndex = -1;
        ItemStack[] acc = null;

        if (isMenuOpen) {
            mask = openInv.getItem(tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.MASK_SLOT);
            if (!PermadeathItems.isAbyssalMask(mask)) mask = null;
        } else {
            acc = tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.load(player);
            if (acc != null) {
                for (int i = 0; i < acc.length; i++) {
                    if (PermadeathItems.isAbyssalMask(acc[i])) {
                        mask = acc[i];
                        maskIndex = i;
                        break;
                    }
                }
            }
        }

        double currentPressure = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, 1.0);

        if (mask != null && mask.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable damageable) {
            short maxDurability = mask.getType().getMaxDurability();
            int currentDamage = damageable.getDamage();
            
            // Sincronizar presión con durabilidad
            double durabilityPercent = 1.0 - ((double) currentDamage / maxDurability);
            currentPressure = durabilityPercent;

            // Desgastarla mientras está en el abismo
            if (new java.util.Random().nextInt(5) == 0) { 
                damageable.setDamage(currentDamage + 1);
                mask.setItemMeta(damageable);
                
                if (isMenuOpen) {
                    // Actualizar el slot directamente en la interfaz
                    openInv.setItem(tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.MASK_SLOT, mask);
                } else {
                    // Guardar el cambio en el array de accesorios y persistirlo
                    acc[maskIndex] = mask;
                    tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.saveFromItems(player, acc);
                }
                
                // Actualizar presión tras el daño
                currentPressure = 1.0 - ((double) (currentDamage + 1) / maxDurability);
            }
            
            bar.setTitle(ChatColor.AQUA + "Oxígeno de la Máscara");
        } else {
            // Sin máscara: La presión baja mucho más rápido
            currentPressure -= 0.005;
            bar.setTitle(ChatColor.DARK_AQUA + "Presión del Abismo");
        }

        if (currentPressure <= 0) {
            currentPressure = 0;
            player.damage(2.0);
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.WITHER, 40, 1));
            bar.setColor(BarColor.RED);
            bar.setTitle(ChatColor.RED + "¡OXÍGENO AGOTADO!");
        } else {
            bar.setColor(currentPressure > 0.3 ? BarColor.BLUE : BarColor.YELLOW);
            bar.setProgress(Math.min(1.0, currentPressure));
        }
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, currentPressure);

        Location loc = player.getLocation();
        player.spawnParticle(Particle.ASH, loc, 50, 8, 4, 8, 0.02);
        player.spawnParticle(Particle.SQUID_INK, loc, 10, 5, 3, 5, 0.01);

        if (new java.util.Random().nextInt(40) == 0) {
            Sound[] abyssSounds = {Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, Sound.ENTITY_WARDEN_HEARTBEAT, Sound.AMBIENT_CAVE, Sound.BLOCK_SCULK_CATALYST_BLOOM};
            player.playSound(loc, abyssSounds[new java.util.Random().nextInt(abyssSounds.length)], 0.4f, 0.5f);
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
        if (abyssWorld == null || !e.getBlock().getWorld().equals(abyssWorld)) return;
        
        org.bukkit.block.Block b = e.getBlock();
        if (b.getType() == Material.DEEPSLATE_EMERALD_ORE) {
            e.setDropItems(false); // Cancelar drop vanilla
            
            Player p = e.getPlayer();
            ItemStack tool = p.getInventory().getItemInMainHand();
            
            // Solo soltar si usa al menos un pico de hierro
            if (tool.getType().name().contains("PICKAXE") && !tool.getType().name().contains("WOODEN") && !tool.getType().name().contains("STONE")) {
                int amount = 1;
                
                // Soporte para Fortuna
                if (tool.containsEnchantment(org.bukkit.enchantments.Enchantment.FORTUNE)) {
                    int level = tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.FORTUNE);
                    amount += new java.util.Random().nextInt(level + 1);
                }
                
                ItemStack drop = PermadeathItems.createAbyssalOre();
                drop.setAmount(amount);
                
                b.getWorld().dropItemNaturally(b.getLocation(), drop);
                e.setExpToDrop(new java.util.Random().nextInt(5) + 3);
            }
        }
    }

    @EventHandler
    public void onAbyssInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || abyssWorld == null || !e.getClickedBlock().getWorld().equals(abyssWorld)) return;
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        org.bukkit.block.Block b = e.getClickedBlock();

        // 1. Procesamiento de Ores con Bloque de Netherite Infernal
        if (plugin.getNetheriteBlock() != null && plugin.getNetheriteBlock().isInfernalNetherite(b.getLocation())) {
            if (item != null && item.getType().name().startsWith("RAW_")) {
                e.setCancelled(true);
                
                // Transmutar mineral en bruto a Mineral Abisal
                item.setAmount(item.getAmount() - 1);
                p.getWorld().dropItemNaturally(b.getLocation().add(0, 1, 0), PermadeathItems.createAbyssalOre());
                
                p.playSound(b.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 0.5f);
                p.spawnParticle(Particle.WITCH, b.getLocation().add(0.5, 1.1, 0.5), 20, 0.2, 0.2, 0.2, 0.1);
                p.sendMessage(ChatColor.DARK_PURPLE + "La energía del abismo ha transmutado el mineral.");
            }
        }
    }

    @EventHandler
    public void onUseFilter(org.bukkit.event.player.PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || !item.isSimilar(PermadeathItems.createAbyssalFilter())) return;
        if (!e.getAction().name().contains("RIGHT_CLICK")) return;

        ItemStack[] acc = tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.load(p);
        if (acc != null) {
            for (int i = 0; i < acc.length; i++) {
                if (PermadeathItems.isAbyssalMask(acc[i]) && acc[i].getItemMeta() instanceof org.bukkit.inventory.meta.Damageable damageable) {
                    e.setCancelled(true);
                    if (damageable.getDamage() == 0) {
                        p.sendMessage(ChatColor.YELLOW + "Tu máscara ya está a plena capacidad.");
                        return;
                    }

                    damageable.setDamage(0);
                    acc[i].setItemMeta(damageable);
                    item.setAmount(item.getAmount() - 1);
                    
                    tech.sebazcrc.permadeath.util.inventory.AccessoryInventory.saveFromItems(p, acc);
                    
                    p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 2.0f);
                    p.spawnParticle(Particle.CLOUD, p.getLocation().add(0, 1.5, 0), 15, 0.2, 0.2, 0.2, 0.05);
                    p.sendMessage(ChatColor.AQUA + "¡Filtro aplicado! Oxígeno restaurado.");
                    
                    p.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, 1.0);
                    return;
                }
            }
        }
    }


    private void removePressureBar(Player player) {
        BossBar bar = pressureBars.remove(player.getUniqueId());
        if (bar != null) bar.removeAll();
        
        double p = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, 1.0);
        if (p < 1.0) {
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyss_pressure"), PersistentDataType.DOUBLE, Math.min(1.0, p + 0.01));
        }
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
            
            if (random.nextInt(100) < 15) {
                String[] deepDarkMobs = {"SilentSeeker", "SculkParasite", "EchoArcher", "HollowGuard", "TwistedWarden"};
                String selected = deepDarkMobs[random.nextInt(deepDarkMobs.length)];
                
                if (selected.equals("TwistedWarden") && random.nextInt(10) != 0) {
                    selected = "SilentSeeker";
                }
                
                plugin.getNmsHandler().spawnNMSCustomEntity(selected, null, loc, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
        }
    }
}