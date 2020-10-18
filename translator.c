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
			else if(strcmp(op,"add")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D+M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"sub")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"neg")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nM=-M\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"eq")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D-M\n@EQ_TRUE\nD;JEQ\n@SP\nA=M\nM=0\n@EQ_FALSE\n");
				fprintf(out,"0;JMP\n(EQ_TRUE)\n@SP\nA=M\nM=1\n(EQ_FALSE)\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"gt")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@GT_TRUE\nD;JGT\n@SP\nA=M\n");
				fprintf(out,"M=0\n@GT_FALSE\n0;JMP\n(GT_TRUE)\n@SP\nA=M\nM=1\n(GT_FALSE)\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"lt")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\n@LT_TRUE\nD;JLT\n@SP\nA=M\n");
				fprintf(out,"M=0\n@LT_FALSE\n0;JMP\n(LT_TRUE)\n@SP\nA=M\nM=1\n(LT_FALSE)\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"and")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D-M\n@AND_TRUE\nD;JEQ\n@SP\nA=M\nM=0\n");
				fprintf(out,"@AND_FALSE\n0;JMP\n(AND_TRUE)\n@SP\nA=M\nM=1\n(AND_FALSE)\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"or")==0){
				fprintf(out,"@SP\nM=M-1\nA=MD=M\n@SP\nM=M-1\nA=M\nD=D-M\n@OR_TRUE\nD;JNE\n@SP\nA=M\nM=0\n@OR_FALSE\n");
				fprintf(out,"0;JMP\n(OR_TRUE)\n@SP\nA=M\nM=1\n(OR_FALSE)\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"not")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\nD=D-1\n@MAKE_ZERO\nD;JEQ\n@SP\nA=M\nM=1\n@MAKE_ONE\n");
				fprintf(out,"(MAKE_ZERO)\n@SP\nA=M\nM=0\n(MAKE_ONE)\n@SP\nM=M+1\n");
			}
		}
	}
	fclose(in);
	fclose(out);
	return 0;
}
