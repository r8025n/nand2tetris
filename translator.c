#include<stdio.h>
#include<ctype.h>
#include<string.h>

int main(int argc,char *argv[]){
	FILE *in=fopen(argv[1],"r");
	FILE *out=fopen("","w");

	char line[50],op[10],mem[10];
	int val;

	while(fgets(line,50,in)!=NULL){
		if(!(isspace(line[0])) || line[0]=='\\'){
			printf("\\%s",line);
			sscanf(line,"%s %s %d",op,mem,val);

			if(strcmp(op,"push")==0){

			}
			else if(strcmp(op,"pop")){

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