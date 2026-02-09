package dev.itsrealperson.permadeath.api;

import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Interfaz para gestionar el loot de Permadeath desde la API.
 */
public interface LootManagerAPI {

    /**
     * Añade un ítem a la tabla de loot de las cápsulas del Abismo.
     * @param item El ítem a añadir.
     * @param chance Probabilidad (0-100).
     */
    void addAbyssLoot(ItemStack item, int chance);

    /**
     * Genera una lista de ítems de loot para una cápsula del Abismo, incluyendo el loot dinámico.
     * @return Lista de ítems generados.
     */
    List<ItemStack> generateAbyssLoot();

    /**
     * @return Una lista de los ítems registrados dinámicamente.
     */
    List<ItemStack> getDynamicAbyssLoot();
}