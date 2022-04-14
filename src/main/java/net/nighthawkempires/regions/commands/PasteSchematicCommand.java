package net.nighthawkempires.regions.commands;

import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.regions.RegionsPlugin;
import net.nighthawkempires.regions.schematic.SchematicModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static org.bukkit.ChatColor.*;

public class PasteSchematicCommand implements CommandExecutor {

    private String[] help = {
            getMessages().getMessage(Messages.CHAT_HEADER),
            ChatColor.translateAlternateColorCodes('&', "&8Command&7: Paste Schematic   &8-   [Optional], <Required>"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
            getMessages().getCommand("pasteschematic", "help", "Show this help menu."),
            getMessages().getCommand("pasteschematic", "<name>", "Paste a schematic."),
            getMessages().getMessage(Messages.CHAT_FOOTER)
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("ne.schematic")) {
                player.sendMessage(getMessages().getChatTag(Messages.NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0 -> {
                    player.sendMessage(help);
                    return true;
                }
                case 1 -> {
                    String arg = args[0];
                    switch (arg.toLowerCase()) {
                        case "help" -> {
                            player.sendMessage(help);
                            return true;
                        }
                        default -> {
                            if (!getSchematicRegistry().schematicExists(arg)) {
                                player.sendMessage(getMessages().getChatMessage(RED + "A schematic with that name does not exist."));
                                return true;
                            }

                            SchematicModel schematicModel = getSchematicRegistry().getSchematic(arg);
                            String[][][] schem = schematicModel.getField();

                            int no = 0;
                            for (int y = 0; y < schem.length; y++) {
                                String[][] xBlocks = schem[y];
                                for (int x = 0; x < xBlocks.length; x++) {
                                    String[] zBlocks = xBlocks[x];
                                    for (int z = 0; z < zBlocks.length; z++) {
                                        Location location = player.getLocation().add(x, y, z);
                                        Block block = location.getBlock();
                                        BlockData blockData = Bukkit.createBlockData(zBlocks[z]);

                                        if (!(block.getType().isAir() && blockData.getMaterial().isAir())) {
                                            no++;
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(RegionsPlugin.getPlugin(), () -> {
                                                block.setBlockData(blockData);
                                            }, 2L * no);
                                        }
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
                default -> {
                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                }
            }
        }
        return false;
    }
}