package com.alejandro.thebestplugin;

import net.dv8tion.jda.core.JDA;

public class MainListenerWrapper {

    MainListenerWrapper(JDA jda, TheBestPlugin plugin) {
        this.jda = jda;
        this.plugin = plugin;
    }

    private JDA jda;
    private TheBestPlugin plugin;

    public void discordChatAction() {

    }
}