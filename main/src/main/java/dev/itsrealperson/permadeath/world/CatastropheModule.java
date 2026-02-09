package dev.itsrealperson.permadeath.world;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.PermadeathModule;
import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.SplittableRandom;

/**
 * Módulo encargado de los eventos catastróficos y mecánicas avanzadas (Día 30, 60+).
 * Incluye Agua Ácida, Withering, Soul Sand Slowness y bombardeo de Phantoms.
 */
public class CatastropheModule implements PermadeathModule {

    private final Main plugin;
    private final SplittableRandom random = new SplittableRandom();
    private int tickCounter = 0;

    public CatastropheModule(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "CatastropheModule";
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
        tickCounter++;
        long day = DateManager.getInstance().getDay();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SURVIVAL) continue;

            // Mecánicas de cada Tick (Agua Ácida)
            applyAcidWater(player, day);

            // Mecánicas cada segundo (20 Ticks)
            if (tickCounter % 20 == 0) {
                applyDay60Effects(player, day);
                applyDay70Effects(player, day);
            }
        }
    }

    private void applyAcidWater(Player player, long day) {
        if (day >= DateManager.getInstance().getAcidWaterDay()) {
            Material eye = player.getEyeLocation().getBlock().getType();
            Material foot = player.getLocation().getBlock().getType();

            if (eye == Material.WATER || foot == Material.WATER) {
                boolean hasMedal = false;
                
                // Buscar medalla en inventario principal y mano secundaria
                for (ItemStack item : player.getInventory().getContents()) {
                    if (dev.itsrealperson.permadeath.util.item.PermadeathItems.isWaterMedal(item)) {
                        hasMedal = true;
                        break;
                    }
                }
                if (!hasMedal && dev.itsrealperson.permadeath.util.item.PermadeathItems.isWaterMedal(player.getInventory().getItemInOffHand())) {
                    hasMedal = true;
                }
                
                // Buscar medalla en Accesorios
                if (!hasMedal) {
                    ItemStack[] acc = dev.itsrealperson.permadeath.util.inventory.AccessoryInventory.load(player);
                    if (acc != null) {
                        for (ItemStack item : acc) {
                            if (dev.itsrealperson.permadeath.util.item.PermadeathItems.isWaterMedal(item)) {
                                hasMedal = true;
                                break;
                            }
                        }
                    }
                }

                if (!hasMedal) {
                    player.damage(2.0); // Daño original
                    player.spawnParticle(Particle.BUBBLE_POP, player.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.05);
                    if (random.nextInt(5) == 0) {
                        player.sendMessage(ChatColor.RED + "¡El agua está altamente contaminada! Necesitas la Medalla de Agua.");
                    }
                }
            }
        }
    }

    private void applyDay60Effects(Player player, long day) {
        DateManager dm = DateManager.getInstance();
        if (day < dm.getAbyssDay()) return;

        // Soul Sand Slowness
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOUL_SAND) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30 * 20, 2));
        }

        // Wither Spawning Logic
        if (day >= dm.getWitherSpawnDay()) {
            Integer timeForWither = player.getPersistentDataContainer().get(new NamespacedKey(plugin, "wither"), PersistentDataType.INTEGER);
            if (timeForWither == null) timeForWither = 0;

            if (timeForWither % (60 * 60) == 0) {
                timeForWither = 0;
                Wither wither = player.getWorld().spawn(player.getLocation().clone().add(0, 5, 0), Wither.class);
                try {
                    Object nmsw = wither.getClass().getDeclaredMethod("getHandle").invoke(wither);
                    nmsw.getClass().getDeclaredMethod("r", int.class).invoke(nmsw, 100);
                } catch (Exception ignored) {}
            }
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "wither"), PersistentDataType.INTEGER, ++timeForWither);
        }

        // Mike Creeper Spawning
        if (plugin.getConfig().getBoolean("Toggles.Mike-Creeper-Spawn")) {
            if (random.nextInt(30) == 0 && player.getNearbyEntities(30, 30, 30).stream().filter(e -> e instanceof Creeper).count() < 10) {
                Location l = player.getLocation().clone();
                int pX = (random.nextBoolean() ? -1 : 1) * (random.nextInt(15)) + 15;
                int pZ = (random.nextBoolean() ? -1 : 1) * (random.nextInt(15)) + 15;
                int y = (int) l.getY();

                Block block = l.getWorld().getBlockAt(l.getBlockX() + pX, y, l.getBlockZ() + pZ);
                Block up = block.getRelative(BlockFace.UP);

                if (block.getType() != Material.AIR && up.getType() == Material.AIR) {
                    plugin.getFactory().spawnEnderQuantumCreeper(up.getLocation(), null);
                }
            }
        }
    }

    private void applyDay70Effects(Player player, long day) {
        if (day >= DateManager.getInstance().getPhantomBombingDay() && random.nextInt(10) == 0) { // Ajustado a tickrate de 1s (Original 1/200 por tick)
            player.getWorld().getNearbyEntities(player.getLocation(), 40, 40, 40).stream()
                    .filter(e -> e instanceof Phantom)
                    .findFirst()
                    .ifPresent(p -> {
                        player.sendMessage(ChatColor.RED + "¡Un Phantom ha soltado algo sobre ti!");
                        plugin.getNmsHandler().spawnNMSCustomEntity("SculkParasite", null, p.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                    });
        }
    }
}