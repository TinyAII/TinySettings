package nl.tinyaii.tinysettings;

import org.bukkit.ChatColor;

public class Messages {
    private final TinySettingsPlugin plugin;

    public Messages(TinySettingsPlugin plugin) {
        this.plugin = plugin;
    }

    public String get(String key) {
        return plugin.getConfig().getString("messages." + key, "&7" + key);
    }

    public String getPrefix() {
        return get("prefix");
    }

    public String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String format(String key, String... pairs) {
        String msg = get(key);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            msg = msg.replace("{" + pairs[i] + "}", pairs[i + 1]);
        }
        return color(getPrefix() + msg);
    }
}
