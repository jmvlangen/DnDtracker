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

public class MoveCommand extends CommandValue {
	public static String COMMAND_WORD = "move";
	public static String USAGE_DESCRIPTION = "move <variable> [destination]";
	
	@Override
	public Value copy() {
		return new MoveCommand();
	}
	
	@Override
	public PrimitiveValue evaluate(data.DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1){
			throw new EvaluationException(String.format("The command \'move\' needs at least one arguments to work: %s",USAGE_DESCRIPTION));
		}
		try{
			DataPair original = Path.convertToPath(args[0],environment.getPath()).getLowestDataPair();
			if(original instanceof Command) throw new EvaluationException(String.format("Can not manipulate %s as it is a system command.", original.getName()));
			if(args.length == 1){
				DataPair result = environment.addData(original.getName(), original.getValue().copy());
				original.getHost().removeData(original);
				output.printf("Variable \'%s\' moved to \'%s\'.\n",original.getPath(),result.getPath());
			}
			else{
				Path result = Path.convertToPath(args[1], environment.getPath());
				result.setValue(original.getValue().copy());
				original.getHost().removeData(original);
				output.printf("Variable \'%s\' moved to \'%s\'.\n",original.getPath(),result);
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
