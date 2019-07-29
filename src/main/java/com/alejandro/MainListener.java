package com.alejandro;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public final class MainListener implements Listener {

    MainListener(PluginAccountRegistry accountRegistry, JDA jda, TheBestPlugin plugin) {
        this.accountRegistry = accountRegistry;
        this.jda = jda;
        this.plugin = plugin;

        mainListenerWrapper = new MainListenerWrapper(jda, plugin);
        inGameChannelIdLong = plugin.inGameChannelIdLong();
    }

    private PluginAccountRegistry accountRegistry;
    private JDA jda;
    private MainListenerWrapper mainListenerWrapper;
    private TheBestPlugin plugin;

    private long inGameChannelIdLong;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        String authorDiscordUsername  = Objects.requireNonNull(accountRegistry.getUserByPlayer(event.getPlayer())).getName();
        String messageContent         = event.getMessage();
        TextChannel inGameTextChannel = jda.getTextChannelById(plugin.inGameChannelIdLong());

        inGameTextChannel.sendMessage(String.format("**%s**: %s", authorDiscordUsername, messageContent)).queue();
    }

    /**
     * When a player logs on, print their join message on Discord
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // getJoinMessage() can return null. Also, make sure to filter out
        // weird minecraft unicode
        String formattedJoinMessage = event.getJoinMessage() == null ?
                "[JOIN MESSAGE ERROR]" : event.getJoinMessage().replace("\u00A7e", "");

        jda.getTextChannelById(inGameChannelIdLong)
                .sendMessage("**" + formattedJoinMessage + "**").queue();
    }

    /**
     * When a player quits, print their leave message on Discord
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        /*
         * Remove unicode. Explained above
         */
        String formattedQuitMessage = event.getQuitMessage().replace("\u00A7e", "");

        jda.getTextChannelById(inGameChannelIdLong)
                .sendMessage("**" + formattedQuitMessage + "**").queue();
    }

    /**
     * When a player dies, print their death in Discord
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        jda.getTextChannelById(inGameChannelIdLong)
                .sendMessage("**" + event.getDeathMessage() + "**").queue();
    }
}