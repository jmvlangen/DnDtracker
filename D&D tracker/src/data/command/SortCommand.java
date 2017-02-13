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
import data.SubVariableValue;
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
		DataContainer collectionToSort = getCollection(environment,args[0]);
		String[] subNames;
		if(args.length >= 2) subNames = getSubNames(args[1]);
		else subNames = new String[0];
		List<DataPair> dataToSort = getVisibleDataInCollection(collectionToSort);
		dataToSort.sort(new SortComparator(subNames));
		exportData(collectionToSort,dataToSort);
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

	private String[] getSubNames(Value value) throws EvaluationException {
		List<String> result = getSubNamesList(value);
		return result.toArray(new String[result.size()]);
	}

	private List<String> getSubNamesList(Value value) throws EvaluationException {
		if(value instanceof NamedValue) return getSubNamesListFromNamedValue((NamedValue) value);
		if(value instanceof SubVariableValue) return getSubNamesListFromSubVariableValue((SubVariableValue) value);
		if(value instanceof VoidValue) return new ArrayList<String>();
		throw new EvaluationException(String.format("Can not use \'%s\' as a criterium to sort by.", value.toString()));
	}

	private List<String> getSubNamesListFromSubVariableValue(SubVariableValue value) throws EvaluationException {
		List<String> list1 = getSubNamesList(value.getReference());
		List<String> list2 = getSubNamesList(value.getSubValue());
		list1.addAll(list2);
		return list1;
	}

	private List<String> getSubNamesListFromNamedValue(NamedValue value) {
		List<String> result = new ArrayList<String>();
		result.add(value.toString());
		return result;
	}

	private DataContainer getCollection(DataContainer environment, Value value) throws EvaluationException {
		try{
			Path path = Path.convertToPath(value, environment.getPath());
			Value val2 = path.getLowestValue();
			if(val2 instanceof DataContainer) return (DataContainer) value;
			throw new EvaluationException(String.format("The variable \'%s\' did not contain a %s.", path.toString(),DataContainer.DATA_TYPE_NAMES[0]));
		} catch(DataException|PathException e){
			throw new EvaluationException(String.format("Could not evaluate, since: %s.",e.getMessage()),e);
		}
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}

}
