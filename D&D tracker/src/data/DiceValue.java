package data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"diceroll","dice","d"};
	
	private Value numberOfRolls;
	private Value sizeOfDice;
	private boolean keepLowest;
	private boolean keepHighest;
	private Value numberToKeep;
	
	private DiceValue(Value numberOfRolls, Value sizeOfDice, boolean keepLowest, boolean keepHighest, Value numberToKeep){
		this.numberOfRolls = numberOfRolls;
		this.sizeOfDice = sizeOfDice;
		this.numberToKeep = numberToKeep;
		this.keepLowest = keepLowest;
		this.keepHighest = keepHighest;
	}
	
	public DiceValue(Value numberOfRolls, Value sizeOfDice){
		this(numberOfRolls,sizeOfDice,false,false,new VoidValue());
	}
	
	public DiceValue(Value numberOfRolls, Value sizeOfDice, Value numberToKeep, boolean keepLowest){
		this(numberOfRolls, sizeOfDice, keepLowest, !keepLowest, numberToKeep);
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new DiceValue(numberOfRolls.copy(),sizeOfDice.copy(),keepLowest,keepHighest,numberToKeep.copy());
	}
	
	/**
	 * Returns the result of rolling the dice.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		long numberOfRolls = getIntEvaluation(this.numberOfRolls,environment,args,output);
		if(numberOfRolls < 0 || numberOfRolls > Integer.MAX_VALUE) throw new EvaluationException(String.format("Can not roll %d number of dice.", numberOfRolls));
		long sizeOfDice = getIntEvaluation(this.sizeOfDice,environment,args,output);
		if(sizeOfDice <= 0 || sizeOfDice > Integer.MAX_VALUE) throw new EvaluationException(String.format("Can not roll a dice of size %d.", sizeOfDice));
		List<IntValue> diceRolls = getDiceRolls((int) numberOfRolls,(int) sizeOfDice);
		diceRolls.sort(null);
		long numberOfRollsToKeep = keepLowest || keepHighest ? getIntEvaluation(numberToKeep,environment,args,output) : 0;
		PrimitiveValue result;
		if(keepLowest) result = getSum(getLowest(diceRolls,numberOfRollsToKeep));
		else if(keepHighest) result = getSum(getHighest(diceRolls,numberOfRollsToKeep));
		else result = getSum(diceRolls);
		printDiceRolls(numberOfRolls,sizeOfDice,diceRolls,numberOfRollsToKeep,output);
		return result;
	}

	private void printDiceRolls(long numberOfRolls, long sizeOfDice, List<IntValue> diceRolls, long numberOfRollsToKeep, PrintStream output) {
		StringBuilder sb = new StringBuilder();
		if(keepLowest){
			for(int i = 0; i < numberOfRollsToKeep;i++) sb.append(" ").append(diceRolls.get(i).toString());
			for(int i = (int) numberOfRollsToKeep; i < diceRolls.size();i++) sb.append(" (").append(diceRolls.get(i).toString()).append(")");
		} else if(keepHighest){
			for(int i = 0; i < diceRolls.size() - numberOfRollsToKeep;i++) sb.append(" (").append(diceRolls.get(i).toString()).append(")");
			for(int i = (int) (diceRolls.size() - numberOfRollsToKeep); i < diceRolls.size();i++) sb.append(" ").append(diceRolls.get(i).toString());
		} else {
			for(int i = 0; i < diceRolls.size();i++) sb.append(" ").append(diceRolls.get(i).toString());
		}
		output.printf("Dice roll (%dD%d):%s\n",numberOfRolls,sizeOfDice,sb.toString());
	}

	private PrimitiveValue getSum(List<IntValue> diceRolls) throws EvaluationException{
		PrimitiveValue result = new VoidValue();
		try{
			for(int i = 0; i < diceRolls.size(); i++) result = result.add(diceRolls.get(i));
		} catch(ComputationException e){
			throw new EvaluationException(String.format("Can not evaluate: %s", e.getMessage()),e);
		}
		return result;
	}

	private List<IntValue> getLowest(List<IntValue> diceRolls, long numberToKeep) throws EvaluationException{
		if(numberToKeep < 0 || numberToKeep > diceRolls.size()) throw new EvaluationException(String.format("Can not keep the lowest %d dice rolls.", numberToKeep));
		return diceRolls.subList(0, (int) numberToKeep);
	}

	private List<IntValue> getHighest(List<IntValue> diceRolls, long numberToKeep) throws EvaluationException{
		if(numberToKeep < 0 || numberToKeep > diceRolls.size()) throw new EvaluationException(String.format("Can not keep the highest %d dice rolls.", numberToKeep));
		return diceRolls.subList(diceRolls.size() - ((int) numberToKeep),diceRolls.size());
	}

	private List<IntValue> getDiceRolls(int numberOfRolls, int sizeOfDice) {
		List<IntValue> result = new ArrayList<IntValue>();
		Random random = new Random(System.currentTimeMillis());
		for(int i = 0; i < numberOfRolls;i++){
			result.add(new IntValue(random.nextInt(sizeOfDice) + 1));
		}
		return result;
	}

	private long getIntEvaluation(Value value,DataContainer environment, Value[] args, PrintStream output) throws EvaluationException{
		Value result = value.evaluate(environment, args, output);
		if(!(result instanceof IntValue)) throw new EvaluationException(String.format("Can not perform a dice roll with %s", result.getTypeName()));
		return ((IntValue) result).getLong();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(numberOfRolls.toString()).append("D").append(sizeOfDice.toString());
		if(keepLowest) sb.append("L").append(numberToKeep.toString());
		if(keepHighest) sb.append("H").append(numberToKeep.toString());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof DiceValue){
			DiceValue other = (DiceValue) o;
			return numberOfRolls.compareTo(other.numberOfRolls);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new DiceValue(numberOfRolls.replaceArgumentsBy(args),sizeOfDice.replaceArgumentsBy(args),keepLowest,keepHighest,numberToKeep.replaceArgumentsBy(args));
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof DiceValue)) return false;
		DiceValue o = (DiceValue) other;
		return keepHighest == o.keepHighest && numberToKeep.equals(o.numberToKeep) && numberOfRolls.equals(o.numberOfRolls) && keepLowest == o.keepLowest && sizeOfDice.equals(o.sizeOfDice);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		throw new EvaluationException(String.format("A value of type \'%s\' does not evaluate to a collection", getTypeName()));
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output);
	}
}