package console;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class ConsoleTest {

	@Test
	@Ignore
	public void test() {
		Console c = new Console();
		c.newWorld();
		//c.worldInfo();
		c.loadCritters("example_critter.txt", 10);
		c.worldInfo();
		/*
		for (int i=0; i<50; i++) {
			for (int j=0; j<68; j++) c.hexInfo(i, j);
		}
		*/
		c.loadWorld("world.txt");
		c.worldInfo();
		//System.out.println(c.w.rows);
	}
	
	@Test
	public void test2() {
		Console c = new Console();
		c.loadWorld("world.txt");
		c.loadCritters("example_critter.txt", 120);
		c.worldInfo();
		c.hexInfo(1, 1);
	}

}
