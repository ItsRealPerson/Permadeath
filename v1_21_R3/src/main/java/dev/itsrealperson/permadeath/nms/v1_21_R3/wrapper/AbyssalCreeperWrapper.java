package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.AbyssalCreeper;
import org.bukkit.entity.Creeper;

public record AbyssalCreeperWrapper(Creeper creeper) implements AbyssalCreeper {

    @Override
    public Creeper getBukkitEntity() {
        return creeper;
    }
}
