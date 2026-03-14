package net.astralis.flytime.service;

import net.astralis.flytime.Main;
import net.astralis.flytime.cache.PlayerCache;
import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.storage.PlayerStorage;
import net.astralis.flytime.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlyTimeService {

    private final Main plugin;
    private final Map<UUID, PlayerModel> models = new ConcurrentHashMap<>();
    private final PlayerStorage storage;
    private final PlayerCache cache;

    public FlyTimeService(Main plugin, PlayerStorage storage, PlayerCache cache) {
        this.plugin = plugin;
        this.storage = storage;
        this.cache = cache;
    }

    public void loadPlayer(UUID uuid) {
        PlayerModel model = cache.get(uuid);
        if (model == null) {
            model = storage.loadPlayer(uuid);
        }

        if (model == null) {
            model = new PlayerModel(0);
        }

        models.put(uuid, model);
        cache.put(uuid, model);
    }

    public PlayerModel getOrLoadModel(UUID uuid) {
        PlayerModel model = models.get(uuid);
        if (model != null) {
            return model;
        }

        loadPlayer(uuid);
        return models.getOrDefault(uuid, new PlayerModel(0));
    }

    public void addFlyTime(UUID uuid, long seconds) {
        PlayerModel model = getOrLoadModel(uuid);
        model.addFlyTime(seconds);
        models.put(uuid, model);
    }

    public void saveAndUnloadPlayer(UUID uuid) {
        PlayerModel model = models.remove(uuid);
        if (model != null) {
            storage.savePlayer(uuid, model);
            cache.put(uuid, model);
        }
    }

    public void savePlayerAsync(UUID uuid) {
        PlayerModel model = models.get(uuid);
        if (model != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> savePlayerSync(uuid, model));
        }
    }

    private void savePlayerSync(UUID uuid, PlayerModel model) {
        storage.savePlayer(uuid, model);
        cache.put(uuid, model);
    }

    public void saveAll() {
        models.forEach(this::savePlayerSync);
    }

    public void saveAllAsync() {
        models.forEach((uuid, model) -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> savePlayerSync(uuid, model)));
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
                        player.sendMessage(Chat.PREFIX + "Your fly time has expired.");
                    }
                }
            }
        }
    }

    public void close() {
        saveAll();
        cache.close();
        storage.close();
    }
}
