package main;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
	
	private JTextArea textArea;

	public TextAreaOutputStream(JTextArea textArea){
		this.textArea = textArea;
	}

	@Override
	public void write(int b) throws IOException {
		if(textArea == null) throw new IOException("The text area has been closed.");
		textArea.append(String.format("%c",b));
	}

}
