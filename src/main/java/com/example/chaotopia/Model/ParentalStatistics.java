package com.example.chaotopia.Model;

import java.io.IOException;

/**
 * Static entity handling the playtime statistics.
 * <br><br>
 * The class loads the total playtime and number of sessions
 * across all files with {@link #loadParentalStatistics}. The data
 * can be accessed with {@link #getTotalPlaytime()} and
 * {@link #getAveragePlaytime()}, and is reset with {@link #resetStatistics}.
 *
 * @version 1.0.0
 * @author Justin Rowbotham
 */
public final class ParentalStatistics {
    /** The total playtime across all files in seconds. */
    private static long totalPlaytime;
    /** The total number of sessions across all files in seconds. */
    private static int numSessions;

    /**
     * Constructor method for parental statistics.
     * Cannot be called due to the class being static.
     */
    private ParentalStatistics() {}

    /**
     * Method that loads the playtime statistics from the game files.
     *
     * @param gameFiles the game files to load from
     */
    public static void loadParentalStatistics(GameFile[] gameFiles) {
        /* Initialize field variables. */
        totalPlaytime = 0L;
        numSessions = 0;
        /* Sum playtime and number of sessions across all three game files. */
        for (int i = 0; i < gameFiles.length; i++) {
            if (gameFiles[i] != null) {
                totalPlaytime += gameFiles[i].getPlaytime();
                numSessions += gameFiles[i].getNumSessions();
            }
        }
    }

    /**
     * Accessor method that returns the total playtime across all games.
     *
     * @return totalPlaytime the total playtime across all games
     */
    public static long getTotalPlaytime() {
        return totalPlaytime;
    }

    /**
     * Accessor method that returns the average playtime across all games.
     *
     * @return The average playtime across all game files.
     */
    public static long getAveragePlaytime() {
        if (numSessions == 0) {
            return 0;
        }
        return (long)(totalPlaytime / numSessions);
    }

    /**
     * Method that resets all the playtime statistics.
     * <p>
     * Sets the total playtime and number of sessions per game file to zero
     * and stores the data in memory.
     *
     * @param gameFiles the game files to store to
     */
    public static void resetStatistics(GameFile[] gameFiles) {
        /* Reset playtime statistics. */
        totalPlaytime = 0L;
        numSessions = 0;
        /* Reset the save data for playtime statistics. */
        for (int i = 0; i < gameFiles.length; i++) {
            if (gameFiles[i] != null) {
                gameFiles[i].setPlaytime(totalPlaytime);
                gameFiles[i].setNumSessions(numSessions);
                try {
                    gameFiles[i].save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
