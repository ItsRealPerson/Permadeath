package tech.sebazcrc.permadeath.nms.v1_21_R3.utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class TeleportUtils {

    public static void lookAt(LivingEntity entity, Location target) {
        if (entity == null || target == null) return;
        Location entityLoc = entity.getLocation();
        Vector direction = target.toVector().subtract(entityLoc.toVector()).normalize();
        entityLoc.setDirection(direction);
        
        try {
            entity.teleportAsync(entityLoc);
        } catch (NoSuchMethodError e) {
            entity.teleport(entityLoc);
        }
    }

    public static void moveTowards(LivingEntity entity, Location target, double speed, double jumpHeight) {
        if (entity == null || target == null) return;
        Vector direction = target.toVector().subtract(entity.getLocation().toVector()).normalize();

        double y = entity.getVelocity().getY();
        if (entity.isOnGround() && jumpHeight > 0) {
            y = jumpHeight;
        }

        entity.setVelocity(direction.multiply(speed).setY(y));
    }
}
