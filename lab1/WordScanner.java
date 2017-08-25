
import java.io.*;

/**
 * This class implements a word (string) scanner
 */
public class WordScanner {
	private BufferedReader input;
	private int charPos;
	private int lineNum;
	private int current;
	/**
	 * Builds a WordScanner object based on the given input
	 */
	public WordScanner(FileReader input) throws IOException {
		this.input = new BufferedReader(input);
		this.charPos = 1;
		this.lineNum = 1;
		this.current = this.input.read();
	}
	
	/**
	 * Returns the next word from input
	 * Precond: there must be at least
	 * one word left in the input
	 * (i.e. hasNextWord() must evaluate to true)
	 */
	public Word nextWord() throws IOException {
		String str = "";
		int pos = charPos;
		int line = lineNum;
		while(current != -1){
			if(isLetter((char)current) || isDigit((char)current) || (char)current == '\'') 
				str += String.valueOf((char)current).toLowerCase();
			else
				break;
			current = input.read();
			charPos++;
		}
		if(str.charAt(str.length()-1) == '\'')
			str = str.substring(0,str.length()-2);
		Info info = new Info(pos, line);
		Word word = new Word(str, info);
		return word;
	}
	private boolean isLetter(char c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	/**
	 * Returns true if there is at least
	 * one word left in the input, false otherwise
	 */
	public boolean hasNextWord() throws IOException{
		while(current != -1){
			if(isLetter((char)current) || isDigit((char)current))
				return true;
			if((char)current == '\n'){
				charPos = 0;
				lineNum++;
			}
			current = input.read();
			charPos++;
		}
		return false;
	}	
}
