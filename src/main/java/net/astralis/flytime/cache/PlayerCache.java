package net.astralis.flytime.cache;

import net.astralis.flytime.models.PlayerModel;

import java.util.UUID;

public interface PlayerCache extends AutoCloseable {

    void put(UUID uuid, PlayerModel model);

    PlayerModel get(UUID uuid);

    void invalidate(UUID uuid);

    @Override
    void close();
}

