package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;
import main.Tracker;

public class ClearCommand extends CommandValue {
	public static String COMMAND_WORD = "clear";
	public static String USAGE_DESCRIPTION = "clear";
	
	@Override
	public Value copy() {
		return new ClearCommand();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		Tracker.mainInstance.screen.clearScreen();
		return new VoidValue();
	}

}
