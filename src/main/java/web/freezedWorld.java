package web;

import simulate.World;

public class freezedWorld {
	public int current_timestep;
	public int current_version_number;
	public double rate;
	public String name;
	public int population;
	public int rows;
	public int cols;
	public int[] dead_critters;
	public displayHex map[][];

	public freezedWorld(int cols,int rows,World thisWorld)
	{
		//do not assign current_verision here, 
		//because current_version should check if there will be a difference from the previous world.
		this.current_timestep=thisWorld.current_timestep;
		this.rate=thisWorld.rate;
		this.name=thisWorld.name;
		this.population=thisWorld.critters.size();
		this.rows=thisWorld.rows;
		this.cols=thisWorld.columns;
		Object list[] = thisWorld.deadCritters();
		this.dead_critters=new int[list.length];
		for(int i=0;i<thisWorld.deadCritters().length;i++)
		{
			this.dead_critters[i]=(Integer)list[i];
		}
		displayHex newHex;
		
		
		map=new displayHex[cols][rows];
		
		
		
		for(int c=0;c<cols;c++)
			for(int r=0;r<rows;r++){
				if(c>=cols  || 2*r -c < 0  || 2*r -c >= 2*rows - cols  || c < 0)
					continue;
				
				newHex=new displayHex();
				newHex.col=c;
				newHex.row=r;
				
				if(thisWorld.world[c][r].isCritter()){
						newHex.program=thisWorld.world[c][r].getCritter().genome.toString();
						newHex.direction=thisWorld.world[c][r].getCritter().dir;
						newHex.mem=thisWorld.world[c][r].getCritter().mem.clone();
						newHex.id=thisWorld.world[c][r].getCritter().id;
						newHex.recently_executed_rule=thisWorld.world[c][r].getCritter().getLastExcutedRuleIndex();
						newHex.species_id=thisWorld.world[c][r].getCritter().species;
						newHex.type="critter";
						newHex.session_id=thisWorld.world[c][r].getCritter().session_id;
					}
					else if(thisWorld.world[c][r].isFood()){
						newHex.type="food";
						newHex.value=thisWorld.world[c][r].getFood();
						newHex.session_id=-1;

					}
					else if(thisWorld.world[c][r].isEmpty()) {
						newHex.type="nothing";
						newHex.session_id=-1;
					}else if(thisWorld.world[c][r].isRock()) {
						newHex.type="rock";
						newHex.session_id=-1;
					}
				map[c][r]=newHex;
			}

	}
	//null world. world version zero.
	public freezedWorld()
	{
		current_version_number=0;
		name="Origin World";
	}
	public void setCurrent_version_number(int version)
	{
		this.current_version_number=version;
	}
	/**
	 * Override from object, to test whether the states have been changed in two world.
	 */
	@Override
	public boolean equals(Object object)
	{
		if(!(object instanceof freezedWorld)) return false;
		freezedWorld w = ((freezedWorld) object);
		if(this.current_timestep!=w.current_timestep ||
			this.current_version_number!=w.current_version_number ||
			this.cols != w.cols ||
			this.rows != w.rows ||
			this.rate != w.rate ||
			(!this.name.equals(w.name)) ||
			this.population != w.population
		  )
		return false;
		
		if(this.map==null)
			return false;
		if(this.dead_critters==null) return false;
		
		if(this.dead_critters.length!=w.dead_critters.length)
			return false;
		for(int i=0;i<this.dead_critters.length;i++)
			if(this.dead_critters[i]!=w.dead_critters[i])
				return false;
		
		for(int c=0;c<this.cols;c++)
	  		for(int r=0;r<this.rows;r++)
	  		{
	  			if(!this.map[c][r].equals(w.map[c][r]))
	  				return false;
	  		}
		
		return true;
  }
		
	
}
