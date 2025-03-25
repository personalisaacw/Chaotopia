package com.example.chaotopia.Model;

/**
 * Entity that tracks the sessional playtime of a particular game file.
 * <br><br>
 * The sessional playtime is initialized with {@link #Time} and can be
 * retrieved with {@link #getSessionPlaytime}. From there, the sessional
 * playtime is incremented with {@link #stepTime}. If the session is closed,
 * the playtime is recorded with {@link #storeTime}.
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

    /**
     * Constructor for the time entity.
     * <p>
     * Initializes the session playtime to zero and the beginning of the
     * first second to the current system time.
     *
     * @param gamefile the game file to load from
     */
    public Time(Gamefile gamefile) {
        sessionPlaytime = 0;
        secondStart = System.nanoTime();
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
    }

    /**
     * Deconstructor method for the current time entity.
     * <p>
     * Stores the new playtime in the game file. Then changes the
     * average playtime and total number of sessions.
     *
     * @param gamefile
     */
    public void storeTime(Gamefile gamefile) {
        gamefile.adjustPlaytime(sessionPlaytime);
        long playtime = gamefile.getPlaytime();
        gamefile.increaseNumSessions();
        int numSessions = gamefile.getNumSessions();
        long averagePlaytime = playtime/numSessions;
        gamefile.setAveragePlaytime(averagePlaytime);
    }
}