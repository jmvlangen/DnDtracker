package data.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import data.DataContainer;
import data.DataException;
import data.DataPair;
import data.EvaluationException;
import data.NamedValue;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class SortCommand extends CommandValue {
	public static String COMMAND_WORD = "sort";
	public static String USAGE_DESCRIPTION = "sort <collection> [variable]";
	
	public static final int START_OF_ARRAY = 1;

	@Override
	public Value copy() {
		return new SortCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1) throw new EvaluationException("The command \'sort\' needs at least one argument to work.");
		DataContainer collectionToSort = args[0].evaluateToFirstDataContainer(environment, args, output);
		Path subPath;
		if(args.length >= 2)
			try {
				subPath = Path.convertToPath(args[1], collectionToSort.getTopLevel().getPath());
			} catch (PathException e) {
				throw new EvaluationException(String.format("Can not evaluate, since %s",e.getMessage()),e);
			}
		else subPath = new Path(new String[0], collectionToSort);
		List<DataPair> dataToSort = getVisibleDataInCollection(collectionToSort);
		dataToSort.sort(new SortComparator(subPath));
		exportData(collectionToSort,dataToSort);
		if(subPath.depth() > 0) output.printf("Collection \'%s\' sorted by \'%s\'.\n", collectionToSort.getPath().toString(), subPath.toString());
		else output.printf("Collection \'%s\' sorted.\n", collectionToSort.getPath().toString());
		return new VoidValue();
	}

	private void exportData(DataContainer collection, List<DataPair> data) throws EvaluationException {
		int count = START_OF_ARRAY;
		for(DataPair dataPair : data){
			DataPair dataPair2 = createOrGetDataPair(collection, String.format("_%d", count));
			dataPair2.setValue(new NamedValue(dataPair.getName()));
			count += 1;
		}
	}

	private DataPair createOrGetDataPair(DataContainer collection, String name) throws EvaluationException {
		try{
			return collection.getData(name);
		} catch(DataException e){
			try{
				return collection.addData(name, new VoidValue());
			} catch(DataException e1){
				throw new EvaluationException(String.format("Can not evaluate, since: %s", e1.getMessage()),e1);
			}
		}
	}

	private List<DataPair> getVisibleDataInCollection(DataContainer collectionToSort) {
		List<DataPair> result = new ArrayList<DataPair>();
		for(DataPair data : collectionToSort){
			if(data.getName().charAt(0) != '_') result.add(data);
		}
		return result;
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}

}
