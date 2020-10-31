#include<stdio.h>
#include<ctype.h>
#include<string.h>
#include<stdbool.h>
#include <dirent.h>

int main(int argc,char *argv[]){
	char filename[25],str[50],path[50],dir_filename[25],fullpath[100];
	strcpy(str,argv[1]);
	
	int i,j,k,len,st,flip=0;

	len=strlen(str);

	//for parsing the filename from filepath stored in argv[]
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
    if(str[len-1]=='m' && str[len-2]=='v' && str[len-3]=='.'){
    	len=len-3;
    	flip=1;
    }
    for(i=0,j=st;j<len;i++,j++){
        filename[i]=str[j];
    }
    filename[i]='\0';

	char name_out[25],stat[50];
	char line[150],op[10],mem[10],loop_label[30];
	int val,loops=0;
	int eq=0,gt=0,lt=0;
	struct dirent *de;
	DIR *dr=opendir(argv[1]);
	FILE *in,*out;

	sprintf(name_out,"%s.asm",filename);
	out=fopen(name_out,"w");
	
	//Bootstrap code in case it is a directory
	if(flip==0){
		fprintf(out,"@256\nD=A\n@SP\nM=D\n");
		fprintf(out,"@ret$bootstrap_00\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@ARG\nD=M\n");
		fprintf(out,"@SP\nA=M\nM=D\n@SP\nM=M+1\n@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@THAT\nD=M\n@SP\nA=M\nM=D\n");
		fprintf(out,"@SP\nM=M+1\n@SP\nD=M\n@5\nD=D-A\n@0\nD=D-A\n@ARG\nM=D\n@SP\nD=M\n@LCL\nM=D\n@Sys.init\n0;JMP\n(ret$bootstrap_00)\n");
	}

	//if its a file, flip=1
	if(flip==1){
		in=fopen(argv[1],"r");
	}

	DIRECTORY:
	//if its a directory, flip=0
	if(flip==0){
		de=readdir(dr);
		if(de==NULL)
			goto END;
		else{
			strcpy(dir_filename,de->d_name);
			int dlen=strlen(dir_filename);
			if(dir_filename[0]=='.' || !(dir_filename[dlen-1]=='m' && dir_filename[dlen-2]=='v' && dir_filename[dlen-3]=='.'))
				goto DIRECTORY;
			printf("%s\n",dir_filename);
			sprintf(fullpath,"%s/%s",str,dir_filename);
			printf("FULLPATH=%s\n",fullpath);
			in=fopen(fullpath,"r");
		}
	}

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
			else if(strcmp(op,"call")==0){
				sprintf(loop_label,"ret$%s_%d",mem,loops);
				loops++;
				fprintf(out,"@%s\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@ARG\nD=M\n",loop_label);
				fprintf(out,"@SP\nA=M\nM=D\n@SP\nM=M+1\n@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@THAT\nD=M\n@SP\nA=M\nM=D\n");
				fprintf(out,"@SP\nM=M+1\n@SP\nD=M\n@5\nD=D-A\n@%d\nD=D-A\n@ARG\nM=D\n@SP\nD=M\n@LCL\nM=D\n@%s\n0;JMP\n(%s)\n",val,mem,loop_label);
			}
			else if(strcmp(op,"function")==0){
				fprintf(out,"(%s)\n@%d\nD=A\n@end_%d\nD;JEQ\n(loop_%d)\n@SP\nA=M\nM=0\n@SP\nM=M+1\nD=D-1\n@loop_%d\nD;JGT\n(end_%d)",mem,val,loops,loops,loops,loops);
				loops++;
			}
			else if(strcmp(op,"return")==0){
				fprintf(out,"@LCL\nD=M\n@endframe_%d\nM=D\n@5\nD=D-A\nA=D\nD=M\n@retaddr_%d\nM=D\n@SP\nA=M-1\nD=M\n@ARG\nA=M\nM=D\n",loops,loops);
				fprintf(out,"@ARG\nD=M+1\n@SP\nM=D\n@endframe_%d\nM=M-1\nA=M\nD=M\n@THAT\nM=D\n@endframe_%d\nM=M-1\nA=M\nD=M\n@THIS\n",loops,loops);
				fprintf(out,"M=D\n@endframe_%d\nM=M-1\nA=M\nD=M\n@ARG\nM=D\n@endframe_%d\nM=M-1\nA=M\nD=M\n@LCL\nM=D\n@retaddr_%d\nA=M\n0;JMP\n",loops,loops,loops);
			}
		}
	}
	if(flip==0){
		fclose(in);
		goto DIRECTORY;
	}
	END:
	fclose(in);
	fclose(out);
	closedir(dr);
	return 0;
}


