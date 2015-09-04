import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class BrainfuckInterpreter {
	public final static int MEMORY_SIZE = 8192;
	
	private final char[] program;
	private final String programAsString;
	private final byte[] memory = new byte[MEMORY_SIZE];
	private final Stack<Pair> parentheses = new Stack<Pair>();
	private int PC = 0, pointer = 0;
	private InputStream inputStream = System.in;
	private Scanner userInput = new Scanner(inputStream);
	private PrintStream programOutput = System.out;
	
	//this is a struct
	private class Pair {
		public int head, tail;
	}
	
	public BrainfuckInterpreter(String program) {
		//give it up for lazy regex
		programAsString = program.replaceAll("[^-><,\\+\\.\\[\\]]", "");
		this.program = programAsString.toCharArray();
	}

	public void run() {		
		while(PC < program.length) {
			parse(program[PC]);
		}
	}
	
	//This method is responsible for increasing the program counter
	private void parse(char character) {
		switch(character) {
		case '>':
			if(pointer + 1 > MEMORY_SIZE) {
				throw new IndexOutOfBoundsException("You're trying to overflow the array.\nDo you want to segfault?\n");
			}
			++pointer;
			
			++PC;
			break;
		case '<':
			if(pointer - 1 < 0) {
				throw new IndexOutOfBoundsException("You're trying to underflow the array.\nStop that.\n");
			}
			--pointer;
			
			++PC;
			break;
		case '+':
			if( (((int) memory[pointer]) & 0xFF) + 1 > 0xFF) {
				throw new IndexOutOfBoundsException("No characters above 0xFF allowed.\n");
			}
			memory[pointer] = (byte) (memory[pointer] + 1);
			
			++PC;
			break;
		case '-':
			if( (((int) memory[pointer]) & 0xFF) - 1 < 0) {
				throw new IndexOutOfBoundsException("No characters below 0x00 allowed.\n");
			}
			memory[pointer] = (byte) (memory[pointer] -1);
			
			++PC;
			break;
		case '.':
			programOutput.print((char) memory[pointer]);
			
			++PC;
			break;
		case ',':
			{
				int input = (int) userInput.next().charAt(0);
				if(input < 0 || input > 0xFF) {
					throw new InputMismatchException("Only values between 0x00 and 0xFF (both inclusive).\n");
				}
				
				memory[pointer] = (byte) input;
			}
			
			++PC;
			break;
		case '[':
			Pair parens = new Pair();
			parens.head = PC;
			parentheses.push(parens);
			
			while(PC != parentheses.peek().head || memory[pointer] != 0x00) {
				if(PC == parentheses.peek().head) {
					++PC;
				}
				
				//System.out.println("PC: " + PC + " program: " + program.get(PC) + " pointer: " + pointer + " memory: " + memory[pointer]);
				parse(program[PC]);
			}//se o PC estiver a tentar fugir, imba parens miss]
			
			PC = parentheses.pop().tail + 1;
			break;
		case ']'://if stack empty, throw imba parens miss [
			parentheses.peek().tail = PC;
			
			PC = parentheses.peek().head;
			break;
		default:
			++PC;
			break;
		}
	}
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		userInput = new Scanner(inputStream);
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public void setOutputStream(PrintStream out) {
		programOutput = out;
	}
	
	public OutputStream getOutputStream() {
		return programOutput;
	}
	
	public String getProgram() {
		return programAsString;
	}
	
}
