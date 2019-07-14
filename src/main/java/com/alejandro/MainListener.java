package com.alejandro;

import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import sun.awt.AWTAccessor;

public final class MainListener implements Listener {

    public MainListener(HashBiMap<Long, OfflinePlayer> linkedAccountsMap, JDA jda) {
        this.linkedAccountsMap = linkedAccountsMap;
        this.jda = jda;

        inGameTextChannel = jda.getTextChannelById(597858140243099693L);
    }

    private HashBiMap<Long, OfflinePlayer> linkedAccountsMap;
    private JDA jda;
    private TextChannel inGameTextChannel;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Long   authorUserIdLong       = linkedAccountsMap.inverse().get( event.getPlayer() );
        String authorDiscordUsername  = jda.getUserById( authorUserIdLong ).getName();
        String messageContent         = event.getMessage();

        inGameTextChannel.sendMessage(String.format("**%s**: %s", authorDiscordUsername, messageContent)).queue();

    }

    /**
     * When a player logs on, print their join message on Discord
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        /*
         * Simply calling getJoinMessage() includes these weird
         * '§e' unicode characters. We filter them out.
         */
        String formattedJoinMessage = event.getJoinMessage().replaceAll("(?:§e)", "");

        inGameTextChannel.sendMessage("**" + formattedJoinMessage + "**").queue();
    }

    /**
     * When a player quits, print their leave message on Discord
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        /*
         * Remove unicode. Explained above
         */
        String formattedQuitMessage = event.getQuitMessage().replaceAll("(?:§e)", "");

        inGameTextChannel.sendMessage("**" + formattedQuitMessage + "**").queue();
    }

    /**
     * When a player dies, print their death in Discord
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        inGameTextChannel.sendMessage("**" + event.getDeathMessage() + "**").queue();
    }
}









