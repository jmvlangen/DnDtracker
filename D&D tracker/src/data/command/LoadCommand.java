package data.command;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import data.DataContainer;
import data.DataException;
import data.DataPair;
import data.EvaluationException;
import data.GlobalValue;
import data.NamedValue;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.ReferenceValue;
import data.SubVariableValue;
import data.SumValue;
import data.TextValue;
import data.Value;
import data.VoidValue;
import main.ReadingException;
import main.ValueReader;

/**
 * A command that copies a variable to a new location.
 */
public class LoadCommand extends CommandValue {
	public static String COMMAND_WORD = "load";
	public static String USAGE_DESCRIPTION = "load <fileName> <variable>";
	
	private static final int MAX_EXTENSION_SIZE = 4;
	private static final String LINE_PREFIX = "line";
	private static final TextValue END_OF_LINE = new TextValue("\n");


	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 2) throw new EvaluationException(String.format("The command \'load\' needs at least two arguments to work: %s",USAGE_DESCRIPTION));
		DataPair loadedData = createAndGetDataPair(environment, args);
		String fileName = getFullFileName(args[0].evaluate(environment, args, output));
		FileReader fileReader = null;
		Scanner input = null;
		try{
			fileReader = new FileReader(fileName);
			input = new Scanner(fileReader);
			String extension = getExtension(fileName);
			if(extension.equalsIgnoreCase("dat")) readDatFile(input,loadedData,new ValueReader());
			else if(extension.equalsIgnoreCase("txt")) readTxtFile(input,loadedData);
			else{
				closeReaders(fileReader, input);
				throw new EvaluationException(String.format("Can not load files with extension %s.", extension));
			}
			output.printf("File \"%s\" loaded to \'%s\'.\n", fileName, loadedData.getPath());
			input.close();
			fileReader.close();
		} catch(IOException e){
			closeReaders(fileReader, input);
			throw new EvaluationException(String.format("Problem occured with reading a file: %s",e.getMessage()),e);
		}
		return new VoidValue();
	}

	private void closeReaders(FileReader fileReader, Scanner input) throws EvaluationException {
		try{
			if(input != null) input.close();
			if(fileReader != null) fileReader.close();
		} catch(IOException e){
			throw new EvaluationException("Could not close the file reader.",e);
		}
	}

	private DataPair createAndGetDataPair(DataContainer environment, Value[] args)
			throws EvaluationException {
		try {
			Path path = Path.convertToPath(args[1], environment.getPath());
			path.createPath();
			return path.getLowestDataPair();
		} catch (DataException | PathException e) {
			throw new EvaluationException(String.format("Can not evaluate, since: %s.",e.getMessage()),e);
		}
	}
	
	private void readTxtFile(Scanner input, DataPair loadedData) throws EvaluationException {
		try{
			readLines(input,getContainer(loadedData));
		} catch(ReadingException e){
			throw new EvaluationException(String.format("Could not load, since: %s", e.getMessage()),e);
		}
	}
	
	private void readLines(Scanner input, DataContainer dataContainer) throws ReadingException {
		int lineNumber = 1;
		Value total = new VoidValue();
		while(input.hasNextLine()){
			String name = LINE_PREFIX + String.valueOf(lineNumber);
			dataContainer.removeData(name);
			Value text = new TextValue(input.nextLine());
			try{
				dataContainer.addData(name,text);
			} catch(DataException e){
				throw new ReadingException(String.format("Could not load %s as it already exists.", name),e);
			}
			total = new SumValue(new SumValue(total, END_OF_LINE),text);
		}
	}

	private void readDatFile(Scanner input, DataPair loadedData, ValueReader valueReader) throws EvaluationException {
		input.useDelimiter("\\s");
		try{
			loadedData.setValue(valueReader.readValue(input));
		} catch(ReadingException e){
			throw new EvaluationException(String.format("Could not load, since: %s", e.getMessage()),e);
		}
	}

	private DataContainer getContainer(DataPair loadedData) throws EvaluationException {
		Value value = loadedData.getValue();
		if(value instanceof DataContainer) return (DataContainer) value;
		DataContainer result = new DataContainer(loadedData);
		loadedData.setValue(result);
		if(!(value instanceof VoidValue)) setValue(loadedData,value);
		return result;
	}

	private DataPair createPathAndGetLowestDataPair(Value path, DataContainer environment) throws EvaluationException {
		if(path instanceof NamedValue) return createOrGetDataPair(path.toString(),environment);
		if(path instanceof SubVariableValue) return createOrGetSubVariable((SubVariableValue) path,environment);
		return null;
	}
	
	private DataPair createOrGetSubVariable(SubVariableValue value, DataContainer environment) throws EvaluationException{
		environment = createOrGetReferencedDataContainer(value.getReference(),environment);
		for(int i = 0; i < value.getLevel(); i++) environment = environment.getLevelAbove();
		return createPathAndGetLowestDataPair(value.getSubValue(), environment);
	}
	
	private DataContainer createOrGetReferencedDataContainer(ReferenceValue reference, DataContainer environment) throws EvaluationException {
		if(reference instanceof GlobalValue) return environment.getTopLevel();
		if(reference instanceof NamedValue) return createOrGetNamedDataContainer((NamedValue) reference, environment);
		throw new EvaluationException(String.format("Can not create variables from arguments of type %s.",reference.getTypeName()));
	}
	
	private DataContainer createOrGetNamedDataContainer(NamedValue reference, DataContainer environment) throws EvaluationException{
		try {
			if(environment.containsNameAbsolute(reference.toString())){
				Value value = reference.getReferencedValue(environment);
				if(value instanceof DataContainer) return (DataContainer) value;
				environment.removeData(reference.toString());
				DataPair data = environment.addSubDataContainer(reference.toString());
				setValue(data,value);
				if(data.getValue() instanceof DataContainer) return (DataContainer) data.getValue();
				throw new EvaluationException(String.format("The variable %s is not a %s after changing it.", data.getPath(),DataContainer.DATA_TYPE_NAMES[0]));

			} else {
				DataPair data = environment.addSubDataContainer(reference.toString());
				if(data.getValue() instanceof DataContainer) return (DataContainer) data.getValue();
				throw new EvaluationException(String.format("The variable %s is not a %s after changing it.", data.getPath(),DataContainer.DATA_TYPE_NAMES[0]));
			}
		} catch (DataException e) {
			throw new EvaluationException(String.format("Could not evaluate, since: %s",e.getMessage()),e);
		}
	}
	
	private DataPair createOrGetDataPair(String name, DataContainer environment) throws EvaluationException{
		if(environment.containsNameAbsolute(name)){
			try{
				return environment.getData(name);
			} catch(DataException e){
				throw new EvaluationException(String.format("Can not load variable \'%s\' as it already exists in \'%s\'.",name,environment.getPath()),e);
			}
		}
		else
			try {
				return environment.addData(name, new VoidValue());
			} catch (DataException e) {
				throw new EvaluationException(String.format("Attempting to create a variable %s that already exists in %s.",name,environment.getPath()));
			}
	}
	
	private void setValue(DataPair data, Value value) throws EvaluationException{
		if(data.getValue() instanceof DataContainer) setValue(createOrGetDataPair(DataContainer.VALUE_NAME,(DataContainer) data.getValue()),value);
		data.setValue(value);
	}

	private String getFullFileName(Value value) throws EvaluationException{
		if(value instanceof TextValue) return getFullFileName(((TextValue) value).getText());
		throw new EvaluationException(String.format("The first argument of the load command should evaluate to a variable of type %s, not of type %s.",TextValue.VALUE_TYPE_NAMES[0],value.getTypeName()));
	}
	
	private String getFullFileName(String text) {
		if(!hasFileExtension(text)) text = String.format("%s.dat", text);
		return text;
	}

	private boolean hasFileExtension(String s){
		int j = s.lastIndexOf(".");
		int k = s.length() - j;
		return j != -1 && k > 1 && k <= MAX_EXTENSION_SIZE + 1;
	}
	
	private String getExtension(String s){
		int j = s.lastIndexOf(".");
		return s.substring(j+1);
	}

	@Override
	public Value copy() {
		return new LoadCommand();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
