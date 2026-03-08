package dev.itsrealperson.permadeath.api.script;

import org.bukkit.Material;
import java.util.List;
import java.util.Map;

/**
 * Define un ítem personalizado creado mediante scripts.
 */
public record ScriptItem(
    String id,
    Material material,
    String displayName,
    List<String> lore,
    int customModelData,
    Map<String, String> nbtTags
) {}
