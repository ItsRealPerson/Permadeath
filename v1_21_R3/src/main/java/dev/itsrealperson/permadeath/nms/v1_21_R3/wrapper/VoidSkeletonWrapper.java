package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.VoidSkeleton;
import org.bukkit.entity.Skeleton;

public record VoidSkeletonWrapper(Skeleton skeleton) implements VoidSkeleton {

    @Override
    public Skeleton getBukkitEntity() {
        return skeleton;
    }
}
