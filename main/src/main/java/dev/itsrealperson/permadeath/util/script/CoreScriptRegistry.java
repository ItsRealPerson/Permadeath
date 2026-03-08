package dev.itsrealperson.permadeath.util.script;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.script.ScriptManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Registra las acciones y condiciones nativas del Core en el motor de scripts.
 */
public class CoreScriptRegistry {

    public static final String CANCEL_METADATA = "pdc_script_cancel";

    public static void registerNativeActions(Main plugin) {
        ScriptManagerAPI manager = plugin.getScriptManager();

        // --- ACCIONES DE MENSAJERÍA ---
        manager.registerAction("broadcast", (context, args) -> {
            String msg = args.get("message");
            if (msg != null) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', context.replacePlaceholders(msg)));
        });

        // --- ACCIONES DE ENTIDAD ---
        manager.registerAction("core_make_hostile", (context, args) -> {
            Entity target = context.getTarget() != null ? context.getTarget() : context.getCaster();
            if (target instanceof LivingEntity liv) {
                plugin.getNmsAccessor().injectHostilePathfinders(liv);
            }
        });

        manager.registerAction("core_apply_day_buffs", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof LivingEntity liv) {
                plugin.deathTrainEffects(liv);
            }
        });

        // --- ACCIONES DE SISTEMA ---
        manager.registerAction("cancel_event", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                ent.setMetadata(CANCEL_METADATA, new FixedMetadataValue(plugin, true));
            }
        });

        manager.registerAction("damage", (context, args) -> {
            Entity target = context.getTarget() != null ? context.getTarget() : context.getCaster();
            if (target instanceof LivingEntity liv) {
                String amountStr = context.replacePlaceholders(args.getOrDefault("amount", "1.0"));
                double amount = dev.itsrealperson.permadeath.api.script.FormulaEvaluator.evaluate(amountStr, java.util.Map.of());
                liv.damage(amount, context.getCaster());
            }
        });

        manager.registerAction("set_day", (context, args) -> {
            if (args.containsKey("value")) {
                String dayStr = context.replacePlaceholders(args.get("value"));
                dev.itsrealperson.permadeath.data.DateManager.getInstance().setDay(Bukkit.getConsoleSender(), dayStr);
            }
        });

        manager.registerAction("spawn_entity", (context, args) -> {
            String typeStr = args.get("type");
            if (typeStr != null) {
                try {
                    org.bukkit.entity.EntityType type = org.bukkit.entity.EntityType.valueOf(typeStr.toUpperCase());
                    Entity locEntity = (context.getTarget() != null ? context.getTarget() : context.getCaster());
                    if (locEntity != null) {
                        locEntity.getWorld().spawnEntity(locEntity.getLocation(), type);
                    }
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("remove_entity", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) ent.remove();
        });

        // --- ACCIONES DE ESTADO Y CLIMA ---
        manager.registerAction("apply_potion", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof LivingEntity liv) {
                try {
                    org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(args.getOrDefault("type", "SPEED").toUpperCase());
                    int duration = (int) dev.itsrealperson.permadeath.api.script.FormulaEvaluator.evaluate(context.replacePlaceholders(args.getOrDefault("duration", "200")), java.util.Map.of());
                    int amplifier = (int) dev.itsrealperson.permadeath.api.script.FormulaEvaluator.evaluate(context.replacePlaceholders(args.getOrDefault("amplifier", "0")), java.util.Map.of());
                    if (type != null) liv.addPotionEffect(new org.bukkit.potion.PotionEffect(type, duration, amplifier));
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("set_weather", (context, args) -> {
            String type = args.getOrDefault("type", "CLEAR").toUpperCase();
            World w = (context.getCaster() != null ? context.getCaster().getWorld() : Bukkit.getWorlds().get(0));
            if (type.equals("THUNDER")) {
                w.setStorm(true);
                w.setThundering(true);
            } else if (type.equals("STORM")) {
                w.setStorm(true);
                w.setThundering(false);
            } else {
                w.setStorm(false);
                w.setThundering(false);
            }
        });

        manager.registerAction("set_gamerule", (context, args) -> {
            String rule = args.get("rule");
            String value = args.get("value");
            if (rule != null && value != null) {
                World w = (context.getCaster() != null ? context.getCaster().getWorld() : Bukkit.getWorlds().get(0));
                w.setGameRuleValue(rule, value);
            }
        });

        manager.registerAction("teleport", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                try {
                    double x = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("x", "0")));
                    double y = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("y", "0")));
                    double z = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("z", "0")));
                    ent.teleport(new Location(ent.getWorld(), x, y, z));
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("strike_lightning", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                ent.getWorld().strikeLightning(ent.getLocation());
            }
        });

        manager.registerAction("command", (context, args) -> {
            String cmd = context.replacePlaceholders(args.get("cmd"));
            String as = args.getOrDefault("as", "CONSOLE").toUpperCase();
            if (cmd != null) {
                if (as.equals("PLAYER") && context.getCaster() instanceof org.bukkit.entity.Player p) {
                    p.performCommand(cmd);
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            }
        });

        manager.registerAction("set_block", (context, args) -> {
            Location loc = null;
            if (context.getMetadata().containsKey("block_location")) {
                loc = (Location) context.getMetadata().get("block_location");
            } else if (context.getTarget() != null) {
                loc = context.getTarget().getLocation();
            }
            
            if (loc != null) {
                try {
                    org.bukkit.Material mat = org.bukkit.Material.valueOf(args.getOrDefault("type", "AIR").toUpperCase());
                    loc.getBlock().setType(mat);
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("set_metadata", (context, args) -> {
            String key = args.get("key");
            String value = context.replacePlaceholders(args.get("value"));
            if (key != null && value != null) {
                // Si hay ubicación de bloque, guardamos en el bloque
                if (context.getMetadata().containsKey("block_location")) {
                    Location loc = (Location) context.getMetadata().get("block_location");
                    loc.getBlock().setMetadata(key, new FixedMetadataValue(plugin, value));
                } else {
                    // Si no, en la entidad caster
                    Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
                    if (ent != null) ent.setMetadata(key, new FixedMetadataValue(plugin, value));
                }
            }
        });

        manager.registerAction("burn", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                int duration = Integer.parseInt(context.replacePlaceholders(args.getOrDefault("duration", "100")));
                ent.setFireTicks(duration);
            }
        });

        // --- ACCIONES DE JUGADOR Y SISTEMA ---
        manager.registerAction("ban_player", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof org.bukkit.entity.Player p) {
                String reason = context.replacePlaceholders(args.getOrDefault("reason", "&cHas muerto permanentemente."));
                p.kickPlayer(org.bukkit.ChatColor.translateAlternateColorCodes('&', reason));
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(p.getName(), reason, null, "PermadeathCore");
            }
        });

        manager.registerAction("set_max_health", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof org.bukkit.entity.Player p) {
                String amountStr = context.replacePlaceholders(args.getOrDefault("amount", "20.0"));
                double amount = dev.itsrealperson.permadeath.api.script.FormulaEvaluator.evaluate(amountStr, java.util.Map.of());
                var attr = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                if (attr != null) attr.setBaseValue(amount);
            }
        });

        manager.registerAction("explode_bed", (context, args) -> {
            if (context.getMetadata().containsKey("bed_location")) {
                Location loc = (Location) context.getMetadata().get("bed_location");
                loc.getWorld().createExplosion(loc, 2.0f, true, false);
            }
        });

        // --- ACCIONES VISUALES (PACKETEVENTS) ---
        manager.registerAction("fake_lightning", (context, args) -> {
            Entity locEntity = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (locEntity != null) {
                dev.itsrealperson.permadeath.util.PacketManager.sendFakeLightning(locEntity.getLocation());
            }
        });

        // --- REGISTRO DE CONDICIONES ---
        registerNativeConditions(manager);
    }

    private static void registerNativeConditions(ScriptManagerAPI manager) {
        // ¿Día actual mayor o igual a X?
        manager.registerCondition("day_at_least", (context, args) -> {
            int required = Integer.parseInt(args.getOrDefault("value", "0"));
            return dev.itsrealperson.permadeath.api.PermadeathAPI.getDay() >= required;
        });

        // ¿Día actual menor o igual a X?
        manager.registerCondition("day_at_most", (context, args) -> {
            int required = Integer.parseInt(args.getOrDefault("value", "1000"));
            return dev.itsrealperson.permadeath.api.PermadeathAPI.getDay() <= required;
        });

        // ¿Es un mob pacífico?
        manager.registerCondition("is_passive", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof LivingEntity liv) {
                return !dev.itsrealperson.permadeath.util.Utils.isHostileMob(liv.getType());
            }
            return false;
        });

        // Probabilidad aleatoria (0-100)
        manager.registerCondition("chance", (context, args) -> {
            double prob = Double.parseDouble(args.getOrDefault("value", "100"));
            return Math.random() * 100 <= prob;
        });

        // Comprobar Mundo
        manager.registerCondition("is_world", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                return ent.getWorld().getName().equalsIgnoreCase(args.get("name"));
            }
            return false;
        });

        // Comprobar Tipo de Ítem (usado en ITEM_CONSUME)
        manager.registerCondition("is_item_type", (context, args) -> {
            if (context.getMetadata().containsKey("item_type")) {
                return ((String) context.getMetadata().get("item_type")).equalsIgnoreCase(args.get("type"));
            }
            return false;
        });

        // Comprobar Nombre de Ítem
        manager.registerCondition("is_item_name", (context, args) -> {
            if (context.getMetadata().containsKey("item_name")) {
                String name = (String) context.getMetadata().get("item_name");
                return org.bukkit.ChatColor.stripColor(name).equalsIgnoreCase(org.bukkit.ChatColor.stripColor(args.get("name")));
            }
            return false;
        });

        // Comprobar Entorno (NORMAL, NETHER, THE_END)
        manager.registerCondition("is_environment", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                return ent.getWorld().getEnvironment().name().equalsIgnoreCase(args.get("type"));
            }
            return false;
        });

        // Comprobar si está en el agua
        manager.registerCondition("is_in_water", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                org.bukkit.Material mat = ent.getLocation().getBlock().getType();
                org.bukkit.Material eyeMat = ent instanceof org.bukkit.entity.LivingEntity liv ? liv.getEyeLocation().getBlock().getType() : mat;
                return mat == org.bukkit.Material.WATER || eyeMat == org.bukkit.Material.WATER;
            }
            return false;
        });

        // Comprobar Ítem en Inventario
        manager.registerCondition("has_item", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof org.bukkit.entity.Player p) {
                String type = args.get("type");
                boolean invert = Boolean.parseBoolean(args.getOrDefault("invert", "false"));
                boolean has = false;
                
                // Soporte para ítems del Core
                if (type.equalsIgnoreCase("WATER_MEDAL")) {
                    has = dev.itsrealperson.permadeath.util.item.PermadeathItems.isWaterMedal(p.getInventory().getItemInOffHand());
                    if (!has) {
                        for (org.bukkit.inventory.ItemStack item : p.getInventory().getContents()) {
                            if (dev.itsrealperson.permadeath.util.item.PermadeathItems.isWaterMedal(item)) { has = true; break; }
                        }
                    }
                    if (!has) {
                        org.bukkit.inventory.ItemStack[] acc = dev.itsrealperson.permadeath.util.inventory.AccessoryInventory.load(p);
                        if (acc != null) {
                            for (org.bukkit.inventory.ItemStack item : acc) {
                                if (dev.itsrealperson.permadeath.util.item.PermadeathItems.isWaterMedal(item)) { has = true; break; }
                            }
                        }
                    }
                } else {
                    try {
                        org.bukkit.Material mat = org.bukkit.Material.valueOf(type.toUpperCase());
                        has = p.getInventory().contains(mat);
                    } catch (Exception ignored) {}
                }
                
                return invert ? !has : has;
            }
            return false;
        });

        // Comprobar proximidad a bloque
        manager.registerCondition("is_near_block", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                try {
                    org.bukkit.Material targetMat = org.bukkit.Material.valueOf(args.getOrDefault("type", "BEDROCK").toUpperCase());
                    int radius = Integer.parseInt(args.getOrDefault("radius", "1"));
                    Location base = ent.getLocation();
                    for (int x = -radius; x <= radius; x++) {
                        for (int y = -radius; y <= radius; y++) {
                            for (int z = -radius; z <= radius; z++) {
                                if (base.clone().add(x, y, z).getBlock().getType() == targetMat) return true;
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
            return false;
        });
    }
}
