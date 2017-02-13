package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;
import main.Tracker;

public class QuitCommand extends CommandValue {
	public static String COMMAND_WORD = "quit";
	public static String USAGE_DESCRIPTION = "quit";

	@Override
	public Value copy() {
		return new QuitCommand();
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		Tracker.mainInstance.quit();
		return new VoidValue();
	}
	
	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}

}
