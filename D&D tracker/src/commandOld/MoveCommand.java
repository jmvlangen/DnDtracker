package command;

import java.io.PrintStream;
import java.util.Scanner;

import data.Data;
import data.DataContainer;
import main.Calculator;
import main.Tracker;

public class MoveCommand implements Command {
	public static String COMMAND_WORD = "move";
	public static int NUMBER_OF_ARGUMENTS = 2;
	public static String USAGE_DESCRIPTION = "move <variable> <destination>";

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
		Data variable = null;
		try{
			variable = readVariableToMove(args[0],program.currentContainer);
			variable.getHost().removeData(variable);
			DataContainer environment = readEnvironment(args[1],program.currentContainer);
			environment.addData(variable);
			variable.changeHost(environment);
			output.printf("Variable %s moved to %s.", variable.getName(),environment.getPath());
		} catch(Exception e){
			if(variable != null) variable.getHost().addData(variable);
			output.printf("Error: %s\n", e.getMessage());
		}
	}
	
	private DataContainer readEnvironment(String s, DataContainer environment) throws Exception{
		Data d = readVariable(s,environment);
		if(!(d instanceof DataContainer)) throw new Exception(String.format("Variable %s contains no subvariables.",d.getName()));
		return (DataContainer) d;
	}
	
	private Data readVariableToMove(String s, DataContainer environment) throws Exception{
		Data d = readVariable(s,environment);
		if(d instanceof DataContainer && ((DataContainer) d).isTopLevel()) throw new Exception(String.format("The variable %s can not be moved.",d.getName()));
		return d;
	}

	private Data readVariable(String s, DataContainer environment) throws Exception {
		Scanner scanner = new Scanner(s);
		Data d = Calculator.readVariable(scanner, environment);
		scanner.close();
		if(d == null) throw new Exception(String.format("The variable %s did not exist.",s));
		return d;
	}
}
