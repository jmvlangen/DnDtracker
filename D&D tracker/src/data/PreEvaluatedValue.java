package data;

import java.io.PrintStream;

/**
 * A Value object that can evaluate before other values are evaluated.
 * This allows constructions wherein arguments are pre-evaluated before being used in an evaluation.
 * This is particularly useful for values which would use non-evaluated arguments otherwise.
 * In order to be able to set the value of a DataPair to this Value object,
 * this object keeps track of a non-negative integer called delay.
 * Whenever this Value object is preevaluated it will lower its delay by one and return itself.
 * Whenever the delay becomes zero it will return the evaluation of the value stored inside instead.
 */
public class PreEvaluatedValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"prevaluate"};
	
	private Value value;
	private int delay;

	/**
	 * Gives a Value object that represents the preevaluation of a given Value object after a certain delay.
	 * @param value A value object.
	 * @param delay A non-negative integer representing the amount of preeveluation steps needed before the aforementioned value object is evaluated.
	 */
	public PreEvaluatedValue(Value value, int delay) {
		this.value = value;
		this.delay = delay;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new PreEvaluatedValue(value.copy(), delay);
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		return value.evaluate(environment, args, output);
	}
	
	@Override
	public int compareTo(Value o) {
		if(o instanceof PreEvaluatedValue){
			return value.compareTo(((PreEvaluatedValue) o).value);
		} else{
			return getTypeName().compareTo(o.getTypeName());
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < delay; i++) sb.append("[");
		sb.append(" ").append(value.toString()).append(" ");
		for(int i = 0; i < delay; i++) sb.append("]");
		return sb.toString();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof PreEvaluatedValue && ((PreEvaluatedValue) other).value.equals(value);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return value.evaluateToFirstDataContainer(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return value.evaluateToFirstAddable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return value.evaluateToFirstSubtractible(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return value.evaluateToFirstMultiplicable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return value.evaluateToFirstDivisible(environment, args, output);
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new PreEvaluatedValue(value.replaceArgumentsBy(args),delay);
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		if(delay > 1) return new PreEvaluatedValue(value.copy(),delay - 1);
		else return value.evaluate(environment, args, output);
	}

}
