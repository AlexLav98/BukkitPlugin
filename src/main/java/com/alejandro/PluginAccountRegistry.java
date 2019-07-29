package com.alejandro;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class PluginAccountRegistry {

    private static Set<Pair<Long, UUID>> registry = new HashSet<>();

    private JDA jda;

    public PluginAccountRegistry(String[][] serializedAccounts, TheBestPlugin plugin, JDA jda) {

        this.jda = jda;

        final int DISCORD_ID_INDEX = 0;
        final int UUID_INDEX = 1;

        for (String[] account : serializedAccounts) {

            // Get the raw values of the account and register them.

            long accountDiscordID     = Long.valueOf(account[ DISCORD_ID_INDEX ]);
            UUID accountMinecraftUUID = UUID.fromString(account[ UUID_INDEX ]);

            registry.add(Pair.of(accountDiscordID, accountMinecraftUUID));

            // Get the user names from the values and log them.

            String accountDiscordUsername   = jda.getUserById(account[ DISCORD_ID_INDEX ]).getName();
            String accountMinecraftUsername = Bukkit.getOfflinePlayer(UUID.fromString(account[ UUID_INDEX ])).getName();

            plugin.getLogger().info("Deserialized account: " + accountDiscordUsername + " / " + accountMinecraftUsername);
        }
    }

    String[][] serialize() {

        int DISCORD_ID_INDEX = 0;
        int UUID_INDEX = 1;

        String[][] data = new String[registry.size()][2];

        Iterator<Pair<Long, UUID>> registryIterator = registry.iterator();
        Pair<Long, UUID> currentPair;

        for (int accountIndex = 0; accountIndex < registry.size(); accountIndex++) {

            if (registryIterator.hasNext())
                currentPair = registryIterator.next();
            else
                break;

            data[ accountIndex ][ DISCORD_ID_INDEX ] = Long.toString(currentPair.getLeft());
            data[ accountIndex ][ UUID_INDEX ]       = currentPair.getRight().toString();
        }

        return data;
    }

    @Nullable User getUserByPlayer(@NotNull OfflinePlayer player) {

        UUID targetPlayerUUID = player.getUniqueId();

        Pair<Long, UUID> currentPair;

        for (Pair<Long, UUID> longUUIDPair : registry) {

            currentPair = longUUIDPair;

            if (currentPair.getRight().equals(targetPlayerUUID))
                return jda.getUserById(currentPair.getLeft());
        }

        return null;
    }

    @Nullable OfflinePlayer getPlayerByUser(@NotNull User user) {

        long targetUserId = user.getIdLong();

        Pair<Long, UUID> currentPair;

        for (Pair<Long, UUID> longUUIDPair : registry) {

            currentPair = longUUIDPair;

            if (currentPair.getLeft().equals(targetUserId))
                return Bukkit.getOfflinePlayer(currentPair.getRight());
        }

        return null;
    }

    void append(String discordId, String playerUUIDString) {

        registry.forEach(currentPair -> {

            boolean containsReusedInfo =
                    Long.toString(currentPair.getLeft()).equals(discordId) ||
                            currentPair.getRight().toString().equals(playerUUIDString);

            if (containsReusedInfo)
                throw new DuplicateAccountInformationException("Account pair " + currentPair.toString() + " contains duplicate information of an existing account");
        });

        registry.add(Pair.of(Long.valueOf(discordId), UUID.fromString(playerUUIDString)));
    }

    static class DuplicateAccountInformationException extends RuntimeException {
        DuplicateAccountInformationException(String msg) {
            super(msg);
        }
    }
}
