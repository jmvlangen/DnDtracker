package data;

/**
 * A class that is used to store Value objects with a given name.
 * A name should start with a letter or the character '_' and furthermore only consist of alpha-numerical characters.
 * The character '_' at the start of the name indicates a 'hidden' variable.
 */
public class DataPair {
	private final DataContainer host;
	private final String name;
	private Value value;
	
	/**
	 * Initializes this DataPair object.
	 * @param name A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters that will be the name of this DataPair.
	 * @param value A Value object to be stored in this DataPair, which may not be null.
	 * @param host The DataContainer object that contains this DataPair.
	 */
	public DataPair(String name, Value value, DataContainer host){
		this.name = name;
		this.value = value;
		this.host = host;
		if(value instanceof DataContainer) ((DataContainer) value).setHost(this);
	}
	
	/**
	 * Returns the name that this DataPair has.
	 * @return A String starting with a letter or the character '_' and furthermore only consisting of alpha-numerical characters that is the name of this DataPair.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the path of this DataPair.
	 * @return new Path(getHost().getPath(),getName());
	 */
	public Path getPath(){
		return new Path(getHost().getPath(),getName());
	}
	
	/**
	 * Returns the Value object of this DataPair.
	 * @return The Value object stored in this DataPair.
	 */
	public Value getValue(){
		return value;
	}
	
	/**
	 * Changes the Value stored in this DataPair to Value.
	 * @param value The new Value object to be stored in this DataPair.
	 * This method will do nothing when value equals null.
	 */
	public void setValue(Value value){
		if(value != null){
			this.value = value;
			if(value instanceof DataContainer) ((DataContainer) value).setHost(this);
		}
	}

	public DataContainer getHost() {
		return host;
	}
}
