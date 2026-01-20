package tech.sebazcrc.permadeath.event.paper;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import com.destroystokyo.paper.event.entity.EntityTeleportEndGatewayEvent;
import com.destroystokyo.paper.event.player.PlayerTeleportEndGatewayEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.end.demon.DemonPhase;

import java.util.ArrayList;
import java.util.SplittableRandom;

public class PaperListeners implements Listener {

    private Main main;
    private SplittableRandom random = new SplittableRandom();

    public PaperListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onProjectileHit(EnderDragonFireballHitEvent e) {
        AreaEffectCloud a = e.getAreaEffectCloud();
        if (main.getTask() != null) {

            ArrayList<Block> toChange = new ArrayList<>();

            Block b = main.endWorld.getHighestBlockAt(a.getLocation());
            Location highest = main.endWorld.getHighestBlockAt(a.getLocation()).getLocation();

            int structure = random.nextInt(4);
            if (structure == 0) {
                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.SOUTH_EAST));
                toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
            } else if (structure == 1) {

                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                toChange.add(b);
            } else if (structure == 2) {

                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.SOUTH_WEST));
                toChange.add(b);
            } else if (structure == 3) {

                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.NORTH_EAST));
                toChange.add(b);
                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.EAST));
            } else if (structure == 4) {

                toChange.add(b.getRelative(BlockFace.SOUTH));
                toChange.add(b.getRelative(BlockFace.NORTH_WEST));
                toChange.add(b);
                toChange.add(b.getRelative(BlockFace.NORTH));
                toChange.add(b.getRelative(BlockFace.WEST));
            }

            if (main.getTask().getCurrentDemonPhase() == DemonPhase.NORMAL) {
                if (highest.getY() > 0) {

                    for (Block all : toChange) {
                        Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();
                        Block now = main.endWorld.getBlockAt(used);
                        if (now.getType() != Material.AIR) {
                            now.setType(Material.BEDROCK);
                        }
                    }
                }
            } else {

                if (random.nextBoolean()) {
                    a.setParticle(Particle.SMOKE);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 20, 1), false);
                } else {
                    if (highest.getY() > 0) {
                        for (Block all : toChange) {
                            Location used = main.endWorld.getHighestBlockAt(new Location(main.endWorld, all.getX(), all.getY(), all.getZ())).getLocation();
                            Block now = main.endWorld.getBlockAt(used);
                            if (now.getType() != Material.AIR) {
                                now.setType(Material.BEDROCK);
                            }
                        }
                    }
                }
            }
        }
    }
}

















