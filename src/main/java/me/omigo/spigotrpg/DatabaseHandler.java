package me.omigo.spigotrpg;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.FileSystems;
import java.sql.*;

public class DatabaseHandler {

    public static DatabaseHandler instance = new DatabaseHandler();
    public Connection connection;

    public void connectToDatabase(FileConfiguration config, File dataFolder) throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:file:" + dataFolder.getAbsolutePath() + FileSystems.getDefault().getSeparator() + config.getString("database-name");

        try {
            connection = DriverManager.getConnection(url, "sa", "");
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36), name VARCHAR(16))");
                System.out.println("Created table");
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
}
