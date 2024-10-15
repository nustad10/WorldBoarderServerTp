package com.yourname.worldborderplugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMovementListener implements Listener {
    private final WorldBorderPlugin plugin;
    private final RegionManager regionManager;

    // Cooldown time in milliseconds (10 seconds)
    private static final long TELEPORT_COOLDOWN = 10 * 1000;
    private final HashMap<UUID, Long> teleportCooldowns = new HashMap<>();

    public PlayerMovementListener(WorldBorderPlugin plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        int fromRegion = regionManager.getRegion(from);
        int toRegion = regionManager.getRegion(to);

        if (fromRegion != toRegion) {
            long currentTime = System.currentTimeMillis();

            // Check if the player is on cooldown
            if (teleportCooldowns.containsKey(playerUUID)) {
                long lastTeleportTime = teleportCooldowns.get(playerUUID);
                long timeSinceLastTeleport = currentTime - lastTeleportTime;

                if (timeSinceLastTeleport < TELEPORT_COOLDOWN) {
                    // Player is on cooldown
                    long timeLeft = (TELEPORT_COOLDOWN - timeSinceLastTeleport) / 1000; // Convert to seconds
                    player.sendMessage(ChatColor.RED + "You cannot cross the border yet. Please wait " + timeLeft + " seconds.");

                    // Prevent the player from crossing by setting their position back to 'from' location
                    event.setCancelled(true);
                    player.teleport(from); // Teleport the player back to their previous location
                    return;
                }
            }

            String serverName = getServerNameForRegion(toRegion);

            // Adjust the player's position to the corresponding location in the new region
            Location targetLocation = adjustLocationForRegionChange(from, to, fromRegion, toRegion);

            // Update the cooldown
            teleportCooldowns.put(playerUUID, currentTime);

            // Send the player to the corresponding server and location
            connectPlayerToServer(player, serverName, targetLocation);

            // Cancel the move event to prevent further movement on the current server
            event.setCancelled(true);
        }
    }

    private Location adjustLocationForRegionChange(Location from, Location to, int fromRegion, int toRegion) {
        double newX = to.getX();
        double newZ = to.getZ();
        double newY = to.getY();
        float yaw = to.getYaw();
        float pitch = to.getPitch();

        int midX = regionManager.getMidX();
        int midZ = regionManager.getMidZ();

        boolean crossingVertical = (fromRegion == 1 && toRegion == 2) || (fromRegion == 2 && toRegion == 1) ||
                (fromRegion == 3 && toRegion == 4) || (fromRegion == 4 && toRegion == 3);

        boolean crossingHorizontal = (fromRegion == 1 && toRegion == 3) || (fromRegion == 3 && toRegion == 1) ||
                (fromRegion == 2 && toRegion == 4) || (fromRegion == 4 && toRegion == 2);

        // Adjust coordinates when crossing vertical borders (X-axis)
        if (crossingVertical) {
            // Mirror the X coordinate across the midX
            newX = 2 * midX - to.getX();

            // Determine movement direction based on actual movement
            if (to.getX() > from.getX()) {
                // Moving East to West (crossing from East to West)
                newX -= 3;
            } else {
                // Moving West to East (crossing from West to East)
                newX += 3;
            }
        }

        // Adjust coordinates when crossing horizontal borders (Z-axis)
        if (crossingHorizontal) {
            // Mirror the Z coordinate across the midZ
            newZ = 2 * midZ - to.getZ();

            // Determine movement direction based on actual movement
            if (to.getZ() > from.getZ()) {
                // Moving South to North (crossing from South to North)
                newZ -= 3;
            } else {
                // Moving North to South (crossing from North to South)
                newZ += 3;
            }
        }

        return new Location(null, newX, newY, newZ, yaw, pitch);
    }

    private String getServerNameForRegion(int region) {
        switch (region) {
            case 1:
                return "abexilas-1";
            case 2:
                return "abexilas-2";
            case 3:
                return "abexilas-3";
            case 4:
                return "abexilas-4";
            default:
                return "abexilas-1";
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
