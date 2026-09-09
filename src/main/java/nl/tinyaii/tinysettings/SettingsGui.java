package nl.tinyaii.tinysettings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设置 GUI：
 *  - 主界面：分类按钮（玩法/战斗/防护/世界/状态）
 *  - 布尔开关（2 选）：点击直接切换
 *  - 多选项（难度4/时间3/天气4）：点击进入选择页单选
 */
public class SettingsGui {

    private final TinySettingsPlugin plugin;

    public static final int SLOT_GAMEPLAY = 10;
    public static final int SLOT_COMBAT   = 12;
    public static final int SLOT_PROTECT  = 14;
    public static final int SLOT_WORLD    = 16;
    public static final int SLOT_STATUS   = 22;

    // 界面标题（监听器按标题识别）
    public static final String T_MAIN     = "一键设置";
    public static final String T_GAMEPLAY = "玩法设置";
    public static final String T_COMBAT   = "战斗设置";
    public static final String T_PROTECT  = "破坏防护";
    public static final String T_WORLD    = "世界环境";
    public static final String T_DIFF     = "选择难度";
    public static final String T_TIME     = "选择时间";
    public static final String T_WEATHER  = "选择天气";

    public SettingsGui(TinySettingsPlugin plugin) {
        this.plugin = plugin;
    }

    /** 主界面 */
    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, T_MAIN);
        for (int i = 0; i < 27; i++) {
            if (i != SLOT_GAMEPLAY && i != SLOT_COMBAT && i != SLOT_PROTECT
                    && i != SLOT_WORLD && i != SLOT_STATUS) {
                inv.setItem(i, gray());
            }
        }
        SettingsManager sm = plugin.getSettingsManager();

        inv.setItem(SLOT_GAMEPLAY, item(Material.GREEN_WOOL, "&a[玩法] 设置",
                "&7死亡不掉落: " + onOff(sm.getBool("keep-inventory")),
                "&7死亡不掉经验: " + onOff(sm.getBool("keep-exp")),
                "&7死亡保留装备: " + onOff(sm.getBool("keep-armor")),
                "&7掉落保护: " + onOff(sm.getBool("death-protect")),
                "",
                "&e点击进入"));

        inv.setItem(SLOT_COMBAT, item(Material.RED_WOOL, "&c[战斗] 设置",
                "&7PVP: " + onOff(sm.getBool("pvp")),
                "&7怪物攻击: " + onOff(sm.getBool("mob-attack")),
                "&7摔落伤害: " + onOff(sm.getBool("fall-damage")),
                "&7火焰伤害: " + onOff(sm.getBool("fire-damage")),
                "",
                "&e点击进入"));

        inv.setItem(SLOT_PROTECT, item(Material.IRON_BLOCK, "&f[防护] 破坏防护",
                "&7TNT破坏: " + onOff(sm.getBool("tnt")),
                "&7苦力怕破坏: " + onOff(sm.getBool("creeper")),
                "&7火焰蔓延: " + onOff(sm.getBool("fire-spread")),
                "&7末影人搬方块: " + onOff(sm.getBool("enderman")),
                "",
                "&e点击进入"));

        inv.setItem(SLOT_WORLD, item(Material.GRASS_BLOCK, "&a[世界] 环境",
                "&7难度: &e" + Cn.difficulty(sm.getString("difficulty")),
                "&7时间: &e" + Cn.time(sm.getString("time")),
                "&7天气: &e" + Cn.weather(sm.getString("weather")),
                "&7天气锁定: " + onOff(sm.getBool("weather-lock")),
                "&7出生点保护: " + onOff(sm.getBool("spawn-protection")),
                "",
                "&e点击进入"));

        inv.setItem(SLOT_STATUS, item(Material.BOOK, "&b[状态] 当前设置",
                "&7查看全部设置状态",
                "",
                "&e点击查看"));

        p.openInventory(inv);
    }

    /** 玩法：全是布尔开关，点击直接切换 */
    public void openGameplay(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, T_GAMEPLAY);
        SettingsManager sm = plugin.getSettingsManager();
        inv.setItem(0, toggle(Material.LIME_DYE, Material.GRAY_DYE, "死亡不掉落", "keep-inventory", sm.getBool("keep-inventory")));
        inv.setItem(1, toggle(Material.LIME_DYE, Material.GRAY_DYE, "死亡不掉经验", "keep-exp", sm.getBool("keep-exp")));
        inv.setItem(2, toggle(Material.LIME_DYE, Material.GRAY_DYE, "死亡保留装备", "keep-armor", sm.getBool("keep-armor")));
        inv.setItem(3, toggle(Material.LIME_DYE, Material.GRAY_DYE, "掉落保护", "death-protect", sm.getBool("death-protect")));
        inv.setItem(8, back());
        p.openInventory(inv);
    }

    /** 战斗：布尔开关 */
    public void openCombat(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, T_COMBAT);
        SettingsManager sm = plugin.getSettingsManager();
        inv.setItem(0, toggle(Material.LIME_DYE, Material.GRAY_DYE, "PVP", "pvp", sm.getBool("pvp")));
        inv.setItem(1, toggle(Material.LIME_DYE, Material.GRAY_DYE, "怪物攻击", "mob-attack", sm.getBool("mob-attack")));
        inv.setItem(2, toggle(Material.LIME_DYE, Material.GRAY_DYE, "摔落伤害", "fall-damage", sm.getBool("fall-damage")));
        inv.setItem(3, toggle(Material.LIME_DYE, Material.GRAY_DYE, "火焰伤害", "fire-damage", sm.getBool("fire-damage")));
        inv.setItem(8, back());
        p.openInventory(inv);
    }

    /** 防护：布尔开关 */
    public void openProtect(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, T_PROTECT);
        SettingsManager sm = plugin.getSettingsManager();
        inv.setItem(0, toggle(Material.LIME_DYE, Material.GRAY_DYE, "TNT 破坏", "tnt", sm.getBool("tnt")));
        inv.setItem(1, toggle(Material.LIME_DYE, Material.GRAY_DYE, "苦力怕破坏", "creeper", sm.getBool("creeper")));
        inv.setItem(2, toggle(Material.LIME_DYE, Material.GRAY_DYE, "火焰蔓延", "fire-spread", sm.getBool("fire-spread")));
        inv.setItem(3, toggle(Material.LIME_DYE, Material.GRAY_DYE, "末影人搬方块", "enderman", sm.getBool("enderman")));
        inv.setItem(8, back());
        p.openInventory(inv);
    }

    /** 世界：布尔开关直接切；难度/时间/天气点击进选择页 */
    public void openWorld(Player p) {
        Inventory inv = Bukkit.createInventory(null, 18, T_WORLD);
        SettingsManager sm = plugin.getSettingsManager();
        inv.setItem(0, toggle(Material.LIME_DYE, Material.GRAY_DYE, "怪物生成", "mob-spawning", sm.getBool("mob-spawning")));
        inv.setItem(1, toggle(Material.LIME_DYE, Material.GRAY_DYE, "动物生成", "animal-spawning", sm.getBool("animal-spawning")));
        inv.setItem(2, toggle(Material.LIME_DYE, Material.GRAY_DYE, "天气锁定", "weather-lock", sm.getBool("weather-lock")));
        inv.setItem(3, toggle(Material.LIME_DYE, Material.GRAY_DYE, "出生点保护", "spawn-protection", sm.getBool("spawn-protection")));
        // 多选项：点击进入选择页
        inv.setItem(4, option(Material.CLOCK, "&e时间: " + Cn.time(sm.getString("time")), "open-time"));
        inv.setItem(5, option(Material.WATER_BUCKET, "&e天气: " + Cn.weather(sm.getString("weather")), "open-weather"));
        inv.setItem(6, option(Material.MOOSHROOM_SPAWN_EGG, "&e难度: " + Cn.difficulty(sm.getString("difficulty")), "open-difficulty"));
        inv.setItem(17, back());
        p.openInventory(inv);
    }

    /** 难度选择页（4 选 1） */
    public void openDifficulty(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, T_DIFF);
        SettingsManager sm = plugin.getSettingsManager();
        String cur = sm.getString("difficulty");
        inv.setItem(0, choice(Material.SHEEP_SPAWN_EGG, "和平", "sel-difficulty:PEACEFUL", cur.equals("PEACEFUL")));
        inv.setItem(1, choice(Material.COW_SPAWN_EGG, "简单", "sel-difficulty:EASY", cur.equals("EASY")));
        inv.setItem(2, choice(Material.ZOMBIE_SPAWN_EGG, "普通", "sel-difficulty:NORMAL", cur.equals("NORMAL")));
        inv.setItem(3, choice(Material.SKELETON_SPAWN_EGG, "困难", "sel-difficulty:HARD", cur.equals("HARD")));
        inv.setItem(8, back());
        p.openInventory(inv);
    }

    /** 时间选择页（3 选 1） */
    public void openTime(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, T_TIME);
        SettingsManager sm = plugin.getSettingsManager();
        String cur = sm.getString("time");
        inv.setItem(0, choice(Material.YELLOW_DYE, "永昼", "sel-time:DAY", cur.equals("DAY")));
        inv.setItem(1, choice(Material.BLACK_DYE, "永夜", "sel-time:NIGHT", cur.equals("NIGHT")));
        inv.setItem(2, choice(Material.CLOCK, "正常", "sel-time:NORMAL", cur.equals("NORMAL")));
        inv.setItem(8, back());
        p.openInventory(inv);
    }

    /** 天气选择页（4 选 1） */
    public void openWeather(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, T_WEATHER);
        SettingsManager sm = plugin.getSettingsManager();
        String cur = sm.getString("weather");
        inv.setItem(0, choice(Material.SUNFLOWER, "晴天", "sel-weather:CLEAR", cur.equals("CLEAR")));
        inv.setItem(1, choice(Material.WATER_BUCKET, "下雨", "sel-weather:RAIN", cur.equals("RAIN")));
        inv.setItem(2, choice(Material.LIGHTNING_ROD, "雷雨", "sel-weather:THUNDER", cur.equals("THUNDER")));
        inv.setItem(3, choice(Material.CLOCK, "正常", "sel-weather:NORMAL", cur.equals("NORMAL")));
        inv.setItem(8, back());
        p.openInventory(inv);
    }

    /** 显示状态 */
    public void openStatus(Player p) {
        SettingsCommand sc = new SettingsCommand(plugin);
        sc.onCommand(p, null, "settings", new String[]{"状态"});
    }

    // ============ 工具 ============
    private ItemStack item(Material mat, String name, String... loreLines) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) lore.add(line.replace("&", "§"));
        meta.setLore(lore);
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack toggle(Material on, Material off, String name, String key, boolean value) {
        Material mat = value ? on : off;
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(new Messages(plugin).color((value ? "&a" : "&c") + name + " &7[" + (value ? "开" : "关") + "]"));
        meta.setLore(Arrays.asList(
                "&7点击切换".replace("&", "§"),
                "§7键: §e" + key
        ));
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack option(Material mat, String name, String key) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        meta.setLore(Arrays.asList("§7点击选择", "§7键: §e" + key));
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack choice(Material mat, String name, String key, boolean selected) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(new Messages(plugin).color((selected ? "&a✔ " : "&7") + name + (selected ? " &7[当前]" : "")));
        meta.setLore(Arrays.asList("§7点击选择", "§7键: §e" + key));
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack back() {
        ItemStack it = new ItemStack(Material.ARROW);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§7返回");
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack gray() {
        ItemStack it = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(" ");
        it.setItemMeta(meta);
        return it;
    }

    private String onOff(boolean b) {
        return b ? "§a开" : "§c关";
    }
}