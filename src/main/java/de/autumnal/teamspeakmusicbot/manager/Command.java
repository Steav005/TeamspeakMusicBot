package de.autumnal.teamspeakmusicbot.manager;

public enum Command {
    JOIN("Let a Bot join your Channel", "!join"),
    LEAVE("Let a Bot leave your Channel", "!leave"),
    NEXT("Plays Next Song", "!next"),
    PREV("Plays Previous Song", "!prev"),
    PAUSE("Pauses Current Song", "!pause"),
    RESUME("Resumes Current Song", "!resume"),
    PLAYMODE("Change the Playmode to NORMAL, LOOPONE or LOOPALL", "!playmode <NORMAL/LOOPONE/LOOPALL>"),
    FORWARD("Forwards Song by <seconds> (backwards with minus)", "!forward <seconds>"),
    JUMP("Jumps to <seconds> in Song", "!jump <seconds>"),
    TOKEN("Provides a unique Token to a User", "!token"),
    NEWTOKEN("Provides a NEW unique Token to User", "!newtoken"),
    ADDRESS("Provedes the RESTful API Address", "!address");
    //LIST("Shows Current Song and upcoming Songs(10)", "!list");

    private String desc;
    private String bsp;

    Command(String desc, String bsp) {
        this.desc = desc;
        this.bsp = bsp;
    }

    public String getDescription() {
        return desc;
    }

    public String getExample(){
        return bsp;
    }
}
