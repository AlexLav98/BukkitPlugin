package com.alejandro.thebestplugin.commands;

import com.alejandro.thebestplugin.TheBestPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Every command should have a name, aliases, and an execution method
 */
@SuppressWarnings("SameParameterValue")
abstract class LocalCommand {

    private final String name;
    private final int numberOfArgs;
    private List<String> aliases;

    LocalCommand(String name, int numberOfArgs) {

        this.name = name;
        this.numberOfArgs = numberOfArgs;
    }

    public LocalCommand(String name, int numberOfArgs, String... aliases) {

        this.name = name;
        this.numberOfArgs = numberOfArgs;
        this.aliases = Arrays.asList(aliases);
    }

    String getName() {
        return name;
    }

    int getNumberOfArgs() {
        return numberOfArgs;
    }

    List<String> getAliases() {
        return aliases;
    }

    public abstract void execute(TheBestPlugin plugin, CommandSender sender, @NotNull String[] args);
}
