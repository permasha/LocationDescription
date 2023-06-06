package ru.permasha.locationdescription.database;

import org.bukkit.Bukkit;
import ru.permasha.locationdescription.LocationDescription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

public abstract class Database {

    LocationDescription plugin;
    Connection connection;
    public String table = "blocks_table";
    public HashMap<String, String> dataCache = new HashMap<>();

    public Database(LocationDescription instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
            connection = getSQLConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String jsonLoc = rs.getString("location");
                    dataCache.put(jsonLoc, rs.getString("attributes"));
                }
                close(ps, rs);
            } catch (SQLException ex) {
                plugin.getPlugin().getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
            }
        });
    }

    public String getEnteredAttributes(String locationStr) {
        if (!dataCache.isEmpty() && dataCache.containsKey(locationStr)) {
            return dataCache.get(locationStr);
        }
        return null;
    }

    /**
     * Delete a location specified by the string
     *
     * @param string location
     */
    public void removeLocation(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM " + table + " WHERE location = '"+string+"';");
            ps.executeUpdate();
            dataCache.remove(string);
        } catch (SQLException ex) {
            plugin.getPlugin().getLogger().log(Level.SEVERE, sqlConnectionExecute(), ex);
        } finally {
            close(ps, conn);
        }
    }

    /**
     * Set JSONArray attributes of string location
     *
     * @param location, attributes
     */
    public void setAttributes(String location, String attributes) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (location,attributes) VALUES(?,?)");
            ps.setString(1, location);
            ps.setString(2, attributes);
            ps.executeUpdate();
            dataCache.put(location, attributes);
        } catch (SQLException ex) {
            plugin.getPlugin().getLogger().log(Level.SEVERE, sqlConnectionExecute(), ex);
        } finally {
            close(ps, conn);
        }
    }

    public HashMap<String, String> getDataCache() {
        return dataCache;
    }

    public void close(PreparedStatement ps, Connection conn) {
        try {
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
        } catch (SQLException ex) {
            plugin.getPlugin().getLogger().log(Level.SEVERE, sqlConnectionClose(), ex);
        }
    }

    public void close(PreparedStatement ps, ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            errorClose(ex);
        }
    }

    private String sqlConnectionExecute(){
        return "Couldn't execute SQL statement: ";
    }
    private String sqlConnectionClose(){
        return "Failed to close SQL connection: ";
    }

    private void errorExecute(Exception ex){
        plugin.getPlugin().getLogger().log(Level.SEVERE, "Couldn't execute SQL statement: ", ex);
    }
    private void errorClose(Exception ex){
        plugin.getPlugin().getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
    }
}
