package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.QuantumReactor;
import org.bukkit.entity.ArmorStand;

public record QuantumReactorWrapper(ArmorStand armorStand) implements QuantumReactor {

    @Override
    public ArmorStand getBukkitEntity() {
        return armorStand;
    }
}
