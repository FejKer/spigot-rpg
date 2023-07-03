package me.omigo.spigotrpg.roulette;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.omigo.spigotrpg.DatabaseHandler;
import me.omigo.spigotrpg.Spigot_rpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RouletteHandler {
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    public static RouletteHandler instance = new RouletteHandler();
    private static Roulette roulette = null;

    private void disposeRoulette() {
        System.out.println("Disposing roulette");
        try {
            PreparedStatement preparedStatement = DatabaseHandler.instance.connection.prepareStatement("INSERT INTO roulettes (items, itemValues, winner) VALUES (?, ?, ?)");
            String items = DatabaseHandler.instance.objectMapper.writeValueAsString(roulette
                    .getItemList()
                    .stream()
                    .map(itemStack -> {
                        return itemStack.getType() + "_" + itemStack.getAmount();
                    }).collect(Collectors.toList()));
            String players = DatabaseHandler.instance.objectMapper.writeValueAsString(roulette
                    .getPlayerChances()
                    .keySet()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            String winner = DatabaseHandler.instance.objectMapper.writeValueAsString(roulette.getWinner().getName());
            System.out.println(items + "\n" + players + "\n" + winner);
            preparedStatement.setString(1, items);
            preparedStatement.setString(2, players);
            preparedStatement.setString(3, winner);
            preparedStatement.executeUpdate();
            System.out.println("Saved roulette to db\n" + items + "\n" + players + "\n" + winner);
        } catch (JsonProcessingException | SQLException e) {
            System.out.println(e.getMessage());
        }
        roulette = null;
    }

    public synchronized void joinRoulette(Player player) {
        player.sendMessage("Joining roulette...");
        if (roulette == null) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "Startujemy ruletkę! 30 sekund do losowania. Chętni wpisują " + ChatColor.DARK_AQUA + "/roulette join");
            roulette = new Roulette();
            EXECUTOR.schedule(() -> {
                Bukkit.getScheduler().runTask(Spigot_rpg.instance, () -> {
                    Player winner = roulette.selectPlayer();
                    Bukkit.broadcastMessage("W ruletce wygrał " + player.getName());
                    Location location = winner.getLocation();
                    location.getBlock().setType(Material.CHEST);
                    Chest chest = (Chest) location.getBlock().getState();
                    chest.update();
                    Inventory inv = chest.getInventory();
                    System.out.println("Items: " + roulette.getItemList());
                    inv.setContents(roulette.getItemList().toArray(new ItemStack[0]));
                    disposeRoulette();
                });
            }, 30, TimeUnit.SECONDS);
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (DatabaseHandler.instance.itemValues.containsKey(itemInHand.getType())) {
            roulette.calculatePlayerChances(player, itemInHand);
            roulette.addItem(itemInHand.clone());
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            player.sendMessage(ChatColor.RED + "Twój item w ręce nie może wziąć udziału w losowaniu.");
        }
        System.out.println("Starting roulette");
    }

    public void sendHistoryToPlayer(Player player) throws SQLException {
        PreparedStatement preparedStatement = DatabaseHandler.instance.connection.prepareStatement("SELECT * FROM roulettes");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            player.sendMessage("===============================================");
            player.sendMessage("Id ruletki: " + rs.getInt("id"));
            player.sendMessage("Itemy w grze: " + rs.getString("items"));
            player.sendMessage("Zwyciezca: " + rs.getString("winner"));
        }
    }
}
