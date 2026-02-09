package dev.itsrealperson.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import dev.itsrealperson.permadeath.nms.v1_21_R3.utils.EffectUtils;

public class VoidSkeleton {

    public static Skeleton spawn(Location loc, Plugin plugin) {
        Skeleton skeleton = (Skeleton) loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
        skeleton.setCustomName("§5Void Skeleton");
        skeleton.setCustomNameVisible(true);
        
        EffectUtils.setMaxHealth(skeleton, 80.0);
        skeleton.setHealth(80.0);
        
        skeleton.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        
        // Efectos visuales
        skeleton.getScheduler().runAtFixedRate(plugin, task -> {
            if (skeleton.isDead() || !skeleton.isValid()) {
                task.cancel();
                return;
            }
            skeleton.getWorld().spawnParticle(Particle.PORTAL, skeleton.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        }, null, 1, 5L);

        return skeleton;
    }
}

