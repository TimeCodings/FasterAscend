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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

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

    private HashMap<Player, Integer> savedHigh = new HashMap<>();
    private ArrayList<Player> needinput = new ArrayList<>();
    private ArrayList<Player> wasonground = new ArrayList<>();
    private ArrayList<Player> isonground = new ArrayList<>();
    private ArrayList<Player> apitriggered = new ArrayList<>();
    private ArrayList<Player> instantanimation = new ArrayList<>();


    public void setBefore(Player p, Location loc){
        before.put(p, loc);
    }

    public void removeBefore(Player p){
        before.remove(p);
    }

    public Location getBefore(Player p){
        if(before.containsKey(p)){
            return before.get(p);
        }
        return null;
    }

    public boolean fixScaffolding(Player p, Material m){
        return plugin.areScaffoldingAllowed()
            && configHandler.getBoolean("FixScaffolding")
            && m == Material.SCAFFOLDING
            && p.isSneaking();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        Location loc = p.getEyeLocation().subtract(0,1,0);
        Block block = loc.getBlock();
        Material posm = block.getType();
        isonground.remove(p);
        if(!fixScaffolding(p, posm) && !plugin.getFaApiListener().getBlock().contains(p)) {
            if (materialAllowed(posm) && !isInBlacklist(p)) {
                if (getBefore(p) == null) {
                    setBefore(p, loc);
                }
                Location before = getBefore(p);
                if (before != loc) {
                    setBefore(p, loc);
                    Location subtract = loc.subtract(0, 1, 0);
                    Material m = subtract.getBlock().getType();
                    boolean blockIt = false;
                    if (materialAllowed(m)) {
                        if (loc.getY() > before.getY() && highIsValid(p)) {
                            if (!apitriggered.contains(p)) {
                                blockIt = triggerAPIEvent(FAAPIArg.OnAscendStart, p);
                                apitriggered.add(p);
                            }
                            if (p.getLocation().getY() >= getHighestPointOnLadder(p.getLocation()) - 1) {
                                if (instantanimation.contains(p)) {
                                    blockIt = triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationEnd, p);
                                } else {
                                    blockIt = triggerAPIEvent(FAAPIArg.OnAscendEnd, p);
                                }
                                instantanimation.remove(p);
                                apitriggered.remove(p);
                                savedHigh.remove(p);
                            }
                            double speed = (0.1 * getCustomSpeed(posm));
                            if (configHandler.getBoolean("RightClick.Enabled") && wasonground.contains(p) || configHandler.getBoolean("ShiftMode.Enabled") && wasonground.contains(p)) {
                                if(!blockIt) {
                                    p.setVelocity(p.getVelocity().setY(speed));
                                    if (p.getLocation().getY() >= getHighestPointOnLadder(p.getLocation()) - 1) {
                                        wasonground.remove(p);
                                    }
                                }
                            }
                            if (!configHandler.getBoolean("InstantClimbUp.Enabled") || configHandler.getBoolean("InstantClimbUp.Enabled") && configHandler.getBoolean("InstantClimbUp.DontDisableNormalBoost") && !wasonground.contains(p)) {
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
                                    blockIt = triggerAPIEvent(FAAPIArg.OnAscendBoost, p);
                                    if(!blockIt) {
                                        p.setVelocity(p.getVelocity().setY(speed));
                                        needinput.add(p);
                                    }
                                }
                            } else {
                                if (!configHandler.getBoolean("InstantClimbUp.OnlyOnGround")) {
                                    if (configHandler.getBoolean("InstantClimbUp.WithAnimation")) {
                                        if (!instantanimation.contains(p)) {
                                            blockIt = triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                            instantanimation.add(p);
                                        }
                                        if(!blockIt) {
                                            p.setVelocity(p.getVelocity().setY(speed));
                                        }
                                    } else {
                                        blockIt = triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                                        if(!blockIt) {
                                            Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                                            p.teleport(location);
                                        }
                                    }
                                } else {
                                    if (configHandler.getBoolean("InstantClimbUp.WithAnimation") && wasonground.contains(p)) {
                                        if (!instantanimation.contains(p)) {
                                            blockIt = triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                            instantanimation.add(p);
                                        }
                                        if(!blockIt) {
                                            p.setVelocity(p.getVelocity().setY(speed));
                                        }
                                        if (p.getLocation().getY() >= getHighestPointOnLadder(p.getLocation()) - 1) {
                                            wasonground.remove(p);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (getBefore(p) != null) {
                        isonground.add(p);
                        removeBefore(p);
                        double speed = (0.1 * getCustomSpeed(posm));
                        if (configHandler.getBoolean("InstantClimbUp.Enabled")) {
                            if (configHandler.getBoolean("InstantClimbUp.OnlyOnGround")) {
                                if (configHandler.getBoolean("InstantClimbUp.WithAnimation")) {
                                    if (!instantanimation.contains(p)) {
                                        if (!instantanimation.contains(p)) {
                                            blockIt = triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                                            instantanimation.add(p);
                                        }
                                        instantanimation.add(p);
                                    }
                                    if(!blockIt) {
                                        p.setVelocity(p.getVelocity().setY(speed));
                                    }
                                    if (!wasonground.contains(p)) {
                                        wasonground.add(p);
                                    }
                                } else {
                                    blockIt = triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                                    if(!blockIt) {
                                        Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                                        p.teleport(location);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                wasonground.remove(p);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(configHandler.getBoolean("RightClick.Enabled") && isonground.contains(p) && !isInBlacklist(p)) {
            if (configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.LEFT_CLICK_BLOCK || configHandler.getBoolean("RightClick.AlsoLeft") && !configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.RIGHT_CLICK_BLOCK || configHandler.getBoolean("RightClick.AlsoLeft") && !configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.LEFT_CLICK_BLOCK || !configHandler.getBoolean("RightClick.AlsoLeft") && !configHandler.getBoolean("RightClick.OnlyLeft") && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                 if(configHandler.getBoolean("RightClick.WithShift") && p.isSneaking() || !configHandler.getBoolean("RightClick.WithShift")){
                     Material posm = e.getClickedBlock().getType();
                     double speed = (0.1 * getCustomSpeed(posm));
                     if(materialAllowed(posm)) {
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
        if(plugin.getConfigHandler().getBoolean("SneakingStop") && e.isSneaking() && plugin.getFaApiListener().getInAnimation().contains(p)){
            plugin.getFaApiListener().cancelAnimation(p);
            needinput.remove(p);
            instantanimation.remove(p);
            apitriggered.remove(p);
            isonground.remove(p);
            wasonground.remove(p);
            return;
        }
        Material posm = p.getLocation().getBlock().getType();
        double speed = (0.1 * getCustomSpeed(posm));
        boolean blockIt = false;
        if(configHandler.getBoolean("ShiftMode.Enabled") && !isInBlacklist(p)){
        if(materialAllowed(posm)) {
            if(!isonground.contains(p) && !wasonground.contains(p)){
                if (configHandler.getBoolean("ShiftMode.WithAnimation")) {
                    if(!instantanimation.contains(p)){
                        blockIt = triggerAPIEvent(FAAPIArg.OnInstantAscendAnimationStart, p);
                        instantanimation.add(p);
                    }
                    if(!blockIt) {
                        p.setVelocity(p.getVelocity().setY(speed));
                    }
                    if (!wasonground.contains(p)) {
                        wasonground.add(p);
                    }
                } else {
                    blockIt = triggerAPIEvent(FAAPIArg.OnInstantTeleport, p);
                    if(!blockIt) {
                        Location location = new Location(p.getWorld(), p.getLocation().getX(), getHighestPointOnLadder(p.getLocation()), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                        p.teleport(location);
                    }
                }
            }
        }
        }
    }

    public boolean materialAllowed(Material posm){
        if(configHandler.getBoolean("Blocks.Ladders") && posm == Material.LADDER || configHandler.getBoolean("Blocks.Vines") && posm == Material.VINE || configHandler.getBoolean("Blocks.Water") && posm == Material.WATER){
            return true;
        }
        if(plugin.areScaffoldingAllowed()){
            if(configHandler.getBoolean("Blocks.Scaffolding") && posm == Material.SCAFFOLDING){
                return true;
            }
        }
        if(plugin.areExtravinesAllowed()){
            if(configHandler.getBoolean("Blocks.ExtraVines.Weeping")){
                if(posm == Material.WEEPING_VINES || posm == Material.WEEPING_VINES_PLANT){
                    return true;
                }
            }
            if(configHandler.getBoolean("Blocks.ExtraVines.Twisting")){
                if(posm == Material.TWISTING_VINES || posm == Material.TWISTING_VINES_PLANT){
                    return true;
                }
            }
        }
        return false;
    }

    public Integer getHighestPointOnLadder(Location loc){
        Integer finali = 0;
        for(int i = loc.getBlockY(); i <= loc.getWorld().getHighestBlockYAt(loc)+256; i++){
            Block b = loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ());
            Material posm = b.getType();
            if(posm != null) {
                if (materialAllowed(posm)) {
                    finali = i;
                }
            }
        }
        return finali+1;
    }

    public Integer getFirstValidY(Location loc){
        boolean found = false;
        Integer y = loc.getBlockY();
        while(!found){
            if(!materialAllowed(loc.subtract(0, y, 0).getBlock().getType())){
                found = true;
            }else{
                y--;
            }
        }
        return y;
    }

    public Integer calculateAscendingBlocks(Location loc, boolean fromGround){
        Integer blocks = 0;
        Integer highesty = getHighestPointOnLadder(loc);
        Integer setI = loc.getBlockY();
        if(fromGround){
            setI = getFirstValidY(loc);
        }
        for(int i = setI; i <= highesty; i++){
            Location finalLoc = new Location(loc.getWorld(), loc.getX(), i, loc.getZ());
            Block block = finalLoc.getBlock();
            if(materialAllowed(block.getType())){
                blocks++;
            }else{
                i = highesty+1;
            }
        }
        return blocks;
    }

    public boolean highIsValid(Player p){
        Integer ascend = calculateAscendingBlocks(p.getLocation(), true);
        if(!savedHigh.containsKey(p)) {
            if (configHandler.getBoolean("RequiredLength.Enabled")) {
                if (ascend >= configHandler.getInteger("RequiredLength.LengthInBlocks")) {
                    savedHigh.put(p, ascend);
                    return true;
                } else {
                    return false;
                }
            }
        }else{
            return true;
        }
        return true;
    }

    public boolean triggerAPIEvent(FAAPIArg faapiArg, Player p){
        switch (faapiArg){
            case OnAscendEnd:
                OnAscendEnd onAscendEnd = new OnAscendEnd(p);
                Bukkit.getPluginManager().callEvent(onAscendEnd);
                return onAscendEnd.isCancelled();
            case OnAscendStart:
                OnAscendStart onAscendStart = new OnAscendStart(p);
                Bukkit.getPluginManager().callEvent(onAscendStart);
                return onAscendStart.isCancelled();
            case OnAscendBoost:
                OnAscendBoost onAscendBoost = new OnAscendBoost(p);
                Bukkit.getPluginManager().callEvent(onAscendBoost);
                return onAscendBoost.isCancelled();
            case OnInstantTeleport:
                OnInstantTeleport onInstantTeleport = new OnInstantTeleport(p);
                Bukkit.getPluginManager().callEvent(onInstantTeleport);
                return onInstantTeleport.isCancelled();
            case OnInstantAscendAnimationStart:
                OnInstantAscendAnimationStart onInstantAscendAnimationStart = new OnInstantAscendAnimationStart(p);
                Bukkit.getPluginManager().callEvent(onInstantAscendAnimationStart);
                return onInstantAscendAnimationStart.isCancelled();
            case OnInstantAscendAnimationEnd:
                OnInstantAscendAnimationEnd onInstantAscendAnimationEnd = new OnInstantAscendAnimationEnd(p);
                Bukkit.getPluginManager().callEvent(onInstantAscendAnimationEnd);
                return onInstantAscendAnimationEnd.isCancelled();
        }
        return false;
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
        if(plugin.areScaffoldingAllowed() && m == Material.SCAFFOLDING){
            return configHandler.getBoolean("Boost.Custom.Scaffolding.Enabled");
        }
        if(plugin.areExtravinesAllowed()){
            if(m.name().startsWith("WEEPING_VINES") || m.name().startsWith("TWISTING_VINES")){
                return configHandler.getBoolean("Boost.Custom.ExtraVines.Enabled");
            }
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
        if(plugin.areScaffoldingAllowed() && m == Material.SCAFFOLDING){
            return configHandler.getInteger("Boost.Custom.Scaffolding.Speed");
        }
        if(plugin.areExtravinesAllowed()){
            if(m.name().startsWith("WEEPING_VINES") || m.name().startsWith("TWISTING_VINES")){
                return configHandler.getInteger("Boost.Custom.ExtraVines.Speed");
            }
        }
        return configHandler.getInteger("Boost.Speed");
    }

    public boolean isInBlacklist(Player p){
        if (!configHandler.getBoolean("Blacklist.Enabled")) return false;
    
        String world = p.getWorld().getName();
        List<String> list = configHandler.getConfig().getStringList("Blacklist.Worlds");
    
        boolean isWhitelisted = configHandler.getBoolean("Blacklist.ToWhitelist");
    
        return isWhitelisted ? !list.contains(world) : list.contains(world);
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
        if(plugin.areScaffoldingAllowed() && m == Material.SCAFFOLDING){
            return configHandler.getInteger("Boost.Custom.Scaffolding.Delay");
        }
        if(plugin.areExtravinesAllowed()){
            if(m.name().startsWith("WEEPING_VINES") || m.name().startsWith("TWISTING_VINES")){
                return configHandler.getInteger("Boost.Custom.ExtraVines.Delay");
            }
        }
        return configHandler.getInteger("Boost.Delay");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player sender = event.getPlayer();
        try {
            if(sender.hasPermission(plugin.getConfigHandler().getString("Command-Permission")) && this.plugin.getUpdateChecker().checkForUpdates()) {
                sender.sendMessage("§aA newer version of the plugin §eFasterAscend §ais already available! §e(Version §c" + plugin.getUpdateChecker().getLatestVersion() + "§e)");
                sender.sendMessage("§aFor the best experience download it here: §ehttps://www.spigotmc.org/resources/faster-ascend.107195/");
            }
        } catch (Exception e) {}
    }

}
