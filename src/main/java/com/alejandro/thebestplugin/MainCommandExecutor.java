package com.alejandro.thebestplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    //TODO Add class-oriented command executing system
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("register-account") && args.length == 2) {

            String userId     = args[0];
            String playerUUID = args[1];

            try {

                String userName   = plugin.getJDA().getUserById(userId).getName();
                String playerName = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName();

                plugin.getAccountRegistry().append(userId, playerUUID);

                sender.sendMessage(ChatColor.GOLD + String.format("%s / %s has been registered",
                        userName,
                        playerName
                ) + ChatColor.RESET);

            } catch(PluginAccountRegistry.DuplicateAccountInformationException e) {

                sender.sendMessage(ChatColor.RED + "Duplicate credentials detected! Check your info carefully" + ChatColor.RESET);
            } catch(NullPointerException e) {

                sender.sendMessage(ChatColor.RED + "Invalid information! Check your info carefully" + ChatColor.RESET);
            }

            return true;
        }

        return false;
    }
}
