package net.astralis.flytime.storage;

import net.astralis.flytime.models.PlayerModel;

import java.util.UUID;

public interface PlayerStorage extends AutoCloseable {

    PlayerModel loadPlayer(UUID uuid);

    void savePlayer(UUID uuid, PlayerModel model);

    @Override
    void close();
}

