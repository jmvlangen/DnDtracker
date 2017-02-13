package data;

public class EvaluationException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4080299736784161147L;
	
	public EvaluationException(String message){
		super(message);
	}
	
	public EvaluationException(String message, Throwable cause){
		super(message,cause);
	}
}
