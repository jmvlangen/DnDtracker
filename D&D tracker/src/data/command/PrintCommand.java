package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.TextValue;
import data.Value;
import data.VoidValue;

public class PrintCommand extends CommandValue {
	public static String COMMAND_WORD = "print";
	public static String USAGE_DESCRIPTION = "print [val1] [val2] ...";

	@Override
	public Value copy() {
		return new PrintCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		for(int i = 0; i < args.length ; i++ ) printValue(args[i].evaluate(environment, args, output),output);
		return new VoidValue();
	}

	private void printValue(Value value, PrintStream output) {
		if(value instanceof TextValue) printTextValue((TextValue) value, output);
		else output.printf("%s\n",value.toString());
	}

	private void printTextValue(TextValue value, PrintStream output) {
		output.printf("%s\n",value.getText());
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}

}
