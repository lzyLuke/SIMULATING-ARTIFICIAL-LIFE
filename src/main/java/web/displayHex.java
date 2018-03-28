package web;

public class displayHex {
	public int row;
	public int col;
	public String type = null;
	public int value;
	public int id;
	public String species_id = null;
	public String program = null;
	public int mem[];
	public int direction;
	public int recently_executed_rule = -1;
	public int session_id=-1;

	public displayHex(int col, int row, String type) {
		this.row = row;
		this.col = col;
		this.type = type;
	}

	public displayHex() {

	}

	/**
	 * Override from object method, test whether two displayHex instances are equal
	 * or not
	 * 
	 * @param displayhex
	 *            the thing to be compared
	 * @return true if they are the same, false if they are not the same
	 */
	@Override
	public boolean equals(Object object) {
		displayHex displayhex;
		if (object instanceof displayHex)
			displayhex = (displayHex) object;
		else
			return false;

		if (displayhex.col != this.col || displayhex.row != this.row)
			return false;
		
		
		
		
		
		if (!(this.type.equals(displayhex.type)))
			return false;
		else if (displayhex.type.equals("critter")) {
			if (displayhex.program.equals(this.program) && displayhex.direction == this.direction
					&& displayhex.id == this.id && displayhex.species_id.equals(this.species_id)
					&& displayhex.recently_executed_rule == this.recently_executed_rule) {
				if (displayhex.mem.length != this.mem.length)
					return false;
				for (int i = 0; i < this.mem.length; i++) {
					if (this.mem[i] != displayhex.mem[i])
						return false;
				}
				return true; // sure to be the same
			} else
				return false;

		} else if (displayhex.type.equals("rock")) {
			return true;
			
		} else if (displayhex.type.equals("nothing")) {
			return true;
		} else if (displayhex.type.equals("food")) {
			if(displayhex.value==this.value)
				return true;
		} 
		return false;
	}
}
