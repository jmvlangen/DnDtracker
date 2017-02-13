package command;

import java.io.PrintStream;
import java.util.Scanner;

import data.Data;
import data.DataContainer;
import main.Calculator;
import main.Tracker;

/**
 * A command that copies a variable to a new location.
 */
public class CopyCommand implements Command {
	public static String COMMAND_WORD = "copy";
	public static int NUMBER_OF_ARGUMENTS = 2;
	public static String USAGE_DESCRIPTION = "copy <variable> <destination>";

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public int getNumberOfArguments() {
		return NUMBER_OF_ARGUMENTS;
	}

	@Override
	public String usageDescription() {
		return USAGE_DESCRIPTION;
	}

	@Override
	public void performCommand(String[] args, Tracker program, PrintStream output) {
		try{
			Data variable = readVariable(args[0],program.currentContainer);
			Data var2 = readVariable(args[1],program.currentContainer);
			if(!(var2 instanceof DataContainer)) throw new Exception(String.format("Variable %s can not contain subvariables.",var2.getPath()));
			DataContainer environment = (DataContainer) var2;
			if(environment.containsName(variable.getName())) throw new Exception(String.format("Variable %s already contains a variable %s.",environment.getPath(),variable.getName()));
			environment.addData(variable.copy(environment));
			output.printf("Variable %s copied to %s.\n",variable.getName(),environment.getPath());
		} catch(Exception e){
			output.printf("Error: %s\n", e.getMessage());
		}
	}

	private Data readVariable(String s, DataContainer environment) throws Exception {
		Scanner scanner = new Scanner(s);
		Data d = Calculator.readVariable(scanner, environment);
		scanner.close();
		return d;
	}
}
