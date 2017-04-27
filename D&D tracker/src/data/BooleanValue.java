package data;

import java.io.PrintStream;

/**
 * This Value object represents a boolean value.
 */
public class BooleanValue implements PrimitiveValue {
	public static final String[] VALUE_TYPE_NAMES = {"boolean","bool"};
	public static final String TRUE_TEXT = "TRUE";
	public static final String FALSE_TEXT = "FALSE";
	
	private boolean flag;
	
	/**
	 * Constructs a BooleanValue object with value equal to the given value.
	 * @param value a Boolean value.
	 */
	public BooleanValue(boolean value){
		flag = value;
	}
	
	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}

	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new BooleanValue(flag);
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return this;
	}

	public int compareTo(Value o) {
		if(o instanceof BooleanValue){
			BooleanValue other = (BooleanValue) o;
			if(flag == other.flag) return 0;
			if(flag && !other.flag) return 1;
			return -1;
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	/**
	 * Returns the logical OR of this and other if the given Value object is a BooleanValue,
	 * i.e. TRUE if this BooleanValue is TRUE and the given BooleanValue otherwise.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	public PrimitiveValue add(PrimitiveValue other) throws ComputationException {
		if(other instanceof BooleanValue) return new BooleanValue(flag || ((BooleanValue) other).flag);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not add %s to %s.",getTypeName(),other.getTypeName()));
	}

	/**
	 * Returns the logical OR of this and NOT other if the given Value object is a BooleanValue,
	 * i.e. TRUE if this BooleanValue is TRUE and NOT the given BooleanValue otherwise.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue subtract(PrimitiveValue other) throws ComputationException {
		if(other instanceof BooleanValue) return new BooleanValue(flag || (!((BooleanValue) other).flag));
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not subtract %s from %s.",other.getTypeName(),this.getTypeName()));
	}

	/**
	 * Returns the logical AND of this and other if the given Value object is a BooleanValue,
	 * i.e. FALSE if this BooleanValue is FALSE and the given BooleanValue otherwise.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue multiply(PrimitiveValue other) throws ComputationException {
		if(other instanceof BooleanValue) return new BooleanValue(flag && ((BooleanValue) other).flag);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not multiply %s with %s.",getTypeName(),other.getTypeName()));
	}

	/**
	 * Returns the logical AND of this and NOT other if the given Value object is a BooleanValue,
	 * i.e. FALSE if this BooleanValue is FALSE and NOT the given BooleanValue otherwise.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue divideBy(PrimitiveValue other) throws ComputationException {
		if(other instanceof BooleanValue) return new BooleanValue(flag && (!((BooleanValue) other).flag));
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not divide %s by %s.",getTypeName(),other.getTypeName()));
	}
	
	public String toString(){
		return "$" + (flag ? TRUE_TEXT : FALSE_TEXT);
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof BooleanValue && ((BooleanValue) other).flag == flag;
	}

	@Override
	public boolean getBool() {
		return flag;
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("Values of type \'%s\' can not be collections.",getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return this;
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return this;
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return this;
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return this;
	}
}
