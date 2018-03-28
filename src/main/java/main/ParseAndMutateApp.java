package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import ast.Program;
import parse.Parser;
import parse.ParserFactory;

public class ParseAndMutateApp {

	public static void main(String[] args) {
		int n = 0;
		String file;
		try {
			if (args.length == 1) {
				file = args[0];
			} else if (args.length == 3 && args[0].equals("--mutate")) {
				n = parsePositive(args[1]);
				file = args[2];
			} else {
				throw new IllegalArgumentException();
			}
			try {
			    InputStream in = new FileInputStream(file);
			    Reader r = new BufferedReader(new InputStreamReader(in));
			    Parser parser = ParserFactory.getParser();
			    Program p = parser.parse(r);
			    StringBuilder sb = new StringBuilder();
			    System.out.println("Original program:");
			    System.out.println("====================================");
			    System.out.println();
			    sb = p.prettyPrint(sb);
			    System.out.println(sb);
			    int type;
			    Program mutated;
			    for (int i=0; i<n; i++) {
			    		mutated = p.mutate();
		    			type = p.getMutationType();
		    			System.out.println();
		    			switch (type) {
		    				case 0: System.out.println("No mutation has occured.");
		    					break;
						case 1: System.out.println("=============== remove ===============");
							break;
						case 2: System.out.println("================ swap ================");
							break;
						case 3: System.out.println("=============== replace ==============");
							break;
						case 4: System.out.println("============== transform =============");
							break;
						case 5: System.out.println("=============== insert ===============");
							break;
						case 6: System.out.println("============== duplicate =============");
							break;
						default: break;
						}
		    			System.out.println();
		    			sb = mutated.prettyPrint(new StringBuilder());
			    		System.out.println(sb);
			    		if (sb.toString().trim().equals("")) {
			    			System.out.println("Rule set is empty. No more mutations!");
			    			break;
			    		}
			    		p = parser.parse(new StringReader(sb.toString()));
			    }
			} catch (FileNotFoundException fnfe) {
				System.out.println("File " + file + " not found.");
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage:\n" + "  <input_file>\n" + " --mutate <n> <input_file>");
            System.out.println();
            System.out.println("n must be a positive integer.");
		}
	}

	/**
     * Parses {@code str} to an integer.
     * 
     * @param str
     *            the string to parse
     * @return the integer represented by {@code str}
     * @throws NumberFormatException
     *             if {@code str} does not contain a parsable integer
     * @throws IllegalArgumentException
     *             if {@code str} represents a negative integer
     */
	public static int parsePositive(String str) {
		int n = Integer.parseInt(str);
		if (n < 0) { throw new IllegalArgumentException(); }
		else { return n; }
	}
}