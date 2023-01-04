package dev.timecoding.fasterascend.command.completer;

import dev.timecoding.fasterascend.FasterAscend;
import dev.timecoding.fasterascend.config.ConfigHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FasterAscendCompleter implements TabCompleter {

    private FasterAscend plugin;
    private ConfigHandler configHandler;

    public FasterAscendCompleter(FasterAscend plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("fasterascend") || command.getName().equalsIgnoreCase("fa")){
            if(args.length == 1 && sender.hasPermission(configHandler.getString("Command-Permission"))){
                List<String> list = new ArrayList<>();
                list.add("reload");
                return list;
            }
        }
        return new ArrayList<>();
    }
}
