package data;

import java.io.PrintStream;

/**
 * A Value object that represents the n-th argument given.
 */
public class ArgumentValue implements Value{
	public static final String[] VALUE_TYPE_NAMES = {"argument","arg"};
	
	private int number;

	/**
	 * Initializes this ArgumentValue object as the n-th argument.
	 * @param argNumber The integer n.
	 */
	public ArgumentValue(int argNumber){
		number = argNumber;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}
	
	public Value copy(){
		return new ArgumentValue(number);
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		return 0 < number && number <= args.length ? args[number-1].evaluate(environment, args, output) : new VoidValue();
	}

	public String toString(){
		return String.format("#%d", number);
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof ArgumentValue){
			ArgumentValue other = (ArgumentValue) o;
			if(number < other.number) return -1;
			if(number == other.number) return 0;
			return 1;
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return 0 < number && number <= args.length ? args[number-1] : new VoidValue();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof ArgumentValue && ((ArgumentValue) other).number == number;
	}
}
