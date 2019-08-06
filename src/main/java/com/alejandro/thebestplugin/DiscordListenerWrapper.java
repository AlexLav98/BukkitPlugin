package com.alejandro.thebestplugin;

import net.dv8tion.jda.core.JDA;

public class DiscordListenerWrapper {

    public DiscordListenerWrapper(TheBestPlugin plugin) {
        this.jda = plugin.getJDA();
        this.plugin = plugin;
    }

    private JDA jda;
    private TheBestPlugin plugin;

    public void guildMessageRecievedAction() {

    }
}
