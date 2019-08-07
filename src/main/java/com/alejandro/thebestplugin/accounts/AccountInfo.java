package com.alejandro.thebestplugin.accounts;

class AccountInfo {

    private final String userID;
    private final String playerUUID;

    AccountInfo(String userID, String playerUUID) {
        this.userID = userID;
        this.playerUUID = playerUUID;
    }

    public boolean hasDuplicatesOf(AccountInfo accountInfo) {
        String userID = accountInfo.getUserID();
        String playerUUID = accountInfo.getPlayerUUID();

        return accountInfo.userIDEquals(userID) || accountInfo.playerUUIDEquals(playerUUID);
    }

    private boolean userIDEquals(String expectedUserID) {
        return userID.equals(expectedUserID);
    }

    private boolean playerUUIDEquals(String expectedUUID) {
        return playerUUID.equals(expectedUUID);
    }

    String getUserID() {
        return userID;
    }

    String getPlayerUUID() {
        return playerUUID;
    }
}
