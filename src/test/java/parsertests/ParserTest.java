package parsertests;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Ignore;
import org.junit.Test;

import ast.Node;
import ast.Program;
import parse.Parser;
import parse.ParserFactory;
/**
 * This class contains tests for the Critter parser.
 */
public class ParserTest {
	private Program prog;
	private Program prog2;
 
	@Test
	public void test0()
	{
		InputStream in = ParserTest.class.getResourceAsStream("1.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        prog = p.parse(r);
        StringBuilder sb = new StringBuilder();
        prog.equals(prog);
        prog.prettyPrint(sb);
        System.out.println(sb);
        assertTrue(p!=null);
	}
	
	@Ignore
    @Test
    public void test1()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("draw_critter.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
	@Ignore
    @Test
    public void test2()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("example-rules.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    @Ignore
    @Test
    public void test3()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("mutated_critter_1.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    @Ignore
    @Test
    public void test4()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("mutated_critter_2.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    @Ignore
    @Test
    public void test5()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("mutated_critter_3.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    @Ignore
    @Test
    public void test6()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("mutated_critter_4.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    
    @Ignore
    @Test
    public void test7()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("mutated_critter_5.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    @Ignore
    @Test
    public void test8()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("mutated_critter_6.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }
    @Ignore
    @Test
    public void test9()
    {
    	 	InputStream in = ParserTest.class.getResourceAsStream("unmutated_critter.txt");
         Reader r = new BufferedReader(new InputStreamReader(in));
         Parser p = ParserFactory.getParser();
         prog = p.parse(r);
         assertTrue(p!=null);
    }

}
