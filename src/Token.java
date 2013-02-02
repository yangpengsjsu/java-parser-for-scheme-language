import java.io.*;
import java.util.*;

//un oggeto Token è un elemento della lista gestita dalla parseDefine()
public class Token {
	
     //usa metodi privati o non funzionano in lista
	 private int tipo; //tipo del Token ( TOKEN_SCANNER | SCHEME_EXPRESS | SCHEME_BRANCH | SCHEME_DEFINITION | TOKEN_ELSE | TOKEN_VUOTO )

	 private int tipo_token; //se da scanner indica tipo di token (-1=errore, -1=eof, 0=if)
	 private String valore_string; // se da scanner indica eventualmente il valore
	 private int valore_int; // se da scanner indica eventualmente il valore
	 
	 //un Token può contenere una espressione, un branch o una definition
	 private SchemeExpression se;
	 private SchemeBranch sb;
	 private SchemeDefinition sd;
	 
	 //numero di linea del token nel file  di input
	 int line;

	 
	 //ctor usato dallo scanner
	 public Token(int l, int tipo_token_)
	 {
	 	tipo = Costanti.TOKEN_SCANNER;
	 	tipo_token = tipo_token_;
	 	line = l;
	 }
	 
	 //ctor usato dallo scanner per EOF
	 public Token()
	 {
	 	tipo = SchemeScanner.EOF; // == Costanti.EOF
	 	line = 0;
	 }


	 //ctor usato dallo scanner per identificatori e valori
	 public Token(int l, int tipo_token_, String tokstring)
	 {
	 	tipo = Costanti.TOKEN_SCANNER;
	 	tipo_token = tipo_token_;
	 	line = l;
	 	
	 	//ID
	 	if (tipo_token==1)
	 		valore_string = tokstring;
	 	
	 	//INT
	 	else if (tipo_token==2)
	 		valore_int = new Integer(tokstring).intValue();
	 		
	 	//STRING
	 	else if (tipo_token==9)
	 		valore_string = tokstring;
	 }	
	 

	 //ctor per Token contenenti oggetti SchemeExpression (come primo argomento si specifica il tipo dell'espressione) 
	 public Token(int l, int tipo_espressione, SchemeExpression se_)
	 {
	 	tipo = Costanti.SCHEME_EXPRESS;//indica che il Token contiene un oggetto SchemeExpression
	 	tipo_token = tipo_espressione;
	 	se = se_;
	 	line = l;
	 }
	 
	 //ctor per la else
	 public Token(int l, SchemeExpression se_)
	 {
	 	tipo = Costanti.TOKEN_ELSE;//indica che il Token contiene un oggetto SchemeExpression
	 	se = se_;
	 	line = l;
	 }
	 
	 //ctor per token vuoto (lambda con n=0)
	 public Token(int l)
	 {
	 	tipo = Costanti.TOKEN_VUOTO;
	 	line = l;
	 }

	 
	 //ctor per Token contenenti oggetti SchemeBranch
	 public Token(int l, SchemeBranch sb_)
	 {
	 	tipo = Costanti.SCHEME_BRANCH;//indica che il Token contiene un oggetto SchemeDefinition
	 	sb = sb_;
	 	line = l;
	 }
	 
	 //ctor per Token contenenti oggetti SchemeDefinition
	 public Token(int l, SchemeDefinition sd_)
	 {
	 	tipo = Costanti.SCHEME_DEFINITION;//indica che il Token contiene un oggetto SchemeDefinition
	 	sd = sd_;
	 	line = l;
	 }
	 
	 
	 //ritorna tipo di oggetto contenuto dal Token (1=Token | 2=SchemeExpr | 3=SchemeBranch | 4=SchemeDefinition
	 public int get_tipo()
	 {
	 	return tipo;
	 }
	 
	 //ritorna tipo_token cioè il sotto del Token
	 //per i Token contenti SchemeExpression, ritorna lo stesso valore usati dal costruttore per crearli
	 public int get_tipo_token()
	 {
	 	return tipo_token;
	 }	
	 
	 //ritorna valore string qualora il Token contenga un identificatore di variabile
	 public String get_valore_string()
	 {
	 	return valore_string;
	 }
	 
	 //ritorna valore int qualora il Token contenga un intero
	 public int get_valore_int()
	 {
	 	return valore_int;
	 }
	 
	  //ritorna numero di linea, usata per errori
	 public int get_line()
	 {
	 	return line;
	 }

	 //ritorna contenuto SchemeExpression
	 public SchemeExpression get_SchemeExpression()
	 {
	 	return se;
	 }
	 
	 //ritorna contenuto SchemeBranch
	 public SchemeBranch get_SchemeBranch()
	 {
	 	return sb;
	 }
	 
	 //ritorna contenuto SchemeDefinition
	 public SchemeDefinition get_SchemeDefinition()
	 {
	 	return sd;
	 }
	 
	 //stampa del Token, qualunque cosa contenga
	 public void stampa()
	 {
		
		//se contiene un Token dallo scanner lo stampo subito
	 	if (tipo==Costanti.TOKEN_SCANNER)
	 	{
	 		System.out.print("TOKEN di tipo ");
	 		if (tipo_token==-2) System.out.print(".");
	 		if (tipo_token==-1) System.out.print("EOF");
	 		if (tipo_token==0) System.out.print("IF");
	 		if (tipo_token==1) System.out.print("ID("+valore_string+")");
	 		if (tipo_token==2) System.out.print("NUM("+valore_string+")");
	 		if (tipo_token==3) System.out.print("REAL");
	 		if (tipo_token==4) System.out.print("(");
	 		if (tipo_token==5) System.out.print(")");
	 		if (tipo_token==6) System.out.print("DEFINE");
	 		if (tipo_token==7) System.out.print("TRUE");
	 		if (tipo_token==8) System.out.print("FALSE");
	 		if (tipo_token==9) System.out.print("STRING");
	 		if (tipo_token==10) System.out.print("AND");
	 		if (tipo_token==11) System.out.print("OR");
	 		if (tipo_token==12) System.out.print("COND");
	 		if (tipo_token==13) System.out.print("ELSE");
	 		if (tipo_token==14) System.out.print("LOCAL");
	 		if (tipo_token==15) System.out.print("LAMBDA");
	 		if (tipo_token==19) System.out.print("LIST");
	 		if (tipo==0 && tipo_token==0 ) System.out.print("DEFAULT");
	 		
	 		//System.out.print("linea" + line);
	 		
	 		//System.out.print(" "+t+" "+valore_string);
 		}
 		
 		//per le espressioni, branch e definition lascio il compito alle classi relative
 		else if (tipo==Costanti.SCHEME_EXPRESS)
 		  se.stampa();
 		else if (tipo==Costanti.SCHEME_BRANCH)
 		   sb.stampa();
 		else if (tipo==Costanti.SCHEME_DEFINITION)
 			sd.stampa();
 		
 		//token else	
 		else if (tipo==Costanti.TOKEN_ELSE)
 		{
 			System.out.print("TOKEN_ELSE( ");	
    	 	se.stampa(); //stampo espressione relativa all'else
    	 	System.out.print(" )");
    	}	
    	//token vuoto
    	else if (tipo == Costanti.TOKEN_VUOTO)
 		   System.out.print("TOKEN_VUOTO()");
    	
    	System.out.println(""); //new line

	 }
	  
  
}
