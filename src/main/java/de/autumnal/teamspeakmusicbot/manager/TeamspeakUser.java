package de.autumnal.teamspeakmusicbot.manager;

public class TeamspeakUser {
    private int databaseID;
    private int userID;
    private int channelID;

    public TeamspeakUser(int userID, int databaseID, int channelID){
        this.userID = userID;
        this.databaseID = databaseID;
        this.channelID = channelID;
    }

    public int getChannelID() {
        return channelID;
    }

    public int getDatabaseID() {
        return databaseID;
    }

    public int getUserID() {
        return userID;
    }
}
