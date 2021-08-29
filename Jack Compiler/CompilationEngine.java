import java.util.*;
import java.io.*;
import java.lang.String;

public class CompilationEngine extends Tokenizer{

	File fileList[];
	String[] outname;
	BufferedWriter out = null;
	String outfile = null;
	String tempToken = null;
	boolean isDirectory = false;
	int currentIndex = 0;
	SymbolTable symbolTable = null;
	String type = "", kind = "", level = "", state = "";

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
				symbolTable = new SymbolTable();
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
		else if(type.equals("identifier") && (level == "class" || level == "sub")) {
			try {
				String k = symbolTable.getKind(s);
				String t = symbolTable.getType(s);
				int in = symbolTable.getIndex(s);
				System.out.println("try e dhora khaise");
				stringToWrite = "<" + type + "-" + k + "-" + t + "-" + in + ">" + s + "</" + type + "-" + k + "-" + t + "-" +  in + ">\n";
			} catch (NullPointerException e) {
				System.out.println("catch e dhora khaise");
				stringToWrite = "<" + "methodCall" + ">" + s + "</" + "methodCall" +">\n";				
			}
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

		try{
			out.write(stringToWrite);
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}	
	}



	void eat(String s) {
		String token = advanceWithoutEating();
		
		if (tokenType(s).equals("keyword") || tokenType(s).equals("symbol")){
			// if (s.equals(token)) {
			// 	writee(token);
			// }
			// else
			// 	System.out.println(token+"->"+"Your code has error");
			writee(s);
		}
		else if (tokenType(s).equals(tokenType(token))) {
			writee(token);
		}
		else {
			System.out.println(token+"->"+"Your code has error");
		}
	}

	String advanceWithoutEating() {
		String token = tokens.get(currentIndex);
		currentIndex++;
		return token;
	}

	String advanceWithoutIncrementing() {
		String token = tokens.get(currentIndex);
		return token;
	}

	void compileClass() {
		try{
			out.write("<class>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
		eat("class");
		tempToken = advanceWithoutEating();
		symbolTable.setClassName(tempToken);
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
		
		try{
			out.write("</class>\n");
		} catch (IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileClassVarDec() {
		level = "class";
		while (2 > 1) {
			String token = tokens.get(currentIndex);
			
			if (token.equals("field") || token.equals("static"))
				singleLineClassVar();
			else
				break;
		}

		level = "none";

	} 

	void singleLineClassVar() {
		try{
			out.write("<classVarDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}

		//state = "defined";
		kind = advanceWithoutEating();
		type = advanceWithoutEating();

		while (2 > 1) {
			String token = advanceWithoutEating();
			
			if(token.equals(";"))
				break;
			if(! token.equals(","))
				symbolTable.defineIdentifier("class", token, type, kind);
			writee(token);
		}

		try{
			out.write("</classVarDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileSubroutineDec() {
		try{
			out.write("<subroutineDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		symbolTable.startSubroutine();
		String token = null;
		token = advanceWithoutEating();
		writee(token);
		token = advanceWithoutEating();
		writee(token);
		token = advanceWithoutEating();
		writee(token);
		eat("(");
		level = "sub";
		if(! advanceWithoutIncrementing().equals(")"))
			compileParameterList();
		eat(")");
		compileSubroutineBody();
		
		try{
			out.write("</subroutineDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileParameterList() {
		try{
			out.write("<parameterList>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		kind = "argument";
		level = "sub";
		
		while (2 > 1) {
			if(advanceWithoutIncrementing().equals(")"))
				break;
			if(advanceWithoutIncrementing().equals(","))
				eat(",");
			type = advanceWithoutIncrementing();
			eat(type);
			//System.out.println(type);
			String token = advanceWithoutIncrementing();
			//System.out.println(token);
			symbolTable.defineIdentifier(level, token, type, kind);
			eat(token);
		}
		
		try{
			out.write("</parameterList>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}

		//level = "none";
	}

	void compileSubroutineBody() {
		try{
			out.write("<subroutineBody>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
		eat("{");
		compileVarDec();
		compileStatements();
		eat("}");
		
		try{
			out.write("</subroutineBody>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileVarDec() {
		while (2 > 1) {
			String token = advanceWithoutIncrementing();
			
			if(token.equals("var"))
				singleLineVarDec();
			else
				break;
		}
	}

	void singleLineVarDec() {
		try{
			out.write("<varDec>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
		level = "sub";
		kind = "local";
		eat("var");
		type = advanceWithoutIncrementing();
		eat(type);
		String token = advanceWithoutIncrementing();
		symbolTable.defineIdentifier(level, token, type, kind);
		eat(token);

		while (! advanceWithoutIncrementing().equals(";")) {
			eat(",");
			token = advanceWithoutEating();
			symbolTable.defineIdentifier(level, token, type, kind);
			eat(token);
		}
		eat(";");
		
		try{
			out.write("</varDec>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileStatements() {
		try{
			out.write("<statements>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
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
		
		try{
			out.write("</statements>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileLet() {
		try{
			out.write("<letStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
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
		try{
			out.write("</letStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileIf() {
		try{
			out.write("<ifStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
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
		
		try{
			out.write("</ifStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileWhile() {
		try{
			out.write("<whileStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
		eat("while");
		eat("(");
		compileExpression();
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
		
		try{
			out.write("</whileStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileDo() {
		try{
			out.write("<doStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
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
		
		try{
			out.write("</doStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileReturn() {
		try{
			out.write("<returnStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
		eat("return");
		
		if (! tokens.get(currentIndex).equals(";"))
			compileExpression();
		eat(";");
		
		try{
			out.write("</returnStatement>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileExpression() {
		try{
			out.write("<expression>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}

		String tempp = tokens.get(currentIndex);

		while ( ! (tempp.equals(";") || tempp.equals("]") || tempp.equals(")") || tempp.equals("}") || tempp.equals(",") )) {
			if (tempp.equals("(")) {
				try{
					out.write("<term>\n");
				} catch (IOException e) {
					System.out.println("Couldn't write in file");
				}

				eat("(");
				compileExpression();
				eat(")");

				try{
					out.write("</term>\n");
				} catch (IOException e) {
					System.out.println("Couldn't write in file");
				}
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
		try{
			out.write("</expression>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileTerm() {
		try{
			out.write("<term>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
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
		try{
			out.write("</term>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}

	void compileExpressionList() {
		try{
			out.write("<expressionList>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}

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
		try{
			out.write("</expressionList>\n");
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}
}