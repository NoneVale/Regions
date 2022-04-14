package net.nighthawkempires.regions.schematic.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.regions.schematic.SchematicModel;

import java.util.Map;

public class FSchematicRegistry extends AbstractFileRegistry<SchematicModel> implements SchematicRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FSchematicRegistry() {
        super("empires/schematics", SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, SchematicModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
