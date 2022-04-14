package net.nighthawkempires.regions.schematic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.regions.RegionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.List;
import java.util.Map;

public class SchematicModel implements Model {

    private final String key;
    private String[][][] field;
    private int length;
    private int width;
    private int height;

    public SchematicModel(String name, Location corner1, Location corner2) {
        this.key = name;

        int x1 = corner1.getBlockX();
        int y1 = corner1.getBlockY();
        int z1 = corner1.getBlockZ();

        int x2 = corner2.getBlockX();
        int y2 = corner2.getBlockY();
        int z2 = corner2.getBlockZ();

        if (x2 < x1) {
            x1 = x2;
            x2 = corner1.getBlockX();
        }

        if (y2 < y1) {
            y1 = y2;
            y2 = corner1.getBlockY();
        }

        if (z2 < z1) {
            z1 = z2;
            z2 = corner1.getBlockZ();
        }

        String[][][] yArray = {};
        List<String[][]> yBlocks = Lists.newArrayList();
        for (int y = y1; y <= y2; y++) {
            String[][] xArray = {};
            List<String[]> xBlocks = Lists.newArrayList();
            for (int x = x1; x <= x2; x++) {
                String[] zArray = {};
                List<String> zBlocks = Lists.newArrayList();
                for (int z = z1; z <= z2; z++) {
                    Block block = corner1.getWorld().getBlockAt(x, y, z);
                    zBlocks.add(block.getBlockData().getAsString());
                }

                width = zBlocks.size();
                zArray = zBlocks.toArray(zArray);
                xBlocks.add(zArray);
            }

            length = xBlocks.size();
            xArray = xBlocks.toArray(xArray);
            yBlocks.add(xArray);
        }

        // THIS IS THE SCHEMATIC LAYOUT //
        height = yBlocks.size();
        field = yArray = yBlocks.toArray(yArray);
    }

    public SchematicModel(String key, DataSection data) {
        this.key = key;

        //List<List<List<String>>>
        List<List<List<String>>> layout = (List) data.getList("layout");

        String[][][] yArray = {};
        List<String[][]> yBlocks = Lists.newArrayList();
        for (List<List<String>> y : layout) {
            String[][] xArray = {};
            List<String[]> xBlocks = Lists.newArrayList();
            for (List<String> x : y) {
                String[] zArray = {};
                List<String> zBlocks = Lists.newArrayList();
                for (String z : x) {
                    zBlocks.add(z);
                }

                width = zBlocks.size();
                zArray = zBlocks.toArray(zArray);
                xBlocks.add(zArray);
            }

            length = xBlocks.size();
            xArray = xBlocks.toArray(xArray);
            yBlocks.add(xArray);
        }

        height = yBlocks.size();
        field = yArray = yBlocks.toArray(yArray);
    }

    public String getKey() {
        return key;
    }

    public String[][][] getField() {
        return field;
    }

    public int getHeight() {
        return height;
    }

    public int getCenterHeight() {
        return height / 2;
    }

    public int getLength() {
        return length;
    }

    public int getCenterLength() {
        return length / 2;
    }

    public int getWidth() {
        return width;
    }

    public int getCenterWidth() {
        return width / 2;
    }

    public void paste(Location location) {
        int y = 0;
        for (int yPos = location.getBlockY() - 1; yPos <= location.getBlockY() + height + 1; yPos++) {
            if (y >= field.length) continue;
            String[][] xBlocks = field[y];
            int x = 0;
            for (int xPos = location.getBlockX() - getCenterLength(); xPos <= location.getBlockX() + getCenterLength() + 1; xPos++) {
                if (x >= xBlocks.length) continue;
                String[] zBlocks = xBlocks[x];
                int z = 0;
                for (int zPos = location.getBlockZ() - getCenterWidth(); zPos <= location.getBlockZ() + getCenterWidth() + 1; zPos++) {
                    if (z >= zBlocks.length) continue;
                    Location pasteLocation = new Location(location.getWorld(), xPos, yPos ,zPos);
                    Block block = pasteLocation.getBlock();
                    BlockData blockData = Bukkit.createBlockData(zBlocks[z]);

                    block.setBlockData(blockData);
                    z++;
                }
                x++;
            }
            y++;
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        List<List<List<String>>> yList = Lists.newArrayList();
        for (String[][] y : field) {
            List<List<String>> xList = Lists.newArrayList();
            for (String[] x : y) {
                List<String> zList = Lists.newArrayList();
                for (String z : x) {
                    zList.add(z);
                }

                xList.add(zList);
            }
            yList.add(xList);
        }

        map.put("layout", yList);
        map.put("length", length);
        map.put("width", width);
        map.put("height", height);

        return map;
    }
}