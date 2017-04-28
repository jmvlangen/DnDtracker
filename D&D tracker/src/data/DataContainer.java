package data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import main.Tracker;

/**
 * A Value object that can contain other Value objects inside DataPairs.
 * As a PrimitiveValue object it operates according to hidden variables which names are given ADDITION_NAME, PRODUCT_NAME, DIVISION_NAME and SUBTRACTION_NAME.
 * Furthermore its type, value, and toString method can be modified by the variables with names given by TYPE_NAME, VALUE_NAME and TO_STRING_NAME respectively.
 * As a ReferenceValue it refers to itself only.
 */
public class DataContainer implements PrimitiveValue, Iterable<DataPair>, ReferenceValue {
	public static final String VALUE_NAME = "_value";
	public static final String TYPE_NAME = "_type";
	public static final String ADDITION_NAME = "_add";
	public static final String PRODUCT_NAME = "_product";
	public static final String DIVISION_NAME = "_divide";
	public static final String SUBTRACTION_NAME = "_subtract";
	public static final String TO_STRING_NAME = "_toString";
	public static final String TEMPLATE_NAME = "_template";
	public static final String[] DATA_TYPE_NAMES = {"collection","col"};

	private Collection<DataPair> dataSet;
	private DataPair host;

	/**
	 * A constructor that makes a (top level) DataContainer.
	 */
	public DataContainer(){
		dataSet = new ArrayList<DataPair>();
		host = null;
	}

	/**
	 * A constructor that makes a DataContaier that is part of a DataPair.
	 * @param host The DataPair that contains this DataContainer.
	 */
	public DataContainer(DataPair host){
		this();
		this.host = host;
	}

	@Override
	public String toString() {
		if(containsName(TO_STRING_NAME)){
			try {
				Value value = getData(TO_STRING_NAME).getValue().evaluate(this, new Value[0], Tracker.mainInstance.screen.getOutput());
				if(value instanceof TextValue) return ((TextValue) value).getText();
				return value.toString();
			} catch (EvaluationException|DataException e) {
				Tracker.mainInstance.screen.getOutput().printf("Error: %s\n", e.getMessage());
			}
		}
		return dataContainerString();
	}

	private String dataContainerString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		int i = 0;
		for(DataPair d : dataSet){
			if(d.getName().charAt(0) != '_'){
				if(i > 0) sb.append(",");
				sb.append(d.getName()).append("=").append(d.getValue().toString());
				i += 1;
			}
		}
		return sb.append("}").toString();
	}

	private DataContainer getTemplate() throws DataException {
		try{
			Value value = Path.convertToPath(getDataAbsolute(TEMPLATE_NAME).getValue(), getPath()).getLowestValue();
			if(value instanceof DataContainer) return (DataContainer) value;
			throw new DataException("The template path does not refer to a DataContainer.");
		} catch(PathException e){
			throw new DataException(String.format("Template is not a path, since: ",e.getMessage()),e);
		}
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof DataContainer && dataSet.equals(((DataContainer) other).dataSet);
	}

	/**
	 * @param name A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters that may be the name of a Data object.
	 * @return TRUE if this DataContainer contains a DataObject with name equal to @param name.
	 * If the DataContainer contains a template, it will also return TRUE if the template contains the named variable.
	 * FALSE otherwise
	 */
	public boolean containsName(String name){
		for(DataPair d : dataSet){
			if(d.getName().equals(name)) return true;
		}
		try{
			return getTemplate().containsNameAbsolute(name);
		} catch(DataException e){
			return false;
		}
	}

	/**
	 * Does the same as containsName, but does not check any template that might be in place.
	 * @param name A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters that may be the name of a Data object.
	 * @return TRUE if this DataContainer contains a DataObject with name equal to @param name.
	 * FALSE otherwise
	 */
	public boolean containsNameAbsolute(String name){
		for(DataPair d : dataSet){
			if(d.getName().equals(name)) return true;
		}
		return false;
	}

	/**
	 * Gets a specific data object from this DataContainer or any above it by its name.
	 * @param name A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters that refers to a Data object in this DataContainer.
	 * @return The DataPair object that has name @param name in this DataContainer, or null if such an object does not exist.
	 * Note that it will also look among the Data objects in higher levels if none are found in this DataContainer.
	 * @throws DataException if no DataPair with the given name exists in this DataContainer or any above it.
	 */
	public DataPair getData(String name) throws DataException{
		try{
			return getDataAbsolute(name);
		} catch(DataException e){
			try{
				return getTemplate().getData(name);
			} catch(DataException e1){
				if(isTopLevel()) throw new DataException(String.format("No variable \'%s\' exists in \'%s\'.", name, getPath()),e1);
				try{
					return host.getHost().getData(name);
				} catch(DataException e2){
					throw new DataException(String.format("No variable \'%s\' exists in \'%s\'.", name, getPath()),e2);
				}
			}
		}
	}

	/**
	 * Gets specific data from this DataContainer by its name.
	 * @param name A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters that refers to a Data object in this DataContainer.
	 * @return The DataPair object that has name @param name in this DataContainer.
	 * Note that this method will ONLY look at Data objects in this DataContainer object.
	 * @throws DataException if no DataPair with the given name exists in this DataContainer.
	 */
	public DataPair getDataAbsolute(String name) throws DataException {
		for(DataPair d : dataSet){
			if(d.getName().equals(name)) return d;
		}
		throw new DataException(String.format("No variable \'%s\' exists in \'%s\'.", name, getPath()));
	}

	/**
	 * Adds a DataPair object to this DataContainer if it contains none with the same name.
	 * It is advised to use the other method addData to create DataPair objects within this DataContainer instead of creating them yourself and adding them with this method.
	 * @param data A DataPair object that has this DataContainer as its host.
	 * @return TRUE if this Data object was added to this DataContainer.
	 * FALSE in all other cases.
	 */
	public boolean addData(DataPair data){
		return data.getHost() == this && ( !this.containsNameAbsolute(data.getName()) ) && dataSet.add(data);
	}

	/**
	 * Adds a new DataPair object to this DataContainer with the given name and the given 
	 * @param name A String consisting of only alpha-numerical characters and starting with a non-digit.
	 * @param value A Value object. (not null)
	 * @return The newly created DataPair with the given name and Value.
	 * @throws DataException If a DataPair with the given name already exists in this DataContainer.
	 */
	public DataPair addData(String name, Value value) throws DataException{
		if(containsNameAbsolute(name)) throw new DataException(String.format("A variable %s already exists.",name));
		DataPair data = new DataPair(name,value,this);
		if(!addData(data)) throw new DataException("Could not create %s for an unknown reason.");
		return data;
	}

	/**
	 * Adds a new DataPair object to this DataContainer with the given name and the given 
	 * @param name A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters.
	 * @param value A Value object. (not null)
	 * @return The newly created DataPair with the given name and Value.
	 * @throws DataException If a DataPair with the given name already exists in this DataContainer.
	 */
	public DataPair addSubDataContainer(String name) throws DataException{
		DataContainer resultValue = new DataContainer();
		DataPair result = addData(name,resultValue);
		resultValue.setHost(result);
		return result;
	}

	/**
	 * Removes data from this DataContainer if it was present therein.
	 * @param data The DataPair object to be removed from this DataContainer.
	 * If the DataPair object is not present in this DataContainer, nothing will happen.
	 */
	public void removeData(DataPair data){
		dataSet.remove(data);
	}

	/**
	 * Removes data from this DataContainer object that is specified by the given name.
	 * @param name a String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters.
	 * If no DataPair object with the given name is present in this DataContainer, nothing will happen.
	 */
	public void removeData(String name){
		try{
			DataPair d = getDataAbsolute(name);
			removeData(d);
		} catch(DataException e){
			//No Variable with the name exists, so none have to be removed!
		}
	}

	/**
	 * Determines whether this is the topmost level DataContainer
	 * @return TRUE if this DataContainer is not contained in any other DataContainer
	 * FALSE otherwise
	 */
	public boolean isTopLevel(){
		return host == null;
	}

	/**
	 * Returns the top level DataContainer in which this DataContainer is contained.
	 * @return A top level DataContainer object that lies above this DataContainer object.
	 */
	public DataContainer getTopLevel() {
		return isTopLevel() ? this : getLevelAbove().getTopLevel();
	}

	/**
	 * Gets the level above this DataContainer.
	 * @return The DataContainer that contains this DataContainer or this DataContainer if it is the top level.
	 */
	public DataContainer getLevelAbove() {
		return isTopLevel() ? this : host.getHost();
	}

	@Override
	public Value copy() {
		try {
			DataContainer result = new DataContainer();
			for(DataPair d : dataSet){
				result.addData(d.getName(),d.getValue().copy());
			}
			return result;
		} catch (DataException e) {
			throw new Error(String.format("Impossible Exception: %s", e.getMessage()),e);
		}
	}

	@Override
	public Iterator<DataPair> iterator() {
		return dataSet.iterator();
	}

	public String getTypeName() {
		if(containsName(TYPE_NAME)){
			try{
				return getData(TYPE_NAME).getValue().toString();
			} catch(DataException e){
				Tracker.mainInstance.screen.getOutput().printf("Error: %s\n", e.getMessage());
			}
		}
		return DATA_TYPE_NAMES[0];
	}

	public String[] getAlternativeTypeNames() {
		return DATA_TYPE_NAMES;
	}

	/**
	 * Only works if a DataPair with name ADDITION_NAME exists in this DataContainer object and the given object is a DataContainer.
	 * In that case this method will return the evaluation of the variable with name ADDITION_NAME,
	 * which is evaluated within the current environment with this and the given DataContainer as its first and second argument respectively.
	 * Any EvaluationException will be thrown again as a ComputationException.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue add(PrimitiveValue other) throws ComputationException {
		if(other instanceof DataContainer && containsName(ADDITION_NAME)) return performOperation(ADDITION_NAME,(DataContainer) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not add %s to %s.",getTypeName(),other.getTypeName()));
	}

	private PrimitiveValue performOperation(String additionName, DataContainer other) throws ComputationException{
		try{
			Value[] args = new Value[2];
			args[0] = this;
			args[1] = other;
			return getDataAbsolute(additionName).getValue().evaluate(Tracker.mainInstance.currentContainer, args, Tracker.mainInstance.screen.getOutput());
		} catch(DataException|EvaluationException e){
			throw new ComputationException(String.format("Can not perform operation, since: %s.",e.getMessage()),e);
		}
	}

	/**
	 * Only works if a DataPair with name SUBTRACTION_NAME exists in this DataContainer object and the given object is a DataContainer.
	 * In that case this method will return the evaluation of the variable with name SUBTRACTION_NAME,
	 * which is evaluated within the current environment with this and the given DataContainer as its first and second argument respectively.
	 * Any EvaluationException will be thrown again as a ComputationException.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue subtract(PrimitiveValue other) throws ComputationException {
		if(other instanceof DataContainer && containsName(SUBTRACTION_NAME)) return performOperation(SUBTRACTION_NAME,(DataContainer) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not subtract %s from %s.",other.getTypeName(),getTypeName()));
	}

	/**
	 * Only works if a DataPair with name PRODUCT_NAME exists in this DataContainer object and the given object is a DataContainer.
	 * In that case this method will return the evaluation of the variable with name PRODUCT_NAME,
	 * which is evaluated within the current environment with this and the given DataContainer as its first and second argument respectively.
	 * Any EvaluationException will be thrown again as a ComputationException.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue multiply(PrimitiveValue other) throws ComputationException {
		if(other instanceof DataContainer && containsName(PRODUCT_NAME)) return performOperation(PRODUCT_NAME,(DataContainer) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not multiply %s with %s.",getTypeName(),other.getTypeName()));
	}

	/**
	 * Only works if a DataPair with name DIVISION_NAME exists in this DataContainer object and the given object is a DataContainer.
	 * In that case this method will return the evaluation of the variable with name DIVISION_NAME,
	 * which is evaluated within the current environment with this and the given DataContainer as its first and second argument respectively.
	 * Any EvaluationException will be thrown again as a ComputationException.
	 * Returns itself if other is an instance of VoidValue.
	 * Throws a ComputationException in all other cases.
	 */
	@Override
	public PrimitiveValue divideBy(PrimitiveValue other) throws ComputationException {
		if(other instanceof DataContainer && containsName(DIVISION_NAME)) return performOperation(DIVISION_NAME,(DataContainer) other);
		if(other instanceof VoidValue) return this;
		throw new ComputationException(String.format("Can not divide %s by %s.",getTypeName(),other.getTypeName()));
	}

	/**
	 * Returns this object, unless it contains a DataPair with name "value", in which case it returns the evaluation of the Value object of that DataPair.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		try{
			return containsName(VALUE_NAME) ? getData(VALUE_NAME).getValue().evaluate(this,args, output) : this;
		} catch(DataException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s", e.getMessage()),e);
		}
	}

	public void setHost(DataPair host) {
		this.host = host;
	}

	/**
	 * Gets the absolute path of this DataContainer
	 * @return The path of this DataContainer, i.e. a String starting with : and followed by the names of all levels underneath it, seperated by commas.
	 * This list of names includes the name of the DataPair this DataContainer is part of, but only if it is not the top level.
	 */
	public Path getPath() {
		return isTopLevel() ? new Path(this) : host.getPath();
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof DataContainer){
			DataContainer other = (DataContainer) o;
			return dataSet.size() - other.dataSet.size();
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	/**
	 * @return The DataPair that contains this DataContainer.
	 * @throws DataException If this DataContainer is not contained in a DataPair.
	 */
	public DataPair getHost() throws DataException {
		if(isTopLevel()) throw new DataException("A top level DataContainer is not part of a DataPair.");
		return host;
	}

	@Override
	public Value getReferencedValue(DataContainer environment) throws DataException {
		return this;
	}

	@Override
	public Path getReferencedPath(DataContainer environment) throws DataException {
		return getPath();
	}

	@Override
	public DataContainer getLevelAboveReferencedValue(DataContainer environment) {
		return getLevelAbove();
	}

	@Override
	public DataPair getReferencedDataPair(DataContainer environment) throws DataException {
		return getHost();
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		try {
			DataContainer result = new DataContainer();
			for(DataPair data : this){
				result.addData(data.getName(), data.getValue().replaceArgumentsBy(args));
			}
			return result;
		} catch (DataException e) {
			throw new Error(String.format("Impossible Exception: %s", e.getMessage()),e);
		}
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		try {
			DataContainer result = new DataContainer();
			for(DataPair data : this){
				result.addData(data.getName(), data.getValue().getPreEvaluation(environment, args, output));
			}
			return result;
		} catch (DataException e) {
			throw new Error(String.format("Impossible Exception: %s", e.getMessage()),e);
		}
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof DataContainer && dataSet.equals(((DataContainer) other).dataSet);
	}

	@Override
	public boolean getBool() {
		return true;
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return this;
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		if(containsName(ADDITION_NAME)) return this;
		if(containsName(VALUE_NAME)){
			try{
				return getData(VALUE_NAME).getValue().evaluateToFirstAddable(this,args, output);
			} catch(DataException e){
				throw new EvaluationException(String.format("Can not evaluate, since: %s", e.getMessage()),e);
			}
		}
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of an addition.", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		if(containsName(SUBTRACTION_NAME)) return this;
		if(containsName(VALUE_NAME)){
			try{
				return getData(VALUE_NAME).getValue().evaluateToFirstSubtractible(this,args, output);
			} catch(DataException e){
				throw new EvaluationException(String.format("Can not evaluate, since: %s", e.getMessage()),e);
			}
		}
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a subtraction.", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		if(containsName(PRODUCT_NAME)) return this;
		if(containsName(VALUE_NAME)){
			try{
				return getData(VALUE_NAME).getValue().evaluateToFirstMultiplicable(this,args, output);
			} catch(DataException e){
				throw new EvaluationException(String.format("Can not evaluate, since: %s", e.getMessage()),e);
			}
		}
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a product.", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		if(containsName(DIVISION_NAME)) return this;
		if(containsName(VALUE_NAME)){
			try{
				return getData(VALUE_NAME).getValue().evaluateToFirstDivisible(this,args, output);
			} catch(DataException e){
				throw new EvaluationException(String.format("Can not evaluate, since: %s", e.getMessage()),e);
			}
		}
		throw new EvaluationException(String.format("A value of type \'%s\' can not be part of a division.", getTypeName()));
	}
}
