package dev.timecoding.fasterascend;

import dev.timecoding.fasterascend.api.mfnalex.McVersion;
import dev.timecoding.fasterascend.api.bstats.Metrics;
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
import java.util.List;

public final class FasterAscend extends JavaPlugin {

    private ConfigHandler configHandler;
    private Metrics metrics = null;
    private int metricsid = 17298;
    private int spigotid = 107195;
    private UpdateChecker updateChecker;
    private boolean disable = false;
    private boolean scaffhold = false;
    private boolean extravines = false;
    private boolean actionbardisabled = false;
    private List<String> legacyversions = new ArrayList<>();

    @Override
    public void onEnable() {
        this.legacyversions.add("1.13");
        this.legacyversions.add("1.12");
        this.legacyversions.add("1.11");
        this.legacyversions.add("1.10");
        this.legacyversions.add("1.9");
        this.legacyversions.add("1.8");
        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();
        ConsoleCommandSender sender = this.getServer().getConsoleSender();
        if(this.configHandler.getBoolean("Enabled")) {
            this.updateChecker = new UpdateChecker(this, spigotid);
            sender.sendMessage("§eFasterAscend §cv" + this.getDescription().getVersion() + " §agot enabled!");
                String version = McVersion.current().getName();
                ArrayList<String> split = new ArrayList<String>(Arrays.asList(version.split("\\.")));
                if (split.get(1) == null) {
                    sender.sendMessage("§cUnable to get Minecraft-Version! Disabling Plugin...");
                    disable = true;
                }
                Integer baseversion = Integer.valueOf(split.get(1));
                if(baseversion < 19){
                    sender.sendMessage("§cInformation: To use this plugin I recommend to use the newest Minecraft-Version!");
                    sender.sendMessage("§cTo get support OR REPORT BUGS for the legacy Minecraft-Versions join my discord: https://discord.gg/mf9JNrzh");
                }
                if (baseversion >= 16) {
                    extravines = true;
                    sender.sendMessage("§7Enabled Support for §cWeeping and Twisting Vines §7(>= Minecraft Version 1.16)");
                }
                if(baseversion >= 14){
                    scaffhold = true;
                    sender.sendMessage("§7Enabled Support for §cScaffholding §7(>= Minecraft Version 1.14)");
                }
                if(baseversion < 14){
                    boolean valid = false;
                    for(String versions : this.legacyversions){
                        if(version.contains(versions)){
                            valid = true;
                            if(isNotForActionBar(versions)){
                                actionbardisabled = true;
                                sender.sendMessage("§cActionbars got disabled, because this plugin isn't using NMS for actionbars! §7(Actionbars are only running on Minecraft-Versions over 1.9 (1.10-1.19))");
                            }
                        }
                    }
                    if(!valid){
                        sender.sendMessage("§cHey, this plugin is sadly only compatible with the Minecraft versions between §f1.8 and 1.19§c! If you a version running on versions below, join my discord: https://discord.gg/mf9JNrzh");
                        disable = true;
                    }
                }

            if(!disable) {
                //Enable bStats
                if (this.configHandler.getBoolean("bStats")) {
                    this.metrics = new Metrics(this, metricsid);
                }
                try {
                    if (this.updateChecker.checkForUpdates()) {
                        sender.sendMessage("");
                        sender.sendMessage("§aA newer version of this plugin is already available! §e(Version §c" + this.updateChecker.getLatestVersion() + "§e)");
                        sender.sendMessage("§aFor the best experience download it here: §ehttps://www.spigotmc.org/resources/faster-ascend.107195/");
                        sender.sendMessage("");
                    } else {
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
            }else{
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }else{
            sender.sendMessage("§cThe plugin got disabled, because someone disabled the plugin in the config.yml!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public boolean isNotForActionBar(String version){
        if(version.contains("1.8") || version.contains("1.9")){
            return true;
        }
        return false;
    }

    public boolean isActionbarDisabled() {
        return actionbardisabled;
    }

    public boolean methodExists(String name){
        try {
            Bukkit.class.getMethod(name);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
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
