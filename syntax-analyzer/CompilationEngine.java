Public class CompilationEngine{

	File fileList[];
	String[] outname;
	BufferedWriter out=null;
	String outfile=null;
	int dir=0;

	CompilationEngine(String name){
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
					out = new BufferedWriter(new FileWriter(outfile));
				}catch(IOException k){
					System.out.println(outfile + " ->Couldn't open the output file");
				}
				//input file
				try{
					sc=new Scanner(f);
				}catch(FileNotFoundException e){
					System.out.println(f.getName()+" ->File not found\n");
				}

				
			}
		}
	}


	void write(String s){
		String val=findType(s);
		System.out.println();
	}

	void eat(String s){
		String token=sc.next();
		if(!s.equals(token)){
			System.out.println("Your code has error");
		}
		else{
			write(s);
		}
	}

	void compileClass(){

	}

	void compileClassVarDec(){

	} 

	void compileSubroutineDec(){

	}

	void compileParameterList(){

	}

	void compileSubroutineBody(){

	}

	void compileVarDc(){

	}

	void compileStatements(){

	}

	void compileLet(){

	}

	void compileIf(){

	}

	void compileWhile(){

	}

	void compileDo(){

	}

	void compileReturn(){

	}

	void compileExpression(){

	}

	void compileTerm(){

	}

	void compileExpressionList(){

	}

}