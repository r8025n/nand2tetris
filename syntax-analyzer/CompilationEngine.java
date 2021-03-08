import java.util.*;
import java.io.*;
import java.lang.String;

public class test2 extends test{

	File fileList[];
	String[] outname;
	BufferedWriter out=null;
	String outfile=null;
	int dir=0,i=0;

	List<String> tokens;

	test2(String name){
		File dirpath=new File(name);

		if(!dirpath.isDirectory()){
			fileList=new File[1];
			fileList[0]=dirpath;
			outname= name.split("[.]");
		}
		else{
			fileList= dirpath.listFiles();
			dir=1;
			outname=new String[1];
			outname[0]=name;
		}
	}


	void parser(){

		for(File f : fileList){
			String[] fname=f.getName().split("[.]");
			if(fname[1].equals("jack")){
				if(dir==0){
					outfile=outname[0]+".xml";
				}
				else{
					outfile=outname[0]+"/"+fname[0]+".xml";
				}
				//output file
				try{
					FileWriter fileout=new FileWriter(outfile);
					out = new BufferedWriter(fileout);
				}catch(IOException k){
					System.out.println(outfile + " ->Couldn't open the output file");
				}
				

				tokens=tokenize(f);

				compileClass();
				try{
					out.close();
				}catch(IOException k){
					System.out.println(outfile + " ->Couldn't close the output file");
				}
			}
		}
	}

	void writee(String s){
		String val=tokenType(s);
		String stringToWrite="";
		if(val.equals("stringConstant")){
			String temp="";
			for(int k=1;k<s.length()-1;k++){
				temp+=s.charAt(k);
			}
			stringToWrite="<"+val+">"+temp+"</"+val+">\n";
		}
		else if(s.equals("<")){
			stringToWrite="<"+val+">"+"&lt;"+"</"+val+">\n";
		}
		else if(s.equals(">")){
			stringToWrite="<"+val+">"+"&gt;"+"</"+val+">\n";
		}
		else if(s.equals("\"")){
			stringToWrite="<"+val+">"+"&quot;"+"</"+val+">\n";
		}
		else if(s.equals("&")){
			stringToWrite="<"+val+">"+"&amp;"+"</"+val+">\n";
		}
		else{
			stringToWrite="<"+val+">"+s+"</"+val+">\n";
		}

		try{
			out.write(stringToWrite);
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}	
	}



	void eat(String s){
		String token=tokens.get(i);
		i++;
		if(tokenType(s).equals("keyword") || tokenType(s).equals("symbol")){
			if(s.equals(token)){
				writee(token);
			}
			else
				System.out.println(token+"->"+"Your code has error");
		}
		else if(tokenType(s).equals(tokenType(token))){
			writee(token);
		}
		else{
			System.out.println(token+"->"+"Your code has error");
		}
	}



	void compileClass(){
		try{
			out.write("<class>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("class");
		eat("abc");
		eat("{");
		compileClassVarDec();
		while(2>1){
			String token=tokens.get(i);
			if(token.equals("constructor") || token.equals("function") || token.equals("method"))
				compileSubroutineDec();
			else
				break;
		}
		eat("}");
		try{
			out.write("</class>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileClassVarDec(){
		while(2>1){
			String token=tokens.get(i);
			if(token.equals("field") || token.equals("static"))
				singleLineClassVar();
			else
				break;
		}

	} 

	void singleLineClassVar(){
		try{
			out.write("<classVarDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		String token=tokens.get(i);

		if(token.equals("static"))
			eat("static");
		else
			eat("field");

		while(2>1){
			token=tokens.get(i);
			if(token.equals(";"))
				break;
			writee(token);
			i++;
		}
		eat(";");
		try{
			out.write("</classVarDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileSubroutineDec(){
		try{
			out.write("<subroutineDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		String token=null;
		token=tokens.get(i);
		i++;
		writee(token);
		token=tokens.get(i);
		i++;
		writee(token);
		token=tokens.get(i);
		i++;
		writee(token);
		eat("(");
		compileParameterList();
		eat(")");
		compileSubroutineBody();
		try{
			out.write("</subroutineDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileParameterList(){
		try{
			out.write("<parameterList>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}

		while(2>1){
			String token=tokens.get(i);
			if(token.equals(")"))
				break;
			else
				writee(token);
			i++;
		}
		try{
			out.write("</parameterList>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileSubroutineBody(){
		try{
			out.write("<subroutineBody>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("{");
		compileVarDec();
		compileStatements();
		eat("}");
		try{
			out.write("</subroutineBody>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileVarDec(){
		while(2>1){
			String token=tokens.get(i);
			if(token.equals("var"))
				singleLineVarDec();
			else
				break;
		}
	}

	void singleLineVarDec(){
		try{
			out.write("<varDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("var");
		writee(tokens.get(i));
		i++;
		eat("abc");
		String token=tokens.get(i);
		while(!token.equals(";")){
			eat(",");
			eat("abc");
			token=tokens.get(i);
		}
		eat(";");
		try{
			out.write("</varDec>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileStatements(){
		try{
			out.write("<statements>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		while(2!=1){
			String token=tokens.get(i);

			if(token.equals("if"))
				compileIf();
			else if(token.equals("while"))
				compileWhile();
			else if(token.equals("let"))
				compileLet();
			else if(token.equals("do"))
				compileDo();
			else if(token.equals("return"))
				compileReturn();
			else
				break;
		}
		try{
			out.write("</statements>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileLet(){
		try{
			out.write("<letStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("let");
		eat("abc");
		if(tokens.get(i).equals("[")){
			eat("[");
			compileExpression();
			eat("]");
		}
		eat("=");
		compileExpression();
		eat(";");
		try{
			out.write("</letStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileIf(){
		try{
			out.write("<ifStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("if");
		eat("(");
		compileExpression();
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
		if(tokens.get(i).equals("else")){
			eat("else");
			eat("{");
			compileStatements();
			eat("}");
		}
		try{
			out.write("</ifStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileWhile(){
		try{
			out.write("<whileStatement>\n");
		}catch(IOException e){
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
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileDo(){
		try{
			out.write("<doStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("do");
		eat("abc");

		String temp=tokens.get(i);
		if(temp.equals(".")){
			eat(".");
			eat("abc");
		}

		eat("(");
		compileExpressionList();
		eat(")");
		eat(";");
		try{
			out.write("</doStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileReturn(){
		try{
			out.write("<returnStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		eat("return");
		if(!tokens.get(i).equals(";"))
			compileExpression();
		eat(";");
		try{
			out.write("</returnStatement>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileExpression(){
		try{
			out.write("<expression>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}

		compileTerm();
		String temp=tokens.get(i);
		while(!(temp.equals(";") || temp.equals("]") || temp.equals(")") || temp.equals("}") || temp.equals(",") )){
			if(tokenType(temp).equals("symbol")){
				writee(tokens.get(i));
				i++;
			}
			else
				compileTerm();

			temp=tokens.get(i);
		}
		try{
			out.write("</expression>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileTerm(){
		try{
			out.write("<term>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		int k=i+1;
		if(tokens.get(k).equals(".")){
			
			eat("abc");
			eat(".");
			eat("abc");
			eat("(");
			compileExpressionList();
			eat(")");
		}
		else{
			if(tokenType(tokens.get(i)).equals("identifier"))
				eat("abc");
			else{
				writee(tokens.get(i));
				i++;
			}
		}
		try{
			out.write("</term>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}

	void compileExpressionList(){
		try{
			out.write("<expressionList>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
		while(2!=1){
			String token=tokens.get(i);
			if(!token.equals(")")){
				compileExpression();
				if(!tokens.get(i).equals(")"))
					eat(",");
			}
			else
				break;
		}
		try{
			out.write("</expressionList>\n");
		}catch(IOException e){
			System.out.println("Couldn't write in file");
		}
	}
}