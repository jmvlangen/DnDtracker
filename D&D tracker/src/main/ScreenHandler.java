package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ScreenHandler extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1752715129092209518L;
	private Tracker instance;
	private JTextField input;
	private JTextArea output;
	private JScrollPane outputScroller;
	private TextAreaOutputStream outputStream;
	private SmartInput inputManager;
	
	public static final int WRIGGLE_SPACE = 10;
	public static final int DEFAULT_WIDTH = 600;
	public static final int DEFAULT_HEIGHT = 400;

	public ScreenHandler(Tracker instance, String programName, int width, int height){
		super(programName);
		this.instance = instance;
		initializeInput();
		initializeOutput(height - input.getFont().getSize());
		initializeFrame(width,height);
	}

	private void initializeFrame(int width, int height) {
		setLayout(new BorderLayout());
		add(outputScroller,BorderLayout.CENTER);
		add(input,BorderLayout.PAGE_END);
		setPreferredSize(new Dimension(width,height));
		pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public ScreenHandler(Tracker instance, String programName){
		this(instance,programName,DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}

	private void initializeOutput(int height) {
		output = new JTextArea();
		outputScroller = new JScrollPane(output);
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		output.setEditable(false);
		outputStream = new TextAreaOutputStream(output);
	}

	private void initializeInput() {
		input = new JTextField();
		input.addActionListener(this);
		inputManager = new SmartInput(input);
		input.setPreferredSize(new Dimension(20,input.getFont().getSize() + WRIGGLE_SPACE));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String line = input.getText();
		if(instance.dispatchLine(line, new PrintStream(outputStream))) inputManager.addLineToMemory(line);
		input.setText("");
	}
	
	public void clearScreen(){
		output.setText("");
	}

	public PrintStream getOutput() {
		return new PrintStream(outputStream);
	}
}
