package com.yourname.worldborderplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoordinateListener implements PluginMessageListener, Listener {
    private final WorldBorderPlugin plugin;
    private final Map<UUID, Location> pendingLocations = new HashMap<>();

    public CoordinateListener(WorldBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("velocity:player_info")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        String uuidString = in.readUTF();

        UUID playerUUID = UUID.fromString(uuidString);
        World world = Bukkit.getWorld("abexilas"); // Adjust if your world has a different name
        if (world == null) {
            plugin.getLogger().severe("World 'world' not found!");
            return;
        }

        Location loc = new Location(world, x, y, z, yaw, pitch);

        pendingLocations.put(playerUUID, loc);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location loc = pendingLocations.remove(player.getUniqueId());
        if (loc != null) {
            // Teleport the player to the specified location
            Bukkit.getScheduler().runTask(plugin, () -> player.teleport(loc));
        }
    }
}

