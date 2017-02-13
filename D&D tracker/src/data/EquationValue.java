package data;

/**
 * A Value object that represents an equation between two Value objects.
 * When evaluated this object will evaluate both Value objects and determine whether they are equal.
 */
import java.io.PrintStream;

public class EquationValue implements Value{
	public static final String[] VALUE_TYPE_NAMES = {"equation","equal"};
	
	private Value a;
	private Value b;
	
	/**
	 * Initializes this EquationValue object as the equation between two given Value objects.
	 * @param a The first Value object.
	 * @param b The second Value object.
	 */
	public EquationValue(Value a, Value b){
		this.a = a;
		this.b = b;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new EquationValue(a.copy(),b.copy());
	}
	
	/**
	 * Evaluates both Value objects in this EquationValue object and determines whether their evaluations are equal.
	 * The returned Value object will be a BooleanValue object which represents TRUE if both evaluations were equal and false otherwise.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		return new BooleanValue(a.evaluate(environment, args, output).equals(b.evaluate(environment, args, output)));
	}
	
	public String toString(){
		return String.format("(%s=%s)", a.toString(),b.toString());
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof EquationValue){
			EquationValue other = (EquationValue) o;
			return a.compareTo(other.a);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new EquationValue(a.replaceArgumentsBy(args),b.replaceArgumentsBy(args));
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof EquationValue)) return false;
		EquationValue o = (EquationValue) other;
		return a.equals(o.a) && b.equals(o.b);
	}
}
