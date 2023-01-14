
![Faster-Ascend Banner](https://user-images.githubusercontent.com/94994775/210624156-b071867d-0c67-4fd9-9f0f-edc260019c9f.png)
![CodeFactor](https://www.codefactor.io/repository/github/timecodings/fasterascend/badge)

**Hey, thanks for viewing this page! Because I found no plugin which allows to ascend faster (climb up ladders faster), I decided to create this Open-Source one! It's still a BETA what means, that there are still bugs and errors which need to be fixed. Because of this I need your help! If you found any bugs or errors or if you have wishes, please join my discord and open a ticket: https://discord.gg/mf9JNrzh | Thanks :)**

**Whats Faster Ascend?:**
With Faster Ascend you're able to speed up the climbing animation on ladders/vines and water. So perfect for any kind of server! In contrast to the version created in 2019 by another Spigot developer, there are many more options here! You may have noticed that the climbing animation cannot be accelerated with a speed effect. So I tricked this a bit using Velocity. In general, there are 2 types to choose from in my version. The liquid and the super fast way. The fluid way plays the climbing animation faster (Informations: it "boosts" the player up) and the super fast way easily teleports the player right to the end of the ladder/water/vines. You can change almost everything, give it a try.
Long story short, below you'll find a tutorial on how to install it, as well as the features and developer API! Have fun and if you like this plugin I would happy if you leave a positive rating at this plugin on my Spigot or Songoda Site ;)

**Features:**
- Climb ladders/vines/(water) up, when you touch one
- Activate/Disable the climbing animation
- Change the Blocks which should be faster climbable
- Change the climbing speed and delay (you can customize the speed and delay for every valid block individually (not required))
- Climb up ladders/vines with (SHIFT + ) RightClick/LeftClick instantly with or without animation
- Climb up ladders/vines DURING the animation with SHIFT instantly (you can activate this in the config.yml)
- Add/Set messages/sounds which should be sended when a specivic event gets triggered

**How to install:**
1. Open the releases tab, click on the newest release, download the .jar file and open your Downloads folder
2. Open your Minecraft-Server-Folder and put the plugin from your Downloads in your plugins folder of your Minecraft-Server
3. Start/Restart your server
4. After your server got started the plugin should get activated. Type /plugins in your Console to see if the plugin is working (In the List which get printed out you should see a plugin named "FasterAscend").
5. You're ready! All options are described in the config.yml (which you can find in the FasterAscend folder in the plugins folder of your Minecraft-Server) and if you need help, join my discord (or watch my German Tutorial after it got released)

**Coming Soon / TODO:**
- Cleaner Code
- Add option for faster "Climbing Down/Ascending Down"
- Your Wishes (Write me your Ideas: https://discord.gg/mf9JNrzh)

**Commands:**
/fasterascend reload - Reloads the config.yml - Needs the permission fasterascend.reload (you can change this permission in the config.yml)

**API:**
For all developers which are using my plugin: The API is simple to use! You just need to add my plugin to your Build Path / Librarys and boom, now you're able to use my Events in your Plugin (If you need support, just join my discord, how I already said at the beginning)! Here are some examples how to use my API:

    //INFORMATION: You need to register the Listener in the Main Class of your Plugin
    //Like this: this.getServer().getPluginManager().registerEvents(new FAApiTestListener(), this);

    @EventHandler
    public void onAscendBoost(OnAscendBoost e){
        e.getPlayer().sendMessage("§eOH! You got boosted!");
    }

    @EventHandler
    public void onAscendStart(OnAscendStart e){
        e.getPlayer().sendMessage("You started ascending ;)");
    }
   
    //ALL usable Event-Classes you can find in the ENUM-Class FAAPIArg or below
    OnAscendBoost | Gets triggered if a player get boosted (up a ladder/vine/water (faster climbing up = "boost"))
    OnAscendStart | Gets triggered if a normal ascend animation of a player starts
    OnAscendEnd | Gets triggered if a normal ascend animation of a player ends
    OnInstantAscendAnimationStart | Gets triggered if a "Instant" ascend animation of a player starts
    OnInstantAscendAnimationEnd | Gets triggered if a "Instant" ascend animation of a player ends
    OnInstantTeleport | Gets triggered if you get instantly teleported to the end of a ladder/vine/(water)

**Spigot-Site:** https://www.spigotmc.org/resources/faster-ascend.107195/
**Songoda-Marketplace-Site:** https://marketplace.songoda.com/marketplace/product/faster-ascend-ascend-faster-ladders-vines-water.886
