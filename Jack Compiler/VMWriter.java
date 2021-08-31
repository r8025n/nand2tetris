import java.io.*;
import java.lang.String;

public class VMWriter {

	//private int labelNumber = 0;
	private String stringToWrite = "";
	private BufferedWriter out = null;
	

	void setBufferedWriter(BufferedWriter bufferedWriter) {
		this.out = bufferedWriter;
	}

	void writePush(String segment, int index) {
		stringToWrite = "push " + segment +" "+ index + "\n";
		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writePop(String segment, int index) {
		stringToWrite = "pop " + segment + " " + index + "\n";
		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeArithmatic(String op) {
		if(op.equals("+"))
			stringToWrite = "add\n";
		else if(op.equals("-"))
			stringToWrite = "sub\n";
		else if(op.equals("*"))
			stringToWrite = "call Math.multiply 2\n";
		else if(op.equals("/"))
			stringToWrite = "call Math.divide 2\n";

		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeLogic(String op) {
		if(op.equals("="))
			stringToWrite = "eq\n";
		else if(op.equals("<"))
			stringToWrite = "lt\n";
		else if(op.equals(">"))
			stringToWrite = "gt\n";
		else if(op.equals("&"))
			stringToWrite = "and\n";
		else if(op.equals("|"))
			stringToWrite = "or\n";
		else if(op.equals("~"))
			stringToWrite = "not\n";

		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeLabel(String label) {
		stringToWrite = "label " + label + "\n";
		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeGoto(String state, String label) {
		if(state.equals("conditional"))
			stringToWrite = "if-goto " + label + "\n";
		else
			stringToWrite = "goto " + label + "\n";

		write(stringToWrite);

	}

	void writeCall(String functionName, int nArgs) {
		stringToWrite = "call " + functionName + " " + nArgs + "\n";   
		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeFunction(String functionName, int nLocals) {
		stringToWrite = "function " + functionName + " " + nLocals + "\n";
		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeReturn() {
		stringToWrite = "return\n";
		write(stringToWrite);
		System.out.println(stringToWrite);
	}

	void writeMisc(String misc) {
		stringToWrite = misc + "\n";
		write(stringToWrite);
	}

	void write(String str) {
		try{
			out.write(str);
		} catch (IOException e) {
			System.out.println("Couldn't write in file");
		}
	}
}