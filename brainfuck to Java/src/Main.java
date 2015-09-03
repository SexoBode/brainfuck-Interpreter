import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Test

public class Main {
	public static final String FILENAME = "code.txt";
	
	public static void main(String[] args) {
		List<Character> program = null;
		
		try(FileReader reader = new FileReader(FILENAME)) {
			program = new ArrayList<Character>();
			
			for(int characterRead = reader.read(); characterRead != -1; characterRead = reader.read()) {
				program.add((char) characterRead);	
			}
		} catch (IOException exception) {
			System.err.printf("Could not open file «%s».\nAre you sure it exists?\n", FILENAME);
			System.exit(-1);
		}
		
		BrainfuckInterpreter interpreter = new BrainfuckInterpreter(program);
		interpreter.run();
		
		System.out.printf("\n--[Characters read ended in the line above]--\n\n");
	}

}
