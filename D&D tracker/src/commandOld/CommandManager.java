package command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import main.Tracker;

public class CommandManager {
	private Collection<Command> commands;
	private Collection<Command> standardCommands;
	
	/**
	 * Loads all the commands which should be always present.
	 */
	private void addStandardCommands(){
		standardCommands.add(new CreateCommand());
		standardCommands.add(new CopyCommand());
		standardCommands.add(new MoveCommand());
		standardCommands.add(new RemoveCommand());
		standardCommands.add(new SetCommand());
		standardCommands.add(new SaveCommand());
		standardCommands.add(new LoadCommand());
		standardCommands.add(new ListCommand());
	}
	
	public CommandManager(){
		commands = new ArrayList<Command>();
		standardCommands = new ArrayList<Command>();
		addStandardCommands();
	}
	
	/**
	 * Determines whether the given String is the command word of one of the registered commands.
	 * @param word A String
	 * @return TRUE if a command exists with command word equal to word in lower case.
	 * FALSE otherwise
	 */
	public boolean isCommand(String word){
		return getCommand(word) != null;
	}
	
	/**
	 * Adds a specified commmand to this CommandManager if none by the same name are already in this CommandManager.
	 * @param command The command to be added
	 * @return TRUE if the command was added, FALSE otherwise
	 */
	public boolean addCommand(Command command){
		if(isCommand(command.getCommandWord())) return false;
		commands.add(command);
		return true;
	}
	/**
	 * Executes the command mentioned at the beginning of the scanner with all that follows on that line of the scanner as its arguments.
	 * @param scanner A Scanner of a String that contains the command followed by its arguments.
	 * @param program A reference to the main program.
	 * @param output A PrintStream object that can be used to print output.
	 */
	public void performCommand(Scanner scanner,Tracker program,PrintStream output){
		if(!scanner.hasNext()) output.print("Couldn't read any command.\n");
		Command command = getCommand(scanner.next());
		if(command == null) output.print("The line didn't start with a command.\n");
		String[] args = new String[command.getNumberOfArguments() + 1];
		for(int i = 0 ; i < command.getNumberOfArguments() ; i++){
			if(!scanner.hasNext()) output.printf("%s\n", command.usageDescription());
			args[i] = scanner.next();
		}
		if(scanner.hasNext()) args[command.getNumberOfArguments()] = scanner.nextLine().trim();
		else args[command.getNumberOfArguments()] = "";
		command.performCommand(args, program, output);
	}
	
	/**
	 * Removes a command from this commandManager. This method will not remove standard commands
	 * @param word The command word of the command to be removed. It will be converted into lower-case letters if it is not already.
	 */
	public void removeCommand(String word){
		for(Command c : commands){
			if(c.getCommandWord().equals(word.toLowerCase())){
				commands.remove(c);
				return;
			}
		}
	}
	
	private Command getCommand(String word){
		for(Command c : commands){
			if(c.getCommandWord().equals(word)) return c;
		}
		for(Command c : standardCommands){
			if(c.getCommandWord().equals(word)) return c;
		}
		return null;
	}
}
