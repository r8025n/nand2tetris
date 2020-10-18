#include<stdio.h>
#include<ctype.h>
#include<string.h>
#include<stdbool.h>

int main(int argc,char *argv[]){
	char filename[25],str[25];
	strcpy(str,argv[1]);
	int i=0;

	while(str[i]!='.'){
		filename[i]=str[i];
		i++;
	}
	filename[i]='\0';

	FILE *in=fopen(argv[1],"r");
	char name_out[25],stat[25];
	sprintf(name_out,"%s.vm",filename);
	FILE *out=fopen(name_out,"w");

	char line[50],op[10],mem[10];
	int val;

	while(fgets(line,50,in)!=NULL){
		if(isspace(line[0])==false && line[0]!='/'){
			fprintf(out,"//%s",line);
			sscanf(line,"%s %s %d",op,mem,&val);

			if(strcmp(op,"push")==0){
				if(strcmp(mem,"local")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@LCL\nD=D+M\nA=D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"argument")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@ARG\nD=D+M\nA=D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"this")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@THIS\nD=D+M\nA=D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"that")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@THAT\nD=D+M\nA=D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"constant")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"temp")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@5\nD=D+M\nA=D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"static")==0){
					sprintf(stat,"%s.%d",filename,val);
					fprintf(out,"@%s\n",stat);
					fprintf(out,"D=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
				}
				else if(strcmp(mem,"pointer")==0){
					if(val==0){
						//this
						fprintf(out,"@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
					}
					else if(val==1){
						//that
						fprintf(out,"@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
					}
				}

			}
			else if(strcmp(op,"pop")==0){
				if(strcmp(mem,"local")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@LCL\nD=D+M\n@R13\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@R13\nA=M\nM=D\n");
				}
				else if(strcmp(mem,"argument")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@ARG\nD=D+M\n@R13\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@R13\nA=M\nM=D\n");
				}
				else if(strcmp(mem,"this")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@THIS\nD=D+M\n@R13\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@R13\nA=M\nM=D\n");
				}
				else if(strcmp(mem,"that")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@THAT\nD=D+M\n@R13\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@R13\nA=M\nM=D\n");
				}
				else if(strcmp(mem,"temp")==0){
					fprintf(out,"@%d\n",val);
					fprintf(out,"D=A\n@5\nD=D+M\n@R13\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@R13\nA=M\nM=D\n");
				}
				else if(strcmp(mem,"static")==0){
					sprintf(stat,"%s.%d",filename,val);
					fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n");
					fprintf(out,"@%s\nM=D\n",stat);
				}
				else if(strcmp(mem,"pointer")==0){
					if(val==0){
						//this
						fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@THIS\nM=D\n");
					}
					else if(val==1){
						//that
						fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@THAT\nM=D\n");
					}
				}
			}
			else if(strcmp(op,"add")){

			}
			else if(strcmp(op,"sub")){

			}
			else if(strcmp(op,"neg")){

			}
			else if(strcmp(op,"eq")){

			}
			else if(strcmp(op,"gt")){

			}
			else if(strcmp(op,"lt")){

			}
			else if(strcmp(op,"and")){

			}
			else if(strcmp(op,"or")){

			}
			else if(strcmp(op,"not")){

			}
		}
	}

}
