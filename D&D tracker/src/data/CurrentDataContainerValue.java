package data;

import java.io.PrintStream;

public class CurrentDataContainerValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"currentEnvironment","current","local"};

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new CurrentDataContainerValue();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.evaluate(environment, args, output);
	}

	public String toString(){
		return "%";
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof CurrentDataContainerValue) return 0;
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof CurrentDataContainerValue;
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment;
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.evaluateToFirstAddable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.evaluateToFirstSubtractible(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.evaluateToFirstMultiplicable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return environment.evaluateToFirstDivisible(environment, args, output);
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return copy();
	}
}
