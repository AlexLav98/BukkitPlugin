package com.alejandro.thebestplugin.commands;

import com.alejandro.thebestplugin.TheBestPlugin;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class RegisteredAccount {

    public static final int numberOfColumns = 2;

    private Pair<User, OfflinePlayer> account;
    public final String[] serializedForm = new String[numberOfColumns];

    private final JDA jda;

    private RegisteredAccount(String discordID, String minecraftUUID, TheBestPlugin plugin) {
        this.jda = plugin.getJDA();

        initializeAccount(discordID, minecraftUUID);
    }

    private void initializeAccount(String discordID, String minecraftUUID) {
        setSerializedForm(discordID, minecraftUUID);
        User userToAdd = getUserByIdString(discordID);
        OfflinePlayer offlinePlayerToAdd = getOfflinePlayerByUUIDString(minecraftUUID);
        account = Pair.of(userToAdd, offlinePlayerToAdd);
    }

    private void setSerializedForm(String discordID, String minecraftUUID) {
        final int DISCORD_ID_INDEX = 0;
        final int UUID_INDEX = 1;
        serializedForm[DISCORD_ID_INDEX] = discordID;
        serializedForm[UUID_INDEX] = minecraftUUID;
    }

    private User getUserByIdString(String id) {
        return jda.getUserById(id);
    }

    private OfflinePlayer getOfflinePlayerByUUIDString(String uuid) {
        return Bukkit.getOfflinePlayer(UUIDfromString(uuid));
    }

    private UUID UUIDfromString(String string) {
        return UUID.fromString(string);
    }

    public User getUser() {
        return account.getLeft();
    }

    public OfflinePlayer getOfflinePlayer() {
        return account.getRight();
    }

    public boolean playerEquals(OfflinePlayer player) {
        return getOfflinePlayer().equals(player);
    }

    public boolean playerUUIDEquals(String playerUUID) {
        return getOfflinePlayer().getUniqueId().toString().equals(playerUUID);
    }

    public boolean userEquals(User user) {
        return getUser().equals(user);
    }

    public boolean userIDEquals(String userID) {
        return getUser().getId().equals(userID);
    }

    public static RegisteredAccount newInstance(String discordIdentifier, String minecraftUUID, TheBestPlugin plugin) {
        return new RegisteredAccount(discordIdentifier, minecraftUUID, plugin);
    }
}
