public interface SchemeScanner {
    static public final int EOF = 0;
    static public final int BOOL = 1;
    static public final int INT = 2;
    static public final int STRING = 3;
    static public final int SYMBOL = 4;
    static public final int OPEN = 10;
    static public final int CLOSE = 11;
    static public final int ID = 12;
    
    
    public int getToken(); //unico metodo da rispettare, restituisce token corrente
    public Token nextToken();
    public String get_file_sorgente();
    public void set_token(int t);
  
}