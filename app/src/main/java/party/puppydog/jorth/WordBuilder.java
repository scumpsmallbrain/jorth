/*
 *  === party/puppydog/WordBuilder.java ===========
 * 	defines a Builder class for Jorth words, tied to the main Jorth class's state
 */
package party.puppydog.jorth;

import java.util.Deque;
import java.util.ArrayDeque;

public class WordBuilder {

	String name;
	private Deque<Word> instructions = new ArrayDeque<>();

	/* set name of the word we're defining */
	public void name(String name) {
		this.name = name;
	}
	/* add word to definition */
	public void add(Word word) {
		instructions.push(word);
	}
	/* create word and push to dictionary */
	public void finalize() {
		Word[] arr = new Word[instructions.size()];
		instructions.reversed().toArray(arr);
		Word.Complex word = new Word.Complex(name, arr);
		Jorth.dictionary.put(name, word);
	}
	@Override
	public String toString() {
		return "WordBuilder [name=" + name + ", instructions=" + instructions + "]";
	}
	
}
