#include<stdio.h>
#include<stdlib.h>
#include<string.h>

char a[3][5];
char bin[17];

int parse(char *str){
    int i=0;
    char * pch;
    pch = strtok (str,"=;");
    while (pch != NULL)
    {
        sscanf(pch,"%s",a[i]);
        printf("%s\n",a[i]);
        pch = strtok (NULL, ";=");
        i++;
    }

    return i;
}

void assign(int start, int end, char *s){
    int i,j;
    for(i=start,j=0;i<=end;i++,j++){
        bin[i]=s[j];
    }
}



int main(){
	FILE *in=fopen("Add.asm","r");
	FILE *out=fopen("Add.hack","w");

	char line[50];
	char tmp[10];
	//char bin[17];
	bin[16]='\0';
	int empty,parts;
	int address,i,j;

	if(in==NULL || out==NULL){
		printf("ERROR!! Couldnt open the damn file\n");
		exit(-1);
	}

	while(fgets(line,50,in)!=NULL){
        //printf("%s\n",line);
        i=15;
		/*if(line[0]=='/'){               //if the line is a comment
            continue;
		}*/
		if(line[0]<48 || line[0]>90){
            continue;
		}

		if(line[0]=='@'){           //if the line is an A instruction
            line[0]='0';
            //printf("string= %s\n",line);
            sscanf(line,"%d",&address);
            //printf("address= %d\n",address);
            while(address>0){
                if(address%2==0){
                    bin[i]='0';
                }
                else{
                    bin[i]='1';
                }
                address=address/2;
                i--;
            }
            for(j=0;j<=i;j++){
                bin[j]='0';
            }
		}
		else if(line[0]=='0'){      //for unconditional jump case
            sscanf("1110101010000111","%s",bin);
            ///eita myb vul ase, check kore dekhte hobe abar...control part ta myb vul hoise
		}
		else{       //if the line is a C instruction
            bin[0]='1';
            bin[1]='1';
            bin[2]='1';
            parts=parse(line);
            if(parts==2){
                if(a[1][0]=='J'){
                    int k=0;
                    while(a[1][k]!=NULL){
                        a[2][k]=a[1][k];
                        a[1][k]=NULL;
                        k++;
                    }
                    a[2][k]=NULL;
                    k=0;
                    while(a[0][k]!=NULL){
                        a[1][k]=a[0][k];
                        a[0][k]=NULL;
                        k++;
                    }
                    a[1][k]==NULL;
                    bin[10]='0';
                    bin[11]='0';
                    bin[12]='0';
                }
                else{
                    bin[13]='0';
                    bin[14]='0';
                    bin[15]='0';
                    a[2][0]=NULL;
                    a[2][1]=NULL;
                    a[2][2]=NULL;
                }

            }
            //All types of jumps
            if(a[2][1]=='G' && a[2][2]=='T'){       //JGT
                sscanf("001","%s",tmp);
                assign(13,15,tmp);
            }
            else if(a[2][1]=='E' && a[2][2]=='Q'){     //JEQ
                sscanf("010","%s",tmp);
                assign(13,15,tmp);
            }
            else if(a[2][1]=='G' && a[2][2]=='E'){      //JGE
                sscanf("011","%s",tmp);
                assign(13,15,tmp);
            }
            else if(a[2][1]=='L' && a[2][2]=='T'){      //JLT
                sscanf("100","%s",tmp);
                assign(13,15,tmp);
            }
            else if(a[2][1]=='N' && a[2][2]=='E'){      //JNE
                sscanf("101","%s",tmp);
                assign(13,15,tmp);
            }
            else if(a[2][1]=='L' && a[2][2]=='E'){      //JLE
                sscanf("110","%s",tmp);
                assign(13,15,tmp);
            }
            else if(a[2][1]=='M' && a[2][2]=='P'){      //JMP
                sscanf("111","%s",tmp);
                assign(13,15,tmp);
            }

            //==============================================================================
            if(a[0][0]=='M' && a[0][1]==NULL){    //destination                      //M
                sscanf("001","%s",tmp);
                assign(10,12,tmp);
            }
            else if(a[0][0]=='A' && a[0][1]==NULL){                                  //A
                sscanf("100","%s",tmp);
                assign(10,12,tmp);
            }
            else if(a[0][0]=='D' && a[0][1]==NULL){                                  //D
                sscanf("010","%s",tmp);
                assign(10,12,tmp);
            }
            else if(a[0][0]=='M' && a[0][1]=='D'){                  //MD
                sscanf("011","%s",tmp);
                assign(10,12,tmp);
            }
            else if(a[0][0]=='A' && a[0][1]=='M' && a[0][2]!='D'){  //AM
                sscanf("101","%s",tmp);
                assign(10,12,tmp);
            }
            else if(a[0][0]=='A' && a[0][1]=='D'){                  //AD
                sscanf("110","%s",tmp);
                assign(10,12,tmp);
            }
            else if(a[0][0]=='A' && a[0][1]=='M' && a[0][2]=='D'){   //AMD
                sscanf("111","%s",tmp);
                assign(10,12,tmp);
            }
            //==============================================================================

            if(a[1][0]=='0' && a[1][1]==NULL){  //control statements    //0
                sscanf("101010","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='1' && a[1][1]==NULL){          //1
                sscanf("111111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='A' && a[1][1]==NULL){          //A
                sscanf("110000","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='D' && a[1][1]==NULL){          //D
                sscanf("001100","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='M' && a[1][1]==NULL){          //M
                sscanf("110000","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='-' && a[1][1]=='1'){          //-!
                sscanf("111010","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='!' && a[1][1]=='D'){          //!D
                sscanf("001101","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='!' && a[1][1]=='A'){          //!A
                sscanf("110001","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='!' && a[1][1]=='M'){      ////!M
                sscanf("110001","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='-' && a[1][1]=='D'){        //-D
                sscanf("001111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='-' && a[1][1]=='A'){          //-A
                sscanf("110011","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='-' && a[1][1]=='M'){              //-M
                sscanf("110011","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='D' && a[1][1]=='+' && a[1][2]=='1'){      //D+1
                sscanf("011111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='A' && a[1][1]=='+' && a[1][2]=='1'){      //A+1
                sscanf("110111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='M' && a[1][1]=='+' && a[1][2]=='1'){      //M+1
                sscanf("110111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='D' && a[1][1]=='-' && a[1][2]=='1'){      //D-1
                sscanf("001110","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='A' && a[1][1]=='-' && a[1][2]=='1'){      //A-1
                sscanf("110010","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='M' && a[1][1]=='-' && a[1][2]=='1'){      //M-1
                sscanf("110010","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='D' && a[1][1]=='+' && a[1][2]=='A'){      //D+A
                sscanf("000010","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='D' && a[1][1]=='+' && a[1][2]=='M'){      //D+M
                sscanf("000010","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='D' && a[1][1]=='-' && a[1][2]=='A'){      //D-A
                sscanf("010011","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='D' && a[1][1]=='-' && a[1][2]=='M'){
                sscanf("010011","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='A' && a[1][1]=='-' && a[1][2]=='D'){
                sscanf("000111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='M' && a[1][1]=='-' && a[1][2]=='D'){
                sscanf("000111","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='D' && a[1][1]=='&' && a[1][2]=='A'){
                sscanf("000000","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='D' && a[1][1]=='&' && a[1][2]=='M'){
                sscanf("000000","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
            else if(a[1][0]=='D' && a[1][1]=='|' && a[1][2]=='A'){
                sscanf("010101","%s",tmp);
                assign(4,9,tmp);
                bin[3]='0';
            }
            else if(a[1][0]=='D' && a[1][1]=='|' && a[1][2]=='M'){
                sscanf("010101","%s",tmp);
                assign(4,9,tmp);
                bin[3]='1';
            }
		}

		fputs(bin,out);
		fprintf(out,"\n");
	}

	fclose(in);
	fclose(out);

	return 0;
}
