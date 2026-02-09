package dev.itsrealperson.permadeath.util.item;

import dev.itsrealperson.permadeath.api.ItemRegistryAPI;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ItemRegistryImpl implements ItemRegistryAPI {

    private final Map<String, ItemStack> items = new HashMap<>();

    @Override
    public void registerItem(String id, ItemStack item) {
        if (id == null || item == null) return;
        items.put(id.toLowerCase(), item.clone());
    }

    @Override
    public Optional<ItemStack> getItem(String id) {
        if (id == null) return Optional.empty();
        ItemStack item = items.get(id.toLowerCase());
        return Optional.ofNullable(item != null ? item.clone() : null);
    }

    @Override
    public Set<String> getRegisteredIds() {
        return items.keySet();
    }
}
