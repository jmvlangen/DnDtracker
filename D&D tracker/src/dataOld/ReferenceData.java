package data;

import java.util.Scanner;

import main.Calculator;

/**
 * A Data object that refers to another Data object.
 */
public class ReferenceData extends Data {
	public static final String[] DATA_TYPE_NAMES = {"reference","ref"};
	
	private String reference;
	
	public ReferenceData(String name, DataContainer host, String reference){
		super(name,host);
		this.reference = reference;
	}

	@Override
	public String toString() {
		return reference;
	}

	@Override
	public boolean equals(Data other) {
		return other.getName().equals(getName()) && other instanceof ReferenceData && ((ReferenceData) other).reference.equals(reference);
	}
	
	public Data getReferencedData(DataContainer environment) throws Exception{
		Scanner scanner = new Scanner(reference);
		Data d = Calculator.readVariable(scanner, environment);
		scanner.close();
		return d;
	}
	
	public Data getReferencedData() throws Exception{
		return getReferencedData(getHost());
	}
	
	public static boolean isValidReference(String s){
		Scanner scanner = new Scanner(s);
		scanner.useDelimiter("");
		boolean flag = isValidReference(scanner);
		scanner.close();
		return flag;
	}
	
	private static boolean isValidReference(Scanner input){
		if(input.hasNext("\\:")) input.next();
		if(input.hasNext("[a-zA-Z]")) return isValidReferenceStartingWithName(input);
		if(input.hasNext("\\.")) return isValidReferenceStartingWithDot(input);
		return false;
	}
	
	private static boolean isValidReferenceStartingWithName(Scanner input){
		if(!input.hasNext("[a-zA-Z]")) return false;
		while(input.hasNext("[a-zA-Z0-9]")) input.next();
		return input.hasNext() ? isValidReferenceStartingWithDot(input) : true;
	}
	
	private static boolean isValidReferenceStartingWithDot(Scanner input){
		if(!input.hasNext("\\.")) return false;
		while(input.hasNext("\\.")) input.next();
		return input.hasNext() ? isValidReferenceStartingWithName(input) : true;
	}

	@Override
	public Data copy(DataContainer host) {
		return new ReferenceData(getName(),host,reference);
	}

	public Object getReferenceString() {
		return reference;
	}

	public String getTypeName() {
		return DATA_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return DATA_TYPE_NAMES;
	}
}
