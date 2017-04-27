package data.command;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import data.DataContainer;
import data.DataException;
import data.DataPair;
import data.EvaluationException;
import data.Path;
import data.PathException;
import data.PrimitiveValue;
import data.TextValue;
import data.Value;
import data.VoidValue;

/**
 * A command that copies a variable to a new location.
 */
public class SaveCommand extends CommandValue {
	public static String COMMAND_WORD = "save";
	public static String USAGE_DESCRIPTION = "save <filename> [variable]";
	
	public static final String TAB = "    ";
	private static final int MAX_EXTENSION_SIZE = 4;
	private static final String VARIABLE_NAME = "variable";

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1) throw new EvaluationException(String.format("The command \'save\' needs at least one argument to work: %s",USAGE_DESCRIPTION));
		String fileName = getFullFileName(args[0].evaluate(environment, args, output));
		Value variable = args.length >= 2 ? getVariable(args[1],environment) : environment;
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter(fileName);
			writeValue(variable,fileWriter,0);
			fileWriter.close();
			output.printf("Variable saved to %s.\n",fileName);
		} catch(IOException e){
			try{
				if(fileWriter != null) fileWriter.close();
			} catch(IOException e2){
				throw new EvaluationException(String.format("Unable to close the file writer."),e2);
			}
			throw new EvaluationException(String.format("Could not evaluate, since: %s",e.getMessage()),e);
		}
		return new VoidValue();
	}

	private Value getVariable(Value value, DataContainer environment) throws EvaluationException {
		try{
			Path path = Path.convertToPath(value, environment.getPath());
			if(path.depth() == 0) throw new EvaluationException("Can not save \':\'.");
			return path.getLowestValue();
		} catch(PathException|DataException e){
			throw new EvaluationException(String.format("Can not evaluate, since: %s",e.getMessage()),e);
		}
	}

	private String getFullFileName(Value value) throws EvaluationException{
		if(value instanceof TextValue) return getFullFileName(((TextValue) value).getText());
		throw new EvaluationException(String.format("The first argument of the load command should evaluate to a variable of type %s, not of type %s.",TextValue.VALUE_TYPE_NAMES[0],value.getTypeName()));
	}
	
	private String getFullFileName(String partialName) {
		if(!hasFileExtension(partialName)) partialName = String.format("%s.dat", partialName);
		return partialName;
	}
	
	private boolean hasFileExtension(String s){
		StringBuilder sb = new StringBuilder(s);
		int j = sb.lastIndexOf(".");
		int k = sb.length() - j;
		return j != -1 && k > 1 && k <= MAX_EXTENSION_SIZE + 1;
	}

	private void writeValue(Value value,FileWriter fileWriter,int level) throws IOException{
		if(value instanceof DataContainer) writeDataContainer((DataContainer) value, fileWriter, level);
		else writeNormalValue(value, fileWriter);
	}
	
	private void writeDataContainer(DataContainer dataContainer, FileWriter fileWriter, int level) throws IOException{
		fileWriter.append("{\n");
		Iterator<DataPair> iterator = dataContainer.iterator();
		while(iterator.hasNext()){
			writeDataPair(iterator.next(),fileWriter,level + 1);
			fileWriter.append(iterator.hasNext() ? " ,\n" : " }");
		}
	}
	
	private void writeDataPair(DataPair data, FileWriter fileWriter, int level) throws IOException{
		if(!(data instanceof Command)){
			indentLevel(fileWriter,level);
			fileWriter.append(data.getName()).append(" = ");
			writeValue(data.getValue(),fileWriter,level);
		}
	}
	
	private void writeNormalValue(Value value, FileWriter fileWriter) throws IOException{
		fileWriter.append(value.toString());
	}
	
	private void indentLevel(FileWriter fileWriter,int level) throws IOException{
		for(int i = 0; i < level; i++){
			fileWriter.append(TAB);
		}
	}

	@Override
	public Value copy() {
		return new SaveCommand();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
