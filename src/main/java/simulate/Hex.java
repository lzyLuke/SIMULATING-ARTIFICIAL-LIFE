package simulate;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Hex {
	public int c, r;
	private boolean isRock = false;
	private boolean isFood = false;
	private int foodAmount;
	private boolean isCritter = false;
	private boolean isEmpty = true;
	private Critter critter;
	private int ROCK_VALUE = -1;
	public ReentrantReadWriteLock lock = new ReentrantReadWriteLock();  
	public int smellDis = -1;
	public String smellColor = "white";
	public int smellDir = -1;
	public int step = -1;
	public int turn = -1;
	
	public Hex(int c, int r, int rockValue) {
		this.c = c;
		this.r = r;
		ROCK_VALUE = rockValue;
	}
	
	/**
	 * Sets the hex content to be rock.
	 */
	public void setRock() {
		isRock = true;
		isFood = false;
		isCritter = false;
		foodAmount = 0;
		critter = null;
		isEmpty = false;
	}
	
	/**
	 * Sets the hex content to be food and sets the food amount.
	 * 
	 * @param amount
	 * 			the amount of food to be put on hex
	 */
	public void setFood(int amount) {
		isFood = true;
		isRock = false;
		isCritter = false;
		isEmpty = false;
		foodAmount = amount;
		critter = null;
	}
	
	/**
	 * Sets the hex content to be critter.
	 * 
	 * @param critter
	 * 			the critter in the hex
	 */
	public void setCritter(Critter critter) {

		isCritter = true;
		isFood = false;
		isRock = false;
		isEmpty = false;
		foodAmount = 0;
		this.critter = critter;
		if (critter.c != c || critter.r != r) {
			critter.myworld.getHex(critter.c, critter.r).setEmpty();
			critter.c = this.c;
			critter.r = this.r;
		}

	}
	
	/**
	 * Sets the hex to be empty.
	 */
	public void setEmpty() {
		isFood = false;
		isRock = false;
		isCritter = false;
		isEmpty = true;
		foodAmount = 0;
		critter = null;
	}
	
	/**
	 * Tests if hex contains rock.
	 * 
	 * @return true if contains rock, false otherwise
	 */
	public boolean isRock() {
		return isRock;
	}
	
	/**
	 * Tests if hex contains food.
	 * 
	 * @return true if contains food, false otherwise
	 */
	public boolean isFood() {
		return isFood;
	}
	
	/**
	 * Tests if hex contains critter.
	 * 
	 * @return true if contains critter, false otherwise
	 */
	public boolean isCritter() {
		return isCritter;
	}
	

	/**
	 * Tests if hex contains critter.
	 * 
	 * @return true if contains critter, false otherwise
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
	/**
	 * Gets the direction of a critter.
	 * Requires: there is a critter in the hex and critter is not null
	 * 
	 * @return a number between 0 and 5 representing critter direction.
	 */
	public int getCritterDirection() {
		assert isCritter && critter != null;
		return critter.getDir();
	}
	
	/**
	 * Gets the terrain of {@code this} hex.
	 * 
	 * @return
	 */
	public int getTerrain() {
		if (isRock) return ROCK_VALUE;
		if (isFood) return -1-foodAmount;
		if (isCritter) return critter.getDir();
		return 0;
	}
	
	/**
	 * Gets the critter in hex.
	 * Requires: there's a critter in hex.
	 * 
	 * @return the critter object
	 */
	public Critter getCritter() {
		assert critter != null;
		return critter;
	}
	/**
	 * Gets the foodAmount of {@code this} hex.
	 * 
	 * @return
	 */
	public int getFood()
	{
		return foodAmount;
	}
	
	/**
	 * Gets the column number of {@code this}.
	 * 
	 * @return this.c
	 */
	public int getColumn() {
		return c;
	}
	
	/**
	 * Gets the row number of {@code this}.
	 * 
	 * @return this.r
	 */
	public int getRow() {
		return r;
	}
	
	/**
	 * Resets the parameters used in smell for future uses.
	 */
	public void resetSmellParams() {
		smellDis = -1;
		smellColor = "white";
		smellDir = -1;
		step = -1;
		turn = -1;
	}
}
