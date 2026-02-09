package dev.itsrealperson.permadeath.data.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.itsrealperson.permadeath.api.Language;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.storage.PlayerData;
import dev.itsrealperson.permadeath.api.storage.PlayerDataStorage;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLitePlayerDataStorage implements PlayerDataStorage {

    private final Main plugin;
    private HikariDataSource dataSource;

    public SQLitePlayerDataStorage(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws Exception {
        File dbFile = new File(plugin.getDataFolder(), "permadeath.db");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setPoolName("PermadeathSQLitePool");
        config.setMaximumPoolSize(1); // SQLite solo permite una escritura simultánea
        config.setConnectionTestQuery("SELECT 1");

        this.dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS pdc_players (" +
                    "name TEXT PRIMARY KEY," +
                    "uuid TEXT," +
                    "ban_day TEXT," +
                    "ban_time TEXT," +
                    "ban_cause TEXT," +
                    "coords TEXT," +
                    "extra_hp INTEGER DEFAULT 0," +
                    "language TEXT DEFAULT 'SPANISH'," +
                    "last_day INTEGER DEFAULT 0" +
                    ")");
        }
    }

    @Override
    public void savePlayer(PlayerData data) {
        String sql = "INSERT OR REPLACE INTO pdc_players (name, uuid, ban_day, ban_time, ban_cause, coords, extra_hp, language, last_day) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error al guardar jugador en SQLite: " + data.getName(), e);
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
                        .build();
                return Optional.of(data);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error al cargar jugador de SQLite: " + name, e);
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
            plugin.getLogger().log(Level.SEVERE, "Error al listar jugadores de SQLite", e);
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
