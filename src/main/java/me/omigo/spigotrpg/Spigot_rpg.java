package me.omigo.spigotrpg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Spigot_rpg extends JavaPlugin {

    private Connection connection;

    @Override
    public void onEnable() {
        System.out.println("Hello world from Spigot-Rpg");
        saveDefaultConfig();
        connectToDatabase();
    }

    @Override
    public void onDisable() {
        disconnectFromDatabase();
    }

    private void connectToDatabase() {
        FileConfiguration config = getConfig();
        String url = "jdbc:h2:file:" + getDataFolder().getAbsolutePath() + "/" + config.getString("database-name");

        try {
            connection = DriverManager.getConnection(url, "sa", "");
            // Create table example
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36), name VARCHAR(16))");
            }
        } catch (SQLException e) {
            getLogger().severe("Could not connect to the database: " + e.getMessage());
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            getLogger().severe("Could not close database connection: " + e.getMessage());
        }
    }
}
