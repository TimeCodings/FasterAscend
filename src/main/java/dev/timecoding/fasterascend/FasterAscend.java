package dev.timecoding.fasterascend;

import dev.timecoding.fasterascend.api.Metrics;
import dev.timecoding.fasterascend.command.FasterAscendCommand;
import dev.timecoding.fasterascend.command.completer.FasterAscendCompleter;
import dev.timecoding.fasterascend.config.ConfigHandler;
import dev.timecoding.fasterascend.listener.AscendListener;
import dev.timecoding.fasterascend.listener.FAApiListener;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FasterAscend extends JavaPlugin {

    private ConfigHandler configHandler;
    private Metrics metrics = null;
    private int metricsid = 17298;

    @Override
    public void onEnable() {
        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();
        ConsoleCommandSender sender = this.getServer().getConsoleSender();
        if(this.configHandler.getBoolean("Enabled")) {
            sender.sendMessage("§eFasterAscend §cv" + this.getDescription().getVersion() + " §agot enabled!");

            //Enable bStats
            if (this.configHandler.getBoolean("bStats")) {
                this.metrics = new Metrics(this, metricsid);
            }

            PluginManager pluginManager = this.getServer().getPluginManager();
            pluginManager.registerEvents(new AscendListener(this), this);
            pluginManager.registerEvents(new FAApiListener(this), this);

            PluginCommand command = this.getCommand("fasterascend");
            command.setExecutor(new FasterAscendCommand(this));
            command.setTabCompleter(new FasterAscendCompleter(this));
        }else{
            sender.sendMessage("§cThe plugin got disabled, because someone disabled the plugin in the config.yml!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
