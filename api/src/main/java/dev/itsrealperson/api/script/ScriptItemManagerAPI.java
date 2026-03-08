package dev.itsrealperson.permadeath.api.script;

import org.bukkit.inventory.ItemStack;
import java.util.Collection;

/**
 * Gestor para ítems creados mediante scripts YAML.
 */
public interface ScriptItemManagerAPI {

    /**
     * Registra un nuevo ítem en el sistema.
     */
    void registerItem(ScriptItem item);

    /**
     * Obtiene una instancia del ítem personalizado.
     * @param id El ID del ítem (ej: "ESPADA_ZEUS").
     * @return El ItemStack listo para usar o null si no existe.
     */
    ItemStack getItem(String id);

    /**
     * Comprueba si un ItemStack es un ítem personalizado específico.
     */
    boolean isCustomItem(ItemStack item, String id);

    /**
     * @return Todos los IDs de ítems registrados.
     */
    Collection<String> getRegisteredItemIds();
}
