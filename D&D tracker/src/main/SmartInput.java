package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JTextField;

import data.DataContainer;
import data.DataException;
import data.DataPair;
import data.Path;
import data.PathException;
import data.Value;

public class SmartInput implements KeyListener {
	private static final int LINE_MEMORY_SIZE = 100;
	
	public List<String> inputLines;
	public JTextField inputScreen;
	private List<String> currentLineSuggestions;
	private int suggestionIndex;

	public SmartInput(JTextField input){
		inputScreen = input;
		input.addKeyListener(this);
		inputLines = new ArrayList<String>();
		inputScreen.setFocusTraversalKeysEnabled(false);
		resetSuggestions();
	}

	public void addLineToMemory(String line){
		inputLines.add(0, line);
		if(inputLines.size() > LINE_MEMORY_SIZE) inputLines.remove(LINE_MEMORY_SIZE);
	}

	public void resetSuggestions(){
		suggestionIndex = -1;
	}

	private String firstStringNotStartingWith(String s) {
		if(s.length() == 0) return String.format("%c", Character.MAX_CODE_POINT);
		return s.substring(0, s.length()-1) + String.format("%c",s.charAt(s.length() - 1) + 1);
	}

	private boolean isLetter(char c) {
		return ('a' <= c && 'z' >= c) || ('A' <= c && 'Z' >= c); 
	}
	
	private boolean isDigit(char c){
		return '0' <= c && '9' >= c;
	}
	
	private boolean isAlphaNumeric(char c){
		return isLetter(c) || isDigit(c);
	}

	private void performNameCompletion() {
		String currentInput = inputScreen.getText();
		Path p = getLastPath(currentInput,Tracker.mainInstance.currentContainer);
		String nameStart;
		if(endsWithDotOrColon(currentInput)){
			nameStart = "";
		} else {
			try{
				nameStart = p.nameAt(p.depth()-1);
				p = p.subPath(p.depth() - 1);
			} catch(PathException e){
				nameStart = "";
			}
		}
		SortedSet<String> suggestions = getNameCollection(p).subSet(nameStart, firstStringNotStartingWith(nameStart));
		if(suggestions.size() > 0){
			String suggestion = getCommonStart(suggestions);
			addText(suggestion.substring(nameStart.length()));
		}
	}

	private void addText(String s) {
		inputScreen.setText(inputScreen.getText() + s);
	}

	private String getCommonStart(SortedSet<String> suggestions) {
		String result = suggestions.first();
		while(result.length() > 0 && suggestions.size() > suggestions.subSet(result, firstStringNotStartingWith(result)).size()) result = result.substring(0,result.length()-1);
		return result;
	}

	private SortedSet<String> getNameCollection(Path p) {
		SortedSet<String> result = new TreeSet<String>();
		for(int i = 0; i <= p.depth(); i ++){
			try{
				Value value = p.subPath(i).getLowestValue();
				if(value instanceof DataContainer){
					for(DataPair d : (DataContainer) value) result.add(d.getName());
				}
			} catch(DataException e){
				break;
			}
		}
		return result;
	}

	private boolean endsWithDotOrColon(String s) {
		return s.length() > 0 && (s.charAt(s.length() - 1) == ':' || s.charAt(s.length() - 1) == '.');
	}

	private Path getLastPath(String s, DataContainer currentContainer) {
		String pathString = getLastPathString(s);
		Path result = currentContainer.getPath();
		for(int i = 0;i < pathString.length();i++){
			char c = pathString.charAt(i);
			if(c == ':') result = result.subPath(0);
			if(c == '.'){
				int count = 0;
				i += 1;
				while(i < pathString.length() && pathString.charAt(i) == '.'){
					count += 1;
					i += 1;
				}
				result = result.subPath(Math.max(result.depth() - count, 0));
			}
			if(isLetter(c) || c == '_'){
				StringBuilder sb = new StringBuilder();
				sb.append(c);
				i += 1;
				while(i < pathString.length() && isAlphaNumeric(pathString.charAt(i))){
					sb.append(pathString.charAt(i));
					i += 1;
				}
				result = new Path(result,sb.toString());
			}
		}
		return result;
	}

	private String getLastPathString(String s) {
		StringBuilder sb = new StringBuilder();
		char lastc = 'a'; //The last character read, 'a' in this case acts as a dummy
		for(int i = s.length() - 1 ; i >= 0 ; i--){
			char c = s.charAt(i);
			if(c == '.' || c == ':'){
				if(!(isLetter(lastc) || lastc == '_' || lastc == '.')) break;
			} else if(isAlphaNumeric(c) || c == '_'){
				if(lastc == '_') break;
			} else break;
			sb.append(c);
			lastc = c;
			if(c == ':') break;
		}
		return sb.reverse().toString();
	}

	private void selectLineSuggestions() {
		List<String> result = new ArrayList<String>();
		String currentInput = inputScreen.getText(); 
		for(String line : inputLines){
			if(line.startsWith(currentInput)) result.add(line);
		}
		currentLineSuggestions = result;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		suggestionIndex = -1;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isConsumed()) return;
		if(e.getKeyCode() == KeyEvent.VK_UP){
			pressKeyUp();
			e.consume();
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			pressKeyDown();
			e.consume();
		}
		if(e.getKeyCode() == KeyEvent.VK_TAB){
			performNameCompletion();
			e.consume();
		}
	}

	private void pressKeyUp() {
		if(suggestionIndex == -1) selectLineSuggestions();
		if(suggestionIndex + 1 < currentLineSuggestions.size()){
			suggestionIndex += 1;
			inputScreen.setText(currentLineSuggestions.get(suggestionIndex));
			inputScreen.setCaretPosition(currentLineSuggestions.get(suggestionIndex).length());
		}
	}

	private void pressKeyDown() {
		if(suggestionIndex - 1 >= 0){
			suggestionIndex -= 1;
			inputScreen.setText(currentLineSuggestions.get(suggestionIndex));
			inputScreen.setCaretPosition(currentLineSuggestions.get(suggestionIndex).length());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//Do nothing
	}
}
