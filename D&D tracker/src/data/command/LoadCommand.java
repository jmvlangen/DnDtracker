package data.command;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import data.DataContainer;
import data.DataException;
import data.EvaluationException;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
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
		Path loadPath;
		try {
			loadPath = Path.convertToPath(args[0], environment.getPath());
		} catch (PathException e) {
			throw new EvaluationException(String.format("Could not load data, since %s",e.getMessage()),e);
		}
		String fileName = getFullFileName(args[0].evaluate(environment, args, output));
		FileReader fileReader = null;
		Scanner input = null;
		try{
			fileReader = new FileReader(fileName);
			input = new Scanner(fileReader);
			String extension = getExtension(fileName);
			if(extension.equalsIgnoreCase("dat")) readDatFile(input,loadPath,new ValueReader());
			else if(extension.equalsIgnoreCase("txt")) readTxtFile(input,loadPath);
			else{
				closeReaders(fileReader, input);
				throw new EvaluationException(String.format("Can not load files with extension %s.", extension));
			}
			output.printf("File \"%s\" loaded to \'%s\'.\n", fileName, loadPath);
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
	
	private void readTxtFile(Scanner input, Path loadPath) throws EvaluationException {
		try{
			loadPath.setValue(readLines(input), true);
		} catch(ReadingException|DataException e){
			throw new EvaluationException(String.format("Could not load, since: %s", e.getMessage()),e);
		}
	}
	
	private Value readLines(Scanner input) throws ReadingException {
		DataContainer result = new DataContainer();
		int lineNumber = 1;
		Value total = new VoidValue();
		while(input.hasNextLine()){
			String name = LINE_PREFIX + String.valueOf(lineNumber);
			result.removeData(name);
			Value text = new TextValue(input.nextLine());
			try{
				result.addData(name,text);
			} catch(DataException e){
				throw new ReadingException(String.format("Could not load %s as it already exists.", name),e);
			}
			total = new SumValue(new SumValue(total, END_OF_LINE),text);
		}
		return result;
	}

	private void readDatFile(Scanner input, Path loadPath, ValueReader valueReader) throws EvaluationException {
		input.useDelimiter("\\s");
		try{
			loadPath.setValue(valueReader.readValue(input), true);
		} catch(ReadingException|DataException e){
			throw new EvaluationException(String.format("Could not load, since: %s", e.getMessage()),e);
		}
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
