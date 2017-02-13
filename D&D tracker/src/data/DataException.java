package data;

public class DataException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8809643385399070101L;

	public DataException(String message){
		super(message);
	}
	
	public DataException(String message, Throwable cause){
		super(message,cause);
	}
}
