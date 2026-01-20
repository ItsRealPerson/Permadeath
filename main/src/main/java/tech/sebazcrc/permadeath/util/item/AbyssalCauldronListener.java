package tech.sebazcrc.permadeath.util.item;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.CauldronDataManager;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbyssalCauldronListener implements Listener {

    private final CauldronDataManager data;
    private static final NamespacedKey HOLO_TAG = new NamespacedKey("permadeath", "cauldron_holo");
    private final Map<Location, ArmorStand> hologramCache = new ConcurrentHashMap<>();
    private final Map<Location, Integer> failCounter = new HashMap<>();
    
    private static final List<Material> HEAT_SOURCES = Arrays.asList(
            Material.LAVA, Material.MAGMA_BLOCK, Material.FIRE, Material.SOUL_FIRE,
            Material.CAMPFIRE, Material.SOUL_CAMPFIRE
    );

    public AbyssalCauldronListener() {
        this.data = new CauldronDataManager();
        
        Runnable initTask = () -> {
            for (org.bukkit.World w : Bukkit.getWorlds()) {
                for (Entity e : w.getEntitiesByClass(ArmorStand.class)) {
                    if (e.getPersistentDataContainer().has(HOLO_TAG, PersistentDataType.BYTE)) e.remove();
                }
            }
            
            for (String path : data.getActiveCauldrons()) {
                Location loc = data.deserializeLoc(path);
                int state = data.getState(path);
                if (state >= 2 && loc.getBlock().getType() == Material.WATER_CAULDRON) {
                    createHologram(loc.getBlock());
                    if (state == 2) startBrewingTask(loc.getBlock());
                    else updateHologram(loc.getBlock(), "&a✔ &b&lLISTA");
                }
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(Main.instance, t -> initTask.run(), 40L);
        } else {
            Bukkit.getScheduler().runTaskLater(Main.instance, initTask, 40L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND) return;

        Block b = e.getClickedBlock();
        if (b == null || (b.getType() != Material.CAULDRON && b.getType() != Material.WATER_CAULDRON)) return;

        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        Location loc = b.getLocation();
        
        String path = serializeLoc(loc);
        int state = data.getActiveCauldrons().contains(path) ? data.getState(path) : 0;

        if (!Main.instance.isExtendedDifficulty()) return;

        if (!isHot(b) && state < 3) {
            if (item != null && (item.getType() == Material.NETHER_WART || item.getType() == Material.ECHO_SHARD)) {
                p.sendMessage(TextUtils.format("&c> El caldero necesita calor debajo."));
            }
            return;
        }

        if (state == 0 && item != null && item.getType() == Material.NETHER_WART) {
            if (b.getType() == Material.CAULDRON) {
                p.sendMessage(TextUtils.format("&c> El caldero debe tener agua."));
                return;
            }
            consumeItem(item);
            data.saveCauldron(loc, 1, 0);
            b.getWorld().playSound(loc, Sound.ITEM_BOTTLE_EMPTY, 1.0f, 0.5f);
            return;
        }

        if (state == 1 && item != null && item.getType() == Material.ECHO_SHARD) {
            consumeItem(item);
            int totalSeconds = Main.instance.getConfig().getInt("Alquimia.Tiempo-Segundos", 120);
            long endTime = System.currentTimeMillis() + (totalSeconds * 1000);
            data.saveCauldron(loc, 2, endTime);
            failCounter.put(loc, 0);
            
            b.getWorld().playSound(loc, Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
            createHologram(b);
            startBrewingTask(b);
            return;
        }

        if (state == 3 && item != null && item.getType() == Material.GLASS_BOTTLE) {
            e.setCancelled(true);
            consumeItem(item);
            
            p.getInventory().addItem(PermadeathItems.createAbyssalPotion());
            
            if (b.getBlockData() instanceof Levelled levelled) {
                int newLevel = levelled.getLevel() - 1;
                if (newLevel <= 0) {
                    b.setType(Material.CAULDRON);
                    data.removeCauldron(loc);
                    removeHologram(b);
                } else {
                    levelled.setLevel(newLevel);
                    b.setBlockData(levelled);
                }
            } else {
                data.removeCauldron(loc);
                removeHologram(b);
            }
            
            b.getWorld().playSound(loc, Sound.ITEM_BOTTLE_FILL, 1.0f, 1.2f);
        }
    }

    private void startBrewingTask(Block b) {
        Location loc = b.getLocation();
        String path = serializeLoc(loc);
        int total = Main.instance.getConfig().getInt("Alquimia.Tiempo-Segundos", 120);
        
        Runnable taskLogic = () -> {
            if (b.getType() != Material.WATER_CAULDRON || !data.getActiveCauldrons().contains(path) || data.getState(path) != 2) {
                removeHologram(b);
                return;
            }

            boolean playersNearby = false;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(loc.getWorld()) && p.getLocation().distanceSquared(loc) < 1024) {
                    playersNearby = true;
                    break;
                }
            }
            if (!playersNearby) return;

            if (!isHot(b)) {
                int fails = failCounter.getOrDefault(loc, 0) + 1;
                failCounter.put(loc, fails);
                if (fails >= 3) { explode(b); return; }
                b.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
                updateHologram(b, "&c&l¡ENFRIADO! (" + fails + "/3)");
                data.saveCauldron(loc, 1, 0);
                return;
            }

            long diff = data.getEndTime(path) - System.currentTimeMillis();
            double remaining = diff / 1000.0;

            if (remaining <= 0) {
                data.saveCauldron(loc, 3, 0);
                updateHologram(b, "&a✔ &b&lLISTA");
                b.getWorld().playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.5f);
                b.getWorld().spawnParticle(Particle.WITCH, loc.clone().add(0.5, 0.8, 0.5), 10, 0.2, 0.2, 0.2, 0.05);
            } else {
                String progressBar = getProgressBar((int)((total - remaining) * 10), total * 10, 20);
                updateHologram(b, "&f[" + progressBar + "&f] &e" + (int)Math.ceil(remaining) + "s");
                if (remaining > 60) loc.getWorld().spawnParticle(Particle.ENTITY_EFFECT, loc.clone().add(0.5, 0.8, 0.5), 1, 0.2, 0.1, 0.2, 0, org.bukkit.Color.fromRGB(128, 0, 128));
                else loc.getWorld().spawnParticle(Particle.SOUL, loc.clone().add(0.5, 0.8, 0.5), 1, 0.2, 0.1, 0.2, 0.02);
                if (Math.ceil(remaining) % 2 == 0) b.getWorld().playSound(loc, Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 0.3f, 1.0f);
            }
        };

        if (Main.isRunningFolia()) {
            Bukkit.getRegionScheduler().runAtFixedRate(Main.instance, loc, t -> {
                if (data.getState(path) != 2) t.cancel();
                taskLogic.run();
            }, 10L, 10L);
        } else {
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (data.getState(path) != 2) this.cancel();
                    taskLogic.run();
                }
            }.runTaskTimer(Main.instance, 10L, 10L);
        }
    }

    private void explode(Block b) {
        Location loc = b.getLocation().add(0.5, 0.5, 0.5);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        b.setType(Material.AIR);
        data.removeCauldron(b.getLocation());
        removeHologram(b);
        failCounter.remove(b.getLocation());
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) if (e instanceof Player p) p.damage(4.0);
    }

    private void createHologram(Block b) {
        removeHologram(b);
        Location loc = b.getLocation().add(0.5, 1.2, 0.5);
        ArmorStand as = b.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setMarker(true);
            stand.setCustomNameVisible(true);
            stand.setGravity(false);
            stand.setSmall(true);
            stand.setCustomName(TextUtils.format("&bPreparando..."));
            stand.getPersistentDataContainer().set(HOLO_TAG, PersistentDataType.BYTE, (byte) 1);
        });
        hologramCache.put(b.getLocation(), as);
    }

    private void updateHologram(Block b, String text) {
        ArmorStand as = hologramCache.get(b.getLocation());
        if (as != null && as.isValid()) as.setCustomName(TextUtils.format(text));
    }

    private void removeHologram(Block b) {
        ArmorStand as = hologramCache.remove(b.getLocation());
        if (as != null) as.remove();
    }

    private boolean isHot(Block b) {
        Block below = b.getRelative(0, -1, 0);
        if (!HEAT_SOURCES.contains(below.getType())) return false;
        if (below.getBlockData() instanceof Campfire campfire) return campfire.isLit();
        return true;
    }

    private String getProgressBar(int current, int max, int totalBars) {
        float percent = (float) current / max;
        int progressBars = Math.min(totalBars, Math.max(0, (int) (totalBars * percent)));
        StringBuilder sb = new StringBuilder("&a");
        for (int i = 0; i < progressBars; i++) sb.append("|");
        sb.append("&7");
        for (int i = 0; i < (totalBars - progressBars); i++) sb.append("-");
        return sb.toString();
    }

    private void consumeItem(ItemStack item) {
        item.setAmount(item.getAmount() - 1);
    }

    private String serializeLoc(Location l) {
        return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.CAULDRON || e.getBlock().getType() == Material.WATER_CAULDRON) {
            data.removeCauldron(e.getBlock().getLocation());
            removeHologram(e.getBlock());
        }
    }
}
