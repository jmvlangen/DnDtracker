package data;

public class ComputationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8809643385399070101L;

	public ComputationException(String message){
		super(message);
	}
	
	public ComputationException(String message, Throwable cause){
		super(message,cause);
	}
}
