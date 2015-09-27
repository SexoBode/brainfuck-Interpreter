package gui;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.JTextComponent;

//merda
public class JTextComponentASCIICharInputStream extends InputStream {

	private boolean returnLineBreak = false;
	private String systemLineSeparator = System.getProperty("line.separator");
	private int lineSeparatorCounter = 0;
	private JTextComponent textComponent;
	
	public JTextComponentASCIICharInputStream(JTextComponent textField) {
		this.textComponent = textField;
	}
	
	@Override
	public int read() throws IOException {	//never ends
		if(!returnLineBreak) {
			synchronized(this) {
				while(textComponent.getText().equals("") && textComponent.getText().length() == 0) {
					try {
						this.wait();
					} catch (InterruptedException e) {}
				}
			}
			
			returnLineBreak= true;
			return (byte) (textComponent.getText().charAt(0));
		} else {
			textComponent.setText("");
			lineSeparatorCounter %= systemLineSeparator.length();
			if(lineSeparatorCounter == systemLineSeparator.length() -1) {
				returnLineBreak = false;
			}
			return systemLineSeparator.charAt(lineSeparatorCounter++);
		}
	}
	
	@Override
	public int read(byte b[], int off, int len) throws IOException {
		if(b.length < 3) {
			throw new IOException("Buffer must be able to hold at least 3 bytes.");
		}
		
		return super.read(b, 0, 3);
	}
}
