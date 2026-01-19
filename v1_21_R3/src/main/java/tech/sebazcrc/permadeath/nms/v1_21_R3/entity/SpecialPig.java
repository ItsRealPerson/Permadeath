package tech.sebazcrc.permadeath.nms.v1_21_R3.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.EffectUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.MobUtils;
import tech.sebazcrc.permadeath.nms.v1_21_R3.utils.TeleportUtils;

import java.util.ArrayList;
import java.util.Random;

public class SpecialPig {

    public static Pig spawn(Location loc, Plugin plugin) {
        Pig pig = (Pig) loc.getWorld().spawnEntity(loc, EntityType.PIG, CreatureSpawnEvent.SpawnReason.CUSTOM);

        pig.setCustomName("§d§lSpecial Pig");
        pig.setCustomNameVisible(true);
        pig.setRemoveWhenFarAway(true);

        EffectUtils.setMaxHealth(pig, 40.0);
        EffectUtils.setAttackDamage(pig, 40.0);

        ArrayList<PotionEffectType> effects = new ArrayList<>();
        effects.add(PotionEffectType.SPEED);
        effects.add(PotionEffectType.REGENERATION);
        effects.add(PotionEffectType.STRENGTH);
        effects.add(PotionEffectType.INVISIBILITY);
        effects.add(PotionEffectType.JUMP_BOOST);
        effects.add(PotionEffectType.SLOW_FALLING);
        effects.add(PotionEffectType.GLOWING);
        effects.add(PotionEffectType.RESISTANCE);

        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            PotionEffectType type = effects.get(rand.nextInt(effects.size()));
            int level = 0;
            if (type == PotionEffectType.SPEED || type == PotionEffectType.RESISTANCE) level = 2;
            if (type == PotionEffectType.REGENERATION || type == PotionEffectType.STRENGTH) level = 3;
            if (type == PotionEffectType.JUMP_BOOST) level = 4;
            
            pig.addPotionEffect(new PotionEffect(type, 999999, level));
        }

        // IA Manual para atacar
        Runnable pigTask = new Runnable() {
            @Override
            public void run() {
                if (pig.isDead() || !pig.isValid()) {
                    return;
                }

                Player target = MobUtils.getNearestPlayer(pig, 20.0);

                if (target != null) {
                    TeleportUtils.lookAt(pig, target.getLocation());
                    TeleportUtils.moveTowards(pig, target.getLocation(), 0.4, 0.2);

                    if (pig.getLocation().distanceSquared(target.getLocation()) < 4.0) {
                        target.damage(10.0, pig);
                    }
                }
            }
        };

        try {
            pig.getScheduler().runAtFixedRate(plugin, t -> pigTask.run(), null, 20L, 10L);
        } catch (NoSuchMethodError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (pig.isDead() || !pig.isValid()) { this.cancel(); return; }
                    pigTask.run();
                }
            }.runTaskTimer(plugin, 20L, 10L);
        }

        return pig;
    }
}


