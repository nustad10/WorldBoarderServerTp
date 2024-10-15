package com.yourname.worldborderplugin;

import org.bukkit.Location;

public class RegionManager {
    // Overall area boundaries
    private final int minX = -11869;
    private final int maxX = 8166;
    private final int minZ = -10017;
    private final int maxZ = 10017;

    // Midpoints to divide the area into quadrants
    private final int midX = -1852; // Provided midpoint
    private final int midZ = 125;   // Provided midpoint

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMidX() {
        return midX;
    }

    public int getMidZ() {
        return midZ;
    }

    // Determine the region based on the location
    public int getRegion(Location loc) {
        boolean east = loc.getX() > midX;
        boolean south = loc.getZ() > midZ;

        if (east && south) {
            return 4; // SE (Quadrant 4)
        } else if (east) {
            return 2; // NE (Quadrant 2)
        } else if (south) {
            return 3; // SW (Quadrant 3)
        } else {
            return 1; // NW (Quadrant 1)
        }
    }

    public boolean isCrossingBorder(Location from, Location to) {
        return getRegion(from) != getRegion(to);
    }
}
