package com.example.chaotopia;

/**
 * Defines the different types a Chao can have.
 * The type may change through evolution based on alignment.
 */
public enum ChaoType {
    //Hero Chao stays happy for longer and higher satisfaction from gifts
    HERO,
    //Dark Chao's happiness depletes faster
    DARK,

    //Blue Chao gets sleepy faster
    BLUE,

    //Red Chao gets hungry faster
    RED,

    //Green Chao gets sick faster (health depletes quicker)
    GREEN
}
