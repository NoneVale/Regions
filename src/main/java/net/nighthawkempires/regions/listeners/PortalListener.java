package net.nighthawkempires.regions.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.nighthawkempires.core.location.PublicLocationModel;
import net.nighthawkempires.regions.RegionsPlugin;
import net.nighthawkempires.regions.portal.PortalModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static org.bukkit.ChatColor.*;

public class PortalListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        PortalModel portal = getPortalRegistry().getPortal(player.getLocation());

        if (portal != null) {
            if (portal.hasCooldown(player)) return;

            if (portal.getPortalType() == null) {
                portal.setCooldown(player, 5);
                player.sendMessage(getMessages().getChatMessage(GRAY + "This portal does not have a set destination."));
                return;
            }

            switch (portal.getPortalType()) {
                case LOCATION:
                    if (portal.getDestinationLocation() == null) {
                        portal.setCooldown(player, 5);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "This portal does not have a set destination."));
                        return;
                    }

                    player.teleport(portal.getDestinationLocation());
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have teleported through the portal."));
                    portal.setCooldown(player, 5);
                    break;
                case SERVER:
                    if (!bungeeEnabled()) {
                        portal.setCooldown(player, 5);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but BungeeCord must be enabled for this feature."));
                        return;
                    }

                    if (portal.getDestination() == null || portal.getDestination().isEmpty()) {
                        portal.setCooldown(player, 5);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "This portal does not have a set destination."));
                        return;
                    }

                    if (!getBungeeData().servers.contains(portal.getDestination())) {
                        portal.setCooldown(player, 5);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "This portal does not have a set destination."));
                        return;
                    }

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(portal.getDestination());

                    player.sendPluginMessage(RegionsPlugin.getPlugin(), "BungeeCord", out.toByteArray());
                    player.sendMessage(getMessages().getChatMessage(GRAY + "Sending you to server " + WHITE + portal.getDestination() + GRAY + "."));
                    portal.setCooldown(player, 5);
                    break;
                case WARP:
                    if (portal.getDestination() == null || portal.getDestination().isEmpty()) {
                        portal.setCooldown(player, 5);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "This portal does not have a set destination."));
                        return;
                    }

                    PublicLocationModel locations = getPublicLocationRegistry().getPublicLocations();
                    if (!locations.warpExists(portal.getDestination())) {
                        portal.setCooldown(player, 5);
                        player.sendMessage(getMessages().getChatMessage(GRAY + "This portal does not have a set destination."));
                        return;
                    }

                    player.teleport(locations.getWarp(portal.getDestination()));
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You have been teleported to warp "
                            + WHITE + portal.getDestination() + GRAY + "."));
                    portal.setCooldown(player, 5);
                    break;
            }
        }
    }
}
