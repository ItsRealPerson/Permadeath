package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;
import dev.itsrealperson.permadeath.util.inventory.AccessoryInventory;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class DrainOxygenMechanic implements Mechanic {
    private final int amount;
    private final Random random = new Random();

    public DrainOxygenMechanic(Map<String, String> args) {
        this.amount = Integer.parseInt(args.getOrDefault("amount", "20"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof Player p) {
                ItemStack mask = null;
                int maskIndex = -1;
                ItemStack[] accessories = AccessoryInventory.load(p);

                // 1. Intentar encontrar la Máscara del Abismo en el inventario de accesorios
                if (accessories != null) {
                    for (int i = 0; i < accessories.length; i++) {
                        if (PermadeathItems.isAbyssalMask(accessories[i])) {
                            mask = accessories[i];
                            maskIndex = i;
                            break;
                        }
                    }
                }

                // 2. Si tiene la máscara, drenamos su durabilidad (oxígeno de la máscara)
                if (mask != null && mask.getItemMeta() instanceof Damageable damageable) {
                    int currentDamage = damageable.getDamage();
                    
                    // Comprobar encantamiento Respiración Abisal (reduce la probabilidad de daño)
                    int enchantLevel = 0;
                    try {
                        NamespacedKey key = new NamespacedKey("permadeath", "abyssal_breathing");
                        enchantLevel = mask.getEnchantmentLevel(Registry.ENCHANTMENT.get(key));
                    } catch (Exception ignored) {}

                    // La cantidad de daño se reduce según el nivel de Respiración Abisal
                    // Chance base: 1/5. Con nivel 3: 1/20.
                    int chance = 5 + (enchantLevel * 5);
                    
                    if (random.nextInt(chance) < 5) { // Escalar el daño con el 'amount'
                        int damageToApply = Math.max(1, amount / 10); 
                        damageable.setDamage(currentDamage + damageToApply);
                        mask.setItemMeta(damageable);
                        
                        // Guardar cambios
                        accessories[maskIndex] = mask;
                        AccessoryInventory.saveFromItems(p, accessories);
                        
                        dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§b[Mechanic] Drenado oxígeno de Máscara para " + p.getName());
                    }
                } else {
                    // 3. Fallback: Drenar oxígeno vanilla si no tiene máscara
                    p.setRemainingAir(Math.max(0, p.getRemainingAir() - amount));
                    dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§b[Mechanic] Drenado oxígeno vanilla para " + p.getName());
                }
            }
        }
    }
}
