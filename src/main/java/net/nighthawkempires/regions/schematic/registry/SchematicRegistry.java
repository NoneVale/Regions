package net.nighthawkempires.regions.schematic.registry;

import com.google.common.collect.ImmutableList;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.regions.schematic.SchematicModel;
import org.bukkit.Location;

import java.util.Map;

public interface SchematicRegistry extends Registry<SchematicModel> {
    default SchematicModel fromDataSection(String stringKey, DataSection data) {
        return new SchematicModel(stringKey, data);
    }

    default SchematicModel getSchematic(String name) {
        if (schematicExists(name)) {
            for (SchematicModel schematicModel : getSchematics())
                if (schematicModel.getKey().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                    return schematicModel;
        } else {
            return null;
        }
        return null;
    }

    default SchematicModel createSchematic(String name, Location firstCorner, Location secondCorner) {
        if (schematicExists(name))return null;
        SchematicModel schematicModel = new SchematicModel(name, firstCorner, secondCorner);

        register(schematicModel);
        return schematicModel;
    }

    default ImmutableList<SchematicModel> getSchematics() {
        return ImmutableList.copyOf(getRegisteredData().values());
    }

    @Deprecated
    Map<String, SchematicModel> getRegisteredData();

    default boolean schematicExists(String name) {
        for (SchematicModel schematicModel : getSchematics()) {
            if (schematicModel.getKey().toLowerCase().equalsIgnoreCase(name.toString()))
                return true;
        }
        return fromKey(name).isPresent();
    }
}
