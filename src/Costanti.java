//interfaccia scanner
//import java.io.*;
//import java.util.*;

public interface Costanti {
	
	static public final int TOKEN_SCANNER = 1;
  	static public final int SCHEME_EXPRESS = 2;
  	static public final int SCHEME_BRANCH = 3;
  	static public final int SCHEME_DEFINITION = 4;
  	static public final int TOKEN_ELSE = 5;
  	static public final int TOKEN_VUOTO = 6; //creato per espressioni senza nulla all'interno (), per la lambda
  	//static public final int EOF = 0;
	
	//tipi_token, indici dentro la classe Token e SchemeExpression per distinguerli
	static public final int ID = 1;
  	static public final int INT = 2;
  	static public final int REAL = 3;
  	static public final int LPAREN = 4;
  	static public final int RPAREN = 5;
  	static public final int DEFINE = 6;
  	static public final int TRUE = 7;
  	static public final int FALSE = 8;
  	static public final int STRING = 9;
  	static public final int AND = 10;
  	static public final int OR = 11;
  	static public final int COND = 12;
  	static public final int CONDELSE = 22;
  	static public final int ELSE = 13; //nel caso di (else <expr>)
  	static public final int LOCAL = 14;
  	static public final int LAMBDA = 15;
  	static public final int COND_ELSE = 16; //(cioè espressione cond con else dentro)
  	static public final int DEFINE_INTERNA = 17;
  	static public final int LISTA_EXPR = 18;
  	static public final int LIST = 19;

}
