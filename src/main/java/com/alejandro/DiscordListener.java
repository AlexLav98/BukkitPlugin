package com.alejandro;

import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.OfflinePlayer;

public class DiscordListener extends ListenerAdapter {

    DiscordListener(TheBestPlugin plugin, HashBiMap<Long, OfflinePlayer> linkedAccountsMap) {
        this.plugin = plugin;
        this.linkedAccountsMap = linkedAccountsMap;
    }

    private TheBestPlugin plugin;
    private HashBiMap<Long, OfflinePlayer> linkedAccountsMap;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        var rawMessageContent       = event.getMessage().getContentDisplay();
        var authorDiscordUsername   = event.getAuthor().getName();
        var messagedChannelIDLong   = event.getChannel().getIdLong();
        var mainServerInstance      = plugin.getServer();
        var consoleChannelIDLong    = plugin.consoleChannelIdLong();
        var inGameChannelIDLong     = plugin.inGameChannelIdLong();

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

            var targetOfflinePlayer = linkedAccountsMap.get(event.getAuthor().getIdLong());

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