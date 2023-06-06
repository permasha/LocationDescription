package ru.permasha.locationdescription.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.permasha.locationdescription.LocationDescription;
import ru.permasha.locationdescription.objects.Attribute;

import java.util.HashMap;
import java.util.List;

public class PlayerMove implements Listener {

    LocationDescription plugin;

    public PlayerMove(LocationDescription plugin) {
        this.plugin = plugin;
    }

    private HashMap<Player, Integer> coolDown = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
                plugin.getDatabase().getDataCache().keySet().forEach(locStr -> {
                    Location location = plugin.getAttributesManager().fromJsonLocation(locStr);
                    plugin.getHologramManager().showPlayerHologram(player, location);

                    List<Attribute> attributes = plugin.getAttributesManager().getAttributesFromLocation(location);
                    attributes.forEach(attribute -> {
                        int radius = attribute.getRadius();
                        String message = colorize(attribute.getMessage());

                        if (location.distanceSquared(player.getLocation()) <= radius * radius) {
                            player.sendMessage(message);
                        }
                    });
                });
            });
        }
    }

    private String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
