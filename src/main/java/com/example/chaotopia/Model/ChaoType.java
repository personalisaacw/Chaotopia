package com.example.chaotopia.Model;

/**
 * Defines the different types a Chao can have.
 * The type may change through evolution based on alignment.
 * @author Rosaline Scully
 */
public enum ChaoType {
    /**Stays happy longer*/
    HERO,
    /**Happiness depletes faster*/
    DARK,
    /**Gets sleepy faster*/
    BLUE,
    /**Gets hungry faster*/
    RED,
    /**Gets sick more often (health depletes faster)*/
    GREEN;

    /**
     * Returns the string representation used in resource file names.
     * Returns the enum name as-is (e.g., "DARK").
     */
    public String getResourceName() {
        // Return the enum name directly
        return name();
    }
}