import java.util.*;
import java.io.*;
import java.lang.String;

public class JackAnalyzer{

	public static void main(String args[]) {
		String fileName = args[0];
		File dirpath = new File(fileName);
		CompilationEngine newSyntaxAnalyzer = new CompilationEngine(fileName);
		newSyntaxAnalyzer.mapInitialize();
		newSyntaxAnalyzer.parser();
	}
}