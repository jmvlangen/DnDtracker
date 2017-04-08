package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.DataException;
import data.EvaluationException;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class CreateCommand extends CommandValue {
	public static final String COMMAND_WORD = "create";
	public static final String USAGE_DESCRIPTION = "create <path> [value]";

	@Override
	public Value copy() {
		return new CreateCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1) throw new EvaluationException(String.format("The create command needs at least one argument: %s",USAGE_DESCRIPTION));
		try{
			Path path = Path.convertToPath(args[0], environment.getPath());
			path.createPath();
			output.printf("Variable \'%s\' created.\n", path.toString());
			if(args.length >= 2){
				path.setValue(args[1], false);
				output.printf("Value of \'%s\' set to \'%s\'.\n", path.toString(), args[1].toString());
			}
			return new VoidValue();
		} catch(DataException|PathException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s",e.getMessage()),e);
		}
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
