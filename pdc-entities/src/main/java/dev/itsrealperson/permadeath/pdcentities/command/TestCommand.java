package dev.itsrealperson.permadeath.pdcentities.command;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobTemplate;
import dev.itsrealperson.permadeath.pdcentities.script.SkillParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        // Crear una plantilla de prueba manualmente
        PdcMobTemplate testTemplate = new PdcMobTemplate("test_boss", EntityType.ZOMBIE, "&6Boss de Prueba");
        testTemplate.setHealthFormula("(day * 2) + 100"); // Día 100 -> 300 HP
        
        // Registrar la plantilla
        MobManager.registerTemplate(testTemplate);

        // Spawnear el mob (Asumiendo día 100 para la prueba)
        MobManager.spawnMob("test_boss", player.getLocation(), 100L);
        
        player.sendMessage("§a[PDC-Entities] Mob de prueba spawneado con fórmula: (day * 2) + 100");
        return true;
    }
}
