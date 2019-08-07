package com.alejandro.thebestplugin.commands;

import com.alejandro.thebestplugin.TheBestPlugin;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RegisterAccountCommand extends LocalCommand {

    public RegisterAccountCommand() {

        super("register-account", 2);
    }

    private TheBestPlugin plugin;
    private CommandSender sender;
    private String[] args;

    @Override
    public void execute(TheBestPlugin plugin, CommandSender sender, @NotNull String[] args) {

        this.plugin = plugin;
        this.sender = sender;
        this.args = args;
        String userArg = args[0];
        String playerArg = args[1];

        User user = getUserByName(userArg);
        OfflinePlayer player = getPlayerByName(playerArg);

        sendResponseMessage(user, player);
    }

    private User getUserByName(String name) {
        final int FIRST = 0;
        final boolean IGNORE_CASE = true;

        try {
            return getPluginJDA().getUsersByName(name, IGNORE_CASE).get(FIRST);
        } catch(ArrayIndexOutOfBoundsException exception) {
            sender.sendMessage("No users were found by that name.");
        }
        return null;
    }

    private OfflinePlayer getPlayerByName(String name) {
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            if (player.getName() == null)
                continue;

            if (player.getName().equals(name))
                return player;
        }
        sender.sendMessage("No players were found by that name.");
        return null;
    }

    private void sendResponseMessage(User user, OfflinePlayer player) {
        try {
            sender.sendMessage(ChatColor.GOLD + String.format("%s / %s has been registered",
                    user.getName(),
                    player.getName()
            ) + ChatColor.RESET);
        } catch(NullPointerException exception) {
            // do nothing
        }
    }

    private JDA getPluginJDA() {
        return plugin.getJDA();
    }
}
