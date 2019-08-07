package com.alejandro.thebestplugin.accounts;

class AccountInfoBuilder {

    private String userID;
    private String playerUUID;

    AccountInfoBuilder setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    AccountInfoBuilder setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    AccountInfo build() {
        return new AccountInfo(userID, playerUUID);
    }
}