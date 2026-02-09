package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.SpecialPig;
import org.bukkit.entity.Pig;

public record SpecialPigWrapper(Pig pig) implements SpecialPig {

    @Override
    public Pig getBukkitEntity() {
        return pig;
    }
}
