package dev.timecoding.fasterascend.listener;

import dev.timecoding.fasterascend.FasterAscend;
import dev.timecoding.fasterascend.config.ConfigHandler;
import dev.timecoding.fasterascend.event.*;
import dev.timecoding.fasterascend.event.enums.FAAPIArg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AscendListener implements Listener {

    private FasterAscend plugin;
    private ConfigHandler configHandler;

    public AscendListener(FasterAscend plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }

    private HashMap<Player, Location> before = new HashMap<>();
    private ArrayList<Player> needinput = new ArrayList<>();
    private ArrayList<Player> wasonground = new ArrayList<>();
    private ArrayList<Player> isonground = new ArrayList<>();
    private ArrayList<Player> apitriggered = new ArrayList<>();
    private ArrayList<Player> instantanimation = new ArrayList<>();


    public void setBefore(Player p, Location loc){
        if(before.containsKey(p)){
            before.remove(p);
        }
        before.put(p, loc);
    }

    public void removeBefore(Player p){
        if(before.containsKey(p)){
            before.remove(p);
        }
    }

    public Location getBefore(Player p){
        if(before.containsKey(p)){
            return before.get(p);
        }
        return null;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        Location loc = p.getEyeLocation().subtract(0,1,0);
        Block block = loc.getBlock();
        Material posm = block.getType();
        isonground.remove(p);
        if(configHandler.getBoolean("Blocks.Ladders") && posm == Material.LADDER || configHandler.getBoolean("Blocks.Vines") && posm == Material.VINE || configHandler.getBoolean("Blocks.Water") && posm == Material.WATER){
            if(getBefore(p) == null){
                setBefore(p, loc);
            }
            Location before = getBefore(p);
            if(before != loc){
                setBefore(p, loc);
                Location subtract = loc.subtract(0,1,0);
                Material m = subtract.getBlock().getType();
                if(configHandler.getBoolean("Blocks.Ladders") && m == Material.LADDER || configHandler.getBoolean("Blocks.Vines") && m == Material.VINE || configHandler.getBoolean("Blocks.Water") && m == Material.WATER) {
                    if (loc.getY() > before.getY()) {
                        if(!apitriggered.contains(p)){
                            triggerAPIEvent(FAAPIArg.OnAscendStart, p);
                            apitriggered.add(p);
                        }
                        if(p.getLocation().getY() >= getHighestPointOnLadder(p.getLocation())-1){
                            if(instantanimation.contains(p)){
                                triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationEnd, p);
                            }else{
                                triggerAPIEvent(FAAPIArg.OnAscendEnd, p);
                            }
                            instantanimation.remove(p);
                            apitriggered.remove(p);
                        }
                        double speed = (0.1 * getCustomSpeed(posm));
                        if(configHandler.getBoolean("RightClick.Enabled") && wasonground.contains(p) || configHandler.getBoolean("ShiftMode.Enabled") && wasonground.contains(p)){
                            p.setVelocity(p.getVelocity().setY(speed));
                            if(p.getLocation().getY() >= getHighestPointOnLadder(p.getLocation())-1){
                                wasonground.remove(p);
                            }
                        }
                        if(!configHandler.getBoolean("InstantClimbUp.Enabled") || configHandler.getBoolean("InstantClimbUp.Enabled") && configHandler.getBoolean("InstantClimbUp.DontDisableNormalBoost") && !wasonground.contains(p)) {
                            if (needinput.contains(p)) {
                                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (e.getFrom().getY() < e.getTo().getY()) {
                                            needinput.remove(p);
                                        }
                                    }
                                }, getCustomDelay(posm));
                            } else {
                                triggerAPIEvent(FAAPIArg.OnAscendBoost, p);
                                p.setVelocity(p.getVelocity().setY(speed));
                                needinput.add(p);
                            }
                        }else{
                            if(!configHandler.getBoolean("InstantClimbUp.OnlyOnGround")) {
                                if (configHandler.getBoolean("InstantClimbUp.WithAnimation")) {
                                    if(!instantanimation.contains(p)){
                                        triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                        instantanimation.add(p);
                                    }
                                    p.setVelocity(p.getVelocity().setY(speed));
                                } else {
                                    Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                                    p.teleport(location);
                                    triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                                }
                            }else{
                                if (configHandler.getBoolean("InstantClimbUp.WithAnimation") && wasonground.contains(p)) {
                                    if(!instantanimation.contains(p)){
                                        triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                        instantanimation.add(p);
                                    }
                                    p.setVelocity(p.getVelocity().setY(speed));
                                    if(p.getLocation().getY() >= getHighestPointOnLadder(p.getLocation())-1){
                                        wasonground.remove(p);
                                    }
                                }
                            }
                        }
                    }
                }else if(getBefore(p) != null){
                    isonground.add(p);
                    removeBefore(p);
                    double speed = (0.1 * getCustomSpeed(posm));
                    if(configHandler.getBoolean("InstantClimbUp.Enabled")) {
                        if (configHandler.getBoolean("InstantClimbUp.OnlyOnGround")) {
                            if (configHandler.getBoolean("InstantClimbUp.WithAnimation")) {
                                if(!instantanimation.contains(p)){
                                    if(!instantanimation.contains(p)){
                                        triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                        instantanimation.add(p);
                                    }
                                    instantanimation.add(p);
                                }
                                p.setVelocity(p.getVelocity().setY(speed));
                                if (!wasonground.contains(p)) {
                                    wasonground.add(p);
                                }
                            } else {
                                Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                                p.teleport(location);
                                triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                            }
                        }
                    }
                }
            }
        }else{
            wasonground.remove(p);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(configHandler.getBoolean("RightClick.Enabled") && isonground.contains(p)) {
            if (configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.LEFT_CLICK_BLOCK || configHandler.getBoolean("RightClick.AlsoLeft") && !configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.RIGHT_CLICK_BLOCK || configHandler.getBoolean("RightClick.AlsoLeft") && !configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.LEFT_CLICK_BLOCK || !configHandler.getBoolean("RightClick.AlsoLeft") && !configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                 if(configHandler.getBoolean("RightClick.WithShift") && p.isSneaking() || !configHandler.getBoolean("RightClick.WithShift")){
                     Material posm = e.getClickedBlock().getType();
                     double speed = (0.1 * getCustomSpeed(posm));
                     if(configHandler.getBoolean("Blocks.Ladders") && posm == Material.LADDER || configHandler.getBoolean("Blocks.Vines") && posm == Material.VINE || configHandler.getBoolean("Blocks.Water") && posm == Material.WATER) {
                         if (configHandler.getBoolean("RightClick.WithAnimation")) {
                             if(!instantanimation.contains(p)){
                                 triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                 instantanimation.add(p);
                             }
                             p.setVelocity(p.getVelocity().setY(speed));
                             if (!wasonground.contains(p)) {
                                 wasonground.add(p);
                             }
                         } else {
                             Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                             p.teleport(location);
                             triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                         }
                     }
                 }
                }
            }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        Material posm = p.getLocation().getBlock().getType();
        double speed = (0.1 * getCustomSpeed(posm));
        if(configHandler.getBoolean("ShiftMode.Enabled")){
        if(configHandler.getBoolean("Blocks.Ladders") && posm == Material.LADDER || configHandler.getBoolean("Blocks.Vines") && posm == Material.VINE || configHandler.getBoolean("Blocks.Water") && posm == Material.WATER) {
            if(!isonground.contains(p) && !wasonground.contains(p)){
                if (configHandler.getBoolean("ShiftMode.WithAnimation")) {
                    if(!instantanimation.contains(p)){
                        triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                        instantanimation.add(p);
                    }
                    p.setVelocity(p.getVelocity().setY(speed));
                    if (!wasonground.contains(p)) {
                        wasonground.add(p);
                    }
                } else {
                    Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                    p.teleport(location);
                    triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                }
            }
        }
        }
    }

    public Integer getHighestPointOnLadder(Location loc){
        Integer finali = 0;
        for(int i = loc.getBlockY(); i <= loc.getWorld().getHighestBlockYAt(loc)+256; i++){
            Block b = loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ());
            Material posm = b.getType();
            if(posm != null) {
                if (configHandler.getBoolean("Blocks.Ladders") && posm == Material.LADDER || configHandler.getBoolean("Blocks.Vines") && posm == Material.VINE || configHandler.getBoolean("Blocks.Water") && posm == Material.WATER) {
                    finali = i;
                }
            }
        }
        return finali+1;
    }

    public void triggerAPIEvent(FAAPIArg faapiArg, Player p){
        switch (faapiArg){
            case OnAscendEnd:
                OnAscendEnd onAscendEnd = new OnAscendEnd(p);
                if(!onAscendEnd.isCancelled()){
                    Bukkit.getPluginManager().callEvent(onAscendEnd);
                }
                break;
            case OnAscendStart:
                OnAscendStart onAscendStart = new OnAscendStart(p);
                if(!onAscendStart.isCancelled()){
                    Bukkit.getPluginManager().callEvent(onAscendStart);
                }
                break;
            case OnAscendBoost:
                OnAscendBoost onAscendBoost = new OnAscendBoost(p);
                if(!onAscendBoost.isCancelled()){
                    Bukkit.getPluginManager().callEvent(onAscendBoost);
                }
                break;
            case OnInstantTeleport:
                OnInstantTeleport onInstantTeleport = new OnInstantTeleport(p);
                if(!onInstantTeleport.isCancelled()){
                    Bukkit.getPluginManager().callEvent(onInstantTeleport);
                }
                break;
            case OnInstantAscendAnimationStart:
                OnInstantAscendAnimationStart onInstantAscendAnimationStart = new OnInstantAscendAnimationStart(p);
                if(!onInstantAscendAnimationStart.isCancelled()){
                    Bukkit.getPluginManager().callEvent(onInstantAscendAnimationStart);
                }
                break;
            case OnInstantAscendAnimationEnd:
                OnInstantAscendAnimationEnd onInstantAscendAnimationEnd = new OnInstantAscendAnimationEnd(p);
                if(!onInstantAscendAnimationEnd.isCancelled()){
                    Bukkit.getPluginManager().callEvent(onInstantAscendAnimationEnd);
                }
                break;
        }
    }

    public boolean customEnabled(Material m){
        switch (m){
            case VINE:
                return configHandler.getBoolean("Boost.Custom.Vines.Enabled");
            case WATER:
                return configHandler.getBoolean("Boost.Custom.Water.Enabled");
            case LADDER:
                return configHandler.getBoolean("Boost.Custom.Ladders.Enabled");
        }
        return false;
    }

    public Integer getCustomSpeed(Material m){
        if(customEnabled(m)){
            switch (m){
                case VINE:
                    return configHandler.getInteger("Boost.Custom.Vines.Speed");
                case WATER:
                    return configHandler.getInteger("Boost.Custom.Water.Speed");
                case LADDER:
                    return configHandler.getInteger("Boost.Custom.Ladders.Speed");
            }
        }
        return configHandler.getInteger("Boost.Speed");
    }

    public Integer getCustomDelay(Material m){
        if(customEnabled(m)){
            switch (m){
                case VINE:
                    return configHandler.getInteger("Boost.Custom.Vines.Delay");
                case WATER:
                    return configHandler.getInteger("Boost.Custom.Water.Delay");
                case LADDER:
                    return configHandler.getInteger("Boost.Custom.Ladders.Delay");
            }
        }
        return configHandler.getInteger("Boost.Delay");
    }

}
