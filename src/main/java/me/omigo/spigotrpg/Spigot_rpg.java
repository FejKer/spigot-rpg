package me.omigo.spigotrpg;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public final class Spigot_rpg extends JavaPlugin {

    private Connection connection;
    private final DatabaseHandler databaseHandler = DatabaseHandler.instance;

    @Override
    public void onEnable() {
        System.out.println("Hello world from Spigot-Rpg");
        System.out.println("Registering listener");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        System.out.println("Registering commands");
        this.getCommand("roulette").setExecutor(new RouletteCommand());
        saveDefaultConfig();
        try {
            databaseHandler.connectToDatabase(getConfig(), getDataFolder());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find org.h2.Driver class");
        }
    }

    @Override
    public void onDisable() {
        databaseHandler.disconnectFromDatabase();
    }

}
