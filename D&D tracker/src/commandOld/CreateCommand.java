package command;

import java.io.PrintStream;
import java.util.Scanner;

import data.Data;
import data.DataContainer;
import data.ReferenceData;
import main.Calculator;
import main.Tracker;

public class CreateCommand implements Command {
	private static final String COMMAND_WORD = "create";
	private static final int NUMBER_OF_ARGUMENTS = 2;
	private static final String USAGE_DESCRIPTION =
			"create variable <name> <value>\n"
					+"create function <name> <description>\n"
					+"create collection <name>\n"
					+"create command <name> <number of arguments> <formatted command line>\n"
					+"create reference <name> <variable reference>";
	private static final String[] KEY_WORDS = {"variable","function","collection","command","reference","var","fun","col","com","ref"};

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

	private String[] splitIntoNames(String string){
		Scanner scanner = new Scanner(string);
		String[] result = splitIntoNames(scanner);
		scanner.close();
		return result;
	}
	
	private String[] splitIntoNames(Scanner input){
		input.useDelimiter("");
		StringBuilder environment = new StringBuilder();
		StringBuilder name = new StringBuilder();
		while(input.hasNext()){
			if(input.hasNext("[\\.\\:]")){
				name.append(input.next());
				environment.append(name.toString());
				name = new StringBuilder();
			} else {
				name.append(input.next());
			}
		}
		String[] result = {environment.toString(),name.toString()};
		return result;
	}
	
	private void createVariable(String name,String calculation,Tracker program) throws Exception{
		String[] names = splitIntoNames(name);
		DataContainer environment = getEnvironment(names[0],program.currentContainer);
		name = names[1];
		int value = Calculator.calculate(calculation, program.currentContainer);
		if(!isValidName(name)) throw new Exception(String.format("%s is no valid name for a variable", name));
		if(environment.addIntData(name, value) == null) throw new Exception(String.format("Could not create variable %s inside %s", name,environment.getName()));
	}

	private boolean isValidName(String name) {
		if(name.length() <= 0) return false;
		if(!isLetter(name.charAt(0))) return false;
		for(int i = 1 ; i < name.length() ; i++){
			if(!isAlphaNumeric(name.charAt(i))) return false;
		}
		return true;
	}

	private boolean isLetter(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}
	
	private boolean isDigit(char c){
		return '0' <= c && c <= '9';
	}
	
	private boolean isAlphaNumeric(char c){
		return isLetter(c) || isDigit(c);
	}

	private DataContainer getEnvironment(String string, DataContainer environment) throws Exception{
		if(string.equals(":")) return environment.getTopLevel();
		if(string.equals("")) return environment;
		String s = cutDotsAtEnd(string);
		int numberOfDots = string.length() - s.length();
		if(numberOfDots < 1) throw new Exception("Incorrect definition of a variable");
		Data d = s.length() == 0 ? environment : Calculator.readVariable(s, environment);
		if(d == null) throw new Exception(String.format("Variable %s does not exist.", s));
		if(!(d instanceof DataContainer)) throw new Exception(String.format("Variable %s can contain no subvariables.",d.getName()));
		return getCorrectSubEnvironment((DataContainer) d,numberOfDots);
	}
	
	private DataContainer getCorrectSubEnvironment(DataContainer environment, int numberOfDots) {
		boolean flag = false;
		for(int i = 0; i < numberOfDots ; i++){
			if(flag) environment = environment.getHost();
			flag = true;
		}
		return environment;
	}

	private String cutDotsAtEnd(String string) {
		while(string.endsWith(".")) string = string.substring(0, string.length()-1);
		return string;
	}

	private void createFunction(String name,String calculation,Tracker program) throws Exception{
		String[] names = splitIntoNames(name);
		DataContainer environment = getEnvironment(names[0],program.currentContainer);
		name = names[1];
		if(!isValidName(name)) throw new Exception(String.format("%s is no valid name for a variable", name));
		if(environment.addCalculationData(name, calculation) == null) throw new Exception(String.format("Could not create variable %s inside %s", name,environment.getName()));
	}
	
	private void createCollection(String name, Tracker program) throws Exception{
		String[] names = splitIntoNames(name);
		DataContainer environment = getEnvironment(names[0],program.currentContainer);
		name = names[1];
		if(!isValidName(name)) throw new Exception(String.format("%s is no valid name for a variable", name));
		if(environment.addSubDataContainer(name) == null) throw new Exception(String.format("Could not create variable %s inside %s", name,environment.getName()));
	}

	@Override
	public void performCommand(String[] args, Tracker program, PrintStream output) {
		try{
			switch(getRequestedFunction(args[0])){
			case 0:
				createVariable(args[1],args[2],program);
				output.printf("Variable %s created.\n",args[1]);
				break;
			case 1:
				createFunction(args[1],args[2],program);
				output.printf("Function %s created.\n",args[1]);
				break;
			case 2:
				createCollection(args[1],program);
				output.printf("Collection %s created.\n",args[1]);
				break;
			case 3:
				createCommand(args[1],args[2],program);
				output.printf("Command %s created.\n",args[1]);
				break;
			case 4:
				createReference(args[1],args[2],program);
				output.printf("Reference %s created.\n",args[1]);
				break;
			default:
				output.printf("%s\n", usageDescription());
			}
		} catch(Exception e){
			output.printf("Error: %s\n", e.getMessage());
		}
	}

	private void createReference(String name, String value, Tracker program) throws Exception{
		String[] names = splitIntoNames(name);
		DataContainer environment = getEnvironment(names[0],program.currentContainer);
		name = names[1];
		if(!isValidName(name)) throw new Exception(String.format("%s is no valid name for a variable", name));
		if(!ReferenceData.isValidReference(value)) throw new Exception(String.format("%s is not a valid reference.", value));
		if(environment.addReferenceData(name, value) == null) throw new Exception(String.format("Could not create variable %s inside %s", name,environment.getName()));
	}

	private void createCommand(String commandWord, String args, Tracker program) throws Exception {
		if(!isCommandWord(commandWord)) throw new Exception(String.format("%s can not be used as a command word.", commandWord));
		commandWord = commandWord.toLowerCase();
		if(program.isCommand(commandWord)) throw new Exception(String.format("%s is already a command.", commandWord));
		Scanner argsScanner = new Scanner(args);
		if(!argsScanner.hasNextInt()) {
			argsScanner.close();
			throw new Exception("Could not read number of arguments.");
		}
		int numberOfArguments = argsScanner.nextInt();
		if(!argsScanner.hasNext()) {
			argsScanner.close();
			throw new Exception("Command has no arguments assigned to it.");
		}
		String text = argsScanner.nextLine();
		argsScanner.close();
		if(!TextCommand.isValidText(text, numberOfArguments)) throw new Exception("The given command is not valid.");
		program.commandManager.addCommand(new TextCommand(commandWord,numberOfArguments,text));
	}

	private boolean isCommandWord(String commandWord) {
		for(int i = 0 ; i < commandWord.length(); i++){
			if(!isLetter(commandWord.charAt(i))) return false;
		}
		return true;
	}

	private int getRequestedFunction(String arg) {
		for(int i = 0; i < KEY_WORDS.length ; i++){
			if(KEY_WORDS[i].equalsIgnoreCase(arg)) return i % 5;
		}
		return -1;
	}

}
