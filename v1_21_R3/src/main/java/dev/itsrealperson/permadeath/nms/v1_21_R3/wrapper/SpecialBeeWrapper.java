package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.SpecialBee;
import org.bukkit.entity.Bee;

public record SpecialBeeWrapper(Bee bee) implements SpecialBee {

    @Override
    public Bee getBukkitEntity() {
        return bee;
    }
}
