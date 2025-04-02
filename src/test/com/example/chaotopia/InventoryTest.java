package com.example.chaotopia;

import com.example.chaotopia.Model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Inventory class to verify proper initialization and behavior.
 * Tests all methods including adding, removing, checking, counting items and clearing inventory.
 */
public class InventoryTest {

    private Inventory inventory;

    /**
     * Sets up a new inventory before each test.
     */
    @BeforeEach
    public void setUp() {
        // Create a fresh inventory before each test
        inventory = new Inventory();
    }

    /**
     * Tests that a new inventory is empty.
     */
    @Test
    public void testNewInventoryIsEmpty() {
        // A newly created inventory should not have any items
        assertFalse(inventory.hasItem("Trumpet"));
        assertEquals(0, inventory.getItemCount("Trumpet"));
    }

    /**
     * Tests adding a new item to the inventory.
     */
    @Test
    public void testAddNewItem() {
        // Add a new item to the inventory
        inventory.addItem("Trumpet", 1);

        // Verify the item exists and has the correct count
        assertTrue(inventory.hasItem("Trumpet"));
        assertEquals(1, inventory.getItemCount("Trumpet"));
    }

    /**
     * Tests adding multiple quantities of an item.
     */
    @Test
    public void testAddMultipleQuantity() {
        // Add multiple quantities of an item
        inventory.addItem("Duck", 5);

        // Verify the item has the correct count
        assertEquals(5, inventory.getItemCount("Duck"));
    }

    /**
     * Tests adding an existing item to the inventory.
     */
    @Test
    public void testAddExistingItem() {
        // Add an item twice
        inventory.addItem("Tv", 1);
        inventory.addItem("Tv", 2);

        // Verify the quantities are combined
        assertEquals(3, inventory.getItemCount("Tv"));
    }

    /**
     * Tests removing an item from the inventory.
     */
    @Test
    public void testRemoveItem() {
        // Add and then remove an item
        inventory.addItem("Green Fruit", 1);
        boolean result = inventory.removeItem("Green Fruit");

        // Verify the item was successfully removed
        assertTrue(result);
        assertFalse(inventory.hasItem("Green Fruit"));
    }

    /**
     * Tests removing a non-existent item from the inventory.
     */
    @Test
    public void testRemoveNonExistentItem() {
        // Try to remove an item that doesn't exist
        boolean result = inventory.removeItem("Blue Fruit");

        // Verify the removal operation failed
        assertFalse(result);
    }

    /**
     * Tests that removing an item decrements its quantity.
     */
    @Test
    public void testRemoveDecrementsQuantity() {
        // Add multiple of an item and remove one
        inventory.addItem("Red Fruit", 3);
        boolean result = inventory.removeItem("Red Fruit");

        // Verify one was removed, but the item still exists
        assertTrue(result);
        assertTrue(inventory.hasItem("Red Fruit"));
        assertEquals(2, inventory.getItemCount("Red Fruit"));
    }

    /**
     * Tests removing the last instance of an item.
     */
    @Test
    public void testRemoveLastItem() {
        // Add one of an item and remove it
        inventory.addItem("Dark Fruit", 1);
        inventory.removeItem("Dark Fruit");

        // Verify the item is completely removed
        assertFalse(inventory.hasItem("Dark Fruit"));
        assertEquals(0, inventory.getItemCount("Dark Fruit"));
    }

    /**
     * Tests checking if an item exists in the inventory.
     */
    @Test
    public void testHasItem() {
        // Test hasItem with items that exist and don't exist
        inventory.addItem("Hero Fruit", 1);

        assertTrue(inventory.hasItem("Hero Fruit"));
        assertFalse(inventory.hasItem("Unknown Item"));
    }

    /**
     * Tests getting the count of an item in the inventory.
     */
    @Test
    public void testGetItemCount() {
        // Test getting count of items with different quantities
        inventory.addItem("Trumpet", 1);
        inventory.addItem("Duck", 5);

        assertEquals(1, inventory.getItemCount("Trumpet"));
        assertEquals(5, inventory.getItemCount("Duck"));
        assertEquals(0, inventory.getItemCount("Nonexistent Item"));
    }

    /**
     * Tests clearing the inventory.
     */
    @Test
    public void testClearInventory() {
        // Add multiple items then clear the inventory
        inventory.addItem("Trumpet", 1);
        inventory.addItem("Duck", 2);
        inventory.addItem("Tv", 3);

        inventory.clearInventory();

        // Verify all items are removed
        assertFalse(inventory.hasItem("Trumpet"));
        assertFalse(inventory.hasItem("Duck"));
        assertFalse(inventory.hasItem("Tv"));
        assertEquals(0, inventory.getItemCount("Trumpet"));
    }

    /**
     * Tests adding an item with zero quantity.
     */
    @Test
    public void testAddZeroQuantity() {
        // Adding zero quantity should still add the item to inventory
        inventory.addItem("Trumpet", 0);

        assertTrue(inventory.hasItem("Trumpet"));
        assertEquals(0, inventory.getItemCount("Trumpet"));
    }

    /**
     * Tests adding a negative quantity to an item.
     */
    @Test
    public void testAddNegativeQuantity() {
        // Adding a negative quantity should decrease the count
        // First add some items to avoid negative counts
        inventory.addItem("Duck", 5);
        inventory.addItem("Duck", -2);

        assertEquals(3, inventory.getItemCount("Duck"));
    }

    /**
     * Tests adding a negative quantity to a new item.
     */
    @Test
    public void testAddNegativeToNewItem() {
        // Adding a negative quantity to a new item
        inventory.addItem("Trumpet", -3);

        assertTrue(inventory.hasItem("Trumpet"));
        assertEquals(-3, inventory.getItemCount("Trumpet"));
    }

    /**
     * Tests case insensitivity for item names.
     */
    @Test
    public void testCaseInsensitivity() {
        // The Inventory class uses String keys which are case-sensitive
        inventory.addItem("Trumpet", 1);

        assertTrue(inventory.hasItem("Trumpet"));
        assertFalse(inventory.hasItem("trumpet"));
        assertEquals(0, inventory.getItemCount("trumpet"));
    }
}