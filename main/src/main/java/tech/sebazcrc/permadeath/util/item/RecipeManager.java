package tech.sebazcrc.permadeath.util.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.lib.ItemBuilder;

public class RecipeManager {

    private Main instance;

    public RecipeManager(Main instance) {
        this.instance = instance;
    }

    public void registerRecipes() {

        try {
            registerHyperGAP();
            registerSuperGAP();
            registerShulkerUnCraft();
            registerEndRel();
            registerSmithingArmor();
            registerWaterMedal();
        } catch (IllegalStateException ex) {
            // Ignorar, la receta fue registrada antes probablemente
        }
    }

    private void registerWaterMedal() {
        ItemStack s = PermadeathItems.createWaterMedal();
        NamespacedKey key = new NamespacedKey(instance, "water_medal_craft");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("TGT", "GNG", "GGG");
        recipe.setIngredient('T', Material.TRIDENT);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('N', Material.HEART_OF_THE_SEA);
        Bukkit.addRecipe(recipe);
    }

    private void registerSmithingArmor() {
        registerAncestralFragment();
        // Recetas para las 4 piezas
        registerSingleSmithing("helmet", Material.NETHERITE_HELMET, NetheriteArmor.craftNetheriteHelmet());
        registerSingleSmithing("chestplate", Material.NETHERITE_CHESTPLATE, NetheriteArmor.craftNetheriteChest());
        registerSingleSmithing("leggings", Material.NETHERITE_LEGGINGS, NetheriteArmor.craftNetheriteLegs());
        registerSingleSmithing("boots", Material.NETHERITE_BOOTS, NetheriteArmor.craftNetheriteBoots());
    }

    private void registerAncestralFragment() {
        ItemStack s = NetheriteArmor.craftAncestralFragment();
        NamespacedKey key = new NamespacedKey(instance, "ancestral_fragment");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("DDD", "DND", "DDD");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        Bukkit.addRecipe(recipe);
    }

    private void registerSingleSmithing(String type, Material baseMat, ItemStack result) {
        NamespacedKey key = new NamespacedKey(instance, "netherite_" + type + "_smithing");
        
        ItemStack template = NetheriteArmor.craftTemplate(type);
        ItemStack addition = NetheriteArmor.craftAncestralFragment();

        SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                key,
                result,
                new RecipeChoice.ExactChoice(template),
                new RecipeChoice.MaterialChoice(baseMat),
                new RecipeChoice.ExactChoice(addition)
        );

        Bukkit.addRecipe(recipe);
    }

    public void registerD50Recipes() {
        try {
            registerINH();
            registerINC();
            registerINL();
            registerINB();
        } catch (IllegalStateException ex) {
            // Ignorar, la receta fue registrada antes probablemente
        }
    }

    public void registerD60Recipes() {
        registerBeginningRelic();
        registerLifeOrb();

        if (!Main.instance.getConfig().getBoolean("Toggles.ExtendToDay90")) return;

        ShapedRecipe heart = new ShapedRecipe(new NamespacedKey(Main.instance, "abyssal_heart"), PermadeathItems.createAbyssalHeart());
        heart.shape("ENE", "EHE", "EEE");
        heart.setIngredient('E', Material.ECHO_SHARD);
        heart.setIngredient('N', Material.NETHERITE_BLOCK);
        heart.setIngredient('H', Material.HEART_OF_THE_SEA);
        Bukkit.addRecipe(heart);

        ShapedRecipe filter = new ShapedRecipe(new NamespacedKey(Main.instance, "abyssal_filter"), PermadeathItems.createAbyssalFilter());
        filter.shape(" S ", "SES", " S ");
        filter.setIngredient('S', Material.AMETHYST_SHARD); // Representa Fragmento de Vacío en receta
        filter.setIngredient('E', Material.ECHO_SHARD);
        Bukkit.addRecipe(filter);

        ShapedRecipe mask = new ShapedRecipe(new NamespacedKey(Main.instance, "abyssal_mask"), PermadeathItems.createAbyssalMask());
        mask.shape("FSF", "VHV", "VVV");
        mask.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(PermadeathItems.createAbyssalFilter()));
        mask.setIngredient('S', new org.bukkit.inventory.RecipeChoice.ExactChoice(NetheriteArmor.craftTemplate("helmet")));
        mask.setIngredient('V', new org.bukkit.inventory.RecipeChoice.ExactChoice(PermadeathItems.createVoidShard()));
        mask.setIngredient('H', Material.NETHERITE_HELMET);
        Bukkit.addRecipe(mask);

        // Receta de Alquimia (Brewing)

    }

    private void registerBeginningRelic() {
        Bukkit.getConsoleSender().sendMessage("[Permadeath-Debug] Registrando receta de Reliquia del Comienzo...");
        ItemStack s = PermadeathItems.createBeginningRelic();

        NamespacedKey key = new NamespacedKey(instance, "beginning_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("SBS", "BDB", "SBS");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('D', Material.LIGHT_BLUE_DYE);
        recipe.setIngredient('S', Material.SHULKER_SHELL);
        instance.getServer().addRecipe(recipe);
    }

    private void registerIE() {

        ItemStack s = PermadeathItems.craftInfernalElytra();

        NamespacedKey key = new NamespacedKey(instance, "infernal_elytra");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("III", "IPI", "III");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.ELYTRA);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINH() {
        ItemStack s = InfernalNetherite.craftNetheriteHelmet();

        NamespacedKey key = new NamespacedKey(instance, "infernal_helmet");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_HELMET);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINC() {
        ItemStack s = InfernalNetherite.craftNetheriteChest();

        NamespacedKey key = new NamespacedKey(instance, "infernal_chestplate");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_CHESTPLATE);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINL() {
        ItemStack s = InfernalNetherite.craftNetheriteLegs();

        NamespacedKey key = new NamespacedKey(instance, "infernal_leggings");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_LEGGINGS);
        instance.getServer().addRecipe(recipe);
    }

    private void registerINB() {
        ItemStack s = InfernalNetherite.craftNetheriteBoots();

        NamespacedKey key = new NamespacedKey(instance, "infernal_boots");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" I ", "IPI", " I ");
        recipe.setIngredient('I', Material.DIAMOND);
        recipe.setIngredient('P', Material.LEATHER_BOOTS);
        instance.getServer().addRecipe(recipe);
    }


    private void registerEndRel() {

        ItemStack s = PermadeathItems.crearReliquia();

        ItemMeta meta = s.getItemMeta();
        meta.setUnbreakable(true);
        s.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(instance, "end_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape(" S ", " D ", " S ");
        recipe.setIngredient('S', Material.SHULKER_SHELL);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        instance.getServer().addRecipe(recipe);
    }

    private void registerShulkerUnCraft() {

        for (Material m : Material.values()) {
            if (m.name().toLowerCase().contains("shulker_box")) {
                Bukkit.addRecipe(new ShapelessRecipe(new NamespacedKey(instance, m.name() + "_uncraft"), new ItemStack(Material.SHULKER_SHELL, 2)).addIngredient(m));
            }
        }
    }

    private void registerHyperGAP() {

        ItemStack s = new ItemBuilder(Material.GOLDEN_APPLE, 1).setDisplayName(TextUtils.format("&6Hyper Golden Apple +")).addEnchant(Enchantment.INFINITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();
        String id = "hyper_golden_apple";
        NamespacedKey key = new NamespacedKey(instance, id);
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);

        try {
            instance.getServer().addRecipe(recipe);
        } catch (Exception x) {
        }
    }

    private void registerSuperGAP() {

        ItemStack s = new ItemBuilder(Material.GOLDEN_APPLE, 1).setDisplayName(TextUtils.format("&6Super Golden Apple +")).addEnchant(Enchantment.INFINITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();

        NamespacedKey key = new NamespacedKey(instance, "super_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);
        instance.getServer().addRecipe(recipe);
    }

    private void registerLifeOrb() {

        ItemStack s = PermadeathItems.createLifeOrb();

        NamespacedKey key = new NamespacedKey(instance, "PERMADEATHCORE_LIFO");
        ShapedRecipe recipe = new ShapedRecipe(key, s);
        recipe.shape("DGB", "RSE", "NOL");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.BONE_BLOCK);
        recipe.setIngredient('R', Material.BLAZE_ROD);
        recipe.setIngredient('S', Material.HEART_OF_THE_SEA);
        recipe.setIngredient('E', Material.END_STONE);
        recipe.setIngredient('N', Material.NETHER_BRICKS);
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('L', Material.LAPIS_BLOCK);
        instance.getServer().addRecipe(recipe);
    }
}

















