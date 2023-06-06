package ru.permasha.locationdescription.commads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.permasha.locationdescription.LocationDescription;
import ru.permasha.locationdescription.objects.Attribute;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class LocDescCommand implements CommandExecutor {

    LocationDescription plugin;

    public LocDescCommand(LocationDescription plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {

        if (!sender.hasPermission("")) {
            sender.sendMessage("You must be player");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(colorize("&cСинтаксис: &7/locdesc <set/remove/get> <x> <y> <z>"));
            return true;
        }

        if (!isNumeric(args[1]) || !isNumeric(args[2]) || !isNumeric(args[3])) {
            sender.sendMessage(colorize("&cИспользуйте числа в аргументах"));
            return true;
        }

        World world = Bukkit.getWorlds().get(0);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);

        Location location = new Location(world, x, y, z);
        String jsonLoc = plugin.getAttributesManager().toJsonLocation(location);

        // Getting list attributes from location if it exists
        if (args[0].equalsIgnoreCase("get")) {
            if (args.length != 4) {
                sender.sendMessage(colorize("&cСинтаксис: &7/locdesc get <x> <y> <z>"));
                return true;
            }

            if (!plugin.getAttributesManager().isBlockConsistsAttributes(location)) {
                sender.sendMessage(colorize("&cДанный блок не содержит атрибутов"));
                return true;
            }

            List<String> attributes = plugin.getAttributesManager().getListAttributes(location);
            attributes.forEach(sender::sendMessage);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 6) {
                sender.sendMessage(colorize("&cСинтаксис: &7/locdesc get <x> <y> <z> <radius> <description>"));
                return true;
            }

            if (!isNumeric(args[4])) {
                sender.sendMessage(colorize("&cРадиус должен быть числом"));
                return true;
            }
            int radius = Integer.parseInt(args[4]);

            // Getting args after first arg
            String[] desc = Arrays.copyOfRange(args, 5, args.length);
            // Create attribute
            String description = String.join(" ", desc);

            Attribute attribute = new Attribute(radius, description);
            String jsonAttribute = plugin.getAttributesManager().toJsonAttribute(attribute);

            if (!plugin.getAttributesManager().isBlockConsistsAttributes(location)) {
                // Create new array and convert it to string, and put string to database
                String[] attributesArray = plugin.getAttributesManager().addAttribute(new String[0], jsonAttribute);
                String attributes = plugin.getAttributesManager().toJsonAttributes(attributesArray);
                plugin.getDatabase().setAttributes(jsonLoc, attributes);

                plugin.getHologramManager().createHologram(location);
            } else {
                // Get string Json format from database, convert to Array, create new array and put new description
                String[] attributesArray = getAttributesArrayFromJson(jsonLoc);
                String[] attributes = plugin.getAttributesManager().addAttribute(attributesArray, jsonAttribute);

                // Convert new array to str and put it in database
                String finalStr = plugin.getAttributesManager().toJsonAttributes(attributes);
                plugin.getDatabase().setAttributes(jsonLoc, finalStr);
            }
            sender.sendMessage(colorize("&aВ локацию успешно добавлен атрибут - &f" + jsonAttribute));
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 4) {
                sender.sendMessage(colorize("&cСинтаксис: &7/locdesc remove <x> <y> <z>"));
                return true;
            }

            if (!plugin.getAttributesManager().isBlockConsistsAttributes(location)) {
                sender.sendMessage(colorize("&cДанный блок не содержит атрибутов"));
                return true;
            }

            plugin.getDatabase().removeLocation(jsonLoc);
            plugin.getHologramManager().removeHologram(location);

            sender.sendMessage(colorize("&aВсе атрибуты в локации были успешно удалены"));
            return true;
        }

        return true;
    }

    /**
     * Get array from Database of string loc
     * @param jsonLoc - Json format location
     * @return String array
     */
    private String[] getAttributesArrayFromJson(String jsonLoc) {
        String strAttributes = plugin.getDatabase().getEnteredAttributes(jsonLoc);
        return plugin.getAttributesManager().fromJsonAttributes(strAttributes);
    }

    private String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

}
