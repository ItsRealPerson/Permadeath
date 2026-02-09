package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.SculkParasite;
import org.bukkit.entity.Silverfish;

public record SculkParasiteWrapper(Silverfish silverfish) implements SculkParasite {

    @Override
    public Silverfish getBukkitEntity() {
        return silverfish;
    }
}
