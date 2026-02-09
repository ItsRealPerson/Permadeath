package dev.itsrealperson.permadeath.api;

import org.bukkit.inventory.ItemStack;
import java.util.Optional;
import java.util.Set;

/**
 * Interfaz para el registro central de ítems de Permadeath.
 */
public interface ItemRegistryAPI {

    /**
     * Registra un nuevo ítem en el registro global.
     * @param id Identificador único (ej: "medalla_superviviente").
     * @param item El ítem a registrar.
     */
    void registerItem(String id, ItemStack item);

    /**
     * Obtiene un ítem del registro.
     * @param id Identificador del ítem.
     * @return Un Optional con el ítem (clonado) si existe.
     */
    Optional<ItemStack> getItem(String id);

    /**
     * @return Todas las claves registradas actualmente.
     */
    Set<String> getRegisteredIds();
}
