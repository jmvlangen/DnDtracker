package data;

import java.io.PrintStream;

/**
 * This Value object represents a Value that should be evaluated within a certain variable.
 * It also records at which level upwards relative to this variable the Value should live.
 */
public class SubVariableValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"subvariable","subvar"};
	
	private Value value;
	private Value subValue;
	private int level;
	
	/**
	 * A constructor for the SubVariableValue.
	 * @param value A Value object which evaluates to a DataContainer in which we want to evaluate the Value subValue.
	 * @param subValue A Value object that we want to perceive relative to the first Value object.
	 * @param level A non-negative integer which indicates the level (upwards) at which this Value object should live.
	 * For example 0 indicates that this Value should be evaluated within the DataContainer that follows from value,
	 * 1 indicates it should be evaluated within the DataContainer above that,
	 * 2 indicates it should be evaluated within the DataContainer above that, etc.
	 * Note that this never goes higher than the top level.
	 */
	public SubVariableValue(Value value, Value subValue, int level){
		this.value = value;
		this.subValue = subValue;
		this.level = level;
	}
	
	/**
	 * A constructor for the SubVariableValue that refers only to a sub value relative to a given Value.
	 * @param value A Value object.
	 * @param level The relative level of the sub value relative to the value of the first parameter.
	 */
	public SubVariableValue(Value value, int level){
		this(value,new VoidValue(),level);
	}
	
	/**
	 * A constructor for the SubVariableValue.
	 * @param value A Value object which evaluates to a DataContainer in which we want to evaluate the Value subValue.
	 * @param subValue A Value object that we want to perceive relative to the first Value object.
	 */
	public SubVariableValue(Value value, Value subValue){
		this(value,subValue,0);
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new SubVariableValue(value.copy(),subValue.copy(),level);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(value.toString());
		if(value instanceof GlobalValue) return sb.append(subValue.toString()).toString();
		for(int i = 0 ; i <= level ; i++) sb.append(".");
		if(!(subValue instanceof VoidValue)) sb.append(subValue.toString());
		return sb.toString();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		environment = getLocalEnvironment(environment, args, output);
		return subValue instanceof VoidValue ? environment.evaluate(environment, args, output) : subValue.evaluate(environment,args,output);
	}
	
	/**
	 * Gives the DataContainer in which the Value object stored in this SubVariableValue object will be evaluated when evaluate is called.
	 * @param globalEnvironment The DataContainer in which the local environment should be determined.
	 * @return The DataContainer in which the Value object stored in this SubVariableValue will be evaluated when evaluate(globalEnvironment,*,*) is called.
	 * @throws EvaluationException If no local DataContainer could be determined. This can be caused by:
	 * - The ReferenceValue object does not refer to a Value in the globalEnvironment DataContainer.
	 * - The Value object which should be the local environment is not a DataContainer.
	 */
	public DataContainer getLocalEnvironment(DataContainer globalEnvironment, Value[] args, PrintStream output) throws EvaluationException {
		DataContainer localEnvironment = value.evaluateToFirstDataContainer(globalEnvironment, args, output);
		for(int i = 1;i < level;i++) localEnvironment = localEnvironment.getLevelAbove();
		return localEnvironment;		
	}

	public int getLevel(){
		return level;
	}
	
	public Value getTopValue(){
		return value;
	}
	
	public Value getSubValue(){
		return subValue;
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof SubVariableValue){
			SubVariableValue other = (SubVariableValue) o;
			return value.compareTo(other.value);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new SubVariableValue(value.replaceArgumentsBy(args),subValue.replaceArgumentsBy(args),level);
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return new SubVariableValue(value.getPreEvaluation(environment, args, output),subValue.getPreEvaluation(environment, args, output),level);
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof SubVariableValue)) return false;
		SubVariableValue o = (SubVariableValue) other;
		return o.level == level && o.value.equals(value) && o.subValue.equals(subValue);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		environment = getLocalEnvironment(environment, args, output);
		return subValue instanceof VoidValue ? environment : subValue.evaluateToFirstDataContainer(environment,args,output);
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		environment = getLocalEnvironment(environment, args, output);
		return subValue instanceof VoidValue ? environment.evaluateToFirstAddable(environment, args, output) : subValue.evaluateToFirstAddable(environment,args,output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		environment = getLocalEnvironment(environment, args, output);
		return subValue instanceof VoidValue ? environment.evaluateToFirstSubtractible(environment, args, output) : subValue.evaluateToFirstSubtractible(environment,args,output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		environment = getLocalEnvironment(environment, args, output);
		return subValue instanceof VoidValue ? environment.evaluateToFirstMultiplicable(environment, args, output) : subValue.evaluateToFirstMultiplicable(environment,args,output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		environment = getLocalEnvironment(environment, args, output);
		return subValue instanceof VoidValue ? environment.evaluateToFirstDivisible(environment, args, output) : subValue.evaluateToFirstDivisible(environment,args,output);
	}
}
