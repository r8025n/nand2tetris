import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.String;

public class Tokenizer{
	public static void main(String args[]){

		Scanner sc=null;
		Scanner sc2=null;
		String temp=null;


		try{
			sc=new Scanner(new File(args[0]));//.useDelimiter(" |,");
		}catch(FileNotFoundException e){
			System.out.println("File not found\n");
		}


		while(sc.hasNextLine()){
			String st=sc.nextLine();
			if(!st.equals("")){
				char ch=st.charAt(0);
				if(ch=='/'){
					continue;
				}
				else{
					sc2=new Scanner(st);
						while(sc2.hasNext()){
							String token=sc2.next();
							System.out.println(token);
					}
				}
			}
		}
	}
}