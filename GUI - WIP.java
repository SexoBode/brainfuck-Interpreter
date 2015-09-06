package brainfuckingGUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

import java.awt.Window.Type;

public class GUI {

	private JFrame frame;
	private JPanel panel;
	private JTextArea codeTextArea, outputTextArea;
	private JTextField charInputTextField, byteValueTextField;
	private JScrollPane codeScrollPanel, outputScrollPanel;
	private JLabel codeLabel, outputLabel, inputCharLabel, inputByteValueLabel;
	private JButton runButton;
	private JMenuBar menuBar;
	private JMenu fileMenu, helpMenu;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		GUI window = new GUI();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
//		while(true) {
//			System.out.printf("Window width: %d, Window length: %d%n", window.frame.getWidth(), window.frame.getHeight());
//		}
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	//perhaps add a "Status" text field, separate from the output
	@SuppressWarnings("serial")
	private void initialize() {
		frame = new JFrame("Brainfuck Interpreter");
		frame.setResizable(false);
		frame.setBounds(0, 0, 560, 400);	//324 is good, 300 might work (second number)
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		JMenuItem openFileMenuItem = new JMenuItem("Open file"), saveCodeMenuItem = new JMenuItem("Save program"), exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(openFileMenuItem);
		fileMenu.add(saveCodeMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		
		helpMenu = new JMenu("Help");
		JMenuItem specificationMenuItem = new JMenuItem("Interpreter Specification"), versionMenuItem = new JMenuItem("Version");
		helpMenu.add(specificationMenuItem);
		helpMenu.add(versionMenuItem);
		menuBar.add(helpMenu);
		
		frame.setJMenuBar(menuBar);
		
		panel = new JPanel();
		frame.getContentPane().add(panel);
		
		codeLabel = new JLabel("Code:");
		codeLabel.setBounds(29, 91, 29, 14);
		codeLabel.setOpaque(true);
		codeLabel.setBackground(Color.YELLOW);
		panel.add(codeLabel);
		
		codeTextArea = new JTextArea(10, 31);	//old 10, 48 dimensions
		codeTextArea.setLineWrap(true);
		codeTextArea.setTabSize(2);
		codeScrollPanel = new JScrollPane(codeTextArea);
		codeScrollPanel.setBounds(63, 5, 254, 186);
		panel.add(codeScrollPanel);
		
		outputLabel = new JLabel("Program output:");
		outputLabel.setBounds(322, 91, 79, 14);
		outputLabel.setOpaque(true);
		outputLabel.setBackground(Color.YELLOW);
		panel.add(outputLabel);
		
		outputTextArea = new JTextArea(10, 14);
		outputTextArea.setLineWrap(true);
		outputTextArea.setEditable(false);
		outputTextArea.setTabSize(2);
		outputScrollPanel = new JScrollPane(outputTextArea);
		outputScrollPanel.setBounds(406, 5, 118, 186);
		panel.add(outputScrollPanel);
		
		runButton = new JButton("Run");
		runButton.setBounds(130, 196, 51, 23);
		panel.add(runButton);
		
		inputCharLabel = new JLabel("Input character:");
		inputCharLabel.setBounds(186, 200, 79, 14);
		inputCharLabel.setOpaque(true);
		inputCharLabel.setBackground(Color.YELLOW);
		panel.add(inputCharLabel);
		
		charInputTextField = new JTextField(3);
		charInputTextField.setBounds(270, 197, 30, 20);
		charInputTextField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if(getLength() + str.length() == 1) {
					super.insertString(offs, str, a);
				}
			}
		});
		panel.add(charInputTextField);
		
		inputByteValueLabel = new JLabel("Input byte value:");
		inputByteValueLabel.setBounds(305, 200, 84, 14);
		inputByteValueLabel.setOpaque(true);
		inputByteValueLabel.setBackground(Color.YELLOW);
		panel.add(inputByteValueLabel);
		
		byteValueTextField = new JTextField(3);
		byteValueTextField.setBounds(394, 197, 30, 20);
		byteValueTextField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if(getLength() + str.length() <= 3) {	//3 being the number of digits in our max val, 255
					AbstractDocument.Content content = getContent();
					String newStr = (content.getString(0, content.length() - 1) + str);	//-1 to remove the trailing linebreak

					try {
						int value = new Integer(newStr);
						
						if(value >= 0 && value <= 255) {
							super.insertString(offs, str, a);
						}
					} catch(NumberFormatException e) {
						//Do nothing
					}
				}
			}
		});
		panel.add(byteValueTextField);
	}

}
