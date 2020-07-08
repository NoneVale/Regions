package net.nighthawkempires.regions.selection;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Clipboard {

    private String name;

    private int slot;
    private ItemStack slotRestore;

    private Location firstCorner;
    private Location secondCorner;

    private SelectionType selectionType;

    private boolean selecting;

    public boolean isSelecting() {
        return selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getSlotRestore() {
        return slotRestore;
    }

    public void setSlotRestore(ItemStack slotRestore) {
        this.slotRestore = slotRestore;
    }

    public Location getFirstCorner() {
        return firstCorner;
    }

    public void setFirstCorner(Location firstCorner) {
        this.firstCorner = firstCorner;
    }

    public Location getSecondCorner() {
        return secondCorner;
    }

    public void setSecondCorner(Location secondCorner) {
        this.secondCorner = secondCorner;
    }

    public SelectionType getType() {
        return selectionType;
    }

    public void setType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
