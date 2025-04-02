package com.example.chaotopia.Model;

/**
 * Defines the different animation states a Chao can have.
 * Each state corresponds to a specific behavior animation.
 * @author Rosaline Scully, Modified
 */
public enum AnimationState {
    NORMAL(4),
    SIT(2),
    SLEEPING(2),
    HAPPY(2),
    ANGRY(2),
    HUNGRY(4),
    DEAD(2),
    EVOLVING(2);

    private final int frameCount;

    AnimationState(int frameCount) {
        this.frameCount = frameCount;
    }

    public String getResourceName() {
        // Special case for EVOLVING animation if it uses SIT sprites
        if (this == EVOLVING) {
            return AnimationState.SIT.name(); // Use SIT sprites for EVOLVING animation
        }
        return name();
    }

    public int getFrameCount() {
        // Special case for EVOLVING animation if it uses SIT sprites
        if (this == EVOLVING) {
            return AnimationState.SIT.getFrameCount(); // Use SIT framecount
        }
        return frameCount;
    }

    /**
     * Convert from a State enum to the corresponding AnimationState
     *
     * @param state The Chao logical state
     * @return The corresponding animation state
     */
    public static AnimationState fromState(State state) {
        if (state == null) return NORMAL; // Handle null case

        switch (state) {
            case NORMAL: return NORMAL;
            case SIT: return SIT;
            case SLEEPING: return SLEEPING;
            case HAPPY: return HAPPY;
            case ANGRY: return ANGRY;
            case HUNGRY: return HUNGRY;
            case DEAD: return DEAD;
            case EVOLVING: return EVOLVING; // Map State.EVOLVING to AnimationState.EVOLVING
            default: return NORMAL;
        }
    }
}