package parsertests;

import static org.junit.Assert.*;
import parse.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import ast.*;
import org.junit.Before;
import org.junit.Test;
public class TokenizerTest {
	 Tokenizer t;
	 
	@Before
	public void setUp() throws Exception {
		  InputStream in = ParserTest.class.getResourceAsStream("test.txt");
		  BufferedReader r = new BufferedReader(new InputStreamReader(in));
		  t=new Tokenizer(r);
		  while(t.hasNext())
		  {
			  Token token = t.peek();
			  System.out.println(token.toString());
			  t.next();
			  
		  }
	}
	@Test
	public void testMine ()
	{
		InputStream in = ParserTest.class.getResourceAsStream("unmutated_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        
	}

}
