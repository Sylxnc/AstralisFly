package net.astralis.flytime;

import net.astralis.flytime.cache.PlayerCache;
import net.astralis.flytime.commands.BrigadierCommandRegistrar;
import net.astralis.flytime.listeners.CouponListener;
import net.astralis.flytime.listeners.PlayerListener;
import net.astralis.flytime.listeners.VoteListener;
import net.astralis.flytime.service.FlyTimeService;
import net.astralis.flytime.storage.PlayerStorage;
import net.astralis.flytime.storage.StorageFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private PlayerStorage storage;
    private PlayerCache cache;
    private FlyTimeService flyTimeService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        storage = StorageFactory.createStorage(this);
        cache = StorageFactory.createCache(this);
        flyTimeService = new FlyTimeService(this, storage, cache);

        new BrigadierCommandRegistrar(this, flyTimeService).register();

        getServer().getPluginManager().registerEvents(new PlayerListener(flyTimeService), this);
        getServer().getPluginManager().registerEvents(new CouponListener(this, flyTimeService), this);
        if (getServer().getPluginManager().isPluginEnabled("NuVotifier")) {
            getServer().getPluginManager().registerEvents(new VoteListener(this, flyTimeService), this);
            getLogger().info("NuVotifier hook enabled.");
        } else {
            getLogger().info("NuVotifier not found. Vote rewards are disabled.");
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> flyTimeService.tick(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> flyTimeService.saveAllAsync(), 30 * 20L, 30 * 20L);

        getLogger().info("AstralisFly enabled.");
    }

    @Override
    public void onDisable() {
        if (flyTimeService != null) {
            flyTimeService.close();
        }
        getLogger().info("AstralisFly disabled.");
    }
}