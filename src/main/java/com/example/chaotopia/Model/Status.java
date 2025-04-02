package com.example.chaotopia.Model;

import java.util.ArrayList;

/**
 * The Status class represents the vital statistics of a Chao character.
 * It tracks sleep, health, happiness, and fullness values, ensuring they
 * remain within valid ranges (0-100).
 * @author Rosaline Scully
 */
public class Status {
    /** The sleep level of the Chao. */
    private int sleep;

    /** The health level of the Chao. */
    private int health;

    /** The happiness level of the Chao.*/
    private int happiness;

    /**The fullness level of the Chao.*/
    private int fullness;

    /**The maximum value for any stat.*/
    private final int MAX_STAT = 100;

    /**The minimum value for any stat.*/
    private final int MIN_STAT = 0;

    /**Default constructor that initializes all stats to maximum value (100).*/
    public Status() {
        this.sleep = 100;
        this.health = 100;
        this.happiness = 100;
        this.fullness = 100;
    }

    /**
     * Constructor that initializes stats with specified values.
     * All values are validated to ensure they stay within the valid range.
     *
     * @param happiness the initial happiness value
     * @param health the initial health value
     * @param fullness the initial fullness value
     * @param sleep the initial sleep value
     */
    public Status(int happiness, int health, int fullness, int sleep) {
        this.sleep = validateStat(sleep);
        this.health = validateStat(health);
        this.happiness = validateStat(happiness);
        this.fullness = validateStat(fullness);
    }

    /**
     * Helper method to validate and constrain stats between MIN_STAT and MAX_STAT.
     *
     * @param value the value to validate
     * @return the validated value, constrained between MIN_STAT and MAX_STAT
     */
    private int validateStat(int value) {
        if (value < MIN_STAT) {
            return MIN_STAT;
        } else if (value > MAX_STAT) {
            return MAX_STAT;
        }
        return value;
    }

    /**
     * Gets the current sleep value.
     *
     * @return the sleep value
     */
    public int getSleep() {
        return sleep;
    }

    /**
     * Adjusts the sleep value, ensuring it stays within valid range.
     *
     * @param sleep the value by which to increment sleep
     */
    public void adjustSleep(int sleep) {
        this.sleep = validateStat(this.sleep + sleep);
    }

    /**
     * Gets the current health value.
     *
     * @return the health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Adjusts the health value, ensuring it stays within valid range.
     *
     * @param health the value by which to increment health
     */
    public void adjustHealth(int health) {
        this.health = validateStat(this.health + health);
    }

    /**
     * Gets the current happiness value.
     *
     * @return the happiness value
     */
    public int getHappiness() {
        return happiness;
    }

    /**
     * Adjusts the happiness value, ensuring it stays within valid range.
     *
     * @param happiness the value by which to increment happiness
     */
    public void adjustHappiness(int happiness) {
        this.happiness = validateStat(this.happiness + happiness);
    }

    /**
     * Gets the current fullness value.
     *
     * @return the fullness value
     */
    public int getFullness() {
        return fullness;
    }

    /**
     * Adjusts the fullness value, ensuring it stays within valid range.
     *
     * @param fullness the value by which to increment fullness
     */
    public void adjustFullness(int fullness) {
        this.fullness = validateStat(this.fullness + fullness);
    }

    /**
     * Sets all stats to specific values, with validation to ensure
     * they stay within valid range.
     *
     * @param happiness the new happiness value
     * @param health the new health value
     * @param fullness the new fullness value
     * @param sleep the new sleep value
     */
    public void setStats(int happiness, int health, int fullness, int sleep) {
        this.sleep = validateStat(sleep);
        this.health = validateStat(health);
        this.happiness = validateStat(happiness);
        this.fullness = validateStat(fullness);
    }

    /**
     * Updates all stats by specified amounts. The changes can be positive or negative.
     * All resulting values are validated to ensure they stay within valid range.
     *
     * @param happinessChange the amount to change happiness by
     * @param healthChange the amount to change health by
     * @param fullnessChange the amount to change fullness by
     * @param sleepChange the amount to change sleep by
     */
    public void updateStats(int happinessChange, int healthChange, int fullnessChange, int sleepChange) {
        this.happiness = validateStat(this.happiness + happinessChange);
        this.health = validateStat(this.health + healthChange);
        this.fullness = validateStat(this.fullness + fullnessChange);
        this.sleep = validateStat(this.sleep + sleepChange);
    }

    /**
     * Checks if the Chao is dead (health is 0 or less).
     *
     * @return true if the Chao is dead, false otherwise
     */
    public boolean isDead() {
        return this.health <= 0;
    }

    /**
     * Gets all current stats as an ArrayList in the order:
     * happiness, health, fullness, sleep.
     *
     * @return ArrayList containing all current stat values
     */
    public ArrayList<Integer> getCurrStats() {
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(this.getHappiness());
        stats.add(this.getHealth());
        stats.add(this.getFullness());
        stats.add(this.getSleep());

        return stats;
    }

}