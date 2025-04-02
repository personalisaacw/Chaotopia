package com.example.chaotopia.Model;

/**
 * Defines the different animation states a Chao can have.
 * Each state corresponds to a specific behavior animation.
 *
 * @author Rosaline Scully
 */
public enum AnimationState {
    /** The normal animation state. */
    NORMAL(4),

    /** The sitting animation state. */
    SIT(2),

    /** The sleeping animation state. */
    SLEEPING(2),

    /** The happy animation state. */
    HAPPY(2),

    /** The angry animation state. */
    ANGRY(2),

    /** The hungry animation state. */
    HUNGRY(4),

    /** The dead animation state. */
    DEAD(2),

    /** The evolving animation state. */
    EVOLVING(2);

    /** The number of frames in the animation. */
    private final int frameCount;

    /**
     * Constructs an AnimationState with the specified frame count.
     *
     * @param frameCount The number of frames in the animation.
     */
    AnimationState(int frameCount) {
        this.frameCount = frameCount;
    }

    /**
     * Gets the resource name for the animation state.
     * Special case for EVOLVING animation if it uses SIT sprites.
     *
     * @return The resource name.
     */
    public String getResourceName() {
        if (this == EVOLVING) {
            return AnimationState.SIT.name();
        }
        return name();
    }

    /**
     * Gets the frame count for the animation state.
     * Special case for EVOLVING animation if it uses SIT sprites.
     *
     * @return The frame count.
     */
    public int getFrameCount() {
        if (this == EVOLVING) {
            return AnimationState.SIT.getFrameCount();
        }
        return frameCount;
    }

    /**
     * Converts a State enum to the corresponding AnimationState.
     *
     * @param state The Chao logical state.
     * @return The corresponding animation state.
     */
    public static AnimationState fromState(State state) {
        if (state == null) return NORMAL;

        switch (state) {
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
            case EVOLVING:
                return EVOLVING;
            default:
                return NORMAL;
        }
    }
}