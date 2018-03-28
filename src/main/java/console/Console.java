package console;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import simulate.World;

/**
 * The console user interface for Assignment 5.
 */
public class Console {
    private Scanner scan;
    public boolean done;
    public PrintStream out;

    /* =========================== */
    /* DO NOT EDIT ABOVE THIS LINE */
    /* (except imports...)         */
    /* =========================== */

    //TODO world representation...
    World w;

    /**
     * Starts new random world simulation.
     */
    public void newWorld() {
		w = new World();
        w.newWorld();
        w.randomRocks();
    }

    /**
     * Starts new simulation with world specified in filename.
     *
     * @param filename
     */
    public void loadWorld(String filepath) {
		w = new World();
    		w.readFile(new File(filepath));
    }

    /**
     * Loads critter definition from filename and randomly places
     * n critters with that definition into the world.
     *
     * @param filename
     * @param n
     */
    public void loadCritters(String filename, int n) {
        if (w == null || !w.initialized()) {
        		System.out.println("Can't load critters because world is not initialized.");
        		return;
        }
        for (int i=0; i<n; i++) {
        		w.placeCritterInWorld(filename);
        }
    }

    /**
     * Advances the world by n time steps.
     *
     * @param n
     */
    public void advanceTime(int n) {
    		if (w == null || !w.initialized()) {
    			System.out.println("Can't advance time steps because world is not initialized.");
    			return;
    		}
        for (int i=0; i<n; i++) {
        		w.advanceATimeStep();
        }
    }

    /**
     * Prints current time step, number of critters, and world
     * map of the simulation.
     */
    public void worldInfo() {
    		if (w == null || !w.initialized()) {
    			System.out.println("Can't print world info becasue world is not initialized.");
    			return;
    		}
        worldInfo(w.getNumTimeSteps(), w.getNumCrittersAlive());
        System.out.println(w.reprensentWorld());
    }

    /**
     * Prints description of the contents of hex (c,r).
     *
     * @param c column of hex
     * @param r row of hex
     */
    public void hexInfo(int c, int r) {
    		if (w == null || !w.initialized()) {
    			System.out.println("Can't print hex info because world is not initialized.");
    			return;
    		}
        if (w.hasCritter(c, r)) {
            critterInfo(w.getCritterSpecies(c, r), w.getCritterMem(c, r), 
            		w.getCritterGenome(c, r), w.getLastExecutedRule(c, r));
        } else terrainInfo(w.getTerrain(c, r));



    }

    /* =========================== */
    /* DO NOT EDIT BELOW THIS LINE */
    /* =========================== */

    /**
     * Be sure to call this function, we will override it to grade.
     *
     * @param numSteps      The number of steps that have passed in the world.
     * @param crittersAlive The number of critters currently alive.
     */
    protected void worldInfo(int numSteps, int crittersAlive) {
        out.println("steps: " + numSteps);
        out.println("critters: " + crittersAlive);
    }

    /**
     * Be sure to call this function, we will override it to grade.
     *
     * @param species  The species of the critter.
     * @param mem      The memory of the critter.
     * @param program  The program of the critter pretty printed as a String. This should be able to be parsed back to
     *                 the same AST.
     * @param lastrule The last rule executed by the critter pretty printed as a String. This should be able to be
     *                 parsed back to the same AST. If no rule has been executed, this parameter should be null.
     */
    protected void critterInfo(String species, int[] mem, String program, String lastrule) {
        out.println("Species: " + species);
        StringBuilder sbmem = new StringBuilder();
        for (int i : mem) {
            sbmem.append(" ").append(i);
        }
        out.println("Memory:" + sbmem.toString());
        out.println("Program: " + program);
        out.println("Last rule: " + lastrule);
    }

    /**
     * Be sure to call this function, we will override it to grade.
     *
     * @param terrain 0 is empty, -1 is rock, -X is (X-1) food
     */
    protected void terrainInfo(int terrain) {
        if (terrain == 0) {
            out.println("Empty");
        } else if (terrain == -1) {
            out.println("Rock");
        } else {
            out.println("Food: " + (-terrain - 1));
        }
    }

    /**
     * Prints a list of possible commands to the standard output.
     */
    public void printHelp() {
        out.println("new: start a new simulation with a random world");
        out.println("load <world_file>: start a new simulation with "
                + "the world loaded from world_file");
        out.println("critters <critter_file> <n>: add n critters "
                + "defined by critter_file randomly into the world");
        out.println("step <n>: advance the world by n timesteps");
        out.println("info: print current timestep, number of critters "
                + "living, and map of world");
        out.println("hex <c> <r>: print contents of hex "
                + "at column c, row r");
        out.println("exit: exit the program");
    }

    /**
     * Constructs a new Console capable of reading a given input.
     */
    public Console(InputStream in, PrintStream out) {
        this.out = out;
        scan = new Scanner(in);
        done = false;
    }

    /**
     * Constructs a new Console capable of reading the standard input.
     */
    public Console() {
        this(System.in, System.out);
    }

    /**
     * Processes a single console command provided by the user.
     */
    public void handleCommand() {
        out.print("Enter a command or \"help\" for a list of commands.\n> ");
        String command = scan.next();
        switch (command) {
            case "new": {
                newWorld();
                break;
            }
            case "load": {
                String filename = scan.next();
                loadWorld(filename);
                break;
            }
            case "critters": {
                String filename = scan.next();
                int n = scan.nextInt();
                loadCritters(filename, n);
                break;
            }
            case "step": {
                int n = scan.nextInt();
                advanceTime(n);
                break;
            }
            case "info": {
                worldInfo();
                break;
            }
            case "hex": {
                int c = scan.nextInt();
                int r = scan.nextInt();
                hexInfo(c, r);
                break;
            }
            case "help": {
                printHelp();
                break;
            }
            case "exit": {
                done = true;
                break;
            }
            default:
                out.println(command + " is not a valid command.");
        }
    }

    public static void main(String[] args) {
        Console console = new Console();
        while (!console.done) {
            console.handleCommand();
        }
    }

}
