package com.example.chaotopia.Model;

/**
 * The Chao class represents a virtual pet character with different types, states,
 * and status attributes. A Chao can evolve based on its alignment value.
 * @author Rosaline Scully
 */
public class Chao {
    private int alignment;
    private String name;
    private ChaoType type;
    private State state;
    private Status status;

    /**
     * Constructor that initializes a Chao with specified attributes.
     *
     * @param alignment the initial alignment value (positive for heroic, negative for dark)
     * @param name the name of the Chao
     * @param type the initial type of the Chao
     * @param state the initial state of the Chao
     * @param status the initial status object containing vital statistics
     */
    public Chao(int alignment, String name, ChaoType type, State state, Status status) {
        this.alignment = alignment;
        this.name = name;
        this.type = type;
        this.state = state;
        this.status = status;
    }

    /**
     * Gets the current alignment value.
     *
     * @return the alignment value (positive for heroic, negative for dark)
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Adjusts the alignment by adding the specified value to the current alignment.
     * Positive values make the Chao more heroic, negative values make it more dark.
     *
     * @param alignment the amount to adjust the alignment by
     */
    public void adjustAlignment(int alignment) {
        this.alignment = this.alignment + alignment;
    }

    /**
     * Gets the current type of the Chao.
     *
     * @return the current ChaoType
     */
    public ChaoType getType() {
        return type;
    }

    /**
     * Sets the type of the Chao.
     *
     * @param type the new ChaoType
     */
    public void setType(ChaoType type) {
        this.type = type;
    }

    /**
     * Gets the name of the Chao.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current state of the Chao.
     *
     * @return the current State
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state of the Chao.
     *
     * @param state the new State
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Gets the status object containing the Chao's vital statistics.
     *
     * @return the Status object
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Evolves the Chao based on its current alignment value.
     * If alignment is >= 7, the Chao evolves to HERO type.
     * If alignment is <= -7, the Chao evolves to DARK type.
     * Otherwise, the Chao's type remains unchanged.
     */
    public void evolve() {
        if (alignment >= 7) {
            this.setType(ChaoType.HERO);
        } else if (alignment <= -7) {
            this.setType(ChaoType.DARK);
        }
    }

}