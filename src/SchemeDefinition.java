import java.io.*;

public class SchemeDefinition  {
	
	  //una definizione contiene il nome della variabile e l'espressione
		private String nomeid; 
		private SchemeExpression espressione;
		
		
		public SchemeDefinition(String nomeid_, SchemeExpression espressione_)
		{
			 nomeid = nomeid_;
			 espressione = espressione_;
		}	

     
    public void stampa()
    {
    	System.out.print("SCHEME_DEF( "+nomeid+" ");
    	espressione.stampa();
    	System.out.println(" )");
    }	

    
}