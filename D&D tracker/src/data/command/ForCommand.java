package data.command;

import java.io.PrintStream;

import data.BooleanValue;
import data.DataContainer;
import data.DataPair;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class ForCommand extends CommandValue {
	public static String COMMAND_WORD = "for";
	public static String USAGE_DESCRIPTION = "for <collection> <value to be evaluated> [use hidden variables]";
	public static int MAXIMAL_LOOP_SIZE = 1000;

	@Override
	public Value copy() {
		return new ForCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length < 2) throw new EvaluationException(String.format("The \'while\' command needs at least two arguments: %s",USAGE_DESCRIPTION));
		DataContainer dataContainer = args[0].evaluateToFirstDataContainer(environment, new Value[0], output);
		boolean loopOverHidden = false;
		if(args.length >= 3) loopOverHidden = !isFalse(args[2].evaluate(environment, new Value[0], output));
		for(DataPair data : dataContainer) {
			if(loopOverHidden || !data.isHidden()){
				Value[] myArgs = new Value[1];
				myArgs[0] = data.getPath().toValue();
				args[1].evaluate(dataContainer, myArgs, output);
			}
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
