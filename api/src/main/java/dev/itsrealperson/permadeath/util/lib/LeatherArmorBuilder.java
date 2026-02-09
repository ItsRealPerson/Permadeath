package dev.itsrealperson.permadeath.util.lib;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorBuilder extends ItemBuilder {

    public LeatherArmorBuilder(Material material, int amount) {
        super(material, amount);
    }

    public LeatherArmorBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    public LeatherArmorBuilder setColor(Color color) {
        if (this.im instanceof LeatherArmorMeta lam) {
            lam.setColor(color);
        }
        return this;
    }
}
