/**
 * The {@code Transition} class represents a transition between two states in a
 * DFA. Each transition is directed and labeled.
 *
 * @author Jason Brown
 */
public class Transition {
    private final int source;
    private final int end;
    private final char label;

    /**
     * Initializes a transition from the source state {@code source} to the
     * target state {@code end} with the given label {@code label}.
     * 
     * @param source the source state
     * @param end    the end state
     * @param label  the label for the transition
     */
    public Transition(int source, int end, char label) {
        if (source < 0) {
            throw new IllegalArgumentException("Illegal source state.");
        }
        if (end < 0) {
            throw new IllegalArgumentException("Illegal end state.");
        }
        this.source = source;
        this.end = end;
        this.label = label;
    }

    /**
     * Returns the number of the source state of the transition.
     * 
     * @return the source of the transition
     */
    public int from() {
        return source;
    }

    /**
     * Returns the number of the end state of the transition.
     * 
     * @return the end of the transition
     */
    public int to() {
        return end;
    }

    /**
     * Gets the label of the transition.
     *
     * @return the label
     */
    public char getLabel() {
        return label;
    }

    /**
     * Creates a String to represent a Transition data type.
     *
     * @return a String representing the Transition
     */
    public String toString() {
        return "Transition: " + source + " -> " + end;
    }
}
