package data;

import java.io.PrintStream;

/**
 * A Data class that represents data containing a single integer
 */
public class IntValue implements PrimitiveValue {
	public static final String[] VALUE_TYPE_NAMES = {"integer","int"};

	private long number;

	/**
	 * A constructor that initializes this IntValue object.
	 * @param number The integer data that this IntValue object should contain.
	 */
	public IntValue(int number){
		this((long) number);
	}

	/**
	 * A constructor that initializes this IntValue object.
	 * @param number The number that should be stored in this IntValue object.
	 */
	public IntValue(long number){
		this.number = number;
	}

	@Override
	public String toString() {
		return String.format("%d", number);
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof IntValue && number == ((IntValue) other).number;
	}

	@Override
	public Value copy() {
		return new IntValue(number);
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}

	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	/**
	 * Returns the addition of this and another IntValue if other is an instance of IntValue.
	 * Throws a ComputationException if this addition fails in the above case.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue add(PrimitiveValue other) throws ComputationException {
		if(other instanceof IntValue) return addIntValue((IntValue) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not add %s to %s.",getTypeName(),other.getTypeName()));
	}

	private PrimitiveValue addIntValue(IntValue other) throws ComputationException{
		try{
			return new IntValue(Math.addExact(number, other.number));
		} catch(ArithmeticException e){
			throw new ComputationException(String.format("Can not add %d and %d, result causes overflow.", number, other.number),e);
		}
	}

	/**
	 * Returns the subtraction of another IntValue from this one if other is an instance of IntValue.
	 * Throws a ComputationException if this subtraction fails in the above case.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue subtract(PrimitiveValue other) throws ComputationException {
		if(other instanceof IntValue) return substractIntValue((IntValue) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not substract %s from %s.",other.getTypeName(),getTypeName()));
	}

	private PrimitiveValue substractIntValue(IntValue other) throws ComputationException{
		try{
			return new IntValue(Math.subtractExact(number, other.number));
		} catch(ArithmeticException e){
			throw new ComputationException(String.format("Can not substract %d from %d, result causes overflow.", other.number, number),e);
		}
	}

	/**
	 * Returns the multiplication of this IntValue with another IntValue if other is an instance of IntValue.
	 * Throws a ComputationException if the multiplication fails in the case above.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue multiply(PrimitiveValue other) throws ComputationException {
		if(other instanceof IntValue) return multiplyIntValue((IntValue) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not multiply %s by %s.",getTypeName(),other.getTypeName()));
	}

	private PrimitiveValue multiplyIntValue(IntValue other) throws ComputationException{
		try{
			return new IntValue(Math.multiplyExact(number, other.number));
		} catch(ArithmeticException e){
			throw new ComputationException(String.format("Can not multiply %d and %d, result causes overflow.", number, other.number),e);
		}
	}

	public PrimitiveValue divideByIntValue(IntValue other) throws ComputationException {
		try{
			return new IntValue(Math.floorDiv(number, other.number));
		} catch(ArithmeticException e){
			throw new ComputationException("Can not divide by zero.",e);
		}
	}

	/**
	 * Returns itself.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) {
		return this;
	}

	/**
	 * Returns this IntValue divided by another IntValue if other is an instance of IntValue.
	 * The result will always be rounded downwards.
	 * Throws a ComputationException if the division fails in the case above.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue divideBy(PrimitiveValue other) throws ComputationException {
		if(other instanceof IntValue) return divideByIntValue((IntValue) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not divide %s by %s.",getTypeName(),other.getTypeName()));
	}

	public long getLong() {
		return number;
	}

	public int compareTo(Value o) {
		if(o instanceof IntValue){
			IntValue other = (IntValue) o;
			if(number > other.number) return 1;
			if(number < other.number) return -1;
			return 0;
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof IntValue && ((IntValue) other).number == number;
	}

	@Override
	public boolean getBool() {
		return true;
	}
}
