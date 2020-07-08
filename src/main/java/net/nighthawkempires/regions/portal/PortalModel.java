package net.nighthawkempires.regions.portal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.core.location.SavedLocation;
import net.nighthawkempires.regions.RegionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static net.nighthawkempires.regions.RegionsPlugin.*;

public class PortalModel implements Model {

    private String key;

    private int priority;

    private SavedLocation firstCorner;
    private SavedLocation secondCorner;

    private PortalType portalType;
    private String destination;
    private SavedLocation destinationLocation;

    private List<UUID> insidePortal;
    private List<UUID> cooldown;

    public PortalModel(String key) {
        this.key = key;

        this.priority = 1;

        this.firstCorner = null;
        this.secondCorner = null;

        this.portalType = PortalType.LOCATION;
        this.destination = "";
        this.destinationLocation = null;

        this.insidePortal = Lists.newArrayList();
        this.cooldown = Lists.newArrayList();
    }

    public PortalModel(String key, DataSection data) {
        this.key = key;

        this.priority = data.getInt("priority");

        if (data.isSet("first-corner"))
            this.firstCorner = new SavedLocation(data.getMap("first-corner"));
        if (data.isSet("second-corner"))
            this.secondCorner = new SavedLocation(data.getMap("second-corner"));

        this.portalType = PortalType.valueOf(data.getString("portal-type"));
        if (data.isSet("destination"))
            this.destination = data.getString("destination");

        if (data.isSet("destination-location"))
            this.destinationLocation = new SavedLocation(data.getMap("destination-location"));

        this.insidePortal = Lists.newArrayList();
        this.cooldown = Lists.newArrayList();

        Random random = new Random();
        double[][] aye = new double[][] {{random.nextDouble(),random.nextDouble(),random.nextDouble()}, {1,2,3}};
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        getPortalRegistry().register(this);
    }

    public void setFirstCorner(Location location) {
        this.firstCorner = SavedLocation.fromLocation(location, false);
        getPortalRegistry().register(this);
    }

    public void setSecondCorner(Location location) {
        this.secondCorner = SavedLocation.fromLocation(location, false);
        getPortalRegistry().register(this);
    }

    public PortalType getPortalType() {
        return this.portalType;
    }

    public void setPortalType(PortalType portalType) {
        this.portalType = portalType;
        getPortalRegistry().register(this);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
        getPortalRegistry().register(this);
    }

    public Location getDestinationLocation() {
        return this.destinationLocation.toLocation();
    }

    public void setDestinationLocation(Location location) {
        this.destinationLocation = SavedLocation.fromLocation(location, false);
        getPortalRegistry().register(this);
    }

    public List<UUID> getInsidePortal() {
        return this.insidePortal;
    }

    public boolean inPortal(Location location) {
        Location firstCorner = this.firstCorner.toLocation();
        Location secondCorner = this.secondCorner.toLocation();

        if (location.getWorld() != firstCorner.getWorld()) return false;

        int x1 = firstCorner.getBlockX(), x2 = secondCorner.getBlockX(), x3 = location.getBlockX();
        int y1 = firstCorner.getBlockY(), y2 = secondCorner.getBlockY(), y3 = location.getBlockY();
        int z1 = firstCorner.getBlockZ(), z2 = secondCorner.getBlockZ(), z3 = location.getBlockZ();

        if (x1 > x2)  {
            x2 = x1;
            x1 = secondCorner.getBlockX();
        }

        if (y1 > y2) {
            y2 = y1;
            y1 = secondCorner.getBlockY();
        }

        if (z1 > z2) {
            z2 = z1;
            z1 = secondCorner.getBlockZ();
        }

        return (x1 <= x3 && x3 <= x2) && (y1 <= y3 && y3 <= y2) && (z1 <= z3 && z3 <= z2);
    }

    public boolean hasCooldown(Player player) {
        return this.cooldown.contains(player.getUniqueId());
    }

    public void setCooldown(Player player, int seconds) {
        this.cooldown.add(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(),
                () -> this.cooldown.remove(player.getUniqueId()),
                20 * 5);
    }

    public String getKey() {
        return this.key;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("priority", this.priority);

        if (firstCorner != null)
            map.put("first-corner", this.firstCorner.serialize());
        if (secondCorner != null)
            map.put("second-corner", this.secondCorner.serialize());

        map.put("portal-type", this.portalType.name());

        if (this.destination != null || !this.destination.isEmpty())
            map.put("destination", this.destination);

        if (this.destinationLocation != null)
            map.put("destination-location", this.destinationLocation.serialize());

        return map;
    }
}