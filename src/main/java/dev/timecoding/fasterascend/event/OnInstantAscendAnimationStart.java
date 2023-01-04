package dev.timecoding.fasterascend.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnInstantAscendAnimationStart extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();

  private Player p;

  private boolean cancelled;

  public OnInstantAscendAnimationStart(Player p) {
    this.cancelled = false;
    this.p = p;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return this.p;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
