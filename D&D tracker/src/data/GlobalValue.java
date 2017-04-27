package data;

import java.io.PrintStream;

/**
 * A ReferenceValue object that refers to the top most DataContainer in a given environment.
 */
public class GlobalValue implements ReferenceValue {
	public static final String[] VALUE_TYPE_NAMES = {"global","all"};

	@Override
	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}

	@Override
	public Value copy() {
		return new GlobalValue();
	}

	@Override
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		return environment.getTopLevel().evaluate(environment, args, output);
	}

	@Override
	public Value getReferencedValue(DataContainer environment) throws DataException {
		return environment.getTopLevel();
	}

	@Override
	public Path getReferencedPath(DataContainer environment) throws DataException {
		return new Path(environment);
	}

	@Override
	public DataContainer getLevelAboveReferencedValue(DataContainer environment) {
		return environment.getTopLevel();
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof GlobalValue) return 0;
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public DataPair getReferencedDataPair(DataContainer environment) throws DataException {
		throw new DataException("The top level DataContainer is not part of a DataPair.");
	}

	public String toString(){
		return ":";
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof GlobalValue;
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.getTopLevel();
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.getTopLevel().evaluateToFirstAddable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.getTopLevel().evaluateToFirstSubtractible(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.getTopLevel().evaluateToFirstMultiplicable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.getTopLevel().evaluateToFirstDivisible(environment, args, output);
	}
}
