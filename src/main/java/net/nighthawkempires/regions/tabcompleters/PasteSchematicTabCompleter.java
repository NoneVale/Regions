package net.nighthawkempires.regions.tabcompleters;

import com.google.common.collect.Lists;
import net.nighthawkempires.regions.RegionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class PasteSchematicTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player player) {

            if (!player.hasPermission("ne.schematic")) {
                return completions;
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], RegionsPlugin.getSchematicRegistry().loadAllFromDb().keySet(), completions);
                Collections.sort(completions);
                return completions;
            }
        }
        return completions;
    }
}