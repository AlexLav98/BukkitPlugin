package com.alejandro;

import org.bukkit.plugin.java.JavaPlugin;

public class TheBestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MainListener(this), this);
    }

    @Override
    public void onDisable() {

    }

}