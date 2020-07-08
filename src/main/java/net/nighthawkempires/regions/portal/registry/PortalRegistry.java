package net.nighthawkempires.regions.portal.registry;

import com.google.common.collect.ImmutableList;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.regions.portal.PortalModel;
import org.bukkit.Location;

import java.util.Map;

public interface PortalRegistry extends Registry<PortalModel> {

    default PortalModel fromDataSection(String stringKey, DataSection data) {
        return new PortalModel(stringKey, data);
    }

    default PortalModel getPortal(String name) {
        if (portalExists(name)) {
            for (PortalModel portalModel : getPortals())
                if (portalModel.getKey().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                    return portalModel;
        } else {
            return register(new PortalModel(name));
        }

        return fromKey(name).orElseGet(() -> register(new PortalModel(name)));
    }

    default void createPortal(String name, Location firstCorner, Location secondCorner) {
        if (portalExists(name))return;
        PortalModel portalModel = getPortal(name);
        portalModel.setFirstCorner(firstCorner);
        portalModel.setSecondCorner(secondCorner);
    }

    default PortalModel getPortal(Location location) {
        for (PortalModel portalModel : getPortals()) {
            if (portalModel.inPortal(location))
                return portalModel;
        }

        return null;
    }

    default boolean isInPortal(Location location) {
        for (PortalModel portalModel : getPortals()) {
            return portalModel.inPortal(location);
        }

        return false;
    }

    default ImmutableList<PortalModel> getPortals() {
        return ImmutableList.copyOf(getRegisteredData().values());
    }

    @Deprecated
    Map<String, PortalModel> getRegisteredData();

    default boolean portalExists(String name) {
        for (PortalModel portalModel : getPortals()) {
            if (portalModel.getKey().toLowerCase().equalsIgnoreCase(name.toString()))
                return true;
        }
        return fromKey(name).isPresent();
    }
}