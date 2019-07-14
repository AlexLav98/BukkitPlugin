package com.alejandro;

import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class TheBestPlugin extends JavaPlugin {

    private JDA jda;
    private HashBiMap<Long, OfflinePlayer> linkedAccountsMap = HashBiMap.create();
    private AccountMapSerializationHandler serializationHandler;

    @Override
    public void onEnable() {

        /*
         * Initialize the global JDA instance, log the bot in,
         * and register event listeners.
         */
        try {

            jda = new JDABuilder(AccountType.BOT).setToken(getBotToken()).build().awaitReady();
        }

        catch (Exception e) { e.printStackTrace(); }


        /*
         * Initialize the MySQL database
         */
        try {
            Connection SQLConnection = DriverManager.getConnection("jdbc:mysql://198.245.51.96:3306/db_63051", "db_63051", "dc06f6ce63");
            if ( SQLConnection != null )
                getLogger().info("SQL CONNECTION ESTABLISHED");

            /*
             * Deserialize accountLinks in the database
             * and store them in the linked accounts map.
             */
            serializationHandler = new AccountMapSerializationHandler(SQLConnection, this, jda);
            linkedAccountsMap = serializationHandler.deserializeFromDatabase();

            /*
             * Register event handler classes for the Minecraft plugin
             */
            getServer().getPluginManager().registerEvents(new MainListener(linkedAccountsMap, jda), this);

            // This has to be registered here because it depends on linkedAccountsMap
            jda.addEventListener(new DiscordListener(this, linkedAccountsMap));

        } catch (SQLException e) { e.printStackTrace(); getLogger().warning("AN ERROR HAS OCCURRED AND MYSQL COULD NOT START"); }
    }

    @Override
    public void onDisable() {
        jda.shutdown();

        // Serialize the map of account links and sent it to the database
        serializationHandler.serializeAndSend(linkedAccountsMap);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("register-account") && args.length == 3) {

            Long          userIdLong     = Long.valueOf(args[1]);
            OfflinePlayer offlinePlayer  = Bukkit.getOfflinePlayer(UUID.fromString(args[2]));
            linkedAccountsMap.put(userIdLong, offlinePlayer);

            sender.sendMessage(ChatColor.GOLD + String.format("%s / %s has been registered",
                    jda.getUserById(userIdLong).getName(),
                    offlinePlayer.getName()
            ) + ChatColor.RESET);

            return true;
        }

        return false;
    }

    private String getBotToken() throws IOException {

        FileReader tokenTextFileReader = new FileReader("..\\..\\..\\resources\\bot_token.txt");
        BufferedReader tokenTextFileBufferedReader = new BufferedReader(tokenTextFileReader);

        return tokenTextFileBufferedReader.readLine();
    }
}