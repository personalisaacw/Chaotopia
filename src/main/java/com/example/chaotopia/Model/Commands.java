package com.example.chaotopia.Model;

/**
 * Static entity or utility class for player-issued commands.
 * <br><br>
 * The player can command their Chao to {@link #sleep}, {@link #feed},
 * {@link #give} (receive a gift), {@link #vet} (go to vet), {@link #play},
 * {@link #exercise}, {@link #pet} (receive a pet), and {@link #bonk}
 * (be bonked).
 *
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
    /** The start of the cooldown for the pet command. */
    private static long petCooldown = System.nanoTime() - MINUTE;
    /** The start of the cooldown for the bonk command. */
    private static long bonkCooldown = System.nanoTime() - MINUTE;

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
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (isNotAngry(chao) != 0) return; // If Chao is angry...
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
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (isNotAngry(chao) != 0) return; // If Chao is angry...
        chao.getStatus().adjustFullness(food.getEffectValue());
    }

    /**
     * Gift command that increments the Chao's happiness stat based on
     * the passed gift item.
     * @param chao the Chao being commanded
     * @param gift the gift item being consumed
     */
    public static void give(Chao chao, Item gift) {
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        chao.getStatus().adjustHappiness(gift.getEffectValue());
    }

    /**
     * Vet command that heals the Chao at the vet.
     * @param chao the Chao being commanded
     */
    public static void vet(Chao chao) {
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (isNotAngry(chao) != 0) return; // If Chao is angry...
        if (cooldownActive(vetCooldown, CooldownType.VET) != 0) return;
        chao.getStatus().adjustHealth(VET_HEALTH);
    }

    /**
     * Play command that makes the Chao happier through play.
     * @param chao the Chao being commanded
     */
    public static void play(Chao chao) {
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (cooldownActive(playCooldown, CooldownType.PLAY) != 0) return;
        chao.getStatus().adjustHappiness(PLAY_HAPPINESS);
    }

    /**
     * Exercise command that makes the Chao healthier but decrements
     * fullness and sleepiness.
     * @param chao the Chao being commanded
     */
    public static void exercise(Chao chao) {
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (isNotAngry(chao) != 0) return; // If Chao is angry...
        chao.getStatus().updateStats(0,EXER_HEALTH,EXER_FULLNESS,
                EXER_SLEEPINESS);
    }

    /**
     * Pet command that aligns the Chao closer to being a Hero Chao.
     * @param chao the Chao being commanded
     */
    public static void pet(Chao chao) {
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (isNotAngry(chao) != 0) return; // If Chao is angry...
        if (cooldownActive(petCooldown, CooldownType.PET) != 0) return;
        chao.adjustAlignment(PET_VAL);
    }

    /**
     * Bonk command that aligns the Chao closer to being a Dark Chao.
     * @param chao the Chao being commanded
     */
    public static void bonk(Chao chao) {
        if (isConscious(chao) != 0) return; // If Chao is not conscious...
        if (cooldownActive(bonkCooldown, CooldownType.BONK) != 0) return;
        chao.adjustAlignment(BONK_VAL);
    }

    /**
     * Utility that checks if the Chao is conscious.
     * @param chao The Chao to be assessed
     * @return 0 if the Chao is conscious, or 1 if they are not
     */
    private static int isConscious(Chao chao) {
        if (chao.getState() == State.SLEEPING
                || chao.getStatus().isDead()) {
            System.out.println(chao.getName() + " is unresponsive!");
            return 1;
        } else return 0;
    }

    /**
     * Utility that checks if the Chao is angry.
     * @param chao The Chao to be assessed
     * @return 0 if the Chao is not angry, or 1 if they are
     */
    private static int isNotAngry(Chao chao) {
        if (chao.getState() == State.ANGRY) {
            System.out.println(chao.getName() + " is being uncooperative!");
            return 1;
        } else return 0;
    }

    /**
     * Utility that checks if the cooldown for an action has passed.
     * @param cooldown The last time the command was used
     * @param type The type of cooldown
     * @return 0 if the cooldown is over, 1 if not
     */
    private static int cooldownActive(long cooldown, CooldownType type) {
        /* Check the cooldown. */
        long currentTime = System.nanoTime();
        if (currentTime - cooldown >= MINUTE) {
            if (type == CooldownType.VET) vetCooldown = currentTime;
            else if (type == CooldownType.PLAY) playCooldown = currentTime;
            else if (type == CooldownType.PET) petCooldown = currentTime;
            else if (type == CooldownType.BONK) bonkCooldown = currentTime;
            return 0;
        } else {
            System.out.println("Too soon!");
            return 1;
        }
    }
}
