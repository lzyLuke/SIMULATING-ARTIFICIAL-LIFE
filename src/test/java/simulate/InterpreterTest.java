package simulate;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

import ast.Program;
import parse.Parser;
import parse.ParserFactory;
import parsertests.ParserTest;

public class InterpreterTest {
	Program prog;
	
	@Before
	public void setUp() throws Exception {
	
	}

	@Test
	public void testInterpreterImpl() {

		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
		
	}

	@Test
	public void test1() {
		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
	}
	
	@Test
	public void test2() {
		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
	}
	
	@Test
	public void test3() {
		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
	}
	
	@Test
	public void test4() {
		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
	}
	
	@Test
	public void test5() {
		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
	}
	
	@Test
	public void test6() {
		World w = new World();
		w.readFile(new File("world.txt"));
		InputStream in = InterpreterTest.class.getResourceAsStream("example_critter.txt");
		Critter critter = new Critter(1,2,3,w);
		critter.setCritter(in);
		critter.printCritter();
	}

}
