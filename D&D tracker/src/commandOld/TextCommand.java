package command;

import java.io.PrintStream;
import java.util.Scanner;
import main.Tracker;

/**
 * A TextCommand can be used as a shortcut to type multiple pieces of information at once.
 *
 */
public class TextCommand implements Command {
	String commandWord;
	int numberOfArguments;
	String text;
	
	public TextCommand(String commandWord, int numberOfArguments, String text){
		this.commandWord = commandWord;
		this.numberOfArguments = numberOfArguments;
		this.text = text;
	}

	@Override
	public String getCommandWord() {
		return commandWord;
	}

	@Override
	public int getNumberOfArguments() {
		return numberOfArguments;
	}

	@Override
	public String usageDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(commandWord);
		for(int i = 1; i <= numberOfArguments ; i++) sb.append(" <arg").append(i).append(">");
		return sb.toString();
	}
	
	public static boolean isValidText(String text,int numberOfArguments){
		Scanner scanner = new Scanner(text);
		boolean v = isValidText(numberOfArguments,scanner);
		scanner.close();
		return v;
	}

	private static boolean isValidText(int numberOfArguments, Scanner scanner) {
		scanner.useDelimiter("");
		int count = 0;
		while(scanner.hasNext()){
			count += 1;
			String c = scanner.next();
			if(c.equals("<")){
				scanner.useDelimiter(">");
				if(!scanner.hasNextInt()) return false;
				int i = scanner.nextInt();
				if(i < 0 || i > numberOfArguments) return false;
				scanner.useDelimiter("");
				if(!(scanner.hasNext() && scanner.next().equals(">"))) return false;
			}
			else if(c.equals("&")) count = 0;
		}
		return count != 0;
	}

	@Override
	public void performCommand(String[] args, Tracker program, PrintStream output) {
		Scanner scanner = new Scanner(text);
		try{
			scanner.useDelimiter("");
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNext()){
				String c = scanner.next();
				if(c.equals("<")){
					insertArgument(args, scanner, sb);
				} else if(c.equals("&")){
					program.dispatchLine(sb.toString(), output);
					sb = new StringBuilder();
				} else sb.append(c);
			}
			program.dispatchLine(sb.toString(), output);
		} catch(Exception e){
			output.printf("Error: %s\n",e.getMessage());
		}
		scanner.close();
	}

	private void insertArgument(String[] args, Scanner scanner, StringBuilder sb) throws Exception {
		scanner.useDelimiter(">");
		sb.append(args[scanner.nextInt()-1]);
		scanner.useDelimiter("");
		if(!(scanner.hasNext() && scanner.next().equals(">"))) throw new Exception("Could not read closing symbol >");
	}

}
