package net.astralis.flytime.cache;

import com.google.gson.Gson;
import net.astralis.flytime.models.PlayerModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

public class RedisPlayerCache implements PlayerCache {

    private static final String KEY_PREFIX = "flytime:";

    private final JedisPool jedisPool;
    private final Gson gson;

    public RedisPlayerCache(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(16);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(1);

        String redisPassword = (password == null || password.isBlank()) ? null : password;
        this.jedisPool = new JedisPool(poolConfig, host, port, null, redisPassword);
        this.gson = new Gson();
    }

    @Override
    public void put(UUID uuid, PlayerModel model) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(KEY_PREFIX + uuid, gson.toJson(model));
        }
    }

    @Override
    public PlayerModel get(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(KEY_PREFIX + uuid);
            return json == null ? null : gson.fromJson(json, PlayerModel.class);
        }
    }

    @Override
    public void invalidate(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(KEY_PREFIX + uuid);
        }
    }

    @Override
    public void close() {
        jedisPool.close();
    }
}

