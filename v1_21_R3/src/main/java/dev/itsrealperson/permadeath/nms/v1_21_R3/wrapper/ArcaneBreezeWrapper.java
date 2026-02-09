package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.ArcaneBreeze;
import org.bukkit.entity.Breeze;

public record ArcaneBreezeWrapper(Breeze breeze) implements ArcaneBreeze {

    @Override
    public Breeze getBukkitEntity() {
        return breeze;
    }
}
