package data.command;

import java.io.PrintStream;

import data.BooleanValue;
import data.DataContainer;
import data.DataException;
import data.EvaluationException;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

/**
 * A command that sets a variable to a specific value
 *
 */
public class SetCommand extends CommandValue{
	public static String COMMAND_WORD = "set";
	public static String USAGE_DESCRIPTION = "set <variable> <value> [createPath?]";

	@Override
	public Value copy() {
		return new SetCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 2) throw new EvaluationException(String.format("The set command needs at least two argument: %s",USAGE_DESCRIPTION));
		try{
			Path path = Path.convertToPath(args[0], environment.getPath());
			path.setValue(args[1],args.length >= 3 ? getBool(args[3],environment,output) : false);
			output.printf("Value of \'%s\' set to \'%s\'.\n", path.toString(), args[1].toString());
			return new VoidValue();
		} catch(DataException|PathException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s",e.getMessage()),e);
		}
	}

	private boolean getBool(Value value, DataContainer environment, PrintStream output) throws EvaluationException {
		PrimitiveValue evaluatedValue = value.evaluate(environment, new Value[0], output);
		if(evaluatedValue instanceof VoidValue) return false;
		if(evaluatedValue instanceof BooleanValue) return ((BooleanValue) evaluatedValue).getBooleanFlag();
		return true;
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
