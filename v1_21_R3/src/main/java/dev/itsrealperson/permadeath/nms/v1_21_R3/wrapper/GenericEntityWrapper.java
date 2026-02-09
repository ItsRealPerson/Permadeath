package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.PermadeathEntity;
import org.bukkit.entity.LivingEntity;

public record GenericEntityWrapper(LivingEntity entity, String internalName) implements PermadeathEntity {

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public LivingEntity getBukkitEntity() {
        return entity;
    }
}
