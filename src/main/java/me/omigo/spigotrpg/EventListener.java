package me.omigo.spigotrpg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EventListener implements Listener {
    private final DatabaseHandler databaseHandler = DatabaseHandler.instance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage(player.getName() + " dołączył do zabawy :D");
        if (!isPlayerInDatabase(player.getUniqueId())) {
            insertPlayerIntoDatabase(player);
        }
    }

    private void insertPlayerIntoDatabase(Player player) {
        String query = "INSERT INTO players VALUES ( ?, ? )";
        try (PreparedStatement pstmt = databaseHandler.connection.prepareStatement(query)) {
            pstmt.setString(1, String.valueOf(player.getUniqueId()));
            pstmt.setString(2, player.getName());
            pstmt.executeUpdate();
            player.sendMessage("Zarejestrowano Cię do bazy danych.");
        } catch (SQLException e) {
            System.out.println("Could not check if player is in database: " + e.getMessage());
        }
    }

    private boolean isPlayerInDatabase(UUID playerUuid) {
        String query = "SELECT * FROM players WHERE uuid = ?";
        try (PreparedStatement pstmt = databaseHandler.connection.prepareStatement(query)) {
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
