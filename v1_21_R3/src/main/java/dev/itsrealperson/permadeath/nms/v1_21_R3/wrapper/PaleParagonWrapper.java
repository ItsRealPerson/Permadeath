package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.PaleParagon;
import org.bukkit.entity.IronGolem;

public record PaleParagonWrapper(IronGolem ironGolem) implements PaleParagon {

    @Override
    public IronGolem getBukkitEntity() {
        return ironGolem;
    }
}
