package com.example.chaotopia.Model;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    /** Flag determining if the limitations are enabled or not. */
    private static boolean enabled;
    /** The starting time of play allowed by the parent (in system time). */
    private static java.time.LocalTime allowedStartTime;
    /** The ending time of play allowed by the parent (in system time). */
    private static java.time.LocalTime allowedEndTime;
    /** Path for the limitations save file. */
    private static final String LIMITATIONS_FILE = "src/main/resources" +
            "/com/example/chaotopia/Saves/parental_limitations.json";

    private static final String DEFAULT_LIMITATIONS_JSON =
            "{\n" +
                    "  \"enabled\": false,\n" +
                    "  \"allowedStartTime\": \"00:00\",\n" +
                    "  \"allowedEndTime\": \"23:59:59.999999999\"\n" +
                    "}";

    /**
     * Constructor method for parental limitations.
     * Cannot be called due to the class being static.
     */
    private ParentalLimitations() {}

    /**
     * Method that loads the field variables stored in the save file.
     */
    public static void loadParentalLimitations() {
        ensureLimitationsFileExists();
        try {
            /* Get the .json file. */
            String content = new String(Files.readAllBytes(Paths
                    .get(LIMITATIONS_FILE)));
            JSONObject jsonData = new JSONObject(content);

            /* Load the values from the .json file. */
            enabled = jsonData.getBoolean("enabled");
            allowedStartTime = LocalTime.parse(jsonData.getString
                    ("allowedStartTime"));
            allowedEndTime = LocalTime.parse(jsonData.getString
                    ("allowedEndTime"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Method that stores the field variables stored into the save file.
     * Must be called before exiting the application.
     */
    public static void storeParentalLimitations() {
        try {
            /* Get the .json file. */
            String content = new String(Files.readAllBytes(Paths
                    .get(LIMITATIONS_FILE)));
            JSONObject jsonData = new JSONObject(content);

            /* Save values to the .json file. */
            jsonData.put("enabled", enabled);
            jsonData.put("allowedStartTime", allowedStartTime);
            jsonData.put("allowedEndTime", allowedEndTime);

            String updatedJsonString = jsonData.toString(4);
            Files.write(Paths.get(LIMITATIONS_FILE), updatedJsonString.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //creates the parental limitations json if it doesn't exist
    public static void ensureLimitationsFileExists() {
        Path filePath = Paths.get(LIMITATIONS_FILE);

        if (!Files.exists(filePath)) {
            try {
                Files.write(filePath, DEFAULT_LIMITATIONS_JSON.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
