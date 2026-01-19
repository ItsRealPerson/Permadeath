package tech.sebazcrc.permadeath.nms.v1_21_R3;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import tech.sebazcrc.permadeath.api.interfaces.NMSAccessor;

public class NMSAccessorImpl implements NMSAccessor {

    public NMSAccessorImpl() {
        // Constructor vacío
    }

    @Override
    public void setMaxHealth(LivingEntity entity, Double d, boolean setHealth) {
        org.bukkit.attribute.AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(d);
            if (setHealth) {
                entity.setHealth(d);
            }
        }
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
        org.bukkit.attribute.AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        return attr != null ? attr.getValue() : 20.0;
    }

    @Override
    public void registerAttribute(Attribute a, double value, LivingEntity who) {
        org.bukkit.attribute.AttributeInstance attr = who.getAttribute(a);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }

    @Override
    public void registerHostileMobs() {
        // En 1.21 usamos los atributos de Bukkit directamente
    }

    @Override
    public void injectHostilePathfinders(LivingEntity entity) {
    }

    @Override
    public void drown(Player p, double amount) {
        p.damage(amount);
    }

    @Override
    public void clearEntityPathfinders(Object goalSelector, Object targetSelector) {
    }
}
