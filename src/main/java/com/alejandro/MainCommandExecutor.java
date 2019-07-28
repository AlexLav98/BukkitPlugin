package com.alejandro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MainCommandExecutor implements CommandExecutor {

    private TheBestPlugin plugin;

    MainCommandExecutor(TheBestPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("register-account") && args.length == 2) {

            Long          userIdLong     = Long.valueOf(args[0]);
            OfflinePlayer offlinePlayer  = Bukkit.getOfflinePlayer(UUID.fromString(args[1]));

            plugin.getLinkedAccountsMap().put(userIdLong, offlinePlayer);

            sender.sendMessage(ChatColor.GOLD + String.format("%s / %s has been registered",
                    plugin.getJDA().getUserById(userIdLong).getName(),
                    offlinePlayer.getName()
            ) + ChatColor.RESET);

            return true;
        }

        return false;
    }
}
