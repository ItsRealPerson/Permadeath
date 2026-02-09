package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.HollowGuard;
import org.bukkit.entity.Husk;

public record HollowGuardWrapper(Husk husk) implements HollowGuard {

    @Override
    public Husk getBukkitEntity() {
        return husk;
    }
}
