package ru.permasha.locationdescription.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.permasha.locationdescription.LocationDescription;
import ru.permasha.locationdescription.objects.Attribute;

import java.util.HashMap;
import java.util.List;

public class PlayerMove implements Listener {

    LocationDescription plugin;

    public PlayerMove(LocationDescription plugin) {
        this.plugin = plugin;
    }

    private final HashMap<Player, Integer> coolDown = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        handle(event);
    }

    private void handle(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
                plugin.getDatabase().getDataCache().keySet().forEach(locStr -> {
                    Location location = plugin.getAttributesManager().fromJsonLocation(locStr);
                    // Get list attributes of all locations
                    List<Attribute> attributes = plugin.getAttributesManager().getAttributesFromLocation(location);
                    attributes.forEach(attribute -> {
                        int radius = attribute.getRadius();
                        String message = colorize(attribute.getMessage());

                        // Check player enter to zone
                        if (!coolDown.containsKey(player)) {
                            if (to.distance(location) <= radius && from.distance(location) >= radius) {
                                player.sendMessage(message);
                                // Set cooldown for receive messages
                                setCoolDown(player, plugin.getAttributesManager().getCoolDown());
                            }
                        }
                    });

                    // Showing player holograms if he is near
                    plugin.getHologramManager().showPlayerHologram(player, location);
                });
            });
        }
    }

    private void setCoolDown(Player player, int value) {
        coolDown.put(player, value * 20 * 60);
        new BukkitRunnable() {
            @Override
            public void run() {
                int timeLeft = coolDown.get(player);
                coolDown.put(player, --timeLeft);
                if(timeLeft == 0){
                    coolDown.remove(player);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin.getPlugin(), 20, 20);
    }

    private String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
