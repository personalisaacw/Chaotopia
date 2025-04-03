package com.example.chaotopia.Model;

import com.example.chaotopia.Controller.GameplayController;
import javafx.scene.Scene;

/**
 * Static entity or utility class for player-issued commands.
 * <br><br>
 * The player can command their Chao to {@link #sleep}, {@link #feed},
 * {@link #give} (receive a gift), {@link #vet} (go to vet), {@link #play},
 * {@link #exercise}, {@link #pet} (receive a pet), and {@link #bonk}
 * (be bonked).
 *
 * There is also a method {@link #applyNaturalDecay(Chao)} to naturally decrease the
 * stats which the ChaoStatusController will use.
 * @version 1.0.0
 * @author Justin Rowbotham
 */
public final class Commands {
    /** A minute measured in nanoseconds. */
    private static final long MINUTE = 60000000000L;

    /** The health recovered from a trip to the vet. */
    private static final int VET_HEALTH = 50;
    /** The happiness gained from playing. */
    private static final int PLAY_HAPPINESS = 25;
    /** The health recovered from exercising. */
    private static final int EXER_HEALTH = 25;
    /** The sleepiness gained from exercising. */
    private static final int EXER_SLEEPINESS = -15;
    /** The fullness lost from exercising. */
    private static final int EXER_FULLNESS = -15;
    /** The evolutionary effect of petting a Chao. */
    private static final int PET_VAL = 1;
    /** The evolutionary effect of bonking a Chao. */
    private static final int BONK_VAL = -1;

    /** The start of the cooldown for the vet command. */
    private static long vetCooldown = System.nanoTime() - MINUTE;
    /** The start of the cooldown for the play command. */
    private static long playCooldown = System.nanoTime() - MINUTE;

    /** Enumerator identifying the cooldown type. */
    private enum CooldownType {
        VET, PLAY, PET, BONK
    }

    /**
     * Constructor for the com.example.chaotopia.Model.Commands class.
     * <p>
     * Cannot be called due to the class being static.
     */
    private Commands() {
    }

    /**
     * Sleep command that places the Chao into the sleeping state.
     * @param chao the Chao being commanded
     */
    public static void sleep(Chao chao) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        if (isNotAngry(chao) != null) return; // If Chao is angry...
        if (chao.getStatus().getSleep() >= 100) return;
        chao.setState(State.SLEEPING);
    }

    /**
     * Feed command that increments the Chao's fullness stat based on
     * the passed food item.
     * @param chao the Chao being commanded
     * @param food the food item being consumed
     */
    public static void feed(Chao chao, Item food) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        if (isNotAngry(chao) != null) return; // If Chao is angry...
        chao.getStatus().adjustFullness(food.getEffectValue());
    }

    /**
     * Feed command that changes the Chao's alignment value stat based on
     * the passed special fruit item.
     * @param chao the Chao being commanded
     * @param specialFruit the special fruit item being consumed
     */
    public static void feedSpecialFruit(Chao chao, Item specialFruit) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        if (isNotAngry(chao) != null) return; // If Chao is angry...
        chao.getStatus().adjustFullness(specialFruit.getEffectValue());
        chao.adjustAlignment(specialFruit.getAlignmentChange());
    }

    /**
     * Gift command that increments the Chao's happiness stat based on
     * the passed gift item.
     * @param chao the Chao being commanded
     * @param gift the gift item being consumed
     */
    public static void give(Chao chao, Item gift) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        chao.getStatus().adjustHappiness(gift.getEffectValue());
    }

    /**
     * Vet command that heals the Chao at the vet.
     * @param chao the Chao being commanded
     */
    public static String vet(Chao chao) {
        if (isConscious(chao) != null) return chao.getName() + " is unresponsive!";
        if (isNotAngry(chao) != null) return chao.getName() + " is being uncooperative!";
        if (cooldownActive(vetCooldown, CooldownType.VET) != null) return "Too soon!";
        chao.getStatus().adjustHealth(VET_HEALTH);
        return null; // Indicate success with no message needed, or return a success string
    }

    /**
     * Play command that makes the Chao happier through play.
     * Checks consciousness and cooldown.
     * @param chao the Chao being commanded
     * @return null if the command succeeded, or a String message indicating the reason for failure (e.g., "Too soon!").
     */
    public static String play(Chao chao) {
        String consciousCheck = isConscious(chao);
        if (consciousCheck != null) {
            System.out.println("Commands.play: Conscious check failed: " + consciousCheck); // DEBUG LOG
            return consciousCheck;
        }
        String cooldownCheck = cooldownActive(playCooldown, CooldownType.PLAY);
        if (cooldownCheck != null) {
            System.out.println("Commands.play: Cooldown check failed: " + cooldownCheck); // DEBUG LOG
            return cooldownCheck;
        }
        System.out.println("Commands.play: SUCCESS - Applying happiness."); // DEBUG LOG
        chao.getStatus().adjustHappiness(PLAY_HAPPINESS);
        return null;
    }

    /**
     * Exercise command that makes the Chao healthier but decrements
     * fullness and sleepiness.
     * @param chao the Chao being commanded
     */
    public static void exercise(Chao chao) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        if (isNotAngry(chao) != null) return; // If Chao is angry...
        chao.getStatus().updateStats(0,EXER_HEALTH,EXER_FULLNESS,
                EXER_SLEEPINESS);
    }

    /**
     * Pet command that aligns the Chao closer to being a Hero Chao.
     * @param chao the Chao being commanded
     */
    public static void pet(Chao chao) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        if (isNotAngry(chao) != null) return; // If Chao is angry...
        //if (cooldownActive(petCooldown, CooldownType.PET) != 0) return;
        chao.adjustAlignment(PET_VAL);
        chao.getStatus().adjustHappiness(3);
    }

    /**
     * Bonk command that aligns the Chao closer to being a Dark Chao.
     * @param chao the Chao being commanded
     */
    public static void bonk(Chao chao) {
        if (isConscious(chao) != null) return; // If Chao is not conscious...
        //if (cooldownActive(bonkCooldown, CooldownType.BONK) != 0) return;
        chao.adjustAlignment(BONK_VAL);
        chao.getStatus().adjustHappiness(-3);
    }

    /**
     * Applies natural stat decay over time to simulate hunger, tiredness, etc.
     * @param chao the Chao to apply decay to
     */
    public static void applyNaturalDecay(Chao chao) {
        if (chao.getStatus().isDead()) return;
        if (chao.getState() == State.SLEEPING) {
            chao.getStatus().adjustSleep(10);
        }

        Status status = chao.getStatus();
        int happinessDecrease = 2;
        int fullnessDecrease = 2;
        int sleepDecrease = 2;
        int healthDecrease = 0;

        // Apply type-specific modifications
        ChaoType type = chao.getType();
        if (type == ChaoType.DARK) {
            happinessDecrease = (int)(happinessDecrease * 1.5);
        } else if (type == ChaoType.BLUE) {
            sleepDecrease = (int)(sleepDecrease * 1.5);
        } else if (type == ChaoType.RED) {
            fullnessDecrease = (int)(fullnessDecrease * 1.5);
        } else if (type == ChaoType.HERO){
            happinessDecrease = (int)(happinessDecrease * 0.5);
        }

        // Handle hunger effects
        if (status.getFullness() == 0) {
            happinessDecrease *= 2;
            healthDecrease += 5;
            if (chao.getType() == ChaoType.GREEN) {
                healthDecrease += 2;
            }
        }

        // Update the stats
        status.updateStats(-happinessDecrease, -healthDecrease, -fullnessDecrease, -sleepDecrease);
    }

    /**
     * Method that resets the cooldowns on stats.
     */
    public static void resetCooldowns() {
        vetCooldown = System.nanoTime() - MINUTE;
        playCooldown = System.nanoTime() - MINUTE;
    }

    /**
     * Utility that checks if the Chao is conscious.
     * @param chao The Chao to be assessed
     * @return 0 if the Chao is conscious, or 1 if they are not
     */
    private static String isConscious(Chao chao) {
        if (chao.getState() == State.SLEEPING || chao.getStatus().isDead()) {
            return chao.getName() + " is unresponsive!"; // Return message
        } else return null; // Indicate success (conscious)
    }

    /**
     * Utility that checks if the Chao is angry.
     * @param chao The Chao to be assessed
     * @return 0 if the Chao is not angry, or 1 if they are
     */
    private static String isNotAngry(Chao chao) {
        if (chao.getState() == State.ANGRY) {
            return chao.getName() + " is being uncooperative!";
        } else return null; //success
    }

    /**
     * Utility that checks if the cooldown for an action has passed.
     * @param cooldown The last time the command was used
     * @param type The type of cooldown
     * @return 0 if the cooldown is over, 1 if not
     */
    private static String cooldownActive(long cooldown, CooldownType type) {
        /* Check the cooldown. */
        long currentTime = System.nanoTime();
        if (currentTime - cooldown >= MINUTE) {
            if (type == CooldownType.VET) vetCooldown = currentTime;
            else if (type == CooldownType.PLAY) playCooldown = currentTime;
            return null;
        } else {
            return "Too soon!";
        }
    }
}
