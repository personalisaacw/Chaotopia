package com.example.chaotopia.Model;

/**
 * Defines the different types of fruits that can be displayed and animated.
 * Each type corresponds to a specific fruit appearance.
 * @author Rosaline Scully
 */
public enum FruitType {
    HERO,
    DARK,
    RED,
    GREEN,
    BLUE;

    /**
     * Returns the string representation used in resource file names.
     * Returns the enum name as-is (all uppercase) for file path construction.
     */
    public String getResourceName() {
        return name();
    }
}