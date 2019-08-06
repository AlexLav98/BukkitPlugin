package com.alejandro.thebestplugin.accounts;

class AccountInfo {

    private final String userID;
    private final String playerUUID;

    AccountInfo(String userID, String playerUUID) {
        this.userID = userID;
        this.playerUUID = playerUUID;
    }

    public boolean userIDEquals(String expectedUserID) {
        return userID.equals(expectedUserID);
    }

    public boolean playerUUIDEquals(String expectedUUID) {
        return playerUUID.equals(expectedUUID);
    }

    public String getUserID() {
        return userID;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }
}
