package dev.itsrealperson.permadeath.api.interfaces;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface NMSAccessor {

    /**
     * Devuelve la interfaz de abstracción de Permadeath para una entidad de Bukkit.
     * @return La interfaz o null si no es una entidad personalizada.
     */
    dev.itsrealperson.permadeath.api.entity.PermadeathEntity getCustomEntity(org.bukkit.entity.Entity entity);

    void setMaxHealth(LivingEntity entity, Double d, boolean setHealth);

    double getMaxHealth(LivingEntity entity);

    void registerAttribute(Attribute a, double value, LivingEntity who);

    void registerHostileMobs();

    void injectHostilePathfinders(LivingEntity entity);

    void drown(Player p, double amount);

    void clearEntityPathfinders(Object goalSelector, Object targetSelector);
}


