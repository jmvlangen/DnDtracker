package command;

import java.io.PrintStream;
import java.util.Scanner;

import data.CalculationData;
import data.Data;
import data.DataContainer;
import data.IntData;
import data.ReferenceData;
import main.Calculator;
import main.Tracker;

/**
 * A command that sets a variable to a specific value
 *
 */
public class SetCommand implements Command{
	public static String COMMAND_WORD = "set";
	public static int NUMBER_OF_ARGUMENTS = 1;
	public static String USAGE_DESCRIPTION = "set <variable> <value>";

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
			Data data = readData(args[0],program.currentContainer);
			if(data instanceof IntData) setIntData(data.getHost(),data.getName(),Calculator.calculate(args[1],program.currentContainer));
			else if(data instanceof CalculationData) setCalculationData(data.getHost(),data.getName(),args[1]);
			else if(data instanceof ReferenceData) setReferenceData(data.getHost(),data.getName(),args[1]);
			else output.printf("Can't set variable %s",data.getName());
		} catch(Exception e){
			program.printError(e, output);
		}
	}

	private void setCalculationData(DataContainer host, String name, String value) {
		host.removeDataByName(name);
		host.addCalculationData(name, value);
	}

	private void setIntData(DataContainer host, String name, int value) {
		host.removeDataByName(name);
		host.addIntData(name,value);
	}

	private void setReferenceData(DataContainer host, String name, String reference) throws Exception{
		if(!ReferenceData.isValidReference(reference)) throw new Exception(String.format("%s is not a valid reference.", reference));
		host.removeDataByName(name);
		host.addReferenceData(name,reference);
	}

	private Data readData(String name,DataContainer environment) throws Exception {
		Scanner scanner = new Scanner(name);
		Data data = Calculator.readVariable(scanner, environment);
		scanner.close();
		return data;
	}
	
}
