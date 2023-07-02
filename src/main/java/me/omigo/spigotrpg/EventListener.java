package me.omigo.spigotrpg;

import net.md_5.bungee.api.ChatColor;
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
        event.setJoinMessage(ChatColor.GOLD + player.getName() + " dołączył do zabawy :D");
        if (!databaseHandler.isPlayerInDatabase(player.getUniqueId())) {
            databaseHandler.insertPlayerIntoDatabase(player);
        } else {
            player.sendMessage("Witamy z powrotem.");
        }
    }
}
