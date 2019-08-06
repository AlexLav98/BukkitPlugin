package com.alejandro.thebestplugin;

public class AccountInfoBuilder {

    private String userID;
    private String playerUUID;

    public AccountInfoBuilder setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public AccountInfoBuilder setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public AccountInfo build() {
        return new AccountInfo(userID, playerUUID);
    }
}