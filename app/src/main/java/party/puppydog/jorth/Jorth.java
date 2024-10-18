/*
 *  === party/puppydog/Jorth.java ===========
 *  the Jorth parser & interpreter
 * (this can be one class because forths are very simple languages)
 */
package party.puppydog.jorth;

import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;

/* TODO: de-staticify this class & its dependents so
	i can have multiple instances of it */
public class Jorth {

	/* interpreter state */
	public static boolean interpret = true; // when not interpreting, compiling
	public static boolean consume_name = false; // need 2 get name after 'def'
	private static int comments = 0; // nestable comments
	/* program output */
	public static String output = "";
	public static String error = "";
	public static String status = "OK";
	/* word builder of current Complex word */
	public static WordBuilder currentWordBuilder = new WordBuilder();
			// TODO: make this a stack and always use the top one, so i can use WordBuilder to compile if-statements and the like
	/* stack of integers to do stuff with */
	public static Deque<Integer> executionStack = new ArrayDeque<>();

	/* Jorth Dictionary */
	public static Map<String, Word> dictionary = new HashMap<>();
	/* define basic dictionary words */
	static {
		/* def keyword */
		dictionary.put("def", new Word("def") {
			@Override
			public void execute() {
				interpret = false;
				consume_name = true;
			}
		});
		/* keyword to end def */
		dictionary.put(";", new Word(";") {
			@Override
			public void execute() {
				error = "ERROR ';': NO DEF TO END";
			}
			@Override
			public void compile() {
				/* build word & push it to dictionary */
				currentWordBuilder.finalize();
				/* print that the word was defined */
				output += "DEFINED " + currentWordBuilder.name;
				/* rest wordbuilder & stop compiling */
				currentWordBuilder = new WordBuilder();
				interpret = true;
			}
		});

		/* basic arithmetic operators */
		dictionary.put("+", new Word.BinaryOp("+", (a, b) -> a + b));
		dictionary.put("-", new Word.BinaryOp("-", (a, b) -> a - b));
		dictionary.put("*", new Word.BinaryOp("*", (a, b) -> a * b));
		dictionary.put("/", new Word.BinaryOp("/", (a, b) -> a / b));
		dictionary.put("%", new Word.BinaryOp("%", (a, b) -> a % b));
		dictionary.put("|", new Word.BinaryOp("|", (a, b) -> a | b));

		/* print function */
		dictionary.put(".", new Word.StackManipulator
		(".", "STACK EMPTY") {
			@Override
			public void stackManip() {
				output += executionStack.pop() + " ";
			}
		});
		/* non-destructive Show */
		dictionary.put(".S", new Word(".S") {
			@Override
			public void execute() {
				executionStack.reversed().forEach((i) -> output += i);
			}
		});
	}

	public String processCommand(String input) {
		/* reset output */
		output = "";
		error = "";
		status = "OK";
		/* tokenize by spaces */
		String[] tokens = input.split("\s");
		/* main interpreter loop */
		try {
			for ( String t : tokens )
				processToken(t);
		} catch (ParseException e) {}
		catch (RuntimeException e) {}

		/* debug */
		System.out.println("> " + input);
		System.out.println("Compilation: " + currentWordBuilder);
		System.out.println("Execution stack: " + executionStack);
		/* ----- */
		return output + '\t' + status + " " + error;
	}

	private void processToken(String t) throws ParseException {
		/* handle comments */
		if (t.equals("//"))
			comments++;
		if (t.equals("\\\\") && comments > 0)
			comments--;
		if (t.equals("\\\\") && comments == 0) {
			error = "INVALID COMMENT EXIT";
			throw new ParseException();
		}
		if (comments > 0) return;

		/* try to get word from dictionary */
		Word w = dictionary.get(t);
		/* if dict has word ... */
		if (w != null) {
			/* ... & interpreting */
			if (interpret)
				w.execute();
			/* ... & compiling & needs a name (this lets you reassign names) */
			else if (consume_name) {
				currentWordBuilder.name = t;
				consume_name = false;
			}
			/* ... & compiling */
			else
				w.compile();
		}
		/* if dict doesn't have word, try parsing int */
		else try {
			Word.Number num = new Word.Number(t, Integer.parseInt(t));
			if (interpret)
				num.execute();
			else
				num.compile();
		} catch (NumberFormatException e) {
			/* if compiling & need name, use as name */
			if (!interpret && consume_name) {
				currentWordBuilder.name = t;
				consume_name = false;
			}
			/* else throw error */
			else {
				error = t + " IS NOT A KNOWN WORD";
				status = "!?";
				throw new ParseException();
			}
		}
	}

	/* simple helper to convert java booleans to forth-style -1 or 0 */
	static int forthBool(boolean bool) {
		return bool ? -1 : 0;
	}

}
