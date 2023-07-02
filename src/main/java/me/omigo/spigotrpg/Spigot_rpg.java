package me.omigo.spigotrpg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.FileSystems;
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
        try {
            connectToDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find org.h2.Driver class");
        }
    }

    @Override
    public void onDisable() {
        disconnectFromDatabase();
    }

    private void connectToDatabase() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        FileConfiguration config = getConfig();
        String url = "jdbc:h2:file:" + getDataFolder().getAbsolutePath() + FileSystems.getDefault().getSeparator() + config.getString("database-name");

        try {
            connection = DriverManager.getConnection(url, "sa", "");
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36), name VARCHAR(16))");
                System.out.println("Created table");
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
