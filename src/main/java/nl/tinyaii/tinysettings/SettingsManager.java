package nl.tinyaii.tinysettings;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.GameRule;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 设置状态管理器：读取 config 默认值 + settings.yml 运行时覆盖，
 * 提供读写 + 应用到世界的逻辑。
 */
public class SettingsManager {

    private final TinySettingsPlugin plugin;
    private final Map<String, Object> state = new LinkedHashMap<>();
    private File overrideFile;
    private YamlConfiguration override;

    public SettingsManager(TinySettingsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        // 从 config.yml 读默认值
        state.put("keep-inventory", plugin.getConfig().getBoolean("gameplay.keep-inventory", true));
        state.put("keep-exp", plugin.getConfig().getBoolean("gameplay.keep-exp", false));
        state.put("keep-armor", plugin.getConfig().getBoolean("gameplay.keep-armor", false));
        state.put("death-protect", plugin.getConfig().getBoolean("gameplay.death-protect", false));
        state.put("death-protect-seconds", plugin.getConfig().getInt("gameplay.death-protect-seconds", 300));
        state.put("pvp", plugin.getConfig().getBoolean("combat.pvp", true));
        state.put("mob-attack", plugin.getConfig().getBoolean("combat.mob-attack", true));
        state.put("fall-damage", plugin.getConfig().getBoolean("combat.fall-damage", true));
        state.put("fire-damage", plugin.getConfig().getBoolean("combat.fire-damage", true));
        state.put("tnt", plugin.getConfig().getBoolean("protection.tnt", false));
        state.put("creeper", plugin.getConfig().getBoolean("protection.creeper", false));
        state.put("fire-spread", plugin.getConfig().getBoolean("protection.fire-spread", false));
        state.put("enderman", plugin.getConfig().getBoolean("protection.enderman", false));
        state.put("difficulty", plugin.getConfig().getString("world.difficulty", "NORMAL"));
        state.put("mob-spawning", plugin.getConfig().getBoolean("world.mob-spawning", true));
        state.put("animal-spawning", plugin.getConfig().getBoolean("world.animal-spawning", true));
        state.put("time", plugin.getConfig().getString("world.time", "NORMAL"));
        state.put("weather", plugin.getConfig().getString("world.weather", "NORMAL"));
        state.put("weather-lock", plugin.getConfig().getBoolean("world.weather-lock", false));
        state.put("spawn-protection", plugin.getConfig().getBoolean("spawn-protection.enabled", true));
        state.put("spawn-radius", plugin.getConfig().getInt("spawn-protection.radius", 16));
        state.put("spawn-pvp", plugin.getConfig().getBoolean("spawn-protection.protect-pvp", true));
        state.put("spawn-build", plugin.getConfig().getBoolean("spawn-protection.protect-build", true));
        state.put("spawn-mob", plugin.getConfig().getBoolean("spawn-protection.protect-mob", true));

        // 读 settings.yml 覆盖
        overrideFile = new File(plugin.getDataFolder(), "settings.yml");
        override = YamlConfiguration.loadConfiguration(overrideFile);
        for (String key : override.getKeys(true)) {
            if (override.isBoolean(key) || override.isInt(key) || override.isString(key)) {
                state.put(key, override.get(key));
            }
        }

        applyWorldSettings();
    }

    /** 保存所有运行时状态到 settings.yml */
    public void save() {
        if (override == null) return;
        for (Map.Entry<String, Object> e : state.entrySet()) {
            override.set(e.getKey(), e.getValue());
        }
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            override.save(overrideFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("保存设置失败: " + ex.getMessage());
        }
    }

    public Object get(String key) {
        return state.getOrDefault(key, false);
    }

    public boolean getBool(String key) {
        Object v = state.get(key);
        return v instanceof Boolean && (Boolean) v;
    }

    public int getInt(String key) {
        Object v = state.get(key);
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public String getString(String key) {
        Object v = state.get(key);
        return v == null ? "" : String.valueOf(v);
    }

    /** 设置布尔状态并保存 */
    public void setBool(String key, boolean value) {
        state.put(key, value);
        save();
        if (key.equals("mob-spawning") || key.equals("animal-spawning")
                || key.equals("difficulty") || key.equals("time") || key.equals("weather")) {
            applyWorldSettings();
        }
    }

    /** 设置字符串状态并保存 */
    public void setString(String key, String value) {
        state.put(key, value);
        save();
        if (key.equals("difficulty") || key.equals("time") || key.equals("weather")) {
            applyWorldSettings();
        }
    }

    /** 应用世界级设置（难度/时间/天气/生成）到所有世界 */
    public void applyWorldSettings() {
        for (World w : Bukkit.getWorlds()) {
            // 难度
            try {
                w.setDifficulty(Difficulty.valueOf(getString("difficulty").toUpperCase()));
            } catch (Exception ignored) { }
            // 时间
            String time = getString("time");
            switch (time) {
                case "DAY":
                    w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    w.setTime(6000);
                    break;
                case "NIGHT":
                    w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    w.setTime(18000);
                    break;
                default:
                    w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                    break;
            }
            // 天气
            String weather = getString("weather");
            boolean lock = getBool("weather-lock");
            switch (weather) {
                case "CLEAR":
                    w.setStorm(false);
                    w.setThundering(false);
                    break;
                case "RAIN":
                    w.setStorm(true);
                    w.setThundering(false);
                    break;
                case "THUNDER":
                    w.setStorm(true);
                    w.setThundering(true);
                    break;
                default:
                    break; // NORMAL 保持原样（不锁定）
            }
            w.setGameRule(GameRule.DO_WEATHER_CYCLE, !lock);
            // 生成
            w.setGameRule(GameRule.DO_MOB_SPAWNING, getBool("mob-spawning"));
        }
    }

    /** 重置所有状态为 config 默认 */
    public void reset() {
        if (overrideFile != null && overrideFile.exists()) {
            overrideFile.delete();
        }
        if (override != null) {
            for (String key : override.getKeys(true)) {
                override.set(key, null);
            }
        }
        load();
    }
}