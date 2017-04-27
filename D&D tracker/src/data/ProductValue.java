package data;

/**
 * A Value object that represents the sum of two Value objects.
 */
import java.io.PrintStream;

public class ProductValue implements Value{
	public static final String[] VALUE_TYPE_NAMES = {"sum"};
	
	private Value a;
	private Value b;
	
	/**
	 * Initializes this ProductValue object as the product of two given Value objects.
	 * @param a The first Value object.
	 * @param b The second Value object to be multiplied with the first.
	 */
	public ProductValue(Value a, Value b){
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
		return new ProductValue(a.copy(),b.copy());
	}
	
	/**
	 * Returns the product of the evaluations of the two stored Values.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		try{
			return a.evaluateToFirstMultiplicable(environment,args, output).multiply(b.evaluateToFirstMultiplicable(environment, args, output));
		} catch(ComputationException e){
			throw new EvaluationException(String.format("Can not evaluate: %s",e.getMessage()),e);
		}
	}
	
	public String toString(){
		return String.format("(%s*%s)", a.toString(),b.toString());
	}
	


	@Override
	public int compareTo(Value o) {
		if(o instanceof ProductValue){
			ProductValue other = (ProductValue) o;
			return a.compareTo(other.a);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new ProductValue(a.replaceArgumentsBy(args),b.replaceArgumentsBy(args));
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof ProductValue)) return false;
		ProductValue o = (ProductValue) other;
		return a.equals(o.a) && b.equals(o.b);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstDataContainer(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstAddable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstSubtractible(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstMultiplicable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstDivisible(environment, args, output);
	}
}
