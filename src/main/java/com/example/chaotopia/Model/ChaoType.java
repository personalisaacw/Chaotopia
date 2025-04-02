package com.example.chaotopia.Model;

/**
 * Defines the different types a Chao can have.
 * The type may change through evolution based on alignment.
 * @author Rosaline Scully
 */
public enum ChaoType {
    HERO, //Stays happy longer, higher satisfactions from gifts
    DARK, //Happiness depletes faster
    BLUE, //Gets sleepy faster
    RED, //Gets hungry faster
    GREEN; //Gets sick more often (health depletes faster)

    /**
     * Returns the string representation used in resource file names.
     * Returns the enum name as-is (e.g., "DARK").
     */
    public String getResourceName() {
        // Return the enum name directly
        return name();
    }
}