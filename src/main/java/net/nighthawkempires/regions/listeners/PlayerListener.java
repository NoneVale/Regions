package net.nighthawkempires.regions.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.regions.RegionsPlugin;
import net.nighthawkempires.regions.selection.Clipboard;
import net.nighthawkempires.regions.selection.SelectionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static org.bukkit.ChatColor.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("ne.portals") || player.hasPermission("ne.regions")) {
            if (player.getInventory().getItemInMainHand().equals(getSelectionManager().getSelectionWand())) {
                if (getSelectionManager().hasClipboard(player.getUniqueId())) {
                    Clipboard clipboard = getSelectionManager().getClipboard(player.getUniqueId());
                    if (clipboard.isSelecting()) {
                        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            Location location = event.getClickedBlock().getLocation();
                            if (clipboard.getType() == SelectionType.REGION)
                                location.setY(0);
                            clipboard.setFirstCorner(location);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have selected the first corner at "
                                    + locationName(location) + GRAY + "."));
                        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            Location location = event.getClickedBlock().getLocation();
                            if (clipboard.getType() == SelectionType.REGION)
                                location.setY(256);
                            clipboard.setSecondCorner(location);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have selected the second corner at "
                                    + locationName(location) + GRAY + "."));
                        }

                        if (clipboard.getFirstCorner() != null && clipboard.getSecondCorner() != null) {
                            clipboard.setSelecting(false);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "Please type " + GREEN + "yes" + GRAY + " or " + RED + "no"
                                    + GRAY + " to confirm the creation of this " + clipboard.getType().name().toLowerCase() + "."));
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("ne.portals") || player.hasPermission("ne.regions")) {
            if (player.getInventory().getItemInMainHand().equals(getSelectionManager().getSelectionWand())) {
                if (getSelectionManager().hasClipboard(player.getUniqueId())) {
                    Clipboard clipboard = getSelectionManager().getClipboard(player.getUniqueId());
                    if (!clipboard.isSelecting()) {
                        if (event.getMessage().toLowerCase().equals("yes")) {
                            if (clipboard.getType() == SelectionType.REGION) {
                                if (getRegionRegistry().regionExists(clipboard.getName())) {
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but a region with that name already exists."));
                                    player.getInventory().setHeldItemSlot(clipboard.getSlot());
                                    player.getInventory().setItem(clipboard.getSlot(), clipboard.getSlotRestore());
                                    getSelectionManager().deleteClipboard(player.getUniqueId());
                                    getChatFormat().setCancelled(event.getMessage(), true);
                                    return;
                                }

                                getRegionRegistry().createRegion(clipboard.getName(), clipboard.getFirstCorner(), clipboard.getSecondCorner());
                                player.sendMessage(getMessages().getChatMessage(GRAY + "Region " + WHITE + clipboard.getName() + GRAY
                                        + " has been successfully created."));
                            } else if (clipboard.getType() == SelectionType.PORTAL) {
                                if (getPortalRegistry().portalExists(clipboard.getName())) {
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but a portal with that name already exists."));
                                    player.getInventory().setHeldItemSlot(clipboard.getSlot());
                                    player.getInventory().setItem(clipboard.getSlot(), clipboard.getSlotRestore());
                                    getSelectionManager().deleteClipboard(player.getUniqueId());
                                    getChatFormat().setCancelled(event.getMessage(), true);
                                    return;
                                }

                                getPortalRegistry().createPortal(clipboard.getName(), clipboard.getFirstCorner(), clipboard.getSecondCorner());
                                player.sendMessage(getMessages().getChatMessage(GRAY + "Portal " + WHITE + clipboard.getName() + GRAY
                                        + " has been successfully created."));
                            }

                            player.getInventory().setHeldItemSlot(clipboard.getSlot());
                            player.getInventory().setItem(clipboard.getSlot(), clipboard.getSlotRestore());
                            getSelectionManager().deleteClipboard(player.getUniqueId());
                            getChatFormat().setCancelled(event.getMessage(), true);
                        } else if (event.getMessage().toLowerCase().equals("no")) {
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have cancelled the creation of "
                                    + clipboard.getType().name().toLowerCase() + " " + WHITE + clipboard.getName() + GRAY + "."));

                            player.getInventory().setHeldItemSlot(clipboard.getSlot());
                            player.getInventory().setItem(clipboard.getSlot(), clipboard.getSlotRestore());
                            getSelectionManager().deleteClipboard(player.getUniqueId());
                            getChatFormat().setCancelled(event.getMessage(), true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (bungeeEnabled()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GetServers");

            player.sendPluginMessage(RegionsPlugin.getPlugin(), "BungeeCord", out.toByteArray());
        }
    }

    private String locationName(Location location) {
        return DARK_GRAY + "[" + AQUA + location.getWorld().getName() + DARK_GRAY + ", " + GOLD + location.getX() + DARK_GRAY + ", "
                + GOLD + location.getY() + DARK_GRAY + ", " + GOLD + location.getZ() + DARK_GRAY + "]";
    }
}
