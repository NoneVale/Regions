package net.nighthawkempires.regions.region.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.regions.portal.PortalModel;
import net.nighthawkempires.regions.region.RegionModel;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public interface RegionRegistry extends Registry<RegionModel> {
    default RegionModel fromDataSection(String stringKey, DataSection data) {
        return new RegionModel(stringKey, data);
    }

    default RegionModel getRegion(String name) {

        if (regionExists(name)) {
            for (RegionModel regionModel : getRegions())
                if (regionModel.getKey().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                    return regionModel;
        } else {
            return register(new RegionModel(name));
        }

        return fromKey(name).orElseGet(() -> register(new RegionModel(name)));
    }

    default void createRegion(String name, Location firstCorner, Location secondCorner) {
        if (regionExists(name))return;
        RegionModel regionModel = getRegion(name);
        regionModel.setFirstCorner(firstCorner);
        regionModel.setSecondCorner(secondCorner);
    }

    default RegionModel getRegion(Location location) {
        for (RegionModel regionModel : getRegions()) {
            if (regionModel.inRegion(location))
                return regionModel;
        }

        return null;
    }

    default boolean isInRegion(Location location) {
        for (RegionModel regionModel : getRegions()) {
            return regionModel.inRegion(location);
        }

        return false;
    }

    default RegionModel getObeyRegion(Location location) {
        List<RegionModel> regions = Lists.newArrayList();
        for (RegionModel regionModel : getRegions()) {
            if (regionModel.inRegion(location))
                regions.add(regionModel);
        }

        int highestPriority = -1;
        for (RegionModel regionModel : regions) {
            if (highestPriority == -1 || regionModel.getPriority() > highestPriority)
                highestPriority = regionModel.getPriority();
        }

        RegionModel obey = null;
        for (RegionModel regionModel : regions) {
            if (regionModel.getPriority() == highestPriority)
                obey = regionModel;
        }
        return obey;
    }

    default RegionModel getRegion(Chunk chunk) {
        for (RegionModel regionModel : getRegions()) {
            if (regionModel.inChunk(chunk))
                return regionModel;
        }

        return null;
    }

    default boolean isInChunk(Chunk chunk) {
        for (RegionModel regionModel : getRegions()) {
            return regionModel.inChunk(chunk);
        }

        return false;
    }

    default ImmutableList<RegionModel> getRegions() {
        return ImmutableList.copyOf(getRegisteredData().values());
    }

    @Deprecated
    Map<String, RegionModel> getRegisteredData();

    default boolean regionExists(String name) {
        for (RegionModel regionModel : getRegions()) {
            if (regionModel.getKey().toLowerCase().equalsIgnoreCase(name.toString()))
                return true;
        }
        return fromKey(name).isPresent();
    }
}
