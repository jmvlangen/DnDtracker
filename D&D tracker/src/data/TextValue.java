package data;

import java.io.PrintStream;

public class TextValue implements PrimitiveValue{
	public static final String[] VALUE_TYPE_NAMES = {"text","txt","string"};
	
	private String text;
	
	/**
	 * Initializes this TextValue object.
	 * @param text The text that this TextValue object should contain.
	 */
	public TextValue(String text){
		this.text = text;
	}

	/**
	 * Returns this if other is an instance of of VoidValue.
	 * Throws a ComputationException in all other cases;
	 */
	@Override
	public PrimitiveValue add(PrimitiveValue other) throws ComputationException {
		if(other instanceof VoidValue) return this;
		if(other instanceof TextValue) return new TextValue(text + ((TextValue) other).getText());
		else throw new ComputationException(String.format("Can not add %s to %s.",getTypeName(),other.getTypeName()));
	}

	/**
	 * Returns this if other is an instance of of VoidValue.
	 * Throws a ComputationException in all other cases;
	 */
	@Override
	public PrimitiveValue subtract(PrimitiveValue other) throws ComputationException {
		if(other instanceof VoidValue) return this;
		else throw new ComputationException(String.format("Can not subtract %s from %s.",other.getTypeName(),getTypeName()));
	}

	/**
	 * Returns this if other is an instance of of VoidValue.
	 * Throws a ComputationException in all other cases;
	 */
	@Override
	public PrimitiveValue multiply(PrimitiveValue other) throws ComputationException {
		if(other instanceof VoidValue) return this;
		else throw new ComputationException(String.format("Can not multiply %s with %s.",getTypeName(),other.getTypeName()));
	}

	/**
	 * Returns this if other is an instance of of VoidValue.
	 * Throws a ComputationException in all other cases;
	 */
	@Override
	public PrimitiveValue divideBy(PrimitiveValue other) throws ComputationException {
		if(other instanceof VoidValue) return this;
		else throw new ComputationException(String.format("Can not divide %s by %s.",getTypeName(),other.getTypeName()));
	}

	@Override
	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}

	@Override
	public Value copy() {
		return new TextValue(text);
	}

	@Override
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	/**
	 * Returns this;
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) {
		return this;
	}
	
	public boolean equals(Object other){
		return other != null && other instanceof TextValue && ((TextValue) other).text.equals(text);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			if(mustEscapeCharacter(c)){
				sb.append("\\");
				if(c == '\n') c = 'n';
			}
			sb.append(c);
		}
		return sb.append("\"").toString();
	}

	private boolean mustEscapeCharacter(char c) {
		switch(c){
		case '\"':
		case '\\':
		case '\n':
			return true;
		default:
			return false;
		}
	}

	/**
	 * In contrast to the method toString() this method actually reproduces the precise String stored in this TextValue object without any additional formatting.
	 * @return The string stored in this TextValue object.
	 */
	public String getText() {
		return text;
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof TextValue){
			TextValue other = (TextValue) o;
			return text.compareTo(other.text);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return copy();
	}

	@Override
	public boolean equals(Value other) {
		return other instanceof TextValue && ((TextValue) other).text.equals(text);
	}

	@Override
	public boolean getBool() {
		return true;
	}
}
