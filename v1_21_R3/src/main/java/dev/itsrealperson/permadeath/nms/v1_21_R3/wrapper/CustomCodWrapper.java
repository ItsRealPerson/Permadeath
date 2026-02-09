package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.CustomCod;
import org.bukkit.entity.Cod;

public record CustomCodWrapper(Cod cod) implements CustomCod {

    @Override
    public Cod getBukkitEntity() {
        return cod;
    }
}
