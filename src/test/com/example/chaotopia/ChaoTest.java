package com.example.chaotopia;

import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.ChaoType;
import com.example.chaotopia.Model.State;
import com.example.chaotopia.Model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Chao class.
 * Note: This test assumes that Status can be instantiated directly.
 */
public class ChaoTest {

    private Status status;
    private Chao chao;

    @BeforeEach
    void setUp() {
        // Assuming Status can be instantiated directly
        // Modify this if Status has a different constructor
        status = new Status();
        chao = new Chao(0, "TestChao", ChaoType.BLUE, State.NORMAL, status);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(0, chao.getAlignment());
        assertEquals("TestChao", chao.getName());
        assertEquals(ChaoType.BLUE, chao.getType());
        assertEquals(State.NORMAL, chao.getState());
        assertEquals(status, chao.getStatus());
    }

    @Test
    void testSetters() {
        chao.setType(ChaoType.RED);
        assertEquals(ChaoType.RED, chao.getType());

        chao.setState(State.SLEEPING);
        assertEquals(State.SLEEPING, chao.getState());
    }

    @Test
    void testAdjustAlignment() {
        chao.adjustAlignment(5);
        assertEquals(5, chao.getAlignment());

        chao.adjustAlignment(-8);
        assertEquals(-3, chao.getAlignment());
    }

    @Test
    void testEvolveToHero() {
        // Test at threshold
        Chao heroChao1 = new Chao(7, "HeroChao1", ChaoType.BLUE, State.NORMAL, status);
        heroChao1.evolve();
        assertEquals(ChaoType.HERO, heroChao1.getType());

        // Test above threshold
        Chao heroChao2 = new Chao(10, "HeroChao2", ChaoType.BLUE, State.NORMAL, status);
        heroChao2.evolve();
        assertEquals(ChaoType.HERO, heroChao2.getType());
    }

    @Test
    void testEvolveToDark() {
        // Test at threshold
        Chao darkChao1 = new Chao(-7, "DarkChao1", ChaoType.BLUE, State.NORMAL, status);
        darkChao1.evolve();
        assertEquals(ChaoType.DARK, darkChao1.getType());

        // Test below threshold
        Chao darkChao2 = new Chao(-10, "DarkChao2", ChaoType.BLUE, State.NORMAL, status);
        darkChao2.evolve();
        assertEquals(ChaoType.DARK, darkChao2.getType());
    }

    @Test
    void testNoEvolution() {
        // Test with alignment between thresholds
        chao.evolve();
        assertEquals(ChaoType.BLUE, chao.getType());

        // Test with positive alignment below hero threshold
        Chao positiveChao = new Chao(6, "PositiveChao", ChaoType.GREEN, State.NORMAL, status);
        positiveChao.evolve();
        assertEquals(ChaoType.GREEN, positiveChao.getType());

        // Test with negative alignment above dark threshold
        Chao negativeChao = new Chao(-6, "NegativeChao", ChaoType.RED, State.NORMAL, status);
        negativeChao.evolve();
        assertEquals(ChaoType.RED, negativeChao.getType());
    }

    @Test
    void testEvolutionWithChangingAlignment() {
        // Evolve to HERO
        chao.adjustAlignment(10);
        chao.evolve();
        assertEquals(ChaoType.HERO, chao.getType());

        // Evolve to DARK
        chao.adjustAlignment(-20);
        chao.evolve();
        assertEquals(ChaoType.DARK, chao.getType());

        // Test going back to HERO
        chao.adjustAlignment(30);
        chao.evolve();
        assertEquals(ChaoType.HERO, chao.getType());
    }
}