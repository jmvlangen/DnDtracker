package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

//A container of Data objects. It is itself a Data object adhering to all the rules of a Data object

public class DataContainer extends Data implements Iterable<Data>{
	public static final String[] DATA_TYPE_NAMES = {"collection","col"};
	
	private Collection<Data> dataSet;
	
	/**
	 * A contructor that makes a top level DataContainer.
	 * This DataContainer will not be contained in any other DataContainer
	 * @param name The name that this DataContainer will be known by.
	 * This name will not have to be unique, since the container will be the top level.
	 */
	public DataContainer(String name){
		this(name,null);
		changeHost(this);
		addData(this);
	}
	
	/**
	 * A constructor that makes a DataContainer.
	 * Note that this constructor will not register this Data object with its host.
	 * @param name The name that this DataContainer will be known by.
	 * @param host The DataContainer that will contain this Data object.
	 */
	public DataContainer(String name, DataContainer host){
		super(name,host);
		dataSet = new ArrayList<Data>();
	}

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		sb.append("Collection containing:");
		for(Data d : dataSet){
			sb.append("\n");
			if(d instanceof DataContainer) sb.append(d.getName()).append(": collection");
			else sb.append(d.toString());
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Data other) {
		if(getHost() == other.getHost() && other.getName().equals(getName()) && other instanceof DataContainer){
			DataContainer otherDataContainer = (DataContainer) other;
			return otherDataContainer.dataSet.equals(dataSet);
		}
		return false;
	}
	
	/**
	 * @param name A String of alpha-numerical characters starting with a non-digit that may be the name of a Data object.
	 * @return TRUE if this DataContainer contains a DataObject with name equal to @param name
	 * FALSE otherwise
	 */
	public boolean containsName(String name){
		for(Data d : dataSet){
			if(d.getName().equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * Gets a specific Data object from this DataContainer by its name.
	 * @param name A String of alhpanumerical characters starting with a non-digit that refers to a Data object in this DataContainer.
	 * @return The Data object that has name @param name in this DataContainer, or null if such an object does not exist.
	 * Note that it will also look among the Data objects in higher levels if none are found in the lower levels.
	 */
	public Data getDataByName(String name){
		for(Data d : dataSet){
			if(d.getName().equals(name)) return d;
		}
		return isTopLevel() ? null : getHost().getDataByName(name);
	}
	
	/**
	 * Gets a specific Data object from this DataContainer by its name.
	 * @param name A String of alhpanumerical characters starting with a non-digit that refers to a Data object in this DataContainer.
	 * @return The Data object that has name @param name in this DataContainer, or null if such an object does not exist.
	 * Note that this method will ONLY look at Data objects in this DataContainer object.
	 */
	public Data getDataByNameAbsolute(String name){
		for(Data d : dataSet){
			if(d.getName().equals(name)) return d;
		}
		return null;
	}
	
	/**
	 * Adds a Data object to this Container if it contains none with the same name.
	 * It is advised to use the other methods add[Type]Data to create Data objects within this Data container instead of creating them yourself and adding them with this method.
	 * @param data A Data object that has this DataContainer as its host.
	 * @return TRUE if this Data object was added to this DataContainer.
	 * FALSE in all other cases.
	 */
	public boolean addData(Data data){
		return data.getHost() == this && ( !this.containsName(data.getName()) ) && dataSet.add(data);
	}
	
	/**
	 * Creates a DataContainer and adds it to this DataContainer.
	 * @param name A String of alphanumerical characters starting with a non-digit that should be the name of the newly created DataContainer.
	 * This name may not be contained in this DataContainer already to be succesful.
	 * @return The newly created DataContainer or null if none could be added to this DataContainer.
	 */
	public DataContainer addSubDataContainer(String name){
		if(this.containsName(name)) return null;
		DataContainer subDataContainer = new DataContainer(name,this);
		if(this.addData(subDataContainer)) return subDataContainer;
		return null;
	}
	
	/**
	 * Creates a CalculationData object and adds it to this DataContainer.
	 * @param name A String of alphanumerical characters starting with a non-digit that should be the name of the newly created CalculationData.
	 * This name may not be contained in this DataContainer already to be succesful.
	 * @param calculation The calculation that should be stored in the new CalculationData object.
	 * @return The newly created CalculationData or null if none could be added to this DataContainer.
	 */
	public CalculationData addCalculationData(String name, String calculation){
		if(this.containsName(name)) return null;
		CalculationData calculationData = new CalculationData(name,this,calculation);
		return this.addData(calculationData) ? calculationData : null;
	}
	
	/**
	 * Creates a IntData object and adds it to this DataContainer.
	 * @param name A String of alphanumerical characters starting with a non-digit that should be the name of the newly created IntData.
	 * This name may not be contained in this DataContainer already to be succesful.
	 * @param value The integer that should be stored in the new IntData object.
	 * @return The newly created IntData or null if none could be added to this DataContainer.
	 */
	public IntData addIntData(String name, int value){
		if(this.containsName(name)) return null;
		IntData data = new IntData(name,this,value);
		return this.addData(data) ? data : null;
	}

	/**
	 * Creates a ReferenceData object and adds it to this DataContainer.
	 * @param name A String of alphanumerical characters starting with a non-digit that should be the name of the newly created ReferenceData.
	 * This name may not be contained in this DataContainer already to be succesful.
	 * @param reference The reference that should be stored in this ReferenceData object.
	 * Note that this reference should be valid, i.e. ReferenceData.isValidReference(reference) == true.
	 * @return The newly created ReferenceData or null if none could be added to this DataContainer.
	 */
	public Object addReferenceData(String name, String reference) {
		if(this.containsName(name)) return null;
		ReferenceData data = new ReferenceData(name,this,reference);
		return this.addData(data) ? data : null;
	}

	/**
	 * Creates a TextData object and adds it to this DataContainer.
	 * @param name A String of alphanumerical characters starting with a non-digit that should be the name of the newly created TextData.
	 * This name may not be contained in this DataContainer already to be succesful.
	 * @param text The String that should be stored in this TextData object.
	 * @return The newly created TextData or null if none could be added to this DataContainer.
	 */
	public Object addTextData(String name, String text) {
		if(this.containsName(name)) return null;
		ReferenceData data = new ReferenceData(name,this,text);
		return this.addData(data) ? data : null;
	}
	
	/**
	 * Removes a Data object from this DataContainer if it was present therein.
	 * @param data The Data object to be removed from this DataContainer.
	 */
	public void removeData(Data data){
		if(data != this) dataSet.remove(data);
	}
	
	/**
	 * Removes a Data object from this DataContainer object that is specified by the given name.
	 * @param name A name of a Data object.
	 * If the Data object is not present in this DataContainer, nothing will happen.
	 */
	public void removeDataByName(String name){
		Data d = getDataByNameAbsolute(name);
		if(d != null) removeData(d);
	}
	
	/**
	 * Determines whether this is the topmost level DataContainer
	 * @return TRUE if this DataContainer is not contained in any other DataContainer
	 * FALSE otherwise
	 */
	public boolean isTopLevel(){
		return getHost() == this;
	}

	/**
	 * Returns the top level DataContainer in which this DataContainer is contained.
	 * @return A top level DataContainer object that lies above this DataContainer object.
	 */
	public DataContainer getTopLevel() {
		return isTopLevel() ? this : getHost().getTopLevel();
	}
	
	public String getPath(){
		return isTopLevel() ? getName() : getHost().getPath() + "." + getName();
	}

	@Override
	public Data copy(DataContainer host) {
		DataContainer copy = new DataContainer(getName(),host);
		for(Data d : dataSet){
			copy.addData(d.copy(copy));
		}
		return copy;
	}

	@Override
	public Iterator<Data> iterator() {
		return dataSet.iterator();
	}

	public String getTypeName() {
		return DATA_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return DATA_TYPE_NAMES;
	}
}
