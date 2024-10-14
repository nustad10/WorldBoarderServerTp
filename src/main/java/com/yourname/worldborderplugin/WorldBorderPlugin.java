package com.yourname.worldborderplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldBorderPlugin extends JavaPlugin {
    private RegionManager regionManager;

    @Override
    public void onEnable() {
        this.regionManager = new RegionManager();
        buildWalls();

        PlayerMovementListener movementListener = new PlayerMovementListener(this, regionManager);
        getServer().getPluginManager().registerEvents(movementListener, this);

        CoordinateListener coordinateListener = new CoordinateListener(this);
        getServer().getPluginManager().registerEvents(coordinateListener, this);

        // Register plugin messaging channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:player_info");
        getServer().getMessenger().registerIncomingPluginChannel(this, "velocity:player_info", coordinateListener);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic if needed
    }

    public void buildWalls() {
        World world = Bukkit.getWorld("world");
        if (world == null) {
            getLogger().severe("World 'world' not found!");
            return;
        }

        int x1 = regionManager.getX1();
        int z1 = regionManager.getZ1();
        int x2 = regionManager.getX2();
        int z2 = regionManager.getZ2();

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        int midX = (x1 + x2) / 2;
        int midZ = (z1 + z2) / 2;
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        // Build wall along X-axis (vertical wall)
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(midX, y, z).setType(org.bukkit.Material.GLASS);
            }
        }

        // Build wall along Z-axis (horizontal wall)
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                world.getBlockAt(x, y, midZ).setType(org.bukkit.Material.GLASS);
            }
        }
    }
}
