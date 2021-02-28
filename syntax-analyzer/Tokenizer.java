import java.util.*;
import java.io.*;
import java.lang.String;

public class test{
	public static void main(String args[]){

		Scanner sc=null;
		Scanner sc2=null;
		int flag=0,brk=0;
		String temp="";

		String name=args[0];
		File dirpath=new File(name);
		File fileList[];
		String[] outname;
		int dir=0;
		BufferedWriter out=null;
		String outfile=null;

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

		if(!dirpath.isDirectory()){
			//System.out.println("it is a single file, not a directory");
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

		for(File f : fileList){
			String[] fname=f.getName().split("[.]");
			if(fname[1].equals("jack")){
				if(dir==0){
					outfile=outname[0]+".xml";
				}
				else{
					outfile=outname[0]+"/"+fname[0]+".xml";
				}

				try{
					out = new BufferedWriter(new FileWriter(outfile));
					out.write("<tokens>\n");
				}catch(IOException k){
					System.out.println(outfile + " ->Couldn't open the output file");
				}

				try{
					sc=new Scanner(f);
					System.out.println(f.getName()+"OPENED");
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

									String val=map.get(str[i]);

									if(val==null){
										//System.out.println(str[i]+"->Not included in the map\n");
										int num=str[i].charAt(0);
										if(str[i].charAt(0)=='/'){
											brk=1;
											continue;
										}
										if(num>=48 && num<=57){
											val="integerConstant";
											//System.out.println("<"+val+">"+str[i]+"</"+val+">");
											try{
												out.write("<"+val+">"+str[i]+"</"+val+">\n");
											}catch(IOException k){
												System.out.println("Something is Wrong");
											}
										}

										else if(str[i].equals("\"") && flag==0){
											flag=1;
											temp="";
										}
										else if(str[i].equals("\"") && flag==1){
											flag=0;
											//System.out.println("<stringConstant>"+temp+"</stringConstant>");
											try{
												out.write("<stringConstant>"+temp+"</stringConstant>\n");
											}catch(IOException k){
												System.out.println("Something is Wrong");
											}
										}
										else if(flag==1){
											temp+=str[i];
										}
										else{
											//System.out.println("<identifier>"+str[i]+"</identifier>");
											try{
												out.write("<identifier>"+str[i]+"</identifier>\n");
											}catch(IOException k){
												System.out.println("Something is Wrong");
											}
										}
									}
									else{
										try{
											if(str[i].equals("<"))
												out.write("<"+val+">"+"&lt;"+"</"+val+">\n");
											else if(str[i].equals(">"))
												out.write("<"+val+">"+"&gt;"+"</"+val+">\n");
											else if(str[i].equals("&"))
												out.write("<"+val+">"+"&amp;"+"</"+val+">\n");
											else if(str[i].equals("\""))
												out.write("<"+val+">"+"&quot;"+"</"+val+">\n");
											else
												out.write("<"+val+">"+str[i]+"</"+val+">\n");
										}catch(IOException k){
											System.out.println("Something is Wrong");
										}
									}
								}
							}
						}
					}
				}
				try{
					out.write("</tokens>");
					out.close();
				}catch(IOException k){
					System.out.println("File didn't close properly");
				}
			}
		}

	}
}