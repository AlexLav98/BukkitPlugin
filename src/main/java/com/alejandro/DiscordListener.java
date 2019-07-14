package com.alejandro;

import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordListener extends ListenerAdapter {

    public DiscordListener(JavaPlugin plugin, HashBiMap<Long, OfflinePlayer> linkedAccountsMap) {
        this.plugin = plugin;
        this.linkedAccountsMap = linkedAccountsMap;
    }

    private JavaPlugin plugin;
    private HashBiMap<Long, OfflinePlayer> linkedAccountsMap;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String rawMessageContent       = event.getMessage().getContentDisplay();
        String authorDiscordUsername   = event.getAuthor().getName();
        String authorMinecraftUsername = null;
        Server mainServerInstance      = plugin.getServer();
        long messagedChannelIDLong     = event.getChannel().getIdLong();
        long consoleChannelIDLong      = 597831182377549824L;
        long inGameChannelIDLong       = 597858140243099693L;

        /*
         * If the user doesn't have a minecraft account
         * linked to them, don't fetch their Minecraft username
         * to avoid a null pointer exception.
         */
        if (linkedAccountsMap.containsKey(event.getAuthor().getIdLong()))
            authorMinecraftUsername = linkedAccountsMap.get(event.getAuthor().getIdLong()).getName();

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

            boolean isAuthorRegistered = authorMinecraftUsername != null;

            /*
             * If the author of the message has a minecraft account linked to their discord account,
             * use their minecraft display name. If not, use their discord name.
             */
            String usernameToDisplay = isAuthorRegistered ? authorMinecraftUsername : authorDiscordUsername;

            // Send to game chat "<username> (message)"
            mainServerInstance.broadcastMessage(String.format("<%s> %s", usernameToDisplay, rawMessageContent));
        }
    }
}