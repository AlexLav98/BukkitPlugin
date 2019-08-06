package com.alejandro.thebestplugin;

import com.alejandro.thebestplugin.commands.RegisteredAccount;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class PluginAccountRegistry {

    private static Set<RegisteredAccount> registry = new HashSet<>();


    private TheBestPlugin plugin;

    public PluginAccountRegistry(String[][] serializedAccountsArray, TheBestPlugin plugin) {
        this.plugin = plugin;
        registerAllAccountsIn(serializedAccountsArray);
    }

    private void registerAllAccountsIn(String[][] serializedAccounts) {
        for (String[] currentSerializedAccount : serializedAccounts) {
            registry.add(newRegisteredAccountFrom(currentSerializedAccount));
            logAccount(currentSerializedAccount);
        }
    }

    private RegisteredAccount newRegisteredAccountFrom(String[] account) {
        String discordId = getAccountDiscordIdString(account);
        String minecraftUUID = getAccountMinecraftUUIDString(account);
        return RegisteredAccount.newInstance(discordId, minecraftUUID, plugin);
    }

    private String getAccountDiscordIdString(String[] account) {
        int DISCORD_ID_INDEX = 0;
        return account[DISCORD_ID_INDEX];
    }

    private String getAccountMinecraftUUIDString(String[] account) {
        int UUID_INDEX = 1;
        return account[UUID_INDEX];
    }

    private void logAccount(String[] account) {
        String discordName = getAccountDiscordName(account);
        String minecraftName = getAccountOfflinePlayerName(account);
        getPluginLogger().info("Deserialized account: " + discordName + " / " + minecraftName);
    }

    private String getAccountDiscordName(String[] account) {
        String discordId = getAccountDiscordIdString(account);
        User accountDiscordUser = getUserByIdString(discordId);
        return accountDiscordUser.getName();
    }

    private String getAccountOfflinePlayerName(String[] account) {
        String minecraftUUIDString = getAccountMinecraftUUIDString(account);
        OfflinePlayer accountOfflinePlayer = getOfflinePlayerByUUIDString(minecraftUUIDString);
        return accountOfflinePlayer.getName();
    }

    private JDA getPluginJDA() {
        return plugin.getJDA();
    }

    private Logger getPluginLogger() {
        return plugin.getLogger();
    }

    private User getUserByIdString(String id) {
        return getPluginJDA().getUserById(id);
    }

    private OfflinePlayer getOfflinePlayerByUUIDString(String UUIDString) {
        UUID uuidFromString = UUID.fromString(UUIDString);
        return Bukkit.getOfflinePlayer(uuidFromString);
    }

    public String[][] serialize() {

        final int numberOfColumns = RegisteredAccount.numberOfColumns;

        String[][] data = new String[registry.size()][numberOfColumns];
        addAllAccountsInSerializedFormTo(data);

        return data;
    }

    private void addAllAccountsInSerializedFormTo(String[][] serializedAccounts) {
        Iterator<RegisteredAccount> registryIterator = registry.iterator();
        RegisteredAccount currentAccount;

        for (int accountIndex = 0; accountIndex < registry.size(); accountIndex++) {
            currentAccount = registryIterator.next();
            serializedAccounts[accountIndex] = currentAccount.serializedForm;
        }
    }

    @Nullable User getUserByPlayer(@NotNull OfflinePlayer player) {
        for (RegisteredAccount currentAccount : registry)
            if (currentAccount.playerEquals(player))
                return currentAccount.getUser();
        return null;
    }

    @Nullable OfflinePlayer getPlayerByUser(@NotNull User user) {
        for (RegisteredAccount account : registry)
            if (account.userEquals(user))
                return account.getOfflinePlayer();
        return null;
    }

    public void addAccount(String discordId, String playerUUIDString) {

        if (credentialsAlreadyUsed(discordId, playerUUIDString))
            throw new DuplicateAccountInformationException("Duplicate credentials, check your info carefully");

        registry.add(RegisteredAccount.newInstance(discordId, playerUUIDString, plugin));
    }

    private boolean credentialsAlreadyUsed(String userID, String playerUUID) {
        for (RegisteredAccount currentAccount : registry)
            if (currentAccount.userIDEquals(userID) || currentAccount.playerUUIDEquals(playerUUID))
                return true;
        return false;
    }

    public static class DuplicateAccountInformationException extends RuntimeException {
        DuplicateAccountInformationException(String msg) {
            super(msg);
        }
    }
}
