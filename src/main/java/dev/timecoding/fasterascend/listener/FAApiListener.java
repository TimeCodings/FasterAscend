package dev.timecoding.fasterascend.listener;

import dev.timecoding.fasterascend.FasterAscend;
import dev.timecoding.fasterascend.config.ConfigHandler;
import dev.timecoding.fasterascend.event.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;

public class FAApiListener implements Listener {

    private FasterAscend plugin;
    private ConfigHandler configHandler;

    //NEW
    private List<Player> inAnimation = new ArrayList<>();
    //

    public FAApiListener(FasterAscend plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }

    @EventHandler
    public void onAscendStart(OnAscendStart e){
        if(isExcluded(e.getPlayer())){
            e.setCancelled(true);
            return;
        }
        inAnimation.add(e.getPlayer());
        triggerConfigEvents(e.getPlayer(), "OnAscendStart");
    }

    @EventHandler
    public void onAscendEnd(OnAscendEnd e){
        inAnimation.remove(e.getPlayer());
        triggerConfigEvents(e.getPlayer(), "OnAscendEnd");
    }

    @EventHandler
    public void onInstantAscendAnimationStart(OnInstantAscendAnimationStart e){
        if(isExcluded(e.getPlayer())){
            e.setCancelled(true);
            return;
        }
        inAnimation.add(e.getPlayer());
        triggerConfigEvents(e.getPlayer(), "OnAscendStart");
    }

    @EventHandler
    public void onInstantAscendAnimationEnd(OnInstantAscendAnimationEnd e){
        inAnimation.remove(e.getPlayer());
        triggerConfigEvents(e.getPlayer(), "OnAscendEnd");
    }

    @EventHandler
    public void onAscendBoost(OnAscendBoost e){
        if(isExcluded(e.getPlayer())){
            e.setCancelled(true);
            return;
        }
        triggerConfigEvents(e.getPlayer(), "OnAscendBoost");
    }

    @EventHandler
    public void onInstantTeleport(OnInstantTeleport e){
        if(isExcluded(e.getPlayer())){
            e.setCancelled(true);
            return;
        }
        triggerConfigEvents(e.getPlayer(), "OnInstantTeleport");
    }

    public void triggerConfigEvents(Player p, String argname){
        String arg = "Events."+argname+".";
        if(configHandler.getBoolean(arg+"Enabled")){
            p.sendMessage(String.valueOf(getObjectWithPlaceholders(configHandler.getString(arg+"Message"), p)));
            if(!plugin.isActionbarDisabled()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.valueOf(getObjectWithPlaceholders(configHandler.getString(arg + "Message"), p))));
            }
            p.playSound(p.getLocation(), Sound.valueOf(configHandler.getString(arg+"Sound.Name")), configHandler.getInteger(arg+"Sound.Volume"), configHandler.getInteger(arg+"Sound.Pitch"));
        }
    }

    public Object getObjectWithPlaceholders(Object o, Player p){
        String edited = String.valueOf(o);
        Block b = p.getLocation().getBlock();
        return edited.replace("%player%", p.getName()).replace("%block_name_lowercase%", b.getType().toString().toLowerCase()).replace("%block_name_uppercase%", b.getType().toString().toUpperCase());
    }

    public boolean isExcluded(Player player){
        GameMode gameMode = player.getGameMode();
        boolean creative = plugin.getConfigHandler().getBoolean("DisableForGameMode.Creative");
        boolean survival = plugin.getConfigHandler().getBoolean("DisableForGameMode.Survival");
        boolean adventure = plugin.getConfigHandler().getBoolean("DisableForGameMode.Adventure");
        String permission = plugin.getConfigHandler().getString("Exclusion.Permission");
        if(gameMode.equals(GameMode.CREATIVE) && creative || gameMode.equals(GameMode.SURVIVAL) && survival || gameMode.equals(GameMode.ADVENTURE) && adventure){
            return true;
        }
        if(plugin.getConfigHandler().getBoolean("Exclusion.Enabled") && player.hasPermission(permission)){
            return true;
        }
        return false;
    }

    public List<Player> getInAnimation() {
        return inAnimation;
    }
}
