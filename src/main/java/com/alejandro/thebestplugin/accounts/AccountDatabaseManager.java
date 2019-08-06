package com.alejandro.thebestplugin.accounts;

import com.alejandro.thebestplugin.TheBestPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Provides implementation to serialize players and discord users
 * and store them by their IDs in the MySQL database provided.
 */
public class AccountDatabaseManager {

    public AccountDatabaseManager(TheBestPlugin plugin) {
        this.plugin = plugin;
    }

    private final TheBestPlugin plugin;

    public PluginAccountRegistry newAccountRegistry(Statement SQLStatement) {

        return new PluginAccountRegistry(retrieveAccountsFromDatabase(SQLStatement), plugin);
    }

    public String[][] retrieveAccountsFromDatabase(Statement SQLStatement) {

        ArrayList<String[]> accountArrayList = new ArrayList<>();

        try {

            ResultSet accountsTable = SQLStatement.executeQuery("SELECT * FROM `USERS`;");

            byte DISCORD_ID_INDEX = 1;
            byte UUID_INDEX = 2;

            while (accountsTable.next()) {

                String[] currentAccount = {accountsTable.getString(DISCORD_ID_INDEX), accountsTable.getString(UUID_INDEX)};

                accountArrayList.add(currentAccount);
            }

        } catch(SQLException e) {

            e.printStackTrace();
        }

        return accountArrayList.toArray(new String[0][]);
    }

    public void sendToDatabase(String[][] data, Statement SQLStatement) {

        byte DISCORD_ID_INDEX = 0;
        byte UUID_INDEX = 1;

        for (String[] account : data) {

            String discordId  = account[DISCORD_ID_INDEX];
            String playerUUID = account[UUID_INDEX];

            try {

                SQLStatement.executeQuery("INSERT INTO `USERS` (`discord_ID`, `minecraft_ID`) VALUES ('" + discordId + "', '" + playerUUID + "');");

            } catch (SQLException e) {

                plugin.getLogger().warning("Account (" + discordId + " / " + playerUUID + ") could not be sent to the database!");
            }
        }
    }
}