package com.example.chaotopia;

public class StatusTester {
    public static void main(String[] args) {
        // Test the default constructor
        System.out.println("Testing default constructor:");
        Status defaultStatus = new Status();
        defaultStatus.displayStats();
        System.out.println();

        // Test negative values (should be constrained to 0)
        System.out.println("Testing negative values (should be constrained to 0):");
        Status negativeStatus = new Status(-10, -20, -30, -40);
        negativeStatus.displayStats();
        System.out.println();

        // Test values above maximum (should be constrained to 100)
        System.out.println("Testing values above maximum (should be constrained to 100):");
        Status maxStatus = new Status(110, 120, 130, 140);
        maxStatus.displayStats();
        System.out.println();

        // Test the new updateStats method (increase values)
        System.out.println("Testing updateStats method (increase):");
        Status status1 = new Status(50, 50, 50, 50);
        System.out.println("Before update:");
        status1.displayStats();
        status1.updateStats(10, 20, 15, 5);
        System.out.println("After update (increase):");
        status1.displayStats();
        System.out.println();

        // Test the new updateStats method (decrease values)
        System.out.println("Testing updateStats method (decrease):");
        Status status2 = new Status(50, 50, 50, 50);
        System.out.println("Before update:");
        status2.displayStats();
        status2.updateStats(-10, -20, -15, -5);
        System.out.println("After update (decrease):");
        status2.displayStats();
        System.out.println();

        // Test the isDead method
        System.out.println("Testing isDead method:");
        Status livingStatus = new Status(50, 1, 50, 50);
        System.out.println("Living chao (health = 1):");
        livingStatus.displayStats();

        livingStatus.updateStats(0, -1, 0, 0);
        System.out.println("After health reduced to 0:");
        livingStatus.displayStats();
        System.out.println("Is dead? " + livingStatus.isDead());

        System.out.println("\nAll tests completed!");
    }
}