package simulate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import ast.Node;
import ast.Program;
import ast.ProgramImpl;
import ast.Rule;
import exceptions.SensingError;
import interpret.Interpreter;
import interpret.InterpreterImpl;
import interpret.Outcome;
import interpret.OutcomeImpl;
import parse.Parser;
import parse.ParserFactory;
import web.displayHex;

/**
 * A class that describes a critter.
 * 
 * @author JingjingLi
 *
 */
public class Critter {

	public int c;
	public int r;
	public int dir;
	public int[] mem;
	public Program genome;
	public String species;
	public World myworld;
	public Rule lastExe = null;
	public Outcome outcome;
	public boolean isDead = false;
	public int id;
	public int session_id=-1;
	public int recently_executed_rule;
	/**
	 * Creates a critter.
	 * 
	 * @param column
	 *            the number of column the critter is in
	 * @param row
	 *            the number of row the critter is in
	 * @param direction
	 *            an int [0,5] representing the direction the critter is facing
	 * @Param myworld the world that the current critter is in
	 */
	public Critter(int column, int row, int direction, World myworld) {
		c = column;
		r = row;
		dir = (direction % 6 + 6) % 6;
		this.myworld = myworld;
		if (myworld.getHex(c, r).isEmpty()) {
			myworld.getHex(c, r).setCritter(this);
		}
	}
	
	public Critter(int column, int row, World myworld) {
		c = column;
		r = row;
		Random rand = new Random();
		dir=rand.nextInt(6);
		this.myworld = myworld;
	
	}

	/**
	 * Sets up the critter from a critter file or another form of input.
	 * 
	 * @param in
	 *            the input {@code InputStream}
	 */
	public void setCritter(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			String line = reader.readLine();
			while (!line.contains("-->")) {
				setUpCritter(line);
				reader.mark(99);
				line = reader.readLine();
			}
			if (!legalCritter()) {
				System.out.println("Critter not legal. Can't load rule set.");
			} else {
				reader.reset();
				Parser parser = ParserFactory.getParser();
				genome = parser.parse(reader);
			}
		} catch (IOException e) {
			System.out.println("IOException when trying to read critter file.");
		}
	}

	/**
	 * Sets up the critter attributes when loading a new critter from file.
	 * 
	 * @param line
	 *            a line in a critter file
	 */
	private void setUpCritter(String line) {
		line = line.trim().toLowerCase();
		if (line.startsWith("species")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter species input.");
				species = "unknown";
				return;
			}
			species = temp[1].trim();
		} else if (line.startsWith("memsize")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter memsize input.");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				return;
			}
			int memsize = myworld.getMinMemory();
			try {
				memsize = Integer.parseInt(temp[1].trim());
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parsing memsize of critter.");
			}
			if (memsize <= myworld.getMinMemory()) {
				System.out.println("Critter memsize must be >= " + myworld.getMinMemory() + ".");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				return;
			}
			mem = new int[memsize];
			mem[0] = memsize;
		} else if (line.startsWith("defense")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter defense input.");
				mem[1] = 1;
				return;
			}
			int defense = 1;
			try {
				defense = Integer.parseInt(temp[1].trim());
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parsing defense of critter.");
			}
			if (mem == null) {
				System.out.println("Can't set up defense if mem is not initiated.");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				mem[1] = 1;
				return;
			}
			if (defense < 1) {
				System.out.println("Critter defense must be >= 1.");
				mem[1] = 1;
				return;
			}
			mem[1] = defense;
		} else if (line.startsWith("offense")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter offense input.");
				mem[2] = 1;
				return;
			}
			int offense = 1;
			try {
				offense = Integer.parseInt(temp[1].trim());
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parcing offense of critter.");
			}
			if (mem == null) {
				System.out.println("Can't set up offense if mem is not initiated.");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				mem[2] = 1;
				return;
			}
			if (offense < 1) {
				System.out.println("Critter offense must be >= 1.");
				mem[2] = 1;
				return;
			}
			mem[2] = offense;
		} else if (line.startsWith("size")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter size input.");
				mem[3] = 1;
				return;
			}
			int size = 1;
			try {
				size = Integer.parseInt(temp[1].trim());
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parsing size of critter.");
			}
			if (mem == null) {
				System.out.println("Can't set up size if mem is not initiated.");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				mem[3] = 1;
				return;
			}
			if (size < 1) {
				System.out.println("Critter size must be >= 1.");
				mem[3] = 1;
				return;
			}
			mem[3] = size;
		} else if (line.startsWith("energy")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter energy input.");
				mem[4] = 1;
				return;
			}
			int energy = 1;
			try {
				energy = Integer.parseInt(temp[1].trim());
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parcing energy of critter.");
			}
			if (mem == null) {
				System.out.println("Can't set up energy if mem is not initiated.");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				mem[4] = 1;
				return;
			}
			if (energy < 1) {
				System.out.println("Critter energy must be >= 1.");
				mem[4] = 1;
				return;
			}
			mem[4] = energy;
		} else if (line.startsWith("posture")) {
			String[] temp = line.split(":");
			if (temp.length != 2) {
				System.out.println("Check critter posture input.");
				mem[7] = 0;
				return;
			}
			int posture = 0;
			try {
				posture = Integer.parseInt(temp[1].trim());
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException when parsing posture of critter.");
			}
			if (mem == null) {
				System.out.println("Can't set up posture if mem is not initiated.");
				mem = new int[myworld.getMinMemory()];
				mem[0] = myworld.getMinMemory();
				mem[7] = 0;
				return;
			}
			if (posture < 0 || posture > 99) {
				System.out.println("Critter posture must be between 0 and 99.");
				mem[7] = 0;
				return;
			}
			mem[7] = posture;
		}
	}

	/**
	 * Checks if the critter attributes are legal. Doesn't check mem[5] pass number
	 * because it's not initialized by .
	 * 
	 * @return true if attributes are legal, false otherwise
	 * 
	 */
	private boolean legalCritter() {
		if (mem.length < myworld.getMinMemory()) {
			System.out.println("Memsize < " + myworld.getMinMemory() + ".");
			return false;
		}
		if (mem.length != mem[0]) {
			System.out.println("mem[0] != memsize.");
			return false;
		}
		if (mem[1] < 1) {
			System.out.println("Defensive ability < 1.");
			return false;
		}
		if (mem[2] < 1) {
			System.out.println("Offensive ability < 1.");
			return false;
		}
		if (mem[3] < 1) {
			System.out.println("Size < 1.");
			return false;
		}
		if (mem[4] < 1) {
			System.out.println("Energy < 1.");
			return false;
		}
		/*
		 * if (mem[5] < 1) { System.out.println("Pass number < 1."); return false; }
		 */
		if (mem[6] < 0 || mem[6] > 99) {
			System.out.println("Tag not between 0 and 99.");
			return false;
		}
		if (mem[7] < 0 || mem[7] > 99) {
			System.out.println("Posture not between 0 and 99.");
			return false;
		}
		return true;
	}

	/**
	 * returns the value of {@code appearance}.
	 */
	public int appearance() {

		return mem[3] * 100000 + mem[6] * 1000 + mem[7] * 10 + dir;
	}

	/**
	 * Change the mem of this critter.This function is called when interpreted the
	 * rules, thus only memindex at or over 7 can work.
	 * 
	 * @param memindex
	 *            the index to be used in mem array.
	 * @param number
	 *            the number to be assigned.
	 * @return If successfully change, return true, else return false.
	 */
	public boolean changeMem(int memindex, int number) {
		if (memindex >= mem.length || memindex < 0)
			return false;

		if (legalCritter()) {
			switch (memindex) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				return false;

			case 7:
				if (number < 0 || number > 99)
					return false;
				else
					mem[memindex] = number;
				return true;
			default:
				mem[memindex] = number;
			}

		}
		return true;
	}

	/**
	 * iterpret the rule of this critter in this time step, the outcome is stored in
	 * itself.
	 */
	public void interpret() {
		Interpreter in = new InterpreterImpl(this);
		outcome = in.interpret(genome);
	}

	/**
	 * The critter makes a turn in a time step according to its outcome (must be
	 * call interpret() first).
	 */
	public void makeATurn() {

		String action = outcome.getRep().toLowerCase();
		if (action.equals("wait"))
			this.waitAction();
		else if (action.equals("forward"))
			this.move(1);
		else if (action.equals("backward"))
			this.move(-1);
		else if (action.equals("left"))
			this.turn(-1);
		else if (action.equals("right"))
			this.turn(1);
		else if (action.equals("eat"))
			this.eat();
		else if (action.equals("serve"))
			this.serve(outcome.getNum());
		else if (action.equals("attack"))
			this.attack();
		else if (action.equals("tag"))
			this.tag(outcome.getNum());
		else if (action.equals("grow"))
			this.grow();
		else if (action.equals("bud"))
			this.bud();
		else if (action.equals("mate"))
			this.mate();
	}

	public int getNearby(int index) {

		index = (index % 6 + 6) % 6; // handle of out of bounds

		int direction = (index + dir) % 6;
		int nc = c;
		int nr = r;
		switch (direction) {
		case 0:
			nc = c;
			nr = r + 1;
			break;
		case 1:
			nc = c + 1;
			nr = r + 1;
			break;

		case 2:
			nc = c + 1;
			nr = r;
			break;
		case 3:
			nc = c;
			nr = r - 1;
			break;
		case 4:
			nc = c - 1;
			nr = r - 1;
			break;
		case 5:
			nc = c - 1;
			nr = r;
			break;
		}

		if (myworld.isOutofBounds(nc, nr))
			return -1;

		if (myworld.world[nc][nr].isEmpty())
			return 0;
		else if (myworld.world[nc][nr].isFood())
			return -myworld.world[nc][nr].getFood() - 1;
		else if (myworld.world[nc][nr].isRock())
			return -1;
		else if (myworld.world[nc][nr].isCritter())
			return myworld.world[nc][nr].getCritter().appearance();

		try {
			throw new SensingError();
		} catch (Exception e) {
			System.out.println("Error in Nearby Sensing");
			e.printStackTrace();
		}

		return -1;
	}

	public int getAhead(int index) {
		if (index < 0)
			index = 0; // handle of out of bounds

		int ac = c;
		int ar = r;
		int addc = 0;
		int addr = 0;

		switch (dir) {
		case 0:
			addc = 0;
			addr = 1;
			break;
		case 1:
			addc = 1;
			addr = 1;
			break;

		case 2:
			addc = 1;
			addr = 0;
			break;
		case 3:
			addc = 0;
			addr = -1;
			break;
		case 4:
			addc = -1;
			addr = -1;
			break;
		case 5:
			addc = -1;
			addr = 0;
			break;
		}

		ac = c + addc * index;
		ar = r + addr * index;

		if (myworld.isOutofBounds(ac, ar))
			return -1;

		if (myworld.world[ac][ar].isEmpty())
			return 0;
		else if (myworld.world[ac][ar].isFood())
			return -myworld.world[ac][ar].getFood() - 1;
		else if (myworld.world[ac][ar].isRock())
			return -1;
		else if (myworld.world[ac][ar].isCritter())
			return myworld.world[ac][ar].getCritter().appearance();

		try {
			throw new SensingError();
		} catch (Exception e) {
			System.out.println("Error in Ahead Sensing");
			e.printStackTrace();
		}

		return -1;
	}

	public int getRandom(int index) {
		if (index < 2)
			return 0;// handle of out of bounds
		Random rand = new Random();
		if (index < 0)
			return 0;
		else
			return rand.nextInt(index);

	}

	public int getMem(int index) {
		if (index < 0 || index > mem[0] - 1)
			return 0;
		return mem[index];
	}

	/**
	 * Gets the smell value = step * 1000 + turn.
	 * 
	 * @return the smell value. If there's no food within the hex, returns 1000000.
	 */
	public int getSmell() {
		int col = this.c;
		int row = this.r;
		// hex with the least smellDis is the least
		PriorityQueue<Hex> q = new PriorityQueue<Hex>((a, b) -> a.smellDis - b.smellDis);
		Hex current = myworld.getHex(col, row);
		current.smellColor = "grey";
		current.smellDis = 0;
		current.smellDir = current.getCritterDirection(); // must be a critter in current hex
		current.step = 0;
		current.turn = 0;
		q.add(current);
		Hex temp;
		while (q.size() > 0) {
			current = q.poll();
			col = current.getColumn();
			row = current.getRow();
			
			temp = myworld.getHex(col+1, row);
			if (temp != null && !temp.isRock() && !temp.smellColor.equals("black")) {
				if (temp.smellDis == -1) {
					temp.smellDir = 2;
					temp.step = current.step + 1;
					temp.turn = current.turn + Math.abs(current.smellDir - 2);
					temp.smellDis = current.smellDis + Math.abs(current.smellDir - 2) + 1;
					temp.smellColor = "grey";
					q.add(temp);
				} else {
					if (temp.smellDis > current.smellDis + Math.abs(current.smellDir - 2) + 1) {
						temp.smellDir = 2;
						temp.step = current.step + 1;
						temp.turn = current.turn + Math.abs(current.smellDir - 2);
						temp.smellDis = current.smellDis + Math.abs(current.smellDir - 2) + 1;
					}
				}
			}
			
			temp = myworld.getHex(col-1, row);
			if (temp != null && !temp.isRock() && !temp.smellColor.equals("black")) {
				if (temp.smellDis == -1) {
					temp.smellDir = 5;
					temp.step = current.step + 1;
					temp.turn = current.turn + Math.min(Math.abs(current.smellDir - 5), current.smellDir+1);
					temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir - 5), current.smellDir+1) + 1;
					temp.smellColor = "grey";
					q.add(temp);
				} else {
					if (temp.smellDis > current.smellDis + Math.min(Math.abs(current.smellDir - 5), current.smellDir+1) + 1) {
						temp.smellDir = 5;
						temp.step = current.step + 1;
						temp.turn = current.turn + Math.min(Math.abs(current.smellDir - 5), current.smellDir+1);
						temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir - 5), current.smellDir+1) + 1;
					}
				}
			}
			
			temp = myworld.getHex(col, row+1);
			if (temp != null && !temp.isRock() && !temp.smellColor.equals("black")) {
				if (temp.smellDis == -1) {
					temp.smellDir = 0;
					temp.step = current.step + 1;
					temp.turn = current.turn + Math.min(Math.abs(current.smellDir), 6-current.smellDir);
					temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir), 6-current.smellDir) + 1;
					temp.smellColor = "grey";
					q.add(temp);
				} else {
					if (temp.smellDis > current.smellDis + Math.min(Math.abs(current.smellDir), 6-current.smellDir) + 1) {
						temp.smellDir = 0;
						temp.step = current.step + 1;
						temp.turn = current.turn + Math.min(Math.abs(current.smellDir), 6-current.smellDir);
						temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir), 6-current.smellDir) + 1;
					}
				}
			}
			
			temp = myworld.getHex(col, row-1);
			if (temp != null && !temp.isRock() && !temp.smellColor.equals("black")) {
				if (temp.smellDis == -1) {
					temp.smellDir = 3;
					temp.step = current.step + 1;
					temp.turn = current.turn + Math.abs(current.smellDir - 3);
					temp.smellDis = current.smellDis + Math.abs(current.smellDir - 3) + 1;
					temp.smellColor = "grey";
					q.add(temp);
				} else {
					if (temp.smellDis > current.smellDis + Math.abs(current.smellDir - 3) + 1) {
						temp.smellDir = 3;
						temp.step = current.step + 1;
						temp.turn = current.turn + Math.abs(current.smellDir - 3);
						temp.smellDis = current.smellDis + Math.abs(current.smellDir - 3) + 1;
					}
				}
			}
			
			temp = myworld.getHex(col-1, row-1);
			if (temp != null && !temp.isRock() && !temp.smellColor.equals("black")) {
				if (temp.smellDis == -1) {
					temp.smellDir = 4;
					temp.step = current.step + 1;
					temp.turn = current.turn + Math.min(Math.abs(current.smellDir - 4), current.smellDir+2);
					temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir - 4), current.smellDir+2) + 1;
					temp.smellColor = "grey";
					q.add(temp);
				} else {
					if (temp.smellDis > current.smellDis + Math.min(Math.abs(current.smellDir - 4), current.smellDir+2) + 1) {
						temp.smellDir = 4;
						temp.step = current.step + 1;
						temp.turn = current.turn + Math.min(Math.abs(current.smellDir - 4), current.smellDir+2);
						temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir - 4), current.smellDir+2) + 1;
					}
				}
			}
			
			temp = myworld.getHex(col+1, row+1);
			if (temp != null && !temp.isRock() && !temp.smellColor.equals("black")) {
				if (temp.smellDis == -1) {
					temp.smellDir = 1;
					temp.step = current.step + 1;
					temp.turn = current.turn + Math.min(Math.abs(current.smellDir - 1), 7-current.smellDir);
					temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir - 1), 7-current.smellDir) + 1;
					temp.smellColor = "grey";
					q.add(temp);
				} else {
					if (temp.smellDis > current.smellDis + Math.min(Math.abs(current.smellDir - 1), 7-current.smellDir) + 1) {
						temp.smellDir = 1;
						temp.step = current.step + 1;
						temp.turn = current.turn + Math.min(Math.abs(current.smellDir - 1), 7-current.smellDir);
						temp.smellDis = current.smellDis + Math.min(Math.abs(current.smellDir - 1), 7-current.smellDir) + 1;
					}
				}
			}
			
			current.smellColor = "black";
		}
		int value = 1000000;
		for (int i=0; i<myworld.columns; i++) {
			for (int j=0; j<myworld.rows; j++) {
				temp = myworld.getHex(i, j);
				if (temp.isFood() && temp.smellDis>0 && temp.smellDis - 1 <= 10) { // smellDis calculation is off by 1
					int altValue = (temp.step - 1) * 1000 + temp.turn;
					if (altValue < value) value = altValue;
				}
				if (temp.smellDis>-1)
				temp.resetSmellParams();
			}
		}
		return value;
	}

	/**
	 * Gets the direction of the critter.
	 * 
	 * @return an int between 0 to 5 representing direction
	 */
	public int getDir() {
		return dir;
	}

	/**
	 * 
	 * @return the outcome of this critter for its current turn.
	 */
	public Outcome getOutcome() {
		return outcome;
	}

	public void printCritter() {
		System.out.println("Species:" + species);
		System.out.println("column,row,direction:" + c + "," + r + "," + dir);
		System.out.println("MEM[0]:" + mem[0]);
		System.out.println("MEM[1]:" + mem[1]);
		System.out.println("MEM[2]:" + mem[2]);
		System.out.println("MEM[3]:" + mem[3]);
		System.out.println("MEM[4]:" + mem[4]);
		System.out.println("MEM[5]:" + mem[5]);
		System.out.println("MEM[6]:" + mem[6]);
		System.out.println("MEM[7]:" + mem[7]);
		if (outcome != null)
			System.out.println("Outcome: " + outcome.getRep() + "," + outcome.getNum());
		/*
		 * StringBuilder sb=new StringBuilder(); genome.prettyPrint(sb);
		 * System.out.println(sb);
		 */
	}

	/**
	 * Gets the mem[] of {@code this}. Requires: mem[] is not null
	 * 
	 * @return a deep copy of {@code mem}
	 */
	public int[] getMem() {
		assert mem != null;
		return mem.clone();
	}

	/**
	 * Executes the action wait. The critter waits until the next turn without doing
	 * anything except absorbing solar energy.
	 */
	public void waitAction() {
		int incr = mem[3] * myworld.getSolarFlux();
		if (mem[4] + incr > mem[3] * myworld.getEnergyPerSize()) {
			mem[4] = mem[3] * myworld.getEnergyPerSize();
		} else {
			mem[4] += incr;
		}
	}

	/**
	 * The critter uses some energy to move forward to the hex in front of it or
	 * backward to the hex behind it. If it attempts to move and there is a critter,
	 * food, or a rock in the destination hex, the move fails but still takes
	 * energy.
	 * 
	 * @param d
	 *            the direction to which it moves if d >= 0, it moves forward;
	 *            otherwise backward
	 */
	public void move(int d) {
		mem[4] -= myworld.getMoveCost() * mem[3];
		this.checkDeath();
		if (isDead)
			return;
		if (d >= 0) {
			Hex ahead = this.getHexAhead();
			if (ahead == null)
				return;
			if (ahead.isEmpty()) {
				myworld.getHex(c, r).setEmpty();
				c = ahead.getColumn();
				r = ahead.getRow();
				ahead.setCritter(this);
			}
		} else {
			Hex behind = this.getHexBehind();
			if (behind == null)
				return;
			if (behind.isEmpty()) {
				myworld.getHex(c, r).setEmpty();
				c = behind.getColumn();
				r = behind.getRow();
				behind.setCritter(this);
			}
		}
	}

	/**
	 * The critter rotates 60 degrees right or left. This takes little energy.
	 * 
	 * @param t
	 *            an int representing the direction of turning turn right if t>=0;
	 *            otherwise turn left
	 */
	public void turn(int t) {
		// energy consumption equals critter size
		mem[4] -= mem[3];
		this.checkDeath();
		if (isDead)
			return;
		if (t >= 0) {
			if (dir == 5)
				dir = 0;
			else
				dir += 1;
		} else {
			if (dir == 0)
				dir = 5;
			else
				dir -= 1;
		}
	}

	/**
	 * The critter may eat some of the food that might be available on the hex ahead
	 * of it, gaining the same amount of energy as the food it consumes. When there
	 * is more food available on the hex than the critter can absorb, the remaining
	 * food is left on the hex.
	 */
	public void eat() {
		// energy consumption equals critter size
		mem[4] -= mem[3];
		this.checkDeath();
		if (isDead)
			return;
		Hex ahead = this.getHexAhead();
		if (ahead == null)
			return;
		if (ahead.isFood()) {
			int amount = ahead.getFood();
			int maxAbsorb = mem[3] * myworld.getEnergyPerSize() - mem[4];
			if (amount <= maxAbsorb) {
				mem[4] += amount;
				ahead.setEmpty();
			} else {
				mem[4] += maxAbsorb;
				ahead.setFood(amount - maxAbsorb);
			}
		}
	}

	/**
	 * Gets the hex ahead of the critter.
	 * 
	 * @return the hex in front of the critter null if no hex behind critter
	 */
	public Hex getHexAhead() {
		int ac = c;
		int ar = r;
		switch (dir) {
		case 0:
			ar += 1;
			break;
		case 1:
			ac += 1;
			ar += 1;
			break;
		case 2:
			ac += 1;
			break;
		case 3:
			ar -= 1;
			break;
		case 4:
			ac -= 1;
			ar -= 1;
			break;
		case 5:
			ac -= 1;
			break;
		}
		return myworld.getHex(ac, ar);
	}

	/**
	 * Gets the hex behind the critter.
	 * 
	 * @return the hex behind the critter null if no hex behind critter
	 */
	private Hex getHexBehind() {
		int ac = c;
		int ar = r;
		switch (dir) {
		case 0:
			ar -= 1;
			break;
		case 1:
			ac -= 1;
			ar -= 1;
			break;
		case 2:
			ac -= 1;
			break;
		case 3:
			ar += 1;
			break;
		case 4:
			ac += 1;
			ar += 1;
			break;
		case 5:
			ac += 1;
			break;
		}
		return myworld.getHex(ac, ar);
	}

	/**
	 * A critter may convert some of its own energy into food added to the hex in
	 * front of it, if that hex is either empty or already contains some food.
	 * 
	 * @param amount
	 *            the amount of energy to serve
	 */
	public void serve(int amount) {
		mem[4] -= mem[3];
		this.checkDeath();
		if (isDead)
			return;
		if (amount < 0)
			amount = 0;
		else if (amount > mem[4])
			amount = mem[4];
		if (amount == 0)
			return;
		Hex ahead = this.getHexAhead();
		if (ahead == null)
			return;
		if (ahead.isEmpty()) {
			ahead.setFood(amount);
			mem[4] -= amount;
		} else if (ahead.isFood()) {
			ahead.setFood(ahead.getFood() + amount);
			mem[4] -= amount;
		}
		this.checkDeath();
	}

	/**
	 * The critter may attack a critter directly in front of it. The attack removes
	 * an amount of energy from the attacked critter that is determined by the size
	 * and offensive ability of the attacker and the defensive ability of the
	 * victim.
	 */
	public void attack() {
		Hex ahead = this.getHexAhead();
		if (ahead == null)
			return;
		if (ahead.isCritter()) {
			int cost = mem[3] * myworld.getAttackCost();
			if (mem[4] < cost)
				this.die();
			else {
				mem[4] -= cost;
				this.checkDeath();
				double damage = myworld.getBaseDamage() * mem[3] * (1 / (1 + Math.pow(Math.E, -(myworld.getDamageInc()
						* (mem[3] * mem[2] - ahead.getCritter().getMem(3) * ahead.getCritter().getMem(1))))));
				int rounded = (int) Math.round(damage);
				int aheadE = ahead.getCritter().getMem(4);
				if (aheadE <= rounded) {
					ahead.getCritter().die();
				} else {
					ahead.getCritter().addEnergy(-rounded);
				}
			}
		}
	}

	/**
	 * The critter may tag the critter in front of it, e.g., to mark that critter as
	 * an enemy or a friend.
	 * 
	 * @param t
	 *            the value of tag
	 */
	public void tag(int t) {
		if (mem[4] < mem[3])
			this.die();
		else {
			mem[4] -= mem[3];
			this.checkDeath();
			Hex ahead = this.getHexAhead();
			if (ahead == null)
				return;
			if (ahead.isCritter()) {
				ahead.getCritter().changeTag(t);
			}
		}
	}

	/**
	 * The critter may use energy to increase its size by one unit.
	 */
	public void grow() {
		mem[4] -= myworld.getGrowCost() * mem[3] * this.getComplexity();
		this.checkDeath();
		if (!isDead) {
			mem[3]++;
		}
	}

	/**
	 * Calculates the complexity of the critter.
	 * 
	 * @return an int representing the complexity of the critter
	 */
	private int getComplexity() {
		return genome.numChild() * myworld.getRuleCost() + (mem[2] + mem[1]) * myworld.getAbilityCost();
	}

	/**
	 * Checks if the critter is dead and cleans its body if it's dead.
	 */
	private void checkDeath() {
		if (mem[4] <= 0) {
			die();
		}
	}

	/**
	 * Cleans the dead body of the critter.
	 */
	private void die() {
		Hex hex = myworld.getHex(c, r);
		hex.setFood(myworld.getFoodPerSize() * mem[3]);
		myworld.deleteCritter(this);
		this.isDead = true;
		mem[4] = 0;
	}

	/**
	 * Adds energy to a critter.
	 * 
	 * @param change
	 *            the amount of energy to be added negative if reduce energy
	 */
	public void addEnergy(int change) {
		mem[4] += change;
	}

	/**
	 * Changes the tag of the critter. This method can only be called by another
	 * critter. Has no effect if {@code t} is not between 0 and 99.
	 * 
	 * @param t
	 *            the value of tag to be changed to
	 */
	public void changeTag(int t) {
		// do nothing if out of bounds
		if (t >= 0 && t <= 99)
			mem[6] = t;
	}

	/**
	 * The critter will bud a critter at its behind if successful, it will cost the
	 * parent critter energy and the newly created critter will inherit its parent
	 * feature and it will also has a probability to have a mutation.
	 */
	public void bud() {
		Hex behind = getHexBehind();
		Random rand = new Random();
		mem[4] = mem[4] - myworld.BUD_COST * this.getComplexity();
		this.checkDeath();
		if (isDead == true)
			return; // dead then do nothing

		if (behind != null && behind.isEmpty()) {

			Critter newcritter = new Critter(behind.getColumn(), behind.getRow(), rand.nextInt(6), myworld);
			newcritter.genome = (Program) (genome.copy());
			int[] newMem = new int[mem.length];
			newMem[4] = myworld.INITIAL_ENERGY; // ENERGY set to INITIAL_ENERGY
			newMem[3] = 1; // SIZE to 1
			newMem[7] = 0; // POSTURE to 0
			newMem[6] = 0; // TAG to 0
			newMem[0] = mem[0];
			newMem[1] = mem[1];
			newMem[2] = mem[2];
			newMem[5] = mem[5];
			newcritter.mem = newMem;
			newcritter.mutation();
			newcritter.species = this.species;
			newcritter.outcome = new OutcomeImpl("wait", 0);
			myworld.addCritter(newcritter, behind.getColumn(), behind.getRow());

		}
	}

	/**
	 * If successful it will cost as much energy as a turn, else it will cost a mate
	 * cost.
	 */
	public void mate() {
		boolean successful = false;		//this boolean checks that whether this mate is successful or not
		int newc = 0;	//the column that the new baby critter will in
		int newr = 0;	//the row
		Critter father = null;
		if (this.getHexAhead() != null && // in bound
				this.getHexAhead().isCritter() && // is a critter
				this.getHexAhead().getCritter().getHexAhead().getColumn() == c && // face to face
				this.getHexAhead().getCritter().getHexAhead().getRow() == r
				&& this.getHexAhead().getCritter().getOutcome() != null && // outcome not null
				this.getHexAhead().getCritter().getOutcome().getRep() != null
				&& this.getHexAhead().getCritter().getOutcome().getRep().equals("mate") // string part of outcome is a
																						// "mate"
		) {
			father = this.getHexAhead().getCritter();
			Random rand = new Random();

			if (rand.nextBoolean()) // the baby will be produced behind the current critter
			{
				if (this.getHexBehind() == null || !this.getHexBehind().isEmpty())// oops, the space to born
					successful = false; // is not an empty space,fail!

				else {
					successful = true;
					newc = this.getHexBehind().getColumn();
					newr = this.getHexBehind().getRow();
				}

			} else // the baby will be produced behind the fatherCritter.
			{
				if (father.getHexBehind() == null || !father.getHexBehind().isEmpty()) // space is not empty.
					successful = false;
				else {
					successful = true;
					newc = father.getHexBehind().getColumn();
					newr = father.getHexBehind().getRow();
				}
			}

			if (father.mem[4] < myworld.MATE_COST * father.getComplexity()
					|| this.mem[4] < myworld.MATE_COST * this.getComplexity()) // Not enough energy to do a complete
																				// mating.
				successful = false;

		}

		if (successful) {// successfully create a child critter.

			Random rand = new Random();
			father.mem[4] = father.mem[4] - myworld.MATE_COST * father.getComplexity();
			this.mem[4] = this.mem[4] - myworld.MATE_COST * this.getComplexity();
			this.outcome = new OutcomeImpl("mated", -1); // This is a pseudo action and this action will not be handled in this turn.
			// =================================================
			// This is the part for the inheritance of rules from parents.
			Critter newcritter = new Critter(newc, newr, rand.nextInt(6), myworld);
			LinkedList<Node> rules1 = this.genome.getRuleList();
			LinkedList<Node> rules2 = father.genome.getRuleList();
			int size = rand.nextBoolean() ? rules1.size() : rules2.size(); // the size of rule set randomly from
																			// parents.
			LinkedList<Node> rules3 = new LinkedList<Node>();
			Rule r1;
			Rule r2;
			Rule r3;
			Iterator<Node> it1 = rules1.iterator();
			Iterator<Node> it2 = rules2.iterator();
			for (int i = 0; i < size; i++) {

				if (it1.hasNext() && it2.hasNext()) { // two iterator returns two rules every time,
														// either of them will be assigned to the sub rule list.

					r1 = (Rule) (it1.next());
					r2 = (Rule) (it2.next());
					r3 = rand.nextBoolean() ? (Rule) (r1.copy()) : (Rule) (r2.copy());
					rules3.add(r3);
				}

				else if (it1.hasNext()) { // The rest will will be only inherited from one parent.
					r1 = (Rule) (it1.next());
					r3 = (Rule) (r1.copy());
					rules3.add(r3);
				}

				else if (it2.hasNext()) {
					r2 = (Rule) (it2.next());
					r3 = (Rule) (r2.copy());
					rules3.add(r3);
				}

			}
			Program geno3 = new ProgramImpl();
			assert rules3.size() == size; // the size of rules3 should be this
			geno3.setRuleList(rules3);
			newcritter.genome = geno3;
			// =================================================

			// =================================================
			// This is the part for the inheritance of attributes
			int[] newMem;
			if (rand.nextBoolean()) {// assign the length of the newMem and mem[0]
				newMem = new int[this.mem.length];
				newMem[0] = this.mem[0];
			} else {
				newMem = new int[father.mem.length];
				newMem[0] = father.mem[0];
			}

			newMem[4] = myworld.INITIAL_ENERGY; // ENERGY set to INITIAL_ENERGY
			newMem[3] = 1; // SIZE to 1
			newMem[7] = 0; // POSTURE to 0
			newMem[6] = 0; // TAG to 0
			newMem[1] = rand.nextBoolean() ? mem[1] : father.mem[1];
			newMem[2] = rand.nextBoolean() ? mem[2] : father.mem[2];
			newMem[5] = rand.nextBoolean() ? mem[5] : father.mem[5];

			newcritter.mem = newMem;
			// =================================================
			newcritter.species = this.species;
			newcritter.mutation();
			newcritter.outcome = new OutcomeImpl("wait", 0);
			myworld.addCritter(newcritter, newc, newr);

		} else {
			this.outcome = new OutcomeImpl("mated", -1);		//Unsuccessful mate will result energy reduction.
			mem[4] -= mem[3];
			this.checkDeath();
			if (isDead)
				return;
		}

	}

	/**
	 * give mutations to this critter. A mutation will happen either in attributes
	 * or rule set. With probability p=1/4 there will be at least one mutation. If a
	 * mutation occurs, then there is a 1/4 chance of further mutations.
	 */
	public void mutation() {
		Random rand = new Random();
		boolean increase = false;
		while (rand.nextInt(4) == 1) // Has a probability of 1/4 to mutate
		{
			if (rand.nextBoolean()) // Mutate attributes
			{
				if (rand.nextBoolean()) // to increase a certain attributes.
					increase = true;
				else
					increase = false;

				switch (rand.nextInt(3)) {
				case 0: // mutate defense
					if (increase) {
						mem[1] = mem[1] + rand.nextInt(mem[1]);
						if (mem[1] < 0) // out of bounds
							mem[1] = Integer.MAX_VALUE;
					} else {
						mem[1] = mem[1] - rand.nextInt(mem[1]) / 2;
					}
					break;
				case 1: // mutate offensive
					if (increase) {
						mem[2] = mem[2] + rand.nextInt(mem[2]);
						if (mem[2] < 0)
							mem[2] = Integer.MAX_VALUE;
					} else {
						mem[2] = mem[2] - rand.nextInt(mem[2]) / 2;
					}

					break;
				default: // mutate memory
					if (increase) {
						mem[0] = mem[0] + rand.nextInt(mem[0]);
						if (mem[0] < 0)
							mem[0] = Integer.MAX_VALUE;
					} else {
						mem[0] = mem[0] - rand.nextInt(mem[0]) / 2;
						if (mem[0] <= 8)
							mem[0] = 8;
					}

					int[] newmem = new int[mem[0]]; // change the size of original mem array.
					for (int i = 0; i < 8; i++) {
						newmem[i] = mem[i];
					}
					mem = newmem;

				}

			} else {// Mutate rules
				genome = genome.mutate();
			}

		}

	}

	public static displayHex displayHex(InputStream in)
	{
		try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line=reader.readLine();
		String[] token;
		displayHex hex = new displayHex(0,0,"critter");
		while(line!=null)
		{
			
			token=line.split(":");
			if(token[0].trim().equals("species")){
				hex.species_id=token[1].trim();
			}else if(token[0].trim().equals("memsize")) {
				hex.mem=new int[Integer.parseInt(token[1].trim())];
				hex.mem[0]=Integer.parseInt(token[1].trim());
			}else if(token[0].trim().equals("defense")) {
				hex.mem[1]=Integer.parseInt(token[1].trim());
			}else if(token[0].trim().equals("offense")) {
				hex.mem[2]=Integer.parseInt(token[1].trim());
			}else if(token[0].trim().equals("size")) {
				hex.mem[3]=Integer.parseInt(token[1].trim());
			}else if(token[0].trim().equals("energy")) {
				hex.mem[4]=Integer.parseInt(token[1].trim());
			}else if(token[0].trim().equals("posture")) {
				hex.mem[7]=Integer.parseInt(token[1].trim());
			}
			else if(line.contains("-->"))
				break;
			
			line=reader.readLine();
		}
		StringBuilder sb= new StringBuilder();
		while(line!=null)
		{
			sb.append(line+"\n");
			line=reader.readLine();
		}
		hex.program=sb.toString();
		return hex;
		
		}catch (IOException e) {
			System.out.println("IOException when trying to read critter file.");
		}
		
		return null;
	}
	
	public int getLastExcutedRuleIndex()
	{
		if(lastExe==null) return -1;
		
		LinkedList<Node> ruleList = genome.getRuleList();
		int count=0;
		for(Node n:ruleList)
		{
			if(	((Rule)(n)).equals(lastExe))
			{
				recently_executed_rule=count;
				break;
			}
			count++;
		}
		return count;
	}
	
	
	
}
