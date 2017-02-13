package data;

import java.io.PrintStream;

public class NamedValue implements ReferenceValue {
	public static final String[] VALUE_TYPE_NAMES = {"variable","var"};
	
	private final String name;
	
	/**
	 * A constructor for a NamedValue object, that sets the name of the variable it should refer to.
	 * @param name a String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters.
	 */
	public NamedValue(String name){
		this.name = name;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new NamedValue(name);
	}

	/**
	 * Finds a DataPair inside the environment with name equals to the name stored in this NamedValue and evaluates that variable.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		try{
			return getReferencedValue(environment).evaluate(environment, args, output);
		} catch(DataException e){
			throw new EvaluationException(String.format("Can not evaluate, since %s",e.getMessage()),e);
		}
	}
	
	public String toString(){
		return name;
	}

	@Override
	public Value getReferencedValue(DataContainer environment) throws DataException {
		return getReferencedDataPair(environment).getValue();
	}

	@Override
	public Path getReferencedPath(DataContainer environment) {
		return new Path(environment.getPath(),name);
	}

	@Override
	public DataContainer getLevelAboveReferencedValue(DataContainer environment) {
		return environment;
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof NamedValue){
			NamedValue other = (NamedValue) o;
			return name.compareTo(other.name);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public DataPair getReferencedDataPair(DataContainer environment) throws DataException {
		DataPair data = environment.getData(name);
		if(data == null) throw new DataException(String.format("No variable %s exists in %s.",name,environment.getPath()));
		return data;
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof NamedValue && ((NamedValue) other).name.equals(name);
	}
}
