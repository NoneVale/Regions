package net.nighthawkempires.regions.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.nighthawkempires.regions.RegionsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import static net.nighthawkempires.regions.RegionsPlugin.*;

public class BungeeListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord"))return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("GetServers")) {
            String[] serverList = in.readUTF().split(", ");

            for (String server : serverList) {
                getBungeeData().servers.clear();
                getBungeeData().servers.add(server);
            }
        }
    }
}
