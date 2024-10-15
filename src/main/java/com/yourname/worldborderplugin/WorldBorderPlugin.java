package com.yourname.worldborderplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldBorderPlugin extends JavaPlugin {
    private RegionManager regionManager;

    @Override
    public void onEnable() {
        this.regionManager = new RegionManager();

        PlayerMovementListener movementListener = new PlayerMovementListener(this, regionManager);
        getServer().getPluginManager().registerEvents(movementListener, this);

        CoordinateListener coordinateListener = new CoordinateListener(this);
        getServer().getPluginManager().registerEvents(coordinateListener, this);

        // Register plugin messaging channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:player_info");
        getServer().getMessenger().registerIncomingPluginChannel(this, "velocity:player_info", coordinateListener);

        // Start border particle task
        BorderParticleTask borderParticleTask = new BorderParticleTask(this, regionManager);
        borderParticleTask.runTaskTimer(this, 0L, 10L); // Adjust the period as needed
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic if needed
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }
}
