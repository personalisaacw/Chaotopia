package com.example.chaotopia;

import com.example.chaotopia.Model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Junit test for the Commands class.
 */
class CommandsTest {

    /**
     * Test the sleep command, for the Commands class.
     */
    @Test
    void testSleep() {
        Status status = new Status(90, 90, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.NORMAL,
                status);
        Commands.sleep(chao);
        assertSame(State.SLEEPING, chao.getState());
    }

    /**
     * Test the feed command, for the Commands class.
     * Also test that the isNotAngry utility works for non-happy commands.
     */
    @Test
    void testIsNotAngryAndFeed() {
        /* Test the isNotAngry method. */
        Status status = new Status(90, 90, 60, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.ANGRY,
                status);
        Item item = new Item("Red Fruit");
        Commands.feed(chao, item);
        assertSame(60, chao.getStatus().getFullness());

        /* Now test the feed command. */
        chao.setState(State.NORMAL);
        Commands.feed(chao, item);
        assertSame(90, chao.getStatus().getFullness());
    }

    /**
     * Test the give command, for the Commands class.
     * Also test that the isNotAngry utility works for happiness commands.
     */
    @Test
    void testGiveAndIsNotAngry() {
        /* Ensure isNotAngry works for a happiness command. */
        Status status = new Status(85, 90, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.ANGRY,
                status);
        Item item = new Item("Trumpet");
        Commands.give(chao, item);
        assertSame(95, chao.getStatus().getHappiness());
    }

    /**
     * Test the vet command, for the Commands class.
     * Also test that the cooldownActive utility works.
     */
    @Test
    void testVetAndCooldown() {
        /* Check that the vet command works. */
        Status status = new Status(90, 25, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.NORMAL,
                status);
        Commands.vet(chao);
        assertSame(75, chao.getStatus().getHealth());

        /* Check that the cooldown works. */
        Commands.vet(chao);
        assertSame(75, chao.getStatus().getHealth());
    }

    /**
     * Test the play command, for the Commands class.
     * Also test the consciousCheck method.
     */
    @Test
    void testPlayAndIsConscious() {
        /* Check that consciousCheck command works for death. */
        Status status = new Status(50, 0, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.NORMAL,
                status);
        Commands.play(chao);
        assertSame(50, chao.getStatus().getHappiness());

        /* Now check for sleep compatibility. */
        chao.getStatus().adjustHealth(100);
        chao.setState(State.SLEEPING);
        Commands.play(chao);
        assertSame(50, chao.getStatus().getHappiness());

        /* Now test the play command. */
        chao.setState(State.NORMAL);
        Commands.play(chao);
        assertSame(75, chao.getStatus().getHappiness());
    }

    /**
     * Test the exercise command, for the Commands class.
     */
    @Test
    void testExercise() {
        /* Test the exercise command once. */
        Status status = new Status(90, 65, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.NORMAL,
                status);
        Commands.exercise(chao);
        assertSame(90, chao.getStatus().getHealth());
        assertSame(75, chao.getStatus().getFullness());
        assertSame(75, chao.getStatus().getSleep());

        /* Test it twice. */
        Commands.exercise(chao);
        assertSame(100, chao.getStatus().getHealth());
        assertSame(60, chao.getStatus().getFullness());
        assertSame(60, chao.getStatus().getSleep());
    }

    /**
     * Test the pet command, for the Commands class.
     */
    @Test
    void testPet() {
        /* Test the pet command. */
        Status status = new Status(90, 65, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.NORMAL,
                status);
        Commands.pet(chao);
        assertSame(1, chao.getAlignment());
    }

    /**
     * Test the bonk command, for the Commands class.
     */
    @Test
    void testBonk() {
        /* Test the bonk command. */
        Status status = new Status(90, 65, 90, 90);
        Chao chao = new Chao(0, "Bingy", ChaoType.RED, State.NORMAL,
                status);
        Commands.bonk(chao);
        assertSame(-1, chao.getAlignment());
    }
}