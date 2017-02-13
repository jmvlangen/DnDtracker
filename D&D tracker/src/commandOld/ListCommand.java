package command;

import java.io.PrintStream;
import java.util.Scanner;

import data.Data;
import data.DataContainer;
import main.Calculator;
import main.Tracker;

public class ListCommand implements Command{
	public static String COMMAND_WORD = "list";
	public static int NUMBER_OF_ARGUMENTS = 0;
	public static String USAGE_DESCRIPTION = "list" + "list <environment>";
	public static final String TAB = "    ";
	
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
			DataContainer environment = getEnvironment(args[0],program.currentContainer);
			printVariablesInEnvironment(environment, 0, output);
		} catch(Exception e){
			output.printf("Error: %s\n", e.getMessage());
		}
	}
	
	private void printVariablesInEnvironment(DataContainer environment, int level, PrintStream output) {
		printTabs(level, output);
		for(Data d : environment){
			if(d instanceof DataContainer) printEnvironment((DataContainer) d, level, output);
			else{
				output.printf("%s: %s", d.getName(), d.toString());
			}
		}
	}

	private void printEnvironment(DataContainer environment, int level, PrintStream output) {
		output.printf("%s: %s", environment.getName(), environment.getTypeName());
		printVariablesInEnvironment(environment,level+1,output);
	}

	private void printTabs(int number, PrintStream output){
		for(int i = 0; i < number; i++){
			output.print(TAB);
		}
	}

	private DataContainer getEnvironment(String s, DataContainer environment) throws Exception{
		if(s.length() == 0) return environment;
		s = getFirstArgument(s);
		Data d = Calculator.readVariable(s,environment);
		if(d == null) throw new Exception(String.format("No variable %s exists.",s));
		if(!(d instanceof DataContainer)) throw new Exception(String.format("The variable %s does not contain subvariables.",d.getPath()));
		return (DataContainer) d;
	}
	
	private String getFirstArgument(String s){
		Scanner scanner = new Scanner(s);
		String result = scanner.next();
		scanner.close();
		return result;
	}
}
