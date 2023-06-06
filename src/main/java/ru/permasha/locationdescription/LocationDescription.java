package ru.permasha.locationdescription;

import org.bukkit.plugin.java.JavaPlugin;
import ru.permasha.locationdescription.commads.LocDescCommand;
import ru.permasha.locationdescription.database.Database;
import ru.permasha.locationdescription.database.SQLite;
import ru.permasha.locationdescription.listeners.PlayerMove;
import ru.permasha.locationdescription.managers.AttributesManager;
import ru.permasha.locationdescription.managers.HologramManager;

public class LocationDescription {

    private JavaPlugin plugin;

    private AttributesManager attributesManager;
    private HologramManager hologramManager;
    private Database database;

    public LocationDescription(JavaPlugin plugin) {
        this.init(plugin);
    }

    private void init(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new PlayerMove(this), plugin);
        plugin.getCommand("locdesc").setExecutor(new LocDescCommand(this));
        plugin.saveDefaultConfig();

        attributesManager = new AttributesManager(this);
        hologramManager = new HologramManager(this);

        database = new SQLite(this);
        database.load();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public AttributesManager getAttributesManager() {
        return attributesManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public Database getDatabase() {
        return database;
    }

}
