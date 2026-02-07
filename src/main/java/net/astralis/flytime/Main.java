package net.astralis.flytime;


import net.astralis.flytime.commands.FlyCommand;
import net.astralis.flytime.commands.FlyTimeCommand;
import net.astralis.flytime.commands.FlyTimeVoteCommand;
import net.astralis.flytime.database.CacheManager;
import net.astralis.flytime.database.DatabaseManager;
import net.astralis.flytime.listeners.CouponListener;
import net.astralis.flytime.listeners.PlayerListener;
import net.astralis.flytime.service.FlyTimeService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private DatabaseManager databaseManager;
    private CacheManager cacheManager;
    private FlyTimeService flyTimeService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Database & Cache initialization
        String mongoUri = getConfig().getString("mongodb.uri");
        String mongoDb = getConfig().getString("mongodb.database");
        String mongoColl = getConfig().getString("mongodb.collection");
        databaseManager = new DatabaseManager(mongoUri, mongoDb, mongoColl);

        String redisHost = getConfig().getString("redis.host");
        int redisPort = getConfig().getInt("redis.port");
        String redisPass = getConfig().getString("redis.password");
        cacheManager = new CacheManager(redisHost, redisPort, redisPass);

        flyTimeService = new FlyTimeService(databaseManager, cacheManager);

        // Register Commands
        getCommand("fly").setExecutor(new FlyCommand(flyTimeService));
        getCommand("flytime").setExecutor(new FlyTimeCommand(this, flyTimeService));
        getCommand("flytimevote").setExecutor(new FlyTimeVoteCommand(this, flyTimeService));

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(flyTimeService), this);
        getServer().getPluginManager().registerEvents(new CouponListener(this, flyTimeService), this);

        // Start Tick Task
        Bukkit.getScheduler().runTaskTimer(this, () -> flyTimeService.tick(), 20L, 20L);

        // Start Auto-Save Task (every 30 seconds)
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> flyTimeService.saveAllAsync(), 30 * 20L, 30 * 20L);

        getLogger().info("ʟᴜᴍᴀхɪᴀ | Flytime System aktiviert");
    }

    @Override
    public void onDisable() {
        if (flyTimeService != null) {
            flyTimeService.saveAll();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        if (cacheManager != null) {
            cacheManager.close();
        }
        getLogger().info("ʟᴜᴍᴀхɪᴀ | Flytime System deaktiviert");
    }
}