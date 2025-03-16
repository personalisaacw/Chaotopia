package com.example.chaotopia;

/**
 * The ItemType enum defines the types of items that can exist in the inventory.
 * Each type represents a category that an item can belong to, which helps classify items 
 * in the game for different purposes or actions.
 */
public enum ItemType {

    /**
     * Represents food items in the game that can be consumed by pets to increase their stats.
     */
    FOOD,

    /**
     * Represents gift items that can be given to pets, usually to increase happiness or affection.
     */
    GIFT,

    /**
     * Represents special items in the game with unique properties or effects.
     */
    SPECIAL
}
