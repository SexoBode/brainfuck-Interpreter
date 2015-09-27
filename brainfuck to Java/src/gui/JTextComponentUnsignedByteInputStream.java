package gui;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

//eu juro que isto foi tudo feito com o poder do CRISTAL ANAL (TM)
//como caralhos � que o scanner sabe, da default input stream, que acabou um token??? sem esta retornar -1
//isto � completamente thread unsafe e pode n�o funcionar em outras implementa��es
//FODE-TE
public class JTextComponentUnsignedByteInputStream extends InputStream {
	private boolean returnLineBreak = false;
	private String systemLineSeparator = System.getProperty("line.separator");
	private int lineSeparatorCounter = 0;
	private JTextComponent textComponent;
	
	public JTextComponentUnsignedByteInputStream(JTextComponent textField) {
		this.textComponent = textField;
	}
	
	@Override
	public int read() throws IOException {	//never ends
		if(!returnLineBreak) {
			synchronized(this) {
				while(textComponent.getText().equals("")) {
					try {
						this.wait();
					} catch (InterruptedException e) {}
				}
			}
			
			returnLineBreak= true;
			assert(Integer.parseInt(textComponent.getText()) >= 0);
			assert(Integer.parseInt(textComponent.getText()) <= 255);
			return Integer.parseInt(textComponent.getText());
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
