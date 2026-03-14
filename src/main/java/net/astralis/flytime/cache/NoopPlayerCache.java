package net.astralis.flytime.cache;

import net.astralis.flytime.models.PlayerModel;

import java.util.UUID;

public class NoopPlayerCache implements PlayerCache {

    @Override
    public void put(UUID uuid, PlayerModel model) {
    }

    @Override
    public PlayerModel get(UUID uuid) {
        return null;
    }

    @Override
    public void invalidate(UUID uuid) {
    }

    @Override
    public void close() {
    }
}

