package tech.sebazcrc.permadeath.world.beginning.generator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.permadeath.world.beginning.BeginningManager;

import java.util.*;

public class BeginningLootTable {

    private final List<String> chances;
    private final List<Material> alreadyRolled;
    private final SplittableRandom random;

    public BeginningLootTable(BeginningManager man) {
        this.chances = new ArrayList<>();
        this.alreadyRolled = new ArrayList<>();
        this.random = new SplittableRandom();

        addItem(chances, Material.GOLD_INGOT, 5, 50, 60);
        addItem(chances, Material.GOLDEN_APPLE, 60, 1, 8);
        addItem(chances, Material.DIAMOND, 60, 16, 24);
        addItem(chances, Material.ARROW, 10, 10, 16);
        addItem(chances, Material.FIREWORK_ROCKET, 20, 55, 64);
        addItem(chances, Material.TOTEM_OF_UNDYING, 5, 1, 2);
        addItem(chances, Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1, 1, 1);
    }

    public void populateChest(Chest chest) {
        World w = chest.getWorld();
        if (!w.getName().toLowerCase().endsWith("beginning")) return;
        
        roll(chest);
    }

    private void addItem(List<String> list, Material mat, int chance, int min, int max) {
        list.add(mat.toString() + ";" + chance + ";" + min + ";" + max);
    }

    private void roll(Chest c) {
        alreadyRolled.clear();
        int rollTimes = random.nextInt(3) + 1;
        for (int i = 0; i < rollTimes; i++) {
            generate(c);
        }
    }

    private void generate(Chest chest) {
        Inventory inventory = chest.getInventory();
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                slots.add(i);
            }
        }
        if (slots.isEmpty()) return;

        Collections.shuffle(slots);
        int slotPointer = 0;

        for (String chanceEntry : chances) {
            if (slotPointer >= slots.size()) break;

            String[] split = chanceEntry.split(";");
            Material mat = getMaterial(split);

            if (random.nextInt(100) + 1 <= getChance(split) && !alreadyRolled.contains(mat)) {
                if (mat == Material.TOTEM_OF_UNDYING || mat == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                    inventory.setItem(slots.get(slotPointer++), new ItemStack(mat));
                    alreadyRolled.add(mat);
                    continue;
                }

                int ammount = Math.min(mat.getMaxStackSize(), Math.max(1, generateValue(getMin(split), getMax(split))));
                ItemStack s = new ItemStack(mat, ammount);
                inventory.setItem(slots.get(slotPointer++), s);

                if (slotPointer < slots.size()) {
                    int x = Math.min(mat.getMaxStackSize(), Math.max(1, ammount + getMin(split) / 2));
                    ItemStack s2 = new ItemStack(mat, x);
                    inventory.setItem(slots.get(slotPointer++), s2);
                }

                alreadyRolled.add(mat);
            }
        }
    }

    private int getMin(String[] s) {
        return Integer.parseInt(s[2]);
    }

    private int getMax(String[] s) {
        return Integer.parseInt(s[3]);
    }

    private int getChance(String[] s) {
        return Integer.parseInt(s[1]);
    }

    private Material getMaterial(String[] s) {
        return Material.valueOf(s[0]);
    }

    private int generateValue(int min, int max) {
        return min + (max > min ? random.nextInt(max - min + 1) : 0);
    }
}
