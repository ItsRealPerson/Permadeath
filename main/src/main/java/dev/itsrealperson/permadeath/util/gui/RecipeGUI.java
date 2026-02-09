package dev.itsrealperson.permadeath.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;
import dev.itsrealperson.permadeath.util.item.PermadeathItems;
import dev.itsrealperson.permadeath.util.item.NetheriteArmor;
import dev.itsrealperson.permadeath.util.lib.ItemBuilder;

import java.util.Arrays;

public class RecipeGUI {

    public static final String GUI_NAME = TextUtils.format("&8Recetas de Permadeath");
    public static final NamespacedKey PAGE_KEY = new NamespacedKey(Main.instance, "gui_page");
    public static final NamespacedKey CAT_KEY = new NamespacedKey(Main.instance, "gui_category");

    public static void openMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_NAME);
        ItemStack gray = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        for (int i = 0; i < 27; i++) inv.setItem(i, gray);

        boolean extended = Main.instance.isExtendedDifficulty();

        if (extended) {
            inv.setItem(10, createCategoryIcon(Material.CAULDRON, "&b&lALQUIMIA ABISAL", "alchemy"));
        } else {
            inv.setItem(10, new ItemBuilder(Material.BARRIER).setDisplayName("&c&l??? (BLOQUEADO)")
                    .setLore(Arrays.asList("&7Esta categoría se desbloquea", "&7al despertar el Corazón del Abismo.")).build());
        }

        inv.setItem(11, createCategoryIcon(Material.SMITHING_TABLE, "&6&lMESA DE HERRERÍA", "smithing"));
        inv.setItem(13, createCategoryIcon(Material.NETHERITE_CHESTPLATE, "&6&lARMADURAS ESPECIALES", "armor"));
        inv.setItem(15, createCategoryIcon(Material.LIGHT_BLUE_DYE, "&e&lRELIQUIAS", "relics"));
        inv.setItem(16, createCategoryIcon(Material.GOLDEN_APPLE, "&d&lCONSUMIBLES", "special"));

        p.openInventory(inv);
    }

    private static ItemStack createCategoryIcon(Material mat, String name, String cat) {
        ItemStack item = new ItemBuilder(mat).setDisplayName(name).setLore(Arrays.asList("&eClick para ver recetas")).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(CAT_KEY, PersistentDataType.STRING, cat);
        item.setItemMeta(meta);
        return item;
    }

    public static void openCategory(Player p, String category, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, GUI_NAME);
        ItemStack gray = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        for (int i = 0; i < 54; i++) inv.setItem(i, gray);

        loadCategoryRecipe(inv, category, page);

        inv.setItem(49, new ItemBuilder(Material.BOOK).setDisplayName("&eVolver al Menú").build());
        p.openInventory(inv);
    }

    private static void loadCategoryRecipe(Inventory inv, String category, int page) {
        for (int i : new int[]{10,11,12,19,20,21,28,29,30,23,24,25,4}) inv.setItem(i, new ItemStack(Material.AIR));
        
        ItemStack arrow = new ItemBuilder(Material.IRON_BARS).setDisplayName("&7➤").build();
        inv.setItem(23, arrow);
        inv.setItem(24, arrow);

        boolean extended = Main.instance.isExtendedDifficulty();

        if (category.equals("alchemy")) {
            if (extended) setupAbyssalPotion(inv);
        }
        else if (category.equals("smithing")) {
            setupSmithingLayout(inv, page);
            setupPagination(inv, category, page, 3);
        } else if (category.equals("relics")) {
            if (page == 0) setupEndRelic(inv);
            else if (page == 1) setupBeginningRelic(inv);
            else if (page == 2) setupWaterMedal(inv);
            else if (page == 3) setupLifeOrb(inv);
            else {
                boolean canSeeHeart = Main.instance.getDay() >= 60 && Main.instance.getConfig().getBoolean("Toggles.ExtendToDay90");
                if (canSeeHeart) setupAbyssalHeart(inv);
                else setupLockedRecipe(inv, "Corazón del Abismo");
            }
            setupPagination(inv, category, page, 4);
        } else if (category.equals("armor")) {
            if (page == 0) setupAncestralFragment(inv);
            else {
                if (extended) setupAbyssalMask(inv);
                else setupLockedRecipe(inv, "Máscara del Abismo");
            }
            setupPagination(inv, category, page, 1);
        } else if (category.equals("special")) {
            if (page == 0) setupSuperGAP(inv);
            else if (page == 1) setupHyperGAP(inv);
            else {
                if (Main.instance.getDay() >= 60 || extended) setupAbyssalFilter(inv);
                else setupLockedRecipe(inv, "Filtro Abisal");
            }
            setupPagination(inv, category, page, 2);
        }
    }

    private static void setupLockedRecipe(Inventory inv, String name) {
        inv.setItem(4, new ItemBuilder(Material.BARRIER).setDisplayName("&c&lRECETA BLOQUEADA: " + name).setLore(Arrays.asList("&7Esta receta requiere que el", "&7Corazón del Abismo sea despertado.")).build());
        inv.setItem(25, new ItemBuilder(Material.BARRIER).setDisplayName("&cBLOQUEADO").build());
    }

    private static void setupSmithingLayout(Inventory inv, int page) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Mejora de Netherite").setLore(Arrays.asList("&7Usa la Mesa de Herrería")).build());
        
        String type = "helmet";
        Material base = Material.NETHERITE_HELMET;
        ItemStack result = NetheriteArmor.craftNetheriteHelmet();
        
        switch(page) {
            case 1 -> { type = "chestplate"; base = Material.NETHERITE_CHESTPLATE; result = NetheriteArmor.craftNetheriteChest(); }
            case 2 -> { type = "leggings"; base = Material.NETHERITE_LEGGINGS; result = NetheriteArmor.craftNetheriteLegs(); }
            case 3 -> { type = "boots"; base = Material.NETHERITE_BOOTS; result = NetheriteArmor.craftNetheriteBoots(); }
        }

        inv.setItem(19, NetheriteArmor.craftTemplate(type));
        inv.setItem(20, new ItemStack(base));
        inv.setItem(21, NetheriteArmor.craftAncestralFragment());
        inv.setItem(25, result);
    }

    private static void setupPagination(Inventory inv, String cat, int page, int max) {
        if (page > 0) inv.setItem(45, createPageButton("&a« Anterior", cat, page - 1));
        if (page < max) inv.setItem(53, createPageButton("&aSiguiente »", cat, page + 1));
    }

    private static ItemStack createPageButton(String name, String cat, int page) {
        ItemStack item = new ItemBuilder(Material.ARROW).setDisplayName(name).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(CAT_KEY, PersistentDataType.STRING, cat);
        meta.getPersistentDataContainer().set(PAGE_KEY, PersistentDataType.INTEGER, page);
        item.setItemMeta(meta);
        return item;
    }

    private static void setupAbyssalPotion(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&b&lRECETA: Poción Abisal").setLore(Arrays.asList("&7Método: &dAlquimia en Caldero")).build());
        inv.setItem(11, new ItemStack(Material.WATER_BUCKET));
        inv.setItem(19, new ItemStack(Material.NETHER_WART));
        inv.setItem(20, new ItemStack(Material.CAULDRON));
        inv.setItem(21, new ItemStack(Material.ECHO_SHARD));
        inv.setItem(23, new ItemBuilder(Material.CAMPFIRE).setDisplayName("&eCalor debajo").build());
        inv.setItem(24, new ItemBuilder(Material.IRON_BARS).setDisplayName("&7➤").build());
        inv.setItem(25, PermadeathItems.createAbyssalPotion());
    }

    private static void setupEndRelic(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Reliquia del Fin").build());
        inv.setItem(11, new ItemStack(Material.SHULKER_SHELL));
        inv.setItem(20, new ItemStack(Material.DIAMOND_BLOCK));
        inv.setItem(29, new ItemStack(Material.SHULKER_SHELL));
        inv.setItem(25, PermadeathItems.crearReliquia());
    }

    private static void setupBeginningRelic(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Reliquia del Comienzo").build());
        ItemStack b = new ItemStack(Material.DIAMOND_BLOCK, 32);
        ItemStack s = new ItemStack(Material.SHULKER_SHELL);
        ItemStack d = new ItemStack(Material.LIGHT_BLUE_DYE);
        inv.setItem(10, s); inv.setItem(11, b); inv.setItem(12, s);
        inv.setItem(19, b); inv.setItem(20, d); inv.setItem(21, b);
        inv.setItem(28, s); inv.setItem(29, b); inv.setItem(30, s);
        inv.setItem(25, PermadeathItems.createBeginningRelic());
    }

    private static void setupLifeOrb(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Orbe de Vida").build());
        
        ItemStack d = new ItemStack(Material.DIAMOND, 64);
        ItemStack g = new ItemStack(Material.GOLD_INGOT, 64);
        ItemStack b = new ItemStack(Material.BONE_BLOCK, 64);
        ItemStack r = new ItemStack(Material.BLAZE_ROD, 64);
        ItemStack s = new ItemStack(Material.HEART_OF_THE_SEA, 1);
        ItemStack e = new ItemStack(Material.END_STONE, 64);
        ItemStack n = new ItemStack(Material.NETHER_BRICKS, 64);
        ItemStack o = new ItemStack(Material.OBSIDIAN, 64);
        ItemStack l = new ItemStack(Material.LAPIS_BLOCK, 64);

        inv.setItem(10, d); inv.setItem(11, g); inv.setItem(12, b);
        inv.setItem(19, r); inv.setItem(20, s); inv.setItem(21, e);
        inv.setItem(28, n); inv.setItem(29, o); inv.setItem(30, l);
        
        inv.setItem(25, PermadeathItems.createLifeOrb());
    }

    private static void setupWaterMedal(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&b&lRECETA: Medalla de Agua").build());
        ItemStack t = new ItemStack(Material.TRIDENT);
        ItemStack g = new ItemStack(Material.GOLD_BLOCK);
        g.setAmount(32);
        ItemStack n = new ItemStack(Material.HEART_OF_THE_SEA);
        inv.setItem(10, t); inv.setItem(11, g); inv.setItem(12, t);
        inv.setItem(19, g); inv.setItem(20, n); inv.setItem(21, g);
        inv.setItem(28, g); inv.setItem(29, g); inv.setItem(30, g);
        inv.setItem(25, PermadeathItems.createWaterMedal());
    }

    private static void setupAbyssalHeart(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&3&lRECETA: Corazón del Abismo").build());
        ItemStack e = new ItemStack(Material.ECHO_SHARD);
        ItemStack n = new ItemStack(Material.NETHERITE_BLOCK);
        ItemStack h = new ItemStack(Material.HEART_OF_THE_SEA);
        inv.setItem(10, e); inv.setItem(11, n); inv.setItem(12, e);
        inv.setItem(19, e); inv.setItem(20, h); inv.setItem(21, e);
        inv.setItem(28, e); inv.setItem(29, e); inv.setItem(30, e);
        inv.setItem(25, PermadeathItems.createAbyssalHeart());
    }

    private static void setupAncestralFragment(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Fragmento Ancestral").build());
        ItemStack d = new ItemStack(Material.DIAMOND_BLOCK);
        ItemStack n = new ItemStack(Material.NETHERITE_INGOT);
        for(int i : new int[]{10,11,12,19,21,28,29,30}) inv.setItem(i, d);
        inv.setItem(20, n);
        inv.setItem(25, NetheriteArmor.craftAncestralFragment());
    }

    private static void setupAbyssalMask(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&b&lRECETA: Máscara del Abismo").build());
        inv.setItem(10, PermadeathItems.createAbyssalFilter());
        inv.setItem(11, NetheriteArmor.craftTemplate("helmet"));
        inv.setItem(12, PermadeathItems.createAbyssalFilter());
        inv.setItem(19, PermadeathItems.createVoidShard());
        inv.setItem(20, new ItemStack(Material.NETHERITE_HELMET));
        inv.setItem(21, PermadeathItems.createVoidShard());
        inv.setItem(28, PermadeathItems.createVoidShard());
        inv.setItem(29, PermadeathItems.createVoidShard());
        inv.setItem(30, PermadeathItems.createVoidShard());
        inv.setItem(25, PermadeathItems.createAbyssalMask());
    }

    private static void setupSuperGAP(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Super Golden Apple +").build());
        ItemStack g = new ItemStack(Material.GOLD_INGOT);
        ItemStack a = new ItemStack(Material.GOLDEN_APPLE);
        for(int i : new int[]{10,11,12,19,21,28,29,30}) inv.setItem(i, g);
        inv.setItem(20, a);
        inv.setItem(25, new ItemBuilder(Material.GOLDEN_APPLE).setDisplayName("&6Super Golden Apple +").build());
    }

    private static void setupHyperGAP(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&6&lRECETA: Hyper Golden Apple +").build());
        ItemStack g = new ItemStack(Material.GOLD_BLOCK);
        ItemStack a = new ItemStack(Material.GOLDEN_APPLE);
        for(int i : new int[]{10,11,12,19,21,28,29,30}) inv.setItem(i, g);
        inv.setItem(20, a);
        inv.setItem(25, new ItemBuilder(Material.GOLDEN_APPLE).setDisplayName("&6Hyper Golden Apple +").build());
    }

    private static void setupAbyssalFilter(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.BOOK).setDisplayName("&b&lRECETA: Filtro Abisal").build());
        ItemStack shard = PermadeathItems.createVoidShard();
        ItemStack echo = new ItemStack(Material.ECHO_SHARD);
        
        inv.setItem(11, shard);
        inv.setItem(19, shard);
        inv.setItem(20, echo);
        inv.setItem(21, shard);
        inv.setItem(29, shard);
        
        inv.setItem(25, PermadeathItems.createAbyssalFilter());
    }
}
