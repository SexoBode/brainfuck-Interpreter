package brainfuckingGUI;

import gui.JTextAreaOutputStream;
import gui.JTextComponentASCIICharInputStream;
import gui.JTextComponentCombinedInputStream;
import gui.JTextComponentUnsignedByteInputStream;

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
import javax.swing.JOptionPane;
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
import java.io.InputStream;
import java.io.PrintStream;

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
					window.frame.pack();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
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
		
		
		byteValueTextField = new JTextField(3);
		charInputTextField = new JTextField(3);
		final InputStream inputByte = new JTextComponentUnsignedByteInputStream(byteValueTextField);
		final InputStream inputChar = new JTextComponentASCIICharInputStream(charInputTextField);
		final JTextComponentCombinedInputStream inputStream = new JTextComponentCombinedInputStream();
		
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BrainfuckInterpreter interpreter = new BrainfuckInterpreter(codeTextArea.getText());
				interpreter.setOutputStream(new PrintStream(new JTextAreaOutputStream(outputTextArea)));
				interpreter.setInputStream(inputStream);
				Thread bfThread = new Thread(interpreter);
				bfThread.setName("Brainfuck Interpreter Thread");
				bfThread.setDaemon(true);
				bfThread.start();
				
				runButton.setEnabled(false);
			}
		});
		controlPanel.add(runButton);
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputTextArea.setText("");
				codeTextArea.setText("");
			}
		});
		controlPanel.add(clearButton);
		
		inputCharLabel = new JLabel("Input character:");
		inputCharLabel.setOpaque(true);
		inputCharLabel.setBackground(Color.YELLOW);
		controlPanel.add(inputCharLabel);
		
		charInputTextField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if(getLength() + str.length() == 1) {
					super.insertString(offs, str, a);
				}
			}
		});
		charInputTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				synchronized(inputByte) {
//					inputByte.notify();
//				}
				inputStream.setSource(inputChar);
				synchronized(inputStream) {
					inputStream.notify();
				}
			}
		});
		controlPanel.add(charInputTextField);
		
		inputByteValueLabel = new JLabel("Input byte value:");
		inputByteValueLabel.setOpaque(true);
		inputByteValueLabel.setBackground(Color.YELLOW);
		controlPanel.add(inputByteValueLabel);
		
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
		byteValueTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				inputStream.setSource(inputByte);
				synchronized(inputStream) {
					inputStream.notify();
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
							StringBuilder codeFileName = new StringBuilder(saveFile.getSelectedFile().getAbsolutePath());
							
							if(codeFileName.lastIndexOf(".") == -1 || !codeFileName.substring(codeFileName.lastIndexOf(".")).equals(".txt")) {
								codeFileName.append(".txt");
							}
							
							File codeFile = new File(codeFileName.toString());
							
							try(FileWriter writer = new FileWriter(codeFile)) {
								//codeTextArea's linebreaks are all \n, which is not compatible with some platforms
								String linebrokenText = codeTextArea.getText().replace("\n", System.getProperty("line.separator"));	//could be microoptimised to be avoided on systems which accept \n as a linebreak

								writer.write(linebrokenText);
								writer.flush();	//Always good practice, even if it doesn't do anything!

								lastFileHandled = codeFile;
							} catch (IOException exception) {
								EventQueue.invokeLater(() -> outputTextArea.setText("Failed to save " + codeFile.getName()));
//								outputTextArea.setText("Failed to save " + codeFile.getName());
							}
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
		JMenuItem specificationMenuItem = new JMenuItem("Interpreter Specification"), contactMenuItem = new JMenuItem("Version & Contact");
		
		specificationMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Cell values are limited to values between 0 and 255 (inclusive).\nThey do not wrap around, and the running bf program will be terminated if you attempt to do so.\n\nThe full specification may be found on this program's github page, and it should have come bundled with the program.", "Interpreter Specification", JOptionPane.INFORMATION_MESSAGE);	//http://stackoverflow.com/questions/8348063/clickable-links-in-joptionpane
			}
		});
		helpMenu.add(specificationMenuItem);
		
		contactMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Find me and my github profile at http://sexobode.github.io\n\nProgram version 0.1", "Version & Contact", JOptionPane.INFORMATION_MESSAGE);	//http://stackoverflow.com/questions/8348063/clickable-links-in-joptionpane && http://stackoverflow.com/questions/14011492/text-wrap-in-joptionpane
			}
		});
		helpMenu.add(contactMenuItem);
		menuBar.add(helpMenu);
		
		frame.setJMenuBar(menuBar);
	}
	
}
