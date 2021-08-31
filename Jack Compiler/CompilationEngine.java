import java.util.*;
import java.io.*;
import java.lang.String;

public class CompilationEngine extends Tokenizer{

	File fileList[];
	String[] outname;
	BufferedWriter out = null;
	String outfile = null;
	String tempToken = null;
	boolean isDirectory = false, isTypeVoid = false;
	int currentIndex = 0, localCount = 0, argCount = 0, labelIndex = 0;
	SymbolTable symbolTable = null;
	String type = "", kind = "", level = "", state = "", functionName = "", subroutineType = "";
	VMWriter vmWriter = null;
	List<String> tokens;

	CompilationEngine(String fileName) {
		File dirpath=new File(fileName);
		vmWriter = new VMWriter();

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

	void toggleState() {
		if(state.equals("use") || state.equals(""))
			state = "define";
		else
			state = "use";
	}

	void parser() {
		for(File f : fileList){
			String[] fname=f.getName().split("[.]");
			if(fname[1].equals("jack")){
				symbolTable = new SymbolTable();
				currentIndex = 0;
				
				if(isDirectory == false){
					outfile=outname[0] + ".vm";
				}
				else{
					outfile=outname[0] + "/" + fname[0] + ".vm";
				}
				
				//output file
				try{
					FileWriter fileout=new FileWriter(outfile);
					out = new BufferedWriter(fileout);
					vmWriter.setBufferedWriter(out);
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
				stringToWrite = "<" + state + "-" + k + "-" + t + "-" + in + ">" + s + "</" + state + "-" + k + "-" + t + "-" +  in + ">\n";
			} catch (NullPointerException e) {
				if (s.equals(symbolTable.getClassName()))
					stringToWrite = "<" + "className" + ">" + s + "</" + "className" +">\n";
				else	
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
			//writee(s);
		}
		else if (tokenType(s).equals(tokenType(token))) {
			//writee(token);
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
		eat("class");
		tempToken = advanceWithoutEating();
		symbolTable.setClassName(tempToken);
		eat("{");
		compileClassVarDec();

		while (2 > 1) {
			String token = advanceWithoutIncrementing();
			// for(int i=0;i<tokens.size();i++){
			// 	System.out.println(tokens.get(i));
			// }
			
			if(token.equals("constructor") || token.equals("function") || token.equals("method")){
				System.out.println(tokens.size());
				compileSubroutineDec();
			}
			else
				break;
		}
		
		eat("}");
	}

	void compileClassVarDec() {
		level = "class";
		toggleState();

		while (2 > 1) {
			String token = tokens.get(currentIndex);
			
			if (token.equals("field") || token.equals("static"))
				singleLineClassVar();
			else
				break;
		}

		level = "none";
		toggleState();
	} 

	void singleLineClassVar() {
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
	}

	void compileSubroutineDec() {
		String token = null;
		subroutineType = advanceWithoutEating();
		symbolTable.startSubroutine(subroutineType);
		String returnType = advanceWithoutEating();
		
		if(returnType.equals("void"))
			isTypeVoid = true;
		else
			isTypeVoid = false;
		//writee(token);
		String name= advanceWithoutEating();
		functionName = symbolTable.getClassName() + "." + name;
		//writee(token);
		eat("(");
		level = "sub";
		if(! advanceWithoutIncrementing().equals(")")){
			compileParameterList();
		}
		eat(")");
		compileSubroutineBody();
	}

	void compileParameterList() {
		toggleState();
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

		toggleState();
	}

	void compileSubroutineBody() {
		eat("{");
		toggleState();
		compileVarDec();
		vmWriter.writeFunction(functionName, localCount);
		toggleState();
		compileStatements();
		eat("}");
		if(isTypeVoid)
			vmWriter.writePush("constant", 0);

		vmWriter.writeReturn();
	}

	void compileVarDec() {
		localCount = 0;
		while (2 > 1) {
			String token = advanceWithoutIncrementing();
			
			if(token.equals("var"))
				singleLineVarDec();
			else
				break;
		}
	}

	void singleLineVarDec() {
		level = "sub";
		kind = "local";
		eat("var");
		type = advanceWithoutIncrementing();
		eat(type);
		String name = advanceWithoutIncrementing();
		symbolTable.defineIdentifier(level, name, type, kind);
		localCount++;
		eat(name);

		while (! advanceWithoutIncrementing().equals(";")) {
			eat(",");
			name = advanceWithoutEating();
			symbolTable.defineIdentifier(level, name, type, kind);
			localCount++;
			eat(name);
		}
		eat(";");
	}

	void compileStatements() {
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
	}

	void compileLet() {
		boolean isLeftSideArray = false;
		eat("let");
		String leftSide = advanceWithoutEating();
		
		// if it is an array
		if (advanceWithoutIncrementing().equals("[")){
			isLeftSideArray = true;
			vmWriter.writePush(symbolTable.getKind(leftSide), symbolTable.getIndex(leftSide)); //pushes the array base to stack
			eat("[");
			compileExpression();
			eat("]");
			//vmWriter.writePush("temp", 0);
			vmWriter.writeArithmatic("+"); //adds calculated expression with array base
		}
		eat("=");
		compileExpression();

		if (isLeftSideArray) {
			vmWriter.writePop("temp", 0);
			vmWriter.writePop("pointer", 1); //store the summed value to THAT
			vmWriter.writePush("temp", 0);
			vmWriter.writePop("that", 0);
		}
		else{
			//vmWriter.writePush("temp", 0);
			vmWriter.writePop(symbolTable.getKind(leftSide), symbolTable.getIndex(leftSide));
		}
		eat(";");
	}

	void compileIf() {
		String label_1 = "L" + labelIndex;
		labelIndex++;
		String label_2 = "L" + labelIndex;
		labelIndex++;
		eat("if");
		eat("(");
		compileExpression();
		vmWriter.writeLogic("~");
		vmWriter.writeGoto("conditional", label_1);
		eat(")");
		eat("{");
		compileStatements();
		vmWriter.writeGoto("unconditional", label_2);
		eat("}");
		vmWriter.writeLabel(label_1);
		
		if (advanceWithoutIncrementing().equals("else")) {
			eat("else");
			eat("{");
			compileStatements();
			eat("}");
		}
		vmWriter.writeLabel(label_2);
	}

	void compileWhile() {
		String label_1 = "L" + labelIndex;
		labelIndex++;
		String label_2 = "L" + labelIndex;
		labelIndex++;
		vmWriter.writeLabel(label_1);
		eat("while");
		eat("(");
		compileExpression();
		vmWriter.writeLogic("~");
		vmWriter.writeGoto("conditional", label_2);
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
		vmWriter.writeGoto("unconditional", label_1);
		vmWriter.writeLabel(label_2);
	}

	void compileDo() {
		eat("do");
		String methodCall = advanceWithoutEating();
		// System.out.println(methodCall);
		String temp = advanceWithoutIncrementing();
		String obj = "", method = "";

		if (temp.equals(".")) {
			if(! tokenType(methodCall).equals("keyword")){	
				eat(".");
				obj = methodCall;
				methodCall = advanceWithoutEating();
				argCount++;
				vmWriter.writePush(symbolTable.getKind(obj), symbolTable.getIndex(obj));
			}
			else{
				eat(".");
				methodCall = methodCall + "." + advanceWithoutEating();
			}
		}

		eat("(");
		compileExpressionList();
		eat(")");
		eat(";");

		vmWriter.writeCall(methodCall, argCount);
		vmWriter.writePop("temp", 0);
	}

	void compileReturn() {
		eat("return");
		
		if (! advanceWithoutIncrementing().equals(";"))
			compileExpression();
		eat(";");
	}

	void compileExpression() {
		String tempp = advanceWithoutIncrementing();
		String op = "";

		while ( ! (tempp.equals(";") || tempp.equals("]") || tempp.equals(")") || tempp.equals("}") || tempp.equals(",") )) {
			if (tempp.equals("(")) {
				eat("(");
				compileExpression();
				eat(")");
			}
			else if (tempp.equals("[")) {
				eat("[");
				compileExpression();
				eat("]");
			}
			else if ((tempp.equals("~") || (tempp.equals("-")) && tokens.get(currentIndex-1).equals("("))) {
				compileTerm();
			}
			else if(isMathmaticalOp(tempp)) {
				op = tempp;
				currentIndex++;
				compileTerm();
				vmWriter.writeArithmatic(op);
			}
			else if(isLogicalOp(tempp)) {
				op = tempp;
				currentIndex++;
				compileTerm();
				vmWriter.writeLogic(op);
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
			tempp = advanceWithoutIncrementing();
		}
	}

	void compileTerm() {
		int k = currentIndex+1;
		//argCount = 0;
		String methodCall = "", obj = "";

		if (tokens.get(k).equals(".")) {
			obj = advanceWithoutEating();
			eat(".");
			methodCall = advanceWithoutEating();
			vmWriter.writePush(symbolTable.getKind(obj), symbolTable.getIndex(obj)); //pushing object as the first argument
			eat("(");
			compileExpressionList();
			eat(")");
			vmWriter.writeCall(methodCall, argCount + 1);
			//argCount = 0;
		}
		else if (advanceWithoutIncrementing().equals("~")) {
			String op = advanceWithoutEating();
			compileTerm();
			vmWriter.writeLogic("~");
		}
		else if (tokens.get(currentIndex).equals("(")) {
			eat("(");
			compileExpression();
			eat(")");
		}
		else if (tokens.get(currentIndex).equals("-") && tokens.get(currentIndex-1).equals("(")) {
			String op = advanceWithoutEating();
			compileTerm();
			vmWriter.writeMisc("neg");
		}
		else {
			if (tokenType(tokens.get(currentIndex)).equals("identifier")) {
				String var = advanceWithoutEating();
				vmWriter.writePush(symbolTable.getKind(var), symbolTable.getIndex(var));
				if (tokens.get(currentIndex).equals("[")) {
					eat("[");
					compileExpression();
					eat("]");
				}
			}
			else if(tokenType(tokens.get(currentIndex)).equals("integerConstant")){
				int intVal = Integer.parseInt(advanceWithoutEating());
				vmWriter.writePush("constant", intVal);
			}
			else if(tokenType(tokens.get(currentIndex)).equals("stringConstant")) {
				
			}
			else {
				writee(tokens.get(currentIndex));
				currentIndex++;
			}
		}
	}

	void compileExpressionList() {
		argCount = 0;

		while (2 != 1) {
			String token = advanceWithoutIncrementing();

			if (! token.equals(")")) {
				argCount++;
				//System.out.println("argCount = " +argCount);
				compileExpression();
				
				if (! tokens.get(currentIndex).equals(")")){
					argCount++;
					//System.out.println("argCount = " +argCount);
					eat(",");
				}
			}
			else
				break;
		}
	}

	boolean isMathmaticalOp(String token) {
		String[] ops = {"+", "-", "*", "/"};
	
		for(int i = 0; i < 4; i++){
			if(token.equals(ops[i]))
				return true;
		}

		return false;
	}

	boolean isLogicalOp(String token) {
		String[] ops = {"<", ">", "&", "|", "="};
		
		for(int i = 0; i < 5; i++){
			if(token.equals(ops[i]))
				return true;
		}

		return false;
	}
}