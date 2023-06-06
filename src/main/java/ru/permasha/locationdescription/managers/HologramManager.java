package ru.permasha.locationdescription.managers;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.permasha.locationdescription.LocationDescription;

public class HologramManager {

    LocationDescription plugin;
    HolographicDisplaysAPI api;

    public HologramManager(LocationDescription plugin) {
        this.plugin = plugin;
        this.api = HolographicDisplaysAPI.get(plugin.getPlugin());
        refreshHolograms();
    }

    public void refreshHolograms() {
        Bukkit.getScheduler().runTaskLater(plugin.getPlugin(), () -> {
            plugin.getDatabase().getDataCache().keySet().forEach(locStr -> {
                Location location = plugin.getAttributesManager().fromJsonLocation(locStr);
                createHologram(location);
            });
        }, 100L);
    }

    public void clearHolograms() {
        plugin.getDatabase().getDataCache().keySet().forEach(locStr -> {
            Location location = plugin.getAttributesManager().fromJsonLocation(locStr)
                    .add(0.5D, 0.5D, 0.5D);
            removeHologram(location);
        });
    }

    public void createHologram(Location location) {
        Location formattedLoc = location.add(0.5D, 0.5D, 0.5D);
        Hologram hologram =  api.createHologram(formattedLoc);
        String symbol = plugin.getAttributesManager().getSymbol();
        hologram.getLines().appendText(symbol);
    }

    public void removeHologram(Location location) {
        api.getHolograms().forEach(hologram -> {
            if (location.equals(hologram.getPosition().toLocation())) {
                hologram.delete();
            }
        });
    }

    public Hologram getHologramOnLocation(Location location) {
        for (Hologram hologram : api.getHolograms()) {
            if (location.equals(hologram.getPosition().toLocation())) {
                return hologram;
            }
        }
        return null;
    }

    public void showPlayerHologram(Player player, Location location) {
        Location formattedLoc = location.add(0.5D, 0.5D, 0.5D);
        Hologram hologram = getHologramOnLocation(formattedLoc);
        int radius = plugin.getAttributesManager().getRadius();
        if (hologram != null) {
            VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();
            if (location.distance(player.getLocation()) <= radius) {
                visibilitySettings.setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
            } else {
                visibilitySettings.setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
            }
        }
    }
}
