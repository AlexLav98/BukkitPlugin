package com.alejandro.thebestplugin.commands;

import com.alejandro.thebestplugin.TheBestPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MainCommandExecutor implements CommandExecutor {

    private final TheBestPlugin plugin;

    private final LocalCommand[] commands = {new RegisterAccountCommand()};

    public MainCommandExecutor(TheBestPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("launch")) {

            Player playerSender = (Player) sender;

            double targetDistance = Double.valueOf(args[0]); // meters
            sender.sendMessage("Target distance = " + targetDistance);

            double gravity = 9;  // m/s
            sender.sendMessage("Gravity is " + gravity + " m/s");

            double resultingVelocity = Math.sqrt( targetDistance * gravity * (1 / Math.sin( 0.5 * Math.PI )) );
            sender.sendMessage("Resultant velocity = " + resultingVelocity);

            double vectorValue = resultingVelocity / Math.sqrt( 2 );
            sender.sendMessage("Vector values = " + vectorValue);

            Vector arrowLaunchVector = new Vector(vectorValue, vectorValue, 0.0);

            playerSender.launchProjectile(Arrow.class, arrowLaunchVector);

            sender.sendMessage("Executed");

            return true;
        }

        for (LocalCommand currentCommand : commands) {

            if ((currentCommand.getName().equalsIgnoreCase(cmd.getName()) || currentCommand.getAliases().contains(cmd.getName())) &&
                    args.length == currentCommand.getNumberOfArgs()) {

                currentCommand.execute(plugin, sender, args);
                return true;
            }
        }

        return false;
    }
}
