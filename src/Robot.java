import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The {@code Robot} is the main class which is called from the command line.
 * This class is responsible for reading in the input data from a given file for
 * the various modes and executing the various functions accordingly
 * 
 * @author Jason Brown
 */
public class Robot {
    private static Stopwatch  timer;
    private static int mode;
    private static StringBuilder mmsStr = new StringBuilder();
    private static char[] mazeStates;

    /**
     * The {@code Maze} is a private class which is used to create an object of
     * type maze to store its DFA and associated information.
     */
    private static class Maze {
        private DFA maze;
        private int mazeRows;
        private int mazeCols;

        /**
         * Instantiates a new maze.
         *
         * @param maze the maze in the form of a DFA
         * @param rows the number of rows in the maze
         * @param cols the number of cols in the maze
         */
        Maze(DFA maze, int rows, int cols) {
            this.maze = maze;
            mazeRows = rows;
            mazeCols = cols;
        }

        /**
         * Returns the dfa form of the maze
         *
         * @return maze the dfa
         */
        public DFA maze() {
            return maze;
        }
    }

    /**
     * The main method. This class is called and the main method utilized to run
     * the program. It runs for modes 0-4, each performing a specific function.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        timer = new Stopwatch();
        mode = Integer.parseInt(args[0]);
        String dfaFileName;
        File dfaFile;
        String mazeFileName;
        File mazeFile;
        String targetFileName;
        File targetFile;

        switch (mode) {
        case 0:
            dfaFileName = args[1];
            dfaFile = new File(dfaFileName);
            DFA dfa = initializeDFA(dfaFile);
            printMms(generateModifiedMatrixDFA(dfa));
            break;
        case 1:
            mazeFileName = args[1];
            mazeFile = new File(mazeFileName);
            Maze maze = initializeMaze(mazeFile);
            printMms(generateModifiedMatrixMaze(maze.maze()));
            break;
        case 2:
            targetFileName = args[1];
            targetFile = new File(targetFileName);

            dfaFileName = args[2];
            dfaFile = new File(dfaFileName);

            System.out.println(initializeDFATargets(dfaFile, targetFile));
            break;
        case 3:
            targetFileName = args[1];
            targetFile = new File(targetFileName);

            mazeFileName = args[2];
            mazeFile = new File(mazeFileName);

            System.out.println(initializeMazeTargets(mazeFile, targetFile));
            break;
        case 4:
            targetFileName = args[1];
            targetFile = new File(targetFileName);

            dfaFileName = args[2];
            dfaFile = new File(dfaFileName);

            mazeFileName = args[3];
            mazeFile = new File(mazeFileName);
            moveRobot(targetFile, dfaFile, mazeFile);
            break;
        default:
            System.out.println("Illegal mode.");
            System.exit(0);
        }

    }

    /**
     * Initializes the DFA by taking input from the in file and putting it into
     * a memory representation for the DFA. This is used in mode 0.
     * 
     * @param dfaFile the input file for the DFA
     * @return stateMachine the resulting DFA
     */
    @SuppressWarnings("unused")
    private static DFA initializeDFA(File dfaFile) {
        // Scanner to scan thorugh DFA and read data into memory representation
        Scanner scFile = null;
        try {
            scFile = new Scanner(dfaFile);
        } catch (FileNotFoundException e) {
            e = new FileNotFoundException("This is an illegal file name.");
        }

        // Retrirves the number of states, edges and transitions in the DFA
        int numStates = scFile.nextInt();
        int numEdges = scFile.nextInt();
        int numTransitions = scFile.nextInt();

        // Instantiates an edge weighted digraph
        DFA stateMachine = new DFA(numStates);

        if (numStates > 1) {
            scFile.nextLine();

            // Reads the contents of the DFA to fill the state array by creating
            // the states
            // and the edges
            while (scFile.hasNextLine()) {
                String line = scFile.nextLine();
                Scanner scLine = new Scanner(line);

                // Reads in the characters representing the source and end
                // states and the edge
                char source = scLine.next().charAt(0);
                char edge = scLine.next().charAt(0);
                char end = scLine.next().charAt(0);

                stateMachine.addTransition(new Transition(hashState(source),
                        hashState(end), edge));
                scLine.close();
            }
        }
        scFile.close();
        return stateMachine;
    }

    /**
     * Initializes the maze by taking input from the in file and putting it into
     * a memory representation for the maze which makes use of the DFA class.
     * This is used in mode 1.
     * 
     * @param mazeFile the input file for the maze
     * @return maze a Maze object of the initialized maze
     */
    private static Maze initializeMaze(File mazeFile) {
        // Scanner to scan through maze and read data into memory representation
        Scanner scFile = null;
        try {
            scFile = new Scanner(mazeFile);
        } catch (FileNotFoundException e) {
            e = new FileNotFoundException("This is an illegal file name.");
        }

        // reads in the number of rows and columns in the maze
        int mazeRows = scFile.nextInt();
        int mazeCols = scFile.nextInt();
        int mazeSize = mazeRows * mazeCols;

        DFA maze = new DFA(mazeSize);
        mazeStates = new char[mazeSize];

        scFile.nextLine();
        scFile.nextLine();

        int row = 0;
        while (scFile.hasNextLine()) {
            String line = scFile.nextLine();
            String nextLine = null;
            if (row <= mazeRows - 1) {
                nextLine = scFile.nextLine();
            }
            for (int i = 0; i < mazeCols; i++) {
                int block = row * mazeCols + i;
                mazeStates[block] = line.charAt((i * 4) + 2);
                if (line.charAt((i * 4) + 4) != '|') {
                    maze.addTransition(new Transition(block, block + 1, 'b'));
                    maze.addTransition(new Transition(block + 1, block, 'd'));
                }
                if (row != mazeRows - 1) {
                    if (nextLine.charAt((i * 4) + 2) != '-') {
                        maze.addTransition(
                                new Transition(block, block + mazeCols, 'c'));
                        maze.addTransition(
                                new Transition(block + mazeCols, block, 'a'));
                    }
                }
            }
            row++;
        }
        scFile.close();
        return new Maze(maze, mazeRows, mazeCols);
    }

    /**
     * Initialize targets by taking input in the forms of a target file and the
     * associated DFA. It then return the shortest path which needs to be
     * followed to reach every target.
     * 
     * @param dfaFile    the input file for the dfa
     * @param targetFile the list of targets to be reached in the dfa
     * @return the list of edges to be followed
     */
    public static String initializeDFATargets(File dfaFile, File targetFile) {
        StringBuilder targets = new StringBuilder();
        DFA dfa;
        dfa = initializeDFA(dfaFile);

        String[][] mms = generateModifiedMatrixDFA(dfa);

        Scanner scFile = null;
        try {
            scFile = new Scanner(targetFile);
        } catch (FileNotFoundException e) {
            e = new FileNotFoundException("This is an illegal file name.");
        }

        String start = scFile.next();
        int numTargets = scFile.nextInt();

        int currentSource;
        currentSource = hashState(start.charAt(0));

        for (int i = 0; i < numTargets; i++) {
            int currentTarget;
            currentTarget = hashState(scFile.next().charAt(0));

            while (currentSource != currentTarget) {
                char transition = mms[currentSource][currentTarget].charAt(
                        (mms[currentSource][currentTarget].length() - 1));
                targets.append(transition + " \n");

                for (Transition t : dfa.adjTransitions(currentSource)) {
                    if (t.getLabel() == transition) {
                        currentSource = t.to();
                        break;
                    }
                }
            }
        }

        return (targets.toString());
    }

    /**
     * Initialize targets by taking input in the forms of a target file and the
     * associated DFA. It then return the shortest path which needs to be
     * followed to reach every target.
     * 
     * @param mazeFile   the input file for the dfa
     * @param targetFile the list of targets to be reached in the dfa
     * @return the list of edges to be followed
     */
    public static String initializeMazeTargets(File mazeFile, File targetFile) {
        StringBuilder targets = new StringBuilder();
        DFA dfa;
        Maze tarMaze = null;

        tarMaze = initializeMaze(mazeFile);
        dfa = tarMaze.maze();

        String[][] mms = generateModifiedMatrixMaze(dfa);

        Scanner scFile = null;
        try {
            scFile = new Scanner(targetFile);
        } catch (FileNotFoundException e) {
            e = new FileNotFoundException("This is an illegal file name.");
        }

        String start = scFile.next();
        int numTargets = scFile.nextInt();

        int currentSource;
        currentSource = hashMaze(start, tarMaze.mazeCols);

        for (int i = 0; i < numTargets; i++) {
            int currentTarget;
            currentTarget = hashMaze(scFile.next(), tarMaze.mazeCols);

            while (currentSource != currentTarget) {
                char transition = mms[currentSource][currentTarget].charAt(
                        (mms[currentSource][currentTarget].length() - 1));

                switch (transition) {
                case 'a':
                    targets.append("up \n");
                    break;
                case 'b':
                    targets.append("right \n");
                    break;
                case 'c':
                    targets.append("down \n");
                    break;
                case 'd':
                    targets.append("left \n");
                    break;
                default:
                    System.out.println("Illegal transition");
                }

                for (Transition t : dfa.adjTransitions(currentSource)) {
                    if (t.getLabel() == transition) {
                        currentSource = t.to();
                        break;
                    }
                }
            }
        }

        return (targets.toString());
    }

    /**
     * Moves the Robot according to mode 4. This requires a dfa, state maze and
     * list of targets, in order to determine the combination of movements which
     * need to be made in the dfa to change states according to the state maze,
     * and the corresponding movements in the maze.
     * 
     * @param targetFile the list of targets
     * @param dfaFile    the input file for the dfa
     * @param mazeFile   the input file for the maze
     */
    public static void moveRobot(File targetFile, File dfaFile, File mazeFile) {

        StringBuilder targets = new StringBuilder();
        DFA dfa = initializeDFA(dfaFile);
        Maze tarMaze = initializeMaze(mazeFile);
        DFA mazeDfa = tarMaze.maze();

        String[][] mmsDfa = generateModifiedMatrixDFA(dfa);
        String[][] mmsMaze = generateModifiedMatrixMaze(mazeDfa);

        Scanner scFile = null;
        try {
            scFile = new Scanner(targetFile);
        } catch (FileNotFoundException e) {
            e = new FileNotFoundException("This is an illegal file name.");
        }

        String start = scFile.next();
        int numTargets = scFile.nextInt();

        int currentSource;
        currentSource = hashMaze(start, tarMaze.mazeCols);
        char currentState = mazeStates[hashMaze(start, tarMaze.mazeCols)];

        for (int i = 0; i < numTargets; i++) {
            int currentTarget;
            currentTarget = hashMaze(scFile.next(), tarMaze.mazeCols);
            char targetState = ' ';

            boolean found = false;
            while (currentSource != currentTarget) {

                char transition = mmsMaze[currentSource][currentTarget].charAt(
                        (mmsMaze[currentSource][currentTarget].length() - 1));

                for (Transition t : mazeDfa.adjTransitions(currentSource)) {
                    if (t.getLabel() == transition) {
                        currentSource = t.to();
                        targetState = mazeStates[t.to()];
                        break;
                    }
                }

                targets.append(singleRobotMovement(mmsDfa, dfa,
                        hashState(currentState), hashState(targetState)));
                currentState = targetState;

                switch (transition) {
                case 'a':
                    targets.append("up \n");
                    break;
                case 'b':
                    targets.append("right \n");
                    break;
                case 'c':
                    targets.append("down \n");
                    break;
                case 'd':
                    targets.append("left \n");
                    break;
                default:
                    System.out.println("Illegal transition");
                }
            }
        }
        System.out.println(targets.toString());
    }

    /**
     * Single robot movement. This is the helper method when moving a robot
     * through a maze which is used to compute the shortest path between states
     * before the robot can move to a new cell in the maze
     *
     * @param mms    the modified matrix
     * @param dfa    the associated dfa
     * @param source the source state
     * @param target the target state
     * @return the string of movements from source to target
     */
    public static String singleRobotMovement(String[][] mms, DFA dfa,
            int source, int target) {
        StringBuilder targets = new StringBuilder();

        int currentSource = source;

        while (currentSource != target) {
            char transition = mms[currentSource][target]
                    .charAt((mms[currentSource][target].length() - 1));
            targets.append(transition + " \n");

            for (Transition t : dfa.adjTransitions(currentSource)) {
                if (t.getLabel() == transition) {
                    currentSource = t.to();
                    break;
                }
            }
        }

        return targets.toString();
    }

    /**
     * Generates the modified modified matrix for a maze.
     *
     * @param stateMachine the maze in DFA representation
     * @return mms[][] the modified matrix as a 2D array string representation
     */
    public static String[][] generateModifiedMatrixMaze(DFA stateMachine) {
        String[][] mms = new String[stateMachine.getOrder()][stateMachine
                .getOrder()];

        for (int i = 0; i < stateMachine.getOrder(); i++) {
            mms[i][i] = "  ";

            for (Transition t : stateMachine.adjTransitions(i)) {
                mms[i][t.to()] = " " + t.getLabel();
            }

            ShortestPathTree sp = new ShortestPathTree(stateMachine, i);

            for (int j = i + 1; j < stateMachine.getOrder(); j++) {
                boolean firstTrans = true;
                Transition adj = null;
                Transition last = null;

                for (Transition t : sp.pathTo(j)) {
                    adj = t;
                    last = sp.lastTrans(j);
                    break;
                }
                if (mms[i][j] == null) {
                    mms[i][j] = "-" + adj.getLabel();
                }
                if (mms[j][i] == null) {
                    mms[j][i] = "-"
                            + (char) ((last.getLabel() - 97 + 2) % 4 + 97);
                }
            }
        }
        return mms;
    }

    /**
     * Generates the modified modified matrix for a dfa.
     *
     * @param stateMachine the dfa in DFA representation
     * @return mms[][] the modified matrix as a 2D array string representation
     */
    public static String[][] generateModifiedMatrixDFA(DFA stateMachine) {
        String[][] mms = new String[stateMachine.getOrder()][stateMachine
                .getOrder()];

        for (int i = 0; i < stateMachine.getOrder(); i++) {
            mms[i][i] = "  ";

            for (Transition t : stateMachine.adjTransitions(i)) {
                mms[i][t.to()] = " " + t.getLabel();
            }

            ShortestPathTree sp = new ShortestPathTree(stateMachine, i);

            for (int j = 0; j < stateMachine.getOrder(); j++) {
                Transition adj = null;

                for (Transition t : sp.pathTo(j)) {
                    adj = t;
                    break;
                }
                if (mms[i][j] == null) {
                    mms[i][j] = "-" + adj.getLabel();
                }
            }
        }
        return mms;
    }

    /**
     * Prints the modified matrix
     *
     * @param mms the modified matrix to be printed
     */
    public static void printMms(String[][] mms) {
        for (int i = 0; i < mms.length; i++) {
            for (int j = 0; j < mms[i].length; j++) {
                if (j != mms[i].length - 1) {
                    mmsStr.append(mms[i][j] + " ");
                } else {
                    mmsStr.append(mms[i][j]);
                }
            }
            if (i < mms.length - 1) {
                mmsStr.append("\n");
            }
        }
        System.out.println(mmsStr.toString());
        System.out.println(timer.elapsedTime());
    }

    /**
     * Hashes a given state in a dfa from its String form to its unique integer
     * identifier.
     *
     * @param s the String representation of a maze cell
     * @return the corresponding integer value of the node in the dfa
     */
    private static int hashState(char s) {
        return s - 65;
    }

    /**
     * Hashes a given cell in a maze from its String form to its unique integer
     * identifier.
     *
     * @param s        the String representation of a maze cell
     * @param mazeCols the number of columns in the maze
     * @return the corresponding integer value of the node in the maze dfa
     */
    private static int hashMaze(String s, int mazeCols) {
        int r = s.charAt(0) - 65;
        int c = s.charAt(1) - 65;
        return r * mazeCols + c;
    }
}
