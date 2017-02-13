package command;

import java.io.PrintStream;

import main.Tracker;

/**
 * The basic implementation for any command.
 */
public interface Command {
	/**
	 * Should return the String that contains the name to be typed to perform this command.
	 * @return a String containing only lower-case letters.
	 */
	public String getCommandWord();
	
	/**
	 * Returns the number of arguments that this command accepts.
	 * @return a non-negative integer that describes the number of arguments this command expects.
	 */
	public int getNumberOfArguments();
	
	/**
	 * Returns a String that describes how this command should be used.
	 * This should be formatted as follows
	 * <getCommandWord()> <arg1> ... <arg<getNumberOfArguments>>
	 * @return A String containing multiple lines with no end of line symbol in the final line.
	 * Each line must start with getCommandWord() and describe one of the usages of this command as above.
	 */
	public String usageDescription();
	
	/**
	 * This method is called whenever this command is called by the user,
	 * i.e. the input line starts with getCommandWord()
	 * @param args an array of getNumberOfArguments()+1 strings, such that all except the last string contain no whitespace.
	 * The arguments are chosen such that the input line is:
	 * getCommandWord() args[0] ... args[getNumberOfArguments()].
	 * In other words each args[i] is the i-th whitespace seperated argument after the command word and args[getNumberOfArguments()] is all remaining on the line.
	 * @param program A link to the Tracker object this program currently uses.
	 * @param output The PrintStream object that can be used to print any output.
	 */
	public void performCommand(String[] args,Tracker program,PrintStream output);
}
