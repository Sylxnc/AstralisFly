package net.astralis.flytime.service;

import net.astralis.flytime.database.CacheManager;
import net.astralis.flytime.database.DatabaseManager;
import net.astralis.flytime.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlyTimeService {

    private static final String PREFIX = "§x§2§2§D§3§E§Eʟ§x§2§B§B§D§F§0ᴜ§x§3§3§A§7§F§2ᴍ§x§3§B§8§2§F§6ᴀ§x§6§0§7§3§F§6x§x§8§4§6§4§F§6ɪ§x§A§8§5§5§F§7ᴀ §7✦ ";
    private final Map<UUID, PlayerModel> models = new ConcurrentHashMap<>();
    private final DatabaseManager databaseManager;
    private final CacheManager cacheManager;

    public FlyTimeService(DatabaseManager databaseManager, CacheManager cacheManager) {
        this.databaseManager = databaseManager;
        this.cacheManager = cacheManager;
    }

    public void loadPlayer(UUID uuid) {
        PlayerModel model = cacheManager.getCachedPlayer(uuid);
        if (model == null) {
            model = databaseManager.loadPlayer(uuid);
        }
        
        if (model == null) {
            model = new PlayerModel(0);
        }
        
        models.put(uuid, model);
        cacheManager.cachePlayer(uuid, model);
    }

    public void saveAndUnloadPlayer(UUID uuid) {
        PlayerModel model = models.remove(uuid);
        if (model != null) {
            databaseManager.savePlayer(uuid, model);
            cacheManager.cachePlayer(uuid, model);
        }
    }

    public void savePlayerAsync(UUID uuid) {
        PlayerModel model = models.get(uuid);
        if (model != null) {
            Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("AstralisFly"), () -> {
                databaseManager.savePlayer(uuid, model);
                cacheManager.cachePlayer(uuid, model);
            });
        }
    }

    public void saveAll() {
        models.forEach(databaseManager::savePlayer);
    }

    public void saveAllAsync() {
        models.forEach((uuid, model) -> {
            Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("AstralisFly"), () -> {
                databaseManager.savePlayer(uuid, model);
                cacheManager.cachePlayer(uuid, model);
            });
        });
    }

    public PlayerModel getModel(UUID uuid) {
        return models.get(uuid);
    }

    public void tick() {
        for (Map.Entry<UUID, PlayerModel> entry : models.entrySet()) {
            PlayerModel model = entry.getValue();
            if (model.isEnabled()) {
                model.tick();
                
                if (!model.hasFlyTime()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅᴇɪɴᴇ ғʟʏᴛɪᴍᴇ ɪsᴛ ᴀʙɢᴇʟᴀᴜғᴇɴ!");
                    }
                }
            }
        }
    }
}
