package com.alejandro.thebestplugin;

import com.alejandro.thebestplugin.accounts.AccountDatabaseManager;
import com.alejandro.thebestplugin.accounts.PluginAccountRegistry;
import com.alejandro.thebestplugin.commands.MainCommandExecutor;
import com.alejandro.thebestplugin.listeners.DiscordListener;
import com.alejandro.thebestplugin.listeners.MainListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

public class TheBestPlugin extends JavaPlugin {

    private PluginAccountRegistry accountRegistry;
    private AccountDatabaseManager databaseManager;
    private JDA jda;

    private static final YamlConfiguration pluginYaml = new YamlConfiguration();

    private final Connection SQLConnection = DriverManager.getConnection(
            Objects.requireNonNull(getConfig().getString("db_url")),
            getConfig().getString("db_user"),
            getConfig().getString("db_pass")
    );

    {
        try {

            Reader pluginYamlTextResource = getTextResource("plugin.yml");

            if (pluginYamlTextResource != null) {
                pluginYaml.load(pluginYamlTextResource);
            } else {

                getLogger().warning("Yaml configuration loading has failed!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public TheBestPlugin() throws SQLException {
    }

    /**
     * The instance of the JDA used by the bot.
     *
     * @return Main JDA instance.
     */
    public JDA getJDA() {

        return jda;
    }

    public PluginAccountRegistry getAccountRegistry() {

        return accountRegistry;
    }

    public long inGameChannelIdLong() {
        return getConfig().getLong("in_game_channel_id");
    }

    public long consoleChannelIdLong() {
        return getConfig().getLong("command_channel_id");
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
         * Initialize the global JDA instance, and log the bot in.
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

            databaseManager = new AccountDatabaseManager(this);

            if ( SQLConnection != null ) {

                getLogger().info("SQL connection established...");
                accountRegistry = databaseManager.newAccountRegistry(SQLConnection.createStatement());

            } else {

                getLogger().warning("SQL Connection failed! Account registry could not load!");
            }

            /*
             * Register event handler classes for the Minecraft plugin
             */
            getServer().getPluginManager().registerEvents(new MainListener(accountRegistry, this), this);

        } catch (SQLException e) { e.printStackTrace(); getLogger().warning("An error has occurred and mySQL could not start!"); }

        jda.addEventListener(new DiscordListener(this, accountRegistry));
    }

    @Override
    public void onDisable() {
        jda.shutdown();

        // Serialize the account registry and send it to the database
        try {

            databaseManager.sendToDatabase( accountRegistry.serialize(), SQLConnection.createStatement() );

        } catch (SQLException e) {

            getLogger().warning("The account registry could not be sent to the database!");
        }
    }
}