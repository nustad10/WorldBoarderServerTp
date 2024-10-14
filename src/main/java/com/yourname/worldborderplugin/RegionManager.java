package com.yourname.worldborderplugin;

import org.bukkit.Location;

public class RegionManager {
    private final int x1 = 1424;
    private final int z1 = -387;
    private final int x2 = 1974;
    private final int z2 = -937;

    public int getX1() {
        return x1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getZ2() {
        return z2;
    }

    // Determine the region based on the location
    public int getRegion(Location loc) {
        int midX = (x1 + x2) / 2;
        int midZ = (z1 + z2) / 2;

        boolean east = loc.getX() > midX;
        boolean south = loc.getZ() > midZ;

        if (east && south) {
            return 4;
        } else if (east) {
            return 2;
        } else if (south) {
            return 3;
        } else {
            return 1;
        }
    }

    public boolean isCrossingBorder(Location from, Location to) {
        return getRegion(from) != getRegion(to);
    }
}
