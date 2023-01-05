package dev.timecoding.fasterascend;

import dev.timecoding.fasterascend.api.Metrics;
import dev.timecoding.fasterascend.api.UpdateChecker;
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

import java.util.ArrayList;
import java.util.Arrays;

public final class FasterAscend extends JavaPlugin {

    private ConfigHandler configHandler;
    private Metrics metrics = null;
    private int metricsid = 17298;
    private int spigotid = 107195;
    private UpdateChecker updateChecker;
    private boolean disable = false;
    private boolean scaffhold = false;
    private boolean extravines = false;

    @Override
    public void onEnable() {
        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();
        ConsoleCommandSender sender = this.getServer().getConsoleSender();
        if(this.configHandler.getBoolean("Enabled")) {
            this.updateChecker = new UpdateChecker(this, spigotid);
            sender.sendMessage("§eFasterAscend §cv" + this.getDescription().getVersion() + " §agot enabled!");

            //Enable bStats
            if (this.configHandler.getBoolean("bStats")) {
                this.metrics = new Metrics(this, metricsid);
            }

            String version = Bukkit.getMinecraftVersion();
            ArrayList<String> split = new ArrayList<String>(Arrays.asList(version.split("\\.")));
            if(split.get(1) == null){
                disable = true;
            }
            Integer baseversion = Integer.valueOf(split.get(1));
            if(baseversion >= 14){
                scaffhold = true;
                sender.sendMessage("§7Enabled Support for §cScaffholding §7(>= Minecraft Version 1.14)");
            }
            if(baseversion >= 16){
                extravines = true;
                sender.sendMessage("§7Enabled Support for §cWeeping and Twisting Vines §7(>= Minecraft Version 1.16)");
            }

            try {
                if(this.updateChecker.checkForUpdates()) {
                    sender.sendMessage("");
                    sender.sendMessage("§aA newer version of this plugin is already available! §e(Version §c"+this.updateChecker.getLatestVersion()+"§e)");
                    sender.sendMessage("§aFor the best experience download it here: §ehttps://www.spigotmc.org/resources/faster-ascend.107195/");
                    sender.sendMessage("");
                }else{
                    sender.sendMessage("§cNo update found! You're ready to go :)");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            PluginManager pluginManager = this.getServer().getPluginManager();
            pluginManager.registerEvents(new AscendListener(this), this);
            pluginManager.registerEvents(new FAApiListener(this), this);

            PluginCommand command = this.getCommand("fasterascend");
            command.setExecutor(new FasterAscendCommand(this));
            command.setTabCompleter(new FasterAscendCompleter(this));
            if(disable){
                sender.sendMessage("§cUnable to get Minecraft-Version! Disabling Plugin...");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }else{
            sender.sendMessage("§cThe plugin got disabled, because someone disabled the plugin in the config.yml!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void checkForUpdate(){

    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public boolean areScaffholdingsAllowed() {
        return scaffhold;
    }

    public boolean areExtravinesAllowed() {
        return extravines;
    }
}
