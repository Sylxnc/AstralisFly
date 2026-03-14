package net.astralis.flytime.storage;

import net.astralis.flytime.Main;
import net.astralis.flytime.cache.NoopPlayerCache;
import net.astralis.flytime.cache.PlayerCache;
import net.astralis.flytime.cache.RedisPlayerCache;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public final class StorageFactory {

    private StorageFactory() {
    }

    public static PlayerStorage createStorage(Main plugin) {
        String type = plugin.getConfig().getString("storage.type", "mongodb").toLowerCase();

        return switch (type) {
            case "mysql" -> {
                String host = plugin.getConfig().getString("storage.mysql.host", "localhost");
                int port = plugin.getConfig().getInt("storage.mysql.port", 3306);
                String database = plugin.getConfig().getString("storage.mysql.database", "flytime");
                String username = plugin.getConfig().getString("storage.mysql.username", "root");
                String password = plugin.getConfig().getString("storage.mysql.password", "");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                String upsert = "INSERT INTO flytime_players (uuid, fly_time) VALUES (?, ?) ON DUPLICATE KEY UPDATE fly_time = VALUES(fly_time)";
                yield new JdbcPlayerStorage(url, username, password, "com.mysql.cj.jdbc.Driver", upsert);
            }
            case "sqlite" -> {
                String fileName = plugin.getConfig().getString("storage.sqlite.file", "flytime.db");
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                    throw new IllegalStateException("Could not create plugin data folder");
                }

                File databaseFile = new File(dataFolder, fileName);
                String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
                String upsert = "INSERT INTO flytime_players (uuid, fly_time) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET fly_time = excluded.fly_time";
                yield new JdbcPlayerStorage(url, "", "", "org.sqlite.JDBC", upsert);
            }
            case "mongodb" -> {
                String uri = plugin.getConfig().getString("storage.mongodb.uri", "mongodb://localhost:27017");
                String database = plugin.getConfig().getString("storage.mongodb.database", "flytime");
                String collection = plugin.getConfig().getString("storage.mongodb.collection", "players");
                yield new MongoPlayerStorage(uri, database, collection);
            }
            default -> throw new IllegalArgumentException("Unsupported storage.type: " + type);
        };
    }

    public static PlayerCache createCache(Main plugin) {
        ConfigurationSection redis = plugin.getConfig().getConfigurationSection("cache.redis");
        boolean enabled = plugin.getConfig().getBoolean("cache.redis.enabled", false);
        if (!enabled || redis == null) {
            return new NoopPlayerCache();
        }

        String host = redis.getString("host", "localhost");
        int port = redis.getInt("port", 6379);
        String password = redis.getString("password", "");
        return new RedisPlayerCache(host, port, password);
    }
}

