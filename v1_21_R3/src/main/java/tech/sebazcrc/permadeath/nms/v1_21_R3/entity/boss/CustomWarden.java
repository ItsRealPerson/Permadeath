package tech.sebazcrc.permadeath.nms.v1_21_R3.entity.boss;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Warden;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.entity.minions.*;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.SpawnUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.Random;

public class CustomWarden implements Listener {

    private static boolean listenerRegistered = false;

    public static void init(Plugin plugin) {
        if (!listenerRegistered) {
            Bukkit.getPluginManager().registerEvents(new CustomWarden(), plugin);
            listenerRegistered = true;
        }
    }

    public static Warden spawn(Location loc, Plugin plugin) {
        init(plugin);
        Warden warden = (Warden) loc.getWorld().spawnEntity(loc, EntityType.WARDEN, CreatureSpawnEvent.SpawnReason.CUSTOM);

        // --- Configuración Visual y Base ---
        warden.setCustomName("§3§lTwisted Warden");
        warden.setCustomNameVisible(true);
        SpawnUtils.playSpawnEffects(loc);

        // --- Atributos Mejorados ---
        // Vida de jefe real (500 de vida = 250 corazones)
        EffectUtils.setMaxHealth(warden, 500.0);
        // Daño físico absurdo (45 = 22.5 corazones, mata de un golpe sin armadura)
        EffectUtils.setAttackDamage(warden, 45.0);
        // Resistencia al empuje total
        EffectUtils.setKnockbackResistance(warden, 1.0);
        // Un poco más rápido que el Warden normal (que ya es rápido)
        EffectUtils.setMovementSpeed(warden, 0.35);

        // --- Comportamiento y Habilidades (IA Manual) ---
        Runnable wardenTask = new Runnable() {
            int tickCounter = 0;
            Random random = new Random();

            @Override
            public void run() {
                if (warden.isDead() || !warden.isValid()) {
                    // Efecto al morir: Sonido grave y partículas
                    warden.getWorld().playSound(warden.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1.0f, 0.5f);
                    warden.getWorld().spawnParticle(Particle.SOUL, warden.getLocation(), 50, 1, 2, 1, 0.1);
                    return;
                }

                tickCounter++;

                // 1. Detección de Jugadores (Rango amplio de 50 bloques)
                Player target = MobUtils.getNearestPlayer(warden, 50.0);

                if (target != null) {
                    // Forzar ira hacia el jugador (mecánica nativa del Warden)
                    warden.setAnger(target, 150); // Máxima ira
                    warden.setTarget(target);

                    // 2. Efecto Pasivo: Oscuridad Eterna y Debilidad
                    if (tickCounter % 60 == 0) { // Cada 3 segundos
                        EffectUtils.addPotionEffect(target, new PotionEffect(PotionEffectType.DARKNESS, 100, 0, false, false));
                        EffectUtils.addPotionEffect(target, new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false));
                    }

                    // 3. Habilidad Especial: Sonic Boom Manual (Rango medio)
                    // Si el jugador está lejos (> 10 bloques) y el Warden tiene línea de visión
                    double distanceSq = warden.getLocation().distanceSquared(target.getLocation());
                    if (distanceSq > 100.0 && distanceSq < 900.0 && tickCounter % 100 == 0) { // Cada 5 segundos
                        performSonicBoom(warden, target);
                    }

                    // 4. Habilidad Definitiva: Invocar Minions Custom
                    // Se activa si tiene menos del 80% de vida
                    if (warden.getHealth() < 400.0 && tickCounter % 150 == 0) { // Cada 15 segundos
                        summonCustomMinions(warden);
                    }

                    // Ayuda de movimiento si el jugador está muy lejos
                    if (distanceSq > 400.0) { // > 20 bloques
                        TeleportUtils.moveTowards(warden, target.getLocation(), 0.5, 0.3);
                    }
                }
            }

            private void performSonicBoom(Warden source, Player target) {
                // Simulación visual y daño del Sonic Boom (ya que la API para forzarlo es limitada)
                source.getWorld().playSound(source.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 3.0f, 1.0f);
                source.getWorld().spawnParticle(Particle.SONIC_BOOM, source.getEyeLocation(), 1, 0, 0, 0, 0);

                // Daño mágico que atraviesa armadura
                target.damage(15.0, source); // 7.5 corazones
                target.setVelocity(target.getLocation().toVector().subtract(source.getLocation().toVector()).normalize().multiply(2.5));
                target.sendMessage("§3§l" + source.getCustomName() + " §bte ha golpeado con su rugido sónico!");
            }

            private void summonCustomMinions(Warden source) {
                source.getWorld().playSound(source.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 0.5f);
                source.getWorld().playSound(source.getLocation(), Sound.ENTITY_WARDEN_ROAR, 1.0f, 0.5f);
                source.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, source.getLocation(), 40, 2, 1, 2, 0.1);

                int type = random.nextInt(5);
                int count = 2 + random.nextInt(3); // Entre 2 y 4 minions

                for (int i = 0; i < count; i++) {
                    Location spawnLoc = source.getLocation().add(random.nextInt(7) - 3, 1, random.nextInt(7) - 3);
                    
                    switch (type) {
                        case 0 -> HollowGuard.spawn(spawnLoc, plugin);
                        case 1 -> EchoArcher.spawn(spawnLoc, plugin);
                        case 2 -> SilentSeeker.spawn(spawnLoc, plugin);
                        case 3 -> SculkParasite.spawn(spawnLoc, plugin);
                        case 4 -> GloomBat.spawn(spawnLoc, plugin);
                    }
                }
                
                // También invocamos almas (Vexes) siempre como apoyo
                for (int i = 0; i < 2; i++) {
                    Vex soul = (Vex) source.getWorld().spawnEntity(source.getLocation().add(0, 2, 0), EntityType.VEX);
                    soul.setCustomName("§bTormented Soul");
                    soul.setLifeTicks(20 * 30);
                    EffectUtils.setAttackDamage(soul, 8.0);
                }
            }
        };

        try {
            warden.getScheduler().runAtFixedRate(plugin, t -> wardenTask.run(), null, 20L, 2L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (warden.isDead() || !warden.isValid()) { this.cancel(); return; }
                    wardenTask.run();
                }
            }.runTaskTimer(plugin, 20L, 2L);
        }

        return warden;
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getEntity() instanceof Warden warden)) return;
        if (e.getTarget() == null) return;

        if (isTwistedWarden(warden)) {
            if (!(e.getTarget() instanceof Player)) {
                e.setCancelled(true);
                warden.setTarget(null);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Warden warden)) return;
        
        if (isTwistedWarden(warden)) {
            if (!(e.getEntity() instanceof Player)) {
                e.setCancelled(true);
                warden.setTarget(null);
            }
        }
    }

    private boolean isTwistedWarden(Warden warden) {
        if (warden.getCustomName() == null) return false;
        String name = ChatColor.stripColor(warden.getCustomName());
        return name.contains("Twisted Warden");
    }
}


