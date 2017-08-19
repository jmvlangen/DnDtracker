package data;

import java.io.PrintStream;

import main.ReadingException;
import main.ValueReader;

/**
 * A Value object that interprets a given Value object when evaluated,
 * i.e. it evaluates the internal Value object and attempts to interpret the outcome, which may mean:
 * Convert it into a reference '_[number]' if it is an IntValue which stores '[number]'.
 * Read it as if it is input, if it is a TextValue object.
 * Throw errors in other cases.
 */
public class InterpretedValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"interpretation"};
	
	private Value value;

	/**
	 * Gives a Value object that represents the interpretation of a given Value object.
	 * @param value A value object.
	 */
	public InterpretedValue(Value value) {
		this.value = value;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new InterpretedValue(value.copy());
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		ValueReader valueReader = new ValueReader();
		return interpretValue(value.evaluate(environment,args,output),valueReader).evaluate(environment, args, output);
	}
	
	private Value interpretValue(PrimitiveValue value, ValueReader valueReader) throws EvaluationException {
		if(value instanceof IntValue) return interpretIntValue((IntValue) value);
		if(value instanceof TextValue) return interpretTextValue((TextValue) value, valueReader);
		throw new EvaluationException(String.format("Can not interpret \'%s\' as it is of type \'%s\'.", value.toString(), value.getTypeName()));
	}

	private Value interpretTextValue(TextValue value, ValueReader valueReader) throws EvaluationException {
		try{
			return valueReader.readValue(value.getText());
		} catch(ReadingException e){
			throw new EvaluationException(String.format("Can not interpret \"%s\", since: %s", value.getText(),e.getMessage()),e);
		}
	}

	private Value interpretIntValue(IntValue value) {
		return new NamedValue("_" + value.toString());
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof InterpretedValue){
			return value.compareTo(((InterpretedValue) o).value);
		} else{
			return getTypeName().compareTo(o.getTypeName());
		}
	}
	
	public String toString(){
		return "<" + value.toString() + ">";
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new InterpretedValue(value.replaceArgumentsBy(args));
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return new InterpretedValue(value.getPreEvaluation(environment, args, output));
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof InterpretedValue && ((InterpretedValue) other).value.equals(value);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		ValueReader valueReader = new ValueReader();
		return interpretValue(value.evaluate(environment,args,output),valueReader).evaluateToFirstDataContainer(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		ValueReader valueReader = new ValueReader();
		return interpretValue(value.evaluate(environment,args,output),valueReader).evaluateToFirstAddable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		ValueReader valueReader = new ValueReader();
		return interpretValue(value.evaluate(environment,args,output),valueReader).evaluateToFirstSubtractible(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		ValueReader valueReader = new ValueReader();
		return interpretValue(value.evaluate(environment,args,output),valueReader).evaluateToFirstMultiplicable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		ValueReader valueReader = new ValueReader();
		return interpretValue(value.evaluate(environment,args,output),valueReader).evaluateToFirstDivisible(environment, args, output);
	}

}
