package com.alejandro.thebestplugin.listeners;

import com.alejandro.thebestplugin.TheBestPlugin;
import com.alejandro.thebestplugin.accounts.PluginAccountRegistry;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class DiscordListener extends ListenerAdapter {

    public DiscordListener(TheBestPlugin plugin, PluginAccountRegistry accountRegistry) {
        this.plugin = plugin;
        this.accountRegistry = accountRegistry;
    }

    private final TheBestPlugin plugin;
    private final PluginAccountRegistry accountRegistry;

    @Override
    //TODO Make command feedback get sent to Discord Command console, as well
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String rawMessageContent      = event.getMessage().getContentDisplay();
        String authorDiscordUsername  = event.getAuthor().getName();
        long messagedChannelIDLong    = event.getChannel().getIdLong();
        Server mainServerInstance     = plugin.getServer();
        long consoleChannelIDLong     = plugin.consoleChannelIdLong();
        long inGameChannelIDLong      = plugin.inGameChannelIdLong();

        if (event.getAuthor().isBot() || (messagedChannelIDLong != consoleChannelIDLong && messagedChannelIDLong != inGameChannelIDLong))
            return;

        if (messagedChannelIDLong == consoleChannelIDLong && rawMessageContent.startsWith("/")) {

            // The server takes commands without a preceding slash.
            // We only need the preceding slash in the chat to tell apart server commands from chat.
            String messageWithoutStartingSlash = rawMessageContent.substring(1);

            // Send the text to the server as a command to be run
            boolean success = mainServerInstance.dispatchCommand(mainServerInstance.getConsoleSender(), messageWithoutStartingSlash);

            // How did it go?
            if (success)
                event.getChannel().sendMessage("\u2705 Executed successfully").queue();
            else
                event.getChannel().sendMessage("\u274C An error has occurred").queue();

        }
        else if (messagedChannelIDLong == inGameChannelIDLong) {

            OfflinePlayer targetOfflinePlayer = accountRegistry.getPlayerByUser(event.getAuthor());

            /*
             * If the author of the message has a minecraft account linked to their discord account,
             * use their minecraft display name. If not, use their discord name.
             */
            String usernameToDisplay = authorDiscordUsername;
            if (targetOfflinePlayer != null)
                usernameToDisplay = targetOfflinePlayer.getName();

            // Send to game chat "<username> (message)"
            mainServerInstance.broadcastMessage(String.format("<%s> %s", usernameToDisplay, rawMessageContent));
        }
    }
}