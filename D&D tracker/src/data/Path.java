package data;

import data.command.Command;

/**
 * A class that represents an explicit path to a variable.
 */
public class Path {
	private DataContainer environment;
	private String[] names;

	/**
	 * Creates an empty path.
	 */
	public Path(DataContainer environment){
		names = new String[0];
		this.environment = environment;
	}

	/**
	 * Creates a path from a String of names.
	 * @param names An array of names.
	 * A name should be a String starting with a letter or an underscore and furthermore only consisting of alpha-numerical characters.
	 */
	public Path(String[] names,DataContainer environment){
		this.names = names;
		this.environment = environment;
	}

	/**
	 * Creates a path that extends another path.
	 * @param path A Path object.
	 * @param names The names that should be added to the aforementioned Path object.
	 * A name should be a String starting with a letter or an underscore and furthermore only consisting of alpha-numerical characters.
	 */
	public Path(Path path, String[] names){
		this.names = new String[path.depth() + names.length];
		for(int i = 0; i < path.depth(); i++) this.names[i] = path.names[i];
		for(int i = 0; i < names.length; i++) this.names[path.depth()+i] = names[i];
		this.environment = path.environment;
	}

	/**
	 * Creates a path that extends another path by a single name.
	 * @param path A Path object.
	 * @param name The name to be added to the aforementioned Path object.
	 * A name should be a String starting with a letter or an underscore and furthermore only consisting of alpha-numerical characters.
	 */
	public Path(Path path, String name){
		names = new String[path.depth() + 1];
		for(int i = 0; i < path.depth(); i++) names[i] = path.names[i];
		names[path.depth()] = name;
		this.environment = path.environment;
	}

	/**
	 * Attempts to convert a Value object into the path it represents.
	 * @param value A Value object.
	 * @param withinPath The path within which the Value object resides.
	 * @return A Path object representing the location referred to by the Value object.
	 * @throws PathException If the Value object does not refer to a path.
	 */
	public static Path convertToPath(Value value, Path withinPath) throws PathException {
		if(value instanceof GlobalValue) return new Path(withinPath.environment);
		if(value instanceof NamedValue) return new Path(withinPath,((NamedValue) value).toString());
		if(value instanceof CurrentDataContainerValue) return withinPath;
		if(value instanceof VoidValue) return withinPath;
		if(value instanceof DataContainer) return ((DataContainer) value).getPath();
		if(value instanceof SubVariableValue){
			SubVariableValue subValue = (SubVariableValue) value;
			withinPath = convertToPath(subValue.getReference(),withinPath);
			return convertToPath(subValue.getSubValue(),withinPath.subPath(withinPath.depth()-subValue.getLevel()));
		}
		throw new PathException(String.format("Can not make a path from arguments of type \'%s\'", value.getTypeName()));
	}

	public Path subPath(int depth) {
		depth = Math.min(Math.max(0, depth), depth());
		String[] subNames = new String[depth];
		for(int j = 0 ; j < depth; j++) subNames[j] = names[j];
		return new Path(subNames,environment);
	}

	/**
	 * Attempts to create all variables in this path if they do not already exist.
	 * @throws DataException If an unexpected error occurs when creating the variables in this path.
	 */
	public void createPath() throws DataException {
		DataContainer currentEnvironment = this.environment.getTopLevel();
		for(int i = 0; i < depth(); i++){
			DataPair dataPair;
			if(currentEnvironment.containsNameAbsolute(names[i])) dataPair = currentEnvironment.getDataAbsolute(names[i]);
			else dataPair = currentEnvironment.addData(names[i], new VoidValue());
			if(i < depth() - 1){
				Value value = dataPair.getValue();
				if(value instanceof DataContainer) currentEnvironment = (DataContainer) value;
				else{
					currentEnvironment = new DataContainer();
					dataPair.setValue(currentEnvironment);
					currentEnvironment.addData(DataContainer.VALUE_NAME,value);
				}
			}
		}
	}

	/**
	 * Gives the lowest DataPair object in this path.
	 * @return The lowest DataPair in this path.
	 * @throws DataException If no such DataPair exists.
	 */
	public DataPair getLowestDataPair() throws DataException {
		if(depth() == 0) throw new DataException(String.format("The path \'%s\' contains no variables.",toString()));
		DataPair result = environment.getTopLevel().getDataAbsolute(names[0]);
		for(int i = 1; i < depth(); i++){
			Value value = result.getValue();
			if(value instanceof DataContainer) result = ((DataContainer) value).getDataAbsolute(names[i]);
			else throw new DataException(String.format("The variable \'%s\' contains no subvariables.",result.getPath()));
		}
		return result;
	}
	
	/**
	 * Gets the Value at the end of this path.
	 * @return The Value object that is referred to by this path.
	 * @throws DataException If such a Value object does not exist.
	 */
	public Value getLowestValue() throws DataException {
		if(depth() == 0) return environment.getTopLevel();
		return getLowestDataPair().getValue();
	}
	
	/**
	 * Sets the Value object at the end of this path. Creating the path if necessary.
	 * @param value The value that the Value object at the end of this path must get.
	 * @param createPath TRUE if the path should be created if it doesn't already.
	 * @throws DataException If the value at the end of the path could not be set.
	 */
	public void setValue(Value value, boolean createPath) throws DataException {
		if(createPath) createPath();
		Value oldValue = getLowestValue();
		if(oldValue instanceof DataContainer) (new Path(this,DataContainer.VALUE_NAME)).setValue(value,createPath);
		else{
			DataPair dataPair = getLowestDataPair();
			if(dataPair instanceof Command) throw new DataException(String.format("Can not manipulate %s as it is a system command.", dataPair.getName()));
			dataPair.setValue(value);
		}
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(":");
		for(int i = 0;i < depth() ; i++){
			if(i > 0) sb.append(".");
			sb.append(names[i]);
		}
		return sb.toString();
	}

	public int depth(){
		return names.length;
	}

	/**
	 * Gives the i-th name in this path.
	 * @param i A non-negative integer smaller than the depth of this path.
	 * @return The i-th name in this path.
	 * @throws PathException If i is an invalid integer.
	 */
	public String nameAt(int i) throws PathException{
		if(i < 0 || i >= depth()) throw new PathException("Invalid index for path.");
		return names[i];
	}
}
