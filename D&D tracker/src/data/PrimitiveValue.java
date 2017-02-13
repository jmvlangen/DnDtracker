package data;

/**
 * A PrimitiveValue object should implement basic operations, or throw an error when they can't be performed.
 */
public interface PrimitiveValue extends Value {
	/**
	 * Attempts to add a given Value object to this Value object.
	 * The implementation of this method may depend on the implementation of the Value object and must therefore be specified for every implementation.
	 * If an implementation does not support the addition of the given Value object to this one, it should throw a ComputationException.
	 * @param other The Value object to be added to this one.
	 * @return The Value object that gives the result of this addition.
	 * @throws ComputationException If the addition fails or is not supported by this Value object.
	 */
	public PrimitiveValue add(PrimitiveValue other) throws ComputationException;
	
	/**
	 * Attempts to subtract a given Value object from this Value object.
	 * The implementation of this method may depend on the implementation of the Value object and must therefore be specified for every implementation.
	 * If an implementation does not support the subtraction of the given Value object to this one, it should throw a ComputationException.
	 * @param other The Value object to be subtracted from this one.
	 * @return The Value object that gives the result of this subtraction.
	 * @throws ComputationException If the subtraction fails or is not supported by this Value object.
	 */
	public PrimitiveValue subtract(PrimitiveValue other) throws ComputationException;
	
	/**
	 * Attempts to multiply this Value object with a given one.
	 * The implementation of this method may depend on the implementation of the Value object and must therefore be specified for every implementation.
	 * If an implementation does not support the multiplication of this Value object with the given one, it should throw a ComputationException.
	 * @param other The Value object to be multiplied with.
	 * @return The Value object that gives the result of this multiplication.
	 * @throws ComputationException If the multiplication fails or is not supported by this Value object.
	 */
	public PrimitiveValue multiply(PrimitiveValue other) throws ComputationException;
	
	/**
	 * Attempts to divide this Value object by a given one.
	 * The implementation of this method may depend on the implementation of the Value object and must therefore be specified for every implementation.
	 * If an implementation does not support the division of this Value object by the given one, it should throw a ComputationException.
	 * @param other The Value object to be divided by.
	 * @return The Value object that gives the result of this division.
	 * @throws ComputationException If the division fails or is not supported by this Value object.
	 */
	public PrimitiveValue divideBy(PrimitiveValue other) throws ComputationException;
}
