package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.ExplosivePufferfish;
import org.bukkit.entity.PufferFish;

public record ExplosivePufferfishWrapper(PufferFish pufferFish) implements ExplosivePufferfish {

    @Override
    public PufferFish getBukkitEntity() {
        return pufferFish;
    }
}
