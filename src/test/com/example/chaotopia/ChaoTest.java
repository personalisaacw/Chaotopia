package com.example.chaotopia;

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
        chao = new Chao(0, "TestChao", Chao.ChaoType.BLUE, Chao.State.IDLE, status);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(0, chao.getAlignment());
        assertEquals("TestChao", chao.getName());
        assertEquals(Chao.ChaoType.BLUE, chao.getType());
        assertEquals(Chao.State.IDLE, chao.getState());
        assertEquals(status, chao.getStatus());
    }

    @Test
    void testSetters() {
        chao.setType(Chao.ChaoType.RED);
        assertEquals(Chao.ChaoType.RED, chao.getType());

        chao.setState(Chao.State.HAPPY);
        assertEquals(Chao.State.HAPPY, chao.getState());
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
        Chao heroChao1 = new Chao(7, "HeroChao1", Chao.ChaoType.BLUE, Chao.State.IDLE, status);
        heroChao1.evolve();
        assertEquals(Chao.ChaoType.HERO, heroChao1.getType());

        // Test above threshold
        Chao heroChao2 = new Chao(10, "HeroChao2", Chao.ChaoType.BLUE, Chao.State.IDLE, status);
        heroChao2.evolve();
        assertEquals(Chao.ChaoType.HERO, heroChao2.getType());
    }

    @Test
    void testEvolveToDark() {
        // Test at threshold
        Chao darkChao1 = new Chao(-7, "DarkChao1", Chao.ChaoType.BLUE, Chao.State.IDLE, status);
        darkChao1.evolve();
        assertEquals(Chao.ChaoType.DARK, darkChao1.getType());

        // Test below threshold
        Chao darkChao2 = new Chao(-10, "DarkChao2", Chao.ChaoType.BLUE, Chao.State.IDLE, status);
        darkChao2.evolve();
        assertEquals(Chao.ChaoType.DARK, darkChao2.getType());
    }

    @Test
    void testNoEvolution() {
        // Test with alignment between thresholds
        chao.evolve();
        assertEquals(Chao.ChaoType.BLUE, chao.getType());

        // Test with positive alignment below hero threshold
        Chao positiveChao = new Chao(6, "PositiveChao", Chao.ChaoType.GREEN, Chao.State.IDLE, status);
        positiveChao.evolve();
        assertEquals(Chao.ChaoType.GREEN, positiveChao.getType());

        // Test with negative alignment above dark threshold
        Chao negativeChao = new Chao(-6, "NegativeChao", Chao.ChaoType.RED, Chao.State.IDLE, status);
        negativeChao.evolve();
        assertEquals(Chao.ChaoType.RED, negativeChao.getType());
    }

    @Test
    void testEvolutionWithChangingAlignment() {
        // Evolve to HERO
        chao.adjustAlignment(10);
        chao.evolve();
        assertEquals(Chao.ChaoType.HERO, chao.getType());

        // Evolve to DARK
        chao.adjustAlignment(-20);
        chao.evolve();
        assertEquals(Chao.ChaoType.DARK, chao.getType());

        // Test going back to HERO
        chao.adjustAlignment(30);
        chao.evolve();
        assertEquals(Chao.ChaoType.HERO, chao.getType());
    }
}