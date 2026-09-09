package nl.tinyaii.tinysettings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {

    private final TinySettingsPlugin plugin;

    public SettingsCommand(TinySettingsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Messages msg = new Messages(plugin);
        SettingsManager sm = plugin.getSettingsManager();

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(msg.format("player-only"));
                return true;
            }
            if (!sender.hasPermission("tinysettings.admin")) {
                sender.sendMessage(msg.format("no-permission"));
                return true;
            }
            new SettingsGui(plugin).open((Player) sender);
            return true;
        }

        String sub = args[0];
        boolean admin = sender.hasPermission("tinysettings.admin");

        // 状态（允许只读）
        if (sub.equalsIgnoreCase("状态") || sub.equalsIgnoreCase("status")) {
            if (!sender.hasPermission("tinysettings.query")) {
                sender.sendMessage(msg.format("no-permission"));
                return true;
            }
            sender.sendMessage(msg.color(msg.getPrefix() + msg.get("status-header")));
            sender.sendMessage(msg.format("status-line", "setting", "死亡不掉落", "value", onOff(sm.getBool("keep-inventory"))));
            sender.sendMessage(msg.format("status-line", "setting", "死亡不掉经验", "value", onOff(sm.getBool("keep-exp"))));
            sender.sendMessage(msg.format("status-line", "setting", "死亡保留装备", "value", onOff(sm.getBool("keep-armor"))));
            sender.sendMessage(msg.format("status-line", "setting", "PVP", "value", onOff(sm.getBool("pvp"))));
            sender.sendMessage(msg.format("status-line", "setting", "怪物攻击", "value", onOff(sm.getBool("mob-attack"))));
            sender.sendMessage(msg.format("status-line", "setting", "摔落伤害", "value", onOff(sm.getBool("fall-damage"))));
            sender.sendMessage(msg.format("status-line", "setting", "火焰伤害", "value", onOff(sm.getBool("fire-damage"))));
            sender.sendMessage(msg.format("status-line", "setting", "TNT破坏", "value", onOff(sm.getBool("tnt"))));
            sender.sendMessage(msg.format("status-line", "setting", "苦力怕破坏", "value", onOff(sm.getBool("creeper"))));
            sender.sendMessage(msg.format("status-line", "setting", "火焰蔓延", "value", onOff(sm.getBool("fire-spread"))));
            sender.sendMessage(msg.format("status-line", "setting", "末影人搬方块", "value", onOff(sm.getBool("enderman"))));
            sender.sendMessage(msg.format("status-line", "setting", "难度", "value", Cn.difficulty(sm.getString("difficulty"))));
            sender.sendMessage(msg.format("status-line", "setting", "怪物生成", "value", onOff(sm.getBool("mob-spawning"))));
            sender.sendMessage(msg.format("status-line", "setting", "动物生成", "value", onOff(sm.getBool("animal-spawning"))));
            sender.sendMessage(msg.format("status-line", "setting", "时间", "value", Cn.time(sm.getString("time"))));
            sender.sendMessage(msg.format("status-line", "setting", "天气", "value", Cn.weather(sm.getString("weather"))));
            sender.sendMessage(msg.format("status-line", "setting", "天气锁定", "value", onOff(sm.getBool("weather-lock"))));
            sender.sendMessage(msg.format("status-line", "setting", "出生点保护", "value", onOff(sm.getBool("spawn-protection"))));
            return true;
        }

        if (!admin) {
            sender.sendMessage(msg.format("no-permission"));
            return true;
        }

        // 难度
        if (sub.equalsIgnoreCase("难度") || sub.equalsIgnoreCase("difficulty")) {
            if (args.length < 2) { sender.sendMessage(msg.format("invalid-arg", "arg", "和平/简单/普通/困难")); return true; }
            String d = mapDifficulty(args[1]);
            if (d == null) { sender.sendMessage(msg.format("invalid-arg", "arg", args[1])); return true; }
            sm.setString("difficulty", d);
            sender.sendMessage(msg.format("changed", "setting", "难度", "value", Cn.difficulty(d)));
            return true;
        }
        // 死亡不掉落
        if (sub.equalsIgnoreCase("不掉落") || sub.equalsIgnoreCase("keepinventory")) {
            sm.setBool("keep-inventory", parseOnOff(args.length > 1 ? args[1] : "", true));
            sender.sendMessage(msg.format("changed", "setting", "死亡不掉落", "value", onOff(sm.getBool("keep-inventory"))));
            return true;
        }
        // PVP
        if (sub.equalsIgnoreCase("pvp")) {
            sm.setBool("pvp", parseOnOff(args.length > 1 ? args[1] : "", true));
            sender.sendMessage(msg.format("changed", "setting", "PVP", "value", onOff(sm.getBool("pvp"))));
            return true;
        }
        // 防爆（TNT+苦力怕）
        if (sub.equalsIgnoreCase("防爆") || sub.equalsIgnoreCase("explosion")) {
            boolean v = parseOnOff(args.length > 1 ? args[1] : "", true);
            sm.setBool("tnt", v);
            sm.setBool("creeper", v);
            sender.sendMessage(msg.format("changed", "setting", "防爆(TNT/苦力怕)", "value", onOff(v)));
            return true;
        }
        // 天气
        if (sub.equalsIgnoreCase("天气") || sub.equalsIgnoreCase("weather")) {
            if (args.length < 2) { sender.sendMessage(msg.format("invalid-arg", "arg", "晴/雨/雷/正常")); return true; }
            String w = mapWeather(args[1]);
            if (w == null) { sender.sendMessage(msg.format("invalid-arg", "arg", args[1])); return true; }
            sm.setString("weather", w);
            sender.sendMessage(msg.format("changed", "setting", "天气", "value", Cn.weather(w)));
            return true;
        }
        // 时间
        if (sub.equalsIgnoreCase("时间") || sub.equalsIgnoreCase("time")) {
            if (args.length < 2) { sender.sendMessage(msg.format("invalid-arg", "arg", "永昼/永夜/正常")); return true; }
            String t = mapTime(args[1]);
            if (t == null) { sender.sendMessage(msg.format("invalid-arg", "arg", args[1])); return true; }
            sm.setString("time", t);
            sender.sendMessage(msg.format("changed", "setting", "时间", "value", Cn.time(t)));
            return true;
        }
        // 重置
        if (sub.equalsIgnoreCase("重置") || sub.equalsIgnoreCase("reset")) {
            sm.reset();
            sender.sendMessage(msg.format("reset"));
            return true;
        }
        // 重载
        if (sub.equalsIgnoreCase("重载") || sub.equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sm.load();
            sender.sendMessage(msg.format("reloaded"));
            return true;
        }

        sender.sendMessage(msg.color(msg.getPrefix() + "&7/设置 [状态|难度|不掉落|防爆|pvp|天气|时间|重置|重载]"));
        return true;
    }

    private String onOff(boolean b) {
        Messages msg = new Messages(plugin);
        return b ? msg.color(msg.get("on")) : msg.color(msg.get("off"));
    }

    private boolean parseOnOff(String s, boolean def) {
        if (s.equalsIgnoreCase("开") || s.equalsIgnoreCase("on") || s.equalsIgnoreCase("true")) return true;
        if (s.equalsIgnoreCase("关") || s.equalsIgnoreCase("off") || s.equalsIgnoreCase("false")) return false;
        return def;
    }

    private String mapDifficulty(String s) {
        if (s.contains("和") || s.contains("平")) return "PEACEFUL";
        if (s.contains("简")) return "EASY";
        if (s.contains("普")) return "NORMAL";
        if (s.contains("困") || s.contains("难")) return "HARD";
        return null;
    }

    private String mapWeather(String s) {
        if (s.contains("晴") || s.contains("clear")) return "CLEAR";
        if (s.contains("雷") || s.contains("thunder")) return "THUNDER";
        if (s.contains("雨") || s.contains("rain")) return "RAIN";
        if (s.contains("正") || s.contains("normal")) return "NORMAL";
        return null;
    }

    private String mapTime(String s) {
        if (s.contains("昼") || s.contains("日") || s.contains("day")) return "DAY";
        if (s.contains("夜") || s.contains("night")) return "NIGHT";
        if (s.contains("正") || s.contains("normal")) return "NORMAL";
        return null;
    }
}