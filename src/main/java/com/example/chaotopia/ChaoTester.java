package com.example.chaotopia;

public class ChaoTester {
    public static void main(String[] args) {
        // Create a Status object for our Chao
        Status status = new Status(80, 90, 85, 95);

        // Create a new Chao
        Chao chao = new Chao(0, "Sonic", Chao.ChaoType.BLUE, Chao.State.IDLE, status);

        // Test initial values
        System.out.println("=== Initial Chao State ===");
        System.out.println("Name: " + chao.getName());
        System.out.println("Type: " + chao.getType());
        System.out.println("Alignment: " + chao.getAlignment());
        System.out.println("State: " + chao.getState());
        System.out.println("Status:");
        chao.getStatus().displayStats();
        System.out.println();

        // Test changing alignment and evolving
        System.out.println("=== Testing Alignment Changes and Evolution ===");
        System.out.println("Initial Type: " + chao.getType());

        // Test making the Chao a HERO
        System.out.println("Adding +8 alignment...");
        chao.adjustAlignment(8);
        System.out.println("New Alignment: " + chao.getAlignment());
        chao.evolve();
        System.out.println("After evolve, Type: " + chao.getType());
        System.out.println();

        // Reset type and test making the Chao DARK
        System.out.println("Resetting type to BLUE...");
        chao.setType(Chao.ChaoType.BLUE);
        // Reset alignment and go negative
        System.out.println("Setting alignment to -8...");
        chao = new Chao(-8, "Shadow", Chao.ChaoType.BLUE, Chao.State.UPSET, status);
        System.out.println("New Alignment: " + chao.getAlignment());
        chao.evolve();
        System.out.println("After evolve, Type: " + chao.getType());
        System.out.println();

        // Test different types
        System.out.println("=== Testing Different Types ===");
        Chao redChao = new Chao(0, "Knuckles", Chao.ChaoType.RED, Chao.State.HAPPY, status);
        System.out.println("Red Chao Type: " + redChao.getType());

        Chao greenChao = new Chao(0, "Amy", Chao.ChaoType.GREEN, Chao.State.SLEEPING, status);
        System.out.println("Green Chao Type: " + greenChao.getType());
        System.out.println();

        // Test state changes
        System.out.println("=== Testing State Changes ===");
        System.out.println("Initial State: " + chao.getState());
        chao.setState(Chao.State.HAPPY);
        System.out.println("New State: " + chao.getState());
        chao.setState(Chao.State.SLEEPING);
        System.out.println("New State: " + chao.getState());
        System.out.println();

        // Test Status interaction
        System.out.println("=== Testing Status Interaction ===");
        System.out.println("Initial Status:");
        chao.getStatus().displayStats();

        // Update status values
        System.out.println("Updating Status...");
        chao.getStatus().updateStats(-10, -5, -15, -20);
        System.out.println("Updated Status:");
        chao.getStatus().displayStats();

        System.out.println("\nAll tests completed!");
    }
}
