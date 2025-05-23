package com.example.chaotopia.Model;

/**
 * Represents an item in the game with properties like name, item type, description,
 * flavor text, and effect value.
 * Each item in the game can be of different types, such as GIFT, FOOD, or SPECIAL.
 * Items also have a description and flavor text to enhance the player's experience.
 */
public class Item {

    /**
     * The name of the item.
     */
    private String name;

    /**
     * The type of the item, such as GIFT, FOOD, or SPECIAL.
     */
    private ItemType itemType;

    /**
     * A description of the item, explaining its purpose or effect.
     */
    private String description;

    /**
     * A flavor text related to the item, providing additional context or flavor to the game.
     */
    private String flavorText;

    /**
     * The effect value of the item, which determines how much it affects the pet's happiness or other attributes.
     */
    private int effectValue;

    /**
     * The amount of alignment change that this item has. It can change Chao alignment by +1 if it is a Hero Fruit and -1 if it is a Dark Fruit.
     */
    private int alignmentChange;

    /**
     * Constructs an Item based on the given name. The item is initialized with specific
     * attributes like type, description, flavor text, effect value and alignment change based on the item name.
     *
     * @param name The name of the item, which determines its attributes.
     *             Valid item names include: Trumpet, Duck, T.V., Green Fruit, Blue Fruit,
     *             Red Fruit, Dark Fruit, and Hero Fruit.
     * @throws IllegalArgumentException if the item name is null or not recognized
     */
    public Item(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Item name cannot be null");
        }

        if (name.equals("Trumpet")) {
            this.name = "Trumpet";
            this.itemType = ItemType.GIFT;
            this.description = "This gift item boosts your pet's happiness by 10 points.";
            this.flavorText = "Blow the trumpet, and watch your pet groove to the rhythm of joy!";
            this.effectValue = 10;
        }
        else if (name.equals("Duck")) {
            this.name = "Duck";
            this.itemType = ItemType.GIFT;
            this.description = "This gift item boosts your pet's happiness by 20 points.";
            this.flavorText = "Quack! The duck's cheerful waddling always lifts the spirits. Your pet's happiness just went up a notch!";
            this.effectValue = 20; //will be used to give 20 happiness
        }
        else if (name.equals("T.V.")) {
            this.name = "T.V.";
            this.itemType = ItemType.GIFT;
            this.description = "This gift item boosts your pet's happiness by 30 points.";
            this.flavorText = "Binge-watch your pet's favorite shows! Hours of entertainment and pure happiness!";
            this.effectValue = 30; //will be used to give 30 happiness
        }
        else if (name.equals("Green Fruit")) {
            this.name = "Green Fruit";
            this.itemType = ItemType.FOOD;
            this.description = "This food item fills your pet's stomach by 30 points. It may also have special effects when fed to a green Chao.";
            this.flavorText = "Fresh from the garden! Sweet, tangy, and perfect for keeping your pet full and happy!";
            this.effectValue = 30; //will be used to give 30 fullness
        }
        else if (name.equals("Blue Fruit")) {
            this.name = "Blue Fruit";
            this.itemType = ItemType.FOOD;
            this.description = "This food item fills your pet's stomach by 40 points. It may also have special effects when fed to a blue Chao.";
            this.flavorText = "A burst of refreshing flavor straight from the blue skies! Your pet will love it!";
            this.effectValue = 40;
        }
        else if (name.equals("Red Fruit")) {
            this.name = "Red Fruit";
            this.itemType = ItemType.FOOD;
            this.description = "This food item fills your pet's stomach by 50 points. It may also have special effects when fed to a red Chao.";
            this.flavorText = "Ripe and juicy with a hint of spice! Your pet can't resist this tasty treat!";
            this.effectValue = 50;
        }
        else if (name.equals("Dark Fruit")) {
            this.name = "Dark Fruit";
            this.itemType = ItemType.SPECIAL;
            this.description = "This special item fills your pet's stomach by 10 points. It also makes your Chao feel evil, giving -1 points toward your Chaos alignment and increasing its chances of becoming a Dark Chao.";
            this.flavorText = "Darkness has its taste... and your pet can't get enough of it. A deliciously sinister choice!";
            this.effectValue = 10;
            this.alignmentChange = -1;
        }
        else if (name.equals("Hero Fruit")) {
            this.name = "Hero Fruit";
            this.itemType = ItemType.SPECIAL;
            this.description = "This special item fills your pet's stomach by 10 points. It also makes your Chao feel heroic, giving +1 points toward your Chaos alignment and increasing its chances of becoming a Hero Chao.";
            this.flavorText = "The fruit of champions! Your pet feels stronger and ready for anything. A true hero in the making!";
            this.effectValue = 10;
            this.alignmentChange = 1;
        }
        else {
            throw new IllegalArgumentException("Invalid item name: " + name + ". Valid items are: Trumpet, Duck, Tv, Green Fruit, Blue Fruit, Red Fruit, Dark Fruit, Hero Fruit");
        }
    }

    /**
     * Gets the name of the item.
     *
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the item type, such as GIFT, FOOD, or SPECIAL.
     *
     * @return The item type.
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * Gets the description of the item.
     *
     * @return The description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the flavor text of the item, providing additional context or fun facts about the item.
     *
     * @return The flavor text of the item.
     */
    public String getFlavorText() {
        return flavorText;
    }

    /**
     * Gets the effect value of the item, which determines its impact on the pet.
     * For example, a higher effect value may increase happiness or other pet stats.
     *
     * @return The effect value of the item.
     */
    public int getEffectValue() {
        return effectValue;
    }

    /**
     * Gets the alignment change value of the item, which determines how it changes Chao alignment.
     * For example, a Hero fruit gives Chao +1 alignment, making them more likely to evolve into a Hero Chao.
     *
     * @return The alignment change value of the item.
     */
    public int getAlignmentChange() {
        return alignmentChange;
    }
}