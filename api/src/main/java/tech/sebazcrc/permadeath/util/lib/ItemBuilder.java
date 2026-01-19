package tech.sebazcrc.permadeath.util.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.permadeath.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemBuilder {
    protected final ItemStack is;
    protected final ItemMeta im;

    public ItemBuilder(Material material) {
        this.is = new ItemStack(material);
        this.im = this.is.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.is = new ItemStack(material, amount);
        this.im = this.is.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.is = itemStack.clone();
        this.im = this.is.getItemMeta();
    }

    public ItemBuilder setDurability(int durability) {
        if (this.im instanceof org.bukkit.inventory.meta.Damageable damageable) {
            damageable.setDamage(durability);
        }
        return this;
    }

    public ItemBuilder setUnbrekeable(boolean b) {
        this.im.setUnbreakable(b);
        return this;
    }

    public ItemBuilder setCustomModelData(int model) {
        this.im.setCustomModelData(model);
        return this;
    }

    public ItemBuilder setCustomModelData(int model, boolean b) {
        if (b) this.im.setCustomModelData(model);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        this.im.displayName(TextUtils.formatComponent(name));
        return this;
    }

    public ItemBuilder setDisplayName(Component name) {
        this.im.displayName(name);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        this.im.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach((ench, level) -> this.im.addEnchant(ench, level, true));
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemflag) {
        this.im.addItemFlags(itemflag);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.im.lore(lore.stream().map(TextUtils::formatComponent).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder setLoreComponents(List<Component> lore) {
        this.im.lore(lore);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        this.im.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemStack build() {
        this.is.setItemMeta(this.im);
        return this.is;
    }
}


