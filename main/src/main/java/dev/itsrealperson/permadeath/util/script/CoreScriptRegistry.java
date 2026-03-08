package dev.itsrealperson.permadeath.util.script;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.script.ScriptManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
                        Entity spawned = locEntity.getWorld().spawnEntity(locEntity.getLocation().add(Math.random()*4-2, 0, Math.random()*4-2), type);
                        
                        // Si es TNT, le aplicamos la potencia del core si se pide
                        if (spawned instanceof org.bukkit.entity.TNTPrimed tnt && args.containsKey("yield")) {
                            float yield = Float.parseFloat(args.get("yield"));
                            tnt.setYield(yield);
                            tnt.setFuseTicks(60); // 3 segundos como el core
                        }
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

        // --- ACCIONES DE COMUNICACIÓN Y EFECTOS ---
        manager.registerAction("send_title", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                String title = org.bukkit.ChatColor.translateAlternateColorCodes('&', context.replacePlaceholders(args.getOrDefault("title", "")));
                String subtitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', context.replacePlaceholders(args.getOrDefault("subtitle", "")));
                p.sendTitle(title, subtitle, 10, 70, 20);
            }
        });

        manager.registerAction("set_bossbar", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                String id = args.getOrDefault("id", "default_bar");
                String title = org.bukkit.ChatColor.translateAlternateColorCodes('&', context.replacePlaceholders(args.getOrDefault("title", "Barra")));
                double progress = dev.itsrealperson.permadeath.api.script.FormulaEvaluator.evaluate(context.replacePlaceholders(args.getOrDefault("progress", "1.0")), java.util.Map.of());
                org.bukkit.boss.BarColor color = org.bukkit.boss.BarColor.valueOf(args.getOrDefault("color", "RED").toUpperCase());
                
                org.bukkit.boss.BossBar bar;
                if (p.hasMetadata("pdc_bar_" + id)) {
                    bar = (org.bukkit.boss.BossBar) p.getMetadata("pdc_bar_" + id).get(0).value();
                    bar.setTitle(title);
                } else {
                    bar = Bukkit.createBossBar(title, color, org.bukkit.boss.BarStyle.SOLID);
                    bar.addPlayer(p);
                    p.setMetadata("pdc_bar_" + id, new FixedMetadataValue(plugin, bar));
                }
                bar.setProgress(Math.max(0, Math.min(1, progress)));
            }
        });

        manager.registerAction("remove_bossbar", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                String id = args.getOrDefault("id", "default_bar");
                if (p.hasMetadata("pdc_bar_" + id)) {
                    org.bukkit.boss.BossBar bar = (org.bukkit.boss.BossBar) p.getMetadata("pdc_bar_" + id).get(0).value();
                    bar.removeAll();
                    p.removeMetadata("pdc_bar_" + id, plugin);
                }
            }
        });

        manager.registerAction("send_actionbar", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                String msg = org.bukkit.ChatColor.translateAlternateColorCodes('&', context.replacePlaceholders(args.getOrDefault("text", "")));
                p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(msg));
            }
        });

        manager.registerAction("play_sound", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                try {
                    org.bukkit.Sound sound = org.bukkit.Sound.valueOf(args.getOrDefault("sound", "ENTITY_EXPERIENCE_ORB_PICKUP").toUpperCase());
                    float vol = Float.parseFloat(args.getOrDefault("volume", "1.0"));
                    float pitch = Float.parseFloat(args.getOrDefault("pitch", "1.0"));
                    p.playSound(p.getLocation(), sound, vol, pitch);
                } catch (Exception ignored) {}
            }
        });

        // --- ACCIONES DE FÍSICA ---
        manager.registerAction("velocity", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                try {
                    double x = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("x", "0")));
                    double y = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("y", "0")));
                    double z = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("z", "0")));
                    ent.setVelocity(new org.bukkit.util.Vector(x, y, z));
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("shockwave", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                double radius = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("radius", "5")));
                double power = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("power", "1.5")));
                for (Entity nearby : ent.getNearbyEntities(radius, radius, radius)) {
                    if (nearby instanceof LivingEntity liv) {
                        org.bukkit.util.Vector vec = liv.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize().multiply(power);
                        liv.setVelocity(vec);
                    }
                }
            }
        });

        manager.registerAction("pull_closer", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                double radius = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("radius", "10")));
                double speed = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("speed", "0.5")));
                for (Entity nearby : ent.getNearbyEntities(radius, radius, radius)) {
                    if (nearby instanceof LivingEntity liv) {
                        org.bukkit.util.Vector vec = ent.getLocation().toVector().subtract(liv.getLocation().toVector()).normalize().multiply(speed);
                        liv.setVelocity(vec);
                    }
                }
            }
        });

        // --- ACCIONES DE INVENTARIO ---
        manager.registerAction("give_item", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                try {
                    org.bukkit.Material mat = org.bukkit.Material.valueOf(args.getOrDefault("type", "STONE").toUpperCase());
                    int amount = Integer.parseInt(context.replacePlaceholders(args.getOrDefault("amount", "1")));
                    p.getInventory().addItem(new org.bukkit.inventory.ItemStack(mat, amount));
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("drop_item", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent != null) {
                try {
                    org.bukkit.Material mat = org.bukkit.Material.valueOf(args.getOrDefault("type", "STONE").toUpperCase());
                    int amount = Integer.parseInt(context.replacePlaceholders(args.getOrDefault("amount", "1")));
                    ent.getWorld().dropItemNaturally(ent.getLocation(), new org.bukkit.inventory.ItemStack(mat, amount));
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("remove_item", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof Player p) {
                try {
                    org.bukkit.Material mat = org.bukkit.Material.valueOf(args.getOrDefault("type", "STONE").toUpperCase());
                    int amount = Integer.parseInt(context.replacePlaceholders(args.getOrDefault("amount", "1")));
                    p.getInventory().removeItem(new org.bukkit.inventory.ItemStack(mat, amount));
                } catch (Exception ignored) {}
            }
        });

        // --- ACCIONES DE COMBATE AVANZADO ---
        manager.registerAction("shoot_projectile", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof LivingEntity liv) {
                try {
                    String type = args.getOrDefault("type", "ARROW").toUpperCase();
                    Class<? extends org.bukkit.entity.Projectile> projClass = switch(type) {
                        case "FIREBALL" -> org.bukkit.entity.Fireball.class;
                        case "WITHER_SKULL" -> org.bukkit.entity.WitherSkull.class;
                        case "SMALL_FIREBALL" -> org.bukkit.entity.SmallFireball.class;
                        case "SNOWBALL" -> org.bukkit.entity.Snowball.class;
                        default -> org.bukkit.entity.Arrow.class;
                    };
                    liv.launchProjectile(projClass);
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("set_target", (context, args) -> {
            if (context.getCaster() instanceof org.bukkit.entity.Mob mob && context.getTarget() instanceof LivingEntity liv) {
                mob.setTarget(liv);
            }
        });

        manager.registerAction("set_dragon_phase", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof org.bukkit.entity.EnderDragon dragon) {
                try {
                    dragon.setPhase(org.bukkit.entity.EnderDragon.Phase.valueOf(args.getOrDefault("phase", "FLYING").toUpperCase()));
                } catch (Exception ignored) {}
            }
        });

        manager.registerAction("set_invulnerable", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                ent.setInvulnerable(Boolean.parseBoolean(args.getOrDefault("value", "true")));
            }
        });

        manager.registerAction("heal", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof LivingEntity liv) {
                double amount = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("amount", "1.0")));
                double max = liv.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                liv.setHealth(Math.min(max, liv.getHealth() + amount));
            }
        });

        manager.registerAction("message_raw", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent instanceof Player p) {
                String json = context.replacePlaceholders(args.getOrDefault("json", "{\"text\":\"\"}"));
                try {
                    p.spigot().sendMessage(net.md_5.bungee.chat.ComponentSerializer.parse(json));
                } catch (Exception e) {
                    p.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', json));
                }
            }
        });

        // --- ACCIONES VISUALES PRO (PacketEvents) ---
        manager.registerAction("camera_shake", (context, args) -> {
            Location loc = (context.getCaster() != null ? context.getCaster().getLocation() : (context.getTarget() != null ? context.getTarget().getLocation() : null));
            if (loc != null) {
                float power = Float.parseFloat(context.replacePlaceholders(args.getOrDefault("power", "1.0")));
                dev.itsrealperson.permadeath.util.PacketManager.sendCameraShake(loc, power);
            }
        });

        manager.registerAction("guardian_beam", (context, args) -> {
            if (context.getCaster() != null && context.getTarget() != null) {
                dev.itsrealperson.permadeath.util.PacketManager.sendGuardianBeam(
                    context.getCaster().getEntityId(), 
                    context.getTarget().getEntityId(), 
                    context.getCaster().getLocation()
                );
            }
        });

        manager.registerAction("set_sky_darkness", (context, args) -> {
            if (context.getCaster() instanceof Player p) {
                float val = Float.parseFloat(context.replacePlaceholders(args.getOrDefault("value", "0.2")));
                dev.itsrealperson.permadeath.util.PacketManager.setSkyDarkness(p, val);
            }
        });

        manager.registerAction("set_warning_border", (context, args) -> {
            if (context.getCaster() instanceof Player p) {
                boolean active = Boolean.parseBoolean(args.getOrDefault("value", "true"));
                dev.itsrealperson.permadeath.util.PacketManager.setWarningBorder(p, active);
            }
        });

        manager.registerAction("play_particle", (context, args) -> {
            Entity ent = (context.getTarget() != null ? context.getTarget() : context.getCaster());
            if (ent != null) {
                try {
                    org.bukkit.Particle part = org.bukkit.Particle.valueOf(args.getOrDefault("type", "FLAME").toUpperCase());
                    int amount = Integer.parseInt(context.replacePlaceholders(args.getOrDefault("amount", "10")));
                    double spread = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("spread", "0.5")));
                    ent.getWorld().spawnParticle(part, ent.getLocation(), amount, spread, spread, spread, 0.1);
                } catch (Exception ignored) {}
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

        manager.registerAction("delete_blocks", (context, args) -> {
            Location center = (context.getCaster() != null ? context.getCaster().getLocation() : null);
            if (center != null) {
                int radius = Integer.parseInt(args.getOrDefault("radius", "2"));
                int height = Integer.parseInt(args.getOrDefault("height", "5"));
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        for (int y = -height; y <= height; y++) {
                            org.bukkit.block.Block b = center.clone().add(x, y, z).getBlock();
                            if (b.getType() == org.bukkit.Material.END_STONE || b.getType() == org.bukkit.Material.END_STONE_BRICKS) {
                                b.setType(org.bukkit.Material.AIR);
                            }
                        }
                    }
                }
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

        // Modificar metadatos con matemáticas (ej: cooldowns o contadores)
        manager.registerAction("modify_metadata", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            String key = args.get("key");
            String op = args.getOrDefault("op", "SET"); // SET, ADD, SUB
            if (ent != null && key != null) {
                double current = 0;
                if (ent.hasMetadata(key)) current = ent.getMetadata(key).get(0).asDouble();
                
                String valStr = context.replacePlaceholders(args.getOrDefault("value", "0"));
                double val = dev.itsrealperson.permadeath.api.script.FormulaEvaluator.evaluate(valStr, java.util.Map.of());
                
                double result = switch (op.toUpperCase()) {
                    case "ADD" -> current + val;
                    case "SUB" -> current - val;
                    default -> val;
                };
                
                ent.setMetadata(key, new FixedMetadataValue(plugin, result));
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

        manager.registerCondition("is_exposed_to_sky", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent != null) {
                return ent.getLocation().getBlockY() >= ent.getWorld().getHighestBlockYAt(ent.getLocation());
            }
            return false;
        });

        manager.registerCondition("is_holding", (context, args) -> {
            if (context.getCaster() instanceof Player p) {
                org.bukkit.Material mat = org.bukkit.Material.valueOf(args.getOrDefault("type", "AIR").toUpperCase());
                return p.getInventory().getItemInMainHand().getType() == mat || p.getInventory().getItemInOffHand().getType() == mat;
            }
            return false;
        });

        manager.registerCondition("is_wearing", (context, args) -> {
            if (context.getCaster() instanceof Player p) {
                String type = args.getOrDefault("type", "AIR");
                String slot = args.getOrDefault("slot", "HEAD").toUpperCase();
                
                // Soporte especial para la Máscara del Abismo (Solo Accesorios)
                if (type.equalsIgnoreCase("ABYSSAL_MASK")) {
                    org.bukkit.inventory.ItemStack[] acc = dev.itsrealperson.permadeath.util.inventory.AccessoryInventory.load(p);
                    if (acc != null) {
                        for (org.bukkit.inventory.ItemStack item : acc) {
                            if (dev.itsrealperson.permadeath.util.item.PermadeathItems.isAbyssalMask(item)) return true;
                        }
                    }
                    return false;
                }

                org.bukkit.Material mat = org.bukkit.Material.valueOf(type.toUpperCase());
                org.bukkit.inventory.ItemStack item = switch(slot) {
                    case "HEAD" -> p.getInventory().getHelmet();
                    case "CHEST" -> p.getInventory().getChestplate();
                    case "LEGS" -> p.getInventory().getLeggings();
                    case "FEET" -> p.getInventory().getBoots();
                    default -> null;
                };
                return item != null && item.getType() == mat;
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

        // --- NUEVAS CONDICIONES DE ESTADO ---
        manager.registerCondition("is_sneaking", (context, args) -> {
            return context.getCaster() instanceof Player p && p.isSneaking();
        });

        manager.registerCondition("is_sprinting", (context, args) -> {
            return context.getCaster() instanceof Player p && p.isSprinting();
        });

        manager.registerCondition("is_gliding", (context, args) -> {
            return context.getCaster() instanceof Player p && p.isGliding();
        });

        manager.registerCondition("is_burning", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            return ent != null && ent.getFireTicks() > 0;
        });

        manager.registerCondition("is_wet", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            return ent != null && (ent.isInWater() || (ent.getWorld().hasStorm() && ent.getLocation().getBlockY() >= ent.getWorld().getHighestBlockYAt(ent.getLocation())));
        });

        manager.registerCondition("has_potion_effect", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent instanceof LivingEntity liv) {
                org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(args.getOrDefault("type", "SPEED").toUpperCase());
                return type != null && liv.hasPotionEffect(type);
            }
            return false;
        });

        manager.registerCondition("light_level", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent != null) {
                int req = Integer.parseInt(args.getOrDefault("value", "0"));
                String op = args.getOrDefault("op", ">=");
                int level = ent.getLocation().getBlock().getLightLevel();
                return compare(level, req, op);
            }
            return false;
        });

        manager.registerCondition("y_level", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            if (ent != null) {
                int req = Integer.parseInt(args.getOrDefault("value", "64"));
                String op = args.getOrDefault("op", ">=");
                int y = ent.getLocation().getBlockY();
                return compare(y, req, op);
            }
            return false;
        });

        manager.registerCondition("check_metadata", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            String key = args.get("key");
            if (ent != null && key != null && ent.hasMetadata(key)) {
                double val = ent.getMetadata(key).get(0).asDouble();
                double req = Double.parseDouble(context.replacePlaceholders(args.getOrDefault("value", "0")));
                String op = args.getOrDefault("op", "==");
                return compare(val, req, op);
            }
            return false;
        });

        manager.registerCondition("has_permission", (context, args) -> {
            return context.getCaster() instanceof Player p && p.hasPermission(args.getOrDefault("node", "permadeath.use"));
        });

        manager.registerCondition("chance_by_day", (context, args) -> {
            double base = Double.parseDouble(args.getOrDefault("base", "0"));
            double inc = Double.parseDouble(args.getOrDefault("increment", "0"));
            double currentDay = dev.itsrealperson.permadeath.api.PermadeathAPI.getDay();
            double finalChance = base + (inc * currentDay);
            return Math.random() * 100 <= finalChance;
        });

        manager.registerCondition("is_dragon_landing", (context, args) -> {
            Entity ent = (context.getCaster() != null ? context.getCaster() : context.getTarget());
            return ent instanceof org.bukkit.entity.EnderDragon dragon && 
                   (dragon.getPhase() == org.bukkit.entity.EnderDragon.Phase.LAND_ON_PORTAL || 
                    dragon.getPhase() == org.bukkit.entity.EnderDragon.Phase.HOVER);
        });

        manager.registerCondition("is_daytime", (context, args) -> {
            World w = (context.getCaster() != null ? context.getCaster().getWorld() : Bukkit.getWorlds().get(0));
            long time = w.getTime();
            return time >= 0 && time < 12000;
        });

        manager.registerCondition("is_raining", (context, args) -> {
            World w = (context.getCaster() != null ? context.getCaster().getWorld() : Bukkit.getWorlds().get(0));
            return w.hasStorm();
        });
    }

    private static boolean compare(double val, double req, String op) {
        return switch (op) {
            case ">" -> val > req;
            case "<" -> val < req;
            case ">=" -> val >= req;
            case "<=" -> val <= req;
            case "==" -> val == req;
            case "!=" -> val != req;
            default -> val >= req;
        };
    }
}
