package data.command;

import java.io.PrintStream;

import data.BooleanValue;
import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class IfCommand extends CommandValue {
	public static String COMMAND_WORD = "if";
	public static String USAGE_DESCRIPTION = "if <condition> <value to be evaluated when true> [value to be evaluated when false]";

	@Override
	public Value copy() {
		return new IfCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 2) throw new EvaluationException(String.format("The \'if\' command needs at least two arguments: %s",USAGE_DESCRIPTION));
		PrimitiveValue condition = args[0].evaluate(environment, new Value[0], output);
		if(isFalse(condition)){
			if(args.length >= 3) return args[2].evaluate(environment, new Value[0], output);
			return new VoidValue();
		} else{
			return args[1].evaluate(environment, new Value[0], output);
		}
	}

	private boolean isFalse(PrimitiveValue condition) {
		return condition instanceof VoidValue || (condition instanceof BooleanValue && condition.equals(new BooleanValue(false)));
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
