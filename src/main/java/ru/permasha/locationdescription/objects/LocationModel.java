package ru.permasha.locationdescription.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationModel {
    int x, y, z;
    String worldName;

    public LocationModel(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.worldName = location.getWorld().getName();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }
}
