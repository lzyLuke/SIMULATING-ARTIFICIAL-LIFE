package ast;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import parse.Parser;
import parse.ParserFactory;

public class ProgramImplTest {
    InputStream in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
    Reader r = new BufferedReader(new InputStreamReader(in));
    Parser parser = ParserFactory.getParser();
    Program p = parser.parse(r);
	Random rand = new Random();
	String filename = "unmutated_critter.txt";
	int index = 10;
	StringBuilder sb;


	@Test
	public void copyEqualsTest() {
        assert p instanceof Program;
        Program p1 = (Program) p.copy();
        assertTrue (p1.equals(p));
        assertFalse(p1 == p);
	}
	
	@Test
	public void nodeAtTest() {
		Node n1 = p.nodeAt(0);
		assertTrue(n1 instanceof ProgramImpl);
		Node n2 = p.nodeAt(p.size() - 1);
		assertNotNull(n2);
		// test if nodeAt() works for indices [0, p.size())
		for (int i=0; i<p.size(); i++) {
			n2 = p.nodeAt(i);
			assertNotNull(n2);
		}
		// test if random selection of a node works
		int index;
		for (int i=0; i<p.size(); i++) {
			index = rand.nextInt(p.size());
			n1 = p.nodeAt(index);
			assertNotNull(n1);
		}
	}
	
	@Test
	@Ignore
	public void nodeAtTest2() {
	    InputStream in = MutationImplTest.class.getResourceAsStream("2.txt");
	    Reader r = new BufferedReader(new InputStreamReader(in));
	    Parser parser = ParserFactory.getParser();
	    Program p = parser.parse(r);
		Node n;
		StringBuilder sb;
		for (int i=0; i<p.size(); i++) {
			n = p.nodeAt(i);	        
			sb = new StringBuilder();
	        sb = n.prettyPrint(sb);
	        System.out.println(sb);
		}
	        in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
	         r = new BufferedReader(new InputStreamReader(in));
	         parser = ParserFactory.getParser();
	         p = parser.parse(r);
	}
	
	@Test
	public void prettyPrintingTest() {
	       InputStream in = MutationImplTest.class.getResourceAsStream(filename);
	        Reader r = new BufferedReader(new InputStreamReader(in));
	        Parser p = ParserFactory.getParser();
	        Program prog = p.parse(r);
	        StringBuilder sb = new StringBuilder();
	        sb = prog.prettyPrint(sb);
	        r = new BufferedReader(new StringReader(sb.toString()));
	        Program p2 = p.parse(r);
	        assertTrue (prog.equals(p2));
	}
	
	@Test
	public void prettyPrintingTestRepetition() {
		filename = "draw_critter.txt";
		this.prettyPrintingTest();
		filename = "example-rules.txt";
		this.prettyPrintingTest();
		filename = "test.txt";
		this.prettyPrintingTest();
		filename = "mutated_critter_1.txt";
		this.prettyPrintingTest();
		filename = "mutated_critter_2.txt";
		this.prettyPrintingTest();
		filename = "mutated_critter_3.txt";
		this.prettyPrintingTest();
		filename = "mutated_critter_4.txt";
		this.prettyPrintingTest();
		filename = "mutated_critter_5.txt";
		this.prettyPrintingTest();
		filename = "mutated_critter_6.txt";
		this.prettyPrintingTest();
	}
	
	public void mutateTest() {
	        MutationImpl m = new MutationImpl(rand.nextInt(6) + 1);
	        Program mutated = p.mutate(index, m);
	        StringBuilder sb = new StringBuilder();
	        if (mutated != null) {
		        sb = mutated.prettyPrint(sb);
		        System.out.println(sb + "\n");
		        r = new BufferedReader(new StringReader(sb.toString()));
		        p = parser.parse(r);
		        assertNotNull(p);
	        } else {
	        		System.out.println("\n" + "No mutation happened." + "\n");
	        }
	}
	
	@Test 
	@Ignore
	public void mutateTestRepete() {
		for (int i=0; i<100; i++) {
			index = rand.nextInt(159);
			mutateTest();
		}
	}
	
	public void mutateTest2() {
		Program mutated = p.mutate();
		sb = new StringBuilder();
		sb = mutated.prettyPrint(sb);
		System.out.println(sb + "\n");
        r = new BufferedReader(new StringReader(sb.toString()));
        p = parser.parse(r);
	}
	
	@Test
	public void mutateTest2Repete() {
	     in = MutationImplTest.class.getResourceAsStream("3.txt");
	     r = new BufferedReader(new InputStreamReader(in));
	     parser = ParserFactory.getParser();
	     p = parser.parse(r);
		while (true) {
			mutateTest2();
			if (sb.toString().trim().equals("")) {
				System.out.println("Rule set is empty. No more mutations!");
				break;
			}
		}
	     in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
	     r = new BufferedReader(new InputStreamReader(in));
	     parser = ParserFactory.getParser();
	     p = parser.parse(r);
	}
}
