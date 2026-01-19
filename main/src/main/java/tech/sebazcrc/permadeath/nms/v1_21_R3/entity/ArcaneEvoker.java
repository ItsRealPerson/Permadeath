package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.plugin.Plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;

public class ArcaneEvoker {
    public static Evoker spawn(Location loc, Plugin plugin) {
        Evoker evoker = (Evoker) loc.getWorld().spawnEntity(loc, EntityType.EVOKER, CreatureSpawnEvent.SpawnReason.CUSTOM);
        evoker.setCustomName("§5§lGrand Arcane Evoker");
        EffectUtils.setMaxHealth(evoker, 150.0);

        Runnable evokerTask = new Runnable() {
            int cooldown = 0;
            @Override
            public void run() {
                if (!evoker.isValid()) { return; }
                cooldown--;

                Player target = MobUtils.getNearestPlayer(evoker, 25);
                if (target != null) {
                    // Teletransporte defensivo
                    if (evoker.getLocation().distanceSquared(target.getLocation()) < 9 && cooldown <= 0) {
                        teleportAway(evoker, target);
                        cooldown = 60; // 3s cooldown
                    }

                    // Ataque especial: Anillo de colmillos
                    if (Math.random() < 0.02) { // Raro
                        spawnFangCircle(evoker.getLocation(), 4);
                        evoker.setSpell(org.bukkit.entity.Spellcaster.Spell.FANGS);
                    }
                }
            }

            private void teleportAway(Evoker e, Player p) {
                Location newLoc = e.getLocation().add(p.getLocation().getDirection().multiply(-8)); // 8 bloques atrás
                newLoc.setY(e.getWorld().getHighestBlockYAt(newLoc) + 1);
                e.teleport(newLoc);
                e.getWorld().spawnParticle(Particle.PORTAL, e.getLocation(), 20);
                e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }

            private void spawnFangCircle(Location center, int radius) {
                for (double t = 0; t < Math.PI * 2; t += 0.5) {
                    Location l = center.clone().add(radius * Math.cos(t), 0, radius * Math.sin(t));
                    l.setY(l.getWorld().getHighestBlockYAt(l));
                    l.getWorld().spawnEntity(l, EntityType.EVOKER_FANGS);
                }
            }
        };

        try {
            evoker.getScheduler().runAtFixedRate(plugin, t -> evokerTask.run(), null, 20L, 5L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!evoker.isValid()) { this.cancel(); return; }
                    evokerTask.run();
                }
            }.runTaskTimer(plugin, 20, 5);
        }
        return evoker;
    }
}





