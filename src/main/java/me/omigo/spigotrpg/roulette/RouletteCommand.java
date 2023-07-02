package me.omigo.spigotrpg.roulette;

import me.omigo.spigotrpg.DatabaseHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RouletteCommand implements CommandExecutor {

    private final DatabaseHandler databaseHandler = DatabaseHandler.instance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            System.out.println("Instance is player");

            if (args.length > 0) {
                if ("items".equalsIgnoreCase(args[0])) {
                    player.sendMessage("Displaying roulette items...");
                    sendItemsWorthToPlayer(player);
                } else if ("join".equalsIgnoreCase(args[0])) {
                    player.sendMessage("Joining roulette...");
                } else {
                    player.sendMessage("Unknown subcommand. Use /roulette items or /roulette join.");
                }
            }
        }
        return true;
    }
        //todo ruletka, tracking progress i nagrody, daily challenge

        private void sendItemsWorthToPlayer (Player player){
            try {
                PreparedStatement preparedStatement = databaseHandler.connection.prepareStatement("SELECT * FROM items_worth");
                ResultSet rs = preparedStatement.executeQuery();
                System.out.println("After executing query");
                while (rs.next()) {
                    System.out.println(rs.getString("name"));
                    System.out.println(rs.getInt("worth"));
                    player.sendMessage("Item: " + rs.getString("name"));
                    player.sendMessage("Worth: " + rs.getInt("worth"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }