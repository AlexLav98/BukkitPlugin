package com.alejandro.thebestplugin.accounts;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

class RegisteredAccount {

    static final int numberOfColumns = 2;
    private static final int DISCORD_ID_INDEX = 0;
    private static final int UUID_INDEX = 1;

    private Pair<User, OfflinePlayer> account;
    private final String[] serializedForm = new String[numberOfColumns];

    private final JDA jda;
    private final AccountInfo accountInfo;

    RegisteredAccount(AccountInfo accountInfo, JDA jda) {
        this.jda = jda;
        this.accountInfo = accountInfo;

        initializeAccount();
    }

    private void initializeAccount() {
        setSerializedForm();
        account = Pair.of(getTargetUser(), getTargetOfflinePlayer());
    }

    private void setSerializedForm() {
        serializedForm[DISCORD_ID_INDEX] = accountInfo.getUserID();
        serializedForm[UUID_INDEX] = accountInfo.getPlayerUUID();
    }

    private User getTargetUser() {
        return jda.getUserById(accountInfo.getUserID());
    }

    private OfflinePlayer getTargetOfflinePlayer() {
        UUID UUIDFromString = UUID.fromString(accountInfo.getPlayerUUID());
        return Bukkit.getOfflinePlayer(UUIDFromString);
    }

    AccountInfo getInfo() {
        return accountInfo;
    }

    User getUser() {
        return account.getLeft();
    }

    OfflinePlayer getOfflinePlayer() {
        return account.getRight();
    }

    boolean playerEquals(OfflinePlayer player) {
        return getOfflinePlayer().equals(player);
    }

    boolean playerUUIDEquals(String playerUUID) {
        return getOfflinePlayer().getUniqueId().toString().equals(playerUUID);
    }

    boolean userEquals(User user) {
        return getUser().equals(user);
    }

    boolean userIDEquals(String userID) {
        return getUser().getId().equals(userID);
    }

    String[] getSerializedForm() {
        return serializedForm;
    }
}
