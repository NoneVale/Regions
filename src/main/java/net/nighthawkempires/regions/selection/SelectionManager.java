package net.nighthawkempires.regions.selection;

import com.google.common.collect.Maps;
import net.nighthawkempires.core.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class SelectionManager {

    private HashMap<UUID, Clipboard> clipboardMap;

    private ItemStack selectionWand;

    public SelectionManager() {
        this.clipboardMap = Maps.newHashMap();

        this.selectionWand = ItemUtil.getItemStack(Material.BLAZE_ROD, RED + "" + BOLD + "" + ITALIC + "Selection Wand");
    }

    public ItemStack getSelectionWand() {
        return selectionWand;
    }

    public Clipboard getClipboard(UUID uuid) {
        if (this.clipboardMap.containsKey(uuid))
            return this.clipboardMap.get(uuid);
        else {
            this.clipboardMap.put(uuid, new Clipboard());
            return this.clipboardMap.get(uuid);
        }
    }

    public void deleteClipboard(UUID uuid) {
        this.clipboardMap.remove(uuid);
    }

    public boolean hasClipboard(UUID uuid) {
        return this.clipboardMap.containsKey(uuid);
    }
}