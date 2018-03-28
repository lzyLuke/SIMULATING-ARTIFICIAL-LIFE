package simulate;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class spiralCritterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
	World w = new World();
	w.readFile(new File("spiralworld.txt"));
	Critter critter = w.world[15][15].getCritter();
	
	System.out.println(w.reprensentWorld());
	for(int i=0;i<100;i++)
	{
			w.advanceATimeStep();
			System.out.println("( "+critter.c+","+critter.r+") -->");
	}
		
	}

}
