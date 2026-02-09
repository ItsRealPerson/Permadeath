package dev.itsrealperson.permadeath.data.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.api.storage.PlayerData;
import dev.itsrealperson.permadeath.api.storage.PlayerDataStorage;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Implementación de almacenamiento en MySQL para redes de servidores.
 */
public class MySQLPlayerDataStorage implements PlayerDataStorage {

    private final Main plugin;
    private HikariDataSource dataSource;

    public MySQLPlayerDataStorage(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws Exception {
        FileConfiguration config = plugin.getConfig();
        String host = config.getString("Storage.MySQL.Host", "localhost");
        int port = config.getInt("Storage.MySQL.Port", 3306);
        String database = config.getString("Storage.MySQL.Database", "permadeath");
        String username = config.getString("Storage.MySQL.Username", "root");
        String password = config.getString("Storage.MySQL.Password", "");
        boolean useSSL = config.getBoolean("Storage.MySQL.UseSSL", false);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(useSSL));

        hikariConfig.setPoolName("PermadeathMySQLPool");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setConnectionTimeout(5000);

        this.dataSource = new HikariDataSource(hikariConfig);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS pdc_players (" +
                    "name VARCHAR(16) PRIMARY KEY," +
                    "uuid VARCHAR(36)," +
                    "ban_day VARCHAR(20)," +
                    "ban_time VARCHAR(20)," +
                    "ban_cause TEXT," +
                    "coords VARCHAR(100)," +
                    "extra_hp INT DEFAULT 0," +
                    "language VARCHAR(20) DEFAULT 'SPANISH'," +
                    "last_day BIGINT DEFAULT 0," +
                    "is_locked BOOLEAN DEFAULT FALSE" +
                    ")");
        }
    }

    @Override
    public void savePlayer(PlayerData data) {
        String sql = "INSERT INTO pdc_players (name, uuid, ban_day, ban_time, ban_cause, coords, extra_hp, language, last_day, is_locked) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "uuid = VALUES(uuid), ban_day = VALUES(ban_day), ban_time = VALUES(ban_time), " +
                     "ban_cause = VALUES(ban_cause), coords = VALUES(coords), extra_hp = VALUES(extra_hp), " +
                     "language = VALUES(language), last_day = VALUES(last_day), is_locked = VALUES(is_locked)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, data.getName());
            pstmt.setString(2, data.getUuid() != null ? data.getUuid().toString() : "");
            pstmt.setString(3, data.getBanDay());
            pstmt.setString(4, data.getBanTime());
            pstmt.setString(5, data.getBanCause());
            pstmt.setString(6, data.getCoords());
            pstmt.setInt(7, data.getExtraHP());
            pstmt.setString(8, data.getLanguage().name());
            pstmt.setLong(9, data.getLastDay());
            pstmt.setBoolean(10, data.isLocked());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error al guardar jugador en MySQL: " + data.getName(), e);
        }
    }

    @Override
    public Optional<PlayerData> loadPlayer(String name) {
        String sql = "SELECT * FROM pdc_players WHERE name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlayerData data = PlayerData.builder()
                        .name(rs.getString("name"))
                        .uuid(rs.getString("uuid").isEmpty() ? null : UUID.fromString(rs.getString("uuid")))
                        .banDay(rs.getString("ban_day"))
                        .banTime(rs.getString("ban_time"))
                        .banCause(rs.getString("ban_cause"))
                        .coords(rs.getString("coords"))
                        .extraHP(rs.getInt("extra_hp"))
                        .language(Language.valueOf(rs.getString("language")))
                        .lastDay(rs.getLong("last_day"))
                        .locked(rs.getBoolean("is_locked"))
                        .build();
                return Optional.of(data);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error al cargar jugador de MySQL: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<String> getSavedPlayers() {
        List<String> players = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM pdc_players")) {
            
            while (rs.next()) {
                players.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error al listar jugadores de MySQL", e);
        }
        return players;
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
