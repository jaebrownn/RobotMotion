/**
 * The {@code DFA} is a representation of a deterministic finite state
 * automaton, consisting of states named as integer values with 0 indexing, and
 * connected by transitions which are directed and labeled.
 * 
 *
 * @author Jason Brown
 */
public class DFA {
    private final int order; // the number of states in this DFA, the order
    private int size; // the number of transitions in this DFA, the size
    private Bag<Transition>[] adjTrans; // adjTrans[s] = adjacent transitions
                                        // of state s, stored in a Bag, for
                                        // state s
    private int[] inTrans; // inTrans[s] = transitions ending at state s

    /**
     * Initializes a DFA with {@code order} number of states and no transitions
     * initially.
     *
     * @param order the number of states in the DFA
     */
    @SuppressWarnings("unchecked")
    public DFA(int order) {
        if (order < 0) {
            throw new IllegalArgumentException(
                    "The number of states may not be negative.");
        }
        this.order = order;
        this.size = 0;
        this.inTrans = new int[order];
        adjTrans = (Bag<Transition>[]) new Bag[order];
        for (int i = 0; i < order; i++) {
            adjTrans[i] = new Bag<Transition>();
        }
    }

    /**
     * Returns the number of states in this DFA.
     *
     * @return the order of the DFA
     */
    public int getOrder() {
        return order;
    }

    /**
     * Returns the number of transitions in this DFA.
     *
     * @return the size of the DFA
     */
    public int getSize() {
        return size;
    }

    /**
     * Adds the transition {@code t} between two states in this DFA, by getting
     * the source and end states of the transition
     *
     * @param t the Transition
     */
    public void addTransition(Transition t) {
        int source = t.from();
        int end = t.to();
        validState(source);
        validState(end);
        adjTrans[source].add(t);
        inTrans[end]++;
        size++;
    }

    /**
     * Returns the adjacent transitions incident from state {@code s}.
     *
     * @param s the states in the DFA
     * @return the transitions incident from state {@code a} as an Iterable
     */
    public Iterable<Transition> adjTransitions(int s) {
        validState(s);
        return adjTrans[s];
    }

    /**
     * Returns the number of the transition incident from state {@code s}.
     *
     * @param s the state
     * @return the number of incident transitions from states {@code s}
     */
    public int outTransitions(int s) {
        validState(s);
        return adjTrans[s].size();
    }

    /**
     * Returns the number of transitions incident to vertex {@code v}.
     *
     * @param s the state
     * @return the number of transitions from states {@code s}
     */
    public int inTransitions(int s) {
        validState(s);
        return inTrans[s];
    }

    /**
     * Returns a list of all transitions in this DFA. foreach notation can be
     * used to iterate through the transitions
     *
     * @return all transitions in this DFA, as an Iterable
     */
    public Iterable<Transition> allTransitions() {
        Bag<Transition> transitions = new Bag<Transition>();
        for (int i = 0; i < order; i++) {
            for (Transition t : adjTrans[i]) {
                transitions.add(t);
            }
        }
        return transitions;
    }

    /**
     * Checks if a state is valid.
     * 
     * @param s the state which is being validated
     */
    private void validState(int s) {
        if (s < 0 || s >= order) {
            throw new IllegalArgumentException("State " + (char) (s + 65) + ""
                    + s + " is outside of the bounds of the DFA.");
        }
    }

    /**
     * Returns a representation of the DFA in the form of a String.
     *
     * @return the number of states <em>S</em>, followed by the number of
     *         transitions <em>T</em>, followed by the <em>T</em> adjacency
     *         lists of transitions belonging to each state
     */
    public String toString() {
        String s = "";
        s = s + (order + " " + size + "/n");
        for (int i = 0; i < order; i++) {
            s = s + (i + ": ");
            for (Transition t : adjTrans[i]) {
                s = s + (t + "  ");
            }
            s = s + "/n";
        }
        return s.toString();
    }

}
