package com.example.chaotopia.Model;

/**
 * This is a stub.
 */

public class GameFile {
    private long playtime;
    private int numSessions;
    private long averagePlaytime;

    public void adjustPlaytime(long additionalTime) {
        this.playtime += additionalTime;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void increaseNumSessions() {
        numSessions++;
    }

    public int getNumSessions() {
        return numSessions;
    }

    public void setAveragePlaytime(long average) {
        this.averagePlaytime = average;
    }
}
