package com.alejandro;

import org.bukkit.*;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MainListener implements Listener {

    public MainListener(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.server = plugin.getServer();
        this.plugin = plugin;
    }

    JavaPlugin plugin;
    Logger logger;
    Server server;

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

    @EventHandler
    public void ohyeahyeah(BlockDamageEvent event) {

        World eventWorld = event.getBlock().getWorld();
        Player damagingPlayer = event.getPlayer();

        Location targetBlockLocation = event.getBlock().getLocation();
        Location dangerousBlockLocation = new Location(eventWorld, 211, 71, 326);

        if (targetBlockLocation.equals(dangerousBlockLocation))
            damagingPlayer.kickPlayer("Nigger");

    }

    @EventHandler
    public void peta(EntityDamageByEntityEvent event) {


        Player damagingPlayer = (Player) event.getDamager();

        Entity targettedEntity = event.getEntity();

        if (targettedEntity instanceof Animals) {
            damagingPlayer.kickPlayer("You have been removed from the server by PETA");
        }
    }
}









