package com.alejandro.thebestplugin.listeners;

import com.alejandro.thebestplugin.TheBestPlugin;
import com.alejandro.thebestplugin.accounts.PluginAccountRegistry;
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

    public MainListener(PluginAccountRegistry accountRegistry, TheBestPlugin plugin) {
        this.accountRegistry = accountRegistry;
        this.jda = plugin.getJDA();
        this.plugin = plugin;

        inGameChannelIdLong = plugin.inGameChannelIdLong();
    }

    private final PluginAccountRegistry accountRegistry;
    private final JDA jda;
    private final TheBestPlugin plugin;

    private final long inGameChannelIdLong;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        try {

            String authorDiscordUsername  = Objects.requireNonNull(accountRegistry.getUserByPlayer(event.getPlayer())).getName();
            String messageContent         = event.getMessage();
            TextChannel inGameTextChannel = jda.getTextChannelById(plugin.inGameChannelIdLong());

            inGameTextChannel.sendMessage(String.format("**%s**: %s", authorDiscordUsername, messageContent)).queue();

        } catch (NullPointerException e) {
            // Ignore exception
        }
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

//    @EventHandler
//    public void arrowKillAura(EntityTargetLivingEntityEvent event) {
//
//        if (!(event.getTarget() instanceof Player))
//            return;
//
//        Player target = (Player) event.getTarget();
//        Player hydroPage90 = Bukkit.getPlayer(UUID.fromString("67fe62ad-2efd-4a2a-8ae5-98cc925116fc"));
//
//        if (!target.equals(hydroPage90))
//            return;
//
//        Vector testVector  = new Vector(5.0, 5.0, 0.0);
//
//        hydroPage90.launchProjectile(Arrow.class, testVector);
//    }
}