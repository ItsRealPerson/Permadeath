package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Cod;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class CustomCod {

    public static Cod spawn(Location loc, Plugin plugin) {
        Cod cod = (Cod) loc.getWorld().spawnEntity(loc, EntityType.COD, CreatureSpawnEvent.SpawnReason.CUSTOM);

        cod.setCustomName("§b§lDeath Cod");
        cod.setCustomNameVisible(true);

        EffectUtils.setMaxHealth(cod, 30.0);

        // IA para saltar fuera del agua hacia jugadores
        Runnable codTask = new Runnable() {
            @Override
            public void run() {
                if (cod.isDead() || !cod.isValid()) {
                    return;
                }

                Player target = MobUtils.getNearestPlayer(cod, 10.0);
                if (target != null) {
                    // Si está en el agua, salta hacia el jugador
                    if (cod.getLocation().getBlock().getType() == org.bukkit.Material.WATER) {
                        TeleportUtils.moveTowards(cod, target.getLocation().add(0, 1, 0), 0.8, 0.6);
                        cod.getWorld().playSound(cod.getLocation(), Sound.ENTITY_FISH_SWIM, 1.0f, 1.0f);
                    }
                }
            }
        };

        try {
            cod.getScheduler().runAtFixedRate(plugin, t -> codTask.run(), null, 20L, 20L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (cod.isDead() || !cod.isValid()) { this.cancel(); return; }
                    codTask.run();
                }
            }.runTaskTimer(plugin, 20L, 20L);
        }

        return cod;
    }
}

