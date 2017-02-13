package data;

public class CalculationData extends Data {
	public static final String[] DATA_TYPE_NAMES = {"function","fun","f"};
	
	private String calculation;

	/**
	 * A constructor for this CalculationData object
	 * @param name The name that this CalculationData object must have.
	 * This non-empty String must contain only alphanumerical characters and must start with a non-digit.
	 * @param host The DataContainer that contains this Data object.
	 * @param calculation A String that contains the calculation that should be done to compute this Data.
	 * This calculation should consist of only input that can be interpreted on the standard input of the program.
	 * All variables within this calculation will be relative to the DataContainer host.
	 */
	public CalculationData(String name, DataContainer host, String calculation){
		super(name,host);
		this.calculation = calculation;
	}

	@Override
	public String toString() {
		return calculation;
	}

	@Override
	public boolean equals(Data other) {
		return getName().equals(other.getName()) && other instanceof CalculationData && calculation.equals(((CalculationData) other).calculation);
	}
	
	public String getCalculation(){
		return calculation;
	}

	@Override
	public Data copy(DataContainer host) {
		return new CalculationData(getName(),host,calculation);
	}

	public String getTypeName() {
		return DATA_TYPE_NAMES[0];
	}

	public String[] getAlternativeTypeNames() {
		return DATA_TYPE_NAMES;
	}

}
