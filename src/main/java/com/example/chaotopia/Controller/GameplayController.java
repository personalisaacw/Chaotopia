package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.Commands;

public class GameplayController extends BaseController {
    /* Variables that the loadGame method will load
    Chao object
    //todo: create inventory class
     */

    private Chao chao;

    //loadGame (calls load game class)


    //play function
    public void playChao() {
        Commands.play(chao);
    }

    //sleep function
    public void sleepChao() {
        Commands.sleep(chao);
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
        //todo: remove item from inventory
    }

    //gifts a duck
    public void giftDuck() {
        //todo: remove item from inventory
    }

    //gifts a tv
    public void giftTV() {
        //todo: remove item from inventory
    }

    public void feedRedFruit() {
        //todo: feed the fruit
        //todo: play the fruit eating animation
        //todo: remove item from inventory
    }

    public void feedBlueFruit() {
        //todo: feed the fruit
        //todo: play the fruit eating animation
        //todo: remove item from inventory
    }

    public void feedGreenFruit() {
        //todo: feed the fruit
        //todo: play the fruit eating animation
        //todo: remove item from inventory
    }

    public void feedHeroFruit() {
        //todo: feed the fruit
        //todo: change alignment value
        //todo: play the fruit eating animation
        //todo: remove item from inventory
    }

    public void feedDarkFruit() {
        //todo: feed the fruit
        //todo: change alignment value
        //todo: play the fruit eating animation
        //todo: remove item from inventory
    }

    //todo: save button (saves all data to csv)

    //todo: back to mainMenu button
}
