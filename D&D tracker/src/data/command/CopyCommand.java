package data.command;

import java.io.PrintStream;

import data.DataException;
import data.DataPair;
import data.EvaluationException;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

/**
 * A command that copies the value of a variable to a new location.
 */
public class CopyCommand extends CommandValue {
	public static String COMMAND_WORD = "copy";
	public static String USAGE_DESCRIPTION = "copy <variable> [destination] [createPath?]";

	@Override
	public Value copy() {
		return new CopyCommand();
	}

	@Override
	public PrimitiveValue evaluate(data.DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1){
			throw new EvaluationException(String.format("Copy needs at least one arguments to work: %s",USAGE_DESCRIPTION));
		}
		try{
			DataPair original = Path.convertToPath(args[0],environment.getPath()).getLowestDataPair();
			if(original instanceof Command) throw new EvaluationException(String.format("Can not manipulate %s as it is a system command.", original.getName()));
			if(args.length == 1){
				DataPair result = environment.addData(original.getName(), original.getValue().copy());
				output.printf("Variable \'%s\' copied to \'%s\'.\n",original.getPath(),result.getPath());
			}
			else{
				Path result = Path.convertToPath(args[1], environment.getPath());
				result.setValue(original.getValue().copy(), args.length >= 3 ? args[3].evaluate(environment, new Value[0], output).getBool() : true);
				output.printf("Variable \'%s\' copied to \'%s\'.\n",original.getPath(),result);
			}
		} catch (DataException|PathException e) {
			throw new EvaluationException(String.format("Could not evaluate, since: %s.",e.getMessage()),e);
		}
		return new VoidValue();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
