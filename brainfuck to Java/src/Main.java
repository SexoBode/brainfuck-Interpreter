import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		String program = null;
		System.out.println("1 - Read from file\n2 - Read from user");
		Scanner in = new Scanner(System.in);
		
		int val = in.nextInt();
		in.nextLine();
		
		if(val == 1) {
			System.out.print("\nFile to open: ");
			String fileName = in.nextLine();
			
			System.out.print("Reading from file: ");
			StringBuilder tempProgram = new StringBuilder();
				
			try(FileReader reader = new FileReader(fileName)) {			
				for(int characterRead = reader.read(); characterRead != -1; characterRead = reader.read()) {
					tempProgram.append((char) characterRead);	
				}
			} catch (IOException exception) {
				System.err.printf("Could not open file «%s».\nAre you sure it exists?\n", fileName);
				System.exit(-1);
			}
			
			program = tempProgram.toString();
		} else if(val == 2) {
			System.out.print("\nReading from user: ");
			program = in.nextLine();
		} else {
			System.err.println("Invalid option selected.");
			System.exit(-2);
		}
		
		BrainfuckInterpreter interpreter = new BrainfuckInterpreter(program);
		System.out.printf("%n--[Now running program «%s»]--%n", interpreter.getProgram());
		interpreter.run();
		
		System.out.println("\n--[Characters read ended in the line above]--");
	}

}
