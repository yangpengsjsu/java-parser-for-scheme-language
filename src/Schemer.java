import java.io.*;

public class Schemer {

	public static void parse(String source, InputStream stream) throws java.io.IOException {
		//istanzio un oggetto scanner che apre il file di input e ci lavora
		final SchemeScanner scanner = new SchemeScannerImpl(source, stream);
		
		//istanzio oggetto parser che prende in input l'oggetto scanner  e un factory (albero) che istanzia
		final SchemeParser parser = new SchemeParserImpl(scanner, new SchemeFactoryImpl() );
		
		//per ogni token dello scanner, l'oggetto def (di tipo Definition) prende il risultato della parseDefine() del parser
		while (scanner.getToken() != SchemeScanner.EOF)
		{ 
		  
			final SchemeDefinition def = parser.parseDefine(); //definition
			def.stampa();
			System.out.println(" - - -");
		}
	}

    //main: aprire il file e chiamare la parse 
	public static void main(String[] args) throws java.io.IOException {
      InputStream is = new DataInputStream(System.in);
     
      if (args.length<1)
        new SchemeSyntaxError("",-1,"File in input non specificato");
       
      parse(args[0],is);
     
	}
}
