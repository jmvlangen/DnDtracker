package data;

public class PathException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3217973717093711955L;

	public PathException(String message){
		super(message);
	}
	
	public PathException(String message, Throwable cause){
		super(message,cause);
	}
}
