package brainfuckingGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GUI {

	private JFrame frame;
	private JPanel labelPanel, textPanel, controlPanel;
	private JTextArea codeTextArea, outputTextArea;
	private JTextField charInputTextField, byteValueTextField;
	private JScrollPane codeScrollPanel, outputScrollPanel;
	private JLabel codeLabel, outputLabel, inputCharLabel, inputByteValueLabel;
	private JButton runButton;
	private JMenuBar menuBar;
	private JMenu fileMenu, helpMenu;
	private File lastFileHandled = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		GUI window = new GUI();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//					GUI window = new GUI();
					window.frame.pack();
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

	//Does not receive characters/bytes from the propers fields, prints output to System.out and not text area
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
		frame.setIconImages(new LinkedList<Image>() {{
			add((new ImageIcon("brainfuck_icon_16x16.png")).getImage());
			add((new ImageIcon("brainfuck_icon_20x20.png")).getImage());
			add((new ImageIcon("brainfuck_icon_32x32.png")).getImage());
			add((new ImageIcon("brainfuck_icon_40x40.png")).getImage());
			add((new ImageIcon("brainfuck_icon_64x64.png")).getImage());
			add((new ImageIcon("brainfuck_icon_128x128.png")).getImage());
		}});
		
		addMenuBar();
		
		Container frameContentPane = frame.getContentPane();
		BoxLayout mainLayout = new BoxLayout(frameContentPane, BoxLayout.PAGE_AXIS);
		frameContentPane.setLayout(mainLayout);
		
		labelPanel = new JPanel();
		codeLabel = new JLabel("Code:");
		codeLabel.setOpaque(true);
		codeLabel.setBackground(Color.YELLOW);
		labelPanel.add(codeLabel);
		
		outputLabel = new JLabel("Program output:");
		outputLabel.setOpaque(true);
		outputLabel.setBackground(Color.YELLOW);
		labelPanel.add(outputLabel);
		frameContentPane.add(labelPanel);
		
		textPanel = new JPanel();
		codeTextArea = new JTextArea(10, 31);	//old 10, 48 dimensions
		codeTextArea.setLineWrap(true);
		codeTextArea.setTabSize(2);
		codeScrollPanel = new JScrollPane(codeTextArea);
		textPanel.add(codeScrollPanel);
		
		outputTextArea = new JTextArea(10, 14);
		outputTextArea.setLineWrap(true);
		outputTextArea.setEditable(false);
		outputTextArea.setTabSize(2);
		outputScrollPanel = new JScrollPane(outputTextArea);
		textPanel.add(outputScrollPanel);
		frameContentPane.add(textPanel);
		
		controlPanel = new JPanel();
		runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BrainfuckInterpreter interpreter = new BrainfuckInterpreter(codeTextArea.getText());
				Thread bfThread = new Thread(interpreter);
				bfThread.setDaemon(true);
				bfThread.start();
			}
		});
		controlPanel.add(runButton);
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputTextArea.setText("");
			}
		});
		controlPanel.add(clearButton);
		
		inputCharLabel = new JLabel("Input character:");
		inputCharLabel.setOpaque(true);
		inputCharLabel.setBackground(Color.YELLOW);
		controlPanel.add(inputCharLabel);
		
		charInputTextField = new JTextField(3);
		charInputTextField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if(getLength() + str.length() == 1) {
					super.insertString(offs, str, a);
				}
			}
		});
		controlPanel.add(charInputTextField);
		
		inputByteValueLabel = new JLabel("Input byte value:");
		inputByteValueLabel.setOpaque(true);
		inputByteValueLabel.setBackground(Color.YELLOW);
		controlPanel.add(inputByteValueLabel);
		
		byteValueTextField = new JTextField(3);
		byteValueTextField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				AbstractDocument.Content content = getContent();
				
				if(!content.getString(0, 1).equals("0") && getLength() + str.length() <= 3) {	//3 being the number of digits in our max val, 255 || first part to avoid leading 0s
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
		controlPanel.add(byteValueTextField);
		frameContentPane.add(controlPanel);
	}

	private void addMenuBar() {
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		
		JMenuItem openFileMenuItem = new JMenuItem("Open file"), saveCodeMenuItem = new JMenuItem("Save program"), exitMenuItem = new JMenuItem("Exit");
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser openFile = new JFileChooser(System.getProperty("user.home"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files - *.txt", "txt");
				
				openFile.setFileFilter(filter);
				openFile.setDialogTitle("Load brainfuck program");
				
				if(lastFileHandled != null) {
					openFile.setSelectedFile(lastFileHandled);
				}
				
				int returnedValue = openFile.showDialog(frame, "Load");
				if(returnedValue == JFileChooser.APPROVE_OPTION) {
					Thread fileOpenerThread = new Thread(new Runnable() {
						public void run() {
							StringBuilder fileText = new StringBuilder("");
							File codeFile = openFile.getSelectedFile();
							String fileName = codeFile.getName();
							
							try(FileReader reader = new FileReader(codeFile)) {
								if(!fileName.substring(fileName.lastIndexOf(".")).equals(".txt")) {
									throw new IOException("Can't open non-txt files");
								}
								
								for(int characterRead = reader.read(); characterRead != -1; characterRead = reader.read()) {	//instead of reading one by one, read in bulk
									fileText.append((char) characterRead);
								}
								
								lastFileHandled = codeFile;
								
								codeTextArea.setText(fileText.toString());
								outputTextArea.setText("");
							} catch (IOException exception) {
								outputTextArea.setText("Failed to open " + fileName);
							}
						}
					});
					
					fileOpenerThread.start();
 				}
			}		
		});
		fileMenu.add(openFileMenuItem);
		
		//não grava linhas (só está a receber ASCII 10)
		//não grava bem se metermos save "f" em vez de f.txt
		saveCodeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveFile = new JFileChooser(System.getProperty("user.home"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files - *.txt", "txt");
				
				saveFile.setFileFilter(filter);
				saveFile.setDialogTitle("Save brainfuck program");
				
				if(lastFileHandled != null) {
					saveFile.setSelectedFile(lastFileHandled);
				}
				
				int returnedValue = saveFile.showSaveDialog(frame);
				if(returnedValue == JFileChooser.APPROVE_OPTION) {
					Thread fileSaverThread = new Thread(new Runnable() {
						public void run() {
							File codeFile = saveFile.getSelectedFile();
							
							System.out.println("Before: " + codeFile.getName());
							
							//thanks short-circuiting
							if(codeFile.getName().lastIndexOf(".") == -1 || !codeFile.getName().substring(codeFile.getName().lastIndexOf(".")).equals(".txt")) {
								codeFile = new File(codeFile.getName() + ".txt");
							}
							
							System.out.println("After: " + codeFile.getName());
							
							try(FileWriter writer = new FileWriter(codeFile)) {
//								String temp = codeTextArea.getText().replace("\\n", "\\r\\n");	//NOT PORTABLE!!
//								for(int i = 0; i < temp.length(); ++i) {
//									System.out.print((int) temp.charAt(i) + " ");
//								}

								System.out.println("After: " + codeFile.getName());
								writer.write(codeTextArea.getText());
								writer.flush();	//Always good practice, even if it doesn't do anything!

								System.out.println("After: " + codeFile.getName());
								lastFileHandled = codeFile;
							} catch (IOException exception) {
								outputTextArea.setText("Failed to save " + codeFile.getName());
							}
							System.out.println("After: " + codeFile.getName());
						}
					});
					
					fileSaverThread.start();
 				}
			}		
		});
		fileMenu.add(saveCodeMenuItem);
		
		fileMenu.addSeparator();
		
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		
		helpMenu = new JMenu("Help");
		JMenuItem specificationMenuItem = new JMenuItem("Interpreter Specification"), versionMenuItem = new JMenuItem("Version"), contactMenuItem = new JMenuItem("Contact the makers");
		helpMenu.add(specificationMenuItem);
		helpMenu.add(versionMenuItem);
		helpMenu.add(contactMenuItem);
		menuBar.add(helpMenu);
		
		frame.setJMenuBar(menuBar);
	}
	
}
