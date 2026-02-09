package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.AggressiveSnowGolem;
import org.bukkit.entity.Snowman;

public record AggressiveSnowGolemWrapper(Snowman snowman) implements AggressiveSnowGolem {

    @Override
    public Snowman getBukkitEntity() {
        return snowman;
    }
}
