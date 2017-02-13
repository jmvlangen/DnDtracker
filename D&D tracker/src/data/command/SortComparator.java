package data.command;

import java.util.Comparator;

import data.DataException;
import data.DataPair;
import data.Path;
import data.Value;
import data.VoidValue;

public class SortComparator implements Comparator<DataPair> {
	private String[] subNames;
	
	public SortComparator(String[] subNames){
		this.subNames = subNames;
	}

	@Override
	public int compare(DataPair data1, DataPair data2) {
		return getCorrespondingValue(new Path(data1.getPath(),subNames)).compareTo(getCorrespondingValue( new Path(data2.getPath(),subNames)));
	}

	private Value getCorrespondingValue(Path path) {
		try{
			return path.getLowestValue();
		} catch(DataException e){
			return new VoidValue();
		}
	}

}
