package data;

import java.io.PrintStream;

/**
 * A Value object represents values that can be stored, saved, printed and manipulated with basic operations.
 */
public interface Value extends Comparable<Value> {
	/**
	 * Gives a String that accurately describes the contents of this Value object.
	 * @return A String that accurately describes the contents of this Value object.
	 */
	public String toString();
	
	/**
	 * Gives a name that can be used to refer to Value objects of this type.
	 * @return A String consisting of only lower case letters that can be used to refer to Value objects of that type.
	 */
	public String getTypeName();
	
	/**
	 * Makes an exact copy of this Value object.
	 * @return An exact deep copy of this Value object.
	 */
	public Value copy();
	
	/**
	 * Checks whether this and another object are in fact the same.
	 * @param other The object to be compared with.
	 * @return TRUE if this Value object and the given object are identical, FALSE otherwise.
	 * Note that it should always return FALSE if the given object is not a Value object.
	 */
	public boolean equals(Object other);
	
	/**
	 * Gives a list of alternative names that can be used to refer to Value objects of this type.
	 * @return An array of String objects consisting only of lower case letters that can be used to refer to Value objects of this type.
	 */
	public String[] getAlternativeTypeNames();
	
	/**
	 * Evaluates this Value object. The way this is done may depend upon the implementation of this Value object.
	 * Every implementation of a Value object must therefore specifically mention what this method does.
	 * @param environment The DataContainer in which this Value object must be evaluated.
	 * @param args Arguments that are given for evaluating this Value object.
	 * @param output A PrintStream object that can be used to print information to the user about the evaluation.
	 * @return A PrimitiveValue object that is an evaluation of this Value object.
	 * @throws EvaluationException if the evaluation did not succeed.
	 * Every implementation of a Value object should specify for what reasons this may occur.
	 */
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException;
	
	/**
	 * Evaluates this Value object, but only until the first DataContainer object is reached.
	 * This evaluation should function in the same way as evaluate() for all other purposes.
	 * @param environment The DataContainer in which this Value object must be evaluated.
	 * @param args Arguments that are given for evaluating this Value object.
	 * @param output A PrintStream object that can be used to print information to the user about the evaluation.
	 * @return A PrimitiveValue object that is an evaluation of this Value object.
	 * @throws EvaluationException if the evaluation did not succeed.
	 * This can occur for any reason evaluate() would give an exception,
	 * but must also be thrown if no DataContainer was encountered in the evaluation.
	 */
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException;
	
	/**
	 * Evaluates this Value object, but only until the first Value object that can be part of an addition.
	 * This evaluation should function in the same way as evaluate() for all other purposes.
	 * @param environment The DataContainer in which this Value object must be evaluated.
	 * @param args Arguments that are given for evaluating this Value object.
	 * @param output A PrintStream object that can be used to print information to the user about the evaluation.
	 * @return A PrimitiveValue object that is an evaluation of this Value object.
	 * @throws EvaluationException if the evaluation did not succeed.
	 * This can occur for any reason evaluate() would give an exception,
	 * but must also be thrown if no Value object is encountered that can be part of an addition.
	 */
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException;
	
	/**
	 * Evaluates this Value object, but only until the first Value object that can be part of a subtraction.
	 * This evaluation should function in the same way as evaluate() for all other purposes.
	 * @param environment The DataContainer in which this Value object must be evaluated.
	 * @param args Arguments that are given for evaluating this Value object.
	 * @param output A PrintStream object that can be used to print information to the user about the evaluation.
	 * @return A PrimitiveValue object that is an evaluation of this Value object.
	 * @throws EvaluationException if the evaluation did not succeed.
	 * This can occur for any reason evaluate() would give an exception,
	 * but must also be thrown if no Value object is encountered that can be part of a subtraction.
	 */
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException;
	
	/**
	 * Evaluates this Value object, but only until the first Value object that can be part of a multiplication.
	 * This evaluation should function in the same way as evaluate() for all other purposes.
	 * @param environment The DataContainer in which this Value object must be evaluated.
	 * @param args Arguments that are given for evaluating this Value object.
	 * @param output A PrintStream object that can be used to print information to the user about the evaluation.
	 * @return A PrimitiveValue object that is an evaluation of this Value object.
	 * @throws EvaluationException if the evaluation did not succeed.
	 * This can occur for any reason evaluate() would give an exception,
	 * but must also be thrown if no Value object is encountered that can be part of a multiplication.
	 */
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException;
	
	/**
	 * Evaluates this Value object, but only until the first Value object that can be part of a division.
	 * This evaluation should function in the same way as evaluate() for all other purposes.
	 * @param environment The DataContainer in which this Value object must be evaluated.
	 * @param args Arguments that are given for evaluating this Value object.
	 * @param output A PrintStream object that can be used to print information to the user about the evaluation.
	 * @return A PrimitiveValue object that is an evaluation of this Value object.
	 * @throws EvaluationException if the evaluation did not succeed.
	 * This can occur for any reason evaluate() would give an exception,
	 * but must also be thrown if no Value object is encountered that can be part of a division.
	 */
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException;
	
	/**
	 * Gives a Value object in which all ArgumentValue objects in this Value object are replaced with the respective argument supplied in the array.
	 * To make this method effective it should be passed on to all Value objects contained in this Value object.
	 * @param args An array of Value objects.
	 * @return A copy of this Value object in which all ArgumentValue objects are replaced with the respective argument supplied in the given array.
	 */
	public Value replaceArgumentsBy(Value[] args);
	
	public boolean equals(Value other);
}
