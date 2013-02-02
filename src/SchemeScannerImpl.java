import java.io.*;

class SchemeScannerImpl implements SchemeScanner {
   
   FileReader ff=null;
   String tokread;
   Token token_corrente ;
   
   Yylex scanner;
   String file_sorgente;
   
   int token;

   //ctor
   public SchemeScannerImpl(String n, InputStream s) throws java.io.IOException
   {
     try
     {file_sorgente = n;
     ff = new FileReader(new File(n));
     scanner = new Yylex(ff);
     //l=1;
     } catch (FileNotFoundException f)
        { new SchemeSyntaxError(file_sorgente, -1, "File di input inesistente o non accessibile"); }
     
   }
   
   //ritorna l'oggetto Token, usato dal parser
   public Token nextToken()
   {
   	 try
   	 { 
   	 	token_corrente = scanner.yylex().tok();
   	 }  catch  (IOException e) { new SchemeSyntaxError(file_sorgente, -1, "Errore generico di lettura file di input"); }
   	 
   	 return token_corrente;

   }	
   
   //ritorna nome del file sorgente, attributo della classe
   public String get_file_sorgente()
   {
   	  return file_sorgente;
   }
   
   //ritorna valore del token, secondo gli indici di scanner.lex
   public int getToken()
   {
     return token;  
   }
   
   public void set_token(int t)
   {
     token = t;
   }

}
