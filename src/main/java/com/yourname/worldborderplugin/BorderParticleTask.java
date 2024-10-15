package com.yourname.worldborderplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderParticleTask extends BukkitRunnable {
    private final WorldBorderPlugin plugin;
    private final RegionManager regionManager;

    public BorderParticleTask(WorldBorderPlugin plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    @Override
    public void run() {
        World world = Bukkit.getWorld("world"); // Adjust if your world has a different name
        if (world == null) return;

        int minX = regionManager.getMinX();
        int maxX = regionManager.getMaxX();
        int minZ = regionManager.getMinZ();
        int maxZ = regionManager.getMaxZ();
        int midX = regionManager.getMidX();
        int midZ = regionManager.getMidZ();
        int minY = 60; // Adjust Y levels as needed
        int maxY = 80;

        // Vertical border at midX (north-south)
        for (int y = minY; y <= maxY; y += 5) {
            for (int z = minZ; z <= maxZ; z += 20) {
                Location loc = new Location(world, midX + 0.5, y, z + 0.5);
                world.spawnParticle(Particle.PORTAL, loc, 5, 0, 0, 0, 0);
            }
        }

        // Horizontal border at midZ (east-west)
        for (int y = minY; y <= maxY; y += 5) {
            for (int x = minX; x <= maxX; x += 20) {
                Location loc = new Location(world, x + 0.5, y, midZ + 0.5);
                world.spawnParticle(Particle.PORTAL, loc, 5, 0, 0, 0, 0);
            }
        }
    }
}
