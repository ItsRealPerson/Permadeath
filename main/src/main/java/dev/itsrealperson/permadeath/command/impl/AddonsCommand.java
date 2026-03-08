package dev.itsrealperson.permadeath.command.impl;

import dev.itsrealperson.permadeath.command.SubCommand;
import dev.itsrealperson.permadeath.util.TextUtils;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class AddonsCommand extends SubCommand {

    @Override
    public String getName() { return "addons"; }

    @Override
    public String getDescription() { return "Lista todos los addons de Permadeath cargados."; }

    @Override
    public String getUsage() { return "/pdc addons"; }

    @Override
    public String getPermission() { return "permadeath.admin"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var addons = dev.itsrealperson.permadeath.api.addons.AddonRegistry.getRegisteredAddons();
        
        sender.sendMessage(TextUtils.format("&7&m------------------------------------------"));
        sender.sendMessage(TextUtils.format(" [Permadeath] &b&lAddons Cargados (&f" + addons.size() + "&b&l):"));
        
        if (addons.isEmpty()) {
            sender.sendMessage(TextUtils.format("  &cNo hay addons registrados."));
        } else {
            for (var addon : addons) {
                sender.sendMessage(TextUtils.format("  &7➤ &f" + addon.getName() + " &bv" + addon.getVersion()));
            }
        }
        sender.sendMessage(TextUtils.format("&7&m------------------------------------------"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}