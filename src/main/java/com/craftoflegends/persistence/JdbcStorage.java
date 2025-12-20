package com.craftoflegends.persistence;

import com.craftoflegends.match.Match;
import com.craftoflegends.session.PlayerSession;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class JdbcStorage implements DataStorage {

    private final Logger logger;
    protected HikariDataSource dataSource;

    protected JdbcStorage(Logger logger) {
        this.logger = logger;
    }

    protected abstract HikariConfig buildConfig();
    protected abstract String sessionUpsertSql();
    protected abstract String matchUpsertSql();

    @Override
    public void initialize() {
        this.dataSource = new HikariDataSource(buildConfig());
        createTables();
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void saveSession(PlayerSession session) {
        final String sql = sessionUpsertSql();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, session.getPlayerId().toString());
            statement.setString(2, session.getChampion());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.severe("Unable to persist session: " + exception.getMessage());
        }
    }

    @Override
    public Optional<PlayerSession> loadSession(UUID playerId) {
        final String sql = "SELECT champion FROM player_sessions WHERE player_uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new PlayerSession(playerId, resultSet.getString("champion")));
                }
            }
        } catch (SQLException exception) {
            logger.severe("Unable to load session: " + exception.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void recordMatchResult(Match match) {
        final String sql = matchUpsertSql();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, match.getId());
            statement.setString(2, match.getState().name());
            statement.setLong(3, match.getStartedAt() != null ? match.getStartedAt().getEpochSecond() : 0);
            statement.setLong(4, match.getEndedAt() != null ? match.getEndedAt().getEpochSecond() : Instant.now().getEpochSecond());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.warning("Unable to record match result: " + exception.getMessage());
        }
    }

    @Override
    public void recordHeartbeat(int matchId, long tick) {
        final String sql = "INSERT INTO match_heartbeats(match_id, tick) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, matchId);
            statement.setLong(2, tick);
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.warning("Unable to record heartbeat: " + exception.getMessage());
        }
    }

    private void createTables() {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_sessions (player_uuid VARCHAR(36) PRIMARY KEY, champion VARCHAR(64))").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS match_results (id INT PRIMARY KEY, state VARCHAR(32), started_at BIGINT, ended_at BIGINT)").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS match_heartbeats (match_id INT, tick BIGINT, recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)").execute();
        } catch (SQLException exception) {
            logger.severe("Failed to prepare storage tables: " + exception.getMessage());
        }
    }
}
