package simulate;

import java.util.LinkedList;

import web.displayHex;

public class diff {
	public int current_timestep;
	public int current_version_number;
	public int update_since;
	public double rate;
	public String name;
	public int population;
	public int rows;
	public int cols;
	public int[] dead_critters;
	public LinkedList<displayHex> state = new LinkedList<displayHex>();
	
	
	public void filter(int session_id)
	{
		for(displayHex d:state)
		{
			if(d.type.equals("critter"))
			{
				if(d.session_id!=session_id)
					{
					d.program=null;
					d.recently_executed_rule=-1;
					}
			}
		}
	}
}
