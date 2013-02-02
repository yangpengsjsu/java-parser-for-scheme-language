import java.io.*;

class SchemeSyntaxError extends Error {
	public SchemeSyntaxError(String sourceName, int line, String msg) {
		
		line = line+1; //in modo che prima riga sta in posizione 1
		System.out.println("ERRORE: "+msg+" - "+sourceName+":"+line);
		System.exit(-1);
	}

}
