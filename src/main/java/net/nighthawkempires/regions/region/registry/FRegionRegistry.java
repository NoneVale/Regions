package net.nighthawkempires.regions.region.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.regions.region.RegionModel;

import java.util.Map;

public class FRegionRegistry extends AbstractFileRegistry<RegionModel> implements RegionRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FRegionRegistry() {
        super("empires/regions", SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, RegionModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
