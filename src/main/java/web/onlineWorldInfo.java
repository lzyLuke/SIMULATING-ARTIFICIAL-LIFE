package web;
import java.util.ArrayList;
import java.util.HashMap;

import application.OnlineGUIController;
import simulate.Critter;
import simulate.Hex;
import simulate.World;
public class onlineWorldInfo {
	World myworld;
	public displayHex[][] worldmap=null;
	private HashMap<Integer,displayHex> critterMap = new HashMap<Integer,displayHex>(); //key is critter's id, value is critter
	private HashMap<Integer,Boolean> isDead = new HashMap<Integer,Boolean>();
	public int rows=0;
	public int cols=0;
	public String name;
	public int current_timestep;
	public int current_version_number;
	public int update_since;
	public int old_update_since=0;
	public double rate;
	public int population;
	
	public void updateWorldState(worldChangeReceivedBundle res, OnlineGUIController controller)
	{
		if(rows!=res.rows || cols!=res.cols)
		{
			worldmap=new displayHex[res.cols][res.rows];
			controller.drawHexMap(res.cols, res.rows);
			for(int r=0;r<res.rows;r++)
				for(int c=0;c<res.cols;c++)
				{
					worldmap[c][r]=new displayHex(c,r,"nothing");
					if(c >= res.cols  || 2*r -c < 0  || 2*r -c >= 2*res.rows - res.cols  || c < 0)
					{
						worldmap[c][r].type = "rock";
					}
	
				}
		}
		
		rows=res.rows;
		cols=res.cols;
		name=res.name;
		old_update_since=res.update_since;
		current_timestep=res.current_timestep;
		current_version_number=res.current_version_number;
		update_since=res.current_version_number;
		rate=res.rate;
		population = res.population;
		
		for(int i:res.dead_critters)	{//Let the critter's state be death;
			isDead.put(i, true);
		}
		for(displayHex s:res.state){
			if(s.col>=cols || s.row>=rows)
				continue;
			worldmap[s.col][s.row]=s;
		}
		
	}
	
	public void updateCritterList(displayHex[] list)
	{
		critterMap = new HashMap<Integer,displayHex>();
		for(int i=0;i<list.length;i++)
		{
			critterMap.put(list[i].id, list[i]);
		}
	}
	
	public boolean inBounds(int c,int r)
	{

		if(c >=cols  || 2*r -c < 0  || 2*r -c >= 2*rows - cols  || c < 0)
			return false;
		else
			return true;
	}
	
}