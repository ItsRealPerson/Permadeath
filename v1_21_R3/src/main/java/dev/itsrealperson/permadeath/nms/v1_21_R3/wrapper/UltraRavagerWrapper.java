package dev.itsrealperson.permadeath.nms.v1_21_R3.wrapper;

import dev.itsrealperson.permadeath.api.entity.UltraRavager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Ravager;

import java.util.List;

public record UltraRavagerWrapper(Ravager ravager) implements UltraRavager {

    @Override
    public Ravager getBukkitEntity() {
        return ravager;
    }

    @Override
    public void performBlockDestruction() {
        List<Block> sight = ravager.getLineOfSight(null, 5);

        for (Block block : sight) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        Block target = block.getRelative(i, j, k);
                        if (target.getType() == Material.NETHERRACK) {
                            target.setType(Material.AIR);
                            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_STONE_BREAK, 2.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }
}
