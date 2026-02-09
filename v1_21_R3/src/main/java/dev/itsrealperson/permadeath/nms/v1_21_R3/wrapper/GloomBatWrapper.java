package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.GloomBat;
import org.bukkit.entity.Bat;

public record GloomBatWrapper(Bat bat) implements GloomBat {

    @Override
    public Bat getBukkitEntity() {
        return bat;
    }
}
