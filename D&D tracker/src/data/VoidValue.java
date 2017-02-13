package data;

import java.io.PrintStream;

/**
 * This class represents an empty Value object. It will act trivially on every operation.
 */
public class VoidValue implements PrimitiveValue {
	private static final String[] VALUE_TYPE_NAME = {"empty","void"};
	
	/**
	 * Returns other.
	 */
	@Override
	public PrimitiveValue add(PrimitiveValue other) throws ComputationException {
		return other;
	}

	/**
	 * Returns other.
	 */
	@Override
	public PrimitiveValue subtract(PrimitiveValue other) throws ComputationException {
		return other;
	}

	/**
	 * Returns other.
	 */
	@Override
	public PrimitiveValue multiply(PrimitiveValue other) throws ComputationException {
		return other;
	}

	/**
	 * Returns other.
	 */
	@Override
	public PrimitiveValue divideBy(PrimitiveValue other) throws ComputationException {
		return other;
	}

	@Override
	public String getTypeName() {
		return VALUE_TYPE_NAME[0];
	}

	@Override
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAME;
	}

	/**
	 * Does nothing and returns a VoidValue object;
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) {
		return this;
	}
	
	public String toString(){
		return "()";
	}

	@Override
	public Value copy() {
		return new VoidValue();
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof VoidValue;
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof VoidValue) return 0;
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof VoidValue;
	}
}
