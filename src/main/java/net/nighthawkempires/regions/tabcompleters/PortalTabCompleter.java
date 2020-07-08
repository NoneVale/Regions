package net.nighthawkempires.regions.tabcompleters;

import com.google.common.collect.Lists;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.regions.RegionsPlugin;
import net.nighthawkempires.regions.portal.PortalType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class PortalTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.portals")) {
                return completions;
            }

            switch (args.length) {
                case 1:
                    List<String> arggs = Lists.newArrayList("list", "create", "delete", "info", "setdest");
                    StringUtil.copyPartialMatches(args[0], arggs, completions);
                    Collections.sort(completions);
                    return completions;
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "delete":
                        case "info":
                        case "setdest":
                            StringUtil.copyPartialMatches(args[1], RegionsPlugin.getPortalRegistry().getRegisteredData().keySet(), completions);
                            return completions;
                    }
                case 3:
                    if (args[0].toLowerCase().equals("setdest")) {
                        arggs = Lists.newArrayList();
                        for (PortalType type : PortalType.values()) {
                            arggs.add(type.name().toLowerCase());
                        }
                        StringUtil.copyPartialMatches(args[2], arggs, completions);
                        return completions;
                    }
                case 4:
                    if (args[0].toLowerCase().equals("setdest") && args[2].toLowerCase().equals("warp")) {
                        StringUtil.copyPartialMatches(args[1], CorePlugin.getPublicLocationRegistry().getPublicLocations().getWarps(), completions);
                        return completions;
                    }
            }
        }
        return completions;
    }
}
