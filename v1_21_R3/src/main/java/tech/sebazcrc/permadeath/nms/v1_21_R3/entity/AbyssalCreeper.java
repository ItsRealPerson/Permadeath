package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;

public class AbyssalCreeper {

    public static Creeper spawn(Location loc, Plugin plugin) {
        Creeper creeper = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
        creeper.setPowered(true);
        creeper.setCustomName("§3Abyssal Creeper");
        creeper.setCustomNameVisible(true);
        
        EffectUtils.setMaxHealth(creeper, 60.0);
        creeper.setHealth(60.0);

        // Tarea para efectos constantes
        creeper.getScheduler().runAtFixedRate(plugin, task -> {
            if (creeper.isDead() || !creeper.isValid()) {
                task.cancel();
                return;
            }
            creeper.getWorld().spawnParticle(Particle.SQUID_INK, creeper.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.01);
        }, null, 1, 10L);

        return creeper;
    }

    // Nota: La explosiÃ³n especial se manejarÃ¡ en el listener de explosiones general
}

