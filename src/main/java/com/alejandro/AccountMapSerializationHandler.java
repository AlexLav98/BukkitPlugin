package com.alejandro;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.JDA;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Provides implementation to serialize players and discord users
 * and store them by their IDs in the MySQL database provided.
 */
public class AccountMapSerializationHandler {

    public AccountMapSerializationHandler(Connection SQLConnection, JavaPlugin plugin, JDA jda) {
        this.SQLConnection = SQLConnection;
        this.plugin = plugin;
        this.jda = jda;
    }

    private Connection SQLConnection;
    private JavaPlugin plugin;
    private JDA jda;

    public void serializeAndSend(BiMap<Long, OfflinePlayer> linkedAccountsMap) {

        HashBiMap<Long, String> serializedLinkedAccountsMap = HashBiMap.create();

        linkedAccountsMap.forEach((userIdLong, offlinePlayer) -> {

            serializedLinkedAccountsMap.put(userIdLong, offlinePlayer.getUniqueId().toString());

            String serializedUserDiscordName   = jda.getUserById(userIdLong).getName();
            String serializedUserMinecraftName = offlinePlayer.getName();

            plugin.getLogger().info(String.format("Serialized account: %s / %s", serializedUserDiscordName, serializedUserMinecraftName));
        });

        sendToDatabase(serializedLinkedAccountsMap);
    }

    public HashBiMap<Long, OfflinePlayer> deserializeFromDatabase() {

        final int DISCORD_ID_ROW = 1;
        final int UUID_ROW = 2;

        HashBiMap<Long, OfflinePlayer> deserializedAccountsMap = HashBiMap.create();

        try {
            Statement SQLStatement = SQLConnection.createStatement();
            ResultSet accountsTable = SQLStatement.executeQuery("SELECT * FROM `USERS`;");

            // Go through every column, and add the data needed to the deserialized list.
            while (accountsTable.next()) {
                deserializedAccountsMap.put(
                        accountsTable.getLong(  DISCORD_ID_ROW  ),
                        Bukkit.getOfflinePlayer(UUID.fromString(accountsTable.getString(  UUID_ROW  )))
                );
            }

            // Tell the logger each user we just deserialized
            deserializedAccountsMap.forEach((userIdLong, offlinePlayer) -> {

                String deserializedUserDiscordName   = jda.getUserById(userIdLong).getName();
                String deserializedUserMinecraftName = offlinePlayer.getName();

                plugin.getLogger().info(String.format("Deserialized account: %s / %s", deserializedUserDiscordName, deserializedUserMinecraftName));
            });

        } catch (SQLException e) { e.printStackTrace(); plugin.getLogger().warning("AN ERROR HAS OCCURRED AND AND THE ACCOUNTS LIST COULD NOT BE DESERIALIZED"); }

        // return the deserialized list
        return deserializedAccountsMap;
    }

    private void sendToDatabase(BiMap<Long, String> serializedLinkedAccountsMap) {

        try {
            Statement SQLStatement = SQLConnection.createStatement();
            serializedLinkedAccountsMap.forEach((discord_id, minecraft_id) -> {

                try {
                    SQLStatement.execute("INSERT INTO `USERS` (`discord_ID`, `minecraft_ID`) VALUES (" + discord_id + ", '" + minecraft_id + "');");
                } catch (SQLException e) {}
            });
        } catch (SQLException e) { e.printStackTrace(); plugin.getLogger().warning("AN ERROR HAS OCCURRED AND THE SERIALIZED DATA COULD NOT BE SENT TO THE DATABASE"); }
    }
}