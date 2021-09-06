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
	int currentIndex = 0, localCount = 0, argCount = 0, whileLabelCount = 0, ifLabelCount = 0;
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

	void eat(String s) {
		String token = advanceWithoutEating();
		
		if (! (tokenType(s).equals("keyword") || tokenType(s).equals("symbol"))){
			if(! tokenType(s).equals(tokenType(token)))
				System.out.println("Error in your Code...Please Recheck");
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
			
			if(token.equals("constructor") || token.equals("function") || token.equals("method")){
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

		if(kind.equals("field"))
			kind = "this";

		while (2 > 1) {
			String varName = advanceWithoutEating();
			
			if(varName.equals(";"))
				break;
			if(! varName.equals(","))
				symbolTable.defineIdentifier("class", varName, type, kind);
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
		String name= advanceWithoutEating();
		functionName = symbolTable.getClassName() + "." + name;
		eat("(");
		level = "sub";

		if(! advanceWithoutIncrementing().equals(")"))
			compileParameterList();

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
			String token = advanceWithoutIncrementing();
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

		if(subroutineType.equals("constructor")) {
			vmWriter.writePush("constant", symbolTable.varCount("field"));
			vmWriter.writeCall("Memory.alloc", 1);
			vmWriter.writePop("pointer", 0);
		}

		if(subroutineType.equals("method")) {
			vmWriter.writePush("argument", 0);
			vmWriter.writePop("pointer", 0);
		}

		toggleState();
		compileStatements();
		eat("}");

		if(isTypeVoid)
			vmWriter.writePush("constant", 0);

		if(subroutineType.equals("constructor"))
			vmWriter.writePush("pointer", 0);

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
			vmWriter.writePop(symbolTable.getKind(leftSide), symbolTable.getIndex(leftSide));
		}
		eat(";");
	}

	void compileIf() {
		String label_1 = "if_" + ifLabelCount;
		ifLabelCount++;
		String label_2 = "if_" + ifLabelCount;
		ifLabelCount++;
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
		String label_1 = "while_" + whileLabelCount;
		whileLabelCount++;
		String label_2 = "while_" + whileLabelCount;
		whileLabelCount++;
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
		int argCountIncrement = 0;
		eat("do");
		String obj = advanceWithoutEating();
		String methodCall = "";
		String temp = advanceWithoutIncrementing();
		String method = "";

		if (temp.equals(".")) {
			if(symbolTable.searchIdentifier(obj) != null){
				eat(".");
				method = advanceWithoutEating();
				methodCall = symbolTable.getType(obj) + "." + method;
				argCountIncrement++;
				vmWriter.writePush(symbolTable.getKind(obj), symbolTable.getIndex(obj));
			}
			else{
				eat(".");
				methodCall = obj + "." + advanceWithoutEating();
			}
		}
		else{
			argCountIncrement++;
			methodCall = symbolTable.getClassName() + "." + obj;
			vmWriter.writePush("pointer", 0);
		}
		eat("(");
		compileExpressionList();
		eat(")");
		eat(";");
		vmWriter.writeCall(methodCall, argCount + argCountIncrement);
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
			else if (tokenType(tempp).equals("identifier") && tokens.get(currentIndex+1).equals("[")) {
				compileTerm();
			}
			else if ((tempp.equals("~") || (tempp.equals("-")) && (tokens.get(currentIndex-1).equals("(") || tokens.get(currentIndex-1).equals(",")))) {
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
				compileTerm();
			}
			tempp = advanceWithoutIncrementing();
		}
	}

	void compileTerm() {
		int k = currentIndex+1;
		String method = "", obj = "";

		if (tokens.get(k).equals(".")) {
			obj = advanceWithoutEating();
			eat(".");
			method = advanceWithoutEating();
			String methodCall = obj + "." + method;
			eat("(");
			compileExpressionList();
			eat(")");

			if(symbolTable.searchIdentifier(obj) != null){
				argCount++;
				vmWriter.writePush(symbolTable.getKind(obj), symbolTable.getIndex(obj));
				methodCall = symbolTable.getType(obj) + "." + method;
				vmWriter.writeCall(methodCall, argCount);
			}
			else{
				vmWriter.writeCall(methodCall, argCount);
			}	
		}
		else if (advanceWithoutIncrementing().equals("~")) {
			String op = advanceWithoutEating();
			compileTerm();
			vmWriter.writeLogic("~");
		}
		else if (advanceWithoutIncrementing().equals("true")) {
			String token = advanceWithoutEating();
			vmWriter.writePush("constant", 0);
			vmWriter.writeLogic("~");
		}
		else if(advanceWithoutIncrementing().equals("false") || advanceWithoutIncrementing().equals("null")){
			String token = advanceWithoutEating();
			vmWriter.writePush("constant", 0);
		}
		else if (tokens.get(currentIndex).equals("(")) {
			eat("(");
			compileExpression();
			eat(")");
		}
		else if (tokens.get(currentIndex).equals("-") && (tokens.get(currentIndex-1).equals("(") || tokens.get(currentIndex-1).equals(","))) {
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
					vmWriter.writeMisc("add");
					vmWriter.writePop("pointer", 1);
					vmWriter.writePush("that", 0);
				}
			}
			else if(tokenType(tokens.get(currentIndex)).equals("integerConstant")){
				int intVal = Integer.parseInt(advanceWithoutEating());
				vmWriter.writePush("constant", intVal);
			}
			else if(tokenType(tokens.get(currentIndex)).equals("stringConstant")) {
				String temp = advanceWithoutEating();
				String stringToken = temp.substring(1, temp.length()-1);
				int len = stringToken.length();
				vmWriter.writePush("constant", len);
				vmWriter.writeCall("String.new", 1);

				for(int i = 0; i < len; i++){
					int charVal = stringToken.charAt(i);
					vmWriter.writePush("constant", charVal);
					vmWriter.writeCall("String.appendChar", 2);
				}
			}
			else {
				currentIndex++;
			}
		}
	}

	void compileExpressionList() {
		argCount = 0;
		String token = advanceWithoutIncrementing();
		if(token.equals("this"))
			vmWriter.writePush("pointer", 0);

		if(!token.equals(")")){
			argCount++;
			compileExpression();
			while(2 != 1){
				token = advanceWithoutIncrementing();

				if(token.equals("this"))
					vmWriter.writePush("pointer", 0);

				if(!token.equals(")")){
					eat(",");
					argCount++;
					compileExpression();
				}
				else
					break;
			}
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