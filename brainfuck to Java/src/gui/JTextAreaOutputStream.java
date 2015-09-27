package gui;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

//Might be more efficient as a BufferedOutputStream, but all that matters now is for it to work
public class JTextAreaOutputStream extends OutputStream {

	private final JTextArea textArea;
	
	public JTextAreaOutputStream(JTextArea textArea) {
		super();
		this.textArea = textArea;
	}
	
	@Override
	public void write(int b) throws IOException {
		final byte[] low_bits = new byte[1];
		low_bits[0] = (byte) (b & 0x000000FF);
		final String byteAsChar = new String(low_bits, "UTF-8");	//utf-8
		
		EventQueue.invokeLater(() -> textArea.append(byteAsChar));
	}

}
