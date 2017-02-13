package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.DataException;
import data.DataPair;
import data.EvaluationException;
import data.PrimitiveValue;
import data.ReferenceValue;
import data.SubVariableValue;
import data.Value;
import data.VoidValue;

public class ListCommand extends CommandValue{
	public static String COMMAND_WORD = "list";
	public static String USAGE_DESCRIPTION = "list [collection]";
	public static String TAB = "   ";

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length >= 1) environment = getDataContainer(args[0],environment);
		printDataContainer(environment,0,output);
		return new VoidValue();
	}

	private DataContainer getDataContainer(Value value, DataContainer environment) throws EvaluationException{
		if(value instanceof DataContainer) return (DataContainer) value;
		if(value instanceof ReferenceValue) return getDataContainerFromReference((ReferenceValue) value, environment);
		if(value instanceof SubVariableValue) return getDataContainerFromSubVariableValue((SubVariableValue) value, environment);
		else throw new EvaluationException(String.format("Variables of type %s have no subvariables.", value.getTypeName()));
	}

	private DataContainer getDataContainerFromSubVariableValue(SubVariableValue value, DataContainer environment) throws EvaluationException {
		return getDataContainer(value.getSubValue(),value.getLocalEnvironment(environment));
	}

	private DataContainer getDataContainerFromReference(ReferenceValue reference, DataContainer environment) throws EvaluationException{
		try{
			Value value = reference.getReferencedValue(environment);
			return getDataContainer(value,reference.getLevelAboveReferencedValue(environment));
		} catch(DataException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s", e.getMessage()),e);
		}
	}

	private void printDataContainer(DataContainer environment, int level, PrintStream output) {
		for(DataPair d : environment){
			if(d.getName().charAt(0) != '_'){
				printTabs(level, output);
				printDataPair(d, level, output);
			}
		}
	}

	private void printDataPair(DataPair d, int level, PrintStream output) {
		output.printf("%s: ", d.getName());
		printValue(d.getValue(), level, output);
	}

	private void printValue(Value value, int level, PrintStream output) {
		if(value instanceof DataContainer){
			output.printf("\n");
			printDataContainer((DataContainer) value, level+1, output);
		}
		else{
			output.print(value.toString());
			output.print("\n");
		}
	}

	private void printTabs(int number, PrintStream output){
		for(int i = 0; i < number; i++){
			output.print(TAB);
		}
	}

	@Override
	public Value copy() {
		return new ListCommand();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
