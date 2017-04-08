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
import main.Tracker;

public class EnvironmentCommand extends CommandValue {
	public static final String COMMAND_WORD = "environment";
	public static final String USAGE_DESCRIPTION = "environment [collection]";

	@Override
	public Value copy() {
		return new EnvironmentCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length >= 1) Tracker.mainInstance.currentContainer = getDataContainer(args[0],environment);
		output.printf("Currently working in \'%s\'.\n", Tracker.mainInstance.currentContainer.getPath());
		return new VoidValue();
	}
	
	private DataContainer getDataContainer(Value value, DataContainer environment) throws EvaluationException{
		try{Path path = Path.convertToPath(value, environment.getPath());
		Value oldValue = path.getLowestValue();
		if(oldValue instanceof DataContainer) return (DataContainer) oldValue;
		else{
			DataPair data = path.getLowestDataPair();
			DataContainer newValue = new DataContainer();
			data.setValue(newValue);
			if(!(oldValue instanceof VoidValue)) newValue.addData(DataContainer.VALUE_NAME,oldValue);
			return newValue;
		}
		} catch(DataException|PathException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s.",e.getMessage()),e);
		}
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}

}
