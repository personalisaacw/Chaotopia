package com.example.chaotopia.Model;

/**
 * An object measuring the player's care for the Chao.
 * <br><br>
 * The score increases as the player performs actions and otherwise
 * raises their Chao. It serves as a gauge of the player's total progress
 * in the game.
 * <br><br>
 * The Score is retrieved with {@link #getScore} and incremented
 * with {@link #setScore}.
 *
 * @version 1.0.1
 * @author Justin Rowbotham
 */
public class Score {
    /** The score value itself representing player progress. */
    private int score;

    /**
     * Constructor method that creates a score.
     * @param score The initial score.
     */
    public Score(int score) {
        this.score = Math.max(score, 0); // Ensure non-negative score
    }

    /**
     * Accessor method that returns the score.
     * @return score the player's score for this game file
     */
    public int getScore() {
        return score;
    }

    /**
     * Mutator method that sets the score.
     * @param score the new score to implement
     */
    public void setScore(int score) {
        this.score = Math.max(score, 0); // Prevent negative score
    }

    /**
     * Updater method that updates the current score.
     * @param update the amount to increase or decrease by
     */
    public void updateScore(int update) {
        this.score = Math.max(this.score + update, 0); // Ensure score stays non-negative
    }
}
