package data;

//An interface that represents any type of data

public abstract class Data {
	private String name;
	private DataContainer host;
	
	protected Data(String name, DataContainer host){
		this.name = name;
		this.host = host;
	}
	
	/**
	 * Should return the name that can be used to refer to this type of Data.
	 * This method should return the same value for every Data object of the same class.
	 * @return a String consisting of only lowercase letters that can be used to identify Data objects of the same class.
	 */
	public abstract String getTypeName();
	
	/**
	 * Should return a list of names that can be used to refer to Data of this type.
	 * This method should return the same value for every Data object of the same class.
	 * @return An array of length at least 1, consisting of strings of only lowercase letters.
	 */
	public abstract String[] getAlternativeTypeNames();
	
	/**
	 * Returns the name of this data.
	 * @return A String containing only alphanumerical characters and starting with a non-digit.
	 * This String must be the name this piece of data is known by.
	 */
	public String getName(){
		return name;
	};
	
	/**
	 * Gives a printable representation of the value included in this object.
	 * This representation should only include the data and not the name of the Data object.
	 * @return A formatted String that accurately describes the content of this Data object.
	 * The String may contain multiple lines, but may not end in a newline character.
	 */
	public abstract String toString();
	
	/**
	 * Determines whether this data object
	 * @param other
	 * @return
	 */
	public abstract boolean equals(Data other);
	
	/**
	 * Returns the DataContainer that contains this Data Object
	 * @return The DataContainer that contains this Data Object.
	 * If this Data object is the top level DataContainer, then this must refer to itself.
	 */
	public DataContainer getHost(){
		return host;
	};
	
	/**
	 * Changes the DataContainer that currently contains this Data object.
	 * Note that this method does NOT remove this Data object from its previous host
	 * and does NOT add it to its new host.
	 * @param otherHost The DataContainer object that now contains this Data object.
	 */
	public void changeHost(DataContainer otherHost){
		if(otherHost != null) host = otherHost;
	}
	
	/**
	 * Returns the complete path of this variable.
	 * @return host.getPath + "." + getName() or getName() if this is a top level DataContainer object.
	 */
	public String getPath(){
		return host.getPath() + "." + getName();
	}
	
	/**
	 * Creates a deep copy of this Data object within the given DataContainer.
	 * @param host The DataContainer in which this new copy will appear.
	 * @return A deep copy of this Data object.
	 * It should be noted that this method does not add the returned copy to the given DataContainer host itself!
	 */
	public abstract Data copy(DataContainer host);

	/**
	 * Checks whether a given String is a valid name for a Data object.
	 * @param s A name
	 * @return
	 */
	public static boolean isValidName(String s){
		if(s.length() <= 0) return false;
		if(!isLetter(s.charAt(0))) return false;
		for(int i = 1 ; i < s.length() ; i++){
			if(!isAlphaNumeric(s.charAt(i))) return false;
		}
		return true;
	}
	
	private static boolean isLetter(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}
	
	private static boolean isDigit(char c){
		return '0' <= c && c <= '9';
	}
	
	private static boolean isAlphaNumeric(char c){
		return isLetter(c) || isDigit(c);
	}
}
