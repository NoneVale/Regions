package net.nighthawkempires.regions.commands;

import net.nighthawkempires.regions.region.RegionFlag;
import net.nighthawkempires.regions.region.RegionModel;
import net.nighthawkempires.regions.selection.Clipboard;
import net.nighthawkempires.regions.selection.SelectionType;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static org.bukkit.ChatColor.*;

public class RegionCommand implements CommandExecutor {

    public RegionCommand() {
        getCommandManager().registerCommands("region", new String[] {
                "ne.regions"
        });
    }

    private String[] help = new String[] {
            getMessages().getMessage(CHAT_HEADER),
            DARK_GRAY + "Command" + GRAY + ": Region    " + DARK_GRAY + "    [Optional], <Required>",
            getMessages().getMessage(CHAT_FOOTER),
            getMessages().getCommand("region", "list", "Show a list of regions"),
            getMessages().getCommand("region", "bypass <name>", "Bypass a region's flags"),
            getMessages().getCommand("region", "create <name>", "Create a region"),
            getMessages().getCommand("region", "delete <name>", "Delete a regions"),
            getMessages().getCommand("region", "info <name>", "Show a regions's info"),
            getMessages().getCommand("region", "priority <region> <priority>", "Set a region's priority"),
            getMessages().getCommand("region", "setflag <region> <flag> <result>", "Set a region flag result"),
            getMessages().getMessage(CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.regions")) {
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
                            StringBuilder regionBuilder = new StringBuilder();
                            regionBuilder.append(translateAlternateColorCodes('&', "&8 - "));

                            for (int i = 0; i < getRegionRegistry().getRegions().size(); i++) {
                                regionBuilder.append(GREEN).append(getRegionRegistry().getRegions().get(i).getKey());

                                if (i < getRegionRegistry().getRegions().size() - 1)
                                    regionBuilder.append(DARK_GRAY).append(", ");
                            }

                            if (getRegionRegistry().getRegions().isEmpty()) {
                                regionBuilder.append(RED).append("None");
                            }

                            String[] list = new String[]{
                                    getMessages().getMessage(CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8List&7: Regions"),
                                    getMessages().getMessage(CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Regions&7: "),
                                    regionBuilder.toString().trim(),
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
                        case "bypass":
                            String name = args[1];
                            if (!getRegionRegistry().regionExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a region that exists with that name."));
                                return true;
                            }

                            RegionModel regionModel = getRegionRegistry().getRegion(name);
                            if (regionModel.getBypassRegion().contains(player.getUniqueId())) {
                                regionModel.getBypassRegion().remove(player.getUniqueId());
                                player.sendMessage(getMessages().getChatMessage(GRAY + "Bypass for region " + WHITE + name + GRAY + " has been "
                                        + RED + "" + UNDERLINE + "DISABLED" + GRAY + "."));
                                return true;
                            } else {
                                regionModel.getBypassRegion().add(player.getUniqueId());
                                player.sendMessage(getMessages().getChatMessage(GRAY + "Bypass for region " + WHITE + name + GRAY + " has been "
                                        + GREEN + "" + UNDERLINE + "ENABLED" + GRAY + "."));
                                return true;
                            }
                        case "create":
                            name = args[1];
                            if (getRegionRegistry().regionExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "A region with that name already exists."));
                                return true;
                            }

                            Clipboard clipboard = getSelectionManager().getClipboard(player.getUniqueId());
                            clipboard.setSlot(player.getInventory().getHeldItemSlot());
                            clipboard.setSlotRestore(player.getInventory().getItemInMainHand());
                            clipboard.setType(SelectionType.REGION);
                            clipboard.setName(name);
                            clipboard.setSelecting(true);

                            player.getInventory().setItemInMainHand(getSelectionManager().getSelectionWand());
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Select the two locations in which you want the region to be located within.  " +
                                    "When you're done creating the region, the item you held in your hand will be restored."));
                            return true;
                        case "delete":
                            name = args[1];
                            if (!getRegionRegistry().regionExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a region that exists with that name."));
                                return true;
                            }

                            regionModel = getRegionRegistry().getRegion(name);
                            getRegionRegistry().remove(regionModel);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Region " + WHITE + name + GRAY + " has been successfully deleted."));
                            return true;
                        case "info":
                            name = args[1];
                            if (!getRegionRegistry().regionExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a region that exists with that name."));
                                return true;
                            }

                            regionModel = getRegionRegistry().getRegion(name);

                            StringBuilder flagBuilder = new StringBuilder();
                            flagBuilder.append(translateAlternateColorCodes('&', "&8 - "));
                            for (int i = 0; i < RegionFlag.values().length; i++) {
                                switch (regionModel.getFlagResult(RegionFlag.values()[i])) {
                                    case IGNORE:
                                        flagBuilder.append(GRAY);
                                        break;
                                    case DENY:
                                        flagBuilder.append(RED);
                                        break;
                                    case ALLOW:
                                        flagBuilder.append(GREEN);
                                        break;
                                }

                                flagBuilder.append(RegionFlag.values()[i].name());

                                if (i < RegionFlag.values().length - 1) {
                                    flagBuilder.append(DARK_GRAY).append(", ");
                                }
                            }

                            String[] info = new String[] {
                                    getMessages().getMessage(CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8Region Info&7: &b" + regionModel.getKey()),
                                    getMessages().getMessage(CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Priority&7: &6" + regionModel.getPriority()),
                                    translateAlternateColorCodes('&', "&8Flags&7: "),
                                    flagBuilder.toString().trim(),
                                    getMessages().getMessage(CHAT_FOOTER)
                            };
                            player.sendMessage(info);
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "priority":
                            String name = args[1];
                            if (!getRegionRegistry().regionExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a region that exists with that name."));
                                return true;
                            }

                            RegionModel regionModel = getRegionRegistry().getRegion(name);

                            if (!NumberUtils.isDigits(args[2])) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "The priority must be a valid number."));
                                return true;
                            }

                            int priority = Integer.parseInt(args[2]);

                            regionModel.setPriority(priority);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "The priority for region " + WHITE + name + GREEN + " has been set to "
                                    + GOLD + priority + GRAY + "."));
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                case 4:
                    switch (args[0].toLowerCase()) {
                        case "setflag":
                            String name = args[1];
                            if (!getRegionRegistry().regionExists(name)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I could not find a region that exists with that name."));
                                return true;
                            }

                            RegionModel regionModel = getRegionRegistry().getRegion(name);

                            RegionFlag regionFlag = null;
                            for (RegionFlag flag : RegionFlag.values()) {
                                if (flag.name().toLowerCase().equals(args[2].toLowerCase())) {
                                    regionFlag = flag;
                                }
                            }

                            if (regionFlag == null) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "That is not a valid region flag."));
                                return true;
                            }

                            RegionFlag.Result flagResult = null;
                            for (RegionFlag.Result result : RegionFlag.Result.values()) {
                                if (result.name().toLowerCase().equals(args[3].toLowerCase())) {
                                    flagResult = result;
                                }
                            }

                            if (flagResult == null) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "That is not a valid result," +
                                        " the results are ALLOW, DENY, and IGNORE."));
                                return true;
                            }

                            ChatColor resultColor = GRAY;
                            switch (flagResult) {
                                case ALLOW:
                                    resultColor = GREEN;
                                    break;
                                case DENY:
                                    resultColor = RED;
                                    break;
                            }

                            regionModel.setFlag(regionFlag, flagResult);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Region flag " + BLUE + regionFlag.name() + GRAY + " has been set to "
                                    + resultColor + flagResult.name() + GRAY + " for region " + WHITE + name + GRAY + "."));
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}