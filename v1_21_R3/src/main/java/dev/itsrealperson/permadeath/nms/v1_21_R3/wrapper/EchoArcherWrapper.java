package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.EchoArcher;
import org.bukkit.entity.Stray;

public record EchoArcherWrapper(Stray stray) implements EchoArcher {

    @Override
    public Stray getBukkitEntity() {
        return stray;
    }
}
