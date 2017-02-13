package data;

/**
 * A ReferenceValue object is a Value object that refers to another Value object within a given DataContainer.
 * Examples include NamedValue and GlobalValue objects.
 */
public interface ReferenceValue extends Value {
	
	/**
	 * Returns the Value that this ReferenceValue object is a reference to.
	 * @param environment The DataContainer object in which the referenced object should be found
	 * @return A Value object referenced by this ReferenceValue object.
	 * Note that evaluate() on this object should give the same result as evaluate() on the returned Value object.
	 * @throws DataException If the referenced Value object can not be found in the given DataContainer.
	 */
	public Value getReferencedValue(DataContainer environment) throws DataException;
	
	/**
	 * Returns the path of the Value object that this ReferenceValue object is a reference to.
	 * @param environment The DataContainer object in which the referenced object should be found
	 * @return A String that gives the path of the referenced Value object.
	 * See also the methods getPath() of the classes DataPair and DataContainer
	 * @throws DataException If the referenced Value object can not be found in the given DataContainer.
	 */
	public Path getReferencedPath(DataContainer environment) throws DataException;
	
	/**
	 * Gives the DataContainer that contains the Value object referenced by this ReferenceValue object
	 * @param environment The DataContainer object in which the referenced Value object should be found.
	 * @return A DataContainer object that contains the Value object referenced by this ReferenceValue object or the referenced Value object if it is a top-level DataContainer.
	 */
	public DataContainer getLevelAboveReferencedValue(DataContainer environment);
	
	/**
	 * Gives the DataPair that contains the referenced Value object.
	 * @param environment The DataContainer in which the referenced Value should be found.
	 * @return A DataPair object that contains the Value object referred to by this reference Value object.
	 * @throws DataException If the referenced Value object is not contained in any DataContainer or does not exist.
	 */
	public DataPair getReferencedDataPair(DataContainer environment) throws DataException;
}
