package com.example.chaotopia.Model;

/**
 * An object measuring the player's care for the Chao.
 * <br><br>
 * The score increases as the player performs actions and otherwise
 * raises their Chao. It serves as gauge of the player's total progress
 * in the game.
 * <br><br>
 * The Score is retrieved with {@link #getScore} and incremented
 * with {@link #setScore}.
 *
 * @version 1.0.0
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
        this.score = score;
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
        this.score = score;
    }
}
