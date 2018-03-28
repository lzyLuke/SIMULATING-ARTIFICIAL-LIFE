package simulate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import web.displayHex;
import web.freezedWorld;

/**
 * The simulation of the critter world.
 * 
 * @author JingjingLi
 *
 */
public class World {
	
	private BufferedReader reader;
	public String name;
	public int rows;
	public int columns;
	public int current_timestep=0;
	public int current_version_number=0;
	public double rate=0;
	public int population=0;
	public Hex[][] world;
	public freezedWorld lastFreezedWorld;
	public List<Critter> critters = Collections.synchronizedList(new ArrayList<Critter>());
	public List<Integer> deadLists = Collections.synchronizedList(new ArrayList<Integer>());
	private int timeSteps = 0;
	public int objectID=-1;
	private ReentrantLock idUpdateLock = new ReentrantLock();
	private Map<Integer,freezedWorld> worldVersion = new ConcurrentHashMap<Integer,freezedWorld>();
	// below are world constants
	// The multiplier for all damage done by attacking
	public int BASE_DAMAGE = -2; 
	// Controls how quickly increased offensive or defensive ability affects damage
	public double DAMAGE_INC = -2;
	// How much energy a critter can have per point of size
	public int ENERGY_PER_SIZE = -2;
	// How much food is created per point of size when a critter dies
	public int FOOD_PER_SIZE = -2;
	// Maximum distance at which food can be sensed
	public int MAX_SMELL_DISTANCE = -2;
	// The value reported when a rock is sensed
	public int ROCK_VALUE = -2;
	// The maximum number of rules that can be run per critter turn
	public int MAX_RULES_PER_TURN = -2;
	// Energy gained from sun by doing nothing
	public int SOLAR_FLUX = -2;
	// Energy cost of moving (per unit size)
	public int MOVE_COST = -2;
	// Energy cost of attacking (per unit size)
	public int ATTACK_COST = -2;
	// Energy cost of growing (per size and complexity)
	public int GROW_COST = -2;
	// Energy cost of budding (per unit complexity)
	public int BUD_COST = -2;
	// Energy cost of successful mating (per unit complexity)
	public int MATE_COST = -2;
	// Complexity cost of having a rule
	public int RULE_COST = -2;
	// Complexity cost of having an ability point
	public int ABILITY_COST = -2;
	// Energy of a newly birthed critter
	public int INITIAL_ENERGY = -2;
	// Minimum number of memory entries in a critter
	public int MIN_MEMORY = -2;
	

	/**
	 * The constructor used when there's no constants file.
	 * This constructor will load the default constants automatically. 
	 */
	public World() {
		InputStream in = World.class.getResourceAsStream("constants.txt");
		this.parseConstants(in);
		lastFreezedWorld=new freezedWorld();
		worldVersion.put(new Integer(0),lastFreezedWorld); 

		//add a null world into the worldVersion, null world current_version_number is 0
	}
	
	
	/**
	 * Changes the value of {@code reader} to the content of a file.
	 * 
	 * @param file
	 * 				The world file to be loaded.
	 */
	public void readFile(File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
			 for (String line = ""; line != null; line = readWorld()) {
				 setWorld(line, file.getAbsolutePath());
			 }
			 reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + file);
		} catch (IOException e) {
			System.out.println("IOException when trying to close reader.");
		}
		this.freezeWorldVersionAndSave();
	}
	
	/**
	 * Reads the next line of {@code reader}. 
	 * 
	 * @return a string of the next line. Null if an {@code IOException} is thrown.
	 */
	private String readWorld() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			System.out.println("IOException in method readWorld()");
		}
		return null;
	}
	
	/**
	 * Sets up the critter world based on input. Reads one line each time.
	 * Does nothing if line is empty or starts with //. 
	 * 
	 * @param line
	 * 			A string of the line to be interpreted.
	 */
	private void setWorld(String line, String filepath) {
		line = line.trim().toLowerCase();
		if (line.equals("")) return;
		if (line.startsWith("//")) return;
		if (line.startsWith("name")) {
			name = line.substring(5).trim();
		} else if (line.startsWith("size")) {
			String[] temp = line.split("\\s");
			if (temp.length != 3) {
				System.out.println("Check world size.");
				System.out.println("Default world initialized.");
				newWorld();
				return;
			}
			try{
				int columns = Integer.parseInt(temp[1]);
				int rows = Integer.parseInt(temp[2]);
				if (columns <= 0 || rows <= 0) {
					System.out.println("Illegal world size.");
					System.out.println("Default world initialized.");
					newWorld();
					return;
				}
				this.columns = columns;
				this.rows = rows;
				world = new Hex[columns][rows];
				for (int i=0; i<columns; i++) {
					for (int j=0; j<rows; j++) {
						world[i][j] = new Hex(i, j, this.ROCK_VALUE);
					}
				}
				setOutsideToRocks();
				
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parcing size of world.");
			}
		} else if (line.startsWith("rock")) {
			// represented by -1
			if (world == null) {
				System.out.println("Trying to place rock when world is not initialized.");
				System.out.println("Default world initialized.");
				newWorld();
			}
			String[] temp = line.split("\\s");
			if (temp.length != 3) {
				System.out.println("Check rock input.");
			} else {
				try {
					int c = Integer.parseInt(temp[1]);
					int r = Integer.parseInt(temp[2]);
					// trying to place rock outside of the world: no need to do anything since there's already a rock
					if (c > columns || r > rows) return;
					world[c][r].setRock();
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException when parsing rock info.");
				}
			}
		} else if (line.startsWith("food")) {
			// represented by -amount-1
			if (world == null) {
				System.out.println("Trying to place food when world is not initialized.");
				System.out.println("Default world initialized.");
				newWorld();
			}
			String[] temp = line.split("\\s");
			if (temp.length != 4) {
				System.out.println("Check food input.");
			} else {
				try {
					int c = Integer.parseInt(temp[1]);
					int r = Integer.parseInt(temp[2]);
					int amount = Integer.parseInt(temp[3]);
					if (c > columns || r > rows) {
						System.out.println("Can't place food outside of world.");
						return;
					} else if (amount < 0) {
						System.out.println("Food amount can't be negative.");
						return;
					}
					world[c][r].setFood(amount);
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException when parsing food info.");
				}
			}
		} else if (line.startsWith("critter")) {
			// represented by an integer from 1 to 6, indicating direction
			String[] temp = line.split("\\s");
			if (temp.length != 5) {
				System.out.println("Check critter input.");
				return;
			}
			try {
				int c = Integer.parseInt(temp[2]);
				int r = Integer.parseInt(temp[3]);
				if (this.isOutofBounds(c, r)) {
					System.out.println("Can't place critter outside of the world.");
					return;
				}
				int dir = Integer.parseInt(temp[4]);
				dir %= 5;
				Critter critter = new Critter(c, r, dir,this);
				world[c][r].setCritter(critter);
				critters.add(critter);
				Path worldPath = Paths.get(filepath);
			    Path folderPath = worldPath.subpath(0, worldPath.getNameCount()-1);
				Path critterPath = folderPath.resolve(temp[1]);
				File critterFile = new File('/'+critterPath.toString());
				critter.setCritter(new FileInputStream(critterFile));
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException when loading critter from file " + temp[1]);
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when loading critter from file " + temp[1]);
			}
		} 
	}
	
	/**
	 * Sets the spots outside of the world to be rocks.
	 * Called when initializing / expanding world.
	 */
	public void setOutsideToRocks() {
		if (world == null) {
			System.out.println("Trying to set up outside of world when world is not initialized.");
			System.out.println("Default world initialized.");
			newWorld();
		}
		for (int i=0; i<columns; i++) {
			for (int j=0; j<rows; j++) {
				if (2*j-i < 0 || 2*j-i >= 2*rows-columns) world[i][j].setRock();
			}
		}
	}
	
	/**
	 * Gets the string representing the world.
	 * 
	 * @return s
	 * 			The string representing the world. 
	 */
	public String reprensentWorld() { 
		String s = "", temp = "";
		for (int i=0; i<rows; i++) {
			int c = 0;
			int r = i;
			while (r < rows && c < columns && 2*r-c >= 0 && 2*r-c < 2*rows-columns) {
				temp += content(c,r) + "   ";
				c += 2;
				r += 1;
			}
			if (temp != "") {
				s = temp + "\n" + s;
				s = s.trim();
				temp = "";
			}
			if (rows > 1) {
				c = 1;
				r = i + 1;
				while (r < rows && c < columns && 2*r-c >= 0 && 2*r-c < 2*rows-columns) {
					temp += "  " + content(c,r) + " ";
					c += 2;
					r += 1;
				}
				if (temp != "") {
					s = temp + "\n" + s;
					temp = "";
				}
			}
		}
		return s;
	}
	
	/**
	 * Tests if the world if full by calculating empty spaces.
	 * 
	 * @return true if empty space = 0, false otherwise
	 */
	private boolean worldFull() {
		int count = 0;
		for (int i=0; i<rows; i++) {
			int c = 0;
			int r = i;
			while (r < rows && c < columns && 2*r-c >= 0 && 2*r-c < 2*rows-columns) {
				if (world[c][r].getTerrain() == 0) count++;
				c += 2;
				r += 1;
			}
			if (rows > 1) {
				c = 1;
				r = i + 1;
				while (r < rows && c < columns && 2*r-c >= 0 && 2*r-c < 2*rows-columns) {
					if (world[c][r].getTerrain() == 0) count++;
					c += 2;
					r += 1;
				}
			}
		}
		if (count == 0) return true;
		return false;
	}
	
	/**
	 * Returns the content in (c,r).
	 * Used by representWorld().
	 * Requires: r < rows && c < columns.
	 * 
	 * @param c
	 * 			the column number
	 * @param r
	 * 			the row number 
	 * @return
	 * 			"-" if empty, "#" for rock, d for a critter facing in direction d, F for food.
	 */
	private String content(int c, int r) {
		assert (r < rows && c < columns);
		Hex temp = world[c][r];
		if (temp.isRock()) return "#";
		if (temp.isFood()) return "F";
		if (temp.isCritter()) return Integer.toString(temp.getCritterDirection());
		return "-";
	}
	
	/**
	 * Parses constants.txt.
	 * 
	 * @param file
	 * 			the name of the file containing world constants
	 */
	public void parseConstants(InputStream in) {
		try {
	        BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = r.readLine().trim().toUpperCase();
			String[] temp;
			for (; line != null; line = r.readLine()) {
				temp = line.split(" ");
				if (temp.length < 2) {
					System.out.println(line + " from constant file is illegal input.");
					return;
				} 
				try {
					if (temp[0].trim().equals("BASE_DAMAGE")) 
						// TODO handle exception and give default values
						BASE_DAMAGE = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("DAMAGE_INC")) 
						DAMAGE_INC = Double.parseDouble(temp[1].trim());
					else if (temp[0].trim().equals("ENERGY_PER_SIZE")) 
						ENERGY_PER_SIZE = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("FOOD_PER_SIZE")) 
						FOOD_PER_SIZE = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("MAX_SMELL_DISTANCE")) 
						MAX_SMELL_DISTANCE = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("ROCK_VALUE")) 
						ROCK_VALUE = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("COLUMNS") ) 
						columns = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("ROWS")) 
						rows = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("MAX_RULES_PER_TURN")) 
						MAX_RULES_PER_TURN = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("SOLAR_FLUX")) 
						SOLAR_FLUX = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("MOVE_COST")) 
						MOVE_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("ATTACK_COST")) 
						ATTACK_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("GROW_COST")) 
						GROW_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("BUD_COST")) 
						BUD_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("MATE_COST")) 
						MATE_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("RULE_COST")) 
						RULE_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("ABILITY_COST")) 
						ABILITY_COST = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("INITIAL_ENERGY")) 
						INITIAL_ENERGY = Integer.parseInt(temp[1].trim());
					else if (temp[0].trim().equals("MIN_MEMORY")) 
						MIN_MEMORY = Integer.parseInt(temp[1].trim());
					else {
						System.out.println(line + " in constant file is not a legal input.");
					}
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException when parsing constants file.");
				}
			}
			r.close();
			loadDefaultConstants();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException when trying to open constants file.");
		} catch (IOException e) {
			System.out.println("IOException when trying to read line from constants file.");
		} catch (NullPointerException npe) {
			System.out.println("NullPointerException when parsing world constants.");
		}
	}
	
	/**
	 * Place random rocks into world. 
	 */
	public void randomRocks() {
		Random rand = new Random();
		int num = rand.nextInt(columns * rows + 1);
		int c, r;
		for (int i=0; i<num; i++) {
			c = rand.nextInt(columns);
			r = rand.nextInt(rows);
			world[c][r].setRock();
		}
	}
	
	/**
	 * Gets the number of time steps elapsed.
	 * 
	 * @return number of time steps elapsed
	 */
	public int getNumTimeSteps() {
		return timeSteps;
	}
	
	/**
	 * Gets the number of critters alive in the world.
	 * 
	 * @return number of critters alive in the world
	 */
	public int getNumCrittersAlive() {
		return critters.size();
	}
	
	/**
	 * Tests if the hex (c,r) contains a critter.
	 * 
	 * @param c
	 * 			the column number of a hex
	 * @param r
	 * 			the row number of a hex
	 * @return true if hex contains a critter, false otherwise.
	 */
	public boolean hasCritter(int c, int r) {
		if (2*r-c >= 0 && 2*r-c < 2*rows-columns && c<columns && r<rows) {
			if (world[c][r].isCritter()) return true;
		}
		return false;
	}
	
	/**
	 * Gets the critter species in the hex (c,r).
	 * Requires: there is a critter in the hex (c,r). 
	 * 
	 * @param c
	 * 			the column number of a hex
	 * @param r
	 * 			the row number of a hex
	 * @return the species of the critter in (c,r)
	 * 			null if no such critter if found
	 */
	public String getCritterSpecies(int c, int r) {
		for (int i=0; i<critters.size(); i++) {
			Critter critter = critters.get(i);
			if (critter.c == c && critter.r == r) return critter.species;
		}
		return null;
	}
	
	/**
	 * Gets the mem[] of the critter in (c,r).
	 * Requires: there is a critter in the hex (c,r). 
	 * 
	 * @param c
	 * 			the column number of a hex
	 * @param r
	 * 			the row number of a hex
	 * @return a copy of the int[] mem of the critter
	 * 			null if no such critter if found
	 */
	public int[] getCritterMem(int c, int r) {
		return world[c][r].getCritter().getMem();
	}
	
	/**
	 * Gets the genome of the critter in (c,r).
	 * Requires: there is a critter in the hex (c,r). 
	 * 
	 * @param c
	 * 			the column number of a hex
	 * @param r
	 * 			the row number of a hex
	 * @return a string representing the genome of the critter
	 * 			null if no such critter is found
	 */
	public String getCritterGenome(int c, int r) {
		for (int i=0; i<critters.size(); i++) {
			Critter critter = critters.get(i);
			if (critter.c == c && critter.r == r) {
				return (critter.genome.toString());
			}
		}
		return null;
	}
	
	/**
	 * Gets the last executed rule of the critter at (c,r).
	 * Requires: there is a critter in the hex (c,r).
	 * 
	 * @param c
	 *  			the column number of a hex
	 * @param r
	 *  			the row number of a hex
	 * @return the prettyPrint string representation of the rule
	 * 			null if no such critter or no rule has been executed
	 */
	public String getLastExecutedRule(int c, int r) {
		for (int i=0; i<critters.size(); i++) {
			Critter critter = critters.get(i);
			if (critter.c == c && critter.r == r) {
				if (critter.lastExe != null) return critter.lastExe.toString();
			}
		}
		return null;
	}
	
	/**
	 * Gets the terrain feature of the hex (c,r).
	 * Requires: (c,r) doesn't contain a critter.
	 * 
	 * @param c
	 * 			the column number of a hex
	 * @param r
	 * 			the row number of a hex
	 * @return 0 if empty, -1 if rock, otherwise food
	 */
	public int getTerrain(int c, int r) {
		if (c >= 0 && r >= 0 && c < columns && r < rows) {
			if (world[c][r].isFood()) return -world[c][r].getTerrain()-1;
			return world[c][r].getTerrain();
		}
		return -1;
	}
	
	/**
	 * Tests if the world is initialized.
	 * 
	 * @return true if {@code world} is not null, false otherwise
	 */
	public boolean initialized() {
		return world != null;
	}
	
	/**
	 * Randomly places a critter in world.
	 * Requires: world is initialized.
	 * 
	 * @param file
	 * 			the name of the critter file
	 */
	public void placeCritterInWorld(String file) {
		Random rand = new Random();
		int c = -1, r = -1;
		while (2*r-c < 0 || 2*r-c >= 2*rows-columns || world[c][r].getTerrain() != 0) {
			if (worldFull()) {
				System.out.println("Can't place critter in world because world is full.");
				return;
			}
			c = rand.nextInt(columns);
			r = rand.nextInt(rows);
		}
		int dir = rand.nextInt(6);
		try {
			Critter newCritter = new Critter(c, r, dir,this);
			newCritter.setCritter(new FileInputStream(file));
			critters.add(newCritter);
			world[c][r].setCritter(newCritter);
		} catch (FileNotFoundException e) {
			System.out.println("Can't find file to load critter.");
		}
	}
	
	/**
	 * Advances {@code this} by one time step.
	 */
	public void advanceATimeStep() {
		if (!(critters == null)) {
			for (int i=0; i<critters.size(); i++) {
				if (critters.get(i) != null)
				critters.get(i).interpret();
			}
			
			for (int i=0; i<critters.size(); i++) {
				if (critters.get(i) != null)
				critters.get(i).makeATurn();
			}
			
		}
		this.timeSteps++;
		this.current_timestep=this.timeSteps;
		this.freezeWorldVersionAndSave();
	}
	
	/**
	 * Set the values of the world constants to default values if they're not initialized.
	 */
	private void loadDefaultConstants() {
		if (BASE_DAMAGE == -2) BASE_DAMAGE = 100; 
		if (DAMAGE_INC == -2) DAMAGE_INC = 0.2;
		if (ENERGY_PER_SIZE == -2) ENERGY_PER_SIZE = 500;
		if (FOOD_PER_SIZE == -2) FOOD_PER_SIZE= 200;
		if (MAX_SMELL_DISTANCE == 0) MAX_SMELL_DISTANCE = 10;
		if (ROCK_VALUE == -2) ROCK_VALUE = -1;
		if (columns == -2) columns = 50;
		if (rows == -2) rows = 68;
		if (MAX_RULES_PER_TURN == -2) MAX_RULES_PER_TURN = 999;
		if (SOLAR_FLUX == -2) SOLAR_FLUX = 1;
		if (MOVE_COST == -2) MOVE_COST = 3;
		if (ATTACK_COST == -2) ATTACK_COST = 5;
		if (GROW_COST == -2) GROW_COST = 1;
		if (BUD_COST == -2) BUD_COST = 9;
		if (MATE_COST == -2) MATE_COST = 5;
		if (RULE_COST == -2) RULE_COST = 2;
		if (ABILITY_COST == -2) ABILITY_COST = 25;
		if (INITIAL_ENERGY == -2) INITIAL_ENERGY = 250;
		if (MIN_MEMORY == -2) MIN_MEMORY = 8;
	}
	
	/**
	 * Initializes a new world using constants if world is not initialized.
	 */
	public void newWorld() {
		if (world == null) {
			world = new Hex[columns][rows];
			for (int i=0; i<columns; i++) {
				for (int j=0; j<rows; j++) {
					world[i][j] = new Hex(i, j, this.ROCK_VALUE);
				}
			}
			setOutsideToRocks();
		}
		
	}
	
	/*
	 * to verify whether a coordinate ({@code c}, {@code r}) is in the world.
	 */
	public boolean isOutofBounds(int c,int r)
	{
		if(c>=columns  || 2*r -c < 0  || 2*r -c >= 2*rows - columns  || c < 0)
			return true;
		else
			return false;
		
		
	}
	
	/**
	 * Gets the world constant {@code RULE_COST}.
	 * @return
	 * 			the value of {@code RULE_COST}
	 */
	public int getRuleCost() {
		return this.RULE_COST;
	}
	
	/**
	 * Gets the world constant {@code ABILITY_COST}.
	 * 
	 * @return
	 * 			the value of {@code ABILITY_COST}
	 */
	public int getAbilityCost() {
		return this.ABILITY_COST;
	}
	
	/**
	 * Gets the world constant {@code SOLAR_FLUX}.
	 * 
	 * @return
	 * 			the value of {@code SOLAR_FLUX}
	 */
	public int getSolarFlux() {
		return this.SOLAR_FLUX;
	}
	
	/**
	 * Gets the world constant {@code ENERGY_PER_SIZE}.
	 * 
	 * @return
	 * 			the value of {@code ENERGY_PER_SIZE}
	 */
	public int getEnergyPerSize() {
		return this.ENERGY_PER_SIZE;
	}
	
	/**
	 * Gets the world constant {@code FOOD_PER_SIZE}.
	 * 
	 * @return
	 * 			the value of {@code FOOD_PER_SIZE}
	 */
	public int getFoodPerSize() {
		return this.FOOD_PER_SIZE;
	}
	
	/**
	 * Gets the world constant {@code ATTACK_COST}.
	 * 
	 * @return
	 * 			the value of {@code ATTACK_COST}
	 */
	public int getAttackCost() {
		return this.ATTACK_COST;
	}
	
	/**
	 * Gets the world constant {@code BASE_DAMAGE}.
	 * 
	 * @return
	 * 			the value of {@code BASE_DAMAGE}
	 */
	public int getBaseDamage() {
		return this.BASE_DAMAGE;
	}
	
	/**
	 * Gets the world constant {@code DAMAGE_INC}.
	 * 
	 * @return
	 * 			the value of {@code DAMAGE_INC}
	 */
	public double getDamageInc() {
		return this.DAMAGE_INC;
	}
	
	/**
	 * Gets the world constant {@code GROW_COST}.
	 * 
	 * @return
	 * 			the value of {@code GROW_COST}
	 */
	public int getGrowCost() {
		return this.GROW_COST;
	}
	
	/**
	 * Gets the world constant {@code MOVE_COST}.
	 * 
	 * @return
	 * 			the value of {@code MOVE_COST}
	 */
	public int getMoveCost() {
		return this.MOVE_COST;
	}
	
	/**
	 * Gets the world constant {@code ROCK_VALUE}.
	 * 
	 * @return
	 * 			the value of {@code ROCK_VALUE}
	 */
	public int getRockValue() {
		return this.ROCK_VALUE;
	}
	
	/**
	 * Gets the world constant {@code MAX_RULES_PER_TURN}.
	 * 
	 * @return
	 * 			the value of {@code MAX_RULES_PER_TURN}
	 */
	public int getMaxRulesPerTurn() {
		return this.MAX_RULES_PER_TURN;
	}
	
	/**
	 * Gets the world constant {@code MIN_MEMORY}.
	 * 
	 * @return
	 * 			the value of {@code MIN_MEMORY}
	 */
	public int getMinMemory() {
		return this.MIN_MEMORY;
	}
	
	/**
	 * Gets the hex of (c,r) in the world.
	 * 
	 * @param c
	 * 			column number
	 * @param r
	 * 			row number
	 * @return
	 * 			the Hex object in (c,r)
	 * 			null if out of bound
	 */
	public Hex getHex(int c, int r) {
		if (c >= 0 && r >= 0 && c < columns && r < rows) return world[c][r];
		return null;
	}
	
	/**
	 * Deletes a {@code critter} from the world critter list.
	 * 
	 * @param critter
	 * 			the critter to be deleted
	 */
	public  boolean deleteCritter(Critter critter) {
		boolean removed = critters.remove(critter);
		if (!removed) System.out.println("Can't remove critter from world because it's not found.");
		this.addtoDeadLists(critter);
		return removed;
	}
	/**
	 * add a newly created critter to the world
	 * @param critter
	 * @param c
	 * @param r
	 */
	public void addCritter(Critter critter,int c,int r)
	{
		try {
		if(world[c][r].lock.writeLock().tryLock() && world[c][r].isEmpty())
		{	world[c][r].setCritter(critter);
			critters.add(critter);
			critter.id=this.objectIDGenerator();
		}else
		{
			critter.id=-1;		//set the critter's id to -1, which means adding fail.
		}
		}finally
		{
			world[c][r].lock.writeLock().unlock();
		}
	}
	/**
	 * @return return all critter into an Object array
	 */
	public Object[] allCritter()
	{
		return critters.toArray();
	}
	
	
	
	/**
	 * @return a unique object id (int), id is an increment and is thread safety.
	 */
	public  int objectIDGenerator()
	{
		this.idUpdateLock.lock();
		try{
		objectID=objectID+1;
		}
		finally {
			this.idUpdateLock.unlock();
		}
		return objectID;
		
	}
	
	
	
	public ReentrantLock updateLock=new ReentrantLock();
	/**
	 * this method should be called after every possible states change. This saves a current 
	 * world version and add to our world Version HashMap.
	 */
	public  void freezeWorldVersionAndSave()
	{
		updateLock.lock();
		try {
			
			freezedWorld currentFreezedWorld = new freezedWorld(this.columns,this.rows,this);
			if(!lastFreezedWorld.equals(currentFreezedWorld)){	
				//must check if the two freezedWorld is not the same, then we can increase the
				//current_version_number and this version of the world.
				this.current_version_number++;
				currentFreezedWorld.setCurrent_version_number(this.current_version_number);
				worldVersion.put(new Integer(this.current_version_number),currentFreezedWorld);
			}
			
			lastFreezedWorld=currentFreezedWorld;
		}
		finally
		{
			updateLock.unlock();
		}
	}
	
	/**
	 * given two freezedWorld, so we can compute their difference.
	 * @param LastUpdate_since the request update_since param
	 * @param last last freezedWorld
	 * @param current current freezedWorld
	 * @return diff is a bundle of the difference of two freezedWorld
	 */
	public diff computeDiff(int LastUpdate_since,freezedWorld last, freezedWorld current)
	{	
	      diff newdiff = new diff();
	      newdiff.cols=current.cols;
		  newdiff.rows=current.rows;
		  newdiff.current_timestep=current.current_timestep;
		  newdiff.current_version_number=current.current_version_number;
		  newdiff.dead_critters=current.dead_critters;
		  newdiff.name=current.name;
		  newdiff.population=current.population;
		  newdiff.rate=current.rate;
		  newdiff.update_since=LastUpdate_since;
		  if(last==null)
			  return newdiff;
		  
		  
		  if(last.current_version_number == 0 || last.cols!=current.cols || last.rows!=current.rows){
			  //return all the diff thing.
			  
	    	  	for(int c=0;c<current.cols;c++)
	    	  		for(int r=0;r<current.rows;r++)
	    	  		{
	    	  			if(!(c>=current.cols  || 2*r -c < 0  || 2*r -c >= 2*current.rows - current.cols  || c < 0))
	    	  			newdiff.state.add(current.map[c][r]);
	    	  		}
	    	  	
	      }else
	      {
	    	  for(int c=0;c<current.cols;c++)
	    	  		for(int r=0;r<current.rows;r++)
	    	  		{
	    	  			if(!(c>=current.cols  || 2*r -c < 0  || 2*r -c >= 2*current.rows - current.cols  || c < 0) &&!current.map[c][r].equals(last.map[c][r]))
	    	  			newdiff.state.add(current.map[c][r]);
	    	  		}
	      }
		  
		  
		  return newdiff;
	}
	/**
	 * Get the freezedWorldByVersion. This is thread safe, because we use a thread safe hashmap.
	 * @param i worldVersion
	 * @return
	 */
	public freezedWorld getWorldByVersion(int i)
	{
		return worldVersion.get(i);
	}
	/**
	 * @return the deadLists to an Object array.
	 */
	public Object[] deadCritters()
	{
		return deadLists.toArray();
	}
	/**
	 * add to deadLists by a critter's id
	 * @param id
	 */
	public void addtoDeadLists(int id)
	{
		deadLists.add(id);
	}
	public void addtoDeadLists(Critter c)
	{
		deadLists.add(c.id);
	}
	/**
	 * delete Critter just by id using admin authorization
	 * @param id
	 * @return
	 */
	public boolean deleteCritterAdmin(int id)
	{
		Critter toDelete=null;
		for(Critter c:this.critters)
		{
			if(c.id==id)
			{
				toDelete = c;
				break;
				
			}
		}
		this.critters.remove(toDelete);
		if(toDelete!=null)
		{
			world[toDelete.c][toDelete.r].setEmpty();
			addtoDeadLists(toDelete.id);
			this.freezeWorldVersionAndSave();
			return true;
		}
		return false;
	}
	/**
	 * if the critter cannot be deleted by the current session_id, it will reject to be deleted
	 * @param id id of the critter
	 * @param session_id session_id
	 * @return
	 */
	public boolean deleteCritterWithCheck(int id, int session_id)
	{
		Critter toDelete=null;
		for(Critter c:this.critters)
		{
			if(c.id==id && session_id == c.session_id)
			{
				toDelete = c;
				break;
				
			}
		}
		this.critters.remove(toDelete);
		if(toDelete!=null)
		{
			world[toDelete.c][toDelete.r].setEmpty();
			addtoDeadLists(toDelete.id);
			this.freezeWorldVersionAndSave();
			return true;
		}
		return false;
	}
	/**
	 * Just like deleteCritter method, this method retrieves all the method of the critter with no session_id check.
	 * @param id
	 * @return 
	 */
	public displayHex retrieveCritterAdmin(int id)
	{
		
		for(Critter c:this.critters)
		{
			if(c.id==id)
			{
				displayHex thisCritter = new displayHex();
				thisCritter.col=c.c;
				thisCritter.row=c.r;
				thisCritter.direction=c.dir;
				thisCritter.mem=c.mem.clone();
				thisCritter.program=c.genome.toString();
				thisCritter.recently_executed_rule=c.getLastExcutedRuleIndex();
				thisCritter.species_id=c.species;
				thisCritter.type="critter";
				return thisCritter;
			}
		}
		return null;
	}
	/**
	 * this method retrieves information with session_id check. Only with the same session_id can retrieve all the critter's information.
	 */
	public displayHex retrieveCritterWithCheck(int id,int session_id)
	{
		
		for(Critter c:this.critters)
		{
			if(c.id==id)
			{
				displayHex thisCritter = new displayHex();
				thisCritter.col=c.c;
				thisCritter.row=c.r;
				thisCritter.direction=c.dir;
				thisCritter.mem=c.mem.clone();
				thisCritter.species_id=c.species;
				thisCritter.type="critter";
				thisCritter.recently_executed_rule=-1;
				if(c.session_id==session_id)
				{
				thisCritter.program=c.genome.toString();
				thisCritter.recently_executed_rule=c.getLastExcutedRuleIndex();
				}
				return thisCritter;
			}
		}
		return null;
		
	}
	
}
