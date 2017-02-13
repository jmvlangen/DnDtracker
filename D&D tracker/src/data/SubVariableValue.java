package data;

import java.io.PrintStream;

/**
 * This Value object represents a Value that should be evaluated within a certain variable.
 * It also records at which level upwards relative to this variable the Value should live.
 */
public class SubVariableValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"subvariable","subvar"};
	
	private ReferenceValue reference;
	private Value variable;
	private int level;
	
	/**
	 * A constructor for the SubVariableValue.
	 * @param reference A ReferencedValue object of which the referenced Value should be the DataContainer in which we want to evaluate the Value variable.
	 * @param variable A Value object that we want to perceive relative to the referenced Value object.
	 * @param level A non-negative integer which indicates the level (upwards) at which this Value object should live.
	 * For example 0 indicates that this Value should be evaluated within the referenced Value,
	 * 1 indicates it should be evaluated within the DataContainer above that,
	 * 2 indicates it should be evaluated within the DataContainer above that, etc.
	 * Note that this never goes higher than the top level.
	 */
	public SubVariableValue(ReferenceValue reference, Value variable, int level){
		this.reference = reference;
		this.variable = variable;
		this.level = level;
	}
	
	/**
	 * A constructor for the SubVariableValue that refers only to a variable relative to a given reference.
	 * @param reference A ReferencedValue object.
	 * @param level The relative level of the variable relative to the referenced value of the first parameter.
	 */
	public SubVariableValue(ReferenceValue reference, int level){
		this(reference,new VoidValue(),level);
	}
	
	/**
	 * A constructor for the SubVariableValue.
	 * @param reference A ReferencedValue object of which the referenced Value should be the DataContainer in which we want to evaluate the Value variable.
	 * @param variable A Value object that we want to perceive relative to the referenced Value object.
	 */
	public SubVariableValue(ReferenceValue reference, Value variable){
		this(reference,variable,0);
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new SubVariableValue((ReferenceValue) reference.copy(),variable.copy(),level);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(reference.toString());
		for(int i = 0 ; i <= level ; i++) sb.append(".");
		if(!(variable instanceof VoidValue)) sb.append(variable.toString());
		return sb.toString();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		environment = getLocalEnvironment(environment);
		return variable instanceof VoidValue ? environment.evaluate(environment, args, output) : variable.evaluate(environment,args,output);
	}
	
	/**
	 * Gives the DataContainer in which the Value object stored in this SubVariableValue object will be evaluated when evaluate is called.
	 * @param globalEnvironment The DataContainer in which the local environment should be determined.
	 * @return The DataContainer in which the Value object stored in this SubVariableValue will be evaluated when evaluate(globalEnvironment,*,*) is called.
	 * @throws EvaluationException If no local DataContainer could be determined. This can be caused by:
	 * - The ReferenceValue object does not refer to a Value in the globalEnvironment DataContainer.
	 * - The Value object which should be the local environment is not a DataContainer.
	 */
	public DataContainer getLocalEnvironment(DataContainer globalEnvironment) throws EvaluationException {
		try{
			if(level == 0){
				Value value = reference.getReferencedValue(globalEnvironment);
				if(!(value instanceof DataContainer)) throw new EvaluationException(String.format("The variable \'%s\' contains no subvariables", reference.getReferencedPath(globalEnvironment)));
				return (DataContainer) value;
			} else {
				DataContainer localEnvironment = reference.getLevelAboveReferencedValue(globalEnvironment);
				for(int i = 1;i < level;i++) localEnvironment = localEnvironment.getLevelAbove();
				return localEnvironment;
			}			
		} catch(DataException e){
			throw new EvaluationException(String.format("Could not evaluate, since: %s", e.getMessage()),e);
		}
	}

	public int getLevel(){
		return level;
	}
	
	public ReferenceValue getReference(){
		return reference;
	}
	
	public Value getSubValue(){
		return variable;
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof SubVariableValue){
			SubVariableValue other = (SubVariableValue) o;
			return reference.compareTo(other.reference);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new SubVariableValue((ReferenceValue) reference.replaceArgumentsBy(args),variable.replaceArgumentsBy(args),level);
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof SubVariableValue)) return false;
		SubVariableValue o = (SubVariableValue) other;
		return o.level == level && o.reference.equals(reference) && o.variable.equals(variable);
	}
}
