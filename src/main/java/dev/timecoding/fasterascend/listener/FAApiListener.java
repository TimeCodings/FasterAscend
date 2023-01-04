package dev.timecoding.fasterascend.listener;

import dev.timecoding.fasterascend.FasterAscend;
import dev.timecoding.fasterascend.config.ConfigHandler;
import dev.timecoding.fasterascend.event.*;
import org.bukkit.Sound;
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
            p.sendMessage(configHandler.getString(arg+"Message"));
            p.playSound(p.getLocation(), Sound.valueOf(configHandler.getString(arg+"Sound.Name")), configHandler.getInteger(arg+"Sound.Volume"), configHandler.getInteger(arg+"Sound.Pitch"));
        }
    }
}
