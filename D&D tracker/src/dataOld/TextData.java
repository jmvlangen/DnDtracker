package data;

/**
 * A Data class that stores some text. Usually a single line or paragraph.
 */
public class TextData extends Data{
	public static final String[] DATA_TYPE_NAMES = {"text","string","t"};
	
	private String text;
	
	/**
	 * A constructor that initializes this TextData object
	 * @param name The name that this Data object should have.
	 * This should be a String consisting only of alphanumerical characters starting with a non-digit.
	 * @param host The DataContainer in which this Data object is contained
	 * @param text The String that this TextData object should contain.
	 */
	public TextData(String name, DataContainer host, String text){
		super(name,host);
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public boolean equals(Data other) {
		return getName().equals(other.getName()) && getHost().equals(other.getHost()) && (other instanceof TextData) && ((TextData) other).getText().equals(getText());
	}

	@Override
	public Data copy(DataContainer host) {
		return new TextData(getName(),host,text);
	}

	public String getTypeName() {
		return DATA_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return DATA_TYPE_NAMES;
	}
}
