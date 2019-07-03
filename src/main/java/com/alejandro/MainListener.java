package com.alejandro;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MainListener implements Listener {

    MainListener(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.server = plugin.getServer();
        this.plugin = plugin;
    }

    private JavaPlugin plugin;
    private Logger logger;
    private Server server;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Location locationWherePlaced = event.getBlockPlaced().getLocation();
        String blockName = event.getBlockPlaced().getBlockData().getAsString();

        Bukkit.broadcastMessage(String.format(
                ChatColor.RED + "[WARNING] %s PLACED AT %d, %d, %d",
                blockName,
                locationWherePlaced.getBlockX(),
                locationWherePlaced.getBlockY(),
                locationWherePlaced.getBlockZ()
                )
        );
    }
}









