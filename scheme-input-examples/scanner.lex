class Yytoken{
	int field;
	String tokstring;
	int line;
	
	Yytoken(int l, int f, String s){
    		field = f;
    		tokstring=s;
    		line = l;
	}
	public Token tok() {
  		switch (field){

		    // Token: primo par = tipo (1= è un token), secondo par = tipo di token; terzo = eventuale valore  
		    case -2: return(new Token(line, -2,tokstring));
		    case -1: return(new Token());       //EOF
		    case 0:  return(new Token(line, 0));
		    case 1:  return(new Token(line, Costanti.ID,tokstring)); // ID
		    case 2:  return(new Token(line, Costanti.INT,tokstring)); // NUM
		    case 3:  return(new Token(line, Costanti.INT,tokstring)); //REAL
		    case 4:  return(new Token(line, Costanti.LPAREN)); // LPAREN
		    case 5:  return(new Token(line, Costanti.RPAREN));  // RPAREN
		    case 6:  return(new Token(line, Costanti.DEFINE)); // DEFINE
		    case 7:  return(new Token(line, Costanti.TRUE)); // TRUE 
		    case 8:  return(new Token(line, Costanti.FALSE)); // FALSE
		    case 9:  return(new Token(line, Costanti.STRING,tokstring)); //STRING
		    case 10: return(new Token(line, Costanti.AND));  // AND
		    case 11: return(new Token(line, Costanti.OR));  // OR
		    case 12: return(new Token(line, Costanti.COND));  // COND
		    case 13: return(new Token(line, Costanti.ELSE));  // ELSE
		    case 14: return(new Token(line, Costanti.LOCAL));  // LOCAL
		    case 15: return(new Token(line, Costanti.LAMBDA));  // LAMBDA
		    case 19: return(new Token(line, Costanti.LIST));  // LIST
		    default: return(new Token(line, 20));   // provare/capire quando viene restituito
		  }
	}
}



%%


%eofval{
return new Yytoken(yyline, -1,"");
%eofval}

%line

%%

<YYINITIAL> " "|\r\n|\t {return yylex();}
<YYINITIAL> "("                                             {return new Yytoken(yyline, 4,"");}
<YYINITIAL>  ")"                                            {return new Yytoken(yyline, 5,"");}
<YYINITIAL> "define"                                        {return new Yytoken(yyline, 6,"");}
<YYINITIAL> "#t"   	                                        {return new Yytoken(yyline, 7,"");}
<YYINITIAL> "#f"                                            {return new Yytoken(yyline, 8,"");}
<YYINITIAL>  \"[^\"]*\"                                     {return new Yytoken(yyline, 9,yytext());}
<YYINITIAL> "and"                                           {return new Yytoken(yyline, 10,"");}
<YYINITIAL> "or"                                            {return new Yytoken(yyline, 11,"");}
<YYINITIAL> "cond"                                          {return new Yytoken(yyline, 12,"");}
<YYINITIAL> "else"                                          {return new Yytoken(yyline, 13,"");}
<YYINITIAL> "local"                                         {return new Yytoken(yyline, 14,"");}
<YYINITIAL> "lambda"                                        {return new Yytoken(yyline, 15,"");}
<YYINITIAL> "list"                                          {return new Yytoken(yyline, 19,"");}


<YYINITIAL> [a-zA-Z_][a-zA-Z0-9_?]*                   						{return new Yytoken(yyline, 1, yytext());}
<YYINITIAL> "+"                                  						{return new Yytoken(yyline, 1, yytext());}
<YYINITIAL> "-"                                  						{return new Yytoken(yyline, 1, yytext());}
<YYINITIAL> "*"                                  						{return new Yytoken(yyline, 1, yytext());}
<YYINITIAL> "/"                                  						{return new Yytoken(yyline, 1, yytext());}




<YYINITIAL> [0-9]+                                          {return new Yytoken(yyline, 2, yytext());}

<YYINITIAL> .  																							{return new Yytoken(yyline, -2,yytext());}
