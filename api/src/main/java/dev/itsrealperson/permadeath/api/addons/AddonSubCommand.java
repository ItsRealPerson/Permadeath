package dev.itsrealperson.permadeath.api.addons;

import org.bukkit.command.CommandSender;
import java.util.List;

/**
 * Interfaz para sub-comandos registrados por addons.
 * Ejemplo: /pdc <subcomando> [args]
 */
public interface AddonSubCommand {
    
    /**
     * @return El nombre del sub-comando (ej: "entities").
     */
    String getName();

    /**
     * @return Descripción breve para el help.
     */
    String getDescription();

    /**
     * Ejecuta la lógica del sub-comando.
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Sugerencias para el tab-complete.
     */
    List<String> tabComplete(CommandSender sender, String[] args);
}
