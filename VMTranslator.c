//shdfgsdjhfjd
#include<stdio.h>
#include<ctype.h>
#include<string.h>
#include<stdbool.h>

int main(int argc,char *argv[]){
	char filename[25],str[50],path[50];
	strcpy(str,argv[1]);
	
	//for parsing the filename from filepath stored in argv[]
	int i,j,k,len,st;
	len=strlen(str);
    for(i=len-1;i>=0;i--){
        if(str[i]=='/' || str[i]=='\\' || i==0){
            if(i==0){
            	st=i;
            	break;
            }
            else{
            	st=i+1;
            	for(k=0;k<st;k++){
            		path[k]=str[k];
            	}
            	path[k]='\0';
            	break;
            }
        }
    }
    len=len-3;
    for(i=0,j=st;j<len;i++,j++){
        filename[i]=str[j];
    }
    filename[i]='\0';

	FILE *in=fopen(argv[1],"r");
	char name_out[25],stat[50];
	sprintf(name_out,"%s.asm",filename);
	FILE *out=fopen(name_out,"w");

	char line[150],op[10],mem[10];
	int val;
	int eq=0,gt=0,lt=0;

	//started reading from the file
	while(fgets(line,150,in)!=NULL){
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
					fprintf(out,"D=A\n@5\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
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
					fprintf(out,"@%d\nD=A\n@5\nD=D+A\n@R13\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@R13\nA=M\nM=D\n",val);
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
				fprintf(out,"@SP\nA=M\nA=A-1\nM=-M\n");
			}
			else if(strcmp(op,"eq")==0){
				eq++;
				fprintf(out,"@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=D-M\n@EQ_TRUE_%d\nD;JEQ\n@SP\nA=M\nM=0\n@EQ_FALSE_%d\n",eq,eq);
				fprintf(out,"0;JMP\n(EQ_TRUE_%d)\n@SP\nA=M\nM=-1\n(EQ_FALSE_%d)\n@SP\nM=M+1\n",eq,eq);
			}
			else if(strcmp(op,"gt")==0){
				gt++;
				fprintf(out,"@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=M-D\n@GT_TRUE_%d\nD;JGT\n@SP\nA=M\n",gt);
				fprintf(out,"M=0\n@GT_FALSE_%d\n0;JMP\n(GT_TRUE_%d)\n@SP\nA=M\nM=-1\n(GT_FALSE_%d)\n@SP\nM=M+1\n",gt,gt,gt);
			}
			else if(strcmp(op,"lt")==0){
				lt++;
				fprintf(out,"@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=M-D\n@LT_TRUE_%d\nD;JLT\n@SP\nA=M\n",lt);
				fprintf(out,"M=0\n@LT_FALSE_%d\n0;JMP\n(LT_TRUE_%d)\n@SP\nA=M\nM=-1\n(LT_FALSE_%d)\n@SP\nM=M+1\n",lt,lt,lt);
			}
			else if(strcmp(op,"and")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nM=D&M\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"or")==0){
				fprintf(out,"@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nM=D|M\n@SP\nM=M+1\n");
			}
			else if(strcmp(op,"not")==0){
				fprintf(out,"@SP\nA=M\nA=A-1\nM=!M\n");
			}
			else if(strcmp(op,"label")==0){
				fprintf(out,"(%s)\n",mem);
			}
			else if(strcmp(op,"goto")==0){
				fprintf(out,"@%s\n0;JMP\n",mem);
			}
			else if(strcmp(op,"if-goto")==0){
				fprintf(out,"@SP\nAM=M-1\nD=M\n@%s\nD;JNE\n",mem);
			}
		}
	}
	fclose(in);
	fclose(out);
	return 0;
}

