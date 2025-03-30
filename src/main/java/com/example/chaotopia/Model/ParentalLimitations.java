package com.example.chaotopia.Model;

import java.time.LocalTime;

/**
 * Static entity tracking the parental limitations placed on the game.
 * <br><br>
 * If the parent or admin enables the parental limitations (with
 * {@link #toggleFeature}) and sets a start time and end time for play
 * (with {@link #setAllowedTime}), the player cannot play on any save
 * file unless they are playing in the set range. This is checked with
 * {@link #isPlayAllowed}.
 *
 * @version 1.0.0
 * @author Justin Rowbotham
 */
public final class ParentalLimitations {
    /* Flag determining if the limitations are enabled or not. */
    static boolean enabled;
    /* The starting time of play allowed by the parent (in system time). */
    static java.time.LocalTime allowedStartTime;
    /* The ending time of play allowed by the parent (in system time). */
    static java.time.LocalTime allowedEndTime;

    /**
     * Constructor method for parental limitations.
     * Cannot be called due to the class being static.
     */
    private ParentalLimitations() {}

    /**
     * Method that loads the field variables stored in the save file.
     */
    public static void loadParentalLimitations() {
        enabled = GameFile.getLimitationStatus();
        allowedStartTime = GameFile.getLimitationStart();
        allowedEndTime = GameFile.getLimitationEnd();
    }

    /**
     * Mutator method that toggles the parental limitations.
     */
    public static void toggleFeature() {
        enabled = !enabled;
    }

    /**
     * Mutator method that sets the start and end times for the parental
     * limitations.
     *
     * @param newAllowedStartTime the start of the allowed play time
     * @param newAllowedEndTime the end of the allowed play time
     */
    public static void setAllowedTime(java.time.LocalTime newAllowedStartTime,
                          java.time.LocalTime newAllowedEndTime) {
        allowedStartTime = newAllowedStartTime;
        allowedEndTime = newAllowedEndTime;
    }

    /**
     * Method that determines if a player can play given the current system
     * time.
     *
     * @param currentTime the current time of play
     * @return True if the game can be played, false otherwise.
     */
    public static boolean isPlayAllowed(java.time.LocalTime currentTime) {
        return !enabled || (currentTime.isAfter(allowedStartTime)
                && currentTime.isBefore(allowedEndTime));
    }
}
