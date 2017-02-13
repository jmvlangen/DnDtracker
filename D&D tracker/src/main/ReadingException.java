package main;

public class ReadingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5549573593020408944L;

	public ReadingException(String message){
		super(message);
	}
	
	public ReadingException(String message, Throwable cause){
		super(message,cause);
	}
}
