package net.astralis.flytime.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.astralis.flytime.models.PlayerModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class JdbcPlayerStorage implements PlayerStorage {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS flytime_players (uuid VARCHAR(36) PRIMARY KEY, fly_time BIGINT NOT NULL)";

    private final HikariDataSource dataSource;
    private final String upsertSql;

    public JdbcPlayerStorage(String jdbcUrl, String username, String password, String driverClassName, String upsertSql) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        if (driverClassName != null && !driverClassName.isBlank()) {
            hikariConfig.setDriverClassName(driverClassName);
        }
        hikariConfig.setMaximumPoolSize(8);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setConnectionTimeout(5000L);

        this.dataSource = new HikariDataSource(hikariConfig);
        this.upsertSql = upsertSql;
        createTableIfMissing();
    }

    private void createTableIfMissing() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not initialize SQL table flytime_players", exception);
        }
    }

    @Override
    public PlayerModel loadPlayer(UUID uuid) {
        String sql = "SELECT fly_time FROM flytime_players WHERE uuid = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new PlayerModel(resultSet.getLong("fly_time"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load player from SQL storage", exception);
        }
    }

    @Override
    public void savePlayer(UUID uuid, PlayerModel model) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(upsertSql)) {
            statement.setString(1, uuid.toString());
            statement.setLong(2, model.getFlyTime());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save player into SQL storage", exception);
        }
    }

    @Override
    public void close() {
        dataSource.close();
    }
}

