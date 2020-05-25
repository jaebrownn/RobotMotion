/**
 * The {@code ShortestPathTree} class is used for determining the shortest path
 * from a single source state to every other state in a given DFA.
 * 
 * This shortest path tree makes use of Dijkstra's shortest path algorithm using
 * priority queue which is indexed on the minimum value and is seen in the
 * Algorithm's 4th Edition textbook by Robert Sedgewick and Kevin Wayne.
 *
 * @author Jason Brown
 */
public class ShortestPathTree {
    private double[] distTo; // distTo[state] = stores the distance of shortest
                             // path to the given state
    private Transition[] transTo; // transTo[state] = stores the last transition
                                  // on shortest path to this state
    private IndexMinPQ<Double> pathPQ; // priority queue of states indexed by
                                       // minimum value
    private boolean[] visited;
    
    private Transition[] firstTrans;

    /**
     * Computes a shortest-paths tree from the state {@code source} to every
     * other state in the DFA {@code dfa}.
     *
     * @param dfa    the DFA
     * @param source the source state
     */
    public ShortestPathTree(DFA dfa, int source) {
        if (source < 0 || source >= dfa.getOrder()) {
            throw new IllegalArgumentException(
                    "The source state must exist in the DFA.");
        }

        distTo = new double[dfa.getOrder()]; // initializes the distances array
                                             // with the order of the given DFA
        transTo = new Transition[dfa.getOrder()]; // array of Transition with
                                                  // size equal to the order of
                                                  // the DFA
        visited = new boolean[dfa.getOrder()];
        firstTrans = new Transition[dfa.getOrder()];

        // initializes the array of the distances to each vertex from a source
        // to infinity, as none of the states have been visited
        for (int i = 0; i < dfa.getOrder(); i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
        }
        distTo[source] = 0.0; // initializes the distance to the source as zero

        pathPQ = new IndexMinPQ<Double>(dfa.getOrder()); // initializes the
                                                         // priority queue of
                                                         // states
        pathPQ.insert(source, distTo[source]); // adds the source as the first
                                               // state in the PQ
        while (!pathPQ.isEmpty()) {
            int s = pathPQ.delMin(); // retrieves the state with the smallest
                                     // current distTo value
            for (Transition t : dfa.adjTransitions(s)) { // loops through all
                                                         // adjacent transitions
                                                         // in the state
                int src = t.from(), end = t.to();
                if (distTo[end] > distTo[src] + 1.0) { // checks if the distance
                                                       // to a neigbouring state
                                                       // by adding one to the
                                                       // distance at the
                                                       // current node is less
                                                       // than the current
                                                       // distance of the
                                                       // shortest path to the
                                                       // neighbouring state. If
                                                       // so, update the pathPQ,
                                                       // transition array
                                                       // and distances
                    distTo[end] = distTo[src] + 1.0;
                    transTo[end] = t;
                    if (pathPQ.contains(end)) {
                        pathPQ.decreaseKey(end, distTo[end]);
                    } else {
                        pathPQ.insert(end, distTo[end]);
                    }
                }
            }
        }
    }

    /**
     * Returns the length of a shortest path from the source state
     * {@code source} to state {@code s}.
     * 
     * @param s the target state
     * @return the length of a shortest path from the source state
     *         {@code source} to state {@code s};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     */
    public double distTo(int s) {
        return distTo[s];
    }

    /**
     * Returns true if a path exists from the source state {@code source} to the
     * given target state {@code s}.
     *
     * @param s the target state
     * @return {@code true} if there is a path from the source state
     *         {@code source} to state {@code s}; {@code false} otherwise
     */
    public boolean hasPathTo(int s) {
        return distTo[s] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source state {@code source} to given
     * state {@code s}.
     *
     * @param s the target state
     * @return a shortest path from the source state {@code source} to given
     *         state {@code s} as an iterable of transition, and {@code null} if
     *         no such path
     */
    public Iterable<Transition> pathTo(int s) {
        if (!hasPathTo(s)) {
            return null;
        }

        Stack<Transition> path = new Stack<Transition>();
        for (Transition t = transTo[s]; t != null; t = transTo[t.from()]) {
            path.push(t);
        }
        return path;
    }
    
    /**
     * Returns the last transition on the shortest path to the given node.
     *
     * @param s the target state
     * @return the last transition on the shortest path to this node
     */
    public Transition lastTrans(int s) {
        return transTo[s];
    }

}
