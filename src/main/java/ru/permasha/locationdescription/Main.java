package ru.permasha.locationdescription;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    LocationDescription module;

    @Override
    public void onEnable() {
        module = new LocationDescription(this);
    }

    @Override
    public void onDisable() {
        module.getHologramManager().clearHolograms();
    }

}
