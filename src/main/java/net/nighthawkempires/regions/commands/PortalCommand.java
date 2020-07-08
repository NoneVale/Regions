package net.nighthawkempires.regions.commands;

import net.nighthawkempires.regions.portal.PortalModel;
import net.nighthawkempires.regions.portal.PortalType;
import net.nighthawkempires.regions.selection.Clipboard;
import net.nighthawkempires.regions.selection.SelectionType;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.RED;

public class PortalCommand implements CommandExecutor {

    public PortalCommand() {
        getCommandManager().registerCommands("portal", new String[] {
                "ne.portals"
        });
    }

    private String[] help = new String[] {
            getMessages().getMessage(CHAT_HEADER),
            DARK_GRAY + "Command" + GRAY + ": Portal    " + DARK_GRAY + "    [Optional], <Required>",
            getMessages().getMessage(CHAT_FOOTER),
            getMessages().getCommand("portal", "list", "Show a list of portals"),
            getMessages().getCommand("portal", "create <name>", "Show a list of portals"),
            getMessages().getCommand("portal", "delete <name>", "Show a list of portals"),
            getMessages().getCommand("portal", "info <name>", "Show a list of portals"),
            getMessages().getCommand("portal", "setdest <portal> <type> <destination>", "Show a list of portals"),
            getMessages().getMessage(CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.portals")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    player.sendMessage(help);
                    return true;
                case 1:
                switch (args[0].toLowerCase()) {
                    case "list":
                        StringBuilder portalBuilder = new StringBuilder();
                        portalBuilder.append(translateAlternateColorCodes('&', "&8 - "));

                        for (int i = 0; i < getPortalRegistry().getPortals().size(); i++) {
                            portalBuilder.append(GREEN).append(getPortalRegistry().getPortals().get(i).getKey());

                            if (i < getPortalRegistry().getPortals().size() - 1)
                                portalBuilder.append(DARK_GRAY).append(", ");
                        }

                        if (getPortalRegistry().getPortals().isEmpty()) {
                            portalBuilder.append(RED).append("None");
                        }

                        String[] list = new String[]{
                                getMessages().getMessage(CHAT_HEADER),
                                translateAlternateColorCodes('&', "&8List&7: Portals"),
                                getMessages().getMessage(CHAT_FOOTER),
                                translateAlternateColorCodes('&', "&8Portals&7: "),
                                portalBuilder.toString().trim(),
                                getMessages().getMessage(CHAT_FOOTER)
                        };
                        player.sendMessage(list);
                        return true;
                    default:
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                        return true;
                }
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "create":
                            String name = args[1];
                            if (getPortalRegistry().portalExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "A portal with that name already exists."));
                                return true;
                            }

                            Clipboard clipboard = getSelectionManager().getClipboard(player.getUniqueId());
                            clipboard.setSlot(player.getInventory().getHeldItemSlot());
                            clipboard.setSlotRestore(player.getInventory().getItemInMainHand());
                            clipboard.setType(SelectionType.PORTAL);
                            clipboard.setName(name);
                            clipboard.setSelecting(true);

                            player.getInventory().setItemInMainHand(getSelectionManager().getSelectionWand());
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Select the two locations in which you want the portal to be located within.  " +
                                    "When you're done creating the portal, the item you held in your hand will be restored."));
                            return true;
                        case "delete":
                            name = args[1];
                            if (!getPortalRegistry().portalExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a portal that exists with that name."));
                                return true;
                            }

                            PortalModel portalModel = getPortalRegistry().getPortal(name);
                            getPortalRegistry().remove(portalModel);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Portal " + WHITE + name + GRAY + " has been successfully deleted."));
                            return true;
                        case "info":
                            name = args[1];
                            if (!getPortalRegistry().portalExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a portal that exists with that name."));
                                return true;
                            }

                            portalModel = getPortalRegistry().getPortal(name);

                            String destination = (portalModel.getDestination() != null || !portalModel.getDestination().isEmpty()
                                    ? portalModel.getDestination() : RED + "None");
                            if (portalModel.getPortalType() == PortalType.LOCATION){
                                if (portalModel.getDestinationLocation() != null)
                                    destination = locationName(portalModel.getDestinationLocation());
                            }

                            String[] info = new String[] {
                                    getMessages().getMessage(CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8Portal Info&7: &b" + portalModel.getKey()),
                                    getMessages().getMessage(CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Priority&7: &6" + portalModel.getPriority()),
                                    translateAlternateColorCodes('&', "&8Destination Type&7: " + portalModel.getPortalType().name()),
                                    translateAlternateColorCodes('&', "&8Destination&9: " + destination),
                                    getMessages().getMessage(CHAT_FOOTER)
                            };
                            player.sendMessage(info);
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                case 4:
                    switch (args[0].toLowerCase()) {
                        case "setdest":
                            String name = args[1];
                            if (!getPortalRegistry().portalExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a portal that exists with that name."));
                                return true;
                            }

                            PortalModel portalModel = getPortalRegistry().getPortal(name);

                            PortalType portalType = null;
                            for (PortalType type : PortalType.values()) {
                                if (type.name().toLowerCase().equals(args[2].toLowerCase())) {
                                    portalType = type;
                                }
                            }

                            if (portalType == null) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "That is not a valid destination type," +
                                        " the types are currently LOCATION, SERVER, or WARP."));
                                return true;
                            }

                            portalModel.setPortalType(portalType);
                            if (portalType == PortalType.LOCATION) {
                                portalModel.setDestinationLocation(player.getLocation());
                            } else {
                                portalModel.setDestination(args[3]);
                            }

                            String destination = (portalModel.getDestination() != null || !portalModel.getDestination().isEmpty()
                                    ? portalModel.getDestination() : RED + "None");
                            if (portalModel.getPortalType() == PortalType.LOCATION){
                                if (portalModel.getDestinationLocation() != null)
                                    destination = locationName(portalModel.getDestinationLocation());
                            }

                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have set the destination for portal " + WHITE + name + GRAY
                                    + " to " + DARK_GRAY + "[" + GREEN + portalType.name() + DARK_GRAY + " | " + destination + DARK_GRAY + "]" + GRAY + "."));
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    }
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }

    private String locationName(Location location) {
        return DARK_GRAY + "[" + AQUA + location.getWorld().getName() + DARK_GRAY + ", " + GOLD + location.getX() + DARK_GRAY + ", "
                + GOLD + location.getY() + DARK_GRAY + ", " + GOLD + location.getZ() + DARK_GRAY + "]";
    }
}