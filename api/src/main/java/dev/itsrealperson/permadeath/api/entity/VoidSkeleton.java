package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Skeleton;

/**
 * Representa un Void Skeleton, un arquero del vacío.
 */
public interface VoidSkeleton extends PermadeathEntity {

    @Override
    default String getInternalName() { return "VoidSkeleton"; }

    @Override
    Skeleton getBukkitEntity();
}
