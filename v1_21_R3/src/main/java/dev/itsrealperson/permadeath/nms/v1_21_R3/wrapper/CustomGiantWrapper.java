package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.CustomGiant;
import org.bukkit.entity.Giant;

public record CustomGiantWrapper(Giant giant) implements CustomGiant {

    @Override
    public Giant getBukkitEntity() {
        return giant;
    }
}
