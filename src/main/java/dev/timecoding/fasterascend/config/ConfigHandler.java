package dev.timecoding.fasterascend.config;

import dev.timecoding.fasterascend.FasterAscend;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private FasterAscend plugin;

    public ConfigHandler(FasterAscend plugin) {
        this.plugin = plugin;
    }

    private File f = null;
    public YamlConfiguration cfg = null;

    private boolean retry = false;

    private String newconfigversion = "1.0";

    public void init() {
        plugin.saveDefaultConfig();
        f = new File(plugin.getDataFolder(), "config.yml");
        cfg = YamlConfiguration.loadConfiguration(f);
        cfg.options().copyDefaults(true);
        checkForConfigUpdate();
    }

    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    public void save() {
        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNewestConfigVersion() {
        return this.newconfigversion;
    }

    public boolean configUpdateAvailable() {
        if (!getNewestConfigVersion().equalsIgnoreCase(getString("config-version")))
            return true;
        return false;
    }

    public void reload() {
        cfg = YamlConfiguration.loadConfiguration(f);
    }

    public void checkForConfigUpdate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Checking for config updates...");
        if (configUpdateAvailable()) {
            final Map<String, Object> quicksave = this.plugin.getConfigHandler().getConfig().getValues(true);
            File file = new File("plugins//FasterAscend", "config.yml");
            if (file.exists()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config Update found! (" + getNewestConfigVersion() + ") Updating config...");
                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {

                    public void run() {
                        plugin.saveResource("config.yml", false);
                        reload();
                        for (String save : quicksave.keySet()) {
                            if (keyExists(save) && !save.equalsIgnoreCase("config-version")) {
                                getConfig().set(save, quicksave.get(save));
                            }
                        }
                        save();
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config got updated!");
                    }
                }, 10);
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No Config found! Creating a new one...");
                this.plugin.saveResource("config.yml", false);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No config update found!");
        }
    }


    public YamlConfiguration getConfig(){
        return cfg;
    }

    public void setString(String key, String value){
        cfg.set(key, value);
        save();
    }

    public Integer getInteger(String key){
        if(keyExists(key)){
            return cfg.getInt(key);
        }
        return null;
    }

    public String getString(String key){
        if(keyExists(key)){
            return ChatColor.translateAlternateColorCodes('&', cfg.getString(key));
        }
        return null;
    }

    public Boolean getBoolean(String key){
        if(keyExists(key)){
            return cfg.getBoolean(key);
        }
        return null;
    }

    public boolean keyExists(String key){
        if(cfg.get(key) != null){
            return true;
        }
        return false;
    }
}
