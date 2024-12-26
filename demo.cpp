#include<stdio.h>                //预处理（头文件包含）
#include<string.h>
#include <conio.h>
#include<iostream>   
using namespace std;

#define MaxNum  100                //源程序最大字符的个数
#define Lbuffer     30   //标示符和关键字最长字符的个数
#define TokenNumber     50   //标识符能包含的最多的字符个数

#define Lbuffer1     30 
#define TokenNumber1     100    


char Ecode[TokenNumber1][Lbuffer1];//存放输入串
int Top=0;
int nID=1;                      
int mID=1;
/*……………………………定义栈………………*/
typedef struct
{
    int add;
	int next;
	char str[Lbuffer1]; 
}ADDL;                              //ADDL用于识别关键字并存储

ADDL L[TokenNumber1];          
int nL=0;
int nadd=1;                         //初始化
/*………………………………输入串栈……………………………*/
typedef struct
{
 int ID;            //类标识
 char b[Lbuffer]; //存放字符
}link;

link Token[TokenNumber];   
link Token2[TokenNumber];
int nTokenNumer=1;                  //标志位置
char a[MaxNum];          //数组用于装入源程序
int nLength=0;                      //用于指向数组中的元素 
//函数声明
void login();                      //把源程序装入数组a
char GetChar();                    //将数组a中的元素读出
int Judge(char& ch);               //用于判断'\0'            
int Isalpha(char c); 
int Isnum(char c); 
void Input(char* p,char c,int& nx); //标识符或关键字进入指针p指向的数组第nx+1个元素
void Save(char* buffer,int x);        //将关键字或标志符或算符装入Token
void Assort();                        //却别关键字、字符、界符 等

void Pop();                         //出栈
void InputZ();
void InputA1();                     //把产生式按逆序入栈
void InputA2();
void InputA3();
void InputB();
void InputE11();
void InputE12();
void InputE13();
void InputT();                         //把产生式按逆序入栈
void InputT11();
void InputT12();
void InputT13();
void InputF1();
void InputF2();
int  SelectP();                     //识别非终结符
int  EStrcmp();
/*…………………………………………词法分析部分……………………………………*/
int wordanalysis()
{
  int flag1=0;                        
  char ch;
  cout<<"***************请输入一个while do文法的句子(以#为结束标志):******************"<<endl;
	
  login();                     //把源程序装入数组a
  int x;
  char buffer[Lbuffer];        //声明临时数组
  while((ch=GetChar())!='\0')      
  {
	  if(!(Judge(ch)))             
	  {
		  break;             //跳过空格和回车
	  }                             
	  x=0;
	  buffer[x]='\0';                    

	  if(Isalpha(ch))            
	  { flag1=1;
	     while(Isalpha(ch)||Isnum(ch))
		 {
		   Input(buffer,ch,x);       //是字符就将其装入数组buffer
           ch=GetChar();           
		  }
		/*if(ch=='>' || ch=='='||ch=='<')*/
		 if(ch=='>' || ch=='='||ch=='<'||ch=='+'||ch=='-'||ch=='*'||ch=='/'||ch=='('||ch==')')
			 nLength=nLength-1;     //指向算符

		 buffer[x]='\0';
         Save(buffer,x);              //将关键字或标志符或算符装入Token
	  }
	  else if(ch=='>')            
	  {
		  Input(buffer,ch,x);
		  buffer[x]='\0';	  
		  Save(buffer,x);
	  }
      else if(ch=='=')            
	  {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	  }
     else if(ch=='<')             
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	  else if(ch=='+')             
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	 else if(ch=='-')             
	 {
			  Input(buffer,ch,x);

			  buffer[x]='\0';

			  Save(buffer,x);
	 }  
	 else if(ch=='*')              
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	 else if(ch=='/')              
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 } 
	 else if(ch=='(')             
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	 else if(ch==')')              
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	 else if(ch=='{')              
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	 else if(ch=='}')             
	 {
			  Input(buffer,ch,x);
			  buffer[x]='\0';
			  Save(buffer,x);
	 }  
	 else
	 {
		 cout<<"输入出错!:"<<endl;       //出错处理
		flag1=0;                       
		 return 0;
	 }
  }                                    
										
  if(flag1==1)                         //词法分析结束
  {
      Assort();   //这个函数的作用是将输入符号放入Token
	  cout<<"********** 1---关键字 || 2---运算符 || 3---界符 ||  ||5--- 标示符************"<<endl;
      cout<<"符号表[<类别编码，单词属性>]：\n ";
	  
	 
      for(int i=1;i<nTokenNumer;i++)   //输出词法分析结果
	  {
        cout<<"<";
    
		 cout<<' '<<Token[i].ID;
	
		  cout<<",";
	
		  cout<<' '<<Token[i].b;

		  cout<<">\n";

		  strcpy(Token2[i].b,Token[i].b);//将输入的符号拷贝在Token2中 
		  
	  }
      for(int d=1;d<nTokenNumer;d++)      //将标识符号替换成id
	  {
		  if(Token[d].ID==5)
		  {
			  strcpy(Token[d].b,"id");
		  }
	  }
	  strcpy(Token[nTokenNumer].b,"#");
	  Token[nTokenNumer].ID=7;          //'#'的种别编码为7

	  return 1;
  }
 getch();//继续取
 return 0;
}

void login()                       //把源程序装入数组a
{
  char ch;

  while((ch=getchar())!='#')
  {
   if(nLength>=MaxNum-2)
	   break;
   else
	   a[nLength++]=ch;  
  }
  a[nLength]='\0';
  
  nLength=0;
}

char GetChar()                          //将数组a中的元素读出
{
  if(nLength>=MaxNum-1)
	  return '\0';
  else
	  return a[nLength++];
}
int Judge(char& ch)                   //用于判断'\0'，返回值为0时为'\0'
{
  while(ch==10 || ch==32)            //当ch为空格和换行时
  {
    ch=GetChar();
	
	if(ch=='\0')
	{
		return 0;
	}
  }
  return 1;
}
int Isalpha(char c)                  //判断是否为字母
{
  if(c>='a' && c<='z' || c>='A' && c<='Z' )
		return 1;
	else
		return 0;
}
int Isnum(char c)
{
	if(c>='0' && c<='9')
		return 1;
	else return 0;
}
void Input(char* p,char c,int& nx)   //标识符或关键字进入指针p指向的数组第nx+1个元素
{
  if(nx<Lbuffer-1)
  {
	  p[nx]=c; 
	  nx++;
  }
}

void Save(char* p,int x)              //将关键字或标志符或算符装入Token[nTokenNumer]数组中
{
   for(int i=0;i<=x;i++)
   
	    Token[nTokenNumer].b[i]=p[i];	
		nTokenNumer=nTokenNumer+1;
      
}
/*…………………………为不同的输入符号赋予不同的类别编码……………………*/
//********** 1---关键字 || 2---运算符 || 3---界符 ||  ||5--- 标示符************
void Assort()
{
  for(int i=1;i<nTokenNumer;i++)
  {
    if(strcmp(Token[i].b,"do\0")==0)
		Token[i].ID=1;
    else if(strcmp(Token[i].b,"=\0")==0)
		Token[i].ID=2;
	else if(strcmp(Token[i].b,"+\0")==0)
		Token[i].ID=2;
	else if(strcmp(Token[i].b,"-\0")==0)
        Token[i].ID=2;
	else if(strcmp(Token[i].b,"*\0")==0)
		Token[i].ID=2;
	else if(strcmp(Token[i].b,"/\0")==0)
		Token[i].ID=2;
	
	 else if(strcmp(Token[i].b,"while\0")==0)
		Token[i].ID=1;
	else if(strcmp(Token[i].b,"(\0")==0)
		Token[i].ID=3;
    else if(strcmp(Token[i].b,")\0")==0)
		Token[i].ID=3;
	else if(strcmp(Token[i].b,"<\0")==0)
        Token[i].ID=2;
	else if(strcmp(Token[i].b,">\0")==0)
        Token[i].ID=2;
	 else if(strcmp(Token[i].b,"{\0")==0)
		Token[i].ID=3;
	else if(strcmp(Token[i].b,"}\0")==0)
		Token[i].ID=3;	

	else 
	    Token[i].ID=5;
  }
}

void InputZ()                             //产生式压栈        
{
	Pop();
	strcpy(Ecode[Top++],"B");
	strcpy(Ecode[Top++],"do");
	strcpy(Ecode[Top++],")");
	strcpy(Ecode[Top++],"A");
	strcpy(Ecode[Top++],"(");
	strcpy(Ecode[Top++],"while");
}
void InputA1()                             //压栈 
{
	Pop();
	strcpy(Ecode[Top++],"id");
	strcpy(Ecode[Top++],"<");
	strcpy(Ecode[Top++],"id");
}
void InputA2()                             
{
    Pop();
	strcpy(Ecode[Top++],"id");
	strcpy(Ecode[Top++],"=");
	strcpy(Ecode[Top++],"id");
}
void InputA3()                            
{
    Pop();
	strcpy(Ecode[Top++],"id");
	strcpy(Ecode[Top++],">");
	strcpy(Ecode[Top++],"id");
}
void InputB()                            
{
    Pop();
	strcpy(Ecode[Top++],"E");
	strcpy(Ecode[Top++],"=");
	strcpy(Ecode[Top++],"id");
}
void InputE()                             
{  
	Pop();
	strcpy(Ecode[Top++],"T1");
	strcpy(Ecode[Top++],"F");
}
void InputE11()                              
{
	Pop();
	strcpy(Ecode[Top++],"T1");
	strcpy(Ecode[Top++],"F");
	strcpy(Ecode[Top++],"+");
}
void InputE12()                             
{
   Pop();
	strcpy(Ecode[Top++],"T1");
	strcpy(Ecode[Top++],"F");
	strcpy(Ecode[Top++],"-");
}
void InputE13()                             
{
    Pop();
	strcpy(Ecode[Top++],"$");
}
void InputT()                              
{
    Pop();
	strcpy(Ecode[Top++],"T1");
	strcpy(Ecode[Top++],"F");
}
void InputT11()                              
{
    Pop();
	strcpy(Ecode[Top++],"T1");
	strcpy(Ecode[Top++],"F");
	strcpy(Ecode[Top++],"*");
}
void InputT12()                             
{
    Pop();
	strcpy(Ecode[Top++],"T1");
	strcpy(Ecode[Top++],"F");
	strcpy(Ecode[Top++],"/");
}
void InputT13()                             
{
    Pop();
	strcpy(Ecode[Top++],"$");	
}
void InputF1()                             
{
   Pop();
   strcpy(Ecode[Top++],"id");
}
void InputF2()                              
{
   Pop();
	strcpy(Ecode[Top++],")");
	strcpy(Ecode[Top++],"E");
	strcpy(Ecode[Top++],"(");
}
int SelectP()                           //识别非终结符
{
    if(strcmp(Ecode[Top-1],"Z")==0)
		return 1;
	else if(strcmp(Ecode[Top-1],"A")==0)
		return 2;
	else if(strcmp(Ecode[Top-1],"B")==0)
		return 3;
	else if(strcmp(Ecode[Top-1],"E")==0)
		return 4;
	else if(strcmp(Ecode[Top-1],"E1")==0)
		return 10;
	
	else if(strcmp(Ecode[Top-1],"T")==0)

		return 8;
	else if(strcmp(Ecode[Top-1],"T1")==0)
		return 10;
	else if(strcmp(Ecode[Top-1],"F")==0)
		return 12;
	else if(strcmp(Ecode[Top-1],"F1")==0)
		return 13;
	else if(strcmp(Ecode[Top-1],"F2")==0)
		return 14;
	else
		return -1;
}/*...............................................预测分析表.................................................*/
int ExpressionAnalyse()               //语法分析
{
	int x=0;
	int FLAG=1;     
	cout<<"\n语法分析得"<<endl;
	
	strcpy(Ecode[Top++],"#");            //将#压栈

	strcpy(Ecode[Top++],"Z");            //将Z压栈

//******************Token2[nTokenNumer].b 存放输入的字符串  *****************************




                    //语法分析未结束
while(FLAG)
	{	 
	 int x=1,f1=0; 
	 f1=SelectP();                    //求栈顶的非终结符 
	 if(f1==1)
	 {
		  cout<<"  Z-> while (A) do  B"<<endl; 
		  InputZ();
	 }
	 else if(f1==2)
	 {   
		  cout<<"  A->id<id"<<endl;
		  InputA1();
		  
	 }
	 else if(f1==3)
	 {
		 cout<<"  B->id=E"<<endl;
		 InputB();
	 }
	 else if(f1==4)
	 {
		 cout<<"  E->TE1"<<endl;
		 InputE();
	 }
	 
	 else if(f1==8)
	 {
		 cout<<"  T->FT1"<<endl;
		 InputT();
	 }
	 else if(f1==10)
	 {    if(strcmp(Token[nID].b ,"+")==0)
		 {
			 cout<<"  E1->+TE1"<<endl;
			 InputE11();
		 }
		 else if(strcmp(Token[nID].b ,"-")==0)
		 {
			 cout<<"  E1->-TE1"<<endl;
			 InputE12();
		 }
		 if(strcmp(Token[nID].b ,"*")==0)
		 {
			 cout<<"  T1->*FT1"<<endl;
			 InputT11();
		 }
		 else if(strcmp(Token[nID].b ,"/")==0)
		 {
			 cout<<"  T1->/FT1"<<endl;
			 InputT12();
		 }
		 else
		 {  
			 cout<<"  T1->$"<<endl;
			 InputT13();
		 }
	 }
	 else if(f1==12)
	 {
		 cout<<"  F->id"<<endl;
		 InputF1();
	 }
	 else if(f1==13)
	 {
		 cout<<"  F->(E)"<<endl;
		 InputF2();
	 }
	 else 
	 {
		 int f2=0;
		 f2=EStrcmp();
		 if(f2==1)                        //识别出关键字
		 {  
			Pop();
			nID=nID+1;
		 }
		 else if(f2==3)                   //识别出#，分析结束
		 {
		  cout<<endl<<"语法正确!"<<endl;
		  FLAG=0;
		  return 1;
		 }
		 else
		 {   
		 
		  FLAG=0;
		  return 0;
		 }
	 }
	 
 }
	getch();
 return 0;
}
void Pop()                               //出栈操作
{
    Top=Top-1;
}
void compare()
{ 
	int k;
 
	for(k=0;k<nTokenNumer;k++)
	{   
		if((strcmp(Token2[k].b,"{")==0)||(strcmp(Token2[k].b,"}")==0)||(strcmp(Token2[k].b,";")==0))
		{
			k++;
		}
		else if((strcmp(Ecode[Top-1],Token2[k].b)==0))
		{
			 Pop();
			 k++;
		}
		else
		{
			break;
		}
	}
}
int EStrcmp()                           //识别while和do关键字
{
           if(strcmp(Ecode[Top-1],"#")==0)
		   {
		              if(strcmp(Token2[nID].b,"#")==0)	
			                return 3;           // 分析结束
		   }
		   else if(strcmp(Ecode[Top-1],Token[nID].b)==0)
		   {
		            
					      if(strcmp(Ecode[Top-1],"while")==0)
						   {
							    L[nL].add=nadd++;
			                    L[nL].next=nadd++;
			                    strcpy(L[nL].str ,"while");
			                    nL++;
						   }
		                   if(strcmp(Ecode[Top-1],"do")==0)
						   {
							     L[nL].add=nadd++;
								 L[nL].next=L[nL-1].add;
                                 strcpy(L[nL].str ,"do");
			                     nL++;
						   }
						   
						   return 1;
		   }
	       else
	           	return 0;
	cout<<Token[2].b<<endl;
	return 0;
}
/*……………………………………语义分析……………………………………*/
int main()

 {
	 cout<<"***********while do语句的翻译分析程序，采用LL（1）法，输出三地址：*************"<<endl;
	 wordanalysis(); 
     ExpressionAnalyse() ;
	 cout<<"\n语法分析得"<<endl;
	 cout<<endl;
	 cout<<"三地址码:"<<endl;
	 cout<<"L0: "  <<"if" <<Token2[3].b<<Token2[4].b<<Token2[5].b<<" goto L1"<<endl;
     cout<<"L1: "  <<"t1"<<":="<< Token2[10].b<<Token2[11].b<<Token2[12].b<<endl;
	 cout<<"L2: "  <<Token2[8].b <<"="<<"t1"<<endl;
	 cout<<"L3: "  <<"goto L0"<<endl;
     cout<<"L4: "  <<"if not goto L5"<<endl;
	 cout<<"L5: "  <<endl;
}
