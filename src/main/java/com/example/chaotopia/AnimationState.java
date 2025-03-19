package com.example.chaotopia;

/**
 * Defines the different animation states a Chao can have.
 * Each state corresponds to a specific behavior animation.
 */
public enum AnimationState {
    NORMAL(4),
    SIT(2),
    SLEEPING(2),
    HAPPY(2),
    ANGRY(3),
    HUNGRY(4),
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
        try {
            // Since State and AnimationState have matching names,
            // we can directly convert between them
            return valueOf(state.name());
        } catch (IllegalArgumentException e) {
            return NORMAL; // Default to NORMAL if no match is found
        }
    }
}