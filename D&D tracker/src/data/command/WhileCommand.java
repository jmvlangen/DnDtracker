package data.command;

import java.io.PrintStream;

import data.BooleanValue;
import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class WhileCommand extends CommandValue {
	public static String COMMAND_WORD = "while";
	public static String USAGE_DESCRIPTION = "while <condition> <value to be evaluated whilst true>";
	public static int MAXIMAL_LOOP_SIZE = 1000;

	@Override
	public Value copy() {
		return new WhileCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 2) throw new EvaluationException(String.format("The \'while\' command needs at least two arguments: %s",USAGE_DESCRIPTION));
		PrimitiveValue condition = args[0].evaluate(environment, new Value[0], output);
		int count = 0;
		while(!isFalse(condition)){
			args[1].evaluate(environment, new Value[0], output);
			condition = args[0].evaluate(environment, new Value[0], output);
			count += 1;
			if(count > MAXIMAL_LOOP_SIZE) throw new EvaluationException(String.format("Reached maximal number of loops (%d).",MAXIMAL_LOOP_SIZE));
		} 
		return new VoidValue();
	}

	private boolean isFalse(PrimitiveValue condition) {
		return condition instanceof VoidValue || (condition instanceof BooleanValue && condition.equals(new BooleanValue(false)));
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
