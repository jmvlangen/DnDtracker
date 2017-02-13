package command;

import java.io.PrintStream;
import java.util.Scanner;

import data.Data;
import data.DataContainer;
import main.Calculator;
import main.Tracker;

public class RemoveCommand implements Command {
	public static String COMMAND_WORD = "remove";
	public static int NUMBER_OF_ARGUMENTS = 1;
	public static String USAGE_DESCRIPTION = "remove <variable>";

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
			if(variable == null) throw new Exception(String.format("The variable %s did not exist.",args[0]));
			if(variable instanceof DataContainer && ((DataContainer) variable).isTopLevel()) throw new Exception(String.format("The variable %s can not be removed.",variable.getName()));
			String path = variable.getPath();
			variable.getHost().removeData(variable);
			output.printf("Variable %s removed.", path);
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
