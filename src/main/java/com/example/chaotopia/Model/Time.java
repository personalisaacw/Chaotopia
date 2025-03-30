package com.example.chaotopia.Model;

/**
 * Entity that tracks the sessional playtime of the application.
 * <br><br>
 * The sessional playtime is initialized with {@link #Time} and can be
 * retrieved with {@link #getSessionPlaytime}. From there, the sessional
 * playtime is incremented with {@link #stepTime}. If the session is closed,
 * the playtime in memory is updated with {@link #storeTime}.
 *
 * @version 1.0.0
 * @author Justin Rowbotham
 */
public class Time {
    /** A second measured in nanoseconds. */
    private final long SECOND = 1000000000L;

    /** The current session playtime. */
    private long sessionPlaytime;
    /** The beginning of a second measured in system time. */
    private long secondStart;
    /** The current time on a 24-hour clock. */
    private java.time.LocalTime currentTime;

    /**
     * Constructor for the time entity.
     * <p>
     * Initializes the session playtime to zero and the beginning of the
     * first second to the current system time. Also initializes the
     * session start time to the system time.
     *
     * @param gameFile the game file to load from
     */
    public Time(GameFile gameFile) {
        sessionPlaytime = 0;
        secondStart = System.nanoTime();
        currentTime = java.time.LocalTime.now();
    }

    /**
     * Accessor method that returns the current session playtime.
     * @return sessionPlaytime the current session playtime
     */
    public long getSessionPlaytime() {
        return sessionPlaytime;
    }

    /**
     * Method that increments the session playtime if a second has passed.
     * The second start is then reset to begin measuring the next second.
     */
    public void stepTime() {
        /* If a second has passed, increase sessional playtime.
         * Reset time tracker. */
        if (System.nanoTime() - secondStart >= SECOND) {
            sessionPlaytime += 1;
            secondStart = System.nanoTime();
        }
        /* Update system time tracker. */
        currentTime = java.time.LocalTime.now();
    }

    /**
     * Method checking if a player can play the game given the current time.
     *
     * @return True if the current local time is within limitation bounds,
     *         false otherwise.
     */
    public boolean canPlay() {
        /* If the current time falls outside the range allowed by
         * the parent, the player can no longer play. */
        return ParentalLimitations.isPlayAllowed(currentTime);
    }

    /**
     * Method that formats playtime to be human-readable with an hours,
     * minutes, and seconds column.
     *
     * @param playtime the playtime in seconds
     * @return format a String displaying playtime
     */
    public static String displayTime(long playtime) {
        String format;
        long seconds = playtime % 60;
        long minutes = (playtime / 60) % 60;
        long hours = (playtime / 3600);
        format = String.format("%dh%dm%ds", hours, minutes, seconds);
        return format;
    }

    /**
     * "Deconstructor" method for the current time entity.
     * <p>
     * Stores the new playtime in the game file. Then changes the
     * average playtime and total number of sessions.
     *
     * @param gameFile the file to store the time to
     */
    public void storeTime(GameFile gameFile) {
        gameFile.setPlaytime(gameFile.getPlaytime() + sessionPlaytime);
        gameFile.setNumSessions(gameFile.getNumSessions() + 1);
    }
}