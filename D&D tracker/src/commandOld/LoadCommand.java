package command;

import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;

import data.CalculationData;
import data.Data;
import data.DataContainer;
import data.IntData;
import data.ReferenceData;
import data.TextData;
import main.Calculator;
import main.Tracker;

/**
 * A command that copies a variable to a new location.
 */
public class LoadCommand implements Command {
	public static String COMMAND_WORD = "load";
	public static int NUMBER_OF_ARGUMENTS = 1;
	public static String USAGE_DESCRIPTION = "load <fileName>\n" + "load <fileName> <environment>";
	
	public static final String DATA_CONTAINER_NAME = "collection";
	public static final String INT_DATA_NAME = "integer";
	public static final String CALCULATION_DATA_NAME = "function";
	public static final String REFERENCE_DATA_NAME = "reference";
	
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
		FileReader fileReader = null;
		Scanner input = null;
		try{
			DataContainer environment = getEnvironment(program.currentContainer,args[1]);
			String fileName = getFullFileName(args[0]);
			fileReader = new FileReader(fileName);
			input = new Scanner(fileReader);
			String extension = getExtension(fileName);
			if(extension.equalsIgnoreCase("dat")) readDatFile(input,environment,program,output);
			else if(extension.equalsIgnoreCase("txt")) readTxtFile(input,environment,output);
			else throw new Exception(String.format("Can not load files with extension %s.", extension));
		} catch(Exception e){
			try{
				if(input != null) input.close();
				if(fileReader != null) fileReader.close();
			} catch(Exception e2){
				output.printf("Error: %s\n", e2.getMessage());
			}
			output.printf("Error: %s\n", e.getMessage());
		}
	}
	
	private void readTxtFile(Scanner input,DataContainer environment, PrintStream output) throws Exception{
		int line = 0;
		while(input.hasNextLine()){
			line += 1;
			createOrChangeTextData(String.format("line%d", line),input.nextLine(),environment);
		}
		output.printf("%d lines loaded", line);
	}
	
	private void readDatFile(Scanner input, DataContainer environment, Tracker program, PrintStream output) throws Exception{
		input.useDelimiter("");
		readWhiteSpace(input);
		while(input.hasNext()){
			if(input.hasNext("\\#")) readInstructionLine(input,program,output);
			if(input.hasNext("\\<")) readDataTag(input,environment,output);
			readWhiteSpace(input);
		}
	}
	
	private void readDataTag(Scanner input, DataContainer environment, PrintStream output) throws Exception{
		readCharacter(input,'<');
		String type = readWord(input);
		readCharacter(input,':');
		String name = readName(input);
		readCharacter(input,'>');
		readData(type, name, environment, input,output);
		output.printf("%s.%s loaded.\n", environment.getPath(), name);
	}
	
	private void readCharacter(Scanner input, char c) throws Exception{
		if(!(input.hasNext() && input.next().charAt(0) == c)) throw new Exception(String.format("Attempting to read %c, but couldn\'t", c)); 
	}
	
	private int readNumber(Scanner input) throws Exception{
		int sign = 1;
		if(input.hasNext("\\-")){
			input.next();
			sign = -1;
		}
		int size = 0;
		if(!input.hasNext("[0-9]")) throw new Exception("Attempting to read a number, but it didn't start with a digit.");
		while(input.hasNext("[0-9]")) size = size * 10 + readDigit(input);
		return sign * size;
	}
	
	private String readWord(Scanner input) throws Exception{
		if(!input.hasNext()) throw new Exception("Attempting to read a word, but no input remains.");
		StringBuilder sb = new StringBuilder();
		do{
			sb.append(readLetter(input));
		} while(input.hasNext("[a-zA-Z]"));
		return sb.toString();
	}
	
	private String readName(Scanner input) throws Exception{
		if(!input.hasNext()) throw new Exception("Attempting to read a name, but no input remains.");
		StringBuilder sb = new StringBuilder();
		do{
			sb.append(readAlphaNumericCharacter(input));
		} while(input.hasNext("[a-zA-Z0-9]"));
		return sb.toString();
	}
	
	private String readAlphaNumericCharacter(Scanner input) throws Exception{
		if(!input.hasNext("[a-zA-Z0-9]")) throw new Exception("Attempting to read an alphanumerical character, but couldn't.");
		return input.next();
	}
	
	private String readLetter(Scanner input) throws Exception{
		if(!input.hasNext("[a-zA-Z]")) throw new Exception("Attempting to read a letter, but couldn't.");
		return input.next();
	}
	
	private int readDigit(Scanner input) throws Exception{
		if(!input.hasNext("[0-9]")) throw new Exception("Attempting to read a digit, but couldn't.");
		return Integer.parseInt(input.next());
	}
	
	private void readData(String type, String name, DataContainer environment, Scanner input, PrintStream output) throws Exception{
		if(type.equalsIgnoreCase(DATA_CONTAINER_NAME)) readDataContainer(name, environment, input, output);
		else if(type.equalsIgnoreCase(INT_DATA_NAME)) readIntData(name, environment, input);
		else if(type.equalsIgnoreCase(CALCULATION_DATA_NAME)) readCalculationData(name, environment, input);
		else if(type.equalsIgnoreCase(REFERENCE_DATA_NAME)) readReferenceData(name, environment, input);
		else throw new Exception(String.format("Data of type %s can not be read.",type));
	}
	
	private void readReferenceData(String name, DataContainer environment, Scanner input) throws Exception{
		readWhiteSpace(input);
		readCharacter(input,'(');
		StringBuilder sb = new StringBuilder();
		while(true){
			if(!input.hasNext()) throw new Exception("Attempting to read a reference, but couldn't continue reading.");
			if(input.hasNext("\\)")) break;
			sb.append(input.next());
		}
		readCharacter(input,')');
		String reference = sb.toString().trim();
		if(!ReferenceData.isValidReference(reference)) throw new Exception(String.format("The term %s is not a valid reference.", reference));
		createOrChangeReferenceData(name,reference,environment);
	}
	
	private void createOrChangeReferenceData(String name, String reference, DataContainer environment) throws Exception{
		if(environment.containsName(name)){
			Data d = environment.getDataByName(name);
			if(!(d instanceof ReferenceData)) throw new Exception(String.format("A variable %s already exists and thus can not be loaded",d.getPath()));
			environment.removeData(d);
		}
		environment.addReferenceData(name, reference);
	}
	
	private void readTextData(String name, DataContainer environment, Scanner input) throws Exception{
		readWhiteSpace(input);
		readCharacter(input,'(');
		StringBuilder sb = new StringBuilder();
		while(true){
			if(!input.hasNext()) throw new Exception("Attempting to read text, but couldn't continue reading until the end.");
			if(input.hasNext("\\)")) break;
			sb.append(input.next());
		}
		readCharacter(input,')');
		createOrChangeReferenceData(name,sb.toString().trim(),environment);
	}
	
	private void createOrChangeTextData(String name, String text, DataContainer environment) throws Exception{
		if(environment.containsName(name)){
			Data d = environment.getDataByName(name);
			if(!(d instanceof TextData)) throw new Exception(String.format("A variable %s already exists and thus can not be loaded",d.getPath()));
			environment.removeData(d);
		}
		environment.addTextData(name, text);
	}
	
	private void readCalculationData(String name, DataContainer environment, Scanner input) throws Exception{
		readWhiteSpace(input);
		readCharacter(input,'(');
		StringBuilder sb = new StringBuilder();
		while(true){
			if(!input.hasNext()) throw new Exception("Attempting to read a calculation, but couldn't continue reading.");
			if(input.hasNext("\\)")) break;
			sb.append(input.next());
		}
		readCharacter(input,')');
		createOrChangeCalculationData(name,sb.toString().trim(),environment);
	}
	
	private void createOrChangeCalculationData(String name, String calculation, DataContainer environment) throws Exception{
		if(environment.containsName(name)){
			Data d = environment.getDataByName(name);
			if(!(d instanceof CalculationData)) throw new Exception(String.format("A variable %s already exists and thus can not be loaded",d.getPath()));
			environment.removeData(d);
		}
		environment.addCalculationData(name, calculation);
	}
	
	private void readIntData(String name, DataContainer environment, Scanner input) throws Exception{
		readWhiteSpace(input);
		readCharacter(input,'(');
		readWhiteSpace(input);
		int number = readNumber(input);
		readWhiteSpace(input);
		readCharacter(input,')');
		createOrChangeIntData(name,number,environment);
	}
	
	private void createOrChangeIntData(String name, int value, DataContainer environment) throws Exception{
		if(environment.containsName(name)){
			Data d = environment.getDataByName(name);
			if(!(d instanceof IntData)) throw new Exception(String.format("A variable %s already exists and thus can not be loaded",d.getPath()));
			environment.removeData(d);
		}
		environment.addIntData(name, value);
	}
	
	private void readDataContainer(String name, DataContainer environment, Scanner input, PrintStream output) throws Exception {
		DataContainer dataContainer = createOrGetDataContainer(name,environment);
		readWhiteSpace(input);
		readCharacter(input,'(');
		while(true){
			readWhiteSpace(input);
			if(input.hasNext("\\)")) break;
			else if(input.hasNext("\\<")) readDataTag(input,dataContainer, output);
			else throw new Exception("Unexpected input, can not read.");
		}
		readCharacter(input,')');
	}
	
	private DataContainer createOrGetDataContainer(String name, DataContainer environment) throws Exception{
		if(environment.containsName(name)){
			Data d = environment.getDataByName(name);
			if(!(d instanceof DataContainer)) throw new Exception(String.format("A variable %s already exists and thus can not be loaded",d.getPath()));
			return (DataContainer) d;
		}
		return environment.addSubDataContainer(name);
	}
	
	private void readInstructionLine(Scanner input, Tracker program, PrintStream output) throws Exception{
		if(!input.hasNext("\\#")) throw new Exception("Attempting to read \'#\', but couldn't.");
		input.next();
		program.dispatchLine(input.nextLine(),output);
	}
	
	private void readWhiteSpace(Scanner input){
		while(input.hasNext("\\s")) input.next();
	}
	private String getExtension(String fileName) throws Exception{
		int i = fileName.lastIndexOf(".") + 1;
		if(i == 0) throw new Exception(String.format("Attempting to find the extension of %s, but it doesn't have one.",fileName));
		return fileName.substring(i);
	}

	private DataContainer getEnvironment(DataContainer environment,String s) throws Exception{
		if(s.length() == 0) return environment;
		s = getFirstArgument(s);
		Data d = Calculator.readVariable(s,environment);
		if(d == null) throw new Exception(String.format("No variable %s exists.",s));
		if(!(d instanceof DataContainer)) throw new Exception(String.format("The variable %s can not contain subvariables.",d.getPath()));
		return (DataContainer) d;
	}
	
	private String getFirstArgument(String s){
		Scanner scanner = new Scanner(s);
		String result = scanner.next();
		scanner.close();
		return result;
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
}
