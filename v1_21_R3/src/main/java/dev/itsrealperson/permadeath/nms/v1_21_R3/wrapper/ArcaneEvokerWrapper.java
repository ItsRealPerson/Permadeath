package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.ArcaneEvoker;
import org.bukkit.entity.Evoker;

public record ArcaneEvokerWrapper(Evoker evoker) implements ArcaneEvoker {

    @Override
    public Evoker getBukkitEntity() {
        return evoker;
    }
}
