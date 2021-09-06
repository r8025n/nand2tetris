import java.util.*;
import java.io.*;
import java.lang.String;

public class Tokenizer {

	Map<String,String> map = new HashMap<String, String>();

	final static String[] keywords = {"class", "constructor", "function", "method", "field", "static", "var",
									 "int", "char", "boolean", "void", "true","false", "null", "this", "let","do", 
									 "if", "else", "while", "return", "Output", "Memory", "String", "Math", "Sys",
									  "Screen", "Keyboard", "Array"};

	final static String[] symbols = {"[", "]", "(", ")", "{", "}", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", ".", "~"};
	
	void mapInitialize() {
		for (String s : keywords) {
			map.put(s, "keyword");
		}

		for(String s : symbols){
			map.put(s, "symbol");
		}
	}


	List<String> tokenize(File file) {
		Scanner scanner1 = null, scanner2 = null;
		boolean stringTypeToken = false, commentTypeToken = false;
		List<String> tokenList = new ArrayList<String>();
		String tempString = "";

		try {
			scanner1=new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println(file.getName()+" ->File not found\n");
		}

		while (scanner1.hasNextLine()) {
			String newString = scanner1.nextLine();
			
			if (! isEmptyLine(newString)) {
				if(isStartingComment(newString))
					continue;
				else {
					scanner2 = new Scanner(newString);
					
					while (scanner2.hasNext()) {
						if (commentTypeToken == true) {
							commentTypeToken = false;
							break;
						}

						String token = scanner2.next();
						String[] splittedToken = token.split("(?<=[~.;,\\[\\])\\-\\+(\"])|(?=[~.;,\\[\\])\\-\\+(\"])"); 

						for (int i = 0; i < splittedToken.length; i++){							
							if (isInLineComment(splittedToken[i])) {
								commentTypeToken = true;
								continue;
							}

							if (splittedToken[i].equals("\"") && stringTypeToken == false) {
								// start of a string
								stringTypeToken = true;
							}
							else if (splittedToken[i].equals("\"") && stringTypeToken == true) {
								// ending of the string
								tempString = "\"" + tempString + "\"";
								tokenList.add(tempString);
								tempString = "";
								stringTypeToken = false;
							}
							else if (stringTypeToken == true){
								// middle of the string
								tempString += splittedToken[i];
								tempString += " ";
							}
							else {
								tokenList.add(splittedToken[i]);
							}
						}
					}
				}
			}
		}
		return tokenList;
	}

	boolean isInLineComment(String str) {
		if(str.charAt(0) == '/' && str.length() > 1)
			return true;

		return false;
	}

	boolean isEmptyLine(String str) {
		if (str.trim().equals(""))
			return true;

		return false;
	}

	boolean isStartingComment(String str) {
		String trimmedStr = str.trim();
		char char1 = trimmedStr.charAt(0);

		if(char1 == '/' || char1 == '*')
			return true;

		return false;
	}

	String tokenType(String str) {
		String type = map.get(str);

		if(type == null) {
			
			char firstChar = str.charAt(0);
			
			if (firstChar >= 48 && firstChar <= 57) {
				type = "integerConstant";
			}
			else if (firstChar == '"') {
				type = "stringConstant";
			}
			else {
				type = "identifier";
			}
		}
		return type;
	}
}