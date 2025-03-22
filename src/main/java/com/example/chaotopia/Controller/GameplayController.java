package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.Commands;
import com.example.chaotopia.Model.Inventory;
import com.example.chaotopia.Model.Item;

public class GameplayController extends BaseController {

    private Chao chao;
    private Inventory inventory;

    //todo: loadGame function (calls load game class)


    //play function
    public void playChao() {
        Commands.play(chao);
    }

    //sleep function
    public void sleepChao() {
        Commands.sleep(chao);
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //exercise function
    public void exerciseChao() {
        Commands.exercise(chao);
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //vet function
    public void vetChao() {
        Commands.vet(chao);
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //pet function
    public void petChao() {
        Commands.pet(chao);
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //bonk function
    public void bonkChao() {
        Commands.bonk(chao);
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //gifts a trumpet
    public void giftTrumpet() {
        //if item exists, give the item and remove it from inventory
        String itemName = "Trumpet";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.give(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the chao animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    //gifts a duck
    public void giftDuck() {
        //if item exists, give the item and remove it from inventory
        String itemName = "Duck";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.give(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the chao animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    //gifts a tv
    public void giftTV() {
        //if item exists, give the item and remove it from inventory
        String itemName = "T.V.";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.give(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the chao animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    public void feedRedFruit() {
        //if item exists, feed the fruit and remove it from inventory
        String itemName = "Red Fruit";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.feed(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the fruit eating animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    public void feedBlueFruit() {
        //if item exists, feed the fruit and remove it from inventory
        String itemName = "Blue Fruit";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.feed(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the fruit eating animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    public void feedGreenFruit() {
        //if item exists, feed the fruit and remove it from inventory
        String itemName = "Green Fruit";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.feed(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the fruit eating animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    public void feedHeroFruit() {
        //if item exists, feed the fruit and remove it from inventory
        String itemName = "Hero Fruit";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.feedSpecialFruit(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the fruit eating animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    public void feedDarkFruit() {
        //if item exists, feed the fruit and remove it from inventory
        String itemName = "Dark Fruit";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.feedSpecialFruit(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: add keyboard shortcuts
            //todo: play the fruit eating animation
            //todo: play the proper sound
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    //todo: save button (saves all data to csv)

    //todo: back to mainMenu button
}
