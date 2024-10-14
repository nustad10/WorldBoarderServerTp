package com.yourname.worldborderplugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {
    private final WorldBorderPlugin plugin;
    private final RegionManager regionManager;

    public PlayerMovementListener(WorldBorderPlugin plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || !regionManager.isCrossingBorder(from, to)) {
            return;
        }

        Player player = event.getPlayer();
        int toRegion = regionManager.getRegion(to);
        String serverName = getServerNameForRegion(toRegion);

        // Use the intended destination coordinates
        Location targetLocation = to;

        // Send the player to the corresponding server and location
        connectPlayerToServer(player, serverName, targetLocation);

        // Cancel the move event to prevent further movement on the current server
        event.setCancelled(true);
    }

    private String getServerNameForRegion(int region) {
        switch (region) {
            case 1:
                return "dev-1";
            case 2:
                return "dev-2";
            case 3:
                return "dev-3";
            case 4:
                return "dev-4";
            default:
                return "dev-1";
        }
    }

    private void connectPlayerToServer(Player player, String serverName, Location targetLocation) {
        // Send the player to the target server
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

        // Send the coordinates to the target server
        ByteArrayDataOutput coordOut = ByteStreams.newDataOutput();
        coordOut.writeDouble(targetLocation.getX());
        coordOut.writeDouble(targetLocation.getY());
        coordOut.writeDouble(targetLocation.getZ());
        coordOut.writeFloat(targetLocation.getYaw());
        coordOut.writeFloat(targetLocation.getPitch());
        coordOut.writeUTF(player.getUniqueId().toString());

        player.sendPluginMessage(plugin, "velocity:player_info", coordOut.toByteArray());
    }
}
