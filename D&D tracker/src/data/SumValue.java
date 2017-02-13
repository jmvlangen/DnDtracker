package data;

/**
 * A Value object that represents the sum of two Value objects.
 */
import java.io.PrintStream;

public class SumValue implements Value{
	public static final String[] VALUE_TYPE_NAMES = {"sum"};
	
	private Value a;
	private Value b;
	
	/**
	 * Initializes this SumValue object as the sum of two given Value objects.
	 * @param a The first Value object.
	 * @param b The second Value object to be added to the first.
	 */
	public SumValue(Value a, Value b){
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
		return new SumValue(a.copy(),b.copy());
	}
	
	/**
	 * Returns the sum of the evaluations of the two stored Value objects.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		try{
			return a.evaluate(environment,args, output).add(b.evaluate(environment, args, output));
		} catch(ComputationException e){
			throw new EvaluationException(String.format("Can not evaluate: %s",e.getMessage()),e);
		}
	}
	
	public String toString(){
		return String.format("(%s+%s)", a.toString(),b.toString());
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof SumValue){
			SumValue other = (SumValue) o;
			return a.compareTo(other.a);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new SumValue(a.replaceArgumentsBy(args),b.replaceArgumentsBy(args));
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof SumValue)) return false;
		SumValue o = (SumValue) other;
		return a.equals(o.a) && b.equals(o.b);
	}
}
