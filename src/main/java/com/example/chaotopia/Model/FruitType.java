package com.example.chaotopia.Model;

/**
 * Defines the different types of fruits that can be displayed and animated.
 * Each type corresponds to a specific fruit appearance.
 * @author Rosaline Scully
 */
public enum FruitType {
    /** Hero fruit increases Hero alignment and hunger by 10 */
    HERO,
    /** Dark fruit increase Dark alignment and hunger by 10*/
    DARK,
    /** Red fruit increase hunger by 50 */
    RED,
    /**Green fruit increases hunger by 30 */
    GREEN,
    /**Blue fruit increases hunger by 40 */
    BLUE;

    /**
     * Returns the string representation used in resource file names.
     * Returns the enum name as-is (all uppercase) for file path construction.
     */
    public String getResourceName() {
        return name();
    }
}