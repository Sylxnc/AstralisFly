package net.astralis.flytime.database;

import com.google.gson.Gson;
import net.astralis.flytime.models.PlayerModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class CacheManager {

    private final JedisPool jedisPool;
    private final Gson gson = new Gson();
    private static final String KEY_PREFIX = "flytime:";

    public CacheManager(String host, int port, String password) {
        this.jedisPool = new JedisPool(host, port, null, password);
    }

    public void cachePlayer(UUID uuid, PlayerModel model) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = gson.toJson(model);
            jedis.set(KEY_PREFIX + uuid.toString(), json);
        }
    }

    public PlayerModel getCachedPlayer(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(KEY_PREFIX + uuid.toString());
            if (json == null) {
                return null;
            }
            return gson.fromJson(json, PlayerModel.class);
        }
    }

    public void deleteCachedPlayer(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(KEY_PREFIX + uuid.toString());
        }
    }

    public void close() {
        jedisPool.close();
    }
}
