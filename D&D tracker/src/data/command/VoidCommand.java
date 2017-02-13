package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class VoidCommand extends CommandValue {
	public static String COMMAND_WORD = "void";
	public static String USAGE_DESCRIPTION = "void [args]";

	@Override
	public Value copy() {
		return new VoidCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		for(int i = 0; i < args.length ; i++) args[i].evaluate(environment,new Value[0],output);
		return new VoidValue();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
