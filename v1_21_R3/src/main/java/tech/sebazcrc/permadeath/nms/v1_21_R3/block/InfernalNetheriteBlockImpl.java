package tech.sebazcrc.permadeath.nms.v1_21_R3.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import tech.sebazcrc.permadeath.api.interfaces.InfernalNetheriteBlock;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;

public class InfernalNetheriteBlockImpl implements InfernalNetheriteBlock {
    private NamespacedKey getKey() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Permadeath");
        return plugin != null ? new NamespacedKey(plugin, "InfernalNetherite") : null;
    }

    @Override
    public void placeCustomBlock(Location pos) {
        Block o = pos.getBlock();
        o.setType(Material.SPAWNER);

        if (o.getState() instanceof CreatureSpawner spawner) {
            spawner.setSpawnedType(EntityType.ARMOR_STAND);
            
            NamespacedKey key = getKey();
            if (key != null) {
                spawner.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            }
            
            spawner.setSpawnRange(0);
            spawner.setSpawnCount(0);
            spawner.setRequiredPlayerRange(0);
            spawner.setMaxNearbyEntities(0);
            
            spawner.update();
        }

        pos.getWorld().playSound(pos, Sound.BLOCK_STONE_BREAK, 1, 1);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent e) {
        if (isInfernalNetherite(e.getBlock().getLocation())) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 0.5, 0), PermadeathItems.createInfernalNetheriteBlock());
            e.setExpToDrop(0);
        }
    }

    @Override
    public boolean isInfernalNetherite(Location pos) {
        if (pos.getBlock().getState() instanceof CreatureSpawner spawner) {
            NamespacedKey key = getKey();
            return (key != null && spawner.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN)) || spawner.getSpawnedType() == EntityType.ARMOR_STAND;
        }
        return false;
    }
}

