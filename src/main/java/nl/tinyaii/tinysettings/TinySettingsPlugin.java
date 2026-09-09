package nl.tinyaii.tinysettings;

import org.bukkit.plugin.java.JavaPlugin;

public class TinySettingsPlugin extends JavaPlugin {

    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        // TinyAII 品牌横幅
        getLogger().info(" _____ _                _    ___ ___");
        getLogger().info("|_   _(_)_ __  _   _   / \\\\  |_ _|_ _|");
        getLogger().info("  | | | | '_ \\\\| | | | / _ \\\\  | | | |");
        getLogger().info("  | | | | | | | |_| |/ ___ \\\\ | | | |");
        getLogger().info("  |_| |_|_| |_|\\\\__, /_/   \\\\_\\\\___|___|");
        getLogger().info("               |___/");
        getLogger().info("TinySettings 一键设置 v" + getDescription().getVersion() + " - TinyAII 出品");

        saveDefaultConfig();
        settingsManager = new SettingsManager(this);
        settingsManager.load();

        getCommand("settings").setExecutor(new SettingsCommand(this));
        getCommand("settings-status").setExecutor(new SettingsCommand(this));
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new SettingsGuiListener(this), this);

        getLogger().info("一键设置插件已启用。死亡不掉落=" + settingsManager.getBool("keep-inventory")
                + " | 难度=" + Cn.difficulty(settingsManager.getString("difficulty"))
                + " | PVP=" + settingsManager.getBool("pvp")
                + " | 出生点保护=" + settingsManager.getBool("spawn-protection"));
    }

    @Override
    public void onDisable() {
        if (settingsManager != null) settingsManager.save();
    }

    public SettingsManager getSettingsManager() { return settingsManager; }
}