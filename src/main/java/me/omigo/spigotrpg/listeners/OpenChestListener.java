package me.omigo.spigotrpg.listeners;

import me.omigo.spigotrpg.roulette.RouletteHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OpenChestListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && event.getInventory().getHolder() instanceof Chest) {
            Player player = (Player) event.getPlayer();
            Location chestLocation = ((Chest) event.getInventory().getHolder()).getLocation();

            if (RouletteHandler.playerLocationMap.containsKey(player) && RouletteHandler.playerLocationMap.get(player).equals(chestLocation)) {
                if (isInventoryEmpty(event.getInventory())) {
                    chestLocation.getBlock().setType(Material.AIR);
                    RouletteHandler.playerLocationMap.remove(player);
                }
            }
        }
    }

    private boolean isInventoryEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }
}
