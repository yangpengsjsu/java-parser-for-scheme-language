import java.io.*;
import java.util.*;

class SchemeParserImpl implements SchemeParser {
	
	   private List<Token> lista_token = new LinkedList<Token>();
       private SchemeFactory f = new SchemeFactoryImpl();
	   private SchemeScanner scanner;
	   private SchemeFactory factory;
	   private int define_da_restituire = 0;
	   
	   int token_corrente = 0;
	  
	   String file_sorgente;
		 
	  //ctor chiamato da Schemer
	   public SchemeParserImpl(SchemeScanner s , SchemeFactory f) throws java.io.IOException
	   {
		  scanner = s;
		  factory = f;
		  file_sorgente = s.get_file_sorgente();
		  scanner.set_token(1);//
		  risolvi_tutto();
		  
	   }
	   
	   //unico metodo del parser, per ogni token dello scanner nel main, da un risultato
       public SchemeDefinition parseDefine() throws java.io.IOException
       {
        SchemeDefinition sd = null;
        //controllo che nella lista ci siano solo define
        if ( get_tipo(token_corrente) == Costanti.SCHEME_DEFINITION )
          sd = lista_token.get(token_corrente).get_SchemeDefinition();
        else
          new SchemeSyntaxError(file_sorgente, get_line(token_corrente), "define non valida");  
        
        scanner.set_token( get_tipo(token_corrente+1) ); //setto il tipo del Token successivo (0 se EOF), in modo che il while dello schemer si ferma al punto giusto
        token_corrente++;
        
        return sd; 
	   }


       //unico metodo del parser, per ogni token dello scanner nel main, da un risultato !
       public void risolvi_tutto() throws java.io.IOException
       {
        Token token_temp = null;
        
        //aggiungo i token alla lista lista_token (attributo di questa classe)
        do
        {
        	token_temp = scanner.nextToken();
        	lista_token.add(token_temp);
        } while ( token_temp.get_tipo() != 0 ); //fino all'ultimo token (di tipo EOF, tipo=0)
      
      
        
        //rimpiazza num, real, id, boolean
        for (int i=0; i<lista_token.size()-1; i++)
        {
          	  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == -2)
          	   new SchemeSyntaxError(file_sorgente, get_line(i), "token non valido in linea");
          	  
          	  //Costanti.ID  = > rimpiazzo con Token SchemeExpression
          	  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.ID)
           	    lista_token.set(i, new Token(get_line(i), Costanti.ID, f.createIdExpression(lista_token.get(i).get_valore_string()  )  ));
			  
			  //Costanti.STRING  = > rimpiazzo con Token SchemeExpression
          	  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.STRING)
           	    lista_token.set(i, new Token(get_line(i), Costanti.STRING, f.createStringExpression(lista_token.get(i).get_valore_string()  )  ));
			  
			  
			  //INT (tipo=1, tt =1) = > rimpiazzo con Token SchemeExpression
			  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.INT)
           	    lista_token.set(i, new Token(get_line(i), Costanti.INT, f.createIntExpression(lista_token.get(i).get_valore_int()  )  ));
           	    
           	 //Costanti.TRUE 
			  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.TRUE)
           	    lista_token.set(i, new Token(get_line(i), Costanti.TRUE, f.createBoolExpression(true)  ));
           	 //Costanti.FALSE 
			  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.FALSE)
           	    lista_token.set(i, new Token(get_line(i), Costanti.FALSE, f.createBoolExpression(false)  ));   

        }
          
        //debug
        //stampa_lista("dopo ciclo ID");
		
		int pc=0;
		
		//finchè esistono sottoespressioni, cioè espressioni fra parentesi...
        while(esistono_sottoexpr() && pc != -1)
        {
        	//System.out.println("cerco parentesi");
        	int ua = ultima_aperta();
        	pc = prima_chiusa_da(ua); //se non esiste deve terminare
        	if (pc != -1)
        	  risolvi( ua, pc );
        }

       }
       

       //ritorno #el lista con ultima parentesi aperta. -1 se non esiste
       int ultima_aperta()
       {
       	int ret=-1;
       	//parto dal fondo (eof) all'indietro (non posso escludere oef o dà errore con lista di un el.)
       	for (int i=lista_token.size()-1; i>=0 && ret == -1; i--)
       	{
       	   //se trovo una parentesi aperta
       	   if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.LPAREN)
       	      ret = i; //e finisce anche il for
       	}  	 
       	 if (ret == -1)
       	   new SchemeSyntaxError(file_sorgente, -1, "parentesi aperta non trovata");
       	 return ret;
       }
       
       
       //trovo prima parentesi chiusa a partire dall'indice passato. -1 se non esiste (ERRORE SINTASSI)
       int prima_chiusa_da(int partenza)
       {
       	int ret=-1;
       	//cerco a dx a partire da partenza
       	for (int i=partenza; i<lista_token.size()-1 && ret == -1 ; i++)
       	  if (get_tipo(i) == Costanti.TOKEN_SCANNER && get_tipo_token(i) == Costanti.RPAREN)
       	    ret = i; //e finisce anche il for
   
       	if (ret == -1)
       	   new SchemeSyntaxError(file_sorgente, partenza, "parentesi chiusa corrispodente assente");
       	 return ret;
       }

	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
       //riduce-risolve la lista dall'indice start all'indice stop. in start e stop ci sono rispettivamente Costanti.LPAREN e Costanti.RPAREN
       public void risolvi(int start,  int stop)
       {
       	  int primo_arg = start+1; //primo argomento dopo la parentesi aperta
       	  int secondo_arg = start+2;
       	  int terzo_arg = start+3;
       	  int quarto_arg = start+4;
       	  int ultimo_arg = stop-1; //ultimo argomento prima della parentesi chiusa
       	   
       	   
       	 
       	   //se fra le parentesi non ho nulla (in lambda con n=0)
       	   if (stop-start == 1)
       	   {
       	   	 Token t = new Token(get_line(start));
       	     //rimpiazzo
       	     rimuovi_porzione(start, stop);
       	     lista_token.add(start, t );
       	   }  
       	   //se dopo la parentesi ho un token di tipo 1 (or,  and,  define,  cond,  lambda,  list,   ...)
       	   else if ( get_tipo(primo_arg) == Costanti.TOKEN_SCANNER  )
       	   {
       	     //match sull'elemento primo_arg,  cioè sul tipo di sottoespressione
       	     if (get_tipo_token(primo_arg) == Costanti.AND || get_tipo_token(primo_arg) == Costanti.OR) // or
       	     {
      	     	List<SchemeExpression> lista_exprs_temp = new LinkedList<SchemeExpression>();
      	     	
      	     	//se non ci sono argomenti non viene mai eseguito (and)
      	     	for(int i=secondo_arg; i<=ultimo_arg; i++)
      	     	  if (get_tipo(i) == Costanti.SCHEME_EXPRESS) //se trovo un oggetto SchemeExpression (escludo parantesi finale e iniziale)
      	     	    lista_exprs_temp.add(lista_token.get(i).get_SchemeExpression()); //aggiungo il token alla lista temporanea
      	     	  else
        	     	new SchemeSyntaxError(file_sorgente, get_line(i), "espressione non valida come argomento di AND/OR");

      	     	  
      	     	int tipo_token = get_tipo_token(primo_arg);
      	     	int line = get_line(primo_arg);
      	     	rimuovi_porzione(start,  stop);
      	     	
      	     	//se non ha argoment
      	     	if (tipo_token == Costanti.AND) 
      	     	  lista_token.add(start,  new Token(line, Costanti.AND, f.createAndExpression((List)lista_exprs_temp))  );
      	     	else if (tipo_token == Costanti.OR) 
      	     	  lista_token.add(start, new Token(line, Costanti.OR, f.createOrExpression((List)lista_exprs_temp))  );

       	     }  
       	     
       	     else if (get_tipo_token(primo_arg) == Costanti.DEFINE ) // define 
       	     {
       	     	  //ci devono essere solo 2 argomenti dopo la define
       	     	  if (stop-start != 4)
       	     	    new SchemeSyntaxError(file_sorgente, get_line(primo_arg), "errato numero di argomenti per la define");
       	     	  
       	     	  //dopo la define è necessario una espressione (id o lista di id)
       	     	  if (get_tipo(secondo_arg) != Costanti.SCHEME_EXPRESS)
       	     	    new SchemeSyntaxError(file_sorgente, get_line(secondo_arg), "id non valido per la define");
       	     	  
       	     	  //terzo e ultim argomento deve essere una espressione
       	     	  if (get_tipo(terzo_arg) != Costanti.SCHEME_EXPRESS)
       	     	    new SchemeSyntaxError(file_sorgente, get_line(terzo_arg), "espressione non valida per la define");
       	     	  
       	     	  
       	     	  //espressione della define (che è l'ultimo argomento)
       	     	  SchemeExpression expr = lista_token.get(ultimo_arg).get_SchemeExpression();
       	            
       	          //se è una normale define (define id <expr>)
       	     	  if (get_tipo_token(secondo_arg)  ==  Costanti.ID) 
       	     	  {
       	     	    
       	     	    //tiro fuori valore dell' id  
       	     	    String nome_var = lista_token.get(secondo_arg).get_SchemeExpression().get_valore_string();
       	            
       	            //creo Token contenente la define
       	            Token t = new Token(get_line(primo_arg), f.createDefinition(nome_var, expr));
       	            
       	            //rimpiazzo la sottoespressione corrente con l'oggetto Token creato
       	            rimuovi_porzione(start, stop);
       	            lista_token.add(start, t );  
       	      		
       	          }
       	     	  //se è una ( define (id1 ....idn) <expr> ) => espando nel secondo tipo con lambda
       	     	  else if (get_tipo_token(secondo_arg)  ==  Costanti.LISTA_EXPR)
       	      	  {
       	     		List<SchemeExpression> lista_exprs_temp = new LinkedList<SchemeExpression>(); 
       	     		//copio la lista presente in secondo_arg
       	     		lista_exprs_temp = lista_token.get(secondo_arg).get_SchemeExpression().get_lista_exprs();
       	     		//estraggo nome funzione (cioè primo elemento della lista)
       	     		String nome_f = lista_exprs_temp.get(0).get_valore_string();
       	     		//rimuovo primo elemento della lista, così il resto lo passo alla lambda
       	     		lista_exprs_temp.remove(0);
       	     		
       	     		//creo Token che contiene una define con all'interno una lambda
       	     		Token t = new Token(get_line(primo_arg), f.createDefinition(nome_f, f.createLambdaExpression(lista_exprs_temp, expr)  ));
       	     		//rimpiazzo
       	     		rimuovi_porzione(start, stop);
       	            lista_token.add(start, t );
       	          }  
       	          else  
       	            new SchemeSyntaxError(file_sorgente, get_line(primo_arg), "id o lista di id non validi per la define"); 

       	     
       	     }
       	     //una espressione del tipo (else <expr>) sarà inserita in una cond più esterna successivamente
       	     else if (get_tipo_token(primo_arg) == Costanti.ELSE) // else
       	     {
       	        
       	        //dopo la else ci deve essere solo un parametro
       	        if ( stop - start != 3 )
       	         new SchemeSyntaxError(file_sorgente, get_line(primo_arg), "numero di parametri non corretto per la else");
       	        //controllo che dopo l'else ci sia una espressione
       	        if ( get_tipo(secondo_arg) != Costanti.SCHEME_EXPRESS )
       	         new SchemeSyntaxError(file_sorgente, get_line(secondo_arg), "espressione dopo l'else non valida");
       	        
       	        //creo Token di tipo TOKEN_ELSE e rimpiazzo
       	        Token t = new Token(get_line(primo_arg), lista_token.get(secondo_arg).get_SchemeExpression() );
       	        rimuovi_porzione(start, stop);
       	        lista_token.add(start, t ); 

       	     }
       	     //COND
       	     else if (get_tipo_token(primo_arg) == Costanti.COND)
       	     {
       	        if (stop-start < 3)
       	          new SchemeSyntaxError(file_sorgente, primo_arg, "dopo la cond è necessario almeno un argomento");
       	        
       	        
       	        List<SchemeBranch> lista_branch = new LinkedList<SchemeBranch>();
       	        List<SchemeExpression> lista_exprs_temp = new LinkedList<SchemeExpression>();
       	        
       	        boolean esiste_else;
       	        int indice_ultimo_branch; //indice 
       	        
       	        //analizzo ultimo argomento per capire se esiste il caso ELSE
       	        if (get_tipo(ultimo_arg)  ==  Costanti.TOKEN_ELSE)
       	        {
       	        	//caso con else alla fine
       	        	esiste_else = true;
       	        	indice_ultimo_branch = ultimo_arg-1;
       	        	
       	        }
       	        else
       	        {
       	        	//caso senza else
       	        	esiste_else = false;
       	        	indice_ultimo_branch = stop-1;

       	        }
       	        
       	        //controllo branch
       	        for(int i=secondo_arg; i<=indice_ultimo_branch; i++)
      	     	{
      	     	  if (get_tipo(i) == Costanti.SCHEME_EXPRESS && get_tipo_token(i) == Costanti.LISTA_EXPR) //se trovo una lista di espessioni (da trasformare in Branch)
      	     	  {
      	     	   	//tiro fuori lista espressioni (che diventeranno branch se corrette)
      	     	   	lista_exprs_temp = lista_token.get(i).get_SchemeExpression().get_lista_exprs();
      	     	   	//se si tratta di una lista di 2 espressioni, cioè un branch...
      	     	   	
      	     	   	if (lista_exprs_temp.size()==2)
      	     	   	{
      	     	   		  //creo un token di tipo SchemeBranch con la createBranch()
      	     	   		  Token t = new Token(get_line(i), f.createBranch(  lista_exprs_temp.get(0)  , lista_exprs_temp.get(1)  ));
      	     	   		  //aggiungo questo branch alla lista_branch 
      	     	   		  lista_branch.add(t.get_SchemeBranch()); //aggiungo il token alla lista temporanea
      	     	   	}
      	     	   	else
      	     	   	  new SchemeSyntaxError(file_sorgente, get_line(i), "branch con errato numero di argomenti");

      	     	  }
      	     	  else
      	     	    new SchemeSyntaxError(file_sorgente, get_line(i), "argomento "+i+"-esimo non branch per la COND");
      	     	}
      	     	  
      	     	Token t = null;
      	     	
      	        
      	     	//costruisco il nuovo token di tipo SchemeExpression con la COND
      	     	if (esiste_else)
      	     	  t = new Token(get_line(primo_arg), Costanti.CONDELSE, f.createCondExpression( lista_branch, lista_token.get(ultimo_arg).get_SchemeExpression() ));
      	     	else
      	     	  //caso senza else, passo una SchemeExpression che contiene il flag -2 a indicare che è una cond senza else
      	     	  t = new Token(get_line(primo_arg), Costanti.COND,    f.createCondExpression( lista_branch, new SchemeExpression(-2, 0) ));
      	     	
      	     	rimuovi_porzione(start, stop);
       	   	    lista_token.add(start, t ) ;

       	     }
       	     
       	     //LOCAL
       	     else if (get_tipo_token(primo_arg) == Costanti.LOCAL)
       	     {
       	     	List<SchemeDefinition> lista_def_temp = new LinkedList<SchemeDefinition>();
       	        
       	        //necessari 2 argomenti dopo la local
       	        if (stop - start != 4)
       	          new SchemeSyntaxError(file_sorgente, get_line(primo_arg), "numero di argomenti non valido per la local");
       	        if (get_tipo(ultimo_arg) != Costanti.SCHEME_EXPRESS)
       	          new SchemeSyntaxError(file_sorgente, get_line(ultimo_arg), "espressione della local non valida o assente");
       	        if (get_tipo(secondo_arg) != Costanti.SCHEME_EXPRESS || get_tipo_token(secondo_arg) != Costanti.DEFINE_INTERNA)
       	          new SchemeSyntaxError(file_sorgente, get_line(secondo_arg), "elenco di define non valido per la local");

       	        
       	        //creo token chiamando la factory, estraggo lista def dal token delle define 
       	        Token t = new Token(get_line(primo_arg), Costanti.LOCAL, f.createLocalExpression(   lista_token.get(secondo_arg).get_SchemeExpression().get_lista_def() , 
       	        												  lista_token.get(ultimo_arg).get_SchemeExpression()  ) );
      	     	rimuovi_porzione(start, stop);
       	   	    lista_token.add(start, t ) ;
  
       	   	      
       	     }
       	     //LAMBDA
       	     else if (get_tipo_token(primo_arg) == Costanti.LAMBDA)
       	     {
       	     	//necessari 2 argomenti dopo la lambda
       	        if (stop - start != 4)
       	          new SchemeSyntaxError(file_sorgente, get_line(primo_arg), "numero di argomenti non valido per la lambda");
       	        //se no si tratta di una lista di expr oppure di una lista vuota di id => errore
       	        if ((get_tipo(secondo_arg) != Costanti.SCHEME_EXPRESS || get_tipo_token(secondo_arg) != Costanti.LISTA_EXPR) && get_tipo(secondo_arg) != Costanti.TOKEN_VUOTO )
       	          new SchemeSyntaxError(file_sorgente, get_line(secondo_arg), "lista di id non valida per la lambda");
       	        if (get_tipo(ultimo_arg) != Costanti.SCHEME_EXPRESS )
       	          new SchemeSyntaxError(file_sorgente, get_line(ultimo_arg), "espressione non valida per la lambda");
       	       
       	       
       	       //il secondo argomento è una lista di espressioni, controllo che siano tutte ID
       	       List<SchemeExpression> lista_exprs_temp = new LinkedList<SchemeExpression>();
       	       
       	       //se si tratta di una lista
       	       if (get_tipo(secondo_arg) != Costanti.TOKEN_VUOTO)
       	       {
       	         lista_exprs_temp = lista_token.get(secondo_arg).get_SchemeExpression().get_lista_exprs();
       	         for (int i=0; i<lista_exprs_temp.size(); i++)
       	           if (lista_exprs_temp.get(i).get_tipo() != Costanti.ID)
       	       	    new SchemeSyntaxError(file_sorgente, get_line(secondo_arg), "id in posizione "+(i+1)+" non valido nella lista di id della lambda"); 
       	       }
       	       
       	       //creo il Token con la lambda.
       	       Token t = new Token(get_line(primo_arg),
       	                           Costanti.LAMBDA,
       	                           f.createLambdaExpression( lista_exprs_temp,  lista_token.get(ultimo_arg).get_SchemeExpression())   );
      	     	rimuovi_porzione(start, stop);
       	   	    lista_token.add(start, t ) ;   
       	
       	          
       	     }
       	     //costruttore list. es: (list 1 2 3 4 ...)
       	     else if (get_tipo_token(primo_arg) == Costanti.LIST)
       	     {
       	        List<SchemeExpression> lista_exprs_temp = new LinkedList<SchemeExpression>();
       	        
       	        for(int i=secondo_arg; i<=ultimo_arg; i++)
      	     	   if (get_tipo(i) == Costanti.SCHEME_EXPRESS) //se trovo un oggetto SchemeExpression (escludo parantesi finale e iniziale)
      	     	    lista_exprs_temp.add(lista_token.get(i).get_SchemeExpression()); //aggiungo il token alla lista temporaneaù
      	     	   else
      	     	    new SchemeSyntaxError(file_sorgente, get_line(i), "la list contiene un argomento non espressione");
      	     	  	
       	       
       	       Token t = new Token(get_line(primo_arg), Costanti.LIST, f.createApplyExpression( lista_exprs_temp  ) );
      	     	rimuovi_porzione(start, stop);
       	   	    lista_token.add(start, t ) ;   

       	     }

       	   }
      	   //LISTA DI ESPRESSIONI
      	   else if (get_tipo(primo_arg) == Costanti.SCHEME_EXPRESS)
       	   {
      	      //controllo se è il caso di una lista di espressioni o di una lista di id
      	      List<SchemeExpression> lista_exprs_temp = new LinkedList<SchemeExpression>();
       	      
       	      //lista di express ?
       	      //controllo che tutti gli argomenti siano espressioni
       	      for (int i=primo_arg; i<stop ; i++)
       	        if (get_tipo(i) == Costanti.SCHEME_EXPRESS) 
          	   	    lista_exprs_temp.add(lista_token.get(i).get_SchemeExpression());
       	   	    else
         	   	    new SchemeSyntaxError(file_sorgente, get_line(i), "espressione non valida nella lista");  
       	     
       	      Token t = new Token(get_line(primo_arg), Costanti.LISTA_EXPR, new SchemeExpression(Costanti.LISTA_EXPR, lista_exprs_temp  )  );
       	   	  rimuovi_porzione(start, stop);
       	   	  lista_token.add(start, t ); 
               
		   }
           //lista di define (dentro la local)
       	   else if (get_tipo(primo_arg) == Costanti.SCHEME_DEFINITION)
       	   {
       	      List<SchemeDefinition> lista_def_temp = new LinkedList<SchemeDefinition>();
       	      
       	      for(int i=primo_arg; i<stop; i++)
      	     	 if (get_tipo(i) == Costanti.SCHEME_DEFINITION) //se trovo un oggetto SchemeDefinition
      	     	   lista_def_temp.add(lista_token.get(i).get_SchemeDefinition());
      	      	 else
      	      	   new SchemeSyntaxError(file_sorgente, get_line(i), "definizione dentro la local non valida");
      	      	     
      	      /* rimpiazzo con token espressione con lista di define */	     
       	      Token t = new Token(get_line(primo_arg), Costanti.DEFINE_INTERNA, new SchemeExpression(lista_def_temp)   );
       	   	  rimuovi_porzione(start, stop);
       	   	  lista_token.add(start, t );	

       	   }
       	   else 
             new SchemeSyntaxError(file_sorgente, get_line(start), "errore di sintassi fra le parentesi");
       	   
       	   
       	   //lista_token.add(  start, new Token(1, f.createIdExpression("BLOCCO PARENTESI DELETATO"))  );
       	   //stampa_lista("DOPO SOST");
       	   
       }
       	/*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
	   /*******************************************************************/
   
       
       public int get_tipo(int indice)
       {
       	return lista_token.get(indice).get_tipo();
       }
       
       public int get_tipo_token(int indice)
       {
       	return lista_token.get(indice).get_tipo_token();
       }
       
       public int get_line(int indice)
       {
       	return lista_token.get(indice).get_line();
       }
       
       //esistono sottoespressioni fra parentesi da risolvere ?
       public boolean esistono_sottoexpr()
       {
       	//ritorno vero se esiste almeno una parentesi aperta
       	boolean ret=false;
       	for (int i=0; i<lista_token.size()-1 ; i++)
       	  if ( lista_token.get(i).get_tipo() == Costanti.TOKEN_SCANNER &&  get_tipo_token(i) == Costanti.LPAREN   )
       	    { ret =true; continue; }
       	return ret;
       }
       
       
       public void debug(String s)
       {
       	System.out.print(s);
       }
       public void debug(int s)
       {
       	System.out.print(s);
       }
       
       
       private void rimuovi_porzione(int start, int stop)
       {
       	int intervallo = stop - start + 1;
       	for(int i = 0; i < intervallo; i++)
       	  lista_token.remove(start);

       	//stampa_lista("DOPO RIMOZIONE");
       }
       
       
       //chiamo la stampa() per ogni oggetto Token della lista
       public void stampa_lista(String premsg)
       {
       	  System.out.println("\n-- " + premsg + " ---");
       	  for (int i=0; i<lista_token.size(); i++)
          {
          	 System.out.print("[");
          	 if (i<10) System.out.print(" ");
          	 System.out.print(i);
          	 System.out.print("] ");
          	 
          	 
          	 lista_token.get(i).stampa();
          }	 
       }
       
       
       
}
