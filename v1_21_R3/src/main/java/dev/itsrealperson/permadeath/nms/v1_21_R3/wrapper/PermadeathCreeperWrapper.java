package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.PermadeathCreeper;
import org.bukkit.entity.Creeper;

public record PermadeathCreeperWrapper(Creeper creeper) implements PermadeathCreeper {

    @Override
    public Creeper getBukkitEntity() {
        return creeper;
    }
}
