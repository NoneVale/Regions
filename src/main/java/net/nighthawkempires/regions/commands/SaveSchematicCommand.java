package net.nighthawkempires.regions.commands;

import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.regions.selection.Clipboard;
import net.nighthawkempires.regions.selection.SelectionType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static org.bukkit.ChatColor.*;

public class SaveSchematicCommand implements CommandExecutor {

    private String[] help = {
            getMessages().getMessage(Messages.CHAT_HEADER),
            ChatColor.translateAlternateColorCodes('&', "&8Command&7: Save Schematic   &8-   [Optional], <Required>"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
            getMessages().getCommand("saveschematic", "help", "Show this help menu."),
            getMessages().getCommand("saveschematic", "<name>", "Save a schematic."),
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
                            if (getSchematicRegistry().schematicExists(arg)) {
                                player.sendMessage(getMessages().getChatMessage(RED + "A schematic with that name already exists."));
                                return true;
                            }

                            Clipboard clipboard = getSelectionManager().getClipboard(player.getUniqueId());
                            clipboard.setSlot(player.getInventory().getHeldItemSlot());
                            clipboard.setSlotRestore(player.getInventory().getItemInMainHand());
                            clipboard.setType(SelectionType.SCHEMATIC);
                            clipboard.setName(arg);
                            clipboard.setSelecting(true);

                            player.getInventory().setItemInMainHand(getSelectionManager().getSelectionWand());
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Select the two locations in which you want the schematic to be located within.  " +
                                    "When you're done creating the schematic, the item you held in your hand will be restored."));
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
