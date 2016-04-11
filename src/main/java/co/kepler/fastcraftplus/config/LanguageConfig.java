package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supplies access to the plugin's language files.
 */
public class LanguageConfig { // TODO Extend Config
    private static final String NOT_FOUND = ChatColor.RED + "[Lang: <key>]";
    private static final String NOT_FOUND_KEY = "key";

    private YamlConfiguration lang;
    private Map<Material, ItemNames> itemNames;

    /**
     * Create an instance of Language
     *
     * @param language The language to use.
     */
    @SuppressWarnings("deprecation")
    public LanguageConfig(String language) {
        FastCraft fastCraft = FastCraft.getInstance();

        String resPath = "lang/" + language + ".yml";
        InputStream resStream = fastCraft.getResource(resPath);
        if (resStream != null) {
            // Load from internal lang file
            lang = YamlConfiguration.loadConfiguration(resStream);
        } else {
            try {
                // Load from custom lang file
                File langFile = new File(fastCraft.getDataFolder(), resPath);
                if (!langFile.exists()) {
                    langFile.getParentFile().mkdirs();
                    FastCraft.log("Created language file: '" + langFile.getName() + "'");
                    Files.copy(fastCraft.getResource("lang/EN.yml"), langFile.toPath());
                }
                lang = YamlConfiguration.loadConfiguration(langFile);
            } catch (IOException e) {
                e.printStackTrace();
                lang = new YamlConfiguration();
            }
        }

        // Load item names
        ConfigurationSection itemSection = lang.getConfigurationSection("items");
        itemNames = new HashMap<>();
        if (itemSection != null) {
            for (String item : itemSection.getKeys(false)) {
                Material itemType = Bukkit.getUnsafe().getMaterialFromInternalName(item);
                if (itemType == null) {
                    FastCraft.err("Unknown item type: '" + item + "'");
                    continue;
                }
                ItemNames itemName;
                if (itemSection.isString(item)) {
                    // If item name was given directly
                    itemName = new ItemNames(itemSection.getString(item), null);
                } else {
                    // If item names are given based off of item data values
                    ConfigurationSection nameSection = itemSection.getConfigurationSection(item);
                    String defName = null;
                    Map<Integer, String> names = new HashMap<>();
                    for (String data : nameSection.getKeys(false)) {
                        if (data.equals("d")) {
                            // Get the default item name
                            defName = nameSection.getString(data);
                        } else {
                            // Get the item name for specific data values
                            try {
                                int num = Integer.parseInt(data);
                                names.put(num, nameSection.getString(data));
                            } catch (NumberFormatException e) {
                                FastCraft.err("Item data is not 'd' or a number: " + data);
                            }
                        }
                    }
                    itemName = new ItemNames(defName, names);
                }
                if (itemName.getDefName() == null) {
                    FastCraft.warning("Language (" + language + ") has missing default (d) for item: '" + item + "'");
                }
                itemNames.put(itemType, itemName);
            }
        }
    }

    /**
     * Useful method to convert an integer to a String.
     *
     * @param integer The ineger to convert to a String.
     * @return Returns the integer as a String.
     */
    private String s(int integer) {
        return Integer.toString(integer);
    }

    /**
     * Format a
     *
     * @param str    The String to format.
     * @param varVal The variables and values.
     * @return Returns the formatted String
     */
    private String format(String str, String... varVal) {
        assert varVal.length % 2 == 0 : "varVal must have an even number of elements";

        for (int i = 0; i < varVal.length; i += 2) {
            str = str.replace("<" + varVal[i] + ">", varVal[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private String get(String key, String... varVal) {
        String entry = lang.getString(key);
        if (entry == null) {
            return format(NOT_FOUND, NOT_FOUND_KEY, key);
        }
        return format(entry, varVal);
    }

    private List<String> getList(String key, String... varVal) {
        List<String> entry = lang.getStringList(key);
        if (entry == null) {
            return Collections.singletonList(format(NOT_FOUND, NOT_FOUND_KEY, key));
        }

        for (int i = 0; i < entry.size(); i++) {
            entry.set(i, format(entry.get(i), varVal));
        }
        return entry;
    }


    public String gui_title() {
        return get("gui.title");
    }

    public String gui_itemName(ItemStack item) {
        if (lang.getString("gui.item-name") == null) {
            return item.getItemMeta().getDisplayName();
        }
        String name = RecipeUtil.getItemName(item);
        return get("gui.item-name", "name", name);
    }

    public String gui_ingredients_item(int amount, String item) {
        return get("gui.ingredients.item", "amount", s(amount), "item", item);
    }

    public String gui_ingredients_label() {
        return get("gui.ingredients.label");
    }

    public String gui_results_label() {
        return get("gui.results.label");
    }

    public String gui_results_item(ItemStack is) {
        String itemName = RecipeUtil.getItemName(is);
        return get("gui.results.item", "amount", s(is.getAmount()), "item", itemName);
    }

    public String gui_toolbar_pagePrev_title() {
        return get("gui.toolbar.page-prev.title");
    }

    public List<String> gui_toolbar_pagePrev_description(int prev, int total, int cur) {
        return getList("gui.toolbar.page-prev.description", "prev", s(prev), "total", s(total), "cur", s(cur));
    }

    public String gui_toolbar_pageNext_title() {
        return get("gui.toolbar.page-next.title");
    }

    public List<String> gui_toolbar_pageNext_description(int prev, int total, int cur) {
        return getList("gui.toolbar.page-next.description", "next", s(prev), "total", s(total), "cur", s(cur));
    }

    public String gui_toolbar_craftItems_title() {
        return get("gui.toolbar.craft-items.title");
    }

    public List<String> gui_toolbar_craftItems_description() {
        return getList("gui.toolbar.craft-items.description");
    }

    public String gui_toolbar_craftArmor_title() {
        return get("gui.toolbar.craft-armor.title");
    }

    public List<String> gui_toolbar_craftArmor_description() {
        return getList("gui.toolbar.craft-armor.description");
    }

    public String gui_toolbar_craftFireworks_title() {
        return get("gui.toolbar.craft-fireworks.title");
    }

    public List<String> gui_toolbar_craftFireworks_description() {
        return getList("gui.toolbar.craft-fireworks.description");
    }

    public String gui_toolbar_refresh_title() {
        return get("gui.toolbar.refresh.title");
    }

    public List<String> gui_toolbar_refresh_description() {
        return getList("gui.toolbar.refresh.description");
    }

    public String gui_toolbar_multiplier_title(int mult) {
        return get("gui.toolbar.multiplier.title", "mult", s(mult));
    }

    public List<String> gui_toolbar_multiplier_description(int mult) {
        return getList("gui.toolbar.multiplier.description", "mult", s(mult));
    }

    public String gui_toolbar_workbench_title() {
        return get("gui.toolbar.workbench.title");
    }

    public List<String> gui_toolbar_workbench_description() {
        return getList("gui.toolbar.workbench.description");
    }

    @SuppressWarnings("deprecation")
    public String items_name(ItemStack item) {
        ItemNames names = itemNames.get(item.getType());
        if (names == null) return null;
        return names.getName(item.getData().getData());
    }


    /**
     * Keeps track of an item's names.
     */
    private class ItemNames {
        private final String defName;
        private final Map<Integer, String> names;

        public ItemNames(String defName, Map<Integer, String> names) {
            this.defName = defName;
            this.names = names;
        }

        public String getDefName() {
            return defName;
        }

        public String getName(int data) {
            if (names == null) return defName;
            String name = names.get(data);
            return name == null ? defName : name;
        }
    }
}