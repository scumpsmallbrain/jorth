/*
 *  === party/puppydog/Word.java ===========
 * 	defines an abstract class Word for the Jorth interpreter to run
 */
package party.puppydog.jorth;

import java.util.NoSuchElementException;

/* runnable binary expression */
interface BinaryOperationExpression {
	int run(int a, int b);
}

public abstract class Word {
	
	String name;
	public Word(String name) {
		this.name = name;
	}

	/* execute word, MUST OVERWRITE */
	public void execute() {}
	/* add word to current builder */
	public void compile() {
		Jorth.currentWordBuilder.add(this);
	}

	/* primitive word that does stack manipulation */
	static class StackManipulator extends Word {
		String errorMsg;

		public StackManipulator(String name, String errorMsg) {
			super(name);
			this.errorMsg = errorMsg;
		}
		
		/* handle any errors in stack manipulation */
		@Override
		public void execute() {
			try {
				stackManip();
			}
			catch (NoSuchElementException e) {
				Jorth.error = "ERROR '"+name+"': " + errorMsg;
				Jorth.status = "!!";
				throw new RuntimeException();
			}
		}
		/* overwrite this to define the behavior w/o worrying abt errors */
		public void stackManip() throws NoSuchElementException {}
	}

	/* binary operator, such as + - * and / */
	static class BinaryOp extends StackManipulator {
		BinaryOperationExpression expr;
		public BinaryOp(String name, BinaryOperationExpression expr) {
			super(name, "NOT ENOUGH OPERANDS");
			this.expr = expr;
		}
		@Override
		public void stackManip() {
			int b = Jorth.executionStack.pop(), a = Jorth.executionStack.pop();
			Jorth.executionStack.push(expr.run(a, b));
		}
	}

	/* instruction to just push a number to the stack */
	static class Number extends Word {
		int value;
		public Number(String name, int value) {
			super(name);
			this.value = value;
		}
		@Override
		public void execute() {
			Jorth.executionStack.push(value);
		}
		@Override
		public String toString() { return "" + value; }
	}

	/* word that expands to a bunch of other words */
	static class Complex extends Word {
		
		public final Word[] instructions;

		public Complex(String name, Word[] instructions) {
			super(name);
			this.instructions = instructions;
		}
		@Override
		public void execute() {
			for (Word w : instructions)
				w.execute();
		}
	}

}
