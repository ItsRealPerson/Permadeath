package dev.itsrealperson.permadeath.event.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;
import dev.itsrealperson.permadeath.data.EndDataManager;
import dev.itsrealperson.permadeath.util.TextUtils;

import java.util.Arrays;
import java.util.List;

public class BlockListener implements Listener {
    public static final List<ItemStack> NO_DAMAGE_TOOLS = Arrays.asList(PermadeathItems.craftNetheriteAxe(), PermadeathItems.craftNetheriteShovel(), PermadeathItems.craftNetheriteSword(), PermadeathItems.craftNetheritePickaxe(), PermadeathItems.craftNetheriteHoe());

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent e) {
        if (Main.getInstance().getEndData() != null && Main.getInstance().getDay() >= 30) {
            EndDataManager ma = Main.getInstance().getEndData();
            if (ma.getConfig().contains("RegenZoneLocation")) {
                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));
                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                    if (e.getBlock().getLocation().distance(loc) <= 10) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(EntityExplodeEvent e) {
        if (Main.getInstance().getEndData() != null) {
            EndDataManager ma = Main.getInstance().getEndData();
            if (ma.getConfig().contains("RegenZoneLocation")) {
                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));
                for (Block b : e.blockList()) {
                    if (b.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                        if (b.getLocation().distance(loc) <= 10) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockCombust(BlockIgniteEvent e) {

        if (Main.getInstance().getEndData() != null && Main.getInstance().getDay() >= 30) {

            EndDataManager ma = Main.getInstance().getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 3) {

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {

        if (Main.getInstance().getEndData() != null && Main.getInstance().getDay() >= 30) {

            EndDataManager ma = Main.getInstance().getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 3) {

                        e.setCancelled(true);
                        e.getPlayer().sendMessage(TextUtils.format("&cNo puedes colocar bloques cerca de la Zona de Regeneración."));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScriptBreak(BlockBreakEvent event) {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("block_type", event.getBlock().getType().name());
        context.put("block_location", event.getBlock().getLocation());
        
        Main.getInstance().getScriptManager().fireTrigger(dev.itsrealperson.permadeath.api.script.ScriptTrigger.BLOCK_BREAK, event.getPlayer(), null, context);
        
        if (event.getPlayer().hasMetadata(dev.itsrealperson.permadeath.util.script.CoreScriptRegistry.CANCEL_METADATA)) {
            event.setCancelled(true);
            event.getPlayer().removeMetadata(dev.itsrealperson.permadeath.util.script.CoreScriptRegistry.CANCEL_METADATA, Main.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScriptPlace(BlockPlaceEvent event) {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("block_type", event.getBlock().getType().name());
        context.put("block_location", event.getBlock().getLocation());
        
        Main.getInstance().getScriptManager().fireTrigger(dev.itsrealperson.permadeath.api.script.ScriptTrigger.BLOCK_PLACE, event.getPlayer(), null, context);
        
        if (event.getPlayer().hasMetadata(dev.itsrealperson.permadeath.util.script.CoreScriptRegistry.CANCEL_METADATA)) {
            event.setCancelled(true);
            event.getPlayer().removeMetadata(dev.itsrealperson.permadeath.util.script.CoreScriptRegistry.CANCEL_METADATA, Main.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScriptBucket(PlayerBucketFillEvent event) {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("block_location", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
        
        Main.getInstance().getScriptManager().fireTrigger(dev.itsrealperson.permadeath.api.script.ScriptTrigger.BUCKET_FILL, event.getPlayer(), null, context);
        
        if (event.getPlayer().hasMetadata(dev.itsrealperson.permadeath.util.script.CoreScriptRegistry.CANCEL_METADATA)) {
            event.setCancelled(true);
            event.getPlayer().removeMetadata(dev.itsrealperson.permadeath.util.script.CoreScriptRegistry.CANCEL_METADATA, Main.getInstance());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (Main.getInstance().getScriptManager().isLogicOverridden("CORE_BLOCK_DAMAGE")) return;

        if (Main.getInstance().getNetheriteBlock().isInfernalNetherite(e.getBlock().getLocation())) {
            Main.getInstance().getNetheriteBlock().onBlockBreak(e);
            return;
        }

        if (Main.getInstance().getEndData() != null && Main.getInstance().getDay() >= 30) {

            EndDataManager ma = Main.getInstance().getEndData();

            if (ma.getConfig().contains("RegenZoneLocation")) {

                Location loc = buildLocation(ma.getConfig().getString("RegenZoneLocation"));

                if (e.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {

                    if (e.getBlock().getLocation().distance(loc) <= 4) {

                        e.setCancelled(true);
                        e.getPlayer().sendMessage(TextUtils.format("&cNo puedes romper bloques cerca de la Zona de Regeneración."));
                    }
                }
            }
        }

        // Daño por picar bloques
        if (Main.getInstance().getDay() >= 50) {
            boolean damage = true;

            e.getPlayer().getInventory().getItemInMainHand();
            ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
            for (ItemStack s : NO_DAMAGE_TOOLS) {
                if (i.getType() == s.getType() && i.getItemMeta().isUnbreakable() && s.getItemMeta().isUnbreakable()) {
                    damage = false;
                    break;
                }
            }

            if (damage) {
                if (Main.instance.getDay() < 60) {
                    e.getPlayer().damage(1.0D);
                } else {
                    e.getPlayer().damage(16.0D);
                }
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {

        if (Main.getInstance().getDay() >= 50) {

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScriptSmelt(FurnaceSmeltEvent event) {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        context.put("item_type", event.getResult().getType().name());
        context.put("block_location", event.getBlock().getLocation());
        
        Main.getInstance().getScriptManager().fireTrigger(dev.itsrealperson.permadeath.api.script.ScriptTrigger.FURNACE_SMELT, null, null, context);
        
        // El script puede cambiar el resultado guardándolo en la metadata
        if (event.getBlock().hasMetadata("pdc_smelt_result")) {
            String newMat = event.getBlock().getMetadata("pdc_smelt_result").get(0).asString();
            event.getResult().setType(Material.valueOf(newMat));
            event.getBlock().removeMetadata("pdc_smelt_result", Main.getInstance());
        }
    }

    @EventHandler
    public void onBlockFurnace(FurnaceSmeltEvent e) {
        if (Main.getInstance().getScriptManager().isLogicOverridden("CORE_FURNACE_NERF")) return;

        if (Main.getInstance().getDay() >= 50) {

            if (e.getResult() != null) {

                if (e.getResult().getType() == Material.IRON_INGOT) {

                    ItemStack resu = e.getResult();
                    resu.setType(Material.IRON_NUGGET);
                    e.setResult(resu);
                }

                if (e.getResult().getType() == Material.GOLD_INGOT) {

                    ItemStack resu = e.getResult();
                    resu.setType(Material.GOLD_NUGGET);
                    e.setResult(resu);
                }

            }
        }
    }

    private Location buildLocation(String s) {

        // X;Y;Z;WORLD
        String[] split = s.split(";");

        Double x = Double.valueOf(split[0]);
        Double y = Double.valueOf(split[1]);
        Double z = Double.valueOf(split[2]);
        World w = Bukkit.getWorld(split[3]);

        return new Location(w, x, y, z);
    }
}

















