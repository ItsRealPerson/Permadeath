package dev.itsrealperson.permadeath.pdcentities.command;

import dev.itsrealperson.permadeath.api.addons.AddonSubCommand;
import dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon;
import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntitiesSubCommand implements AddonSubCommand {

    @Override
    public String getName() { return "entities"; }

    @Override
    public String getDescription() { return "Gestionar entidades personalizadas."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length > 1 && args[0].equalsIgnoreCase("spawn")) {
            String templateId = args[1];
            var instance = MobManager.spawnMob(templateId, player.getLocation(), PdcEntitiesAddon.getApi().getDay());
            
            if (instance != null) {
                player.sendMessage("§a[PDC-Entities] Spawneado mob: §f" + templateId);
            } else {
                player.sendMessage("§cNo existe ninguna plantilla con el ID: " + templateId);
            }
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("permadeath.admin")) {
                sender.sendMessage("§cNo tienes permiso.");
                return true;
            }
            sender.sendMessage("§e[PDC-Entities] Recargando motor de entidades...");
            
            dev.itsrealperson.permadeath.api.addons.AddonRegistry.getAddon("PDC-Entities").ifPresent(addon -> {
                if (addon instanceof PdcEntitiesAddon pdcAddon) {
                    pdcAddon.reload();
                }
            });
            
            sender.sendMessage("§aRecarga completada con éxito.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§b§lPlantillas de Mobs Cargadas:");
            MobManager.getTemplates().keySet().forEach(id -> sender.sendMessage(" §7- §f" + id));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("toggle")) {
                PdcEntitiesAddon.debugMode = !PdcEntitiesAddon.debugMode;
                sender.sendMessage("§e[PDC-Entities] Modo Debug: " + (PdcEntitiesAddon.debugMode ? "§aACTIVADO" : "§cDESACTIVADO"));
                return true;
            }

            org.bukkit.entity.Entity target = player.getTargetEntity(10);
            if (target == null) {
                player.sendMessage("§cDebes mirar a una entidad.");
                return true;
            }

            var instance = MobManager.getInstance(target);
            if (instance == null) {
                player.sendMessage("§cEsa entidad no es un Mob de PDC-Entities.");
                return true;
            }

            sender.sendMessage("§7§m------------------------------------------");
            sender.sendMessage("§b§lInspector de PDC-Entities");
            sender.sendMessage("§7➤ §fTemplate: §b" + instance.getTemplate().getId());
            sender.sendMessage("§7➤ §fVida: §a" + String.format("%.1f", instance.getEntity().getHealth()) + "§7/§2" + instance.getEntity().getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getBaseValue());
            sender.sendMessage("§7➤ §fFórmula Vida: §e" + instance.getTemplate().getHealthFormula());
            
            if (!instance.getVariables().isEmpty()) {
                sender.sendMessage("§bVariables:");
                instance.getVariables().forEach((k, v) -> sender.sendMessage("  §7- §f" + k + ": §b" + v));
            }

            sender.sendMessage("§bAI Goals:");
            if (!instance.getTemplate().getAiGoals().isEmpty()) {
                instance.getTemplate().getAiGoals().forEach(goal -> sender.sendMessage("  §7- §f" + goal));
            } else {
                sender.sendMessage("  §7- §cNinguno");
            }

            sender.sendMessage("§bAI Targets:");
            if (!instance.getTemplate().getAiTargets().isEmpty()) {
                instance.getTemplate().getAiTargets().forEach(targetInfo -> sender.sendMessage("  §7- §f" + targetInfo));
            } else {
                sender.sendMessage("  §7- §cNinguno");
            }

            sender.sendMessage("§bSkills:");
            if (!instance.getTemplate().getSkills().isEmpty()) {
                instance.getTemplate().getSkills().forEach(skill -> {
                    var mechanic = skill.getMechanic();
                    String mechanicName = (mechanic != null) ? mechanic.getClass().getSimpleName() : "§cNULA (Fallo en registro)";
                    sender.sendMessage("  §7- §f" + mechanicName + " §e~" + skill.getTrigger() + (skill.getTriggerValue() > 0 ? ":" + skill.getTriggerValue() : ""));
                });
            } else {
                sender.sendMessage("  §7- §cNinguna");
            }

            if (!instance.getSkillLog().isEmpty()) {
                sender.sendMessage("§bHistorial de Ejecución:");
                instance.getSkillLog().forEach(sender::sendMessage);
            }
            
            sender.sendMessage("§7§m------------------------------------------");
            return true;
        }

        player.sendMessage("§cUsage: /pdc entities <spawn|debug>");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> sub = Arrays.asList("spawn", "debug", "reload", "list");
            return sub.stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Arrays.asList("toggle");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            return MobManager.getTemplates().keySet().stream()
                    .filter(id -> id.startsWith(args[1].toLowerCase()))
                    .toList();
        }
        
        return new ArrayList<>();
    }
}
