package net.nighthawkempires.regions.util;

import net.nighthawkempires.regions.region.RegionModel;
import org.bukkit.entity.Player;

import static net.nighthawkempires.regions.RegionsPlugin.getRegionRegistry;
import static net.nighthawkempires.regions.region.RegionFlag.*;
import static net.nighthawkempires.regions.region.RegionFlag.Result.DENY;

public class RegionUtil {

    public static boolean canPVP(Player player) {
        RegionModel region = getRegionRegistry().getObeyRegion(player.getLocation());

        if (region != null) {
            return region.getFlagResult(DAMAGE) != DENY && region.getFlagResult(PVP) != DENY;
        }

        return true;
    }

    public static boolean canUseRaceAbilities(Player player) {
        RegionModel region = getRegionRegistry().getObeyRegion(player.getLocation());

        if (region != null) {
            return region.getFlagResult(RACE_ABILITIES) != DENY;
        }

        return true;
    }
}