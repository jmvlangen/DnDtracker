package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.TextValue;
import data.Value;

/**
 * A command that returns the given value as text
 *
 */
public class TextCommand extends CommandValue{
	public static String COMMAND_WORD = "text";
	public static String USAGE_DESCRIPTION = "text <value>";

	@Override
	public Value copy() {
		return new TextCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 1) throw new EvaluationException(String.format("The \'text\' command needs at least one argument: %s",USAGE_DESCRIPTION));
		if(args[0] instanceof TextValue) return (TextValue) args[0];
		return new TextValue(args[0].toString());
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
