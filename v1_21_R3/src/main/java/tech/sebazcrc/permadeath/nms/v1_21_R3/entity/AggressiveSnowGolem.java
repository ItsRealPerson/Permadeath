package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

public class AggressiveSnowGolem implements Listener {

    private static final String TURRET_NAME = "§bFrost Turret";
    private static NamespacedKey turretKey;
    private static volatile boolean listenerRegistered = false;

    public static void init(Plugin plugin) {
        if (!listenerRegistered) {
            synchronized (AggressiveSnowGolem.class) {
                if (!listenerRegistered) {
                    turretKey = new NamespacedKey(plugin, "frost_turret");
                    Bukkit.getPluginManager().registerEvents(new AggressiveSnowGolem(), plugin);
                    listenerRegistered = true;
                }
            }
        }
    }

    public static Snowman spawn(Location loc, Plugin plugin) {
        if (!listenerRegistered) {
            init(plugin);
        }

        Snowman snowman = (Snowman) loc.getWorld().spawnEntity(loc, EntityType.SNOW_GOLEM, CreatureSpawnEvent.SpawnReason.CUSTOM);
        snowman.setCustomName(TURRET_NAME);
        snowman.setDerp(true); // Sin calabaza, cara graciosa
        if (turretKey != null) {
            snowman.getPersistentDataContainer().set(turretKey, PersistentDataType.BYTE, (byte) 1);
        }

        EffectUtils.setMaxHealth(snowman, 50.0);

        // IA para atacar jugadores (normalmente solo atacan monstruos)
        Runnable snowmanTask = new Runnable() {
            @Override
            public void run() {
                if (!snowman.isValid()) { return; }
                Player p = MobUtils.getNearestPlayer(snowman, 20);
                if (p != null) {
                    snowman.setTarget(p);
                    TeleportUtils.lookAt(snowman, p.getEyeLocation());

                    if (Math.random() > 0.5) snowman.launchProjectile(Snowball.class);
                }
            }
        };

        try {
            snowman.getScheduler().runAtFixedRate(plugin, t -> snowmanTask.run(), null, 10L, 20L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!snowman.isValid()) { this.cancel(); return; }
                    snowmanTask.run();
                }
            }.runTaskTimer(plugin, 10, 20);
        }

        return snowman;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Snowball ball) {
            if (ball.getShooter() instanceof Snowman snowman) {
                if (TURRET_NAME.equals(snowman.getCustomName()) ||
                    (turretKey != null && snowman.getPersistentDataContainer().has(turretKey, PersistentDataType.BYTE))) {
                    e.setDamage(8.0); // 4 corazones de daño por bola
                }
            }
        }
    }
}
