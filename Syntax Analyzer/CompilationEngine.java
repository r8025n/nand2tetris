import java.util.*;
import java.io.*;
import java.lang.String;

public class CompilationEngine extends Tokenizer{

	File fileList[];
	String[] outname;
	BufferedWriter out = null;
	String outfile = null;
	boolean isDirectory = false;
	int currentIndex = 0;

	List<String> tokens;

	CompilationEngine(String fileName) {
		File dirpath=new File(fileName);

		if(! dirpath.isDirectory()){
			fileList = new File[1];
			fileList[0] = dirpath;
			outname = fileName.split("[.]");
		}
		else{
			fileList= dirpath.listFiles();
			isDirectory = true;
			outname=new String[1];
			outname[0] = fileName;
		}
	}


	void parser() {
		for(File f : fileList){
			String[] fname=f.getName().split("[.]");
			if(fname[1].equals("jack")){
				currentIndex = 0;
				
				if(isDirectory == false){
					outfile=outname[0] + ".xml";
				}
				else{
					outfile=outname[0] + "/" + fname[0] + ".xml";
				}
				
				//output file
				try{
					FileWriter fileout=new FileWriter(outfile);
					out = new BufferedWriter(fileout);
				}catch(IOException k){
					System.out.println(outfile + " ->Couldn't open the output file");
				}
				

				tokens = tokenize(f);
				compileClass();

				try{
					out.close();
				}catch(IOException k){
					System.out.println(outfile + " ->Couldn't close the output file");
				}
			}
		}
	}

	void writee(String s) {
		String type = tokenType(s);
		String stringToWrite = "";
		
		if (type.equals("stringConstant")) {
			String temp = "";
			
			for (int k = 1; k < s.length() - 1; k++){
				temp += s.charAt(k);
			}
			stringToWrite = "<" + type + ">" + temp + "</" + type + ">\n";
		}
		else if (s.equals("<")) {
			stringToWrite = "<" + type + ">" + "&lt;" + "</" + type + ">\n";
		}
		else if (s.equals(">")) {
			stringToWrite = "<" + type + ">" + "&gt;" + "</" + type + ">\n";
		}
		else if (s.equals("\"")) {
			stringToWrite = "<"+type+">"+"&quot;"+"</"+type+">\n";
		}
		else if (s.equals("&")) {
			stringToWrite = "<" + type + ">" + "&amp;" + "</" + type + ">\n";
		}
		else {
			stringToWrite = "<" + type + ">" + s + "</" + type + ">\n";
		}
		writeMisc(stringToWrite);	
	}



	void eat(String s) {
		String token = tokens.get(currentIndex);
		currentIndex++;
		
		if (tokenType(s).equals("keyword") || tokenType(s).equals("symbol")){
			if (s.equals(token)) {
				writee(token);
			}
			else
				System.out.println(token+"->"+"Your code has error");
		}
		else if (tokenType(s).equals(tokenType(token))) {
			writee(token);
		}
		else {
			System.out.println(token+"->"+"Your code has error");
		}
	}



	void compileClass() {
		writeOpeningTag("class");
		eat("class");
		eat("abc");
		eat("{");
		compileClassVarDec();
		
		while (2 > 1) {
			String token = tokens.get(currentIndex);
			
			if(token.equals("constructor") || token.equals("function") || token.equals("method"))
				compileSubroutineDec();
			else
				break;
		}
		
		eat("}");
		writeClosingTag("class");
	}

	void compileClassVarDec() {
		while (2 > 1) {
			String token = tokens.get(currentIndex);
			
			if (token.equals("field") || token.equals("static"))
				singleLineClassVar();
			else
				break;
		}

	} 

	void singleLineClassVar() {
		writeOpeningTag("classVarDec");
		String token = tokens.get(currentIndex);

		if(token.equals("static"))
			eat("static");
		else
			eat("field");

		while (2 > 1) {
			token = tokens.get(currentIndex);
			
			if(token.equals(";"))
				break;
			writee(token);
			currentIndex++;
		}
		eat(";");
		writeClosingTag("classVarDec");
	}

	void compileSubroutineDec() {
		writeOpeningTag("subroutineDec");
		String token = null;
		token = tokens.get(currentIndex);
		currentIndex++;
		writee(token);
		token = tokens.get(currentIndex);
		currentIndex++;
		writee(token);
		token = tokens.get(currentIndex);
		currentIndex++;
		writee(token);
		eat("(");
		compileParameterList();
		eat(")");
		compileSubroutineBody();
		writeClosingTag("subroutineDec");
	}

	void compileParameterList() {
		writeOpeningTag("parameterList");

		while (2 > 1) {
			String token = tokens.get(currentIndex);
			
			if(token.equals(")"))
				break;
			else
				writee(token);
			currentIndex++;
		}
		writeClosingTag("parameterList");
	}

	void compileSubroutineBody() {
		writeOpeningTag("subroutineBody");
		eat("{");
		compileVarDec();
		compileStatements();
		eat("}");
		writeClosingTag("subroutineBody");
	}

	void compileVarDec() {
		while (2 > 1) {
			String token = tokens.get(currentIndex);
			
			if(token.equals("var"))
				singleLineVarDec();
			else
				break;
		}
	}

	void singleLineVarDec() {
		writeOpeningTag("varDec");
		eat("var");
		writee(tokens.get(currentIndex));
		currentIndex++;
		eat("abc");
		String token = tokens.get(currentIndex);
		
		while (! token.equals(";")) {
			eat(",");
			eat("abc");
			token = tokens.get(currentIndex);
		}
		eat(";");
		writeClosingTag("varDec");
	}

	void compileStatements() {
		writeOpeningTag("statements");
		while (2 != 1){
			String token=tokens.get(currentIndex);

			if (token.equals("if"))
				compileIf();
			else if (token.equals("while"))
				compileWhile();
			else if (token.equals("let"))
				compileLet();
			else if (token.equals("do"))
				compileDo();
			else if (token.equals("return"))
				compileReturn();
			else
				break;
		}
		writeClosingTag("statements");
	}

	void compileLet() {
		writeOpeningTag("letStatement");
		eat("let");
		eat("abc");
		
		if (tokens.get(currentIndex).equals("[")){
			eat("[");
			compileExpression();
			eat("]");
		}
		eat("=");
		compileExpression();
		eat(";");
		writeClosingTag("letStatement");
	}

	void compileIf() {
		writeOpeningTag("ifStatement");
		eat("if");
		eat("(");
		compileExpression();
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
		
		if (tokens.get(currentIndex).equals("else")) {
			eat("else");
			eat("{");
			compileStatements();
			eat("}");
		}
		writeClosingTag("ifStatement");
	}

	void compileWhile() {
		writeOpeningTag("whileStatement");
		eat("while");
		eat("(");
		compileExpression();
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
		writeClosingTag("whileStatement");
	}

	void compileDo() {
		writeOpeningTag("doStatement");
		eat("do");
		eat("abc");
		String temp=tokens.get(currentIndex);
		
		if (temp.equals(".")) {
			eat(".");
			eat("abc");
		}
		eat("(");
		compileExpressionList();
		eat(")");
		eat(";");
		writeClosingTag("doStatement");
	}

	void compileReturn() {
		writeOpeningTag("returnStatement");
		eat("return");
		
		if (! tokens.get(currentIndex).equals(";"))
			compileExpression();
		eat(";");
		writeClosingTag("returnStatement");
	}

	void compileExpression() {
		writeOpeningTag("expression");

		String tempp = tokens.get(currentIndex);

		while ( ! (tempp.equals(";") || tempp.equals("]") || tempp.equals(")") || tempp.equals("}") || tempp.equals(",") )) {
			if (tempp.equals("(")) {
				writeOpeningTag("term");
				eat("(");
				compileExpression();
				eat(")");
				writeClosingTag("term");
			}
			else if (tempp.equals("[")) {
				eat("[");
				compileExpression();
				eat("]");
			}
			else if (tempp.equals("~") || (tempp.equals("-") && tokens.get(currentIndex-1).equals("("))) {
				compileTerm();
			}
			else {
				if (tokenType(tempp).equals("identifier") || tokenType(tempp).equals("integerConstant") || tokenType(tempp).equals("stringConstant") || tokenType(tempp).equals("keyword")){
					compileTerm();
				}
				else{
					writee(tempp);
					currentIndex++;
				}
			}
			tempp = tokens.get(currentIndex);
		}
		writeClosingTag("expression");
	}

	void compileTerm() {
		writeOpeningTag("term");
		int k = currentIndex+1;
		
		if (tokens.get(k).equals(".")) {
			eat("abc");
			eat(".");
			eat("abc");
			eat("(");
			compileExpressionList();
			eat(")");
		}
		else if (tokens.get(currentIndex).equals("~")) {
			eat("~");
			compileTerm();
		}
		else if (tokens.get(currentIndex).equals("(")) {
			eat("(");
			compileExpression();
			eat(")");
		}
		else if (tokens.get(currentIndex).equals("-") && tokens.get(currentIndex-1).equals("(")) {
			eat("-");
			compileTerm();
		}
		else {
			if (tokenType(tokens.get(currentIndex)).equals("identifier")) {
				eat("abc");
				
				if (tokens.get(currentIndex).equals("[")) {
					eat("[");
					compileExpression();
					eat("]");
				}
			}
			else {
				writee(tokens.get(currentIndex));
				currentIndex++;
			}
		}
		writeClosingTag("term");
	}

	void compileExpressionList() {
		writeOpeningTag("expressionList");

		while (2 != 1) {
			String token=tokens.get(currentIndex);
			
			if (! token.equals(")")) {
				compileExpression();
				
				if (! tokens.get(currentIndex).equals(")"))
					eat(",");
			}
			else
				break;
		}
		writeClosingTag("expressionList");
	}

	void writeOpeningTag(String classifier) {
		try{
			out.write("<"+ classifier + ">\n");
		}catch(IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void writeClosingTag(String classifier) {
		try{
			out.write("</"+ classifier + ">\n");
		}catch(IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void writeMisc(String str) {
		try{
			out.write(str);
		}catch(IOException e) {
			System.out.println("Couldn't write in file");
		}
	}
}
