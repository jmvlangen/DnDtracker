package data.command;

import data.DataContainer;
import data.DataPair;

/**
 * A special DataPair that stores a Command.
 * It is special in the way that it can't be removed.
 */
public class Command extends DataPair {
	/**
	 * Initializes this Command object.
	 * @param name A String consisting only of alpha-numerical characters starting with a non-digit, that should be the name of this Command.
	 * @param value The CommandValue object that performs the functionality of this command when evaluated.
	 * @param host A DataContainer object that contains this Command.
	 */
	public Command(String name, CommandValue value, DataContainer host){
		super(name,value,host);
	}
	
	/**
	 * Initializes this Command object with the default name of the given CommandValue.
	 * @param value The CommandValue object that performs the functionality of this command when evaluated.
	 * @param host A DataContainer object that contains this Command.
	 */
	public Command(CommandValue value, DataContainer host){
		this(value.getDefaultName(),value,host);
	}
}
