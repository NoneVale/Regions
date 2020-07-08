package net.nighthawkempires.regions.portal.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.location.PublicLocationModel;
import net.nighthawkempires.regions.portal.PortalModel;

import java.util.Map;

public class FPortalRegistry extends AbstractFileRegistry<PortalModel> implements PortalRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FPortalRegistry() {
        super("empires/portals", SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, PortalModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
