package main;

import java.util.Random;
import java.util.Scanner;

import data.*;

public abstract class Calculator {
	
	public static int calculate(String input,DataContainer environment) throws Exception{
		if(input.length() == 0) throw new Exception("Can not calculate an empty string.");
		return readCalculation(new Scanner(input), environment);
	}

	private static void prepareScannerForReading(Scanner input) {
		input.useDelimiter("");
		readWhiteSpace(input);
	}

	private static void readWhiteSpace(Scanner input){
		while(input.hasNext("\\s")) input.next();
	}

	private static int readCalculation(Scanner input, DataContainer environment) throws Exception{
		prepareScannerForReading(input);
		return readSum(input,environment);
	}

	private static int readSum(Scanner input,DataContainer environment) throws Exception{
		readWhiteSpace(input);
		int result = readProduct(input,environment);
		readWhiteSpace(input);
		while(input.hasNext("[+-]")){
			String plus = input.next();
			if(plus.equals("+")) result += readProduct(input,environment);
			else if(plus.equals("-")) result -= readProduct(input,environment);
			else throw new Exception("Expected addition \'+\' or substraction \'-\', but none found");
			readWhiteSpace(input);
		}
		return result;
	}

	private static int readProduct(Scanner input,DataContainer environment) throws Exception{
		readWhiteSpace(input);
		int result = readPossibleDice(input,environment);
		readWhiteSpace(input);
		while(input.hasNext("[*/]")){
			String plus = input.next();
			if(plus.equals("*")) result *= readPossibleDice(input,environment);
			else if(plus.equals("/")){
				int d = readPossibleDice(input,environment);
				if(d == 0) throw new Exception("Attempting to divide by zero! Aborting calculation");
				result = result/d;
			}
			else throw new Exception("Expected multiplication \'*\' or substraction \'/\', but none found");
			readWhiteSpace(input);
		}
		return result;
	}

	private static int readPossibleDice(Scanner input,DataContainer environment) throws Exception {
		int r = readFactor(input,environment);
		readWhiteSpace(input);
		if(input.hasNext("[dD]")){
			String d = input.next();
			if(!(d.equals("d") || d.equals("D"))) throw new Exception("Attempting to read \'d\', but couldn't read it.");
			r = rollDice(r,readFactor(input,environment));
		}
		return r;
	}

	private static int rollDice(int amount, int sides){
		Random r = new Random(System.currentTimeMillis());
		int result = 0;
		for(int i = 0; i < amount; i++){
			result += r.nextInt(sides) + 1;
		}
		return result;
	}

	private static int readFactor(Scanner input,DataContainer environment) throws Exception{
		readWhiteSpace(input);
		if(input.hasNext("\\(")) return readBracketFactor(input,environment);
		if(input.hasNext("[a-zA-Z0-9\\:\\.]")) return readNumberOrVariable(input,environment);
		throw new Exception("Attempting to read a factor, but couldn't find the start of one.");
	}

	private static int readBracketFactor(Scanner input,DataContainer environment) throws Exception {
		readWhiteSpace(input);
		if(!input.next().equals("(")) throw new Exception("Attempting to read \'(\', but couldn't read it");
		int result = readSum(input,environment);
		readWhiteSpace(input);
		if(!input.next().equals(")")) throw new Exception("Attempting to read \')\', but couldn't read it");
		return result;
	}
	
	private static int readNumberOrVariable(Scanner input,DataContainer environment) throws Exception{
		readWhiteSpace(input);
		if(input.hasNext("\\d")) return readNumber(input);
		if(input.hasNext("[a-zA-Z\\:\\.]")) return readVariableCalculation(input,environment);
		throw new Exception("Attempting to read a number or variable, but couldn't.");
	}

	private static int readNumber(Scanner input) throws Exception{
		readWhiteSpace(input);
		int result = readDigit(input);
		while(input.hasNext("\\d")){
			result = result * 10 + readDigit(input);
		}
		return result;
	}

	private static int readDigit(Scanner input) throws Exception{
		String d = input.next();
		if(d.length() != 1 || !Character.isDigit(d.charAt(0))) throw new Exception("Attempting to read a digit, but couldn't read it.");
		return Integer.parseInt(d);
	}
	
	private static int readVariableCalculation(Scanner input,DataContainer environment) throws Exception{
		readWhiteSpace(input);
		if(input.hasNext("\\:")) return readGlobalCalculation(input,environment);
		if(input.hasNext("\\.")) return readRelativeCalculation(input,environment);
		if(input.hasNext("[a-zA-Z]")){
			Data data = readVariableName(input,environment);
			if(input.hasNext("\\.")){
				if(data instanceof DataContainer) return readRelativeCalculation(input,(DataContainer) data);
				throw new Exception(String.format("The variable %s does not contain any subvariables",data.getName()));
			}
			return getVariableValue(data,environment);
		}
		throw new Exception("Attempting to read a variable, but could not find the start of one.");
	}
	
	private static int readGlobalCalculation(Scanner input, DataContainer environment) throws Exception {
		if(!input.hasNext("\\:")) throw new Exception("Attempting to read \':\', but could not read it");
		input.next();
		return readFactor(input,environment.getTopLevel());
	}
	
	private static int readRelativeCalculation(Scanner input, DataContainer environment) throws Exception {
		if(!input.hasNext("\\.")) throw new Exception("Attempting to read \'.\', but could not read it");
		boolean flag = false;
		while(input.hasNext("\\.")){
			if(flag) environment = environment.getHost();
			input.next();
			flag = true;
		}
		return readFactor(input,environment);
	}

	public static Data readVariable(Scanner input,DataContainer environment) throws Exception{
		prepareScannerForReading(input);
		if(input.hasNext("\\:")) return readGlobalVariable(input,environment);
		if(input.hasNext("\\.")) return readRelativeVariable(input,environment);
		if(input.hasNext("[a-zA-Z]")){
			Data data = readVariableName(input,environment);
			if(input.hasNext("\\.")){
				if(data instanceof DataContainer) return readRelativeVariable(input,(DataContainer) data);
				throw new Exception(String.format("The variable %s does not contain any subvariables",data.getName()));
			}
			return data;
		}
		throw new Exception("Attempting to read a variable, but no valid starting point found");
	}
	
	private static Data readRelativeVariable(Scanner input,DataContainer environment) throws Exception{
		if(!input.hasNext("\\.")) throw new Exception("Attempting to read \'.\', but could not.");
		boolean flag = false;
		while(input.hasNext("\\.")){
			if(flag) environment = environment.getHost();
			input.next();
			flag = true;
		}
		return readVariable(input,environment);
	}
	
	private static Data readGlobalVariable(Scanner input,DataContainer environment) throws Exception{
		if(!input.hasNext("\\:")) throw new Exception("Attempting to read \':\', but could not.");
		input.next();
		return readVariable(input,environment.getTopLevel());
	}
	
	public static Data readVariableName(Scanner input, DataContainer environment) throws Exception{
		if(!input.hasNext("[a-zA-Z]")) throw new Exception("Attempting to read a variable name, but did not start with a letter.");
		StringBuilder sb = new StringBuilder();
		while(input.hasNext("[a-zA-Z0-9]")) sb.append(input.next());
		Data d = environment.getDataByName(sb.toString());
		if(d == null) throw new Exception(String.format("The variable %s does not exist in %s.", sb.toString(),environment.getName()));
		if(d instanceof ReferenceData) d = ((ReferenceData) d).getReferencedData(environment);
		return d;
	}
	
	private static int getVariableValue(Data data,DataContainer environment) throws Exception {
		if(data instanceof CalculationData) return getCalculationDataValue((CalculationData) data, environment);
		if(data instanceof IntData) return ((IntData) data).getInteger();
		throw new Exception((new StringBuilder()).append("The variable ").append(data.getName()).append(" is of no type of which a value can be determined.").toString());
	}

	private static int getCalculationDataValue(CalculationData data,DataContainer environment) throws Exception {
		return calculate(data.getCalculation(),environment);
	}

	public static Data readVariable(String s, DataContainer environment) throws Exception{
		Scanner scanner = new Scanner(s);
		Data d = readVariable(scanner,environment);
		scanner.close();
		return d;
	}
	
	
}
