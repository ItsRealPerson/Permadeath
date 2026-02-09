package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.SilentSeeker;
import org.bukkit.entity.Creeper;

public record SilentSeekerWrapper(Creeper creeper) implements SilentSeeker {

    @Override
    public Creeper getBukkitEntity() {
        return creeper;
    }
}
