import java.util.*;
import java.io.*;
import java.lang.String;

public class Tokenizer{

	Map<String,String> map=new HashMap<String,String>();

	String[] keywords={"class","constructor","function","method","field","static","var","int","char","boolean","void","true","false","null","this","let","do","if","else","while","return"};

	String[] symbols={"[","]","(",")","{","}",".",",",";","+","-","*","/","&","|","<",">","=",".","~"};
	
	void mapInitialize(){
		for(String s : keywords){
			map.put(s,"keyword");
		}

		for(String s : symbols){
			map.put(s,"symbol");
		}
	}


	List<String> tokenize(File f){

		Scanner sc=null;
		Scanner sc2=null;
		int flag=0,brk=0;
		String temp="";

		List<String> tokens=new ArrayList<String>();

		try{
			sc=new Scanner(f);
		}catch(FileNotFoundException e){
			System.out.println(f.getName()+" ->File not found\n");
		}

		while(sc.hasNextLine()){
			String st=sc.nextLine();
			if(!st.equals("")){
				char ch=st.charAt(0);
				char ch2;
				try{
					ch2=st.charAt(1);
				}catch(StringIndexOutOfBoundsException s){
					ch2=' ';
				}
				if(ch=='/' || (ch==' ' && ch2=='*')){
					continue;
				}
				else{
					sc2=new Scanner(st);
					while(sc2.hasNext()){
						if(brk==1){
							brk=0;
							break;
						}
						String token=sc2.next();
						String[] str= token.split("(?<=[~.;,\\[\\])(\"])|(?=[~.;,\\[\\])(\"])"); 

						for(int i=0;i<str.length;i++){

							int num=str[i].charAt(0);
							if(str[i].charAt(0)=='/'){
								brk=1;
								continue;
							}

							if(str[i].equals("\"") && flag==0){
								flag=1;
								temp+=str[i];
							}
							else if(str[i].equals("\"") && flag==1){
								temp+=str[i];
								flag=0;
								tokens.add(temp);
								temp="";
							}
							else if(flag==1){
								temp+=str[i];
							}
							else{
								tokens.add(str[i]);
							}
						}
					}
				}
			}
		}
		return tokens;
	}

	String tokenType(String s){
		String val=map.get(s);

		if(val==null){
			char num=s.charAt(0);
			
			if(num>=48 && num<=57){
				val="integerConstant";
			}
			else if(num=='"'){
				val="stringConstant";
			}
			else{
				val="identifier";
			}
		}
		return val;
	}
}