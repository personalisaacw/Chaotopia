/**
 * An object measuring the player's care for the Chao.
 * <br><br>
 * The score increases as the player performs actions and otherwise
 * raises their Chao. It serves as gauge of the player's total progress
 * in the game. It also associates with its particular Chao.
 * <br><br>
 * The Score is retrieved with {@link getScore} and incremented
 * with {@link setScore}. There are also methods to access and mutate
 * the Chao, {@link getChao} and {@link setChao} respectively.
 *
 * @version 1.0.0
 * @author Justin Rowbotham
 */
public class Score {
    /** The score value itself representing player progress. */
    private int score;
    /** The Chao associated with the score entity. */
    private Chao chao;

    /**
     * Constructor method that creates a score.
     * @param score The initial score.
     * @param chao The Chao associated with this score.
     */
    public Score(int score, Chao chao) {
        this.score = score;
        this.chao = chao;
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

    /**
     * Accessor method that returns the Chao.
     * @return chao the Chao of this game file
     */
    public Chao getChao() {
        return chao;
    }

    /**
     * Mutator method that returns the Chao.
     * @param chao the new Chao to set under this score
     */
    public void setChao(Chao chao) {
        this.chao = chao;
    }
}
