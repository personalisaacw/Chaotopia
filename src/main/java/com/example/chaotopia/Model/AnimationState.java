package com.example.chaotopia;

/**
 * Defines the different animation states a Chao can have.
 * Each state corresponds to a specific behavior animation.
 */
public enum AnimationState {
    NORMAL(4),
    SIT(2),
    SLEEPING(2),
    HAPPY(4),
    ANGRY(3),
    HUNGRY(3),
    DEAD(2);

    private final int frameCount;

    /**
     * Constructor for AnimationState
     *
     * @param frameCount The default number of frames for this animation
     */
    AnimationState(int frameCount) {
        this.frameCount = frameCount;
    }

    /**
     * Returns the string representation used in resource file names.
     * Returns the enum name as-is (e.g., "NORMAL").
     */
    public String getResourceName() {
        return name();
    }

    /**
     * Returns the default number of frames for this animation.
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Convert from a State enum to the corresponding AnimationState
     *
     * @param state The Chao state
     * @return The corresponding animation state
     */
    public static AnimationState fromState(State state) {
        // Direct mapping from State to AnimationState
        switch(state) {
            case NORMAL:
                return NORMAL;
            case SIT:
                return SIT;
            case SLEEPING:
                return SLEEPING;
            case HAPPY:
                return HAPPY;
            case ANGRY:
                return ANGRY;
            case HUNGRY:
                return HUNGRY;
            case DEAD:
                return DEAD;
            default:
                return NORMAL; // Default to NORMAL if unknown
        }
    }
}