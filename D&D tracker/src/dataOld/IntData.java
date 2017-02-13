package data;

/**
 * A Data class that represents data containing a single integer
 */
public class IntData extends Data {
	public static final String[] DATA_TYPE_NAMES = {"integer","int"};
	
	private int number;
	
	/**
	 * A constructor that initializes this IntData object
	 * @param name The name that this Data object should have.
	 * This should be a String consisting only of alphanumerical characters starting with a non-digit.
	 * @param host The DataContainer in which this Data object is contained
	 * @param number The integer data that this IntData object should contain.
	 */
	public IntData(String name, DataContainer host, int number){
		super(name,host);
		this.number = number;
	}

	@Override
	public String toString() {
		return String.format("%d",number);
	}

	@Override
	public boolean equals(Data other) {
		return other.getName().equals(getName()) && other instanceof IntData && number == ((IntData) other).number;
	}
	
	/**
	 * Gives the integer stored in this IntData object.
	 * @return The integer stored in this IntData object.
	 */
	public int getInteger(){
		return number;
	}

	@Override
	public Data copy(DataContainer host) {
		return new IntData(getName(),host,number);
	}

	public String getTypeName() {
		return DATA_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return DATA_TYPE_NAMES;
	}
}
