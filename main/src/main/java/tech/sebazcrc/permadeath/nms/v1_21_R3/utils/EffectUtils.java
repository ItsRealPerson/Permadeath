package tech.sebazcrc.permadeath.nms.v1_21_R3.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class EffectUtils {

    public static void setMaxHealth(LivingEntity entity, double health) {
        if (entity == null) return;
        AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(health);
            entity.setHealth(health);
        }
    }

    public static void setAttackDamage(LivingEntity entity, double damage) {
        if (entity == null) return;
        AttributeInstance attr = entity.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attr != null) {
            attr.setBaseValue(damage);
        }
    }

    public static void setMovementSpeed(LivingEntity entity, double speed) {
        if (entity == null) return;
        AttributeInstance attr = entity.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(speed);
        }
    }

    public static void setKnockbackResistance(LivingEntity entity, double resistance) {
        if (entity == null) return;
        AttributeInstance attr = entity.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if (attr != null) {
            attr.setBaseValue(resistance);
        }
    }

    public static void setFollowRange(LivingEntity entity, double range) {
        if (entity == null) return;
        AttributeInstance attr = entity.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(range);
        }
    }

    public static void addPotionEffect(LivingEntity entity, PotionEffect effect) {
        if (entity != null) {
            entity.addPotionEffect(effect);
        }
    }
}



