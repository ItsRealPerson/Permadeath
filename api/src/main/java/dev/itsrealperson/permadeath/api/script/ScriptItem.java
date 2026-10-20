package dev.itsrealperson.permadeath.api.script;

import org.bukkit.Material;
import java.util.List;
import java.util.Map;

/**
 * Define un ítem personalizado creado mediante scripts con soporte de recetas múltiples.
 */
public record ScriptItem(
    String id,
    Material material,
    String displayName,
    List<String> lore,
    int customModelData,
    boolean unbreakable,
    String color,
    Map<String, Integer> enchantments,
    Map<String, Double> attributes,
    Map<String, String> nbtTags,
    List<ScriptRecipe> recipes
) {
    public enum RecipeType { SHAPED, SMITHING }

    /**
     * Representa una receta (Crafteo o Herrería).
     */
    public record ScriptRecipe(
        RecipeType type,
        // Para SHAPED
        List<String> shape, 
        Map<Character, String> ingredients,
        // Para SMITHING
        String template,
        String base,
        String addition
    ) {}
}
