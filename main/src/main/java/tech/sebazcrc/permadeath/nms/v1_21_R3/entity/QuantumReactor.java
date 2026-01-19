package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.SpawnUtils;

public class QuantumReactor {

    public static ArmorStand spawn(Location loc, Plugin plugin) {
        ArmorStand reactor = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM);

        // Configuración Visual
        reactor.setCustomName("§b§l⚡ QUANTUM REACTOR ⚡");
        reactor.setCustomNameVisible(true);
        reactor.setGravity(false);
        reactor.setVisible(false); // Cuerpo invisible, solo se ve la cabeza
        reactor.setHelmet(new ItemStack(Material.BEACON));
        reactor.setBasePlate(false);

        // Efectos de spawn
        SpawnUtils.playSpawnEffects(loc);

        // Simular vida alta (aunque los ArmorStands mueren de un golpe en creativo, en survival aguantan)
        // Nota: ArmorStands no tienen atributos de vida genéricos como LivingEntity, pero podemos simular
        // su resistencia cancelando eventos de daño en un Listener si no es daño explosivo/mágico.

        // Animación y Lógica de Torreta
        Runnable reactorTask = new Runnable() {
            double angle = 0;

            @Override
            public void run() {
                if (reactor.isDead() || !reactor.isValid()) {
                    // Efecto de explosión al morir
                    reactor.getWorld().createExplosion(reactor.getLocation(), 2.0f, false, false);
                    return;
                }

                // Animación: Rotar la cabeza y flotar suavemente
                angle += 0.1;
                double yOffset = Math.sin(angle) * 0.2;
                Location newLoc = loc.clone().add(0, yOffset, 0);
                newLoc.setYaw((float) (angle * 20)); // Rotar cuerpo
                reactor.teleport(newLoc);

                // Efecto de campo de fuerza
                reactor.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, reactor.getLocation().add(0, 1.5, 0), 5, 0.5, 0.5, 0.5, 0.1);

                // Lógica de Torreta
                Player target = MobUtils.getNearestPlayer(reactor, 12.0);
                if (target != null) {
                    // Rayo Láser (Partículas)
                    drawLaser(reactor.getLocation().add(0, 1.5, 0), target.getEyeLocation());

                    // Daño progresivo
                    if (angle % 2.0 < 0.2) { // Ataca cada cierto tiempo
                        target.damage(4.0, reactor);
                        target.getWorld().strikeLightningEffect(target.getLocation());
                        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.5f, 2.0f);
                    }
                }
            }

            private void drawLaser(Location start, Location end) {
                double distance = start.distance(end);
                org.bukkit.util.Vector direction = end.toVector().subtract(start.toVector()).normalize();
                for (double i = 0; i < distance; i += 0.5) {
                    start.getWorld().spawnParticle(Particle.DUST, start.clone().add(direction.clone().multiply(i)), 1, new Particle.DustOptions(org.bukkit.Color.AQUA, 0.5f));
                }
            }
        };

        try {
            reactor.getScheduler().runAtFixedRate(plugin, t -> reactorTask.run(), null, 1L, 2L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (reactor.isDead() || !reactor.isValid()) { this.cancel(); reactorTask.run(); return; }
                    reactorTask.run();
                }
            }.runTaskTimer(plugin, 0L, 2L);
        }

        return reactor;
    }
}



