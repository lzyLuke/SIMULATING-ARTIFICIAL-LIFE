package simulate;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class WorldTest {

	@Test
	public void test() {
		World w = new World();
		w.readFile(new File("world.txt"));
		System.out.println(w.reprensentWorld());
	}
}
