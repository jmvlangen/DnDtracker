package command;

import java.io.FileWriter;
import java.io.PrintStream;

import data.CalculationData;
import data.Data;
import data.DataContainer;
import data.IntData;
import data.ReferenceData;
import main.Calculator;
import main.Tracker;

/**
 * A command that copies a variable to a new location.
 */
public class SaveCommand implements Command {
	public static String COMMAND_WORD = "save";
	public static int NUMBER_OF_ARGUMENTS = 2;
	public static String USAGE_DESCRIPTION = "save <variable> <filename>";
	
	public static final String DATA_CONTAINER_NAME = "collection";
	public static final String INT_DATA_NAME = "integer";
	public static final String CALCULATION_DATA_NAME = "function";
	public static final String REFERENCE_DATA_NAME = "reference";
	
	public static final String TAB = "    ";
	private static final int MAX_EXTENSION_SIZE = 4;

	@Override
	public String getCommandWord() {
		return COMMAND_WORD;
	}

	@Override
	public int getNumberOfArguments() {
		return NUMBER_OF_ARGUMENTS;
	}

	@Override
	public String usageDescription() {
		return USAGE_DESCRIPTION;
	}

	@Override
	public void performCommand(String[] args, Tracker program, PrintStream output){
		FileWriter fileWriter = null;
		try{
			String fileName = getFullFileName(args[1]);
			fileWriter = new FileWriter(fileName);
			Data variable = Calculator.readVariable(args[0], program.currentContainer);
			if(variable == null) throw new Exception(String.format("The variable %s does not exist.",args[0]));
			writeData(variable,fileWriter,0);
			fileWriter.close();
			output.printf("Variable %s saved to %s.\n", variable.getName(),fileName);
		} catch(Exception e){
			try{
				if(fileWriter != null) fileWriter.close();
			} catch(Exception e2){
				output.printf("Error: %s\n", e2.getMessage());
			}
			output.printf("Error: %s\n", e.getMessage());
		}
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

	private void writeData(Data data,FileWriter fileWriter,int level) throws Exception{
		indentLevel(fileWriter,level);
		if(data instanceof DataContainer) writeDataContainer((DataContainer) data, fileWriter,level);
		else if(data instanceof IntData) writeIntData((IntData) data, fileWriter);
		else if(data instanceof ReferenceData) writeReferenceData((ReferenceData) data, fileWriter);
		else if(data instanceof CalculationData) writeCalculationData((CalculationData) data, fileWriter);
		else throw new Exception(String.format("The variable %s is not of a type that can be saved.", data.getPath()));
	}
	
	private void writeDataContainer(DataContainer dataContainer, FileWriter fileWriter, int level) throws Exception{
		writeTag(DATA_CONTAINER_NAME,dataContainer.getName(),fileWriter);
		fileWriter.append("(");
		for(Data d : dataContainer){
			fileWriter.append("\n");
			writeData(d,fileWriter,level + 1);
		}
		fileWriter.append(")");
	}
	
	private void writeIntData(IntData data,FileWriter fileWriter) throws Exception{
		writeTag(INT_DATA_NAME,data.getName(),fileWriter);
		fileWriter.append(String.format("(%d)", data.getInteger()));
	}
	
	private void writeReferenceData(ReferenceData data,FileWriter fileWriter) throws Exception{
		writeTag(REFERENCE_DATA_NAME,data.getName(),fileWriter);
		fileWriter.append(String.format("(%s)", data.getReferenceString()));
	}
	
	private void writeCalculationData(CalculationData data, FileWriter fileWriter) throws Exception{
		writeTag(CALCULATION_DATA_NAME,data.getName(),fileWriter);
		fileWriter.append(String.format("(%s)",data.getCalculation()));
	}
	
	private void indentLevel(FileWriter fileWriter,int level) throws Exception{
		for(int i = 0; i < level; i++){
			fileWriter.append(TAB);
		}
	}
	
	private void writeTag(String type, String name, FileWriter fileWriter) throws Exception{
		fileWriter.append("<");
		fileWriter.append(type);
		fileWriter.append(":");
		fileWriter.append(name);
		fileWriter.append(">");
	}
}
