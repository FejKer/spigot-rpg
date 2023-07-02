package me.omigo.spigotrpg.roulette;

import me.omigo.spigotrpg.DatabaseHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RouletteHandler {
    public static RouletteHandler instance = new RouletteHandler();

    private static Roulette roulette = null;

    private void disposeRoulette() {

    }

    public synchronized void joinRoulette(Player player) {
        player.sendMessage("Joining roulette...");
        if (roulette == null) {
            roulette = new Roulette();
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (DatabaseHandler.instance.itemValues.containsKey(itemInHand.getType())) {
            roulette.calculatePlayerChances(player, itemInHand);
            roulette.addItem(itemInHand.clone());
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            player.sendMessage(ChatColor.RED + "Twój item w ręce nie może wziąć udziału w losowaniu.");
        }
    }
}
