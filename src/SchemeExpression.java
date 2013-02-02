import java.io.*;
import java.util.*; // per list, vector


public class SchemeExpression  {
		
		private int tipo; // indica cosa contiene l'espressione ( stesso valore contenuto in Token.tipo_token quando è inglobata nella classe token)

		private int valore_int;        // eventuale valore int se l'espressione ne deve memorizzare uno
		private String valore_string;  // eventuale valore string
		boolean valore_bool;
		
		//List espressioni; 
		private List<SchemeExpression> lista_exprs = new LinkedList<SchemeExpression>(); //una expr può contenere altre exprs
		private List<SchemeBranch> lista_branch = new LinkedList<SchemeBranch>();		 //una expr può contenere branchs (caso COND)
		private List<SchemeDefinition> lista_def = new LinkedList<SchemeDefinition>();   //una expr può avere una lista di Def (caso LOCAL)
		private SchemeExpression expr;													 //una expr può avere un'altra expr (per ELSE, LAMBDA, LOCAL..)

		
		//ctor lanciati da SchemeFactory:
		
		//ctor per ID e STRING: passo il tipo
		public SchemeExpression(int tipo_, String valore_string_)
		{
		   tipo = tipo_;
		   
		   //se la factory vuole costruire una espressione ID o una string
		   if (tipo == Costanti.ID || tipo == Costanti.STRING)
		     valore_string = valore_string_;
		}
		
		//ctor per BOOL
		public SchemeExpression(boolean v)
		{
		   if (v)
		     tipo= Costanti.TRUE;
		   else
		     tipo = Costanti.FALSE;
		   
		   valore_bool = v;
		}
		

		//ctor per INT
		public SchemeExpression(int tipo_, int valore_int_)
		{
		   tipo = tipo_;
		   //se la factory vuole costruire una espressione int
		   if (tipo == Costanti.INT)
		     valore_int = valore_int_;
		}
		
		//ctor per OR, AND, LIST EXPR
		public SchemeExpression(int tipo_, List<SchemeExpression> exprs)
		{
		   tipo = tipo_;
		   //se la factory vuole costruire una espressione int
		   if (tipo == Costanti.AND || tipo == Costanti.OR || tipo == Costanti.LISTA_EXPR || tipo == Costanti.LIST)
		     lista_exprs = exprs;
		}
		
		//ctor per cond e condelse, chiamato dalla createCondExpression
		public SchemeExpression(List<SchemeBranch> branches, SchemeExpression e)
		{
		   lista_branch = branches;
		   
		   //se non esiste l'else, la createCondExpression crea l'espressione con tipo=-2
		   if (e.get_tipo()!=-2)
		   {
		   	tipo = Costanti.CONDELSE; //cond else
		   	expr = e;
		   }	
		   else
		    tipo = Costanti.COND; //cond
		    
		}
		
		//ctor per local
		public SchemeExpression(List<SchemeDefinition> bindings, SchemeExpression e, int tipo_)
		{
		   tipo = tipo_; //se non lo usa nessun altro metti =14
		   
		     //if (tipo == Costanti.LOCAL)
		     lista_def = bindings;
		   
		   expr = e; 
		    
		}
		
		//ctor per lambda, uso variabile
		public SchemeExpression(List<SchemeExpression> bindings, SchemeExpression e, int tipo_, int temp)
		{
		   tipo = tipo_; //se non lo usa nessun altro metti =14
		   
		     //if (tipo == Costanti.LAMBDA)
		     lista_exprs = bindings;
		   
		   expr = e; 
		    
		}

		
		//ctor per elenco define interna ad una local
		public SchemeExpression(List<SchemeDefinition> lista_def_)
		{
		   tipo = Costanti.DEFINE_INTERNA;
		   lista_def = lista_def_;
		}
		
		

		//ritorno lista delle definition		
		public List<SchemeDefinition> get_lista_def()
		{
			return lista_def;
		}
		
		//ritorno lista delle espression
		public List<SchemeExpression> get_lista_exprs()
		{
			return lista_exprs;
		}
		
		//ritorno tipo dell'oggetto SchemeExpression (cosa contiene)
		public int get_tipo()
		{
			return tipo;
		}
		
		//ritorno il valore string della classe
		public String get_valore_string()
		{
			return valore_string;
		}
		
		//stampo l'espressione, qualsiasi cosa contenga
		public void stampa()
    	{
    	 	if (tipo == Costanti.ID)
    	 	 System.out.print("EXPR_ID(" + valore_string +") ");
    	 	 
    	 	else if (tipo == Costanti.STRING)
    	 	 System.out.print("EXPR_STR(" + valore_string +") "); 
    	 	
    	 	else if (tipo == Costanti.INT)
    	 	 System.out.print("EXPR_INT(" + valore_int + ") "); 
    	 	
    	 	else if (tipo == Costanti.AND || tipo == Costanti.OR) 
    	 	{
    	 		if (tipo == Costanti.AND) System.out.print("EXPR_AND( ");
    	 		else if (tipo == 11) System.out.print("EXPR_OR( ");
    	 		
    	 		//stampo espressioni contenute nella lista
    	 		for (int i=0; i<lista_exprs.size(); i++)
    	 		{
    	 			 lista_exprs.get(i).stampa(); 
    	 			 System.out.print(" ");
    	 		}	 
    	 		System.out.print(" )");
    	 	}
    	 	else if (tipo == Costanti.TRUE)  
    	 	{
    	 		System.out.print("BOOL(true)");
    	 	}
    	 	else if (tipo == Costanti.FALSE)  
    	 	{
    	 		System.out.print("BOOL(false)");
    	 	}
    	 	
    	 	else if (tipo == Costanti.COND ) 
    	 	{
    	 	  System.out.print("EXPR_COND( ");	
    	 	  for (int i=0; i<lista_branch.size(); i++)
    	 	  {
    	 		 lista_branch.get(i).stampa(); //ricorsivo
    	 		 System.out.print(" ");
    	 	  }
    	 	  System.out.print(" )");
    	 		
    	 		
    	 	}
    	 	else if (tipo == Costanti.CONDELSE) 
    	 	{
    	 	  System.out.print("EXPR_CONDELSE( ");	
    	 	  for (int i=0; i<lista_branch.size(); i++)
    	 	  {
    	 		 lista_branch.get(i).stampa();
    	 		 System.out.print(" ");
    	 	  }
    	 	  expr.stampa();
    	 	  System.out.print(" )");
    	 		
    	 		
    	 	}
    	 	else if (tipo == Costanti.LOCAL)
    	 	{
    	 	   System.out.print("EXPR_LOCAL( ");
    	 	   for (int i=0; i<lista_def.size(); i++)
    	 	   {
    	 		 lista_def.get(i).stampa(); //ricorsivo
    	 		 System.out.print(" ");
    	 	   }
    	 	   expr.stampa();
    	 	 
    	 	   System.out.print(" )"); 
    	 	} 
    	 	else if (tipo == Costanti.DEFINE_INTERNA)
    	 	{
    	 	   System.out.print("EXPR_LISTA_DEF( ");
    	 	   for (int i=0; i<lista_def.size(); i++)
    	 	   {
    	 		 lista_def.get(i).stampa(); //ricorsivo
    	 		 System.out.print(" ");
    	 	   }
    	 	   System.out.print(" )");
    	 	} 
    	 	else if (tipo == Costanti.LISTA_EXPR)
    	 	{
    	 	   System.out.print("EXPR_LISTA_EXPRS( ");
    	 	   for (int i=0; i<lista_exprs.size(); i++)
    	 	   {
    	 		 lista_exprs.get(i).stampa(); //ricorsivo
    	 		 System.out.print(" ");
    	 	   }
    	 	   System.out.print(" )");
    	 	}
    	 	else if (tipo == Costanti.LAMBDA)
    	 	{
    	 	   System.out.print("EXPR_LAMBDA( (");
    	 	   for (int i=0; i<lista_exprs.size(); i++)
    	 	   {
    	 		 lista_exprs.get(i).stampa(); //ricorsivo
    	 		 System.out.print(" ");
    	 	   }
    	 	   System.out.print(" ) ");
    	 	   expr.stampa();
    	 	   System.out.print(" )");
    	 	}
    	 	else if (tipo == Costanti.LIST)
    	 	{
    	 	   System.out.print("EXPR_LIST( ");
    	 	   for (int i=0; i<lista_exprs.size(); i++)
    	 	   {
    	 		 lista_exprs.get(i).stampa(); //ricorsivo
    	 		 System.out.print(" ");
    	 	   }
    	 	   System.out.print(" )");
    	 	} 
    	 	 
        }	

    
}
