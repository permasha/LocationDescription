package ru.permasha.locationdescription.managers;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import ru.permasha.locationdescription.LocationDescription;
import ru.permasha.locationdescription.objects.Attribute;
import ru.permasha.locationdescription.objects.LocationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributesManager {

    LocationDescription plugin;

    public AttributesManager(LocationDescription plugin) {
        this.plugin = plugin;
    }

    /**
     * @return List attributes from location
     */
    public List<Attribute> getAttributesFromLocation(Location location) {
        if (isBlockConsistsAttributes(location)) {
            List<Attribute> attributes = new ArrayList<>();
            getListAttributes(location).forEach(atStr -> {
                Attribute attribute = fromJsonAttribute(atStr);
                attributes.add(attribute);
            });
            return attributes;
        }
        return null;
    }

    /**
     * @return List with json attribute strings
     */
    public List<String> getListAttributes(Location location) {
        if (isBlockConsistsAttributes(location)) {
            String jsonLoc = toJsonLocation(location);
            String attributesJson = plugin.getDatabase().getEnteredAttributes(jsonLoc);
            String[] attributesArray = fromJsonAttributes(attributesJson);
            return Arrays.stream(attributesArray).toList();
        }
        return null;
    }

    /**
     * Check location for attributes
     */
    public boolean isBlockConsistsAttributes(Location location) {
        String jsonLoc = toJsonLocation(location);
        return plugin.getDatabase().getEnteredAttributes(jsonLoc) != null;
    }

    /**
     * @return Radius for holograms
     */
    public int getRadius() {
        return plugin.getPlugin().getConfig().getInt("locdesc.radius");
    }

    /**
     * @return Symbol for holograms
     */
    public String getSymbol() {
        return colorize(plugin.getPlugin().getConfig().getString("locdesc.symbol"));
    }

    public Location fromJsonLocation(String json) {
        return new Gson().fromJson(json, LocationModel.class).toLocation();
    }

    public String toJsonLocation(Location location) {
        return new Gson().toJson(new LocationModel(location));
    }

    public Attribute fromJsonAttribute(String json) {
        return new Gson().fromJson(json, Attribute.class).toAttribute();
    }

    public String toJsonAttribute(Attribute attribute) {
        return new Gson().toJson(attribute);
    }

    public String[] fromJsonAttributes(String json) {
        return new Gson().fromJson(json, String[].class);
    }

    public String toJsonAttributes(String[] arrayAttributes) {
        return new Gson().toJson(arrayAttributes);
    }

    public String[] addAttribute(String[] arrayAttributes, String attribute) {
        List<String> attributes = new ArrayList<>(Arrays.asList(arrayAttributes));
        attributes.add(attribute);
        return attributes.toArray(String[]::new);
    }

    public String[] removeAttribute(String[] arrayAttributes, int index) {
        List<String> attributes = new ArrayList<>(Arrays.asList(arrayAttributes));
        attributes.remove(index);
        return attributes.toArray(String[]::new);
    }

    private String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
