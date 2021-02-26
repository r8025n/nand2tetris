import java.util.*;
import java.io.*;
import java.lang.String;

public class test{
	public static void main(String args[]){

		Scanner sc=null;
		Scanner sc2=null;
		int flag=0,brk=0;
		String temp="";
		Map<String,String> map=new HashMap<String,String>();

		map.put("class","keyword");
		map.put("constructor","keyword");
		map.put("function","keyword");
		map.put("method","keyword");
		map.put("field","keyword");
		map.put("static","keyword");
		map.put("var","keyword");
		map.put("int","keyword");
		map.put("char","keyword");
		map.put("boolean","keyword");
		map.put("void","keyword");
		map.put("true","keyword");
		map.put("false","keyword");
		map.put("null","keyword");
		map.put("this","keyword");
		map.put("let","keyword");
		map.put("do","keyword");
		map.put("if","keyword");
		map.put("else","keyword");
		map.put("while","keyword");
		map.put("return","keyword");

		map.put("[","symbol");
		map.put("]","symbol");
		map.put("(","symbol");
		map.put(")","symbol");
		map.put("{","symbol");
		map.put("}","symbol");
		map.put(".","symbol");
		map.put(",","symbol");
		map.put(";","symbol");
		map.put("+","symbol");
		map.put("-","symbol");
		map.put("*","symbol");
		map.put("/","symbol");
		map.put("&","symbol");
		map.put("|","symbol");
		map.put("<","symbol");
		map.put(">","symbol");
		map.put("=","symbol");
		map.put("~","symbol");

		try{
			sc=new Scanner(new File(args[0])).useDelimiter(" |,");
		}catch(FileNotFoundException e){
			System.out.println("File not found\n");
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

							String val=map.get(str[i]);

							if(val==null){
								//System.out.println(str[i]+"->Not included in the map\n");
								int num=str[i].charAt(0);
								if(str[i].charAt(0)=='/'){
									brk=1;
									continue;
								}
								if(num>=48 && num<=57){
									//int integer=Integer.valueOf(str[i]);
									val="integerConstant";
									System.out.println("<"+val+">"+str[i]+"</"+val+">");
								}

								else if(str[i].equals("\"") && flag==0){
									flag=1;
									temp="";
								}
								else if(str[i].equals("\"") && flag==1){
									flag=0;
									System.out.println("<stringConstant>"+temp+"</stringConstant>");
								}
								else if(flag==1){
									temp+=str[i];
								}
								else{
									System.out.println("<identifier>"+str[i]+"</identifier>");
								}
							}
							else{
								if(str[i].equals("<"))
									System.out.println("<"+val+">"+"&lt;"+"</"+val+">");
								else if(str[i].equals(">"))
									System.out.println("<"+val+">"+"&gt;"+"</"+val+">");
								else if(str[i].equals("&"))
									System.out.println("<"+val+">"+"&amp;"+"</"+val+">");
								else if(str[i].equals("\""))
									System.out.println("<"+val+">"+"&quot;"+"</"+val+">");
								else
									System.out.println("<"+val+">"+str[i]+"</"+val+">");
							}
						}
					}
				}
			}
		}

	}
}