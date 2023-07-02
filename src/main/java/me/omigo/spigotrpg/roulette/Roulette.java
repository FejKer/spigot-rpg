package me.omigo.spigotrpg.roulette;

import me.omigo.spigotrpg.DatabaseHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Roulette {
    private Integer id;
    private List<ItemStack> itemList = new ArrayList<>();
    private Map<Player, Double> playerChances = new HashMap<>();

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public synchronized void calculatePlayerChances(Player player, ItemStack itemStack) {
        Double calculated =
                (double) itemStack.getAmount() * DatabaseHandler.instance.itemValues.get(itemStack.getType());
        System.out.println(player.getName() + " " + calculated);
        player.sendMessage("Twoje szanse w losowaniu: " + calculated);
        playerChances.put(player, calculated);
    }

    public synchronized void addItem(ItemStack itemStack) {
        itemList.add(itemStack);
    }
}
