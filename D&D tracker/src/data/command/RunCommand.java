package data.command;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.TextValue;
import data.Value;
import data.VoidValue;
import main.Tracker;

/**
 * A command that copies a variable to a new location.
 */
public class RunCommand extends CommandValue {
	public static String COMMAND_WORD = "run";
	public static String USAGE_DESCRIPTION = "run <fileName>";
	
	private static final int MAX_EXTENSION_SIZE = 4;
	private static final String DEFAULT_EXTENSION = "scr";


	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1) throw new EvaluationException(String.format("The command \'run\' needs at least one argument to work: %s",USAGE_DESCRIPTION));
		String fileName = getFullFileName(args[0].evaluate(environment, args, output));
		FileReader fileReader = null;
		Scanner input = null;
		try{
			fileReader = new FileReader(fileName);
			input = new Scanner(fileReader);
			readScrFile(input,output);
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

	private void readScrFile(Scanner input, PrintStream output) throws EvaluationException {
		while(input.hasNextLine()){
			Tracker.mainInstance.dispatchLine(input.nextLine(), output);
		}
	}

	private String getFullFileName(Value value) throws EvaluationException{
		if(value instanceof TextValue) return getFullFileName(((TextValue) value).getText());
		throw new EvaluationException(String.format("The first argument of the load command should evaluate to a variable of type %s, not of type %s.",TextValue.VALUE_TYPE_NAMES[0],value.getTypeName()));
	}
	
	private String getFullFileName(String text) {
		if(!hasFileExtension(text)) text = String.format("%s.%s", text, DEFAULT_EXTENSION);
		return text;
	}

	private boolean hasFileExtension(String s){
		int j = s.lastIndexOf(".");
		int k = s.length() - j;
		return j != -1 && k > 1 && k <= MAX_EXTENSION_SIZE + 1;
	}

	@Override
	public Value copy() {
		return new RunCommand();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
