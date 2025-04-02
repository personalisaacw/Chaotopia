package com.example.chaotopia.Model;

/**
 * Utility class for converting between State and AnimationState enums.
 */
public class StateUtility {

    /**
     * Convert from an AnimationState enum to the corresponding logical State.
     * This is the reverse of AnimationState.fromState().
     *
     * @param animState The animation state.
     * @return The corresponding logical state, or State.NORMAL if no direct match.
     */
    public static State fromAnimationState(AnimationState animState) {
        if (animState == null) return State.NORMAL;

        switch (animState) {
            case NORMAL: return State.NORMAL;
            case SIT: return State.SIT;
            case SLEEPING: return State.SLEEPING;
            case HAPPY: return State.HAPPY;
            case ANGRY: return State.ANGRY;
            case HUNGRY: return State.HUNGRY;
            case DEAD: return State.DEAD;
            case EVOLVING: return State.EVOLVING; // Map AnimationState.EVOLVING back
            default: return State.NORMAL; // Default fallback
        }
    }

    /**
     * Helper method to check if a ChaoType is one of the basic, non-evolved types.
     * @param type The ChaoType to check.
     * @return true if the type is BLUE, RED, or GREEN, false otherwise.
     */
    public static boolean isBasicChaoType(ChaoType type) {
        return type == ChaoType.BLUE || type == ChaoType.RED || type == ChaoType.GREEN;
    }
}