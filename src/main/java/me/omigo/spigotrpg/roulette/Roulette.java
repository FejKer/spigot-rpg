package me.omigo.spigotrpg.roulette;

import me.omigo.spigotrpg.database.DatabaseHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Roulette {
    private final List<ItemStack> itemList = new ArrayList<>();
    private final Map<Player, Double> playerChances = new HashMap<>();
    private final Random random = new Random();
    private Player winner;

    public List<ItemStack> getItemList() {
        return itemList;
    }

    public Map<Player, Double> getPlayerChances() {
        return playerChances;
    }

    public Player getWinner() {
        return winner;
    }

    public synchronized void calculatePlayerChances(Player player, ItemStack itemStack) {
        Double calculated =
                (double) itemStack.getAmount() * DatabaseHandler.instance.itemValues.get(itemStack.getType());
        System.out.println(player.getName() + " " + calculated);
        if (playerChances.containsKey(player)) {
            playerChances.computeIfPresent(player, (k, v) -> v + calculated);
        } else {
            playerChances.put(player, calculated);
        }
        player.sendMessage("Twoje szanse w losowaniu: " + playerChances.get(player));
        playerChances.put(player, calculated);
    }

    public synchronized void addItem(ItemStack itemStack) {
        itemList.add(itemStack);
    }

    public synchronized Player selectPlayer() {
        Double valuesInMap = 0.0;
        for (Double d : playerChances.values()) {
            valuesInMap += d;
        }
        Double randomValue = random.nextDouble(valuesInMap);
        Double sum = 0.0;
        for (Player p : playerChances.keySet()) {
            if ((sum += playerChances.get(p)) >= randomValue) {
                System.out.println("winner is " + p.getName());
                winner = p;
                return winner;
            }
            sum += playerChances.get(p);
        }
        return null;
    }
}
