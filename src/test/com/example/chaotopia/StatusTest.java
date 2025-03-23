package com.example.chaotopia;

import com.example.chaotopia.Model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class StatusTest {

    private Status defaultStatus;
    private Status customStatus;

    @BeforeEach
    public void setUp() {
        // Initialize with default constructor
        defaultStatus = new Status();

        // Initialize with custom values
        customStatus = new Status(75, 80, 85, 90);
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(100, defaultStatus.getHappiness());
        assertEquals(100, defaultStatus.getHealth());
        assertEquals(100, defaultStatus.getFullness());
        assertEquals(100, defaultStatus.getSleep());
    }

    @Test
    public void testCustomConstructor() {
        assertEquals(75, customStatus.getHappiness());
        assertEquals(80, customStatus.getHealth());
        assertEquals(85, customStatus.getFullness());
        assertEquals(90, customStatus.getSleep());
    }

    @Test
    public void testValidateStatLowerBound() {
        Status lowStatus = new Status(-10, -20, -30, -40);

        assertEquals(0, lowStatus.getHappiness());
        assertEquals(0, lowStatus.getHealth());
        assertEquals(0, lowStatus.getFullness());
        assertEquals(0, lowStatus.getSleep());
    }

    @Test
    public void testValidateStatUpperBound() {
        Status highStatus = new Status(110, 120, 130, 140);

        assertEquals(100, highStatus.getHappiness());
        assertEquals(100, highStatus.getHealth());
        assertEquals(100, highStatus.getFullness());
        assertEquals(100, highStatus.getSleep());
    }

    @Test
    public void testAdjustHappiness() {
        customStatus.adjustHappiness(15);
        assertEquals(90, customStatus.getHappiness());

        customStatus.adjustHappiness(-30);
        assertEquals(60, customStatus.getHappiness());

        // Test boundary
        customStatus.adjustHappiness(-100);
        assertEquals(0, customStatus.getHappiness());

        customStatus.adjustHappiness(200);
        assertEquals(100, customStatus.getHappiness());
    }

    @Test
    public void testAdjustHealth() {
        customStatus.adjustHealth(10);
        assertEquals(90, customStatus.getHealth());

        customStatus.adjustHealth(-25);
        assertEquals(65, customStatus.getHealth());

        // Test boundary
        customStatus.adjustHealth(-100);
        assertEquals(0, customStatus.getHealth());

        customStatus.adjustHealth(150);
        assertEquals(100, customStatus.getHealth());
    }

    @Test
    public void testAdjustFullness() {
        customStatus.adjustFullness(10);
        assertEquals(95, customStatus.getFullness());

        customStatus.adjustFullness(-20);
        assertEquals(75, customStatus.getFullness());

        // Test boundary
        customStatus.adjustFullness(-100);
        assertEquals(0, customStatus.getFullness());

        customStatus.adjustFullness(200);
        assertEquals(100, customStatus.getFullness());
    }

    @Test
    public void testAdjustSleep() {
        customStatus.adjustSleep(5);
        assertEquals(95, customStatus.getSleep());

        customStatus.adjustSleep(-15);
        assertEquals(80, customStatus.getSleep());

        // Test boundary
        customStatus.adjustSleep(-100);
        assertEquals(0, customStatus.getSleep());

        customStatus.adjustSleep(150);
        assertEquals(100, customStatus.getSleep());
    }

    @Test
    public void testSetStats() {
        defaultStatus.setStats(25, 35, 45, 55);

        assertEquals(25, defaultStatus.getHappiness());
        assertEquals(35, defaultStatus.getHealth());
        assertEquals(45, defaultStatus.getFullness());
        assertEquals(55, defaultStatus.getSleep());

        // Test with out-of-range values
        defaultStatus.setStats(-10, 120, 50, 200);

        assertEquals(0, defaultStatus.getHappiness());
        assertEquals(100, defaultStatus.getHealth());
        assertEquals(50, defaultStatus.getFullness());
        assertEquals(100, defaultStatus.getSleep());
    }

    @Test
    public void testUpdateStats() {
        customStatus.updateStats(10, -15, 5, -20);

        assertEquals(85, customStatus.getHappiness());
        assertEquals(65, customStatus.getHealth());
        assertEquals(90, customStatus.getFullness());
        assertEquals(70, customStatus.getSleep());

        // Test boundaries
        customStatus.updateStats(-100, -100, -100, -100);

        assertEquals(0, customStatus.getHappiness());
        assertEquals(0, customStatus.getHealth());
        assertEquals(0, customStatus.getFullness());
        assertEquals(0, customStatus.getSleep());

        customStatus.updateStats(200, 200, 200, 200);

        assertEquals(100, customStatus.getHappiness());
        assertEquals(100, customStatus.getHealth());
        assertEquals(100, customStatus.getFullness());
        assertEquals(100, customStatus.getSleep());
    }

    @Test
    public void testIsDead() {
        assertFalse(customStatus.isDead());

        customStatus.adjustHealth(-80);
        assertEquals(0, customStatus.getHealth());
        assertTrue(customStatus.isDead());

        customStatus.adjustHealth(50);
        assertEquals(50, customStatus.getHealth());
        assertFalse(customStatus.isDead());
    }

    @Test
    public void testGetCurrStats() {
        ArrayList<Integer> stats = customStatus.getCurrStats();

        assertEquals(4, stats.size());
        assertEquals(Integer.valueOf(75), stats.get(0)); // Happiness
        assertEquals(Integer.valueOf(80), stats.get(1)); // Health
        assertEquals(Integer.valueOf(85), stats.get(2)); // Fullness
        assertEquals(Integer.valueOf(90), stats.get(3)); // Sleep

        // Test after modifying a stat
        customStatus.adjustHappiness(15);
        stats = customStatus.getCurrStats();
        assertEquals(Integer.valueOf(90), stats.get(0)); // Updated happiness
    }

    // We don't test displayStats() as it's a void method that prints to console
    // In a real project, you might refactor it to return a String or use a dependency injection for the output
}