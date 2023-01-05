package dev.timecoding.fasterascend.listener;

import dev.timecoding.fasterascend.FasterAscend;
import dev.timecoding.fasterascend.config.ConfigHandler;
import dev.timecoding.fasterascend.event.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FAApiListener implements Listener {

    private FasterAscend plugin;
    private ConfigHandler configHandler;

    public FAApiListener(FasterAscend plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }

    @EventHandler
    public void onAscendStart(OnAscendStart e){
        triggerConfigEvents(e.getPlayer(), "OnAscendStart");
    }

    @EventHandler
    public void onAscendEnd(OnAscendEnd e){
        triggerConfigEvents(e.getPlayer(), "OnAscendEnd");
    }

    @EventHandler
    public void onInstantAscendAnimationStart(OnInstantAscendAnimationStart e){
        triggerConfigEvents(e.getPlayer(), "OnAscendStart");
    }

    @EventHandler
    public void onInstantAscendAnimationEnd(OnInstantAscendAnimationEnd e){
        triggerConfigEvents(e.getPlayer(), "OnAscendEnd");
    }

    @EventHandler
    public void onInstantTeleport(OnInstantTeleport e){
        triggerConfigEvents(e.getPlayer(), "OnInstantTeleport");
    }

    public void triggerConfigEvents(Player p, String argname){
        String arg = "Events."+argname+".";
        if(configHandler.getBoolean(arg+"Enabled")){
            p.sendMessage(String.valueOf(getObjectWithPlaceholders(configHandler.getString(arg+"Message"), p)));
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.valueOf(getObjectWithPlaceholders(configHandler.getString(arg+"Message"), p))));
            p.playSound(p.getLocation(), Sound.valueOf(configHandler.getString(arg+"Sound.Name")), configHandler.getInteger(arg+"Sound.Volume"), configHandler.getInteger(arg+"Sound.Pitch"));
        }
    }

    public Object getObjectWithPlaceholders(Object o, Player p){
        String edited = String.valueOf(o);
        Block b = p.getLocation().getBlock();
        return edited.replace("%player%", p.getName()).replace("%block_name_lowercase%", b.getType().toString().toLowerCase()).replace("%block_name_uppercase%", b.getType().toString().toUpperCase());
    }
}
