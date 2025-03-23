package com.example.chaotopia.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Inventory class represents an inventory system that holds a collection of items.
 * Each item is tracked by its name and quantity in the inventory.
 * This class allows adding, removing, and querying items, as well as clearing the entire inventory.
 */
public class Inventory {

    /**
     * A map that stores item names and their corresponding quantities.
     * The key is the item's name, and the value is the quantity of that item.
     */
    private Map<String, Integer> itemQuantities;

    /**
     * Constructs a new Inventory object with an empty item list.
     */
    public Inventory() {
        itemQuantities = new HashMap<String, Integer>();
    }

    /**
     * Adds a specified quantity of an item to the inventory. If the item already exists,
     * its quantity is increased by the specified amount. If it doesn't exist, the item is added
     * with the given quantity.
     *
     * @param itemName The name of the item to be added to the inventory.
     * @param itemQuantity The quantity of the item to be added (default is 1).
     */
    public void addItem(String itemName, int itemQuantity) {
        // updating the item number in the map
        if (itemQuantities.containsKey(itemName)) { // if the item is already in the map, add the item quantity to the existing quantity
            itemQuantities.put(itemName, itemQuantities.get(itemName) + itemQuantity);
        } else { // if the item doesn't exist in the map, set the initial quantity
            itemQuantities.put(itemName, itemQuantity);
        }
    }

    /**
     * Removes one unit of the specified item from the inventory. If the item exists and its quantity
     * is greater than 1, the quantity is reduced by one. If the item has only one unit, it is completely
     * removed from the inventory.
     *
     * @param itemName The name of the item to be removed.
     * @return true if the item was successfully removed, false if the item does not exist in the inventory.
     */
    public boolean removeItem(String itemName) {
        // check if the item exists in the inventory
        if (itemQuantities.containsKey(itemName)) {
            int currentQuantity = itemQuantities.get(itemName);

            // if there is only 1 item in the inventory, remove it completely
            if (currentQuantity == 1) {
                itemQuantities.remove(itemName);
            } else {
                // if there's more than 1 item in the inventory, decrease the quantity by 1
                itemQuantities.put(itemName, currentQuantity - 1);
            }

            return true;
        }

        // return false if the item doesn't exist
        return false;
    }

    /**
     * Checks whether the inventory contains the specified item.
     *
     * @param itemName The name of the item to check for in the inventory.
     * @return true if the item exists in the inventory (regardless of quantity), false otherwise.
     */
    public boolean hasItem(String itemName) {
        return itemQuantities.containsKey(itemName);
    }

    /**
     * Retrieves the current quantity of the specified item in the inventory.
     * If the item does not exist in the inventory, 0 is returned.
     *
     * @param itemName The name of the item whose quantity is being queried.
     * @return The quantity of the specified item, or 0 if the item is not present in the inventory.
     */
    public int getItemCount(String itemName) {
        // return the quantity of the item or 0 if it doesn't exist
        return itemQuantities.getOrDefault(itemName, 0);
    }

    /**
     * Clears all items from the inventory, resetting it to an empty state.
     */
    public void clearInventory() {
        itemQuantities.clear();
    }
}