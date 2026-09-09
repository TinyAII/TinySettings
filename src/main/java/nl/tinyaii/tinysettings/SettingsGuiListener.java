package nl.tinyaii.tinysettings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SettingsGuiListener implements Listener {
    private final TinySettingsPlugin plugin;

    public SettingsGuiListener(TinySettingsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();
        if (!isSettingsGui(title)) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String display = meta.getDisplayName();
        SettingsGui gui = new SettingsGui(plugin);
        SettingsManager sm = plugin.getSettingsManager();

        if (SettingsGui.T_MAIN.equals(title)) {
            if (display.contains("玩法")) gui.openGameplay(p);
            else if (display.contains("战斗")) gui.openCombat(p);
            else if (display.contains("防护")) gui.openProtect(p);
            else if (display.contains("世界")) gui.openWorld(p);
            else if (display.contains("状态")) gui.openStatus(p);
            return;
        }
        if (display.contains("返回")) {
            gui.open(p);
            return;
        }

        String key = readKey(meta.getLore());
        if (key == null) return;

        // 多选项入口：进入选择页面
        if (key.equals("open-difficulty")) { gui.openDifficulty(p); return; }
        if (key.equals("open-time")) { gui.openTime(p); return; }
        if (key.equals("open-weather")) { gui.openWeather(p); return; }

        // 选择页：sel-xxx:VALUE（选完返回世界环境页）
        if (key.startsWith("sel-difficulty:")) {
            sm.setString("difficulty", key.substring(15));
            p.sendMessage(color("&a已设置难度: &e" + Cn.difficulty(sm.getString("difficulty"))));
            gui.openWorld(p);
            return;
        }
        if (key.startsWith("sel-time:")) {
            sm.setString("time", key.substring(9));
            p.sendMessage(color("&a已设置时间: &e" + Cn.time(sm.getString("time"))));
            gui.openWorld(p);
            return;
        }
        if (key.startsWith("sel-weather:")) {
            sm.setString("weather", key.substring(12));
            p.sendMessage(color("&a已设置天气: &e" + Cn.weather(sm.getString("weather"))));
            gui.openWorld(p);
            return;
        }

        // 仅布尔开关直接切换
        if (isBooleanKey(key)) {
            sm.setBool(key, !sm.getBool(key));
            p.sendMessage(color("&a设置已更新: &e" + key + " &7-> " + (sm.getBool(key) ? "&a开" : "&c关")));
            if (title.equals(SettingsGui.T_GAMEPLAY)) gui.openGameplay(p);
            else if (title.equals(SettingsGui.T_COMBAT)) gui.openCombat(p);
            else if (title.equals(SettingsGui.T_PROTECT)) gui.openProtect(p);
            else if (title.equals(SettingsGui.T_WORLD)) gui.openWorld(p);
        }
    }

    private boolean isSettingsGui(String title) {
        return title.equals(SettingsGui.T_MAIN) || title.equals(SettingsGui.T_GAMEPLAY)
                || title.equals(SettingsGui.T_COMBAT) || title.equals(SettingsGui.T_PROTECT)
                || title.equals(SettingsGui.T_WORLD) || title.equals(SettingsGui.T_DIFF)
                || title.equals(SettingsGui.T_TIME) || title.equals(SettingsGui.T_WEATHER);
    }

    private String readKey(List<String> lore) {
        if (lore == null) return null;
        for (String line : lore) {
            if (line.startsWith("§7键: §e")) return line.substring("§7键: §e".length());
        }
        return null;
    }

    private boolean isBooleanKey(String key) {
        return key.equals("keep-inventory") || key.equals("keep-exp") || key.equals("keep-armor")
                || key.equals("death-protect") || key.equals("pvp") || key.equals("mob-attack")
                || key.equals("fall-damage") || key.equals("fire-damage") || key.equals("tnt")
                || key.equals("creeper") || key.equals("fire-spread") || key.equals("enderman")
                || key.equals("mob-spawning") || key.equals("animal-spawning")
                || key.equals("weather-lock") || key.equals("spawn-protection");
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}