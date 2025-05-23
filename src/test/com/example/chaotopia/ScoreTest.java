package com.example.chaotopia;

import com.example.chaotopia.Model.Score;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test for the Score class.
 */
class ScoreTest {

    /**
     * Test for the getScore method, of the Score class.
     */
    @Test
    public void testGetScore() {
        int expScore = 0;
        Score score = new Score(0);
        assertEquals(expScore, score.getScore());
    }

    /**
     * Test for the setScore method, of the Score class.
     */
    @Test
    public void testSetScore() {
        int expScore = 10;
        Score score = new Score(0);
        score.setScore(score.getScore() + 10);
        assertEquals(expScore, score.getScore());
    }

    /**
     * Test for updateScore method of the Score class.
     */
    @Test
    public void testUpdateScore() {
        int expScore = 11;
        Score score = new Score(10);
        score.updateScore(1);
        assertEquals(expScore, score.getScore());
    }
}