package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;

/**
 * Represents a value that is actually a command.
 * When evaluated this Value will perform the associated command.
 */
public abstract class CommandValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"command","com","c"};
	
	/**
	 * Gives the default name for the command executed by this CommandValue.
	 * @return A String consisting of only alpha_numerical characters, starting with a non-digit, that should be used as the default name corresponding to this CommandValue.
	 */
	public abstract String getDefaultName();
	
	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}
	
	public String toString(){
		return "{SYSTEM COMMAND: " + getDefaultName() + "}";
	}
	
	public int compareTo(Value o){
		if(this.getClass().equals(o.getClass())) return 0;
		return getTypeName().compareTo(o.getTypeName());
	}
	
	public Value replaceArgumentsBy(Value[] args){
		return copy();
	}
	
	public boolean equals(Value other){
		return this.getClass().equals(other.getClass());
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' does not evaluate to a collection", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}
}
