package me.omigo.spigotrpg;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.FileSystems;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseHandler {
    Map<String, Integer> itemValues = new HashMap<>();
    public static DatabaseHandler instance = new DatabaseHandler();
    public Connection connection;

    private void populateHashMap() {
        itemValues.put("DIAMOND", 500);
        itemValues.put("IRON_INGOT", 150);
        itemValues.put("GOLD_INGOT", 200);
        itemValues.put("EMERALD", 350);
        itemValues.put("COAL", 50);
        itemValues.put("LAPIS_LAZULI", 100);
        itemValues.put("REDSTONE", 80);
        itemValues.put("NETHERITE_INGOT", 1000);
        itemValues.put("QUARTZ", 120);
        itemValues.put("OBSIDIAN", 300);
    }

    public void connectToDatabase(FileConfiguration config, File dataFolder) throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:file:" + dataFolder.getAbsolutePath() + FileSystems.getDefault().getSeparator() + config.getString("database-name");
        populateHashMap();
        try {
            connection = DriverManager.getConnection(url, "sa", "");
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36), name VARCHAR(16))");
                System.out.println("Created table players");
                stmt.execute("CREATE TABLE IF NOT EXISTS items_worth (name VARCHAR(20), worth INT)");
                System.out.println("Created table items_worth");
                stmt.execute("TRUNCATE TABLE items_worth");
                for (String item : itemValues.keySet()) {
                    int worth = itemValues.get(item);
                    stmt.execute("INSERT INTO items_worth (name, worth) VALUES ('" + item + "', " + worth + ")");
                    System.out.println("Inserting into db: " + item + " " + worth);
                }
                System.out.println("Inserted item values into items_worth table");

                stmt.execute("CREATE TABLE IF NOT EXISTS roulettes (id INT auto_increment)");

                /*String selectAllQuery = "SELECT * FROM players";
                try (ResultSet rs = stmt.executeQuery(selectAllQuery)) {
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        String name = rs.getString("name");
                        System.out.println("Player UUID: " + uuid + ", Name: " + name);
                    }
                }*/
            }
        } catch (SQLException e) {
            System.out.println("Could connect to database " + e.getMessage());
        }
    }

    public void disconnectFromDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Could not close database connection: " + e.getMessage());
        }
    }

    public void insertPlayerIntoDatabase(Player player) {
        String query = "INSERT INTO players VALUES ( ?, ? )";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, String.valueOf(player.getUniqueId()));
            pstmt.setString(2, player.getName());
            pstmt.executeUpdate();
            player.sendMessage("Zarejestrowano Cię do bazy danych.");
        } catch (SQLException e) {
            System.out.println("Could not check if player is in database: " + e.getMessage());
        }
    }

    public boolean isPlayerInDatabase(UUID playerUuid) {
        String query = "SELECT * FROM players WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, playerUuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Could not check if player is in database: " + e.getMessage());
        }
        return false;
    }
}
