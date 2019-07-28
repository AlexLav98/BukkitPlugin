package com.alejandro;

import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

public class TheBestPlugin extends JavaPlugin {

    private HashBiMap<Long, OfflinePlayer> linkedAccountsMap = HashBiMap.create();
    private AccountMapSerializationHandler serializationHandler;
    private JDA jda;

    private static final YamlConfiguration pluginYaml = new YamlConfiguration();
    private static final YamlConfiguration configYaml = new YamlConfiguration();

    {
        try {

            Reader pluginYamlTextResource = getTextResource("plugin.yml");
            Reader configYamlTextResource = getTextResource("config.yml");

            if (pluginYamlTextResource != null && configYamlTextResource != null) {
                pluginYaml.load(pluginYamlTextResource);
                configYaml.load(configYamlTextResource);
            } else {

                getLogger().warning("Yaml configuration loading has failed!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * The instance of the JDA used by the bot.
     *
     * @return Main JDA instance.
     */
    public JDA getJDA() {

        return jda;
    }

    public HashBiMap<Long, OfflinePlayer> getLinkedAccountsMap() {

        return linkedAccountsMap;
    }

    long inGameChannelIdLong() {
        return configYaml.getLong("in_game_channel_id");
    }

    long consoleChannelIdLong() {
        return configYaml.getLong("command_channel_id");
    }

    /**
     * Register all commands to have the main CommandExecutor instance.
     */
    private void registerCommands() {

        ConfigurationSection configSection = pluginYaml.getConfigurationSection("commands");

        if (configSection == null) {

            getLogger().warning("Configuration section \"commands\" was not found in plugin.yml!");
            return;
        }

        Set<String> commandNameStringSet = configSection.getKeys(false);
        CommandExecutor commandExecutor  = new MainCommandExecutor(this);

        PluginCommand command;
        for (String commandName : commandNameStringSet) {

            command = this.getCommand(commandName);

            if (command == null) {

                getLogger().warning("Command (" + commandName.toUpperCase() + ") was not found!");
                continue;
            }

            command.setExecutor(commandExecutor);
        }
    }

    /**
     * Accesses the plugin's pluginYaml.yml for the "auth" key
     *
     * @return A string representing the bot's auth token
     */
    private String getBotToken() {

        return getConfig().getString("auth");
    }

    @Override
    public void onEnable() {

        /*
         * Initialize the global JDA instance, log the bot in,
         * and register event listeners.
         */
        try {

            jda = new JDABuilder(AccountType.BOT).setToken(getBotToken()).build().awaitReady();

            /*
             * Register the CommandExecutor for all commands
             */
            registerCommands();
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
            getServer().getPluginManager().registerEvents(new MainListener(linkedAccountsMap, jda, this), this);

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
}