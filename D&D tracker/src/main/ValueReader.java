package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import data.ArgumentValue;
import data.BooleanValue;
import data.ComparisonValue;
import data.CompositeValue;
import data.CurrentDataContainerValue;
import data.DataContainer;
import data.DataException;
import data.DiceValue;
import data.DivisionValue;
import data.GlobalValue;
import data.IntValue;
import data.InterpretedValue;
import data.NamedValue;
import data.PreEvaluatedValue;
import data.ProductValue;
import data.ReferenceValue;
import data.SubVariableValue;
import data.SubtractionValue;
import data.SumValue;
import data.TextValue;
import data.Value;
import data.VoidValue;

/**
 * A class that gives the basic implementation of reading Values from Scanner and String objects.
 */
public class ValueReader {
	private static final char[] DICE_CHARACTERS = {'D','d'};
	private static final char[] DICE_LOW_CHARACTERS = {'L','l'};
	private static final char[] DICE_HIGH_CHARACTERS = {'H','h'};

	public ValueReader(){
	}

	public Value readValue(String string) throws ReadingException {
		Scanner scanner = new Scanner(string);
		Value result = readValue(scanner);
		scanner.close();
		return result;
	}

	public Value readValue(Scanner input) throws ReadingException {
		Pattern originalDelimiter = input.delimiter();
		input.useDelimiter("\\s*");
		Value result = readPossibleEquality(input);
		input.useDelimiter(originalDelimiter);
		return result;
	}
	
	public Value readPossibleEquality(Scanner input) throws ReadingException {
		Value result = readSum(input);
		if(input.hasNext("\\=")){
			readCharacter(input, '=');
			return new ComparisonValue(result,readSum(input));
		}
		return result;
	}

	public Value readSum(Scanner input) throws ReadingException {
		Value result = readProduct(input);
		while(input.hasNext("[\\+\\-]")){
			if(input.hasNext("\\+")){
				readCharacter(input,'+');
				result = new SumValue(result,readProduct(input));
			}
			else if(input.hasNext("\\-")) {
				readCharacter(input,'-');
				result = new SubtractionValue(result,readProduct(input));
			} else{
				throw new ReadingException("Attempting to read \'+\' or \'-\', but found nothing.");
			}
		}
		return result;
	}

	public Value readProduct(Scanner input) throws ReadingException {
		Value result = readPossibleDice(input);
		while(input.hasNext("[\\*\\/]")){
			if(input.hasNext("\\*")){
				readCharacter(input,'*');
				result = new ProductValue(result,readPossibleDice(input));
			} else if(input.hasNext("\\/")) {
				readCharacter(input,'/');
				result = new DivisionValue(result,readPossibleDice(input));
			} else{
				throw new ReadingException("Attempting to read \'*\' or \'/\', but found nothing.");
			}
		}
		return result;
	}

	public Value readPossibleDice(Scanner input) throws ReadingException {
		Value amount = readSubValueTree(input);
		if(input.hasNext("[Dd]")){
			readOneOfCharacters(input,DICE_CHARACTERS);
			Value size = readSubValueTree(input);
			if(input.hasNext("[Ll]")){
				readOneOfCharacters(input,DICE_LOW_CHARACTERS);
				return new DiceValue(amount,size,readSubValueTree(input),true);
			} else if(input.hasNext("[Hh]")){
				readOneOfCharacters(input,DICE_HIGH_CHARACTERS);
				return new DiceValue(amount,size,readSubValueTree(input),false);
			} else return new DiceValue(amount, size);
		}
		return amount;
	}

	private Value readSubValueTree(Scanner input) throws ReadingException {
		Value result;
		boolean isGlobal = false;
		input.skip("\\s*");
		if(input.hasNext("\\:")){
			result = readGlobalValue(input);
			isGlobal = true;
		}
		else if(input.hasNext("\\.")) result = new CurrentDataContainerValue();
		else result = readTermWithPossibleArguments(input);
		Pattern oldDelimiter = input.delimiter();
		input.useDelimiter("");
		if(isGlobal || input.hasNext("\\.")){
			if(!(result instanceof ReferenceValue)) throw new ReadingException(String.format("Can not access subvariables of \'%s\'.",result.toString()));
			if(!isGlobal) readCharacter(input, '.');
			int level = 0;
			while(input.hasNext("\\.")){
				readCharacter(input, '.');
				level += 1;
			}
			if(input.hasNext("\\s") || !input.hasNext()){
				input.useDelimiter(oldDelimiter);
				return new SubVariableValue((ReferenceValue) result, level);
			}
			input.useDelimiter(oldDelimiter);
			return new SubVariableValue((ReferenceValue) result,readSubValueTree(input),level);
		}
		input.useDelimiter(oldDelimiter);
		return result;
	}

	private Value readGlobalValue(Scanner input) throws ReadingException {
		readCharacter(input, ':');
		return new GlobalValue();
	}
	
	private Value readTermWithPossibleArguments(Scanner input) throws ReadingException {
		Value result = readTerm(input);
		Pattern oldDelimiter = input.delimiter();
		input.useDelimiter("");
		if(input.hasNext("\\(")){
			input.useDelimiter(oldDelimiter);
			return readArgumentsForValue(input,result);
		}
		input.useDelimiter(oldDelimiter);
		return result;
	}

	private Value readTerm(Scanner input) throws ReadingException {
		if(input.hasNext("\\(")) return readBracketTerm(input);
		if(input.hasNext("\\[")) return readPreEvaluatedTerm(input);
		if(input.hasNext("[0-9\\-]")) return readInteger(input);
		if(input.hasNext("[a-zA-Z\\_]")) return readName(input);
		if(input.hasNext("\\\"")) return readText(input);
		if(input.hasNext("\\#")) return readArgument(input);
		if(input.hasNext("\\$")) return readBooleanValue(input);
		if(input.hasNext("\\{")) return readDataContainer(input);
		if(input.hasNext("\\<")) return readInterpretedTerm(input);
		if(input.hasNext()) throw new ReadingException(String.format("Attempting to read a term at \'%s\', but no term found.", input.nextLine()));
		throw new ReadingException("Attempting to read a term, but encountered the end of the input.");
	}

	private BooleanValue readBooleanValue(Scanner input) throws ReadingException {
		readCharacter(input,'$');
		Pattern oldDelimiter = input.delimiter();
		input.useDelimiter("");
		if(input.hasNext("\\s")) throw new ReadingException("May not contain whitespace after \'$\'.");
		input.useDelimiter(oldDelimiter);
		String text = readNameString(input);
		if(text.equalsIgnoreCase(BooleanValue.TRUE_TEXT)) return new BooleanValue(true);
		if(text.equalsIgnoreCase(BooleanValue.FALSE_TEXT)) return new BooleanValue(false);
		throw new ReadingException(String.format("Only \'$%s\' and \'$%s\' are allowed, not \'$%s\'.", BooleanValue.TRUE_TEXT,BooleanValue.FALSE_TEXT,text));
	}

	private InterpretedValue readInterpretedTerm(Scanner input) throws ReadingException {
		readCharacter(input,'<');
		Value value = readValue(input);
		readCharacter(input,'>');
		return new InterpretedValue(value);
	}

	private DataContainer readDataContainer(Scanner input) throws ReadingException {
		readCharacter(input,'{');
		DataContainer result = new DataContainer();
		int i = 0;
		while(!input.hasNext("\\}")){
			if(i > 0) readCharacter(input,',');
			String name = readNameString(input);
			readCharacter(input,'=');
			try {
				result.addData(name, readValue(input));
			} catch (DataException e) {
				throw new ReadingException(String.format("Can not read variable \'%s\', since: %s",name,e.getMessage()),e);
			}
			i += 1;
		}
		readCharacter(input,'}');
		return result;
	}

	private Value readArgument(Scanner input) throws ReadingException {
		input.useDelimiter("");
		input.skip("\\s*");
		readCharacter(input, '#');
		IntValue number = readInteger(input);
		input.useDelimiter("\\s*");
		if(number.getLong() < 1 || number.getLong() > Integer.MAX_VALUE) throw new ReadingException(String.format("The number %s is invalid as an argument number.",number.toString()));
		return new ArgumentValue((int) number.getLong());
	}

	private Value readArgumentsForValue(Scanner input, Value value) throws ReadingException {
		readCharacter(input,'(');
		List<Value> args = new ArrayList<Value>();
		int i = 0;
		while(!input.hasNext("\\)")){
			if(i > 0) readCharacter(input,',');
			args.add(readValue(input));
			i += 1;
		}
		readCharacter(input,')');
		return new CompositeValue(value,args);
	}
	private NamedValue readName(Scanner input) throws ReadingException {
		return new NamedValue(readNameString(input));
	}

	private String readNameString(Scanner input) throws ReadingException {
		Pattern oldPattern = input.delimiter();
		input.skip("\\s*");
		input.useDelimiter("");
		StringBuilder sb = new StringBuilder ();
		sb.append(readLetterOrLowerDash(input));
		while(input.hasNext("[0-9a-zA-Z]")) sb.append(readAlphaNumericalCharacter(input));
		input.useDelimiter(oldPattern);
		return sb.toString();
	}

	private Value readText(Scanner input) throws ReadingException {
		input.skip("\\s*");
		input.useDelimiter("");
		readCharacter(input,'\"');
		StringBuilder sb = new StringBuilder();
		while(!input.hasNext("\"")){
			if(!input.hasNext()) break;
			String s = input.next();
			if(s.length() != 1) throw new ReadingException(String.format("Attempting to read a character, but read \'%s\'",s));
			char c = s.charAt(0);
			if(c == '\\'){
				if(!input.hasNext()) throw new ReadingException("Attempting to read a character, but the input ended.");
				s = input.next();
				if(s.length() != 1) throw new ReadingException(String.format("Attempting to read a character, but read \'%s\'",s));
				c = s.charAt(0);
				if(c == 'n') c = '\n';
			}
			sb.append(c);
		}
		readCharacter(input,'\"');
		input.useDelimiter("\\s*");
		return new TextValue(sb.toString());
	}

	private Value readBracketTerm(Scanner input) throws ReadingException {
		readCharacter(input,'(');
		if(input.hasNext("\\)")){
			readCharacter(input,')');
			return new VoidValue();
		}
		Value result = readValue(input);
		readCharacter(input,')');
		return result;
	}

	private Value readPreEvaluatedTerm(Scanner input) throws ReadingException {
		int delay = 0;
		while(input.hasNext("\\[")){
			delay += 1;
			readCharacter(input,'[');
		}
		Value unEvaluatedResult = readValue(input);
		for(int i = 0; i < delay; i++) readCharacter(input,']');
		return new PreEvaluatedValue(unEvaluatedResult,delay);
	}

	private IntValue readInteger(Scanner input) throws ReadingException {
		Pattern oldPattern = input.delimiter();
		input.useDelimiter("");
		input.skip("\\s*");
		boolean negative = input.hasNext("\\-");
		if(negative) readCharacter(input, '-');
		long resultNumber = 0;
		try{
			do{
				resultNumber = Math.addExact(Math.multiplyExact(10, resultNumber),readDigit(input));
			} while(input.hasNext("[0-9]"));
			if(negative) resultNumber = Math.multiplyExact(-1, resultNumber);
		} catch(ArithmeticException e){
			throw new ReadingException("Attempting to read an integer that is too big.",e);
		}
		input.useDelimiter(oldPattern);
		return new IntValue(resultNumber);
	}

	private int readDigit(Scanner input) throws ReadingException {
		if(!input.hasNext()) throw new ReadingException("Attempting to read a digit, but reached the end of the input.");
		String s = input.next();
		if(!isDigit(s)) throw new ReadingException(String.format("Attempting to read a digit, but read \'%s\'",s));
		return (int) (s.charAt(0) - '0');
	}
	
	private char readLetterOrLowerDash(Scanner input) throws ReadingException {
		if(!input.hasNext()) throw new ReadingException("Attempting to read a letter, but reached the end of the input.");
		String s = input.next();
		if(!isLetterOrLowerDash(s)) throw new ReadingException(String.format("Attempting to read a letter, but read \'%s\'",s));
		return s.charAt(0);
	}
	
	private boolean isLetterOrLowerDash(String s) {
		return isLetter(s) || isLowerDash(s);
	}

	private boolean isLowerDash(String s) {
		return s.length() == 1 && s.charAt(0) == '_';
	}

	private char readAlphaNumericalCharacter(Scanner input) throws ReadingException {
		if(!input.hasNext()) throw new ReadingException("Attempting to read an alpha-numerical character, but reached the end of the input.");
		String s = input.next();
		if(!isAlphaNumericalCharacter(s)) throw new ReadingException(String.format("Attempting to read an alpha-numerical character, but read \'%s\'",s));
		return s.charAt(0);
	}
	
	private boolean isDigit(String s){
		return s.length() == 1 && s.charAt(0) >= '0' && s.charAt(0) <= '9';
	}
	
	private boolean isLetter(String s){
		return s.length() == 1 && ((s.charAt(0) >= 'a' && s.charAt(0) <= 'z') || (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z'));
	}
	
	private boolean isAlphaNumericalCharacter(String s){
		return isLetter(s) || isDigit(s);
	}

	public void readCharacter(Scanner input, char c) throws ReadingException{
		if(!input.hasNext()) throw new ReadingException(String.format("Attempting to read \'%c\', but reached the end of the input.",c));
		String s = input.next();
		if(s.length() != 1 || s.charAt(0) != c) throw new ReadingException(String.format("Attempting to read \'%c\', but read \'%s\'",c,s));
	}

	private void readOneOfCharacters(Scanner input, char[] c) throws ReadingException{
		if(!input.hasNext()) throw new ReadingException(String.format("Attempting to read \'%c\', but reached the end of the input.",c));
		String s = input.next();
		if(s.length() == 1){
			for(int i = 0;i < c.length;i++){
				if(s.charAt(0) == c[i]) return;
			}
		}
		if(c.length == 1) throw new ReadingException(String.format("Attempting to read \'%c\', but read \'%s\'",c[0],s));
		StringBuilder sb = new StringBuilder();
		sb.append("\'").append(c[0]).append("\'");
		for(int i = 1; i < c.length - 1; i++) sb.append(", \'").append(c[i]).append("\'");
		sb.append(" or \'").append(c[c.length - 1]).append("\'");
		throw new ReadingException(String.format("Attempting to read %s, but read \'%s\'",sb.toString(),s));
	}
}

