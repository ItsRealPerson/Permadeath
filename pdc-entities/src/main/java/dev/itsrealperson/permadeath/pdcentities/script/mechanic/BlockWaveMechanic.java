package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;

/**
 * Crea una onda de bloques que saltan desde el suelo.
 * Argumentos: r (radio), v (velocidad vertical), m (material opcional)
 */
public class BlockWaveMechanic implements Mechanic {
    private final double radius;
    private final double velocity;
    private final Material materialOverride;

    public BlockWaveMechanic(Map<String, String> args) {
        this.radius = Double.parseDouble(args.getOrDefault("r", "5"));
        this.velocity = Double.parseDouble(args.getOrDefault("v", "0.5"));
        String matStr = args.get("m");
        this.materialOverride = (matStr != null) ? Material.valueOf(matStr.toUpperCase()) : null;
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        Entity caster = context.getCaster();
        Location center = caster.getLocation();
        
        // Determinar material base (el que hay bajo el caster si no hay override)
        Material mat = materialOverride;
        if (mat == null) {
            mat = center.clone().subtract(0, 1, 0).getBlock().getType();
            if (!mat.isSolid()) mat = Material.DIRT;
        }

        final Material finalMat = mat;
        int points = (int) (radius * 8); // Densidad de bloques

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location spawnLoc = center.clone().add(x, 0, z);
            // Ajustar a la altura del suelo
            Block b = spawnLoc.getBlock();
            if (!b.getType().isAir()) {
                spawnLoc.add(0, 1, 0);
            } else {
                while (spawnLoc.getBlock().getType().isAir() && spawnLoc.getY() > center.getY() - 3) {
                    spawnLoc.subtract(0, 1, 0);
                }
                spawnLoc.add(0, 1, 0);
            }

            FallingBlock fb = spawnLoc.getWorld().spawnFallingBlock(spawnLoc, finalMat.createBlockData());
            fb.setDropItem(false);
            fb.setHurtEntities(false);
            
            // Impulso hacia afuera y arriba
            Vector dir = spawnLoc.toVector().subtract(center.toVector()).normalize().multiply(0.2);
            dir.setY(velocity + (Math.random() * 0.2));
            fb.setVelocity(dir);
        }
    }
}
