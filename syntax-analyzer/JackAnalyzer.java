import java.util.*;
import java.io.*;
import java.lang.String;

public class JackAnalyzer{
	public static void main(String args[]){
		String name=args[0];
		File dirpath=new File(name);

		CompilationEngine engine=new CompilationEngine(name);
		engine.mapInitialize();
		engine.parser();
	}
}