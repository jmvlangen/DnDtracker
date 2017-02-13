package data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A Value object that represents a Value that should be evaluated with specific arguments.
 */
public class CompositeValue implements Value {
	public static final String[] VALUE_TYPE_NAMES = {"composite","function","fun"};
	
	private Value value;
	private Value[] args;

	public CompositeValue(Value value, Value[] args){
		this.value = value;
		this.args = args;
	}
	
	public CompositeValue(Value value, List<Value> args){
		this(value,args.toArray(new Value[args.size()]));
	}
	
	@Override
	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}

	@Override
	public Value copy() {
		Value[] argsCopy = new Value[args.length];
		for(int i = 0 ; i < args.length ; i++) argsCopy[i] = args[i].copy();
		return new CompositeValue(value.copy(),argsCopy);
	}

	@Override
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		Value[] replacementArgs = new Value[this.args.length];
		for(int i = 0; i < replacementArgs.length; i++) replacementArgs[i] = this.args[i].replaceArgumentsBy(args);
		return value.evaluate(environment, replacementArgs, output);
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof CompositeValue){
			CompositeValue other = (CompositeValue) o;
			return value.compareTo(other.value);
		}
		return getTypeName().compareTo(o.getTypeName());
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(value.toString()).append("(");
		for(int i = 0; i < args.length ; i++){
			if(i > 0) sb.append(",");
			sb.append(args[i].toString());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		List<Value> replacementArgs = new ArrayList<Value>();
		for(int i = 0; i < this.args.length; i++) replacementArgs.add(this.args[i].replaceArgumentsBy(args));
		return new CompositeValue(value.replaceArgumentsBy(args),replacementArgs);
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof CompositeValue)) return false;
		CompositeValue o = (CompositeValue) other;
		if(!value.equals(o.value) || args.length != o.args.length) return false;
		for(int i = 0; i < args.length ; i++){
			if(!args[i].equals(o.args[i])) return false;
		}
		return true;
	}
}
