package data.command;

import java.util.Comparator;

import data.DataException;
import data.DataPair;
import data.Path;
import data.Value;
import data.VoidValue;

public class SortComparator implements Comparator<DataPair> {
	private Path subPath;
	
	public SortComparator(Path subPath){
		this.subPath = subPath;
	}

	@Override
	public int compare(DataPair data1, DataPair data2) {
		return getCompareValue(data1).compareTo(getCompareValue(data2));
	}
	
	private Value getCompareValue(DataPair data) {
		return getCorrespondingValue(data.getPath().extend(subPath));
	}

	private Value getCorrespondingValue(Path path) {
		try{
			return path.getLowestValue();
		} catch(DataException e){
			return new VoidValue();
		}
	}

}
