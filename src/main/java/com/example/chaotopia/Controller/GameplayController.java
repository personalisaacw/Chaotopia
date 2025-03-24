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
        //todo: need to update the animation of the chao
    }

    //exercise function
    public void exerciseChao() {
        Commands.exercise(chao);
        //todo: need to update the animation of the chao
    }

    //vet function
    public void vetChao() {
        Commands.vet(chao);
        //todo: need to update the animation of the chao
    }

    //pet function
    public void petChao() {
        Commands.pet(chao);
        //todo: need to update the animation of the chao
    }

    //bonk function
    public void bonkChao() {
        Commands.bonk(chao);
        //todo: need to update the animation of the chao
    }

    //gifts a trumpet
    public void giftTrumpet() {
        //if item exists, give the item and remove it from inventory
        String itemName = "Trumpet";
        if (inventory.getItemCount(itemName) > 0) {
            Commands.give(chao, new Item(itemName));
            inventory.removeItem(itemName);
            //todo: play sound effect
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
            //todo: play sound effect
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
            //todo: play sound effect
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
            //todo: play sound effect
            //todo: play the fruit eating animation
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
            //todo: play sound effect
            //todo: play the fruit eating animation
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
            //todo: play sound effect
            //todo: play the fruit eating animation
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
            //todo: play sound effect
            //todo: play the fruit eating animation
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
            //todo: play sound effect
            //todo: play the fruit eating animation
        }
        //else:
        //todo: display "no items available"/play sound effect
    }

    //todo: save button (saves all data to csv)

    //todo: back to mainMenu button
}