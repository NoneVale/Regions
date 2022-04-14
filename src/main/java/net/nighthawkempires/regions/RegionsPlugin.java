package net.nighthawkempires.regions;

import net.nighthawkempires.regions.commands.PasteSchematicCommand;
import net.nighthawkempires.regions.commands.PortalCommand;
import net.nighthawkempires.regions.commands.RegionCommand;
import net.nighthawkempires.regions.commands.SaveSchematicCommand;
import net.nighthawkempires.regions.data.BungeeData;
import net.nighthawkempires.regions.listeners.BungeeListener;
import net.nighthawkempires.regions.listeners.PlayerListener;
import net.nighthawkempires.regions.listeners.PortalListener;
import net.nighthawkempires.regions.listeners.RegionListener;
import net.nighthawkempires.regions.portal.registry.FPortalRegistry;
import net.nighthawkempires.regions.portal.registry.PortalRegistry;
import net.nighthawkempires.regions.region.registry.FRegionRegistry;
import net.nighthawkempires.regions.region.registry.RegionRegistry;
import net.nighthawkempires.regions.schematic.registry.FSchematicRegistry;
import net.nighthawkempires.regions.schematic.registry.SchematicRegistry;
import net.nighthawkempires.regions.selection.SelectionManager;
import net.nighthawkempires.regions.tabcompleters.PasteSchematicTabCompleter;
import net.nighthawkempires.regions.tabcompleters.PortalTabCompleter;
import net.nighthawkempires.regions.tabcompleters.RegionTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

public class RegionsPlugin extends JavaPlugin {

    private static BungeeData bungeeData;

    private static PortalRegistry portalRegistry;
    private static RegionRegistry regionRegistry;
    private static SchematicRegistry schematicRegistry;

    private static SelectionManager selectionManager;

    private static Plugin plugin;

    public void onEnable() {
        plugin = this;

        bungeeData = new BungeeData();

        portalRegistry = new FPortalRegistry();
        getPortalRegistry().loadAllFromDb();
        regionRegistry = new FRegionRegistry();
        getRegionRegistry().loadAllFromDb();
        schematicRegistry = new FSchematicRegistry();

        selectionManager = new SelectionManager();

        registerCommands();
        registerListeners();
        registerTabCompleters();
    }

    public void onDisable() {

    }

    public void registerCommands() {
        this.getCommand("pasteschematic").setExecutor(new PasteSchematicCommand());
        this.getCommand("portal").setExecutor(new PortalCommand());
        this.getCommand("region").setExecutor(new RegionCommand());
        this.getCommand("saveschematic").setExecutor(new SaveSchematicCommand());
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new PortalListener(), this);
        pm.registerEvents(new RegionListener(), this);

        if (bungeeEnabled()) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
        }
    }

    public void registerTabCompleters() {
        this.getCommand("pasteschematic").setTabCompleter(new PasteSchematicTabCompleter());
        this.getCommand("portal").setTabCompleter(new PortalTabCompleter());
        this.getCommand("region").setTabCompleter(new RegionTabCompleter());
    }

    public static PortalRegistry getPortalRegistry() {
        return portalRegistry;
    }

    public static RegionRegistry getRegionRegistry() {
        return regionRegistry;
    }

    public static SchematicRegistry getSchematicRegistry() {
        return schematicRegistry;
    }

    public static SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public static BungeeData getBungeeData() {
        return bungeeData;
    }

    public static boolean bungeeEnabled() {
        boolean bungee = SpigotConfig.bungee;
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        return bungee && !onlineMode;
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
