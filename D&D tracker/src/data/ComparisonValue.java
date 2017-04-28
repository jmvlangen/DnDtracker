package data;

/**
 * A Value object that represents an equation between two Value objects.
 * When evaluated this object will evaluate both Value objects and determine whether they are equal.
 */
import java.io.PrintStream;

public class ComparisonValue implements Value{
	public static final String[] VALUE_TYPE_NAMES = {"comparison","compare"};
	
	public static final int LESS_THAN = -2;
	public static final int LESS_THAN_OR_EQUAL = -1;
	public static final int MORE_THAN = 2;
	public static final int MORE_THAN_OR_EQUAL = 1;
	public static final int EQUAL = 0;
	
	private Value a;
	private Value b;
	private int type;
	
	/**
	 * Initializes this EquationValue object as the equation between two given Value objects.
	 * @param a The first Value object.
	 * @param b The second Value object.
	 */
	public ComparisonValue(Value a, Value b){
		this(a,b,EQUAL);
	}
	
	public ComparisonValue(Value a, Value b, int type){
		this.a = a;
		this.b = b;
		this.type = type;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new ComparisonValue(a.copy(),b.copy());
	}
	
	/**
	 * Evaluates both Value objects in this EquationValue object and determines whether their evaluations are equal.
	 * The returned Value object will be a BooleanValue object which represents TRUE if both evaluations were equal and false otherwise.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		switch(type){
		case EQUAL:
			return new BooleanValue(a.evaluate(environment, args, output).equals(b.evaluate(environment, args, output)));
		case LESS_THAN:
			return new BooleanValue(a.evaluate(environment, args, output).compareTo(b.evaluate(environment, args, output)) < 0);
		case MORE_THAN:
			return new BooleanValue(a.evaluate(environment, args, output).compareTo(b.evaluate(environment, args, output)) > 0);
		case LESS_THAN_OR_EQUAL:
			return new BooleanValue(a.evaluate(environment, args, output).compareTo(b.evaluate(environment, args, output)) <= 0);
		case MORE_THAN_OR_EQUAL:
			return new BooleanValue(a.evaluate(environment, args, output).compareTo(b.evaluate(environment, args, output)) >= 0);
		default:
			return new BooleanValue(a.evaluate(environment, args, output).compareTo(b.evaluate(environment, args, output)) == 0);
		}
	}
	
	public String toString(){
		switch(type){
		case LESS_THAN:
			return String.format("(%s < %s)", a.toString(),b.toString());
		case MORE_THAN:
			return String.format("(%s > %s)", a.toString(),b.toString());
		case LESS_THAN_OR_EQUAL:
			return String.format("(%s <= %s)", a.toString(),b.toString());
		case MORE_THAN_OR_EQUAL:
			return String.format("(%s >= %s)", a.toString(),b.toString());
		case EQUAL:
		default:
			return String.format("(%s = %s)", a.toString(),b.toString());
		}
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof ComparisonValue){
			ComparisonValue other = (ComparisonValue) o;
			return a.compareTo(other.a);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new ComparisonValue(a.replaceArgumentsBy(args),b.replaceArgumentsBy(args));
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return new ComparisonValue(a.getPreEvaluation(environment, args, output),b.getPreEvaluation(environment, args, output));
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof ComparisonValue)) return false;
		ComparisonValue o = (ComparisonValue) other;
		return a.equals(o.a) && b.equals(o.b);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' does not evaluate to a collection", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a sum", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a subtraction", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a product", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a division", getTypeName()));
	}
}
