package dev.timecoding.fasterascend.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
  private int project = 0;
  
  private URL checkURL;
  
  private String newVersion = "";
  
  private JavaPlugin plugin;
  
  public UpdateChecker(JavaPlugin plugin, int id) {
    this.plugin = plugin;
    this.newVersion = plugin.getDescription().getVersion();
    this.project = id;
    try {
      this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id+"&time="+System.currentTimeMillis());
    } catch (MalformedURLException malformedURLException) {}
  }
  
  public int getProjectID() {
    return this.project;
  }
  
  public JavaPlugin getPlugin() {
    return this.plugin;
  }
  
  public String getLatestVersion() {
    return this.newVersion;
  }
  
  public String getResourceURL() {
    return "https://www.spigotmc.org/resources/" + this.project;
  }
  
  public boolean checkForUpdates() throws Exception {
    URLConnection con = this.checkURL.openConnection();
    this.newVersion = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
    return !this.plugin.getDescription().getVersion().equals(this.newVersion);
  }
}
