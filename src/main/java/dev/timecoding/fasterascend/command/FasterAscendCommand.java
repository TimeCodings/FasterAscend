package dev.timecoding.fasterascend.command;

import dev.timecoding.fasterascend.FasterAscend;
import dev.timecoding.fasterascend.config.ConfigHandler;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FasterAscendCommand implements CommandExecutor{

    private FasterAscend plugin;
    private ConfigHandler configHandler;

    public FasterAscendCommand(FasterAscend plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(sender.hasPermission(configHandler.getString("Command-Permission"))){
                if(args.length == 1) {
                    if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                        configHandler.reload();
                        sender.sendMessage("§aThe config.yml was successfully reloaded!");
                    }else{
                        sender.sendMessage("§c/fasterascend reload");
                    }
                }else{
                    sender.sendMessage("§c/fasterascend reload");
                }
            }else{
                sender.sendMessage("§cYou do not have the permission to use this command!");
            }
        return false;
    }
}
