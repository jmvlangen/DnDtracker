package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.DataException;
import data.DataPair;
import data.EvaluationException;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class RemoveCommand extends CommandValue {
	public static String COMMAND_WORD = "remove";
	public static String USAGE_DESCRIPTION = "remove <variable>";

	@Override
	public Value copy() {
		return new MoveCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1){
			throw new EvaluationException(String.format("The command 'remove' needs at least one arguments to work: %s",USAGE_DESCRIPTION));
		}
		try{
			Path path = Path.convertToPath(args[0], environment.getPath());
			DataPair original = path.getLowestDataPair();
			if(original instanceof Command) throw new EvaluationException(String.format("Can not manipulate %s as it is a system command.", original.getName()));
			original.getHost().removeData(original);
			output.printf("Variable %s removed.\n",path.toString());
		} catch(DataException|PathException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s.",e.getMessage()),e);
		}
		return new VoidValue();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
