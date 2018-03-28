package ast;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import parse.Parser;
import parse.ParserFactory;

public class MutationImplTest {
	
	StringBuilder sb;
    InputStream in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
    Reader r = new BufferedReader(new InputStreamReader(in));
    Parser p = ParserFactory.getParser();
    Program prog = p.parse(r);

	
	public void m1() {
        sb = new StringBuilder();
        sb = prog.prettyPrint(sb);
        System.out.println(sb);
        System.out.println("=======mutation 1=======");
        r = new BufferedReader(new StringReader(sb.toString()));
        Program mutated = null;
        MutationImpl m = new MutationImpl(1);
        mutated = m.getRuleMutation(prog);
        while (mutated.equals(prog)) {
            mutated = m.getRuleMutation(prog);
        }
        sb = new StringBuilder();
        sb = mutated.prettyPrint(sb);
        r = new BufferedReader(new StringReader(sb.toString()));
        prog = p.parse(r);
        assertNotNull(prog);
        System.out.println(sb);
        System.out.println();
	}
	
	//@Ignore
	@Test
	public void m1Repete() { 
	     in = MutationImplTest.class.getResourceAsStream("1.txt");
	     r = new BufferedReader(new InputStreamReader(in));
	     p = ParserFactory.getParser();
	     prog = p.parse(r);
		while (true) {
			this.m1();
			if (sb.toString().trim().equals("")) break;
		}
        in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
		r = new BufferedReader(new InputStreamReader(in));
	    p = ParserFactory.getParser();
	    prog = p.parse(r);
	}

	public void m2() {
        sb = new StringBuilder();
        sb = prog.prettyPrint(sb);
        System.out.println(sb);
        System.out.println("=======mutation 2=======");
        r = new BufferedReader(new StringReader(sb.toString()));
        Program mutated = null;
        MutationImpl m = new MutationImpl(2);
        mutated = m.getRuleMutation(prog);
        while (mutated.equals(prog)) {
            mutated = m.getRuleMutation(prog);
        }
        sb = new StringBuilder();
        sb = mutated.prettyPrint(sb);
        r = new BufferedReader(new StringReader(sb.toString()));
        prog = p.parse(r);
        assertNotNull(prog);
        System.out.println(sb);
        System.out.println();
	}
	
	//@Ignore
	@Test
	public void m2Repete() {
		for (int i=0; i<1000; i++) this.m2();
	}
	

	public void m4() {
        sb = new StringBuilder();
        sb = prog.prettyPrint(sb);
        System.out.println(sb);
        System.out.println("=======mutation 4=======");
        r = new BufferedReader(new StringReader(sb.toString()));
        Program mutated = null;
        MutationImpl m = new MutationImpl(4);
        mutated = m.getRuleMutation(prog);
        while (mutated.equals(prog)) {
            mutated = m.getRuleMutation(prog);
        }
        StringBuilder sb = new StringBuilder();
        sb = mutated.prettyPrint(sb);
        r = new BufferedReader(new StringReader(sb.toString()));
        prog = p.parse(r);
        assertNotNull(prog);
        System.out.println(sb);
        System.out.println();
	}
	
	//@Ignore
	@Test
	public void m4Repete() {
		for (int i=0; i<1000; i++) this.m4();
	}
	
	//@Ignore
	public void m3(){
		  InputStream in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
	        Reader r = new BufferedReader(new InputStreamReader(in));
	        Parser p = ParserFactory.getParser();
	        Program prog = p.parse(r);
	        StringBuilder sb1 = new StringBuilder();
	        sb1=prog.prettyPrint(sb1);
	        System.out.println("Before========");
	        System.out.println(sb1);
	        
	        Program mutated = null;
	        MutationImpl m = new MutationImpl(3);
	        mutated = m.getRuleMutation(prog);
	        while (mutated.equals(prog)) {
	            mutated = m.getRuleMutation(prog);
	        }
	        
	        StringBuilder sb2 = new StringBuilder();
	        sb2 = mutated.prettyPrint(sb2);
	        System.out.println();
	        System.out.println("After========");
	        System.out.println(sb2);
	        r = new BufferedReader(new StringReader(sb2.toString()));
	        prog = p.parse(r);
	        assertNotNull(prog);

	}
	
	@Test
	public void m3Repete(){
			InputStream in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
	        Reader r = new BufferedReader(new InputStreamReader(in));
	        Parser p = ParserFactory.getParser();
	        Program prog;
	        for(int i=0;i<10000;i++)
	        {
	        
	        	prog = p.parse(r);
		        StringBuilder sb1 = new StringBuilder();
		        sb1=prog.prettyPrint(sb1);
		        System.out.println("Before========");
		        System.out.println(sb1);
		        
		        Program mutated = null;
		        MutationImpl m = new MutationImpl(3);
		        mutated = m.getRuleMutation(prog);
		        while (mutated.equals(prog)) {
		            mutated = m.getRuleMutation(prog);
		        }
	        
		        StringBuilder sb2 = new StringBuilder();
		        sb2 = mutated.prettyPrint(sb2);
		        System.out.println("After========");
		        System.out.println(sb2);
		        System.out.println();
		        System.out.println();
		        r = new BufferedReader(new StringReader(sb2.toString()));
	        }
		  }


	//@Ignore
	@Test
	public void m5Repete() {
		InputStream in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        Program prog;
        for(int i=0;i<1000;i++)
        {
        
        	prog = p.parse(r);
	        StringBuilder sb1 = new StringBuilder();
	        sb1=prog.prettyPrint(sb1);
	        System.out.println("Before========");
	        System.out.println(sb1);
	        
	        Program mutated = null;
	        MutationImpl m = new MutationImpl(5);
	        mutated = m.getRuleMutation(prog);
	        while (mutated.equals(prog)) {
	            mutated = m.getRuleMutation(prog);
	        }
        
	        StringBuilder sb2 = new StringBuilder();
	        sb2 = mutated.prettyPrint(sb2);
	        System.out.println("After========");
	        System.out.println(sb2);
	        System.out.println();
	        System.out.println();
	        r = new BufferedReader(new StringReader(sb2.toString()));
        }
		
		
		
	}

	public void m6() {
        sb = new StringBuilder();
        sb = prog.prettyPrint(sb);
        System.out.println(sb);
        System.out.println("=======mutation 6=======");
        r = new BufferedReader(new StringReader(sb.toString()));
        Program mutated = null;
        MutationImpl m = new MutationImpl(6);
        mutated = m.getRuleMutation(prog);
        while (mutated.equals(prog)) {
            mutated = m.getRuleMutation(prog);
        }
        sb = new StringBuilder();
        sb = mutated.prettyPrint(sb);
        r = new BufferedReader(new StringReader(sb.toString()));
        prog = p.parse(r);
        assertNotNull(prog);
        System.out.println(sb);
        System.out.println();
	}


	@Test
	//@Ignore
	public void m6Repete() {
	     in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
	     r = new BufferedReader(new InputStreamReader(in));
	     p = ParserFactory.getParser();
	     prog = p.parse(r);
		for (int i=0; i<100; i++) this.m6();
	}
	
	//@Ignore
	@Test
	public void equalsTest() {
        InputStream in = MutationImplTest.class.getResourceAsStream("unmutated_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        MutationImpl m = new MutationImpl(6);
        MutationImpl n = new MutationImpl(3);
        assertFalse(m.equals(n));
        assertFalse(n.equals(m));
        m = new MutationImpl(2);
        n = new MutationImpl(2);
        m.setNode(prog, 10);
        assertFalse(m.equals(n));
        assertFalse(n.equals(m));
        m = new MutationImpl(1);
        n = new MutationImpl(1);
        m.setNode(prog, 2000);
        assertFalse(m.equals(n));
        assertFalse(n.equals(m));
        m = new MutationImpl(4);
        n = new MutationImpl(4);
        m.setNode(prog, 0);
        n.setNode(prog, 0);
        assertTrue(m.equals(n));
        assertTrue(n.equals(m));
        m = new MutationImpl(-1);
        m.setNode(prog, -10);
	}
}
