package simulate;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;

import interpret.OutcomeImpl;

public class CritterTest {
	

	@Test
	@Ignore
	public void waitTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		System.out.println(critter.getMem(4));
		assertTrue (critter != null);
		critter.waitAction();
		System.out.println(critter.getMem(4));
	}
	
	@Test
	@Ignore
	public void moveTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		for (int i=0; i<6; i++) {
			critter.dir = i;
			System.out.println("(" + critter.c + "," + critter.r + ")");
			System.out.println("dir: " + critter.dir);
			critter.move(1);
			System.out.println("(" + critter.c + "," + critter.r + ")");
		}
		for (int i=5; i>=0; i--) {
			critter.dir = i;
			System.out.println("(" + critter.c + "," + critter.r + ")");
			System.out.println("dir: " + critter.dir);
			critter.move(-1);
			System.out.println("(" + critter.c + "," + critter.r + ")");
		}
	}
	
	@Test
	@Ignore
	public void trunTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		for (int i=0; i<6; i++) {
			System.out.println("before right turn dir: " + critter.dir);
			critter.turn(1);
			System.out.println("after right turn dir: " + critter.dir);
		}
		for (int i=0; i<6; i++) {
			System.out.println("before left turn dir: " + critter.dir);
			critter.turn(-1);
			System.out.println("after left turn dir: " + critter.dir);
		}
	}
	
	@Test
	@Ignore
	public void eatTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		Hex ahead = critter.getHexAhead();
		critter.mem[4] = 450;
		System.out.println("Energy before eating: " + critter.getMem(4));
		ahead.setFood(100);
		critter.eat();
		System.out.println("Energy after eating: " + critter.getMem(4));
		critter.mem[4] = 200;
		System.out.println("Energy before eating: " + critter.getMem(4));
		ahead.setFood(100);
		critter.eat();
		System.out.println("Energy after eating: " + critter.getMem(4));
		critter.mem[4] = 1;
		System.out.println("Energy before eating: " + critter.getMem(4));
		ahead.setFood(100);
		critter.eat();
		System.out.println("Energy after eating: " + critter.getMem(4));
		assertTrue(critter.isDead);
		assertTrue(w.getHex(critter.c, critter.r).isFood());
		System.out.println("Food amount: " + w.getHex(critter.c, critter.r).getFood());
	}
	
	@Test
	@Ignore
	public void serveTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		Hex ahead = critter.getHexAhead();
		ahead.setRock();
		critter.mem[4] = 450;
		System.out.println("Energy before serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount before serving: " + ahead.getFood());
		critter.serve(100);
		System.out.println("Energy after serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount after serving: " + ahead.getFood());
		System.out.println();
		ahead.setCritter(critter);;
		critter.mem[4] = 450;
		System.out.println("Energy before serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount before serving: " + ahead.getFood());
		critter.serve(100);
		System.out.println("Energy after serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount after serving: " + ahead.getFood());
		System.out.println();
		ahead.setFood(100);
		critter.mem[4] = 450;
		System.out.println("Energy before serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount before serving: " + ahead.getFood());
		critter.serve(100);
		System.out.println("Energy after serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount after serving: " + ahead.getFood());
		System.out.println();
		ahead.setEmpty();
		System.out.println("Energy before serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount before serving: " + ahead.getFood());
		critter.serve(100);
		assertTrue (ahead.isFood());
		System.out.println("Energy after serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount after serving: " + ahead.getFood());
		System.out.println();
		System.out.println("Energy before serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount before serving: " + ahead.getFood());
		critter.serve(300);
		assertTrue (ahead.isFood());
		assertTrue (critter.isDead);
		assertTrue (w.getHex(critter.c, critter.r).isFood());
		System.out.println("Energy after serving: " + critter.getMem(4));
		System.out.println("Hex ahead food amount after serving: " + ahead.getFood());
		System.out.println();
		System.out.println("Amount of food left by critter dead body: " + w.getHex(critter.c, critter.r).getFood());
	}
	
	@Test
	@Ignore
	public void attackTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		Hex hex2 = w.getHex(4, 3);
		assertTrue(hex2.isCritter());
		Critter c2 = hex2.getCritter();
		Hex ahead = critter.getHexAhead();
		ahead.setCritter(c2);
		System.out.println("Energy before attack (attacker): " + critter.getMem(4));
		System.out.println("Energy before attack (attacked): " + c2.getMem(4));
		while (!c2.isDead) {
			critter.attack();
			System.out.println("Energy after attack (attacker): " + critter.getMem(4));
			System.out.println("Energy after attack (attacked): " + c2.getMem(4));
		}
		assertTrue(ahead.isFood());
		System.out.println("Food amount: " + ahead.getFood());
	}
	
	@Test
	@Ignore
	public void tagTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		Hex hex2 = w.getHex(4, 3);
		assertTrue(hex2.isCritter());
		Critter c2 = hex2.getCritter();
		Hex ahead = critter.getHexAhead();
		ahead.setCritter(c2);
		System.out.println("Energy before tag: " + critter.getMem(4));
		System.out.println("Tag before tag: " + c2.getMem(6));
		critter.tag(10);
		System.out.println("Energy after tag: " + critter.getMem(4));
		System.out.println("Tag after tag: " + c2.getMem(6));
		System.out.println("Energy before tag: " + critter.getMem(4));
		System.out.println("Tag before tag: " + c2.getMem(6));
		critter.tag(-10);
		System.out.println("Energy after tag: " + critter.getMem(4));
		System.out.println("Tag after tag: " + c2.getMem(6));
		System.out.println("Energy before tag: " + critter.getMem(4));
		System.out.println("Tag before tag: " + c2.getMem(6));
		critter.tag(100);
		System.out.println("Energy after tag: " + critter.getMem(4));
		System.out.println("Tag after tag: " + c2.getMem(6));
		critter.mem[4] = 1;
		System.out.println("Energy before tag: " + critter.getMem(4));
		System.out.println("Tag before tag: " + c2.getMem(6));
		critter.tag(10);
		System.out.println("Energy after tag: " + critter.getMem(4));
		System.out.println("Tag after tag: " + c2.getMem(6));
		assertTrue(critter.isDead);
	}
	
	@Test
	@Ignore
	public void growTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(2, 5);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		System.out.println("Size before grow: " + critter.getMem(3));
		System.out.println("Energy before grow: " + critter.getMem(4));
		critter.grow();
		System.out.println("Size after grow: " + critter.getMem(3));
		System.out.println("Energy after grow: " + critter.getMem(4));
		critter.grow();
		System.out.println("Size after grow: " + critter.getMem(3));
		System.out.println("Energy after grow: " + critter.getMem(4));
		critter.grow();
		System.out.println("Size after grow: " + critter.getMem(3));
		System.out.println("Energy after grow: " + critter.getMem(4));
		assertTrue(critter.isDead);
		assertTrue(critter.myworld.getHex(critter.c, critter.r).isFood());
	}
	
	@Test
	@Ignore
	public void budTest()
	{
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(4, 4);
		assertTrue (hex.isCritter());
		Critter critter = hex.getCritter();
		critter.mem[4]=10000;
		critter.bud();
		Hex hex2 = w.getHex(3, 4);
		assertTrue(hex2.isCritter());
		Critter critter2=hex2.getCritter();
		critter2.printCritter();
	
	}
	
	@Test
	@Ignore
	public void mateTest()
	{
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(4, 4);
		assertTrue (hex.isCritter());
		Hex hex2 = w.getHex(4,3);
		assertTrue (hex2.isCritter());
		Critter critter1 = hex.getCritter();
		Critter critter2 = hex2.getCritter();
		critter1.mem[4]=110000;
		critter2.mem[4]=110000;
		System.out.println("Critter1 dir "+critter1.dir);
		System.out.println("Critter2 dir "+critter2.dir);
		
		System.out.println("Critter1 ahead "+critter1.getHexAhead().getColumn()+","+critter1.getHexAhead().getRow());
		System.out.println("Critter2 ahead "+critter2.getHexAhead().getColumn()+","+critter2.getHexAhead().getRow());
		
		critter1.turn(1);
		critter2.turn(-1);
		
		System.out.println("Critter1 ahead "+critter1.getHexAhead().getColumn()+","+critter1.getHexAhead().getRow());
		System.out.println("Critter2 ahead "+critter2.getHexAhead().getColumn()+","+critter2.getHexAhead().getRow());
		
		critter1.outcome=new OutcomeImpl("mate",0);
		critter2.outcome=new OutcomeImpl("mate",0);
		critter1.mate();
		
		assertTrue(w.getHex(4, 2).isCritter() || w.getHex(4, 5).isCritter());
		
		if(w.getHex(4, 2).isCritter())
		{
			w.getHex(4, 2).getCritter().printCritter();
			StringBuilder sb = new StringBuilder();
			w.getHex(4, 2).getCritter().genome.prettyPrint(sb);
			System.out.println(sb);
		}
		else
		{
			w.getHex(4, 5).getCritter().printCritter();
			StringBuilder sb = new StringBuilder();
			w.getHex(4, 5).getCritter().genome.prettyPrint(sb);
			System.out.println(sb);
		}
	}
	
	@Test
	@Ignore
	public void mateTestRepte()
	{
		for(int i=0;i<1000;i++)
		mateTest();
	}
	
	@Test
	@Ignore
	public void mutationTest()
	{
		World w = new World();
		w.readFile(new File("world.txt"));
		assertTrue (w.getNumCrittersAlive() == 3);
		Hex hex = w.getHex(4, 4);
		Critter critter1 = hex.getCritter();
		for(int i=0;i<100;i++)
		critter1.mutation();
		
		critter1.printCritter();
		StringBuilder sb = new StringBuilder();
		critter1.genome.prettyPrint(sb);
		System.out.println(sb);
	}
	

	@Test
	public void smellTest() {
		World w = new World();
		w.readFile(new File("world.txt"));
		Critter critter = new Critter(1,2,3,w);
		try {
			critter.setCritter(new FileInputStream("example_critter.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		critter.getSmell();
	}
}
