package net.nighthawkempires.regions.region;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.core.location.SavedLocation;
import net.nighthawkempires.regions.portal.PortalType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.nighthawkempires.regions.RegionsPlugin.getRegionRegistry;

public class RegionModel implements Model {

    private String key;

    private int priority;

    private SavedLocation firstCorner;
    private SavedLocation secondCorner;

    private List<UUID> bypassRegion;
    private List<UUID> insideRegion;

    private HashMap<RegionFlag, RegionFlag.Result> regionFlags;

    public RegionModel(String key) {
        this.key = key;

        this.priority = 1;

        this.firstCorner = null;
        this.secondCorner = null;

        this.bypassRegion = Lists.newArrayList();
        this.insideRegion = Lists.newArrayList();

        this.regionFlags = Maps.newHashMap();
    }

    public RegionModel(String key, DataSection data) {
        this.key = key;

        this.priority = data.getInt("priority");

        if (data.isSet("first-corner"))
            this.firstCorner = new SavedLocation(data.getMap("first-corner"));
        if (data.isSet("second-corner"))
            this.secondCorner = new SavedLocation(data.getMap("second-corner"));

        this.bypassRegion = Lists.newArrayList();
        this.insideRegion = Lists.newArrayList();

        this.regionFlags = Maps.newHashMap();
        if (data.isSet("flags")) {
            Map<String, Object> flagMap = data.getMap("flags");
            for (String flag : flagMap.keySet()) {
                RegionFlag regionFlag = RegionFlag.valueOf(flag.toUpperCase());
                RegionFlag.Result result = RegionFlag.Result.valueOf(flagMap.get(flag).toString().toUpperCase());
                this.regionFlags.put(regionFlag, result);
            }
        }
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        getRegionRegistry().register(this);
    }

    public void setFirstCorner(Location location) {
        this.firstCorner = SavedLocation.fromLocation(location, false);
        getRegionRegistry().register(this);
    }

    public void setSecondCorner(Location location) {
        this.secondCorner = SavedLocation.fromLocation(location, false);
        getRegionRegistry().register(this);
    }

    public List<UUID> getBypassRegion() {
        return this.bypassRegion;
    }

    public List<UUID> getInsideRegion() {
        return this.insideRegion;
    }

    public ImmutableMap<RegionFlag, RegionFlag.Result> getRegionFlags() {
        return ImmutableMap.copyOf(this.regionFlags);
    }

    public void setFlag(RegionFlag regionFlag, RegionFlag.Result result) {
        this.regionFlags.put(regionFlag, result);
        getRegionRegistry().register(this);
    }

    public RegionFlag.Result getFlagResult(RegionFlag regionFlag) {
        if (!this.regionFlags.containsKey(regionFlag)) return RegionFlag.Result.IGNORE;
        return this.regionFlags.get(regionFlag);
    }

    public boolean inRegion(Location location) {
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

    public boolean inChunk(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = 64;
                Block block = chunk.getBlock(x, y, z);
                return inRegion(block.getLocation());
            }
        }

        return false;
    }

    public String getKey() {
        return this.key;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("priority", this.priority);

        if (this.firstCorner != null)
            map.put("first-corner", this.firstCorner.serialize());
        if (this.secondCorner != null)
            map.put("second-corner", this.secondCorner.serialize());

        Map<String, String> flagMap = Maps.newHashMap();
        for (RegionFlag regionFlag : this.regionFlags.keySet()) {
            if (getFlagResult(regionFlag) != RegionFlag.Result.IGNORE) {
                flagMap.put(regionFlag.name(), getFlagResult(regionFlag).name());
            }
        }
        map.put("flags", flagMap);

        return map;
    }
}
