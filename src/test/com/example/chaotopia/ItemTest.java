package com.example.chaotopia;

import com.example.chaotopia.Model.Item;
import com.example.chaotopia.Model.ItemType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Item class to verify proper initialization and behavior.
 * Tests all the available item types and validates their properties.
 */
public class ItemTest {

    @Test
    public void testGiftItems() {
        // Test Trumpet item
        Item trumpet = new Item("Trumpet");
        assertEquals("Trumpet", trumpet.getName());
        assertEquals(ItemType.GIFT, trumpet.getItemType());
        assertEquals("This gift item boosts your pet's happiness by 10 points.", trumpet.getDescription());
        assertEquals("Blow the trumpet, and watch your pet groove to the rhythm of joy!", trumpet.getFlavorText());
        assertEquals(10, trumpet.getEffectValue());

        // Test Duck item
        Item duck = new Item("Duck");
        assertEquals("Duck", duck.getName());
        assertEquals(ItemType.GIFT, duck.getItemType());
        assertEquals("This gift item boosts your pet's happiness by 20 points.", duck.getDescription());
        assertEquals("Quack! The duck's cheerful waddling always lifts the spirits. Your pet's happiness just went up a notch!", duck.getFlavorText());
        assertEquals(20, duck.getEffectValue());

        // Test TV item
        Item tv = new Item("T.V.");
        assertEquals("T.V.", tv.getName());
        assertEquals(ItemType.GIFT, tv.getItemType());
        assertEquals("This gift item boosts your pet's happiness by 30 points.", tv.getDescription());
        assertEquals("Binge-watch your pet's favorite shows! Hours of entertainment and pure happiness!", tv.getFlavorText());
        assertEquals(30, tv.getEffectValue());
    }

    @Test
    public void testFoodItems() {
        // Test Green Fruit
        Item greenFruit = new Item("Green Fruit");
        assertEquals("Green Fruit", greenFruit.getName());
        assertEquals(ItemType.FOOD, greenFruit.getItemType());
        assertEquals("This food item fills your pet's stomach by 30 points. It may also have special effects when fed to a green Chao.", greenFruit.getDescription());
        assertEquals("Fresh from the garden! Sweet, tangy, and perfect for keeping your pet full and happy!", greenFruit.getFlavorText());
        assertEquals(30, greenFruit.getEffectValue());

        // Test Blue Fruit
        Item blueFruit = new Item("Blue Fruit");
        assertEquals("Blue Fruit", blueFruit.getName());
        assertEquals(ItemType.FOOD, blueFruit.getItemType());
        assertEquals("This food item fills your pet's stomach by 40 points. It may also have special effects when fed to a blue Chao.", blueFruit.getDescription());
        assertEquals("A burst of refreshing flavor straight from the blue skies! Your pet will love it!", blueFruit.getFlavorText());
        assertEquals(40, blueFruit.getEffectValue());

        // Test Red Fruit
        Item redFruit = new Item("Red Fruit");
        assertEquals("Red Fruit", redFruit.getName());
        assertEquals(ItemType.FOOD, redFruit.getItemType());
        assertEquals("This food item fills your pet's stomach by 50 points. It may also have special effects when fed to a red Chao.", redFruit.getDescription());
        assertEquals("Ripe and juicy with a hint of spice! Your pet can't resist this tasty treat!", redFruit.getFlavorText());
        assertEquals(50, redFruit.getEffectValue());
    }

    @Test
    public void testSpecialItems() {
        // Test Dark Fruit
        Item darkFruit = new Item("Dark Fruit");
        assertEquals("Dark Fruit", darkFruit.getName());
        assertEquals(ItemType.SPECIAL, darkFruit.getItemType());
        assertEquals("This special item fills your pet's stomach by 10 points. It also makes your Chao feel evil, giving -1 points toward your Chaos alignment and increasing its chances of becoming a Dark Chao.", darkFruit.getDescription());
        assertEquals("Darkness has its taste... and your pet can't get enough of it. A deliciously sinister choice!", darkFruit.getFlavorText());
        assertEquals(10, darkFruit.getEffectValue());

        // Test Hero Fruit
        Item heroFruit = new Item("Hero Fruit");
        assertEquals("Hero Fruit", heroFruit.getName());
        assertEquals(ItemType.SPECIAL, heroFruit.getItemType());
        assertEquals("This special item fills your pet's stomach by 10 points. It also makes your Chao feel heroic, giving +1 points toward your Chaos alignment and increasing its chances of becoming a Hero Chao.", heroFruit.getDescription());
        assertEquals("The fruit of champions! Your pet feels stronger and ready for anything. A true hero in the making!", heroFruit.getFlavorText());
        assertEquals(10, heroFruit.getEffectValue());
    }

    @Test
    public void testInvalidItem() {
        // Test an invalid item name - now should throw IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Item("Invalid Item");
        });
        assertTrue(exception.getMessage().contains("Invalid item name"));
    }

    @Test
    public void testCaseSensitivity() {
        // Test case sensitivity - now should throw IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Item("trumpet");  // "Trumpet" with lowercase
        });
        assertTrue(exception.getMessage().contains("Invalid item name"));
    }

    @Test
    public void testNullItemName() {
        // Test null item name - now should throw IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Item(null);
        });
        assertTrue(exception.getMessage().contains("Item name cannot be null"));
    }

    @Test
    public void testItemTypeEnum() {
        // Verify that all item types used in the Item class exist in the ItemType enum
        // This is a meta-test to ensure consistency between Item class and ItemType enum
        assertNotNull(ItemType.GIFT);
        assertNotNull(ItemType.FOOD);
        assertNotNull(ItemType.SPECIAL);
    }
}