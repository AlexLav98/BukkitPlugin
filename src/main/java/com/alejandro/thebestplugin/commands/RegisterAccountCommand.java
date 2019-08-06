package com.alejandro.thebestplugin.commands;

import com.alejandro.thebestplugin.PluginAccountRegistry;
import com.alejandro.thebestplugin.TheBestPlugin;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RegisterAccountCommand extends LocalCommand {

    public RegisterAccountCommand() {

        super("register-account", 2);
    }

    private TheBestPlugin plugin;

    @Override
    public void execute(TheBestPlugin plugin, CommandSender sender, @NotNull String[] args) {

        this.plugin = plugin;

        User user;
        OfflinePlayer player = null;

        String discordIdentifier = args[0];
        String minecraftIdentifier = args[1];

        try {

            if (isNumericString(discordIdentifier)) {

                user   = getUserById(discordIdentifier);
                player = getOfflinePlayer(minecraftIdentifier);

                plugin.getAccountRegistry().addAccount(user.getId(), player.getUniqueId().toString());

            } else {

                user = plugin.getJDA().getUsersByName(args[0], true).get(0);

                for (OfflinePlayer currentOfflinePlayer : Bukkit.getWhitelistedPlayers()) {

                    if (currentOfflinePlayer.getName() == null)
                        continue;

                    if (currentOfflinePlayer.getName().equalsIgnoreCase(args[0]))
                        player = currentOfflinePlayer;
                }

                assert player != null;
                plugin.getAccountRegistry().addAccount(user.getId(), player.getUniqueId().toString());
            }

            sender.sendMessage(ChatColor.GOLD + String.format("%s / %s has been registered",
                    user.getName(),
                    player.getName()
            ) + ChatColor.RESET);

        } catch(PluginAccountRegistry.DuplicateAccountInformationException e) {

            sender.sendMessage(ChatColor.RED + "Duplicate credentials detected! Check your info carefully" + ChatColor.RESET);
        } catch(NullPointerException e) {

            sender.sendMessage(ChatColor.RED + "Invalid information! Check your info carefully" + ChatColor.RESET);
        }
    }

    private boolean isNumericString(String string) {
        return StringUtils.isNumeric(string);
    }

    private User getUserById(String id) {
        return plugin.getJDA().getUserById(id);
    }

    private OfflinePlayer getOfflinePlayer(String UUIDFromString) {
        return Bukkit.getOfflinePlayer(UUID.fromString(UUIDFromString));
    }
}
