package com.alejandro.thebestplugin.accounts;

import com.alejandro.thebestplugin.TheBestPlugin;
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

    private static final Set<RegisteredAccount> registry = new HashSet<>();
    private static final int DISCORD_ID_INDEX = 0;
    private static final int UUID_INDEX = 1;

    private final TheBestPlugin plugin;
    private String[] account;

    public PluginAccountRegistry(String[][] serializedAccountsArray, TheBestPlugin plugin) {
        this.plugin = plugin;
        registerAllAccountsIn(serializedAccountsArray);
    }

    private void registerAllAccountsIn(String[][] serializedAccounts) {
        for (String[] account : serializedAccounts) {
            this.account = account;
            registry.add(new RegisteredAccount(getAccountInfo(), getPluginJDA()));

            logAccount();
        }
    }

    private AccountInfo getAccountInfo() {
        String userID = account[DISCORD_ID_INDEX];
        String playerUUID = account[UUID_INDEX];

        return new AccountInfoBuilder()
                .setUserID(userID)
                .setPlayerUUID(playerUUID)
                .build();
    }

    private void logAccount() {
        String discordName = getAccountDiscordName();
        String minecraftName = getAccountOfflinePlayerName();
        getPluginLogger().info("Deserialized account: " + discordName + " / " + minecraftName);
    }

    private String getAccountDiscordName() {
        String userID = account[DISCORD_ID_INDEX];
        User accountDiscordUser = getUserByIdString(userID);
        return accountDiscordUser.getName();
    }

    private String getAccountOfflinePlayerName() {
        String playerUUID = account[UUID_INDEX];
        OfflinePlayer accountOfflinePlayer = getOfflinePlayerByUUIDString(playerUUID);
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
            serializedAccounts[accountIndex] = currentAccount.getSerializedForm();
        }
    }

    public @Nullable User getUserByPlayer(@NotNull OfflinePlayer player) {
        for (RegisteredAccount currentAccount : registry)
            if (currentAccount.playerEquals(player))
                return currentAccount.getUser();
        return null;
    }

    public @Nullable OfflinePlayer getPlayerByUser(@NotNull User user) {
        for (RegisteredAccount account : registry)
            if (account.userEquals(user))
                return account.getOfflinePlayer();
        return null;
    }

    public void addAccount(AccountInfo accountInfo) {

        for (RegisteredAccount account : registry)
            if (account.getInfo().hasDuplicatesOf(accountInfo))
                throw new DuplicateAccountInformationException();

        registry.add(new RegisteredAccount(accountInfo, getPluginJDA()));
    }

    public static class DuplicateAccountInformationException extends RuntimeException {
        DuplicateAccountInformationException() {
            super("Duplicate credentials, check your info carefully");
        }
    }
}