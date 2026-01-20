package tech.sebazcrc.permadeath.util.item;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import tech.sebazcrc.permadeath.Main;

import java.util.HashSet;
import java.util.Set;

public class AbyssalBrewingListener implements Listener {

    private static final String META_KEY = "pdc_brewing_time";
    private final Set<Block> activeStands = new HashSet<>();

    public AbyssalBrewingListener() {
        Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            activeStands.removeIf(block -> block.getType() != Material.BREWING_STAND);
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getOpenInventory().getType() == InventoryType.BREWING) {
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof BrewingStand stand) {
                        activeStands.add(stand.getBlock());
                    }
                }
            }

            for (Block b : activeStands) {
                if (b.getState() instanceof BrewingStand stand) {
                    tickBrewingStand(stand);
                }
            }
        }, 1L, 1L);
    }

    private void tickBrewingStand(BrewingStand stand) {
        BrewerInventory inv = stand.getInventory();
        ItemStack ingredient = inv.getIngredient();
        Block block = stand.getBlock();

        if (ingredient == null || ingredient.getType() != Material.ECHO_SHARD) {
            if (block.hasMetadata(META_KEY)) {
                block.removeMetadata(META_KEY, Main.instance);
                stand.setBrewingTime(0);
                stand.update(true);
            }
            return;
        }

        if (stand.getFuelLevel() <= 0) return;

        boolean canBrew = false;
        for (int i = 0; i < 3; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType().name().contains("POTION") && !PermadeathItems.isAbyssalPotion(item)) {
                canBrew = true;
                break;
            }
        }

        if (!canBrew) {
            stand.setBrewingTime(0);
            stand.update(true);
            return;
        }

        int timeLeft = block.hasMetadata(META_KEY) ? block.getMetadata(META_KEY).get(0).asInt() : 400;
        timeLeft--;

        if (timeLeft <= 0) {
            performTransformation(stand);
            block.removeMetadata(META_KEY, Main.instance);
        } else {
            block.setMetadata(META_KEY, new FixedMetadataValue(Main.instance, timeLeft));
            stand.setBrewingTime(timeLeft);
            stand.update(true, false);
        }
    }

    private void performTransformation(BrewingStand stand) {
        Block block = stand.getBlock();
        Location loc = block.getLocation();
        
        // Obtener un estado fresco y real
        if (!(block.getState() instanceof BrewingStand realStand)) return;
        
        BrewerInventory inv = realStand.getInventory();
        ItemStack[] contents = inv.getContents(); // 0-2: potions, 3: ingredient, 4: fuel
        boolean changed = false;

        Bukkit.getConsoleSender().sendMessage("§b[Permadeath] Transformación final iniciada en " + loc.getBlockX() + ", " + loc.getBlockZ());

        for (int i = 0; i < 3; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType().name().contains("POTION") && !PermadeathItems.isAbyssalPotion(item)) {
                contents[i] = PermadeathItems.createAbyssalPotion();
                changed = true;
                Bukkit.getConsoleSender().sendMessage("§a[Permadeath] Slot " + i + " transformado físicamente.");
            }
        }

        if (changed) {
            // Consumir ingrediente (Slot 3)
            if (contents[3] != null) {
                int amount = contents[3].getAmount() - 1;
                if (amount <= 0) contents[3] = null;
                else contents[3].setAmount(amount);
            }
            
            // Aplicar cambios al inventario y al bloque
            inv.setContents(contents);
            realStand.setFuelLevel(Math.max(0, realStand.getFuelLevel() - 1));
            realStand.setBrewingTime(0);
            
            // EL ORDEN ES VITAL: update() al final
            realStand.update(true, true);
            
            loc.getWorld().playSound(loc, org.bukkit.Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);

            // Sincronización visual forzada para todos en el área
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().equals(loc.getWorld()) && p.getLocation().distanceSquared(loc) < 256) {
                        p.updateInventory();
                    }
                }
                Bukkit.getConsoleSender().sendMessage("§b[Permadeath] Sincronización visual completada.");
            }, 1L);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.BREWING) return;
        if (e.getRawSlot() == 3) {
            ItemStack cursor = e.getCursor();
            if (cursor != null && cursor.getType() == Material.ECHO_SHARD) {
                e.setCancelled(true);
                ItemStack current = e.getInventory().getItem(3);
                
                if (current == null || current.getType() == Material.AIR) {
                    e.getInventory().setItem(3, cursor.clone());
                    e.setCursor(null);
                } else if (current.isSimilar(cursor)) {
                    int total = current.getAmount() + cursor.getAmount();
                    if (total <= 64) {
                        current.setAmount(total);
                        e.setCursor(null);
                    }
                }
                
                if (e.getInventory().getHolder() instanceof BrewingStand stand) {
                    stand.getBlock().setMetadata(META_KEY, new FixedMetadataValue(Main.instance, 400));
                    stand.update(true);
                }
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof BrewingStand stand) {
            activeStands.add(stand.getBlock());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        activeStands.remove(e.getBlock());
    }
}
